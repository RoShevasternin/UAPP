package com.rbxhubpro.rohumex.businesModule.push

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.rbxhubpro.rohumex.adsmodule.AdConfig
import com.rbxhubpro.rohumex.businesModule.Biz
import com.rbxhubpro.rohumex.businesModule.backend.Bt
import com.rbxhubpro.rohumex.businesModule.backend.Events
import com.rbxhubpro.rohumex.businesModule.economy.Econ
import com.rbxhubpro.rohumex.businesModule.economy.Wallet

// ═══════════════════════════════════════════════════════════════════════════
// ПРАВКА 6.1 — опт-ін на пуші з нагородою. Переписано 26.08 під ОЧЕРЕДЬ СТАРТА.
//
// ГЛАВНОЕ ПРАВИЛО: наш диалог — это НЕ системный запрос. Системный вызываем
// только по «Хочу +100». Android показывает его один раз за установку и после
// отказа не покажет уже никогда — поэтому тратим эту единственную попытку
// только на тех, кто уже согласился у нас. «Позже» не стоит ничего: спросим
// в одном из следующих запусков.
//
// ПОЧЕМУ ПЕРЕД ТАБОМ: разрешение одноразовое, показ рекламы возобновляемый.
// Цена — стартовый таб уезжает на несколько секунд вправо (решение владельца).
//
// onNext — доклад очереди: вызывается РОВНО раз в любой ветке (показали,
// отложили, отказали, уже разрешено, нет конфига). Очередь не имеет права
// встать: за нами стоит таб, а это деньги.
// ═══════════════════════════════════════════════════════════════════════════

internal object PushOptIn {

    private const val PREFS      = "push_optin"
    private const val KEY_SHOWN  = "shown_count"   // сколько раз ПОКАЗАЛИ наш диалог
    private const val KEY_LAST   = "last_launch"   // на каком запуске показывали
    private const val KEY_PAID   = "reward_paid"   // награду выдаём один раз
    private const val KEY_LAUNCH = "launch_no"     // счётчик запусков

    private const val MAX_ASKS      = 3   // больше — назойливость, а не конверсия
    private const val LAUNCH_GAP    = 2   // не подряд: спрашиваем через запуск

