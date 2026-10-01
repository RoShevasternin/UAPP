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
2. **Баланс існуючих гравців — ВИРІШЕНО, не питати.** `Wallet` тримає
   баланс у власних SharedPreferences (`wallet`/`balance`), а в грі баланс
   майже завжди лежить у своєму DataStore. Рішення користувача від 2026-09-09
   для **всього парку**: старий баланс не переносимо, Wallet стартує з нуля,
   своє сховище балансу прибираємо. (T5, T1 with ADS — так і зроблено.)
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

### ⚠️ Прибрав поле з `PlayerData` — `ignoreUnknownKeys` обов'язково

`DataStoreJsonUtil` шаблону декодує строгим `Json`. Поле `rbx`, прибране з
`PlayerData`, лишається в збереженні кожного живого юзера — і на оновленні
`Json.decodeFromString` кидає `JsonDecodingException: unknown key 'rbx'` у
корутині → **краш на кожному старті, апка не відкривається взагалі**
(відтворено на девайсі, T3, 2026-09-23). На тестах не видно, бо ми завжди
робимо `pm clear`.

⚠️ **Де саме читається збереження — залежить від шаблону.** У шаблонах з
`GameState` (T6, T7, T7-1, …) `PlayerData` декодує **`SaveGameStateManager.load()`**,
а не `DataStoreJsonUtil` — фікс в одному місці іншого не рятує (T7, 2026-09-23:
виправив `DataStoreJsonUtil`, а краш лишився). Перевіряти:
`grep -rn "Json.decodeFromString" app/src/main/java | grep -v businesModule`.

У знайденому місці — `Json { ignoreUnknownKeys = true }` для decode/encode і
`runCatching` навколо decode (зіпсоване збереження → дефолт, а не
краш). Перевірка: встановити апку, підкласти в
`files/datastore/DATA_STORE.preferences_pb` старий JSON з `rbx` (через
`run-as`), запустити.

⚠️ **T5, T1, T4, T9, T2, T7-1 мігровані БЕЗ цього фіксу** (перевірено grep'ом
2026-09-23: `ignoreUnknownKeys` — 0 входжень). Бекпорт — окремим рішенням.

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

### ⚠️ Ручки `price` і `penalty` ставимо ЗАВЖДИ — навіть коли сьогодні 0

Найчастіша моя ж помилка (T4 частково, T9 і T2 повністю, 2026-09-09): «сьогодні
механіка безкоштовна → ключ `price` не потрібен». Це неправильно. Без ключа
**ручки не існує**: щоб зробити спін платним, доведеться випускати нову версію
і чекати місяць розкатки. З ключем — один рядок у картці апки.

Коштує це нічого: `Wallet.spend(0)` повертає `true` одразу і не шле подій, тобто
при дефолті `0` поведінка апки не змінюється ані на крок.

Тому в кожній механіці, де є «спроба» (спін, картка, гра, розкриття):

```kotlin
val price = Econ.price(analyticsBlock!!, PRICE_DEF)   // PRICE_DEF = 0, якщо сьогодні безкоштовно
if (!Wallet.spend(price, bt = analyticsBt!!, block = analyticsBlock!!)) {
    gdxGame.activity.showToast("Not enough coins — you need $price")
    return@setOnClickListener
}
```

Те саме зі **штрафом** у квізі. Еталон знімає монети за неправильну відповідь:

```kotlin
if (userAnswer == question.answer) Wallet.add(Econ.reward(BLOCK, 10), bt, BLOCK)
else                              Wallet.spend(Econ.penalty(BLOCK, PENALTY_DEF), bt, BLOCK)
```

`PENALTY_DEF = 0`, якщо сьогодні неправильна відповідь нічого не коштує — гілка
`else` стає холостою, але ручка з'являється.

Виняток — механіки, де «спроби» немає за задумом: щоденна нагорода, гіфт,
реферал, калькулятори. Там ціна безглузда.

**`Econ.quest` — навмисно не використовується.** Ключ під механіку щоденних
завдань, якої немає в жодній грі парку, включно з еталоном (перевірено
2026-09-09: 0 викликів). Задіяти його = спершу вигадати механіку, а це рішення
не наше — питати серверну команду.

**Стан: борг закрито 2026-09-09.** Ручки `price` і `penalty` проставлені в усіх
мігрованих апках — еталон, T5, T1, T4, T9, T2. Звіряти нові міграції з ними.

Куди саме вішати ціну: у колеса — на кнопку спіну; у скретча / квіза / гесу /
мінігри, де механіка дає **одну спробу на візит** (скидання немає), — на вхід в
екран, `chargeEntryPrice()` у `show()`. Якщо коштів не вистачає, показуємо тост
і повертаємось назад.

Перевірено на девайсі (T2, `spin_win_screen`): з `PRICE_DEF = 100` баланс
10550 → 10480 (−100 ціна, +30 виграш); з дефолтом `0` списання зникає
(10480 → 10500 на виграші 20). Тобто ручка робоча, а нуль нічого не змінює.

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

⚠️ **Вирівнювання кнопки в вертикальній групі.** У `AVerticalGroup` дефолт
`alignH = LEFT`. Плитки меню намальовані всередині картинки з відступом 16, а
кнопка шириною 344 при LEFT стає в x=0 — візуально з'їжджає вліво. Групі, куди
її кладемо, треба явно `alignH = AlignH.CENTER` (T2, 2026-09-09).

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

## 5б. Онбординг — лише при першому запуску (вимога від 2026-09-09)

Стосується всіх апок, де перед меню стоїть візард/онбординг (`Select_1_Screen`
у T1 та подібні). Правило: **онбординг показується один раз; з другого запуску
апка стартує одразу в меню.**

Механіка — три точки, без DataStore:

```kotlin
// game/utils/Onboarding.kt — SharedPreferences "onboarding"/"done"
object Onboarding { val isDone: Boolean; fun markDone() }

// LoaderScreen.navigateToFirstScreen()
val first = if (Onboarding.isDone) MainScreen::class.java.name else Select_1_Screen::class.java.name

// MainScreen.show() — «дійшов до меню» = прапорець
override fun show() { super.show(); Onboarding.markDone() }
```

Чому SharedPreferences: рішення приймається на GDX-потоці в момент навігації,
синхронне читання без корутин. Чому прапорець у `MainScreen.show`, а не в
`onFinish` візарда: не залежить від того, скільки кроків і як саме візард
завершується — будь-який шлях у меню зараховується.

⚠️ Разом з цим `MainScreen` додати в `noAdScreens` `NavigationManager`
(в еталоні він там є): перехід `Loader → MainScreen` тепер прямий і не має
викликати `onFrontNavigation` одразу після `app_open`-гейта.

