# Пуші: правки 1 і 2 — готове рішення

> Джерело задачі: `DEV_TASKS.md` від 08.09.2026, пункти 1 і 2.
> Еталон, на якому зроблено і перевірено: `T1-integrated` (`com.rbxhubpro.rohumex`).
> Збірка проходить, `assembleDebug` зелений.

Змінено **три файли**, усі всередині `businesModule/`:

| файл | що робить правка |
|---|---|
| `push/PushService.kt` | з'явився `push_receive` для серверних пушів |
| `push/LocalPush.kt` | канал `rewards` створюється на старті, а не при першому показі |
| `Biz.kt` | канал на старті + полагоджена доставка `push_token` |

Нижче спершу опис проблем і що саме змінилось, потім **повні файли** — їх
можна просто замінити цілком.

---

## ⚠️ Перед тим як копіювати

Файли нижче з пакетом еталона **`com.rbxhubpro.rohumex`**. Після заміни
зробіть find & replace на свій пакет — інакше не скомпілюється:

```
com.rbxhubpro.rohumex  →  <ваш пакет>
```

Якщо ваша копія `businesModule` уже розходилась з еталоном — не замінюйте
файли цілком, візьміть фрагменти з розділів «Що змінилось».

---

## Правка 1 — факт показу серверного пуша

### Проблема

`PushService` обробляв тільки `onNewToken`. Показ серверного сповіщення малює
система, і до нас не доходив факт показу: є `push_sent` (Firebase прийняв) і
`push_open` (тап), немає `push_receive` (показалось). Через це не відрізнити
«не показалось» від «показалось, але не натиснули»: пішло 15 сповіщень,
повернень 0, пояснити нічим.

Друга частина: канал `rewards` створювався ліниво, при першому **локальному**
показі. До першої локалки серверне сповіщення йшло у fallback-канал Firebase —
не губиться, але налаштування «Rewards» на нього не діють.

### Що змінилось

**`push/PushService.kt`** — доданий `onMessageReceived`:

```kotlin
override fun onMessageReceived(msg: RemoteMessage) {
    Backend.init(applicationContext)

    val d = msg.data
    Events.track(
        "push_receive",
        block  = d[LocalPush.EXTRA_CAMPAIGN],
        hookId = d[LocalPush.EXTRA_HOOK],
    )
    Events.flushBlocking()
}
```

Плюс імпорт `com.google.firebase.messaging.RemoteMessage`.

Два моменти, які легко пропустити:

- **`Events.flushBlocking()` обов'язковий.** Процес міг бути піднятий FCM лише
  заради доставки і буде вбитий одразу після. Асинхронний `flush()` встиг би
  тільки створити потік, і `push_receive` губився б рівно в тому сценарії,
  заради якого його завели. `onMessageReceived` викликається у фоновому потоці,
  тож синхронна відправка звідти дозволена — та сама причина, що вже описана в
  `LocalPush.PushWorker.doWork`.
- **Ключі беруться з `LocalPush.EXTRA_CAMPAIGN` / `EXTRA_HOOK`.** Це ті самі
  рядки `"push_campaign"` / `"push_hook"`, але одним джерелом правди з
  локальними пушами — щоб при зміні не роз'їхалось.

**`push/LocalPush.kt`** — `ensureChannel` піднятий з приватного методу
`PushWorker` на рівень об'єкта `LocalPush` і став `internal`:

```kotlin
const val CHANNEL_ID = "rewards"

internal fun ensureChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (nm.getNotificationChannel(CHANNEL_ID) == null) {
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Rewards", NotificationManager.IMPORTANCE_DEFAULT)
        )
    }
}
```

Виклик усередині `showNotification()` став кваліфікованим:

```kotlin
-            ensureChannel(app)
+            LocalPush.ensureChannel(app)
```

`PushWorker` — вкладений клас, а не `inner`, тож неявного ресівера до об'єкта
в нього немає: константи резолвляться, а функцію треба звати через ім'я
об'єкта. Нові імпорти не потрібні, всі чотири (`NotificationChannel`,
`NotificationManager`, `Build`, `Context`) у файлі вже були.

**`Biz.kt`** — канал створюється на старті:

```kotlin
fun install(app: Application, config: Config) {
    this.appContext = app.applicationContext
    this.config     = config
    Backend.init(app)
    LocalPush.ensureChannel(app)   // ← канал до першого показу, а не після
}
```

