# Оновлення гри до еталону — процедура

Чинний еталон: `june/Game T1-1/T1-integrated/` (пакет `com.rbxhubpro.rohumex`).
Первинне джерело правил — `INTEGRATION_GUIDE` від серверної команди (v9,
2026-08-31). Цей файл — те саме, але у вигляді порядку дій по нашому репо, з
поправками на те, що реально трапляється в наших проєктах.

Формула коротко: **скопіювати два модулі → замінити префікс пакета → чотири
точки в апці (App, MainActivity, StartActivity, LoaderScreen) → розмітити
екрани й монети**. Усередині `businesModule/` і `adsmodule/` нічого не
редагувати.

---

## 0. Перед стартом — три питання користувачу

Ці рішення міняють результат, самому їх не приймати:

1. **`app/google-services.json`** — у багатьох проєктах його немає, а плагін
   `com.google.gms.google-services` без нього ламає збірку, і FCM не видасть
   токен. Файл має бути від Firebase-проєкту **саме цього пакета**.
   Зазвичай користувач кладе його сам.
2. **Баланс існуючих гравців.** `Wallet` тримає баланс у власних
   SharedPreferences (`wallet`/`balance`), а в грі баланс майже завжди лежить
   у своєму DataStore. Якщо апка вже випущена — питати: перенести одноразово
   чи почати з нуля. (У T5 обрали «з нуля».)
3. **Схема диплінка** — виводиться однозначно, не узгоджується: **останній
   сегмент пакета як є**. `com.bossrbx.rbxcalculator` → `rbxcalculator`.
   Питати не треба, але **повідомити** — схему після релізу віддають серверній
   команді для картки (`deeplink_scheme`).

---

## 1. Копіювання модулів

```bash
SRC="…/june/Game T1-1/T1-integrated/app/src/main"
DST="…/<гра>/app/src/main"
PKG_DST=com.example.yourapp      # пакет цільової апки

rm -rf "$DST/java/<шлях>/businesModule"
cp -R "$SRC/java/com/rbxhubpro/rohumex/businesModule" "$DST/java/<шлях>/businesModule"

# adsmodule + аналітика: беремо версії еталона
for f in adsmodule/AdConfig.kt adsmodule/AdManager.kt adsmodule/AppOpenManager.kt \
         adsmodule/BrowserUtil.kt adsmodule/RemoteConfigModel.kt adsmodule/UserDetector.kt \
         services/analytics/AnalyticsManager.kt services/analytics/AnalyticsProvider.kt \
         services/analytics/FirebaseAnalyticsProvider.kt services/analytics/TikTokAnalyticsProvider.kt; do
  cp "$SRC/java/com/rbxhubpro/rohumex/$f" "$DST/java/<шлях>/$f"
done

cp "$SRC/res/drawable/ic_notification.xml" "$DST/res/drawable/"

# один find & replace по скопійованому
grep -rl "com\.rbxhubpro\.rohumex" "$DST" | xargs sed -i '' "s/com\.rbxhubpro\.rohumex/$PKG_DST/g"
```

**НЕ копіювати наосліп:**

| файл | чому |
|---|---|
| `adsmodule/AdPref.kt` | ім'я SharedPreferences своє в кожній апці (`rscount_ads_prefs`, `rbux_ads_prefs`…). Перезапис = втрата збереженого конфігу, `atk`, `iid` у живих юзерів |
| `util/util.kt` | свій лог-тег (`BOSSER`, `RBR`…) |
| `App.kt` / `MainActivity.kt` / `StartActivity.kt` | правити точково (крок 3), не замінювати |

Після заміни перевірити, що чужого префікса не лишилось — включно з
коментарями (в еталоні є `rohumexapp://optin`, схему в коментарі теж
поправити на свою).

---

## 2. Gradle · маніфест · ProGuard

**`app/build.gradle.kts`** — дві залежності:

```kotlin
implementation("com.google.firebase:firebase-messaging")   // firebase-bom 34.18.0+
implementation("androidx.work:work-runtime-ktx:2.11.2")
```

**`AndroidManifest.xml`** — три блоки:

1. `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />`
2. Диплінк-фільтр **на LAUNCHER-активіті** (у наших проєктах це `StartActivity`):
   `VIEW` + `DEFAULT` + `BROWSABLE`, `scheme` = останній сегмент пакета,
   два `host`: `reward` і `optin`.