Перевірка: перший запуск з `pm clear` → онбординг → меню; `am force-stop` і
повторний старт → одразу меню, у `shared_prefs/onboarding.xml` `done=true`.

---

## 5в. Нативка в меню — прибрати (вимога від 2026-09-23)

На екрані меню (`MainScreen`/`HomeScreen`) нативну рекламу **не показуємо**:
прибрати `showNativeAt` у `show()` і `hideNative` у `hide()` меню. Вона закриває
пів сітки механік. Банер лишається, на інших екранах нативка як була.
Відступ списку (`adBottomFlow` = banner + native) перераховується сам: інші
екрани обнуляють `nativeHeightPx` у своєму `hide()`.

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

⚠️ Redmi 10C (MIUI, 4 ГБ): діалог «Встановити через USB» вбиває low memory
killer, поки Play Store у фоні оновлює апки, — install падає з
`USER_RESTRICTED` без діалогу. А **оновлення поверх (`-r`) MIUI після першої
відмови відхиляє автоматично** — ставиться лише чиста інсталяція
(`adb uninstall` → `adb install`). Кнопку «Встановити» тиснути за bounds з
`uiautomator dump`, не наосліп.

⚠️ На MIUI/HyperOS `adb install -r` може завершитись **без жодного виводу і
без помилки**, лишивши стару версію (T1, 2026-09-09: годину тестували не той
білд). Після кожного install звіряти
`adb shell dumpsys package <пакет> | grep lastUpdateTime` з часом APK.

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

**Нативка перекриває GDX-діалоги.** Нативка — Android-view поверх GDX, тож
будь-який попап на екрані з нативкою, що сягає низу екрана, ховається під нею
(T6, попап розблокування). Рішення: на час попапа `hideNative()`, після
закриття — знову `showNativeAt`, а сам попап центрувати над банером
(`marginBottom = adBannerUI`). Фіксований `verticalBias` не рятує: висота
нативки в UI-одиницях залежить від екрана.

**`ACheckBoxGroup`: повторний вибір того ж боксу глушив колбек.** `check()`
ставив `isInvokeCheckBlock = true`, а потім `group.onChecked(this)` робив
`uncheck(invokeBlock = false)` на ЦЬОМУ Ж боксі — прапорець злітав у false, і
`onCheckBlock` не викликався. У квізі (бокси відповідей перевикористовуються між
питаннями) це означає: відповідь на тій самій позиції, що й у попередньому
питанні, позначається, але не зараховується — квіз застрягає. Фікс (T3): у
`check()` спершу група, потім прапорець; у `onChecked` не знімати вибір, якщо
це той самий бокс.

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

**Бекпорт правок пушів** (`docs/for-devs/push-fix-08-09-2026/PUSH_FIX.md`): три файли
`businesModule` (`Biz.kt`, `push/PushService.kt`, `push/LocalPush.kt`) взяті з
еталона з заміною префікса. Перевірено на девайсі: канал `rewards` існує одразу
після холодного старту з чистими даними, тобто до першого локального показу.

Старе (до появи картки): апки ще немає в картці на сервері — `/appconfig` не відповідає,
апка йде на легасі-фолбек Firebase RC, `atk` не видається, блоки `economy` і
`notifications` не приїжджають (тому локальні пуші не перевірені).

### T1 · `april_may/Game T1/T1 with ADS` · `com.rbuxrds.counterds` · 2026-09-09

Схема диплінка **`counterds`**. Баланс — з нуля (правило парку, див. п. 0.2):
своє сховище `DS_Coin` уже було закоментоване, прибирати нічого.

**Особливість: еталон `T1-integrated` — це інтегрована версія ЦІЄЇ Ж гри**
(після нормалізації префікса реально відрізнявся 41 файл, решта ідентичні).
Тому інтегровані файли (екрани, `AWheel`, `AScratch`, `APanelTop`,
`APanelMainRBX`, `MainActivity`, `LoaderScreen`, `AdvancedScreen`) брались
з еталона цілком, а не правились точково. Що НЕ взято з еталона навмисно:

- онбординг `Select_1_Screen` + `select_1_Step` + текстури `screen_1` — в
  еталоні викинуті, тут лишені (`LoaderScreen` → `Select_1_Screen`,
  `NavigationManager` без змін, `APanelTop(this, false)` на візарді);
- `panel_main_balance` (панель балансу в шапці) — в атласі цієї апки регіону
  немає, атлас не перепаковували: регіон вирізано з `all.png` еталона
  (`sips -c 69 285 --cropOffset 2 62`) в окремий файл
  `textures/all/panel/panel_main_balance.png` і підключено як standalone
  текстуру через `SpriteManager.EnumTexture.PANEL_MAIN_BALANCE`;
- privacy policy URL у `MainActivity`, `strings.xml`, `main.mp3`,
  гучності в `SoundUtil`, анімація `ALoaderGroup` — свої, не еталонні.

Розмітка екранів (як в еталоні): `main_screen`/CATALOG, `spin_wheel_screen`/SPIN,
`scratch_screen`/GRID, `logic_quiz_time_screen`/QUIZ, `redeem_coin_screen`/GIFT,
`daily_converter_screen`+`daily_free_rbx_calculator_screen`+`settings_screen`/TOOL,
`memes_for_fun_screen`+`all_characters_screen`+`accessories_screen`+
`animations_screen`+`clothing_screen`+`head_and_body_screen`/CATALOG.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `spin_wheel_screen` | price | `10` |
| `scratch_screen` | price | `100` |
| `logic_quiz_time_screen` | reward / penalty | `10` / `10` |
| `spin_wheel` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `scratch` | rewards_list | `[50,100,150,200,250,300,350,400,450,500,1000,1500]` |

Зелена кнопка: `AGreenButton` на базі `ATextButton`/`AButtonTexture` (у T1
немає `AButtonAnimTexture` і `AScrollLayout`), фон — **генерована** текстура
`drawerUtil.getRoundedRegion(344, 72, r=12, градієнт 22C55E→16A34A)` в стилі
плиток меню + кругла іконка «R$» + шеврон «›». В `APanelMain` перша зверху:
`setBounds(16f, GRID_HEIGHT, …)`, `aContentGroup` = `GRID_HEIGHT + FREE_BTN_HEIGHT`.
⚠️ `GRID_HEIGHT` (1122) уже містить верхній відступ 16 — не додавати GAP
зверху, інакше відступ до першої плитки подвоюється.

