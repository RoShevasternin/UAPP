# Redwave: Music Downloader

Нова апка категорії **Tools** (не гра парку RBX). Завантажувач музики з **легальних**
посилань + безкоштовний CC-каталог, плеєр з еквалайзером, різак рингтонів і **власний
головний екран** (роль HOME), який ловить скопійовані аудіолінки.

Стек як в іграх UAPP: **Kotlin + LibGDX 1.14.2 + scene2d**, UI малюється в GL, Android-бік —
тонкий шар сервісів. **Еталона немає** (правила теки — `poc/TEST_APP/CLAUDE.md`): ні `businesModule`,
ні `adsmodule`, ні TikTok/білінгу, доки VELDAN окремо не скаже. **Firebase — лише Remote Config**
(рішення VELDAN 07.10.2026, проєкт `redwave-original`, `app/google-services.json`); Analytics немає.
**Реклама — AdMob напряму** (App Open + Interstitial, розділ нижче), зараз лише тестові блоки Google;
вмикає Remote Config `is_enable_admob`.

## Реклама AdMob (рішення VELDAN 07.10.2026)

Вмикач — `is_enable_admob` у Remote Config (рішення VELDAN 08.10.2026): `false`, офлайн або без відповіді Firebase —
SDK не ініціалізується, нічого не вантажиться й не показується, треки не рахуються. Частота — `core/ads/AdPolicy.kt`
(+ `AdPolicyTest`), SDK — `android/AdsManager.kt`, де показувати — `GDXGame`. `AD_ID` — явно в маніфесті.

| Що | Коли | Обмеження |
|---|---|---|
| App Open | відкриття апки: холодний старт з іконки (Splash чекає рекламу до 3,5 с), іконка Redwave на нашому лаунчері | не частіше раз на 3 хв від будь-якої повноекранної |
| App Open | повернення в апку з іншої апки / «Недавніх» / вимкненого екрана, якщо не було ≤ 1 год | те саме; > 1 год — не показуємо |
| Interstitial | кожні 2 завантажені треки: через 1,5 с після тосту «Downloaded» або на наступній зміні вкладки | не поверх шторки й поля вводу, не у фоні; ≥ 60 с після попередньої |

**Лише в застосунку** (`AppScreen`, `PlayerScreen`): на лаунчері (роль HOME), онбордингу, екрані-вимозі й першому запуску —
ні. «Додому» → лаунчер — не повернення. Наші вкладка, системні екрани й сама реклама (`AdsManager.isShowing`) у `onStop`
не рахуються виходом. Стан (останній показ, лічильник треків) — SharedPreferences `redwave_ads`, переживає перезапуск.
**Перед релізом:** свій App ID у маніфесті + свої блоки в `AdsManager`, згода UMP (ЄЕЗ), реклама в Privacy Policy і Data safety.

## Remote Config — параметр `redwave_config` (JSON)

Розбір — `core/config/RemoteFlags.kt` (+ `RemoteFlagsTest`), Firebase — `android/RemoteFlagsSource.kt`.

| Поле | true | false | Офлайн / без відповіді Firebase |
|---|---|---|---|
| `enabled` + `url` | сторінка `url` у Custom Tab на «Додому» з іншої апки й після «Недавніх» (лише з роллю HOME і онлайн) | нічого не відкривається | false |
| `home_required` | 3-й слайд лише з «Set as Home screen»; без ролі — екран-вимога на кожному вході | ще й «Maybe later», апка працює без ролі | false |
| `is_uninstall` | довге натискання на іконку в лаунчері → App info / Uninstall | довге натискання нічого не робить | true |
| `is_enable_admob` | реклама AdMob: App Open + Interstitial (розділ вище) | AdMob немає взагалі | false |
| `privacy_url` (необов.) | адреса Privacy Policy в Settings | — | `https://redwave-privacy.oyutetijep68.workers.dev/privacy` |

**Немає інтернету → значення з колонки «Без відповіді» завжди**, навіть якщо в кеші Firebase інші
(рішення VELDAN 07.10.2026; `RemoteFlagsSource.effective`). Перший запуск онлайн: Splash чекає Firebase до 3 с. Далі — кеш Firebase одразу, `fetchAndActivate` на кожному
`onResume` (release — не частіше 5 хв) + real-time listener. Послаблення прапорців діє одразу, посилення
(`home_required` → true) — на наступному вході. Privacy Policy — `../privacy-policy/` (Cloudflare, проєкт `redwave-privacy`; оновлення — «New deployment» з текою `public`).

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
adb logcat -s redwave:V AndroidRuntime:E
adb exec-out screencap -p > /tmp/shot.png                        # скріншот — відкрити й подивитись
```

Пакет — **`com.redwave.downloader`** (рішення VELDAN 07.10.2026). Атлас іконок — `sh ./gradlew :app:packAtlas`
(gdx-tools з `../assets/all/icons`, рішення VELDAN 07.10.2026; GUI TexturePacker не потрібен).

Debug-хуки (лише debug-збірка):
```bash
# лаунчер без зміни дефолтного HOME + імітація лінка з буфера (adb на Android 13 не пише в буфер)
adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME -n com.redwave.downloader/.MainActivity --es redwave.debug_clip "https://…/blue-room.m4a"
# Settings → Debug → AD_MODE: «чорний» режим поверх Remote Config (реклама на Home + AdMob, роль обов'язкова, без Uninstall),
# перемикання перезапускає апку; вимкнути можна й з екрана-вимоги (кнопка AD_MODE OFF зліва вгорі)
# підміна Remote Config (живе до очищення: --es redwave.debug_flags ""); у Settings → Debug видно джерело прапорців
adb shell am start -n com.redwave.downloader/.MainActivity --es redwave.debug_flags '{"enabled":true,"url":"https://google.com","home_required":false,"is_uninstall":true,"is_enable_admob":true}'
# реклама: забути останній показ і лічильник треків (не чекати 3 хв); стан видно в Settings → Debug (ads(test) · dl 1/2 · …)
adb shell am start -n com.redwave.downloader/.MainActivity --ez redwave.debug_ads_reset true
# «повернення в апку» без «Недавніх»: піти в Settings і повернути Redwave наперед
adb shell am start -a android.settings.SETTINGS && sleep 3 && adb shell am start -n com.redwave.downloader/.MainActivity
```

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