3. `PushService` з `com.google.firebase.MESSAGING_EVENT`.

На `MainActivity` обов'язковий `launchMode="singleTask"` (у шаблоні вже є).

**`proguard-rules.pro`** — блок adsmodule уже є з вашим пакетом; додати:

```proguard
-keep class <ваш.пакет>.businesModule.push.LocalPush$PushWorker { *; }
```

⚠️ Обидва блоки — з **вашим** пакетом. Правило з чужим пакетом не матчиться, і
release тихо ламає Gson-парсинг конфігу та показ пушів.

---

## 3. Чотири точки в апці

### `App.onCreate` — першим ділом

```kotlin
Biz.install(this, Biz.Config(
    mainActivityClass   = MainActivity::class.java,
    notificationIconRes = R.drawable.ic_notification,
    appVersion          = BuildConfig.VERSION_NAME,
))
```

Саме `Application`: `PushWorker` може підняти процес без жодної активіті.

### `StartActivity` — прокинути інтент

```kotlin
startActivity(Intent(this, MainActivity::class.java).also {
    it.data = intent?.data
    intent?.extras?.let { e -> it.putExtras(e) }
})
```

Загубите `it.data` — кнопка виводу на лендінгу «нічого не робить», без помилок.

### `MainActivity` — п'ять точок

1. `pushPermissionLauncher` як property (`registerForActivityResult` після
   `onStart` кидає `IllegalStateException`) + публічний `requestPushPermission`.
2. В `onCreate`: `Biz.onWebReward`, `Biz.onWebOptIn`, `Biz.onActivityIntent(this, intent)`.
3. `onNewIntent` → `Biz.onActivityIntent`.
4. `onStart` → `Biz.onStart`, `onStop` → `Biz.onStop`.
5. В `initAds`: `Biz.startSession(this)`, `UserDetector.detectViaReferrer`
   тепер віддає **два** значення (`userType, rawReferrer`), далі
   `Biz.fetchConfig(this, rawReferrer)`.

Плюс два хелпери, які модуль очікує від апки: `showToast(text)` для GDX-шару і
свій діалог «+N coins» для `onWebReward`.

`rawReferrer` іде на сервер **голим** — не парсити, не чистити.

**Фолбек на Firebase RC.** Еталон його зберігає: наш конфіг не приїхав →
`fetchRemoteConfig`. Для вже випущених апок так і лишати (страховка розкатки).

### `LoaderScreen` — черга старту

Той рядок, яким ви зараз переходите в меню, **переїжджає всередину**
`openGateAndContinue`:

```kotlin
val act = gdxGame.activity
Biz.runStartupFlow(
    activity = act,
    requestPermission = { onResult -> act.requestPushPermission(onResult) },
    openGateAndContinue = {
        act.appOpenManager.showOnLoader(act) {
            runGDX { navigateToFirstScreen() }
        }
    },
)
```

⚠️ Перехід у меню має виконуватись **у всіх гілках**. Свої `return` туди не
додавати — апка зависне на сплеші.

### Свої `AlertDialog` — два рядки

```kotlin
AdConfig.suppressAppOpenUntilMs = System.currentTimeMillis() + 30_000
… .setOnDismissListener { AdConfig.suppressAppOpenUntilMs = 0L }.show()
```

Знімати саме в `setOnDismissListener` (спрацьовує і на свайп, і на «назад»).
Тема — **AppCompat**, не платформна: `androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert`.
Платформна разом з appcompat-діалогом малює заголовок двічі на MIUI/HyperOS.

---

## 4. Аналітика екранів

У `AdvancedScreen` (базовий клас усіх екранів шаблону):

```kotlin
open val analyticsBt   : Bt?     = null
open val analyticsBlock: String? = null
// у show(): if (bt != null && block != null) Events.screenView(bt, block)
```

Далі кожен **змістовний** екран оголошує пару. Технічні (Loader, вибір мови,
онбординг, налаштування) лишаються `null` і подій не шлють.

`Bt` — спільний словник на весь парк, **свій елемент не вигадувати**: механіка
не лягає — писати серверній команді. `block` — `snake_case`, після релізу
**не перейменовується**.

Типове мапування наших механік:

