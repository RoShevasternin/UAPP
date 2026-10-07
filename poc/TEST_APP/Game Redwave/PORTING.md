# Redwave — план переносу прототипу в Android LibGDX

Підготовлено 06.10.2026 у хмарній сесії. Позначки: **✔** — перевірено або однозначно,
**◌** — гіпотеза, перевірити на девайсі, **❓** — рішення за VELDAN.

---

## 0. Коротко

- Одна Activity з LibGDX-фрагментом, як у T35, але **без StartActivity-трамплінa** і без реклами.
  Вона ж — головний екран (HOME), застосунок (LAUNCHER) і ціль «Поділитися» (SEND).
- GDX-частина: 5 екранів (`Splash`, `Onboarding`, `AppScreen` з 4 вкладками, `Player`, `Launcher`)
  + шторки (див. `SCREENS.md`), знає Android лише через
  `PlatformBridge` (`port-kit/.../game/platform/PlatformBridge.kt`).
- Android-частина: `AndroidBridge` + `PlaybackService` (Media3) + `DownloadRepo` (DownloadManager)
  + `ClipWatcher` + `AppsRepo` (LauncherApps) + `RingtoneMaker` (Media3 Transformer).
- Уся чиста логіка вже написана й протестована: `port-kit/` (31 тест).
- Робимо **фазами**; кінець кожної фази — збірка, девайс, скріншот, порівняння з прототипом.

---

## 1. ❓ Відкриті рішення — спитати на старті першої сесії

| # | Питання | Пропозиція |
|---|---|---|
| 1 | **applicationId / пакет** (після публікації не змінити) | `com.redwave.downloader` — так названо пакет у port-kit. Інший → перейменувати пакет при копіюванні |
| 2 | Назва в Play | `Redwave: Music Downloader` (25/30). ◌ Перевірити, що вільна |
| 3 | Джерело каталогу Discover у релізі | Поки `test-media/catalog.json` з GitHub raw. Для релізу — свій статичний JSON на нашому хостингу з треками CC0/CC BY (див. §6.10). Jamendo API для комерційних апок платний |
| 4 | Реклама/аналітика | Немає, доки VELDAN не скаже (правило `../CLAUDE.md`). Місця під банер у розкладці не резервуємо |
| 5 | Одна чи дві Activity для HOME і застосунку | Почати з **однієї** (як HomeLauncher-PoC). Якщо на девайсі заважатиме поведінка «Недавніх» — розділити (§6.1) |
| 6 | «100% legal» у промо й на скріні 02 | Пом'якшити до «Free & legal music» або «Creative Commons music» перед релізом |
| 7 | Мінімальна версія Android | minSdk 24, target/compile 37 — як T35 |

---

## 2. Архітектура

```
MainActivity (AppCompatActivity, AndroidFragmentApplication.Callbacks)
 ├─ intent-filters: MAIN+HOME+DEFAULT · MAIN+LAUNCHER · SEND text/plain
 ├─ ClipWatcher      onWindowFocusChanged(true) → ClipGate → events.onClipboardLink
 ├─ AndroidBridge    implements PlatformBridge
 │    ├─ DownloadRepo     DownloadManager + ACTION_DOWNLOAD_COMPLETE + опитування прогресу
 │    ├─ NetProbe         OkHttp: GET Range 0-63 → AudioSniffer
 │    ├─ MediaController  ↔ PlaybackService (Media3 MediaSessionService, ExoPlayer, Equalizer, Tee→Spectrum)
 │    ├─ CoverStore       MediaMetadataRetriever → filesDir/covers/<id>.jpg
 │    ├─ WaveformPeaks    MediaExtractor + MediaCodec → filesDir/peaks/<id>.bin
 │    ├─ RingtoneMaker    Media3 Transformer (clip + fade) → MediaStore → RingtoneManager
 │    └─ AppsRepo         LauncherApps (+Callback), іконки → PNG
 └─ GDXFragment → GDXGame (AdvancedGame, implements PlatformEvents)
        ├─ менеджери з T35: Navigation, Sprite, Msdf, Sound (кліки), DataStore
        ├─ AppModel        AppState (бібліотека, завантаження, EQ, ClipGate-стан) → DataStore JSON
        ├─ контролери: LibraryController, DownloadController, PlayerController, DiscoverController, LauncherController
        └─ екрани: Splash · Onboarding · AppScreen(Home|Discover|Library|Ringtone) · Player · Launcher(+All apps)
```

