package com.rbxhubpro.rohumex.businesModule.push

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rbxhubpro.rohumex.adsmodule.AdConfig
import com.rbxhubpro.rohumex.adsmodule.BrowserUtil
import com.rbxhubpro.rohumex.businesModule.Biz
import com.rbxhubpro.rohumex.businesModule.backend.Backend
import com.rbxhubpro.rohumex.businesModule.backend.Events
import com.rbxhubpro.rohumex.util.log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

// ═══════════════════════════════════════════════════════════════════════════
// ПРАВКА 6.2 — локальные уведомления. Этап 1: БЕЗ сервера рассылки — на выходе
// из приложения WorkManager планирует показ по правилам из конфига (блок
// notifications), возврат отменяет запланированное. Всё нужное для решения
// («не вернулся 24ч») известно уже в момент выхода — серверу тут решать нечего,
// а реестр устройств и сервис рассылки (самое дорогое) откладываются на этап 2.
//
// Приложение правила ИСПОЛНЯЕТ, а не решает: тексты (hook) — арм бандита,
// назначенный сервером этой установке; delay/лимиты/тихие часы — политика
// карточки. Всё меняется без релиза APK.
//
// СОБЫТИЯ (block = id кампании, hook_id = id хука):
//   push_scheduled  — при планировании, уходит ДО ухода из приложения
//                     (знаменатель без survivorship);
//   push_receive    — уведомление реально показано (Worker);
//   push_open       — тап (разбирает MainActivity.handlePushOpen);
//   push_suppressed — подавлено лимитом/конфликтом окна/отзывом разрешения/
//                     неподдержанным условием — иначе «не дошло»,
//                     «не отправляли» и «не умеем» неразличимы.
//
// HOLDOUT ~10% (обязателен): hash(iid) % 10 == 0 → уведомление НЕ планируется,
// но push_scheduled уходит с пометкой slot="holdout" (точное имя поля контракт
// не фиксирует — уговор: slot). Без холдаута «получившие vs неполучившие»
// покажет эффект даже у бесполезного пуша — сравнивались бы живые с мёртвыми.
//
// ⚠️ ПОРЯДОК ПРОВЕРОК В ЦИКЛЕ КРИТИЧЕН ДЛЯ КОРРЕКТНОСТИ ХОЛДАУТА.
// Обе группы обязаны пройти ОДИНАКОВЫЙ отбор (condition → quiet → слот) и
// разойтись лишь на последнем шаге: контроль планирует, холдаут только шлёт
// знаменатель. Если проверить холдаут раньше дедупликации слотов, две кампании
// в одном часу дадут контролю ОДИН push_scheduled, а холдауту ДВА — когорты
// станут неравными, и uplift посчитается по кривому знаменателю. Ровно то,
// ради чего холдаут и заводился.
// ═══════════════════════════════════════════════════════════════════════════

object LocalPush {

    const val CHANNEL_ID = "rewards"

    // Все наши задачи помечены общим тегом (перепланирование = cancel+enqueue);
    // отдельный тег — у тех, кого по конфигу отменяет возврат (cancel_on).
    private const val TAG_ALL            = "localpush"
    private const val TAG_CANCEL_ON_OPEN = "localpush_cancel_app_open"

    private const val PREFS     = "local_push"
    private const val KEY_DAY   = "shown_date"   // yyyyMMdd последнего показа
    private const val KEY_COUNT = "shown_count"  // показов за этот день

    // extras уведомления → разбирает MainActivity.handlePushOpen
    const val EXTRA_CAMPAIGN = "push_campaign"
    const val EXTRA_HOOK     = "push_hook"
    const val EXTRA_ROUTE    = "push_route"
    const val EXTRA_GATE_PL  = "push_gate_pl"

    // ключи inputData Worker'а
    private const val IN_CAMPAIGN    = "campaign"
    private const val IN_HOOK_ID     = "hook_id"
    private const val IN_TITLE       = "title"
    private const val IN_BODY        = "body"
    private const val IN_ROUTE       = "route"
    private const val IN_GATE_PL     = "gate_pl"
    private const val IN_MAX_PER_DAY = "max_per_day"