На девайсі (Xiaomi, Android 13): картка на сервері вже є — `MODEL OUR … atk=yes`,
провайдери `custom_google`, гейт `go.joystix.games/g?…&pl=app_open|front|interstitial`.
Черга старту: діалог опт-іну → системний → «+100» → таб → гра, без петлі.
Баланс після старту **200**, не 100: `Wallet` при першому зверненні бере
`Econ.startBalance` (`economy.start_balance`, у картці немає → дефолт 100) +100
за опт-ін. Це штатно, в еталоні і T5 так само. Блок `economy` у картці — шаблон
з чужими ключами (`gravity_wheel_screen`, `olympus_quiz_screen`…), наших немає
→ працюють дефолти; список вище треба віддати серверній команді.
Release: `RemoteConfigModel` і `LocalPush$PushWorker` у mapping не перейменовані.
Онбординг `Select_1_Screen` — лише перший запуск (п. 5б): `Onboarding.kt`,
`LoaderScreen`, `MainScreen.show`, `MainScreen` у `noAdScreens`.

### T4 · `april_may/Game T4/RBX Golden` · `com.rbxgolden.fungamems` · 2026-09-09

Схема диплінка **`fungamems`**. Баланс — з нуля: `PlayerData.rbx` прибрано,
`PlayerModel` очищено від `addRbx/spendRbx/setRbx` і boost-режиму, лишився лише
streak щоденної нагороди. Лог-тег `COUNTER_DEBUG`, prefs `rscount_ads_prefs` —
свої, не чіпались.

На відміну від T1, це **повноцінна міграція** (еталон — інша гра): 33 екрани,
онбординг з 4 кроків, `MainActivity` свій (меми, share, копіювання).

Розмітка екранів — 28 змістовних: `main_screen`/HUB, `wheel_screen`/SPIN,
`scratch_screen`/GRID, `quiz_screen`/QUIZ, `daily_reward_screen`/DAILY,
`gift_screen`/GIFT, `converter_screen`+`select_converter_screen`/TOOL,
решта каталогів (меми, персонажі, аксесуари, анімації, одяг, голова/тіло) —
CATALOG. Технічні (Loader, Settings, Select_1/2/3, SelectAnimationPack) — `null`.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `wheel_screen` | price | `0` (спін безкоштовний — сьогоднішня поведінка) |
| `quiz_screen` | reward | `10` |
| `gift_screen` | reward | `50` |
| `wheel` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `scratch` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `daily_reward` | rewards_list | `[100,200,400,800,1600,3200,6400]` |

⚠️ **Ручку ціни проставлено лише колесу.** У `ScratchScreen` `Econ.price` +
`Wallet.spend` не додані, тож зробити скретч платним без релізу не вийде.
Те саме в T9 і T2 — там ручок немає взагалі (див. нижче).

Зелена кнопка: `AGreenButton` на `ATextButtonAnimTexture`, фон — генерована
`drawerUtil.getRoundedRegion(344, 72, r=16, градієнт 28BE41→1E9A33)`.
⚠️ Меню тут — **одна картинка** `PANEL_MAIN.png` + невидимі хітбокси, тому
кнопку додано окремим актором у `AVerticalGroup` НАД `aContentGroup`
(`GRID_HEIGHT` = 1084 лишається розміром картинки). `showInterstitial()`
у `MainActivity` не було — додано.

**Відступ під рекламу: виправлено у 24 файлах.** Той самий баг, що описаний у
«Пастках шаблону», але тут він розтиражований по всіх панелях зі списками
(`APanelSelect1/2/3`, всі каталоги, `DailyRewardScreen`…): `paddingBottom +=`
у `collect` StateFlow плюс сума бази з рекламним відступом. Переведено на
`basePaddingBottom` + `= maxOf(base, adBottom)`.

Дрібний баг заодно: `AScratch` друкував ім'я enum (`_50`), а не число —
підкреслення у шрифті NUMBERS відсутнє. Тепер підпис і нарахування беруться
з одного `payout()`.

На девайсі (Xiaomi, Android 13): картка на сервері вже є — `MODEL OUR … atk=yes`,
провайдери `custom_google`. Черга старту: опт-ін → системний → «+100» → таб →
онбординг; баланс 200 (start_balance 100 дефолт + 100 за опт-ін). Зелена кнопка:
3 тапи = 3 відкриття `pl=interstitial`. Колесо: 200 → 225 на секторі 25.
Повторний запуск — одразу меню (`onboarding.done=true`), баланс збережено.
Диплінк `fungamems://reward?h=test` відкриває апку. Release: `RemoteConfigModel`
і `LocalPush$PushWorker` у mapping не перейменовані.

⚠️ `google-services.json` у цьому проєкті **не було взагалі** — апка не
збиралась (`processDebugGoogleServices` падає). Файл дав користувач.

### T9 · `june/Game T9/RBX RUSH` · `com.rbxrush.rushrbx` · 2026-09-09

Схема диплінка **`rushrbx`**. Баланс — з нуля: `GameState.rbxFlow`,
`PlayerData.rbx` і `PlayerModel.addRbx/setRbx/getRbx/spendRbx` прибрано,
лишився streak щоденної нагороди. Лог-тег `RETUSH`, prefs `rush_ads_prefs` —
свої. `ic_notification` вже був свій — з еталона не копіювався.

⚠️ **Пастка, якої не було в інших апках: наївний запит дозволу на пуші.**
У `MainActivity.onCreate` стояв `requestNotificationPermission()` — голий
системний діалог на сплеші. Android дає його один раз за встановлення: два
відмови і дозвіл випалено назавжди, без жодного шансу пояснити навіщо.
Замінено на опт-ін з нагородою через `Biz.runStartupFlow`; launcher лишився в
активіті (контракт має бути зареєстрований до `onStart`), але тепер віддає
результат у колбек черги старту. **Перевіряти це в кожній наступній апці:**
`grep -n "requestNotificationPermission\|POST_NOTIFICATIONS" MainActivity.kt`.

У T9 вже були `POST_NOTIFICATIONS` у маніфесті, `firebase-messaging` і
`default_notification_icon` — дописано лише `work-runtime`, диплінк-фільтр,
`PushService` і ProGuard-правило.

Розмітка екранів — 15 змістовних: `home_screen`/HUB, `wheel_screen`/SPIN,
`scratch_screen`/GRID, `quiz_screen`/QUIZ, `guess_screen`/REVEAL,
`free_screen`/GIFT, конвертери/TOOL, персонажі та одяг/CATALOG.
Технічні (Loader, Settings, Onboarding, Selector_1..4) — `null`.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `quiz_screen` | reward | `10` (за правильну відповідь) |
| `free_screen` | reward | `500` |
| `wheel` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `scratch` | rewards_list | `[50,100,150,200,250,300,350,400,450,500]` |
| `guess` | rewards_list | `[100,200,300,500,700]` |
| `daily_reward` | rewards_list | `[100,200,400,800,1600,3200,6400]` |

Цін немає: жодна механіка сьогодні не списує монети, тому ключів `prices` не
заводимо (порожня відповідь сервера нічого не зламає).