**Чому без StartActivity (на відміну від T35).** У T35 `StartActivity` стартує `MainActivity`
і закривається. Для лаунчера це означало б: кожне натискання «Додому» → трамплін → друга
Activity → мерехтіння і зайві 100–300 мс. HOME-фільтр має стояти на самій GL-Activity. ✔

**Чому Activity + Fragment, а не AndroidApplication.** Як у T35: `AndroidFragmentApplication`
дає звичайну AppCompat-Activity для системних діалогів (роль, дозволи, шер). ✔

**Навігація всередині однієї Activity.**
- `onNewIntent(CATEGORY_HOME)` → `events.onHomePressed()` → `navigate(LauncherScreen)` з порожнім бекстеком.
- Іконка Redwave в доку → `navigate(AppScreen)`, «Назад» з кореня AppScreen → `LauncherScreen`.
- Якщо роль HOME не наша — `LauncherScreen` недоступний, «Назад» з кореня → `moveTaskToBack(true)`.

---

## 3. Що взяти з T35 і що змінити

Донор: `agust/Game T35/Mindora Self Test/app/src/main/java/com/selftest/mindora/`.

| Копіювати як є | Нотатки |
|---|---|
| `game/utils/advanced/*` | **Змінити `AdvancedScreen`**: прибрати `AdSizeManager`, `adBannerUI`, `adBottomUI`, `collectBannerHeightFlow()`; `rootConstraintLayout` = `worldHeight - safeStatusBarUI - safeNavBarUI`. `statusBarHeight/navBarHeight` брати з `PlatformBridge` |
| `game/utils/{Util,SizeScaler,ShaderClock,ShapeDrawerUtil,NumberFormatter}.kt`, `actor/*`, `global/*`, `stateMachine/*`, `overlay/*`, `vfx/*` | без змін |
| `game/utils/font/msdf/**` | без змін; шрифти в `MsdfManager` — з `assets/fonts/README.md` |
| `game/manager/{NavigationManager,SpriteManager,SoundManager,AudioManager,ParticleEffectManager,DataStoreManager}.kt` + `manager/util/*` | **`NavigationManager`**: прибрати `gdxGame.activity.onFrontNavigation()/onBackNavigation()` (реклама) і `noAdScreens`; `exit()` → `bridge.moveToBack()` замість `Gdx.app.exit()` — лаунчер не можна закривати. `MusicManager/MusicUtil` не потрібні (музика — через Media3) |
| `game/actors/{AScrollPane,ATmpGroup}.kt`, `button/base/*`, `checkbox/base/*`, `label/*`, `layout/**`, `ui/ARoundRect.kt`, `vfx/*`, `popup/APopup.kt` (як основа шторки), `particleEffect/*` | база для компонентів §5 |
| `assets/shader/**` | `defaultVS`, `ui/roundRectFS`, `blur/gaussianBlurFS`, `mask/maskFS`, `msdf/*`; `orbit/`, `background/starField` — не треба |
| `GDXFragment.kt` | приймає `MainActivity`, `useImmersiveMode = false` |

| Не брати | Чому |
|---|---|
| `adsmodule/**`, `config/**`, `services/**` (TikTok, analytics), `App.kt` з Firebase/MobileAds | правило теки: без еталона й реклами |
| `content/**`, `controller/**`, `model/PlayerModel`, `state/**`, екрани й панелі Mindora | інший продукт |
| `StartActivity` | див. §2 |
| `utils/Constants.kt`, `utils/GameColor.kt` | замінюються файлами з `port-kit/.../game/utils/` |

З `port-kit/src/main/kotlin/com/redwave/downloader/` копіюється все: `core/**` (без змін),
`game/utils/{Constants,GameColor}.kt`, `game/platform/PlatformBridge.kt`. Тести — в `app/src/test/`.

---

## 4. Gradle

Як у T35 (`build.gradle.kts` кореневий + `app/`, таск `copyAndroidNatives`, JVM 11), плюс:

```kotlin
// LibGDX — як у T35: gdx-backend-android, gdx-platform natives, gdx-freetype (не обов'язково), shapedrawer
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:<як у T35>")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:<остання>")
implementation("androidx.datastore:datastore-preferences:<як у T35>")

// Відтворення, сесія, обрізка рингтону — одна версія Media3 на всі артефакти
val media3 = "<остання стабільна>"
implementation("androidx.media3:media3-exoplayer:$media3")
implementation("androidx.media3:media3-session:$media3")
implementation("androidx.media3:media3-transformer:$media3")
implementation("androidx.media3:media3-common:$media3")

implementation("com.squareup.okhttp3:okhttp:<остання 4.x/5.x>")   // probe + fetchText
```

Без Firebase / google-services / play-services-ads / tiktok / billing.

---

## 5. Маніфест (чернетка)

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-feature android:glEsVersion="0x00020000" android:required="true" />

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <!-- DownloadManager у публічну Music/ на API 24–28 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
    <!-- Media3 PlaybackService -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
    <!-- Рингтон: окремий екран налаштувань, НЕ рантайм-діалог -->
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <!-- POST_NOTIFICATIONS свідомо НЕМАЄ: сповіщення медіасесії звільнені від нього на 13+,
         сповіщення DownloadManager показує системний провайдер. ◌ перевірити на девайсі -->

    <queries>
        <!-- сітка застосунків лаунчера: без QUERY_ALL_PACKAGES -->
        <intent>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent>
        <!-- відкрити посилання (Privacy policy, атрибуція CC BY) -->
        <intent>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="https" />
        </intent>
    </queries>

    <application
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:label="@string/app_name"
        android:appCategory="audio"
        android:theme="@style/Theme.Redwave"
        android:allowBackup="true">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTask"
            android:stateNotNeeded="true"
            android:clearTaskOnLaunch="true"
            android:resumeWhilePausing="true"
            android:screenOrientation="portrait"
            android:configChanges="orientation|screenSize|screenLayout|keyboardHidden|uiMode"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.HOME" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.SEND" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:mimeType="text/plain" />
            </intent-filter>
        </activity>

        <service
            android:name=".playback.PlaybackService"
            android:exported="true"
            android:foregroundServiceType="mediaPlayback">
            <intent-filter>
                <action android:name="androidx.media3.session.MediaSessionService" />
            </intent-filter>
        </service>
    </application>
