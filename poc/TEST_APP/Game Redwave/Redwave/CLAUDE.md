# Redwave: Music Downloader

Нова апка категорії **Tools** (не гра парку RBX). Завантажувач музики з **легальних**
посилань + безкоштовний CC-каталог, плеєр з еквалайзером, різак рингтонів і **власний
головний екран** (роль HOME), який ловить скопійовані аудіолінки.

Стек як в іграх UAPP: **Kotlin + LibGDX 1.14.2 + scene2d**, UI малюється в GL, Android-бік —
тонкий шар сервісів. Еталона немає (правила теки — `poc/TEST_APP/CLAUDE.md`), **але з 12.10.2026
(рішення VELDAN) тут бізнес-логіка UAPP**: `businesModule` + `adsmodule` з еталона
`june/Game T1-1/T1-integrated` (розділ нижче). Firebase — **лише FCM** (проєкт `redwave-original`,
`app/google-services.json`); Remote Config і Firebase Analytics прибрано. **TikTok SDK — як в еталоні**
(ключі з картки, рішення VELDAN 13.10.2026). Meta SDK і білінгу немає.

## Бізнес-логіка UAPP (рішення VELDAN 12.10.2026)

`businesModule/` — копія еталона **без змін** (лише префікс пакета): конфіг з нашого сервера
(`/appconfig`, атрибуція `atk`, гейтвей лендінгів), події, `Wallet`/`Econ` (монет у UI немає), опт-ін на
пуші, локальні пуші (WorkManager), FCM-токен, повернення з веба. **Правки від серверної команди
(`DEV_TASKS`) бекпортувати і сюди** — модуль копіюється, а не шариться.

`adsmodule/` — з еталона з відмінностями (кожна — коментарем у шапці файлу):
- `AdManager` — лише інтерстішал (front / back / interstitial); банера й нативки немає (рішення VELDAN), без Glide;
- `AppOpenManager` — повернення з фону повідомляє апка (`onReturnToApp`), а не `ProcessLifecycleOwner`:
  процес «повертається» на кожне «Додому», а там свої плейсменти;
- `UserDetector` — як в еталоні; `AnalyticsManager` — лише TikTok-провайдер (без Firebase Analytics);
  `AdPref` — prefs `redwave_ads_prefs`.

