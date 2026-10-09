# Driftglass: Live Home Screen

Нова апка (не гра парку RBX): **живі шейдерні шпалери + власний головний екран** (роль HOME).
Прототип — https://claude.ai/artifact/XXRnR3cEUCuAS5h3Mb7QkW (копія `../prototype/index.html`).
Правила теки — `poc/TEST_APP/CLAUDE.md`: еталона немає, **ні Firebase, ні реклами SDK, ні сервера**, доки
VELDAN не скаже. Перенесено зі стеку Redwave (LibGDX 1.14.2 + scene2d, MSDF, `PlatformBridge`).

Спілкування й коментарі в коді — **українською**. UI апки — **лише англійською** (рішення VELDAN 09.10.2026:
`GDXGame.applyLanguage` → EN; переклади uk/ru лишились у `core/i18n/Strings.kt` на потім, перемикача мови немає).

## AD_MODE (як у Redwave, рішення VELDAN 09.10.2026)

Прапорці — `core/config/AppFlags.kt` (+ `AppFlagsTest`), поля й JSON-імена як у Redwave `RemoteFlags`,
джерело — `android/FlagsSource.kt`. Remote Config **немає**: release = `AppFlags.DEFAULT`, debug — підміна.

| Поле | true | false (DEFAULT) |
|---|---|---|
| `enabled_url_ad` + `url` | Custom Tab з `url` на «Додому» з іншої апки, після «Недавніх» / «Очистити все», після вимкненого екрана (лише з роллю HOME і онлайн) | нічого |
| `home_required` | пояснення ролі без «Maybe later»; без ролі — екран-вимога на кожному вході й resume | «Maybe later» є |
| `is_uninstall` | довге натискання на іконку в лаунчері → App info / Uninstall | довге натискання нічого не робить |

AD_MODE = `enabled_url_ad` + `https://google.com` + `home_required` + `!is_uninstall`. Логіка вкладки —
`MainActivity` (1:1 з Redwave): `onStop` від «іншої апки» вмикає, `onResume` відкриває; наша вкладка й
наші системні екрани (`internalNavigation`) не рахуються; холодний старт як HOME теж відкриває.
Перевірено на Redmi 09.10.2026: Settings → «Додому» → вкладка; закрити вкладку → лаунчер без повтору;
«Недавні» → «Очистити все» → вкладка; довге натискання — нічого. **Не перевірено:** екран-вимога після зняття ролі.

```bash
# Settings → Debug → AD_MODE (перемикання перезапускає апку); на екрані-вимозі — кнопка AD_MODE OFF зліва вгорі
# підміна з adb ("" — прибрати):
adb shell am start -n com.driftglass.home/.MainActivity --es driftglass.debug_flags '{"enabled_url_ad":true,"url":"https://google.com","home_required":true,"is_uninstall":false}'
```

## Лаунчер: сітка зі сторінками й авто-папками (запит VELDAN 09.10.2026)

Розкладку MIUI скопіювати не можна (приватна база лаунчера, `READ_SETTINGS` — signature|privileged),
тож папки збираємо самі: `core/logic/HomeLayout.kt` (+ `HomeLayoutTest`) за пакетом,
`ApplicationInfo.category` і «системна чи ні» → **Google, Tools, Games, Social, Media** (папка ≥ 2 апок),
далі решта апок за абеткою; Settings / Галерея лишаються на виду. Перша клітинка — сама Driftglass.
Рядів — скільки влазить між карткою шпалер і доком (`rowsFor`), сторінки — свайп уліво/вправо (з іконки теж),
крапки над смужкою, «Додому» на іншій сторінці → перша. Папка — скляне вікно 4 колонки (`openFolder`).
Багато ігор (казино, 1win) не вказують категорію — лежать окремо, не в «Games».

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

**Змінив UI — скріншот з девайса і подивитись.** Пастка, що вже стріляла: усередині `Image().apply { … }`
`width`/`height` — це розміри Image (0), а не клітинки; брати розмір клітинки в змінну до `apply`.

## Конвенції

- Навігація лише через `NavigationManager`; екрани — `game/screens/*Screen.kt`, вкладки — `screens/tabs/`.
- Координати — world-юніти, база 376 × 815, розміри прототипу — `px()` (348 → 376).
- Кольори — `GameColor`, тексти — `L` (`core/i18n/Strings.kt`).
- Шпалери — GLSL ES 1.00 у `assets/shader/wallpaper/`, рендер — `game/wallpaper/WallpaperRenderer.kt`.
- Android ↔ GDX — лише `PlatformBridge` / `PlatformEvents`; колбеки назад — `runGDX { }`.

## Git

Локально Claude **комітить, але не пушить**; пушить VELDAN через GitHub Desktop — після коміту нагадати.