</manifest>
```

`excludeFromRecents` свідомо **не** ставимо: апка — ще й звичайний застосунок. ◌ Подивитись,
як система показує HOME-таск у «Недавніх»; якщо погано — §6.1, варіант двох Activity.

---

## 6. Android-бік: як робити і де пастки

### 6.1 Роль HOME
Код є в `poc/concept/HomeLauncher-PoC` (`isDefaultHomeApp()`, `requestDefaultHome()`, фолбек
`ACTION_HOME_SETTINGS`) — переносимо. Пам'ять `home-launcher-poc` — деталі тестів.
- ✔ HOME-натискання приходять в `onNewIntent` (singleTask); перший запуск як HOME — в `onCreate`.
- ✔ **«Назад» на LauncherScreen нічого не робить.** `AdvancedScreen.keyDown(BACK)` у T35 при
  порожньому бекстеку кличе `exit()` → для лаунчера це закриття головного екрана. Перевизначити.
- ◌ Процес лаунчера система вбиває частіше, ніж звичайних апок → після холодного старту стан
  має повністю підніматись з `AppState` (DataStore). LibGDX: `setPreserveEGLContextOnPause`.
- ◌ Дві Activity (HOME окремо з `excludeFromRecents`) — лише якщо одна погано поводиться в
  «Недавніх». Мінус: два GL-контексти, подвійне завантаження атласів.
- Шпалери системи не показуємо (свій фон `launcher_bg.jpg`); прозорий GL-шар — не в v1.

### 6.2 Буфер обміну → картка на Home (`core/clip/ClipGate.kt`)
- ✔ Читати в `onWindowFocusChanged(hasFocus = true)`, не в `onResume` (Android 10+ поверне null).
- ✔ Спершу `getPrimaryClipDescription()` (тосту немає) → `ClipGate.shouldRead(timestamp, hasText)`
  → лише тоді `getPrimaryClip()` (Android 12+ покаже тост «Redwave pasted from your clipboard»).
- ✔ Стан `ClipGate` (timestamp, закриті лінки) зберігати в `AppState` — інакше після рестарту
  картка вискочить на старий кліп.
- ✔ Кнопка Paste у застосунку теж викликає тост (своя кнопка = програмне читання).
  Системне «Вставити» з меню поля — без тосту.
- ◌ Android 12+ `ClipDescription.getConfidenceScore(TextClassifier.TYPE_URL)` — можна не
  читати текст, якщо там не URL. Класифікація асинхронна, може не встигнути.
- ◌ MIUI/HyperOS: власні обмеження доступу до буфера. Заміряти на Redmi.

### 6.3 Завантаження
Потік: `LinkResolver.resolve()` → для `DirectAudio`/`Dropbox` можна одразу, для
`WebPage`/`GoogleDrive`/`OneDrive` — `bridge.probe()` → `AudioSniffer.decide()` →
`DownloadManager.Request(finalUrl)`:
```kotlin
.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "Redwave/$fileName")
.setTitle(title).setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
.setMimeType(mime).addRequestHeader("User-Agent", "Redwave/1.0 (Android)")
```
- ✔ Частина хостингів (S3, CDN, файлообмінники) віддає аудіо як `application/octet-stream` —
  тому `AudioSniffer` дивиться магію перших байтів (ID3/fLaC/OggS/RIFF/ftyp).
  raw.githubusercontent.com (заміряно 06.10.2026): `.mp3`→`audio/mpeg`, `.m4a`→`audio/mp4`,
  `.flac`→`audio/flac`, `.ogg`→`audio/ogg`, `.wav`→`audio/wav`; `.mp4` → octet-stream;
  **`.rss`/`.json`/`.xml` → `text/plain`** — тож `fetchText` не має вимагати XML/JSON-тип.
  Лінк `github.com/…/blob/…mp3` — HTML-сторінка (відхиляється); `?raw=true` веде на файл
  через два редиректи. Повна таблиця — `test-media/links.md`.
- ✔ OkHttp з gzip для текстових файлів повертає довжину -1 — розмір брати з тіла, не з заголовка.
- ✔ Google Drive: великі файли → HTML-підтвердження → повтор на `GoogleDrive.confirmedUrl(id)`. ◌ формат Google змінює.
- ✔ Dropbox: `dl=1` (робить `LinkResolver`). OneDrive `1drv.ms`: probe іде за редиректами,
  у `DownloadManager` віддаємо `finalUrl` + `download=1`. ◌
- ✔ API 29+: `DownloadManager` пише в `Music/` без дозволів; API 24–28 — `WRITE_EXTERNAL_STORAGE` (maxSdk 28).
- ◌ Однакове ім'я файлу: DownloadManager дописує суфікс чи падає — перевірити; надійніше
  самим робити унікальне ім'я (`name (2).mp3`).
- ✔ `ACTION_DOWNLOAD_COMPLETE` шле системний провайдер (інший uid) → на API 33+ реєструвати
  ресивер у рантаймі з `RECEIVER_EXPORTED`. Прогрес — опитування `DownloadManager.query()` раз
  на 500 мс, лише поки видно екран з чергою.
- Після завершення: `MediaMetadataRetriever` → title/artist/duration/embedded picture →
  `filesDir/covers/<id>.jpg` → `Track` в `AppState`. Немає тегів → `LinkResolver.titleFromFileName()`.
- ✔ Бібліотека = наш індекс, не MediaStore (після перевстановлення без `READ_MEDIA_AUDIO`
  свої старі файли апка не бачить). Індекс зберігає `localUri`.

### 6.4 Відтворення
- `PlaybackService : MediaSessionService` з ExoPlayer; UI через `MediaController`.
- ✔ `setAudioAttributes(…, handleAudioFocus = true)`, `setHandleAudioBecomingNoisy(true)`
  (вийняли навушники — пауза).
- ✔ **Не `Gdx.audio`**: LibGDX ставить музику на паузу разом з GL-поверхнею.
- Сповіщення з керуванням Media3 робить сам. Таймер сну — `Handler` у сервісі → `pause()`.
- Черга: Library → усі пісні; Podcast → лише подкасти (як у прототипі).

### 6.5 Еквалайзер (`core/eq/EqCurve.kt`)
- `Equalizer(0, player.audioSessionId)`; слухати `onAudioSessionIdChanged`, пересоздавати.
- ✔ Кількість смуг/частоти/діапазон — від прошивки: UI завжди 5 повзунків, на пристрій
  `EqCurve.levelsForDevice()` (центри в **мГц**, рівні в **мБ**). Тест на типових 5 смугах — є.
- ◌ Деякі прошивки (MIUI) мають свій системний EQ — подивитись конфлікти.

### 6.6 Візуалізатор (`core/viz/SpectrumAnalyzer.kt`)
- ✔ **Не `audiofx.Visualizer`** — він вимагає `RECORD_AUDIO`. Замість нього:
  `DefaultRenderersFactory.buildAudioSink()` → `DefaultAudioSink.Builder(ctx).setAudioProcessors(arrayOf(TeeAudioProcessor(sink)))`,
  де `sink.handleBuffer()` → `analyzer.pushPcm16()`.
- Малювання v1: 48 закруглених стовпчиків через `ShapeDrawer` + віддзеркалення з альфою 0.16.
- v2 (шейдер): смуги передавати **текстурою 48×1**, не `uniform float[48]` — у GLSL ES 2.0
  динамічна індексація uniform-масивів у фрагментному шейдері не гарантована (Mali). ✔
- «Пульс» обкладинки: `scale = 1 + bass * 0.035`, світіння `glow_radial.png` з альфою від `bass`.

### 6.7 Рингтон (`core/ringtone/RingtoneCut.kt`)
- Хвиля: `WaveformPeaks` (MediaExtractor + MediaCodec → PCM → 64 піки) у фоні, кеш на диску.
- Обрізка: Media3 **Transformer**, `MediaItem.ClippingConfiguration(startMs, endMs)`,
  fade — свій `AudioProcessor` (лінійний гейн на перших/останніх `RingtoneCut.fadeMs()`),
  вихід AAC у `.m4a`.
- Запис: `MediaStore.Audio.Media` з `RELATIVE_PATH` = `Ringtones/` | `Alarms/` | `Notifications/`
  і `IS_RINGTONE`/`IS_ALARM`/`IS_NOTIFICATION = 1`.
- Встановлення: `Settings.System.canWrite()` → ні → `ACTION_MANAGE_WRITE_SETTINGS` (`package:` URI)
  → після повернення `RingtoneManager.setActualDefaultRingtoneUri(TYPE_…)`.
- ◌ MIUI може відкрити загальний список замість сторінки апки (як було з накладанням — пам'ять
  `home-launcher-poc`). Дати підказку в UI.

### 6.8 Поділитися
- З апки: `ACTION_SEND` з `content://` з MediaStore + `FLAG_GRANT_READ_URI_PERMISSION`.
- В апку: `ACTION_SEND text/plain` → `events.onSharedText()` → `AppScreen(HOME)` з вставленим лінком.