**TikTok** (`services/tiktok/TikTokManager`, як `initTikTok` еталона): app_id / secret — блок `tiktok` картки
сервера; ініціалізація після кожної відповіді `/appconfig` (або з кешу AdPref), OneTime. Подія
`UNLOCK_ACHIEVEMENT` — на кожен показ `AppScreen` (як MainScreen еталона). Немає блоку → лог
`TikTok config missing/invalid — skip init`. R8: `-keep com.tiktok.**` + `-dontwarn com.android.billingclient.**`
(SDK має необов'язковий трекінг покупок, білінгу в Redwave немає).

Конфіг — `android/ServerConfig.kt` (як `initAds` еталона): `Biz.startSession` → Install Referrer (сирий, поки
`atk` не видано) → `Biz.fetchConfig` → `AdConfig.remoteConfig` + кеш `AdPref`. Оновлення — на кожному `onResume`
(release — не частіше 5 хв). Сервер не відповів — кеш минулого запуску; фолбека на Firebase немає.
Перший запуск онлайн без кешу — Splash чекає `/appconfig` до 5 с.

### Режим — з картки сервера (`core/config/AppFlags.kt` + `AppFlagsTest`)

| Провайдер `app_open` для типу юзера | Вкладка на «Додому» / «Недавні» | Роль HOME | Uninstall з лаунчера |
|---|---|---|---|
| `custom` / `custom_google` / `custom_tiktok` / `custom_facebook` | так (лише з роллю й онлайн) | обов'язкова, без «Maybe later» | ні |
| `na` / `admob` / конфігу немає | ні | «Maybe later» є | так |

**Немає інтернету → другий рядок завжди** (рішення VELDAN 07.10.2026, перенесено з Remote Config).
Послаблення діє одразу, посилення (роль стала обов'язковою) — на наступному вході.

### Плейсменти (ключ доходу `<app>-<pl>`, після релізу НЕ перейменовувати)

| pl | Коли | Де |
|---|---|---|
| `click_home` | «Додому» з іншої апки; холодний старт як HOME (ребут, оновлення) | `MainActivity.onResume` |
| `clear_history` | повернулись на лаунчер без HOME-інтенту; **холодний старт після очищення «Недавніх»** | те саме + `core/launcher/HomePlacement` |
| `app_open` | черга старту (Splash → застосунок; або перший вхід у застосунок, якщо процес стартував як лаунчер / з онбордингу); повернення в застосунок | `AppOpenManager` |
| `front` | перемикання вкладки, відкриття плеєра (частота — `front_navigation` у картці) | `GDXGame.adFront` |
| `back` | «Назад» з плеєра в застосунок (`back_navigation`) | `GDXGame.adBack` |
| `interstitial` | кожні 2 завантажені треки (`showInterstitial`) | `GDXGame.maybeInterstitial` |
| `optin_return` | повернення у вкладку після дозволу на пуші з лендінга | `MainActivity` (`Biz.onWebOptIn`) |

`admob` у картці — AdMob App Open / Interstitial замість вкладки (блоки з `ad_units.admob`). **Лише в
застосунку** (`AppScreen`, `PlayerScreen`): на лаунчері, онбордингу, екрані-вимозі — ні.

⚠️ **MIUI «Очистити все» в «Недавніх» вбиває процес Redwave** (`OneKeyClean`) разом із home-таском — живого
`onResume` немає, `onDestroy` приходить не завжди (заміряно на Redmi 12.10.2026). Тому `clear_history` при
холодному старті визначаємо за причиною смерті попереднього процесу (`ApplicationExitInfo`, Android 11+):
`OTHER` + «OneKeyClean» / `USER_REQUESTED` + «remove task». Нестача пам'яті / оновлення / ребут → `click_home`.

⚠️ Відкриття застосунку через трамплін (іконка, тап по пушу, диплінк) скидає вкладку «Додому», а вкладка
не відкривається, якщо інтент уже відкрив свою (`pl=push`) — інакше поверх пуша відкривався `clear_history`
(заміряно 13.10.2026).

⚠️ «Нас сховала наша вкладка / реклама» фіксується в `onStop` (`stoppedByOwnAd`), а не в `onResume`:
стартова вкладка відкривається між `onStart` і `onResume` (одразу після системного дозволу), і перевірка в
`onResume` давала другу вкладку `clear_history` після закриття першої.

### Пуші й диплінк

Опт-ін — черга старту UAPP (`Biz.runStartupFlow`): свій діалог **без монет** («Turn on notifications…»,
тексти — `App.kt`, `Biz.Config`) → системний запит → `app_open`. Тап по пушу й диплінк ідуть через
`LauncherTrampoline` (він прокидає `data` і `extras`). Схема диплінка **`downloader`** (`downloader://reward`,
`downloader://optin`) — віддати серверній команді (`deeplink_scheme`). `Biz.onWebReward` — без UI (монет немає).
Локальні пуші перевіряються тільки на release; кампанії в картці зараз `comeback` (24 год) і `comeback_3d`.

Privacy Policy — `../privacy-policy/` (Cloudflare, проєкт `redwave-privacy`; оновлення — «New deployment» з текою `public`).
⚠️ Після переходу на наш сервер і FCM політика й Data safety застаріли (там Remote Config) — оновити перед релізом.

Спілкування й коментарі в коді — **українською**. UI апки — **англійською** (маркет США).

## Перед роботою — прочитати

Ця тека (`Game Redwave/Redwave/`) — **сама апка** (Android-проєкт). Документи й матеріали до
неї лежать рівнем вище, у `Game Redwave/` (як `Game T35/` → `Mindora Self Test/` + `market/`):

1. `../PORTING.md` — архітектура, Android-бік, маніфест, **фази з чекпоінтами на девайсі**, пастки.
2. `../SCREENS.md` — кожен екран і компонент: розкладка, актори, стани, анімації.
3. Прототип — еталон вигляду й поведінки: https://claude.ai/artifact/1WQgAQ5KYh7M3EHovXgLMC
   (копія `../prototype/index.html`, відкривається в браузері). Розбіжність прототип ↔ апка —
   баг апки, якщо в `PORTING.md` не записано інше.
4. На старті першої сесії поставити VELDAN питання з розділу **«❓ Відкриті рішення»**
   у `../PORTING.md` (пакет, джерело каталогу тощо). Пакет після публікації не змінити.

## Що вже готово поруч (`Game Redwave/`)

| Тека | Що | Куди в апці |
|---|---|---|
| `../prototype/` | HTML-прототип v2 + фото CC0 (`img/SOURCES.md`) | — (довідка) |
| `../port-kit/` | Чиста Kotlin-логіка + 31 JVM-тест: розбір лінків, перевірка «чи це аудіо», буфер, RSS, каталог, стан, рингтон, EQ, спектр, тексти UI, кольори, розміри | `src/main/kotlin/**` → `app/src/main/java/<пакет>/…` (поміняти `com.redwave.downloader`, якщо пакет інший) |
| `../assets/fonts/` | MSDF-шрифти у форматі T35 + TTF + скрипти | `msdf/*` → `app/src/main/assets/font/msdf/` |
| `../assets/all/icons/` | Іконки 96 px, білі (тонуються кольором актора) | джерело атласу `all` |
| `../assets/brand/` | Лого, адаптивна іконка по mipmap-папках, SVG | `android-res/*` → `app/src/main/res/` |
| `../assets/textures/` | Великі текстури: фото онбордингу, банери Discover, фон лаунчера, світіння | `app/src/main/assets/textures/` |
| `../market/` | Google Play: 6 скрінів 1080×1920, іконка 512, feature 1024×500 | Play Console |
| `../test-media/` | Наші згенеровані треки (CC0), RSS, `catalog.json`, `links.md` — **прямі лінки для тестів на девайсі** | хоститься з GitHub raw |

Android-проєкт — **тут, у `Game Redwave/Redwave/`** (поруч із цим `CLAUDE.md`), як
`Game T35/Mindora Self Test/`. У `Game Redwave/` — лише матеріали до апки (маркет, ассети,
прототип, port-kit, тест-треки, документи), коду апки там немає.

## Збірка і запуск

```bash
export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"   # adb не в PATH
cd "/Users/admin/Apps/UAPP/poc/TEST_APP/Game Redwave/Redwave"
sh ./gradlew assembleDebug                                       # gradlew часто без біта виконання
adb install -r -t app/build/outputs/apk/debug/app-debug.apk      # MIUI: INSTALL_FAILED_USER_RESTRICTED → просто повторити
adb shell monkey -p com.redwave.downloader -c android.intent.category.LAUNCHER 1
adb logcat -s REDTERET:V AndroidRuntime:E                       # тег логів — util/util.kt
adb exec-out screencap -p > /tmp/shot.png                        # скріншот — відкрити й подивитись
```

Пакет — **`com.redwave.downloader`** (рішення VELDAN 07.10.2026). Атлас іконок — `sh ./gradlew :app:packAtlas`
(gdx-tools з `../assets/all/icons`, рішення VELDAN 07.10.2026; GUI TexturePacker не потрібен).

Debug-хуки (лише debug-збірка):
```bash
# лаунчер без зміни дефолтного HOME + імітація лінка з буфера (adb на Android 13 не пише в буфер)
adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME -n com.redwave.downloader/.MainActivity --es redwave.debug_clip "https://…/blue-room.m4a"
# Settings → Debug → AD_MODE: «наша реклама» поверх картки (вкладки на «Додому», роль обов'язкова, без Uninstall),
# перемикання перезапускає апку; вимкнути можна й з екрана-вимоги (кнопка AD_MODE OFF зліва вгорі)
# режим як при іншому провайдері app_open (na / admob / custom_google; "" — прибрати); реклама лишається з картки.
# У Settings → Debug видно джерело конфігу, atk, тип юзера, режим і лічильники реклами
adb shell am start -n com.redwave.downloader/.MainActivity --es redwave.debug_app_open na
# диплінк з лендінга (невалідний токен = тиша, це норма)
adb shell am start -a android.intent.action.VIEW -d "downloader://reward?h=test"
# «повернення в апку» без «Недавніх»: піти в Settings і повернути Redwave наперед
adb shell am start -a android.settings.SETTINGS && sleep 3 && adb shell am start -n com.redwave.downloader/.MainActivity
# лог бізнес-логіки: MODEL OUR … atk=yes, openAd URL … pl=…, cold HOME: last exit …, localpush: scheduled …
adb logcat -s REDTERET:V
# локальний пуш без очікування 24 год: вийти в іншу апку (задачі плануються в onStop), знайти job і запустити
adb shell dumpsys jobscheduler | grep -o "JOB #u0a[0-9]*/[0-9]*" | head      # u0a… — uid Redwave
adb shell cmd jobscheduler run -f com.redwave.downloader <job_id>
```

**Release (R8, повний режим) перевіряти на девайсі**, не лише збирати: debug без мініфікації не ловить
вирізаних конструкторів. У `mapping.txt` не перейменовані `adsmodule.RemoteConfigModel` (Gson) і
`businesModule.push.LocalPush$PushWorker` (WorkManager) — правила в `proguard-rules.pro`. Заміряно 08.10.2026 — AdMob тягнув WorkManager 2.7.0 / Room 2.2.5, у release краш на
старті (`Failed to create an instance of androidx.work.impl.WorkDatabase`); лікується явним
`androidx.work:work-runtime` свіжої версії в `app/build.gradle.kts`. Перевірка без ключа апки: зібрати
`assembleRelease`, `zipalign` + `apksigner` debug-ключем (`~/.android/debug.keystore`, пароль `android`) → `adb install -r`.

**Одна MainActivity на процес.** Іконка застосунку йде через `LauncherTrampoline`: на Android 10+
HOME-інтент кладе Activity в окремий home-таск, і дві MainActivity = два GDX в одному процесі
(статики `Gdx.*`, кеш шейдерів → білі прямокутники). Не повертати LAUNCHER-фільтр на MainActivity.

Тести чистої логіки: `sh ./gradlew :app:testDebugUnitTest` (копії тестів port-kit) або `cd ../port-kit && gradle test`.

**Змінив UI, анімацію чи шейдер — зроби скріншот з девайса і подивись на нього.** Порівняй
з тим самим екраном прототипу. Не описувати зміну словами, не здогадуватись.

## Конвенції (як у T35)

- Навігація **тільки** через `NavigationManager` (`navigate()` / `back()`), прямий `setScreen()` заборонено.
- Актори з префіксом `A`, файл = клас. Базові класи — `utils/advanced/` (`Advanced*`).
- Координати — **world-юніти**, база 376 × 815. Розміри з прототипу: `px(16f)` (1 px = 1.074 wu),
  готові — в `Dimens` (`../port-kit/.../game/utils/Constants.kt`).
- Кольори — лише `GameColor`, тексти — лише `Copy`.
- Текст — MSDF (`AMsdfLabel`). **Archivo — тільки фіксовані англійські заголовки й цифри**
  (кирилиці немає). Усе, що прийшло від користувача (ID3-теги, імена файлів, RSS) — **Onest**.
- Усе `Disposable` — у `disposableSet` екрана. Текстури обкладинок (із файлів) — теж.
- Шейдери GLSL ES 2.0: `varying`/`attribute`, без `#version`, `precision mediump float`.
- Android ↔ GDX: виклики з GL-потоку в Android — через `PlatformBridge`; колбеки назад — `runGDX { }`.
- Секції класу — сепаратор `// ----…`, підсекції `// ── Назва ───`, «чому так» — KDoc.

## Git

Локально Claude **комітить, але не пушить**: у локальному git немає логіна до GitHub.
Пушить VELDAN через GitHub Desktop; після коміту — нагадати йому. Хмарна сесія (claude.ai/code)
може сама відкрити PR і змержити в `main` (пам'ять `uapp-cloud-merge-permission`).
Перед роботою — `git pull`, щоб мати свіжі `../test-media/`, `../assets/`, документи.

## Не чіпати руками

- `../assets/fonts/msdf/*` — генерується `../assets/fonts/tools/build_all.sh`.
- Атлас `app/src/main/assets/atlas/all.*` — генерує `:app:packAtlas` (gdx-tools). CLI TexturePacker
  (codeandweb) не використовувати — без ліцензії й з водяними знаками (пам'ять `mindora-texturepacker-cli`).
- `app/libs/**/*.so` — розпаковує таск `copyAndroidNatives`.