Саме в `install`, бо це єдине місце, яке гарантовано відпрацьовує до всього
іншого: `PushWorker` може підняти процес без жодної активіті.

---

## Правка 2 — втрата `push_token`

### Проблема

За даними серверної команди, серед установок, що дозволили сповіщення, токен
дійшов лише від 40% у `rohumex` і 9% у `rbxkingdom`, тоді як в `atlantis`
ланцюжок зібраний повністю — 100%.

**Виклику не бракувало.** `push_token` шлеться з двох місць і обидва на місці:
`PushService.onNewToken` і холодний старт `Biz.startSession()` →
`syncPushToken()`. Ламалась **доставка**:

- `Events.drainAndSend()` чіпляє `session.data = atk` **тільки якщо `atk` уже
  є**; подія без нього йде як анонімна веб-подія і в app-звіти не потрапляє;
- `Biz.startSession()` (звідки летить `syncPushToken`) викликається в
  `initAds` **до** `Biz.fetchConfig`;
- а `atk` зберігається аж усередині `Backend.fetchConfig`.

На свіжій установці FCM віддає токен за мілісекунди, `/appconfig` — за сотні.
Перший `push_token` іде без підпису і зникає зі звітів. Це рівно та картина,
що в таблиці: «дозволили 40 — токен є у 16».

### Що змінилось

**`Biz.kt`** — токен пересилається, щойно `atk` видали:

```kotlin
fun fetchConfig(context: Context, rawReferrer: String?, onResult: (RemoteConfigModel?) -> Unit) {
    val atkBefore = Backend.atk
    Backend.fetchConfig(context, rawReferrer) { model ->
        // atk видали ЩОЙНО (перша установка) — отже перший push_token
        // зі startSession пішов без підпису і в app-звіти не потрапив.
        // На другому і наступних запусках atk уже піднятий з prefs,
        // повторювати нема чого.
        if (atkBefore == null && Backend.atk != null) syncPushToken()
        onResult(model)
    }
}
```

⚠️ Тіло стало блоком, тому `=` перед `Backend.fetchConfig` треба **прибрати** —
інакше не скомпілюється.

Умова саме `atkBefore == null && Backend.atk != null`, а не просто
`Backend.atk != null`: на другому і наступних запусках `Backend.init` уже
підняв `atk` з prefs, тож перший `push_token` і так пішов підписаним, і
повторний виклик був би чистим дублем щозапуску.

### Друга половина правки 2 — потребує уточнення

У задачі також сказано: «у 428 установок порожня версія в полі `block`».
`Events.pushToken(token, appVersion)` кладе версію саме в `block`, і **обидва**
виклики її передають. У поточному коді порожньою вона бути не може.

Питання до серверної команди: ці 428 — з усього парку чи з конкретних апок?
Якщо це установки зі старих білдів, зроблених до появи `appVersion`, то в коді
правити нічого — розсмокчеться з оновленням.

---

## Питання, яке треба закрити ДО релізу

`onMessageReceived` спрацьовує **не завжди**. Якщо сервер шле повідомлення з
блоком `notification`, то при згорнутій апці сповіщення малює система, а
`onMessageReceived` **не викликається взагалі** — і `push_receive` не прийде,
попри правку.

Щоб подія була, розсилка має йти **data-only**: увесь вміст у `data`, без
`notification`, а показ малює наш код. Судячи зі сніпета в задачі (читається
`msg.data`), саме так і планується — але це варто підтвердити явно. Інакше
правка 1 закриє лише випадок «апка на екрані», а це малий відсоток, і діра у
воронці залишиться.

---

## Як перевірити

Тільки на **release**-збірці: мініфікація є лише там, і ProGuard-проблеми на
debug не відтворюються взагалі.

| # | що робимо | що має бути |
|---|---|---|
| 1 | стерти дані апки, запустити з інтернетом | у лозі `MODEL OUR = … atk=yes` |
| 2 | перша установка | `push_token` приходить **з підписом** (раніше перший губився) |
| 3 | попросити тестову кампанію з малим `delay_h`, вийти з апки | у лозі `localpush: scheduled …`, далі сповіщення |
| 4 | сповіщення в шторці | силует, не білий квадрат; канал «Rewards» у налаштуваннях є **до** першої локалки |
| 5 | серверна розсилка (data-only) | приходить `push_receive`, не лише `push_sent`/`push_open` |