### 6.9 Подкасти
- `bridge.fetchText(feedUrl)` → `RssParser.parse()` (DOM, DOCTYPE вимкнено — XXE). Епізоди
  без `<enclosure>` пропускаються. Тестовий фід — `test-media/indie-hour.rss`.

### 6.10 Каталог Discover
- Dev: `test-media/catalog.json` (raw GitHub) → `CatalogCodec.decode()`.
- ❓ Реліз: свій JSON на нашому хостингу. Правила відбору:
  - лише **CC0 / CC BY / CC BY-SA**. **NC (NonCommercial) — ні**: якщо колись буде реклама, апка комерційна;
  - **CC BY вимагає атрибуції**: автор + назва + ліцензія + посилання — показати в деталях
    треку і зберегти в `Track.license`/`sourceUrl`;
  - Jamendo API — для комерційних апок потрібна платна ліцензія; Internet Archive — ліцензії
    різні по кожному item, фільтрувати.

### 6.11 Лаунчер
- `LauncherApps.getActivityList(null, user)` для кожного `UserHandle` з `getProfiles()`;
  `LauncherApps.Callback` → `events.onAppsChanged()`.
- Іконки: `Drawable` → `Bitmap` → PNG-байти → `Pixmap` → `Texture` на GL-потоці; кеш у пам'яті
  на сесію. Завантажувати після першого кадру LauncherScreen, не блокуючи його.
- Запуск: `LauncherApps.startMainActivity(component, user, null, null)`.

---

## 7. Ресурси