Зелена кнопка: `AGreenButton` на `ATextButtonAnim` (у T9 `AButtonAnim.Style`
приймає один drawable), фон — генерована `getRoundedRegion(344, 72, r=16,
градієнт 3DC44B→2A9C36)`. Перша в `APanelHome` (`AAutoLayout`, тому просто
`add()` першим). ⚠️ Плитку «Free Coins» не чіпав — у неї своя механіка,
подія `free_screen`/GIFT і ключ економіки.

На девайсі (Xiaomi, Android 13): `MODEL OUR … atk=yes`, провайдери
`custom_google`. Черга старту: опт-ін → системний → «+100» → таб (`pl=app_open`)
→ онбординг; баланс 200. Зелена кнопка: 3 тапи = 3 відкриття `pl=interstitial`.
Щоденна нагорода: 200 → 300 (день 1 = 100). Повторний запуск — одразу меню
(`onboarding.done=true`), баланс 300 збережено. Диплінк `rushrbx://reward?h=test`
відкриває апку. Release: обидва класи в mapping не перейменовані.

Дрібниця: у `SaveGameStateManager` прибрано рядок `RBX` з дампу стану — поле
переїхало у Wallet.

### T2 · `april_may/Game T2/RSBUX COUNTER with ADS` · `com.rsbuxs.rcounbux` · 2026-09-09

Схема диплінка **`rcounbux`**. Баланс — з нуля: `PlayerData.rbx` і
`addRbx/spendRbx/setRbx` прибрано. Лог-тег `RCUNTER`, prefs `rscount_ads_prefs`.
Наївного запиту пушів у `MainActivity` не було (перевірено за правилом з T9).

**Boost Mode збережено.** В апці є екран, що вмикає ×2 до нагород
(`PlayerModel.isBoostMode`, раніше множник сидів усередині `addRbx`). Оскільки
`Wallet.add` про boost не знає, множник винесено в `PlayerModel.boosted(amount)`
і застосовується **до** нарахування в кожному місці. Перевірено на девайсі:
сектор 150 × 2 = 300.

