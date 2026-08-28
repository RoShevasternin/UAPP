package com.rbxhubpro.rohumex.businesModule

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import com.google.firebase.messaging.FirebaseMessaging
import com.rbxhubpro.rohumex.adsmodule.RemoteConfigModel
import com.rbxhubpro.rohumex.businesModule.backend.Backend
import com.rbxhubpro.rohumex.businesModule.backend.Events
import com.rbxhubpro.rohumex.adsmodule.AdConfig
import com.rbxhubpro.rohumex.businesModule.push.LocalPush
import com.rbxhubpro.rohumex.businesModule.push.PushOptIn
import com.rbxhubpro.rohumex.businesModule.web.WebReward
import com.rbxhubpro.rohumex.util.log
import java.util.concurrent.atomic.AtomicBoolean

// ═══════════════════════════════════════════════════════════════════════════
// Biz — ЄДИНА точка входу в businesModule для MainActivity.
//
// Призначення модуля: правки 1–7 з APK_INTEGRATION.md одним переносним
// пакетом. Копіювання в іншу апку = скопіювати теку businesModule + один
// find&replace префікса пакета + Biz.install() в App.onCreate.
//
// Модуль залежить тільки від adsmodule (теж переносний, їдуть парою) і
// util/log. Три app-специфічні речі приходять через Config:
//   mainActivityClass   — куди веде тап по пушу (PendingIntent)
//   notificationIconRes — іконка нотифікації
//   appVersion          — BuildConfig.VERSION_NAME для push_token
//
// Що модуль НЕ забирає (свідомо лишається в апці):
//   · UserDetector-флоу і Firebase RC фолбек — політика конкретної апки
//     (нові апки фолбека не мають узагалі, див. T1_DEV_ANSWERS §6);
//   · TikTok/сервіси — app-специфіка;
//   · UI діалогів (нагорода з веба) — «UI свій у кожній апці», модуль
//     віддає подію через onWebReward.
// ═══════════════════════════════════════════════════════════════════════════

/**BusinessModuleFacade*/
object Biz {

    class Config(
        val mainActivityClass  : Class<out Activity>,

        @DrawableRes
        val notificationIconRes: Int,
        val appVersion         : String,

        // Тексти опт-іну (правка 6.1) — редагуються під апку без правки модуля
        val optInTitle    : String                = "Turn on notifications",
        val optInMessage  : (reward: Int)->String = { "Get +$it coins right now — and we'll remind you when your free bonus is ready." },
        val optInPositive : (reward: Int)->String = { "Get $it" },
        val optInNegative : String                = "Later",
        val optInGranted  : (reward: Int)->String = { "+$it coins added. We'll ping you when the next bonus is ready." },
    )

    lateinit var config: Config
        private set

    lateinit var appContext: Context
        private set

    // ── Колбеки в апку ────────────────────────────────────────────────────────

    /** Валідний повернений з веба токен → сума нарахована в Wallet, апка
     *  показує свій діалог «+N coins». */
    var onWebReward: (coins: Int) -> Unit = {}

    /** route з хука пуша (правка 6.2б). Дефолт — no-op з логом: у шаблонах без
     *  наскрізної навігації підключати нема куди. */
    var onPushRoute: (route: String) -> Unit = { log("push route=$it (no-op)") }

    // ── Install (App.onCreate!) ───────────────────────────────────────────────
    // САМЕ Application, не Activity: PushWorker може підняти процес без жодної
    // активіті, і Biz.config мусить уже існувати.
    fun install(app: Application, config: Config) {
        this.appContext = app.applicationContext
        this.config     = config
        Backend.init(app)
    }

    // ── Сесія (викликається з initAds; повторні виклики через Retry — no-op) ──
    private val sessionStarted = AtomicBoolean(false)

    /** app_open + синхронізація FCM-токена. Раз на процес. */
    fun startSession(context: Context) {
        if (!sessionStarted.compareAndSet(false, true)) return
        Backend.init(context)
        Events.appOpen()   // холодний старт; retention/LTV рахуються від нього
        syncPushToken()    // правка 6.3: onNewToken у старих установок не стріляє
    }

