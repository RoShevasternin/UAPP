# Driftglass: Live Home Screen

Нова апка (не гра парку RBX): **живі шейдерні шпалери + власний головний екран** (роль HOME).
Прототип — https://claude.ai/artifact/XXRnR3cEUCuAS5h3Mb7QkW (копія `../prototype/index.html`).
Правила теки — `poc/TEST_APP/CLAUDE.md`. Перенесено зі стеку Redwave (LibGDX 1.14.2 + scene2d, MSDF, `PlatformBridge`).
**З 15.10.2026 (рішення VELDAN) тут бізнес-логіка UAPP, як у Redwave і Framefall**: `businesModule` + `adsmodule`
з еталона (розділ нижче). Firebase — **лише FCM** (проєкт `driftglass-original`, `app/google-services.json`);
Remote Config прибрано. TikTok SDK — як в еталоні. Analytics, Meta SDK і білінгу немає.
Маркет-кіт — `../market/` (`LISTING.md`, `README.md`), Privacy Policy — `../privacy-policy/`.

## Бізнес-логіка UAPP (рішення VELDAN 15.10.2026, як Redwave / Framefall)

Модулі скопійовано з **Framefall** (він — найсвіжіша копія каркаса Redwave), лише префікс пакета:
`businesModule/` — копія еталона **без змін**: конфіг з нашого сервера (`/appconfig`), атрибуція `atk`, гейтвей
лендінгів, події, `Wallet`/`Econ` (монет у UI немає), опт-ін на пуші, локальні пуші (WorkManager), FCM-токен,
повернення з веба. **Правки серверної команди (`DEV_TASKS`) бекпортувати і сюди.**
`adsmodule/` — урізаний, як у Redwave: `AdManager` лише інтерстішал (front / back / interstitial), `AppOpenManager` —
повернення в апку повідомляє сама апка (`onReturnToApp`), `AnalyticsManager` — лише TikTok; prefs `driftglass_ads_prefs`.

**Картка на сервері — `home`** (останній сегмент пакета `com.driftglass.home`; VELDAN 15.10.2026: картку вже додано й
опубліковано, пакет не міняємо). Диплінк — **`home://reward`**, **`home://optin`** (`deeplink_scheme` у картці).

Конфіг — `android/ServerConfig.kt` (копія Framefall): `Biz.startSession` → Install Referrer (поки `atk` не видано) →
`Biz.fetchConfig` → `AdConfig.remoteConfig` + кеш `AdPref`; оновлення на кожному `onResume` (release — не частіше 5 хв);
Splash на першому запуску онлайн чекає `/appconfig` до 5 с.

### Режим — з картки (`core/config/AppFlags.kt` + `AppFlagsTest`)

| Провайдер `app_open` для типу юзера | Вкладка на «Додому» / «Недавні» | Роль HOME | Перемикач «Use as Home screen» у Settings | Uninstall з лаунчера |
|---|---|---|---|---|
| `custom` / `custom_google` / `custom_tiktok` / `custom_facebook` | так (лише з роллю й онлайн) | обов'язкова, без «Maybe later»; на поясненні ролі замість «Switch back… Settings» — `ex3Req` | **немає** (VELDAN, 16.10.2026); у debug — у блоці DEBUG | ні |
| `na` / `admob` / конфігу немає / офлайн | ні | «Maybe later» є | є | так |

### Плейсменти (ключ доходу `<app>-<pl>`, після релізу НЕ перейменовувати)

| pl | Коли | Де |
|---|---|---|
| `app_open` | «Додому» з іншої апки й очищення «Недавніх» (лише режим «наша реклама», роль і онлайн) | `MainActivity.onResume`, `AppFlags.PL_HOME_TAB` |
| `app_open` | черга старту (Splash → застосунок, або перший вхід у застосунок з лаунчера / онбордингу); повернення в застосунок | `AppOpenManager` |
| `push` | тап по пушу (`gate_pl` з картки) | `LocalPush.handleOpen` |
| `front` | зміна вкладки, відкриття Preview / Studio | `GDXGame.adFront` |
| `back` | «Назад» з Preview / Studio у застосунок | `GDXGame.adBack` |
| `interstitial` | кожні 2 дії зі шпалерами (Apply / Save у Studio / кадр на систему) | `GDXGame.maybeInterstitial` |
| `optin_return` | повернення у вкладку після дозволу на пуші з лендінга | `MainActivity` (`Biz.onWebOptIn`) |

Реклама **лише в застосунку** (вкладки, Preview, Studio): на лаунчері, онбордингу, екрані-вимозі — ні.
Події: `screen_view` — вкладки (`discover_tab`, `my_glass_tab`, `settings_tab`), `preview`, `studio`; TikTok
`UNLOCK_ACHIEVEMENT` — кожен показ `AppScreen`. Опт-ін — без монет (тексти в `App.kt`).
Пастки — ті самі, що в Redwave (`Game Redwave/Redwave/CLAUDE.md`): MIUI «Очистити все» вбиває процес → вкладка на
холодному старті як HOME; «наша реклама» фіксується в `onStop` (`stoppedByOwnAd`); відкриття через трамплін скидає вкладку «Додому».