    fun maybeShow(
        activity: Activity,
        requestPermission: (onResult: (Boolean) -> Unit) -> Unit,
        onNext: () -> Unit,
        force: Boolean = false,
    ) {
        activity.runOnUiThread {
            val done = OneShot(onNext)
            val prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

            val launch = prefs.getInt(KEY_LAUNCH, 0) + 1
            prefs.edit { putInt(KEY_LAUNCH, launch) }

            // Тексты и размер награды живут в конфиге — без него не спрашиваем.
            if (AdConfig.remoteConfig == null) { done.run(); return@runOnUiThread }

            val reward = Econ.pushOptInReward // economy.push_optin_reward, деф. 100

            // Уже разрешено (Android ≤12 или человек включил сам) — награда, если
            // ещё не выдавали, и дальше без диалога.
            if (Build.VERSION.SDK_INT < 33 ||
                activity.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                grant(prefs, reward, activity, silent = true)
                done.run(); return@runOnUiThread
            }

            val shown = prefs.getInt(KEY_SHOWN, 0)
            val last  = prefs.getInt(KEY_LAST, -99)
            if (shown >= MAX_ASKS || (!force && launch - last < LAUNCH_GAP)) { done.run(); return@runOnUiThread }

            val cfg = Biz.config
            // Гасим app_open-гейт на весь сценарий: свой диалог → системный запрос
            // → результат. Это несколько переходов, одного флага мало.
            AdConfig.suppressAppOpenUntilMs = System.currentTimeMillis() + 120_000

            AlertDialog.Builder(activity, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle(cfg.optInTitle)
                .setMessage(cfg.optInMessage(reward))
                .setCancelable(false)
                .setPositiveButton(cfg.optInPositive(reward)) { _, _ ->
                    requestPermission { granted ->
                        AdConfig.suppressAppOpenUntilMs = 0L
                        if (granted) grant(prefs, reward, activity, silent = false)
                        else Events.track("push_optin", block = "denied")
                        done.run()
                    }
                }
                .setNegativeButton(cfg.optInNegative) { _, _ ->
                    AdConfig.suppressAppOpenUntilMs = 0L
                    // «Позже» ≠ отказ: системный запрос НЕ потрачен, спросим ещё.
                    Events.track("push_optin", block = "later")
                    done.run()
                }
                .create()
                .apply {
                    // Попытка засчитана в момент, когда диалог реально на экране,
                    // а не когда мы собрались его показать: перекрытый диалог
                    // раньше сжигал единственный шанс молча.
                    setOnShowListener {
                        prefs.edit { putInt(KEY_SHOWN, shown + 1); putInt(KEY_LAST, launch) }
                    }
                }
                .show()
        }
    }

    // ── Приход с лендинга (rohumexapp://optin) ───────────────────────────────
    // Человек уже нажал «включить» в вебе — своего диалога больше не показываем,
    // сразу системный запрос. Награду он ждёт НА ЛЕНДИНГЕ, поэтому платим не в
    // кошелёк приложения, а подписанным токеном обратно в таб: обещание и выдача
    // должны совпасть по месту, иначе человек спросит «где мои монеты».
    fun requestFromWeb(
        activity: Activity,
        requestPermission: (onResult: (Boolean) -> Unit) -> Unit,
        reopenTab: (grantedToken: String?) -> Unit,
    ) {
        activity.runOnUiThread {
            val prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val reward = Econ.pushOptInReward
            val already = Build.VERSION.SDK_INT < 33 ||
                    activity.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED

            fun payAndReturn(granted: Boolean) {
                AdConfig.suppressAppOpenUntilMs = 0L
                if (!granted) { Events.track("push_optin", block = "denied"); reopenTab(null); return }

                Events.track("push_optin", block = "granted")
                com.rbxhubpro.rohumex.businesModule.backend.Backend.pushGranted = true
                // Награда одна на установку — второй раз токен не выпускаем,
                // иначе очистка данных превращается в кран монет.
                if (prefs.getBoolean(KEY_PAID, false)) { reopenTab(null); return }
                prefs.edit { putBoolean(KEY_PAID, true) }
                reopenTab(com.rbxhubpro.rohumex.businesModule.backend.Backend.signReward(reward))
            }

            if (already) { payAndReturn(true); return@runOnUiThread }
            AdConfig.suppressAppOpenUntilMs = System.currentTimeMillis() + 120_000
            requestPermission { granted -> payAndReturn(granted) }
        }
    }

    // Награда выдаётся ОДИН раз за установку. silent — когда разрешение уже было
    // и диалога не показывали: молча начислить, поздравлять не с чем.
    private fun grant(
        prefs: android.content.SharedPreferences,
        reward: Int,
        activity: Activity,
        silent: Boolean,
    ) {
        Events.track("push_optin", block = "granted")
        com.rbxhubpro.rohumex.businesModule.backend.Backend.pushGranted = true
        if (prefs.getBoolean(KEY_PAID, false)) return
        prefs.edit { putBoolean(KEY_PAID, true) }
        Wallet.add(reward, bt = Bt.HUB, block = "push_optin")   // coins_earned шлёт Wallet
        if (!silent) {
            AlertDialog.Builder(activity, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setMessage(Biz.config.optInGranted(reward))
                .setPositiveButton(android.R.string.ok) { d, _ -> d.dismiss() }
                .show()
        }
    }

    // Доклад очереди строго один раз: диалог умеет закрыться несколькими путями
    // (кнопка, системный ответ, уход экрана), а таб за нами должен открыться ровно
    // один раз — и обязательно.
    private class OneShot(private val block: () -> Unit) {
        private var fired = false
        fun run() { if (!fired) { fired = true; block() } }
    }
}