| екран | Bt |
|---|---|
| колесо | `SPIN` |
| скретч-картка | `GRID` |
| перевернути картку | `REVEAL` |
| квіз | `QUIZ` |
| щоденна нагорода | `DAILY` |
| конвертер / калькулятор | `TOOL` |
| головне меню | `HUB` |
| списки контенту | `CATALOG` |

На завершенні ігрового циклу — `Events.featureComplete(bt, block, amount)`.

---

## 5. Економіка

### Баланс — тільки `Wallet`

Прибрати з гри своє сховище балансу (у нас це поле в `PlayerData` +
`add/spend` у `PlayerModel`) і перевести всі рухи монет на
`Wallet.add/spend(bt, block)`. Панель балансу підписується на
`Wallet.balanceFlow` (в GDX — оновлення через `runGDX`/`Gdx.app.postRunnable`,
бо `collect` віддає значення в потоці підписника).

- `Events.coinsEarned/coinsSpent` поруч **не звати** — Wallet шле сам;
- списання **тільки** `spend()` (сам відмовляє при нестачі, у мінус не йде).

### Числа — тільки `Econ`

| що | ключ | приклад |
|---|---|---|
| скаляр (ціна/нагорода/штраф) | `<екран>.<предмет>` | `Econ.price("shop.hint", 25)` |
| набір однотипних значень | `<механіка>` | `Econ.rewardList("spin_wheel", DEFAULT_SUMS)` |

`rewardList` звіряє довжину зі списком з APK і при розбіжності бере дефолт —
інакше на секторі намальовано 150, а на баланс упало 25. Тому **число і для
підпису, і для нарахування береться з одного місця** (`payout()` в актора
колеса/картки).

**Дефолт = сьогоднішня поведінка апки.** Платить 10 → пишемо `10`. Нуль
дозволений тільки там, де сьогодні реально нуль (ціни ще немає) — тоді ключ з
дефолтом `0` стає ручкою, якою механіку вмикають без релізу. Ставити `0`
замість реального числа не можна: порожня відповідь сервера вимкне механіку.

Після інтеграції — надіслати серверній команді список ключів + дефолти.

---

## 5а. Зелена кнопка «FREE …» в меню (вимога від 2026-09-08)

Стосується **лише апок, які оновлюємо зараз**. Ті, що вже переведені на нову
систему, не чіпати.

Що треба: у меню, **першою в списку**, зелена кнопка з текстом на кшталт
«FREE COINS» / «FREE R$ REWARDS». Клік — одразу наш лендінг, тобто
`gdxGame.activity.showInterstitial()` (у custom-провайдері він відкриває таб
без частотного гейта, на відміну від front/back — саме те, що треба).

**Нову графіку пакувати не треба.** У шаблоні всі плашки (панель балансу,
панель питання квізу) намальовані плоским прямокутником з `drawerUtil`, тому
кнопка робиться тим самим:

```kotlin
// game/actors/button/AGreenButton.kt
open class AGreenButton(screen: AdvancedScreen, text: String) : ATextButtonAnimTexture(
    screen = screen, text = text, color = Color.WHITE,
    parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(20),
    generator = screen.fontGenerator_FIRENIGHT,
    style = AButtonAnimTexture.Style(
        default = TextureRegionDrawable(screen.drawerUtil.getRegion(GameColor.green_55BF40)),
    ),
)
```

⚠️ **Не перевішувати на лендінг існуючу плитку меню.** Спокуса є (плитка
Daily часто вже зелена), але напис на ній намальований усередині
`panel_main.png`: кнопка обіцятиме одне, а вестиме на інше, і сам екран стане
недосяжним разом зі своєю механікою, подіями та ключем економіки.

⚠️ **Пастка верстки в `AScrollLayout`.** `contentHeight` — це висота ВСЬОГО
контенту панелі, і після додавання кнопки вона більша за намальовану сітку.
Якщо `addContentGroup()` продовжить брати розмір з `contentHeight`, фонова
картинка розтягнеться, а актори поверх неї (панель балансу) поїдуть. Тримати
висоту сітки окремою константою:

```kotlin
override val contentHeight = GRID_HEIGHT + GAP + FREE_BTN_HEIGHT
…
aContentGroup.setSize(344f, GRID_HEIGHT)   // а не contentHeight
```