| Що | Звідки | Куди / як |
|---|---|---|
| Іконки UI (37 шт., білі 96 px) | `assets/all/icons/` | атлас `all` (TexturePacker GUI). Колір — `actor.color` |
| Лого, адаптивна іконка | `assets/brand/android-res/*` | `app/src/main/res/` |
| Фото онбордингу | `assets/textures/onboarding/onboarding_{1..3}.jpg` | `EnumTextureGroup("textures/onboarding","onboarding",3)` |
| Банери Discover, промо | `assets/textures/discover/feat_*.jpg` | `EnumTexture` |
| Фон лаунчера (вже розмитий) | `assets/textures/launcher/launcher_bg.jpg` | `EnumTexture` |
| Світіння, фейд | `assets/textures/fx/glow_radial.png`, `fade_vertical.png` | тонувати кольором |
| Шрифти | `assets/fonts/msdf/` | `app/src/main/assets/font/msdf/` + `MsdfManager` |
| Обкладинки треків | із файлів (теги) / `catalog.json` | runtime `Texture`, `disposableSet` |

**Шейдери, які додаємо до T35-набору:**
- `ui/roundImageFS.glsl` — текстура з закругленими кутами (обкладинки всюди). Альфа = SDF
  закругленого прямокутника, радіус у world-юнітах (як `roundRectFS`).
- `ui/gradientRoundRectFS.glsl` — червона кнопка/чип: `roundRectFS` + лінійний градієнт
  `GameColor.gradTop → gradBot` під 135°.
- `msdf/msdf_gradient.glsl` + `GradientFillEffect` — друге слово заголовків (red → peach),
  як `.hello h1 em` у прототипі. v1 можна суцільним `redHi`.
- `viz/barsFS.glsl` — лише у v2 візуалізатора (§6.6).

Атлас без ліцензії TexturePacker: `gdx-tools` → `TexturePacker.process(settings, in, out, "all")`
з окремого JVM-таска. Рішення — за VELDAN (зараз він пакує в GUI).

---

## 8. Фази (кожна закінчується на девайсі)

| Фаза | Що зробити | Готово, коли на девайсі… |
|---|---|---|
| **Ф0. Каркас** | Проєкт `Redwave/` з T35-каркаса (§3), пакет, іконки, шрифти, `GameColor`, `Constants`, `PlatformBridge` + порожній `AndroidBridge`. Один тестовий екран з `AMsdfLabel` трьома шрифтами | іконка Redwave в лаунчері; екран з текстом Archivo/Onest/Mono; кирилиця в Onest видна |
| **Ф1. Сплеш + онбординг** | `SplashScreen` (лого, смужки), `OnboardingScreen` (3 слайди, свайп, Skip, крапки), `AppState.onboarded` у DataStore | скріни 3 слайдів ≈ прототип; після рестарту онбординг не показується |
| **Ф2. Головна + завантаження** | `AppScreen` + `HomeTab` + `ATabBar`: поле, Paste, Download, помилки, черга; `NetProbe`, `DownloadRepo`, `MediaMetadataRetriever` | лінки з `test-media/links.md`: mp3/m4a/flac/ogg/wav качаються в `Music/Redwave/`; YouTube → помилка; сторінка → «not an audio file»; назва без тегів — з імені файлу |
| **Ф3. Бібліотека** | `LibraryTab`: пошук, сегменти, рядки, місце; індекс у `AppState` | рестарт апки — бібліотека на місці; обкладинки з тегів видно |
| **Ф4. Плеєр** | `PlaybackService`, міні-плеєр, `PlayerScreen` (розмитий фон `ABlur`, перемотка, shuffle/repeat, таймер сну), сповіщення | грає у фоні з вимкненим екраном; кнопки в шторці працюють; навушники вийняли — пауза |
| **Ф5. EQ + візуалізатор** | шторка EQ (пресети, 5 повзунків), `Equalizer`, Tee → `SpectrumAnalyzer`, стовпчики | Bass Boost чутно; стовпчики рухаються в такт; жодного запиту дозволу на мікрофон |
| **Ф6. Discover + подкасти** | `catalog.json`, настрої, підбірки, кільце прогресу, шторка RSS | треки каталогу качаються з кільцем; фід `indie-hour.rss` показує 2 епізоди |
| **Ф7. Рингтон** | хвиля з піків, ручки, fade, Preview, Transformer, MediaStore, WRITE_SETTINGS | виставлений рингтон звучить при дзвінку; фрагмент 3…40 с; fade чутно |
| **Ф8. HOME** | роль, `LauncherScreen` (годинник, картка буфера, віджети, док, усі застосунки), `ClipWatcher`, `SEND` | скопіювати лінк у Telegram → «Додому» → картка → Download; повторне «Додому» без тосту; «Назад» на лаунчері не закриває його |
| **Ф9. Полірування** | анімації переходів, градієнти, світіння, звуки кліків, порожні стани, відсутність мережі, скріни для маркету з девайса | порівняння всіх 8 екранів з прототипом поруч; чекліст §9 зелений |