Розмітка екранів — 10 змістовних: `main_screen`/HUB, `spin_win_screen`/SPIN,
`scratch_screen`/GRID, `quiz_time_screen`/QUIZ, `daily_reward_screen`/DAILY,
`mini_game_screen`/**TAP** (єдина в парку механіка на влучання),
`n_to_rbx_screen`+`rbx_calculator_screen`/TOOL,
`referral_bonus_screen`+`boost_mode_screen`/GIFT.
Технічні (Loader, мова, вітання, налаштування, MiniGameWelcome) — `null`.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `quiz_time_screen` | reward | `5` (за правильну відповідь) |
| `mini_game_screen` | reward | `1` (за влучання) |
| `daily_reward_screen` | reward | `5` (множиться на номер дня) |
| `spin` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `scratch` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |

Щоденна нагорода тут не список, а формула «день × 5», тому ключ скалярний.
Цін немає — жодна механіка не списує монети.

Зелена кнопка: **використано наявний `AGreenButton` апки**, а не генерований
фон. Причина — вся палітра T2 зелена (`green_06`, `green_81`), і в апці вже є
основна CTA-кнопка з текстурою `green_btn` («Get Started», «Spin Now», «Play»).
Друга зелена іншого відтінку виглядала б чужою. Кнопка перша в `APanelMain`,
`GRID_HEIGHT` = 840 (висота `PANEL_MAIN.png`) винесено окремо від висоти групи.

Відступ під рекламу виправлено у двох місцях (як і попереджає розділ «Пастки»):
`APanelMain` — підписки на `adBottomFlow` там **узагалі не було**, висота
читалась один раз на старті, коли банера ще немає (тобто майже завжди 0);
`APanelLanguage` — був `+=` у `collect`, тобто накопичення.

На девайсі (Xiaomi, Android 13): `MODEL OUR … atk=yes`, провайдери
`custom_google`. Черга старту: опт-ін → системний → «+100» → таб → вибір мови;
баланс 200. Зелена кнопка: 3 тапи = 3 відкриття `pl=interstitial`. Дейлі день 1:
200 → 205. Колесо: 205 → 210 (сектор 5); з boost: 210 → 510 (сектор 150 ×2).
Повторний запуск — одразу меню (`onboarding.done=true`), баланс 510 збережено.
Диплінк `rcounbux://reward?h=test` відкриває апку. Release: обидва класи в
mapping не перейменовані.

### T7-1 · `june/Game T7-1/RBX Treasure` · `com.treprosure.starbxup` · 2026-09-09

Схема диплінка **`starbxup`**. Лог-тег `TRESHER`, prefs `treasure_ads_prefs`.
Наївного запиту пушів не було. `ic_notification` в апці не було — взято з еталона.

**Онбордингу немає взагалі** (`LoaderScreen` → `HomeScreen`), тому `Onboarding.kt`
тут не потрібен, а `HomeScreen` уже стояв у `noAdScreens`. Перша апка парку без
онбординга — не шукати його там, де його немає.

⚠️ **Стартовий баланс змінився: було 1000, стало 100.** Своє сховище
(`GameState.rbxFlow(1000L)`, `PlayerData.rbx = 1000`) прибрано, `Wallet` бере
`Econ.startBalance` — дефолт модуля 100. Для нових установок це помітна зміна.
Якщо треба лишити 1000 — серверна команда ставить `economy.start_balance: 1000`
у картці, релізу не потрібно.

Розмітка екранів — 16 змістовних: `home_screen`/HUB, `wheel_screen`/SPIN,
`scratch_screen`/GRID, `quiz_screen`/QUIZ, `finds_screen`/REVEAL,
`gift_screen`/GIFT, `daily_screen`/DAILY, конвертери/TOOL,
персонажі та одяг/CATALOG. Технічні (Loader, Settings) — `null`.

Ключі економіки (ручки `price` і `penalty` проставлені одразу, за правилом
розділу 5 — не відкладались у борг):

| ключ | тип | дефолт |
|---|---|---|
| `quiz_screen` | reward / penalty / price | `10` / `0` / `0` |
| `gift_screen` | reward | `200` |
| `finds_screen` | reward | `100` (за виграшну карту) |
| `wheel_screen` | price | `0` |
| `scratch_screen` | price | `0` |
| `wheel` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `scratch` | rewards_list | `[5,10,15,20,25,30,35,40,45,50]` |
| `daily_reward` | rewards_list | `[100,200,400,800,1600,3200,6400]` |

Зелена кнопка: `AGreenButton` на `ATextButtonAnim`, фон — генерована
`getRoundedRegion(344, 72, r=16, градієнт 3FAA2A→2E8420)`. Палітра апки
коричнево-золота (скарбниця), тож зелений тут єдиний і справді тягне око.
`APanelHome` успадковує `AScrollLayout`, тому кнопка просто перша в
`addContent()` — одразу під панеллю балансу.

`APanelScratch` у цій апці числа на картці **не малює** (тільки картинка
`SCRATCH_WIN`), тож розбіжності «підпис vs нарахування» тут не буває — але
суму все одно проведено через `Econ`, щоб крутилась із сервера.

На девайсі (Xiaomi, Android 13): `MODEL OUR … atk=yes`, провайдери
`custom_google`. Черга старту: опт-ін → системний → «+100» → таб
(`pl=app_open`) → меню; баланс 200. Зелена кнопка: 3 тапи = 3 відкриття
`pl=interstitial`. Щоденна нагорода: 200 → 300 (день 1 = 100), шапка оновилась.
`QuizScreen` з вхідною ціною 0 відкривається і не викидає, баланс не змінюється.
Release: обидва класи в mapping не перейменовані.

⚠️ Колесо і скретч на девайсі **не прокручені** — навігація в меню після
«назад» щоразу з'їжджає, і тап не влучав. Код там ідентичний за формою до T9/T2,
де перевірено (у T2 ціна доведена експериментом 100 → списання, 0 → без
списання). При наступному дотику до апки прокрутити обидві механіки.

### T3 · `april_may/Game T3/Rbux Drop` · `com.rbuxdrop.cougame` · 2026-09-23

Схема диплінка **`cougame`**. Лог-тег `COUNTER`, prefs `rscount_ads_prefs`.
Баланс — з нуля: `PlayerData.rbx` і `PlayerModel.rbxFlow/addRbx/spendRbx/setRbx`
прибрано разом з мертвим boost (екрана boost у T3 немає). Наївного запиту
пушів не було. `ic_notification` взято з еталона.

**Разом з міграцією — оновлення тулчейну:** Gradle 9.6.1 → 9.7.1, AGP 9.3.1 →
9.4.1, Kotlin (serialization plugin) 2.4.10 → 2.4.20, firebase-bom 34.16.0 →
34.19.0, play-services-ads 25.4.0 → 25.5.0, appcompat 1.7.1 → 1.8.0,
navigation 2.9.8 → 2.10.1, TikTok SDK 1.6.1 → 1.7.1. Решта вже була на
останніх стабільних. Збирається чисто, TikTok 1.7.1 без змін API.

⚠️ **Знайдено й виправлено краш на оновленні** (див. розділ 5,
`ignoreUnknownKeys`): відтворено на девайсі зі збереженням старої версії
`{"rbx":250,"dailyRewardDay":2,…}` → після фіксу серія дейлі збереглась.

Розмітка екранів — 10 змістовних: `main_screen`/HUB, `wheel_screen`/SPIN,
`scratch_screen`/GRID, `flip_screen`/REVEAL, `quiz_screen`+`quiz_start_screen`/QUIZ,
`daily_reward_screen`/DAILY, `select_converter_screen`+`converter_screen`/TOOL,
`tips_screen`+`tips_selected_screen`/CATALOG. Технічні (Loader, Language,
Onboarding, Settings, QuizHow) — `null`.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `wheel_screen` | price | `0` (на кнопку спіну) |
| `scratch_screen` | price | `0` (на вхід) |
| `flip_screen` | price | `0` (на вхід) |
| `quiz_start_screen` | price / reward / penalty | `0` / `5` / `0` |
| `wheel` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `scratch` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `flip_card` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `daily_reward` | rewards_list | `[100,200,400,800,1600,3200,6400]` |

⚠️ **Картка на сервері — шаблон з чужими ключами, і два з них збіглись з
нашими:** `prices.scratch_screen: 100` (вхід у скретч реально списав 100) і
`rewards_list.scratch` `[0,0,0,50,…,210]` тієї ж довжини 12 (скретч платить за
ним). Ручки працюють, але значення випадкові — картку треба заповнити таблицею
вище.

Зелена кнопка: `AGreenButton` на `ATextButtonTexture` (у T3 немає
`AButtonAnimTexture`/`FontFactory`), фон — `getRoundedRegion(344, 72, r=16,
22C55E→16A34A)`, натиснутий стан темніший, іконка «R$» і шеврон на `ALabel`.
Меню — одна картинка `PANEL_MAIN` (654) + хітбокси, тому кнопка окремим актором
у `AVerticalGroup(alignH = CENTER)` над сіткою. Нативку з меню прибрано (5в).

Дрібні баги заодно: `AScratch` друкував ім'я enum (`_50`) — тепер `payout()`;
відступ під рекламу `+=` → `maxOf` у `APanelMain`, `APanelLanguage`,
`DailyRewardScreen`; `ACheckBoxGroup` (квіз застрягав, див. «Пастки»).

На девайсі (Redmi 10C, Android 13): `MODEL OUR … atk=yes`, провайдери
`custom_google`. Черга старту: опт-ін (заголовок один) → системний → +100 → таб
(`pl=app_open`) → мова → онбординг → меню, петлі немає; баланс 200. Зелена
кнопка: 3 тапи = 3 `pl=interstitial`. Банер — `pl=banner`, навігація —
`front`/`back`. Колесо 200 → 220 (сектор 20 під стрілкою). Скретч −100 (ціна з
картки) +50. Фліп +45. Квіз 4/4 → +20, відповіді на однаковій позиції
зараховуються. Дейлі день 2 → +200. Повторний запуск — одразу меню
(`onboarding.done=true`), баланс збережено. Диплінк `cougame://reward?h=test`
відкриває апку, баланс не змінює. Release: обидва класи в mapping не
перейменовані (APK непідписаний — signing config у проєкті немає, пуші на
release не перевірені).

### T6 · `april_may/Game T6/Skinly` · `com.skindustry.skinly` · 2026-09-23

Схема диплінка **`skinly`**. Лог-тег `SKINER`, prefs `skinly_prefs`. Наївного
запиту пушів не було. `ic_notification` взято з еталона. Тулчейн оновлено так
само, як у T3 (Gradle 9.7.1, AGP 9.4.1, Kotlin 2.4.20 …).

**Інший тип гри: редактор скінів без валюти.** Картки (одяг, текстури, стікери)
розблоковувались лише за рекламу. Рішення користувача (2026-09-23): **монети як
альтернатива рекламі**. Баланс (`APanelBalance` на `Wallet.balanceFlow`) — у
шапці Home; у попапі розблокування — друга кнопка «Unlock for N coins».
`PlayerData` не чіпали (балансу там не було), але `ignoreUnknownKeys` додано про
запас — і в `DataStoreJsonUtil`, і в `SaveGameStateManager` (саме він тут читає
`PlayerData`; спершу фікс стояв лише в першому — виправлено разом з T7).

Попап: PNG 344×334 з намальованими «Watch Ad»/«Cancel» подовжено вниз на 64 —
кнопка монет лягає на намальований Cancel, Cancel переїхав на білу смугу
знизу; під PNG — біла закруглена підкладка, щоб не було вирізів на стику.
Нативку на час попапа ховаємо (див. «Пастки»).

Розмітка екранів: `home_screen`/HUB, `home_select_screen`/CATALOG,
`skin_book_screen`/CATALOG, `personalization_screen`/TOOL, `share_screen`/TOOL.
Технічні (Loader, Onboarding, Selector_1..3, Settings) — `null`.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `home_select_screen` | price | `100` (розблокування одягу за монети) |
| `personalization_screen` | price | `100` (текстура / стікер за монети) |

`featureComplete` — на кожне розблокування (за рекламу без amount, за монети з
amount = ціна).

Зелена кнопка «**FREE COINS**» (валюта тут монети, не R$) — на
`ATextButtonAnimTexture`, над каруселлю Home, `pl=interstitial`. Нативки на Home
і не було.

Онбординг (Onboarding → Selector_1..3) — лише перший запуск. `HomeScreen` у
`noAdScreens` НЕ додано: Home ↔ SkinBook перемикаються нижньою панеллю, і там
front-реклама потрібна. Замість цього рекламу пропускаємо тільки для
`Loader → Home` (`fromScreenName == null`).

Заодно: накопичення відступу `+=` у `collect` — 8 місць (Onboarding,
Selector_1..3, Home, SkinBook, Personalization, `AScrollLayout`);
`ACheckBoxGroup` — той самий фікс, що в T3; прев'ю в попапі `HomeSelectScreen`
складалися стопкою — тепер попереднє прибирається.

На девайсі (Redmi 10C, Android 13): `MODEL OUR … atk=yes`. Черга старту:
опт-ін → системний → +100 → таб (`pl=app_open`) → онбординг → Home; з «Later»
теж доходить до гри. Зелена кнопка: 3 тапи = 3 `pl=interstitial`. Розблокування
за монети 200 → 100 → 0, третя спроба — тост «Not enough coins», попап лишається.
За рекламу — таб `pl=interstitial`, після закриття картка відкрита. Стан
зберігається (`unlockedTShirt`, `unlockedTextureSolid`). Повторний запуск —
одразу Home. Диплінк `skinly://reward?h=test` відкриває апку. Release: обидва
класи в mapping не перейменовані (APK непідписаний, пуші на release не
перевірені).

### T7 · `june/Game T7/RBX Treasure` · `com.rbxtreasure.fungamers` · 2026-09-23

Схема диплінка **`fungamers`**. Лог-тег `TRESHER`, prefs `treasure_ads_prefs`.
Тулчейн — як у T3/T6.

**Це та сама гра, що T7-1, але окрема апка** (інший пакет, своя графіка, свій
privacy URL). Після нормалізації пакета T7 збігався з T7-1 **до міграції** у 156
файлах; відмінності — онбординг (`OnboardingScreen` + `Selector_1..4`, у T7-1 його
немає), `SpriteManager`/`SpriteUtil`/`SoundUtil`/`Constants`, `NavigationManager`,
privacy URL у `MainActivity`. Тому, як з T1: усі файли, де T7 == T7-1-до-міграції,
взято з мігрованого T7-1 (55 файлів, з `businesModule`), `MainActivity` і
`LoaderScreen` — з T7-1 з точковими правками, решту своїх не чіпали. Розмітка
екранів і ключі економіки — як у T7-1 (див. вище).

Поверх T7-1 — те, що з'явилось після його міграції:
- ⚠️ **краш на оновленні відтворено і виправлено** у `SaveGameStateManager`
  (`PlayerData.rbx` прибрано ще в T7-1): збереження `{"rbx":1500,…}` → після
  фіксу серія дейлі «день 2» збереглась;
- онбординг лише при першому запуску (`Onboarding.kt`, `HomeScreen.show`);
  реклама пропускається тільки на `Loader → Home`;
- нативку з Home прибрано (5в);
- `+=` відступу — 8 місць (у т.ч. `FindsScreen`/`WheelScreen`, скопійовані з
  T7-1 — **у T7-1 цей баг досі є**), `AScrollLayout`;
- `ACheckBoxGroup`.

На девайсі (Redmi 10C, Android 13): `MODEL OUR … atk=yes`. Черга старту:
опт-ін → системний → +100 → таб (`pl=app_open`) → онбординг (4 сторінки +
4 селектори) → Home; баланс 200 (старі 1500 не переносимо — правило парку).
Зелена кнопка: 3 тапи = 3 `pl=interstitial`. Дейлі день 2: +200. Колесо: +45,
під стрілкою 45 (у T7-1 не було перевірено). Скретч: вхід безкоштовний, +25.
Повторний запуск — одразу Home (`onboarding.done=true`), баланс 470 збережено.
Диплінк `fungamers://reward?h=test` відкриває апку. Release: обидва класи в
mapping не перейменовані (APK непідписаний).

Дрібниця, не чіпав: на екранах колеса і скретча намальовано «Try your luck for
300 RBX» / «all for just 450 RBX», хоча ціни спроби дефолтні 0 — текст з
дизайну, був і до міграції.

### T8 · `june/Game T8/RBX COINS` · `com.coinsclub.funrbx` · 2026-09-23

Схема диплінка **`funrbx`**. Лог-тег `COINS_DEBUS`, prefs `coins_ads_prefs`.
Тулчейн — як у T3/T6/T7.

**Той самий шаблон, що T9 (RBX RUSH), але своя гра:** своя графіка, інший
набір панелей Home, інші суми. З T9-до-міграції (коміт `31a8138`) збігалося 97
файлів. Тому міграцію T9 перенесено **трибічним злиттям** (`git merge-file`:
T8 × T9-до × T9-після): 24 файли взято цілком, 18 злились самі, 11 конфліктів
розібрано руками. Спосіб варто повторювати для апок на спільному шаблоні:
всі рішення вже мігрованої апки приїжджають, а своє апки не губиться.

Що в конфліктах вирішено на користь **T8** (дефолт = сьогоднішня поведінка):
- квіз: нагорода росте з номером питання (10/20/30/40/50) → список
  `rewards_list.quiz`, а не скаляр 10 з T9; у T8 намальовані ті самі +10…+50;
- guess: одна сума 250 за виграшну картку → `rewards.guess_screen` = 250
  (у T9 був пул 100…700);
- free: 300 → `rewards.free_screen` = 300 (у T9 — 500);
- скретч: `[50…500]`; колесо — `[5…150]` (як у T9);
- на Home лишено свою плашку балансу `APanelRBX`, зелена кнопка — одразу під нею.

Наївного запиту пушів тут не було; `requestPushPermission` з T9 приїхав разом з
імпортами (`Manifest`, `Build`, `ContextCompat`, `PackageManager` — дописано).
`AGreenButton` з T9 посилався на шрифти Fredoka, яких у T8 немає →
`fontGenerator_LuckiestGuy_Regular`.

Поверх T9 (з'явилось після його міграції): фікс крашу на оновленні у
`SaveGameStateManager` і `DataStoreJsonUtil` (відтворено: збереження
`{"rbx":1500,…}` читається, серія дейлі «день 2» лишилась), нативку з Home
прибрано, `+=` відступу — 14 місць, `ACheckBoxGroup`. `HomeScreen` у T8 вже був
у `noAdScreens` — лишено.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `wheel_screen` / `scratch_screen` | price | `0` |
| `quiz` | rewards_list | `[10,20,30,40,50]` |
| `quiz_screen` | penalty | `0` |
| `guess_screen` | reward / price | `250` / `0` (ціна на вхід) |
| `free_screen` | reward | `300` |
| `wheel` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150]` |
| `scratch` | rewards_list | `[50,100,150,200,250,300,350,400,450,500]` |
| `daily_reward` | rewards_list | `[100,200,400,800,1600,3200,6400]` |

На девайсі (Redmi 10C, Android 13): `MODEL OUR … atk=yes`. Черга старту:
опт-ін → системний → +100 → таб → онбординг (3 сторінки + 4 селектори) → Home;
баланс 200. Зелена кнопка: 3 × `pl=interstitial`. Дейлі день 2: +200, шапка
оновилась, пішов таймер. Колесо +5 — під стрілкою 5. Скретч +500. Квіз +10, +20.
Повторний запуск — одразу Home, баланс 935 збережено. Диплінк
`funrbx://reward?h=test` відкриває апку. Release: обидва класи в mapping не
перейменовані (APK непідписаний).

Не чіпав: «for 300 RBX» / «for just 450 RBX» на колесі й скретчі — з дизайну,
ціни спроби дефолтні 0.

### T2-1 · `june/Game T2-1/RSBUX COUNTER with ADS` · `com.zahbx.blitzrbx` · 2026-09-23

Схема диплінка **`blitzrbx`**. Лог-тег `RCUNTER`, prefs `rscount_ads_prefs`
(ті самі, що в T2 — це різні апки, конфлікту немає). Тулчейн — як у T3/T6/T7/T8.

**Двійник T2** з іншою графікою («RBX BLITZ»): з T2-до-міграції (`158457c`)
збігалося 124 файли. Міграцію T2 перенесено трибічним злиттям, як у T8: 38 файлів
взято цілком, 3 злились самі, 2 конфлікти. Розмітка екранів, ключі економіки
(`quiz_time_screen`, `mini_game_screen`, `daily_reward_screen`, `spin`, `scratch`),
Boost Mode, зелена кнопка на текстурі `green_btn`, `maxOf`-відступи — усе з T2.

Відмінності від T2, збережені:
- **онбордингу немає**: `Loader → MainScreen` одразу, мова — лише з Settings.
  `Onboarding.kt` з T2 прибрано (був би мертвий), `LanguageScreen` у `noAdScreens`
  не додано — туди заходять з налаштувань, реклама там доречна;
- `versionCode 2 / 2.0.0` і своя конфігурація мініфікації (debug теж
  `isMinifyEnabled = true`) — не чіпав.

Поверх T2: фікс крашу на оновленні (`DataStoreJsonUtil`; відтворювано на
збереженні `{"rbx":1500,…}` — читається, серія «день 2» лишилась), нативку з
меню прибрано, `ACheckBoxGroup`.

На девайсі (Redmi 10C, Android 13): `MODEL OUR … atk=yes`. Черга старту:
опт-ін → системний → +100 → таб → меню; баланс 200. Зелена кнопка: 3 ×
`pl=interstitial`. Дейлі день 2: +10 (день × 5). Колесо +30 — під стрілкою 30,
шапка оновилась. Квіз +5. Повторний запуск — меню, баланс 245 збережено. Диплінк
`blitzrbx://reward?h=test` відкриває апку. Release: обидва класи в mapping не
перейменовані (APK непідписаний).


### T10 · `july/Game T10/RBX Sakura` · `com.sakurbx.fungambx` · 2026-09-24

Схема диплінка **`fungambx`**. Лог-тег `SKAKURA`, prefs `rush_ads_prefs` (ім'я
лишилось від шаблону T9 — своє, не чіпав). Наївного запиту пушів не було.
`ic_notification` взято з еталона. `google-services.json` поклав користувач
(пакет збігається). Тулчейн — як у T3/T6/T7/T8/T2-1.

**Ще одна гра на шаблоні T9/T8** («сакура»: рожева палітра, свої суми). З
T9-до-міграції збігалося 96 файлів, з T8-до — 93. Донор — **T8** (у ньому вже
всі пізніші фікси: краш на оновленні, `ACheckBoxGroup`, `maxOf`/`=`-відступи,
нативка з меню), трибічне злиття як у журналі T8. Але верстка T10 відрізняється
сильніше, ніж у T8↔T9, тому частину файлів **не зливав, а правив вручну поверх
своєї версії**: `HomeScreen`, `APanelHome`, `APanelFree`, `GuessScreen`, 4
селектори, `OnboardingScreen` (конфлікти там — не зміст міграції, а різна
розкладка). `QuizController` узято з мігрованого **T9** (T10 == T9-до байт у
байт; у T8 квіз зі зростаючою нагородою, тут — фіксовані 10).

⚠️ **Колесо — 15 номіналів, а не 12.** Enum `AWheel.Result` у T10 має ще
`_500/_125/_250`, тож `DEFAULT_SUMS` з T8 (`[5…150]`, 12 шт.) тут не годився —
виправлено на 15 значень у порядку enum. Звіряти `DEFAULT_SUMS` з enum у кожній
апці на цьому шаблоні: розбіжність довжини тиха (Econ просто бере дефолт).

Відмінності від T8, збережені:
- баланс — у верхній панелі (`APanelTopHome` → `APanelRBX`), а не в списку
  меню; `APanelRBX` переведено на `Wallet.balanceFlow`;
- зелена кнопка перша в `APanelHome` (над Daily), шрифт `Kedebideri_ExtraBold`
  (Luckiest Guy у T10 немає); зелений — єдиний на рожевому меню;
- онбординг сам кличе `onFrontNavigation()` на кожну сторінку — так було до
  міграції, не чіпав.

⚠️ **Стартовий баланс змінився: було 1000, стало 100** (`GameState.rbxFlow(1000L)`
прибрано). Як у T7-1 — якщо треба 1000, це `economy.start_balance` у картці.

Діалоги `showInput` і `showCoinsDialog` — з `suppressAppOpenUntilMs` (як у T3).

Розмітка екранів — 15 змістовних: `home_screen`/HUB, `wheel_screen`/SPIN,
`scratch_screen`/GRID, `quiz_screen`/QUIZ, `guess_screen`/REVEAL,
`free_screen`/GIFT, `daily_reward_screen`/DAILY (з контролера, без екрана),
конвертери/TOOL, персонажі та одяг/CATALOG. Технічні (Loader, Settings,
Onboarding, Selector_1..4) — `null`.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `wheel_screen` / `scratch_screen` / `guess_screen` | price | `0` |
| `quiz_screen` | price / reward / penalty | `0` / `10` / `0` |
| `guess_screen` | reward | `100` (за виграшну картку) |
| `free_screen` | reward | `200` |
| `wheel` | rewards_list | `[5,10,15,20,25,30,35,40,45,50,100,150,500,125,250]` |
| `scratch` | rewards_list | `[50,100,150,200,250,300,350,400,450,500]` |
| `daily_reward` | rewards_list | `[100,200,400,800,1600,3200,6400]` |

На девайсі (Redmi 10C, Android 13): `MODEL OUR … atk=yes`, провайдери
`custom_google`. Черга старту: опт-ін (заголовок один) → системний → +100 → таб
(`pl=app_open`) → онбординг (3 сторінки + 4 селектори) → Home; баланс 200.
Зелена кнопка: 3 × `pl=interstitial`. Дейлі день 1: +100. Колесо +100 — під
стрілкою 100. Скретч +350 (вхід безкоштовний). Квіз 4/7 → +40. Guesser +100.
Free +200 (`pl=interstitial`). Повторний запуск — одразу Home
(`onboarding.done=true`), баланс 1090 збережено, зайвого `front` після
`app_open` немає. Диплінк `fungambx://reward?h=test` відкриває апку. Краш на
оновленні: збереження `{"rbx":1500,…}` читається, серія «день 2» лишилась.
Release: обидва класи в mapping не перейменовані (APK непідписаний).

### T23 · `july/Game T23/RBX Racing` · `com.racing.funtols` · 2026-09-24

Схема диплінка **`funtols`**. Лог-тег `GONKA`, prefs `ads_prefs`. `ic_notification`,
`POST_NOTIFICATIONS`, `firebase-messaging` і `default_notification_icon` у T23 уже
були свої — дописано лише `work-runtime`, диплінк-фільтр, `PushService`, ProGuard.
`google-services.json` у проєкті не було — узято з `july/Game T23/T23.zip`
(пакет і Firebase-проєкт `t23-rbx-racing` збігаються). Тулчейн — як у T10.

⚠️ **Наївний запит пушів** (`requestNotificationPermission()` в `onCreate`, як у
T9) — прибрано, запит тепер лише з черги старту.

**Своя гра на каркасі T9-родини, але з іншими механіками і MSDF-шрифтами.** З
T8/T9/T10-до збігалося лише ~48 файлів із 172, найближча за кодом T28 (94) — ще
не мігрована. Тому злиття з T8 взято лише там, де воно чисте (`StartActivity`,
`ACheckBox*`, `ShapeDrawerUtil`, `DailyRewardController`, `NavigationManager`,
`CharacterScreen`, `SaveGameStateManager`, `AdvancedScreen`); `MainActivity` і
`App` — трибічно з **T10** (4 конфлікти, лише імпорти й блок дозволу); решта —
вручну. Відступи під рекламу в T23 від початку `= база + adBottom` — `+=` немає.
`DataStoreJsonUtil` у T23 немає — `PlayerData` читає лише `SaveGameStateManager`.

Монети йшли через один `APopup` (спільний для всіх механік) → там тепер
`Wallet.add` + `featureComplete` з `bt/block` екрана-власника (`screen.analyticsBt`).
Зелена кнопка — своя `AGreenButton` на `ATextButtonAnim` + MSDF
(`TitilliumWeb_BoldItalic`, як заголовки гри); ⚠️ гліфа `›` в MSDF-атласах
немає — шеврон `>`.

Pick / Plate / TurboMatch — **багато раундів за візит** (`newGame()` після
нагороди), тому ціна (`chargeRound()`) знімається на вході І перед кожним
наступним раундом, а не лише на вході, як у скретчі T8.

⚠️ **Стартовий баланс був 10 000, став 100** (`GameState.rbxFlow(10_000L)`
прибрано). Якщо треба лишити — `economy.start_balance` у картці.

Розмітка екранів — 11 змістовних: `home_screen`/HUB, `turbo_match_screen`/REVEAL
(мемо-пари), `pick_screen`/REVEAL (каністри), `plate_screen`/GRID (пазл-таблички),
`boost_screen`/GIFT (нагорода за рекламу), `daily_reward_screen`/DAILY (з
контролера), `converter_screen`/TOOL, персонажі та одяг/CATALOG. Технічні
(Loader, Settings, Onboarding, Selector_1..4) — `null`.

Ключі економіки:

| ключ | тип | дефолт |
|---|---|---|
| `turbo_match_screen` | price / reward | `0` / `20` |
| `plate_screen` | price / reward | `0` / `30` |
| `pick_screen` | price / reward | `0` / `25` (за кожну виграшну каністру, 0–2 шт.) |
| `boost_screen` | reward | `50` |
| `daily_reward` | rewards_list | `[100,200,300,400,500,600,700]` |

Не чіпав: «25 RBX» на картинці виграшної каністри і «free 50 RBX» на Boost —
намальовані в текстурах; якщо сервер змінить суму, напис розійдеться.

На девайсі (Redmi 10C, Android 13): `MODEL OUR … atk=yes`, провайдери
`custom_google`. Черга старту з **«Later»**: без системного запиту → таб
(`pl=app_open`) → онбординг (3 + 4 селектори) → Home, баланс 100. Повторний опт-ін
на наступному запуску: «GET 100» → системний → +100. Зелена кнопка: 3 ×
`pl=interstitial`. Дейлі +100. Turbo Match +20, новий раунд стартує. Pick: 0 / 25 /
25 / 0 → +50. Boost +50 (`pl=interstitial`). Plate +30. Повторний запуск — одразу
Home (`onboarding.done=true`), баланс збережено. Диплінк `funtols://reward?h=test`
відкриває апку. Краш на оновленні: збереження `{"rbx":10000,…}` читається, серія
«день 2» лишилась. Release: обидва класи в mapping не перейменовані (APK
непідписаний).