```bash
# режим як при іншому провайдері app_open (na / admob / custom_google; "" — прибрати); реклама лишається з картки
adb shell am start -n com.driftglass.home/.MainActivity --es driftglass.debug_app_open na
# відповідь /appconfig коротко: atk, config по типах юзера («→» — діє для цього), tiktok, кампанії пушів
adb logcat -d -v raw -s driftglass:V | grep -A10 "^──── /appconfig"
# диплінк з лендінга (невалідний токен = тиша, це норма)
adb shell am start -a android.intent.action.VIEW -d "home://reward?h=test"
```

## StoreDemo — чисті скріни для Google Play (запит VELDAN 12.10.2026)

Debug-режим `android/StoreDemo.kt`: лаунчер показує 30 вигаданих апок із загальними назвами й нашими іконками
(Material Symbols, Apache 2.0; PNG лише в `app/src/debug/assets/demo_icons`), годинник 9:41. Як перезняти кадри — `../market/README.md`.

```bash
adb shell am start -n com.driftglass.home/.MainActivity --ez driftglass.store_demo true    # false — вимкнути
```

Спілкування й коментарі в коді — **українською**. UI апки — **лише англійською** (рішення VELDAN 09.10.2026:
`GDXGame.applyLanguage` → EN; переклади uk/ru лишились у `core/i18n/Strings.kt` на потім, перемикача мови немає).

## AD_MODE (debug, як у Redwave)

Settings → Debug → AD_MODE — «наша реклама» поверх картки (вкладки на «Додому», роль обов'язкова, без Uninstall),
перемикання перезапускає апку; на екрані-вимозі — кнопка AD_MODE OFF зліва вгорі. Логіка вкладки — `MainActivity`
(1:1 з Framefall). У Settings → Debug видно джерело конфігу, atk, тип юзера, режим і лічильники реклами.

## Лаунчер: одна сторінка з авто-папками + усі апки свайпом угору (рішення VELDAN 09.10.2026)

Розкладку MIUI скопіювати не можна (приватна база лаунчера, `READ_SETTINGS` — signature|privileged),
тож папки збираємо самі: `core/logic/HomeLayout.kt` (+ `HomeLayoutTest`) за пакетом,
`ApplicationInfo.category`, «системна» і префіксом виробника (`com.miui.`, `com.xiaomi.` … — MIUI ставить
Калькулятор / Нотатки / Погоду без FLAG_SYSTEM) → **Google, Tools, Games, Social, Media** (папка ≥ 2 апок),
далі решта апок за абеткою; Settings / Галерея / Безпека лишаються на виду. Перша клітинка — сама Driftglass.

- **Головний екран — одна сторінка, гортання вбік НЕМАЄ** (VELDAN: «не треба гортати по всьому екрану»):
  скільки рядів влазить між карткою шпалер і доком (`rowsFor`), решта не показується.
- **Свайп угору** (з порожнього місця, з іконки чи смужки) → Drawer: ті самі папки + усі апки, вертикальний скрол;
  з пошуком — плоский список збігів. Поки Drawer відкритий, годинник і сітка сховані (видно лише шпалери).
  Свайп униз, коли список нагорі, / «Назад» / «Додому» — закрити.
- Папка — скляне вікно 4 колонки (`openFolder`), відкривається і з головного екрана, і з Drawer.
- Багато ігор (казино, 1win) не вказують категорію — лежать окремо, не в «Games».

## Збірка і запуск

```bash
export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"
cd "/Users/admin/Apps/UAPP/poc/TEST_APP/Game Driftglass/Driftglass"
sh ./gradlew assembleDebug :app:testDebugUnitTest
adb install -r -t app/build/outputs/apk/debug/app-debug.apk      # MIUI: INSTALL_FAILED_USER_RESTRICTED — тапнути «Встановити» на телефоні
adb shell monkey -p com.driftglass.home -c android.intent.category.LAUNCHER 1
adb logcat -s driftglass:V AndroidRuntime:E
```

Перевстановлення на MIUI скидає «запам'ятований» лаунчер — після `adb install` «Додому» показує вибір головного додатка.
Атлас іконок — `sh ./gradlew :app:packAtlas` (з `../assets/icons`). Шрифти — `../assets/fonts/tools/make_msdf.sh`.

**Одна MainActivity на процес** — іконка йде через `LauncherTrampoline` (як Redwave). Не повертати LAUNCHER-фільтр на MainActivity.

**Змінив UI — скріншот з девайса і подивитись.** Пастка, що вже стріляла (іконки лаунчера, палітри Studio, смужка
«усі апки», затемнення карток): усередині `Image().apply { … }` / `ARect(…).apply { … }` `width`/`height` — це розміри
самого актора (0), а не клітинки; брати розмір клітинки в змінну до `apply`.

**Release (R8) перевіряти на девайсі:** `assembleRelease` → `zipalign` + `apksigner` debug-ключем (`~/.android/debug.keystore`,
пароль `android`) → `adb install -r`.

## Конвенції

- Навігація лише через `NavigationManager`; екрани — `game/screens/*Screen.kt`, вкладки — `screens/tabs/`.
- Координати — world-юніти, база 376 × 815, розміри прототипу — `px()` (348 → 376).
- Кольори — `GameColor`, тексти — `L` (`core/i18n/Strings.kt`).
- Шпалери — GLSL ES 1.00 у `assets/shader/wallpaper/`, рендер — `game/wallpaper/WallpaperRenderer.kt`.
- Android ↔ GDX — лише `PlatformBridge` / `PlatformEvents`; колбеки назад — `runGDX { }`.

## Git

Локально Claude **комітить, але не пушить**; пушить VELDAN через GitHub Desktop — після коміту нагадати.