---

## 9. Чекліст на девайсі (з тест-плану прототипу + Android)

| # | Сценарій | Очікування |
|---|---|---|
| 1 | Пройти онбординг, «Maybe later» | Home; роль не запитувалась повторно |
| 2 | Paste → Download прямого mp3 | черга з % → тост «Downloaded · …» → у Recently added |
| 3 | YouTube / Spotify лінк | червоний блок «… doesn’t allow downloads», нічого не качається |
| 4 | Сторінка github.com | «Checking the link…» → «This page isn’t an audio file» |
| 5 | Трек з Discover | кільце прогресу → галочка → грає |
| 6 | Плеєр: пресет Bass Boost, рух повзунка | чутно; `Equalizer.setBandLevel` у логах |
| 7 | Рингтон: 0:24, fade, Save | рингтон у системі, звучить на дзвінок |
| 8 | Роль HOME → копія лінка → «Додому» | картка; тост Android 12+ лише раз на кліп |
| 9 | «Додому» вдруге з тим самим кліпом | картки й тосту немає |
| 10 | Flight mode | бібліотека й плеєр працюють; завантаження → «No connection» |
| 11 | Перевстановити апку | ◌ поведінка старих файлів у Music/Redwave (індекс зник) — вирішити: пересканувати теку? |
| 12 | Убити процес (adb shell am kill) на лаунчері | «Додому» піднімає лаунчер зі станом |
| 13 | Робочий профіль | застосунки профілю в сітці з бейджем |
| 14 | Темна/світла системна тема | апка завжди темна (свідомо) |

---

## 10. Google Play — що не забути

- ✔ Без YouTube/Spotify/SoundCloud і т.д. — блок у `LinkResolver` (порушення IP-політики = бан).
- ✔ Роль HOME — частина основної функції (картка буфера, віджети). З 07.10.2026 вона **обов'язкова** (рішення VELDAN):
  без ролі апка показує лише екран-вимогу. ◌ Ризик рев'ю Play — примусова зміна лаунчера; в описі пояснити, навіщо.
- ◌ Декларація foreground service `mediaPlayback` у Play Console (target 34+).
- ◌ Data safety: буфер читається лише локально, нікуди не відправляється — так і написати.
  Privacy policy потрібна.
- ◌ `WRITE_SETTINGS` — спецдоступ; пояснити в описі («set ringtones»).
- ❓ «100% legal» на промо й скріні 02 — пом'якшити.
- Скріни: `market/screenshot_01…06.png` (з прототипу) — після Ф9 замінити справжніми з девайса.

---

## 11. Що є в port-kit (короткий довідник)

| Файл | Призначення | Тести |
|---|---|---|
| `core/link/LinkResolver.kt` | тип лінка: Direct / Drive / Dropbox / OneDrive / RSS / WebPage / Blocked / Invalid; `titleFromFileName` | 11 |
| `core/link/AudioSniffer.kt` | Content-Type + магія байтів + Content-Disposition → «це аудіо?» | 6 |
| `core/clip/ClipGate.kt` | коли читати буфер, дедуп, закриті картки | 3 |
| `core/feed/RssParser.kt` | RSS 2.0 + itunes, захист від XXE | 2 |
| `core/model/Models.kt`, `Json.kt` | Track, DownloadItem, Catalog, PodcastFeed, AppState + кодеки | 1 |
| `core/ringtone/RingtoneCut.kt` | межі 3–40 с, ручки, fade, імена файлів | 3 |
| `core/eq/EqCurve.kt` | пресети, 5 смуг UI → смуги пристрою | 2 |
| `core/viz/SpectrumAnalyzer.kt` | PCM → FFT → 48 смуг зі спадом | 3 |
| `core/copy/Copy.kt` | усі тексти UI (англ.) | — |
| `game/utils/Constants.kt`, `GameColor.kt` | розміри у wu (`px()`), палітра | — |
| `game/platform/PlatformBridge.kt` | контракт GDX ↔ Android | компілюється |

Запуск: `cd port-kit && gradle test`.