    // ── Планирование (зовётся из MainActivity.onStop) ────────────────────────
    fun scheduleOnExit(context: Context) {
        val cfg = AdConfig.remoteConfig?.notifications ?: return
        val campaigns = cfg.campaigns.orEmpty().filter {
            it.trigger == "on_exit" && !it.id.isNullOrEmpty() && !it.hook?.id.isNullOrEmpty()
        }
        if (campaigns.isEmpty()) return

        val wm = WorkManager.getInstance(context.applicationContext)
        // Каждый выход перепланирует заново от свежего конфига и свежего «сейчас» —
        // старые планы этого же механизма снимаем целиком.
        wm.cancelAllWorkByTag(TAG_ALL)

        Backend.init(context)
        // Holdout считается от iid (стабилен на установку) — группа не мигрирует
        // между запусками. iid ещё не выдан (первый старт офлайн) → не холдаут.
        //
        // toLong() перед absoluteValue: abs(Int.MIN_VALUE) остаётся отрицательным
        // (переполнение), и такая установка никогда бы не попала в холдаут.
        val iid = Backend.iid
        val holdout = iid != null && (iid.hashCode().toLong().absoluteValue % 10L) == 0L

        val maxPerDay = cfg.maxPerDay ?: Int.MAX_VALUE
        val now = System.currentTimeMillis()

        // priority: меньше = важнее. В один часовой слот — одно уведомление,
        // проигравшим шлём push_suppressed (факт подавления должен быть виден).
        val takenSlots = mutableSetOf<Long>()
        for (c in campaigns.sortedBy { it.priority ?: Int.MAX_VALUE }) {
            val cid  = c.id!!
            val hook = c.hook!!

            // condition в T1 поддержан минимально: "true" (или отсутствие) =
            // всегда. Прочие строки ("streak_alive" и т.п.) этот шаблон вычислить
            // не умеет — механик с локальным состоянием здесь нет.
            //
            // ⚠️ Молча пропускать нельзя: в статистике это выглядело бы как
            // «кампания настроена, показов ноль» без причины. Шлём явную метку —
            // сервер сразу видит, что условие приложению не по зубам, а не что
            // оно оказалось false.
            val cond = c.condition?.trim()
            if (!(cond.isNullOrEmpty() || cond == "true")) {
                Events.track("push_suppressed", block = cid, hookId = hook.id,
                             slot = "condition_unsupported")
                log("localpush: condition '$cond' not supported in T1 → skip $cid")
                continue
            }

            val delayH = c.delayH ?: continue
            val fireAt = shiftOutOfQuiet(now + (delayH * 3_600_000.0).toLong(), cfg.quietHours)

            // Дедупликация слота — ДО ветвления на холдаут: обе группы должны
            // пройти одинаковый отбор, иначе знаменатели когорт разойдутся
            // (см. предупреждение в шапке файла).
            val slot = fireAt / 3_600_000L // часовой слот показа
            if (!takenSlots.add(slot)) {
                Events.track("push_suppressed", block = cid, hookId = hook.id, slot = "slot_conflict")
                continue
            }

            if (holdout) {
                // не планируем, но знаменатель уезжает — с пометкой
                Events.track("push_scheduled", block = cid, hookId = hook.id, slot = "holdout")
                continue
            }

            val data = Data.Builder()
                .putString(IN_CAMPAIGN, cid)
                .putString(IN_HOOK_ID,  hook.id)
                .putString(IN_TITLE,    hook.title ?: "")
                .putString(IN_BODY,     hook.body ?: "")
                .putString(IN_ROUTE,    hook.route)
                .putString(IN_GATE_PL,  hook.gatePl)
                .putInt(IN_MAX_PER_DAY, maxPerDay)
                .build()

            val req = OneTimeWorkRequest.Builder(PushWorker::class.java)
                .setInitialDelay(fireAt - now, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(TAG_ALL)
                .apply { if (c.cancelOn == "app_open") addTag(TAG_CANCEL_ON_OPEN) }
                .build()
            wm.enqueue(req)

            // знаменатель: уходит в последнем батче ПЕРЕД уходом — survivorship нет
            Events.track("push_scheduled", block = cid, hookId = hook.id)
            log("localpush: scheduled $cid in ${fireAt - now}ms")
        }
    }

    // ── Тап по уведомлению (правка 6.2б; onCreate + onNewIntent) ─────────────
    // Уведомление, которое просто открывает приложение, не монетизируется —
    // хук несёт route (экран внутри, Biz.onPushRoute) или gate_pl (лендинг
    // через гейтвей). Пересоздание activity донесёт тот же intent — гасим
    // extras, чтобы не задвоить push_open.
    fun handleOpen(activity: Activity, intent: Intent?) {
        val cid = intent?.getStringExtra(EXTRA_CAMPAIGN) ?: return
        intent.removeExtra(EXTRA_CAMPAIGN)

        Backend.init(activity)
        Events.track("push_open", block = cid, hookId = intent.getStringExtra(EXTRA_HOOK))

        intent.getStringExtra(EXTRA_ROUTE)?.let { Biz.onPushRoute(it) }

        // gate_pl: одразу в монетизацію. fallback="" — рекламний конфіг міг ще
        // не приїхати: без atk і без fallback openAd просто нічого не відкриє.
        intent.getStringExtra(EXTRA_GATE_PL)?.let { pl ->
            BrowserUtil.openAd(activity, "", pl)
        }
    }

    // ── Отмена при возврате (cancel_on=app_open; MainActivity.onStart) ──────
    fun cancelOnAppOpen(context: Context) {
        WorkManager.getInstance(context.applicationContext)
            .cancelAllWorkByTag(TAG_CANCEL_ON_OPEN)
    }

    // ── Тихие часы ───────────────────────────────────────────────────────────
    // quiet_hours = [start, end] в ЛОКАЛЬНОМ времени устройства (не UTC — люди
    // в разных зонах). Попали в окно → сдвигаем ВПЕРЁД к его концу (end:00).
    // Окно через полночь ([22,9]): до полуночи — конец окна уже завтра.
    private fun shiftOutOfQuiet(atMs: Long, quiet: List<Int>?): Long {
        if (quiet == null || quiet.size != 2) return atMs
        val start = quiet[0]
        val end   = quiet[1]
        if (start == end) return atMs

        val cal = Calendar.getInstance().apply { timeInMillis = atMs }
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val inQuiet = if (start < end) h in start until end else h >= start || h < end
        if (!inQuiet) return atMs

        if (start in (end + 1)..h) cal.add(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, end)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.timeInMillis
    }

    // ── Показ (Worker: процесс может быть поднят WorkManager'ом с нуля) ──────
    class PushWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

        override fun doWork(): Result {
            val result = showNotification()
            // ДОБАВЛЕНО: досылаем события СИНХРОННО перед выходом. Процесс мог
            // быть поднят WorkManager'ом только ради этого показа и будет убит
            // сразу после doWork() — асинхронный flush() успел бы лишь создать
            // поток, и push_receive/push_suppressed потерялись бы ровно в том
            // сценарии, ради которого их завели. doWork() и так фоновый поток.
            Events.flushBlocking()
            return result
        }

        @SuppressLint("MissingPermission")
        private fun showNotification(): Result {
            val app    = applicationContext
            val cid    = inputData.getString(IN_CAMPAIGN) ?: return Result.success()
            val hookId = inputData.getString(IN_HOOK_ID)
            Backend.init(app) // события должны уйти с atk и из «холодного» процесса

            // Разрешение могли отозвать в настройках после планирования
            if (!NotificationManagerCompat.from(app).areNotificationsEnabled()) {
                Events.track("push_suppressed", block = cid, hookId = hookId, slot = "no_permission")
                return Result.success()
            }

            // Дневной бюджет: счётчик показов в prefs по дате (max_per_day —
            // страховка политики: сколько бы кампаний ни насчитал конфиг,
            // больше N уведомлений в день человек не увидит)
            val maxPerDay = inputData.getInt(IN_MAX_PER_DAY, Int.MAX_VALUE)
            val prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
            val shownToday = if (prefs.getString(KEY_DAY, "") == today) prefs.getInt(KEY_COUNT, 0) else 0
            if (shownToday >= maxPerDay) {
                Events.track("push_suppressed", block = cid, hookId = hookId, slot = "max_per_day")
                return Result.success()
            }

            ensureChannel(app)

            // Тап → MainActivity с extras; route/gate_pl разбирает handlePushOpen.
            // ⚠️ MainActivity (не StartActivity): при мёртвом процессе она стартует
            // сама — exported=true, singleTask, initialize() внутри onCreate.
            // Если в конкретном приложении StartActivity делает что-то ещё, кроме
            // проброса intent.data, — вести пуш надо через неё.
            val open = Intent(app, Biz.config.mainActivityClass).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(EXTRA_CAMPAIGN, cid)
                putExtra(EXTRA_HOOK, hookId)
                inputData.getString(IN_ROUTE)?.let   { putExtra(EXTRA_ROUTE, it) }
                inputData.getString(IN_GATE_PL)?.let { putExtra(EXTRA_GATE_PL, it) }
            }
            val pi = PendingIntent.getActivity(
                app, cid.hashCode(), open,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notif = NotificationCompat.Builder(app, CHANNEL_ID)
                .setSmallIcon(Biz.config.notificationIconRes)
                .setContentTitle(inputData.getString(IN_TITLE) ?: "")
                .setContentText(inputData.getString(IN_BODY) ?: "")
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            runCatching {
                NotificationManagerCompat.from(app).notify(cid.hashCode(), notif)
            }.onFailure {
                // гонка отзыва разрешения между проверкой и notify
                Events.track("push_suppressed", block = cid, hookId = hookId, slot = "no_permission")
                return Result.success()
            }

            prefs.edit {
                putString(KEY_DAY, today)
                putInt(KEY_COUNT, shownToday + 1)
            }
            // потери от OEM-киллеров видны как receive < scheduled
            Events.track("push_receive", block = cid, hookId = hookId)
            return Result.success()
        }

        private fun ensureChannel(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, "Rewards", NotificationManager.IMPORTANCE_DEFAULT)
                )
            }
        }
    }
}
