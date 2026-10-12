# Driftglass: Live Home Screen

Нова апка (не гра парку RBX): **живі шейдерні шпалери + власний головний екран** (роль HOME).
Прототип — https://claude.ai/artifact/XXRnR3cEUCuAS5h3Mb7QkW (копія `../prototype/index.html`).
Правила теки — `poc/TEST_APP/CLAUDE.md`: еталона немає. Перенесено зі стеку Redwave (LibGDX 1.14.2 + scene2d, MSDF,
`PlatformBridge`). **Firebase — лише Remote Config, реклама — AdMob напряму, як у Redwave** (рішення VELDAN 12.10.2026);
Analytics немає. Маркет-кіт — `../market/` (`LISTING.md`, `README.md`), Privacy Policy — `../privacy-policy/`.

## Remote Config + AdMob (як у Redwave, рішення VELDAN 12.10.2026)

Параметр **`driftglass_config`** (JSON), значення VELDAN — `../firebase/driftglass_config.json`, як створити — `../FIREBASE.md`.
Проєкт Firebase — `driftglass-original` (VELDAN, 12.10.2026), `app/google-services.json` у git. Без цього файлу плагін
google-services не підключається (умова в `app/build.gradle.kts`), а `RemoteFlagsSource` бере `AppFlags.DEFAULT` — реклами немає.

- `android/RemoteFlagsSource.kt` — порт Redwave 1:1: офлайн → DEFAULT, Splash чекає Firebase до 3 с на першому запуску,
  `fetchAndActivate` на кожному `onResume`, real-time listener.
- `android/AdsManager.kt` + `core/ads/AdPolicy.kt` (+ `AdPolicyTest`) — порт Redwave, тестові блоки Google.
  App Open — відкриття апки (Splash чекає до 3,5 с; іконка Driftglass на нашому лаунчері) і повернення ≤ 1 год,
  не частіше раз на 3 хв; Interstitial — кожні 2 дії зі шпалерами (Apply / Save у Studio / кадр на систему),
  у паузі (через 1,5 с, зміна вкладки), ≥ 60 с після попередньої. **Лише в застосунку** (вкладки, Preview, Studio) —
  на лаунчері, онбордингу й екрані-вимозі ні. Перевірено на Redmi 12.10.2026: App Open з іконки, Interstitial після
  2 збережень у Studio; release (R8) стартує без крашу (WorkManager 2.12 явно, як у Redwave).
- **Перед релізом:** свій App ID у маніфесті + свої блоки в `AdsManager`, згода UMP (ЄЕЗ).

```bash
adb shell am start -n com.driftglass.home/.MainActivity --ez driftglass.debug_ads_reset true   # забути показ і лічильник
```

## StoreDemo — чисті скріни для Google Play (запит VELDAN 12.10.2026)

Debug-режим `android/StoreDemo.kt`: лаунчер показує 30 вигаданих апок із загальними назвами й нашими іконками
(Material Symbols, Apache 2.0; PNG лише в `app/src/debug/assets/demo_icons`), годинник 9:41. Як перезняти кадри — `../market/README.md`.

```bash
adb shell am start -n com.driftglass.home/.MainActivity --ez driftglass.store_demo true    # false — вимкнути
```

Спілкування й коментарі в коді — **українською**. UI апки — **лише англійською** (рішення VELDAN 09.10.2026:
`GDXGame.applyLanguage` → EN; переклади uk/ru лишились у `core/i18n/Strings.kt` на потім, перемикача мови немає).

## AD_MODE (як у Redwave, рішення VELDAN 09.10.2026)

Прапорці — `core/config/AppFlags.kt` (+ `AppFlagsTest`), поля й JSON-імена як у Redwave `RemoteFlags`,
джерело — Remote Config (`android/RemoteFlagsSource.kt`); debug — підміна поверх нього.

| Поле | true | false (DEFAULT) |
|---|---|---|
| `enabled_url_ad` + `url` | Custom Tab з `url` на «Додому» з іншої апки, після «Недавніх» / «Очистити все», після вимкненого екрана (лише з роллю HOME і онлайн) | нічого |
| `home_required` | пояснення ролі без «Maybe later»; без ролі — екран-вимога на кожному вході й resume | «Maybe later» є |
| `is_uninstall` | довге натискання на іконку в лаунчері → App info / Uninstall | довге натискання нічого не робить |

AD_MODE = `enabled_url_ad` + url (з Remote Config, якщо https, інакше google.com) + `home_required` + `!is_uninstall` + AdMob. Логіка вкладки —
`MainActivity` (1:1 з Redwave): `onStop` від «іншої апки» вмикає, `onResume` відкриває; наша вкладка й
наші системні екрани (`internalNavigation`) не рахуються; холодний старт як HOME теж відкриває.
Перевірено на Redmi 09.10.2026: Settings → «Додому» → вкладка; закрити вкладку → лаунчер без повтору;
«Недавні» → «Очистити все» → вкладка; довге натискання — нічого. **Не перевірено:** екран-вимога після зняття ролі.

```bash
# Settings → Debug → AD_MODE (перемикання перезапускає апку); на екрані-вимозі — кнопка AD_MODE OFF зліва вгорі
# підміна з adb ("" — прибрати):
adb shell am start -n com.driftglass.home/.MainActivity --es driftglass.debug_flags '{"enabled_url_ad":true,"url":"https://google.com","home_required":true,"is_uninstall":false}'
```

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