    // Кожен холодний старт: токен протухає мовчки, перезапис на сервері дешевий
    private fun syncPushToken() {
        runCatching {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                Events.pushToken(token, config.appVersion)
            }
        }.onFailure { log("FCM token sync failed: $it") }
    }

    // ── Конфіг з нашого сервера (правка 1) ────────────────────────────────────
    // Тонка обгортка: рішення про фолбек (Firebase RC чи нічого) — за апкою.
    fun fetchConfig(context: Context, rawReferrer: String?, onResult: (RemoteConfigModel?) -> Unit) =
        Backend.fetchConfig(context, rawReferrer, onResult)

    // ── Lifecycle-хуки (усі чотири — по рядку в MainActivity) ─────────────────

    /** onCreate + onNewIntent: диплінк повернення з веба (7) і тап по пушу (6.2б). */
    fun onActivityIntent(activity: Activity, intent: Intent?) {
        // Возврат из веба с монетами — момент награды: гасим гейт на минуту,
        // чтобы рекламный таб не перебил диалог «+N coins».
        if (intent?.data?.host == "reward") {
            AdConfig.suppressAppOpenUntilMs = System.currentTimeMillis() + 60_000
        }
        WebReward.handle(activity, intent)
        LocalPush.handleOpen(activity, intent)

        // Пришли с лендинга за разрешением: системный запрос → возврат в таб.
        // Токен с наградой отдаём в URL, монеты начислит лендинг — там их обещали.
        if (intent?.data?.host == "optin") {
            Events.track("web_return", slot = "optin")
            onWebOptIn?.invoke(activity)
        }
    }

    /** onStart: повернувся — знімаємо заплановане з cancel_on=app_open. */
    fun onStart(context: Context) {
        // Разрешение могли выдать или снять в настройках, пока нас не было —
        // лендинг узнаёт об этом из ссылки, поэтому состояние освежаем здесь.
        Backend.refreshPushGranted(context)
        LocalPush.cancelOnAppOpen(context)
    }

    /** onStop: пішов — плануємо локалки за правилами конфігу (6.2). */
    fun onStop(context: Context) = LocalPush.scheduleOnExit(context)

    // ── Опт-ін на пуші (правка 6.1) ──────────────────────────────────────────
    // requestPermission — ланцюжок до ActivityResult-launcher'а АКТИВІТІ:
    // контракт мусить бути зареєстрований до onStart, тому launcher живе в
    // MainActivity (5 рядків), а вся логіка — тут.
    /** Ставит MainActivity: там живёт launcher разрешения и открытие таба. */
    var onWebOptIn: ((Activity) -> Unit)? = null

    // ── ОЧЕРЕДЬ СТАРТА (26.08) ────────────────────────────────────────────────
    // Раньше таб и опт-ін решали каждый сам за себя и договаривались через
    // глобальный флаг — а любой такой флаг это гадание, кто успел раньше. Отсюда
    // и молча пропавший таб, и опт-ін, который не показывался ни разу.
    //
    // Теперь порядок владеет ОДНО место, и шаг стартует только по докладу
    // предыдущего:
    //     конфиг → опт-ін (наш диалог → системный → «+100») → таб → игра
    //
    // Опт-ін ПЕРЕД табом (решение владельца): разрешение одноразовое, показ
    // рекламы возобновляемый. Цена — таб уезжает на несколько секунд вправо.
    fun runStartupFlow(
        activity: Activity,
        requestPermission: (onResult: (Boolean) -> Unit) -> Unit,
        openGateAndContinue: () -> Unit,
    ) {
        // onNext вызывается РОВНО раз в любой ветке: не показали опт-ін, отложили,
        // отказали — всё равно идём дальше. Встать очередь не имеет права.
        //
        // Момент показа — ось из конфига (notifications.opt_in_trigger), меняется
        // из карточки без релиза:
        //   first_launch (деф.) — диалог перед табом, как сейчас;
        //   after_reward        — пропускаем, покажет onValueMoment() из игры.
        if (AdConfig.remoteConfig?.notifications?.optInTrigger == "after_reward") {
            openGateAndContinue(); return
        }
        PushOptIn.maybeShow(activity, requestPermission, onNext = openGateAndContinue)
    }

    /**
     * «Момент ценности» — первая выданная награда в игре. Звать ОДНОЙ строкой из
     * места, где человек только что что-то получил; сработает, только если в
     * конфиге стоит opt_in_trigger = after_reward, иначе тихо ничего не делает.
     */
    fun onValueMoment(
        activity: Activity,
        requestPermission: (onResult: (Boolean) -> Unit) -> Unit,
    ) {
        if (AdConfig.remoteConfig?.notifications?.optInTrigger != "after_reward") return
        // force: пауза «не спрашивать два запуска подряд» здесь не нужна — этот
        // показ и есть единственный в режиме after_reward, иначе он гасил бы сам
        // себя в том же запуске. Потолок MAX_ASKS при этом сохраняется.
        PushOptIn.maybeShow(activity, requestPermission, onNext = {}, force = true)
    }
}