Окремо переконатись, що в `proguard-rules.pro` є правило зі **своїм** пакетом:

```proguard
-keep class <ваш пакет>.businesModule.push.LocalPush$PushWorker { *; }
```

Правило з чужим пакетом не матчиться, і release тихо перестає показувати
сповіщення після першого ж оновлення апки.

---

# Повні файли

Нижче — вміст файлів цілком, з еталона. Замінюйте як є, потім find & replace
пакета.

## `businesModule/push/PushService.kt`

```kotlin
package com.rbxhubpro.rohumex.businesModule.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rbxhubpro.rohumex.businesModule.Biz
import com.rbxhubpro.rohumex.businesModule.backend.Backend
import com.rbxhubpro.rohumex.businesModule.backend.Events

// ═══════════════════════════════════════════════════════════════════════════
// ПРАВКА 6.3 — сбор FCM-токена. Делается в ПЕРВОМ же релизе, хотя рассылок
// пока нет, и вот почему: сбор токена — единственная часть пуш-канала, которая
// живёт в APK. Реестр, сегменты и отправка — на сервере, включаются когда
// угодно без релиза. Забыть сейчас = когда рассылка понадобится, ждать ещё
// один релиз и месяц раскатки, и первые недели слать будет некому.
//
// ⚠️ onNewToken у УЖЕ установленного приложения не срабатывает никогда —
// он стреляет только при первичной регистрации и ротации. Поэтому второй,
// обязательный источник — запрос токена на каждом холодном старте
// (Biz.syncPushToken). Слать на каждый старт правильно: токен протухает
// молча, перезапись на сервере дешёвая (одна строка на установку).
// ═══════════════════════════════════════════════════════════════════════════

class PushService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Backend мог не инициализироваться (сервис живёт своим процессом
        // жизни) — init идемпотентен и дёшев.
        Backend.init(applicationContext)
        Events.pushToken(token, Biz.config.appVersion)
    }

    // ── ЭТАП 2: факт ПОКАЗА серверного уведомления ───────────────────────────
    // Без этого события есть push_sent (Firebase принял) и push_open (тап), но
    // нет середины: «не показалось» и «показалось, но не нажали» неразличимы.
    //
    // ⚠️ flushBlocking обязателен. Процесс мог быть поднят FCM только ради
    // доставки и будет убит сразу после — асинхронный flush() успел бы лишь
    // создать поток, и push_receive потерялся бы ровно в том сценарии, ради
    // которого его завели. onMessageReceived вызывается в фоновом потоке,
    // так что синхронная отправка отсюда разрешена (та же причина, что в
    // LocalPush.PushWorker.doWork).
    override fun onMessageReceived(msg: RemoteMessage) {
        Backend.init(applicationContext)

        val d = msg.data
        Events.track(
            "push_receive",
            block  = d[LocalPush.EXTRA_CAMPAIGN],
            hookId = d[LocalPush.EXTRA_HOOK],
        )
        Events.flushBlocking()
    }
}
```

## `businesModule/Biz.kt`

```kotlin
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
        LocalPush.ensureChannel(app)   // ← канал до первого показа, не после
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
    fun fetchConfig(context: Context, rawReferrer: String?, onResult: (RemoteConfigModel?) -> Unit) {
        val atkBefore = Backend.atk
        Backend.fetchConfig(context, rawReferrer) { model ->
            // atk выдали ЩОЙНО (первая установка) — значит первый push_token
            // из startSession ушёл без подписи и в app-отчёты не попал.
            // На втором и следующих запусках atk уже поднят из prefs, и
            // повторять нечего.
            if (atkBefore == null && Backend.atk != null) syncPushToken()
            onResult(model)
        }
    }

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

}
```

## `businesModule/push/LocalPush.kt`

```kotlin
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

    // Канал создаётся на СТАРТЕ приложения (Biz.install), а не при первом
    // локальном показе: до первой локалки серверное уведомление уходило бы в
    // fallback-канал Firebase — не теряется, но настройка «Rewards» на него
    // не действует. Идемпотентен, дёшев, звать можно сколько угодно.
    internal fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Rewards", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }

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

            LocalPush.ensureChannel(app)

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
    }
}
```