**Плейсмент — `interstitial`, і це остаточно.** У макеті трапляється підпис
«Плейсмент rewards», але плейсменти це закритий довідник, і всі, що існують,
уже проставлені в `AdManager` (banner · native · front · back · interstitial ·
`app_open`). `rewards` серед них немає, тому свій рядок сюди не вигадуємо:
`pl` іде в ключ доходу `<app>-<pl>`, і новий рядок почав би рахувати дохід з
нуля. Просто звемо `showInterstitial()` — він і дає `interstitial`.

---

## 6. Перевірка (обов'язково на девайсі)

```bash
export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"
sh ./gradlew :app:assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell pm clear <пакет>          # лічильники опт-іну лежать у prefs
adb shell am start -n <пакет>/.StartActivity
adb logcat -d -v brief | grep <ваш лог-тег>
```

| # | що | має бути |
|---|---|---|
| 1 | конфіг | у лозі `MODEL OUR = … atk=…`. Якщо `Our config failed → fallback` — апки ще немає в картці на сервері |
| 2 | черга старту | діалог «Turn on notifications» (заголовок ОДИН) → системний запит → баланс +100 → лендінг → гра. **У грі опиняємось у всіх гілках**, включно з «Later» і свайпом |
| 3 | гейтвей | у лозі `openAd URL: https://go.joystix.games/g?…&pl=<placement>` для banner/native/front/back/interstitial/app_open |
| 3а | петля | після закриття лендінга він НЕ відкривається знову; після виходу в іншу апку — відкривається |
| 4 | екрани | `screen_view` з кожного змістовного екрана (видно на сервері) |
| 5 | монети | панель балансу оновлюється після кожної механіки; `spend` не пускає баланс у мінус |
| 5а | зелена кнопка | перша в меню, сітка під нею не з'їхала (баланс на місці); тап відкриває лендінг **щоразу** — три тапи поспіль = три відкриття |
| 6 | диплінк | `adb shell am start -a android.intent.action.VIEW -d "<схема>://reward?h=test"` → відкрилась апка (невалідний токен = тиша, це норма). Повний вивід монет перевіряється ПІСЛЯ того, як схему впишуть у картку |
| 7 | release | `sh ./gradlew :app:assembleRelease`, далі в `app/build/outputs/mapping/release/mapping.txt`: `RemoteConfigModel -> …RemoteConfigModel` і `LocalPush$PushWorker -> …LocalPush$PushWorker` (обидва не перейменовані). Пуші перевіряються **тільки на release** |

⚠️ ProGuard-проблеми на debug не відтворюються взагалі.

---

## 7. Що передати серверній команді після релізу

- схема диплінка (`deeplink_scheme`);
- список ключів `economy` з дефолтами;
- прохання додати апку в картку (`/appconfig`) і тестову кампанію
  `notifications` з малим `delay_h` — без неї локальні пуші перевірити нічим.

---

## Пастки спільного шаблону

Не з міграції — це баги/особливості самого LibGDX-шаблону, спільного для всіх
ігор парку. Трапляються повторно, тому тут.

**`setOnClickListener` не працює всередині `ScrollPane`.** Розширення
`Actor.setOnClickListener` (`game/utils/actor/actorUtil.kt`) робить
`event.stop()` у `touchDown`. Подія перестає спливати до предків, а
flick-листенер `ScrollPane` висить саме на предку — тож щойно палець ліг на
елемент списку, скрол мертвий; скролиться тільки з проміжків між елементами,
де під пальцем фонова картинка. Другий бік тієї ж проблеми: порогу зсуву там
немає, тому протяжка, що завершилась у межах елемента, зараховується як тап.

Для елементів усередині `AScrollLayout` брати **`setOnTouchListener`** — він
подію не зупиняє і має поріг 10f. Кнопки на базі `AButtonBase`
(`ABlueButton`, `AGreenButton`, чекбокси) мають власний drag-aware лістенер і
в скролі поводяться правильно — їх чіпати не треба.

**`AScrollLayout.contentHeight` ≠ висота намальованої сітки.** Див. попередження
в розділі 5а: якщо додати щось у `addContent()`, а `addContentGroup()` лишити
на `contentHeight`, фонова картинка розтягнеться і актори поверх неї поїдуть.

**Відступ під рекламу в `AScrollLayout` — `maxOf`, і присвоєння, а не `+=`.**
Підписка на `AdSizeManager.adBottomFlow` мала одразу дві помилки, і обидві
дають той самий симптом — росте порожнеча між останнім елементом списку і
банером:

- `adBottomFlow` це `StateFlow`: віддає поточне значення на підписку і далі
  кожну зміну (нативка з'явилась, банер сховався, висота перерахувалась). З
  `paddingBottom += adBottom` відступ накопичувався з кожною емісією;
- базовий відступ («добити короткий контент до висоти pane») і рекламний
  закривають **одну й ту саму** дірку знизу, тому їх треба брати через
  `maxOf`, а не додавати.

Разом:

```kotlin
private var basePaddingBottom = 0f
…
basePaddingBottom = (scrollPane.height - contentHeight).coerceAtLeast(0f)
verticalGroup.paddingBottom = basePaddingBottom

collect { verticalGroup.paddingBottom = maxOf(basePaddingBottom, screen.adBottomUI.coerceAtLeast(0f)) }
```

Та сама логіка скопійована в `APanelLanguage` (свій ScrollPane повз
`AScrollLayout`) — правити обидва місця. Окремо перевірити підписки, які
двигають прив'язані до низу панелі (`marginBottom += adBottom` у
`QuizGameScreen`): там сума з базовим відступом правильна, але `+=` у
`collect` так само накопичується — має бути `marginBottom = БАЗА + adBottom`.

---

## Журнал міграцій

### T5 · `april_may/Game T5/RBX Boss` · `com.bossrbx.rbxcalculator` · 2026-09-08

Схема диплінка **`rbxcalculator`**. Баланс — почали з нуля (рішення
користувача), `PlayerData.rbx` прибрано, у `PlayerModel` лишився лише streak
щоденної нагороди.

Розмітка екранів: `main_screen`/HUB, `wheel_screen`/SPIN,
`scratch_screen`/GRID, `flip_card_screen`/REVEAL, `quiz_play_screen`+
`quiz_game_screen`/QUIZ, `daily_reward_screen`/DAILY,
`select_converter_screen`+`converter_screen`/TOOL.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `wheel_screen` | price | `0` (спін безкоштовний, ліміт 5 на візит) |
| `quiz.correct` | reward | `10` |
| `quiz.wrong` | penalty | `10` |
| `spin_wheel` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `scratch` | rewards_list | `[5,10,15,20,25,30,35,40,45,50]` |
| `flip_card` | rewards_list | `[15,25,50,75,100]` |
| `daily_reward` | rewards_list | `[15,20,25,30,40,50,100]` |

Скрол списку конвертерів: `APanelSelectConverter` переведено з
`setOnClickListener` на `setOnTouchListener` (див. «Пастки шаблону»).
Відступ під рекламу: `AScrollLayout` і `APanelLanguage` переведені на
`maxOf(base, adBottom)` з присвоєнням; `QuizGameScreen` — на
`marginBottom = PANEL_RS_MARGIN + adBottom`.

Зелена кнопка: `AGreenButton` + перший елемент у `APanelMain`, текст
«FREE R$ REWARDS», клік → `showInterstitial()` (`pl=interstitial`).
`GRID_HEIGHT` винесено окремо від `contentHeight`.

**08.09.2026, пізніше того ж дня.** Картка на сервері з'явилась: у лозі
`MODEL OUR = … atk=yes`, фолбек на Firebase RC більше не спрацьовує, гейт іде
на `go.joystix.games/g?app=rbxcalculator&pl=…`. При цьому всі провайдери в
картці стоять `na` — реклама вимкнена на сервері, банера й нативки немає. Черга
старту це переживає: лендінг пропускається, гра відкривається.

**Бекпорт правок пушів** (`docs/PUSH_FIX_2026-09-08.md`): три файли
`businesModule` (`Biz.kt`, `push/PushService.kt`, `push/LocalPush.kt`) взяті з
еталона з заміною префікса. Перевірено на девайсі: канал `rewards` існує одразу
після холодного старту з чистими даними, тобто до першого локального показу.

Старе (до появи картки): апки ще немає в картці на сервері — `/appconfig` не відповідає,
апка йде на легасі-фолбек Firebase RC, `atk` не видається, блоки `economy` і
`notifications` не приїжджають (тому локальні пуші не перевірені).
