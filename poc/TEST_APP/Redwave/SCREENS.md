# Redwave — екрани й компоненти для LibGDX

Розміри — **px прототипу** (CSS у `prototype/index.html`); у коді `px(16f)` або готові `Dimens.*`
(`port-kit/.../game/utils/Constants.kt`). Кольори — `GameColor.*`, тексти — `Copy.*`.
Шрифти: **D** = Archivo Expanded ExtraBold, **C** = Archivo Expanded Bold (годинник),
**O** = Onest (400/500/600/700), **M** = JetBrains Mono (400/600).

Звіряти з прототипом: панель «Екрани» праворуч від телефона відкриває кожен екран напряму.

---

## Структура екранів

Прототип має 8 «екранів», у LibGDX їх **5** + оверлеї. Причина: вкладки (Home / Discover /
Library / Ringtone) живуть в одному `AppScreen`. Тоді таббар і міні-плеєр не створюються
заново на кожен тап, а скрол кожної вкладки зберігається. Через `NavigationManager` ходимо
лише між великими екранами.

| Екран | Що всередині | Звідки приходять |
|---|---|---|
| `SplashScreen` | лого + анімовані смужки; вантажить атласи, шрифти, `AppState` | старт |
| `OnboardingScreen` | 3 слайди | Splash, якщо `!onboarded` |
| `AppScreen(tab)` | `HomeTab`, `DiscoverTab`, `LibraryTab`, `RingtoneTab` (усі `AdvancedGroup`) + `AMiniPlayer` + `ATabBar` | Splash, Onboarding, док лаунчера, «Назад» з Player |
| `PlayerScreen` | повноекранний плеєр | тап по треку / міні-плеєру / віджету лаунчера |
| `LauncherScreen` | годинник, картка буфера, віджети, док + оверлей «All apps» | HOME-інтент, «Назад» з кореня AppScreen (коли роль наша) |

Оверлеї (`ASheet`, затемнення `GameColor.black_55`): `EqSheet`, `FeedSheet`, `TrackMenuSheet`.
Діалог ролі HOME — **системний** (RoleManager); у прототипі він лише імітований.

---

## Компоненти (нові актори, база — T35)

| Актор | Основа T35 | Опис / розміри з прототипу |
|---|---|---|
| `ACover` | `VfxImage` + новий `roundImageFS` | обкладинка з радіусом (12 у списках, 22 у плеєрі). Без арту — `ph_glyph` на радіальному градієнті `hsl(352…)`. Текстуру з файлу — у `disposableSet` |
| `AButtonRed` | `AButtonBase` + `gradientRoundRect` | висота 46, радіус 15, градієнт 135° `gradTop→gradBot`, тінь-світіння `glow_radial` під кнопкою; іконка 18 + O 700/15 білим. Натиск: scale 0.97 |
| `AButtonGhost` | `AButtonBase` + `ARoundRect` | рамка `white_16`, фон `white_4`, O 700/14.5 |
| `AIconButton` | `AButtonBase` | коло 38, рамка `line_white_7`, іконка 20 |
| `APlayButton` | `AButtonBase` | коло 68, градієнт, іконка 28 (play/pause fill) |
| `AChip` | `ARoundRect` + label | пігулка; варіанти: `RED` (фон `red_14`, текст `pink_FF8A98`, O 700/10.5), `GLASS` (`white_16`), `MOOD` (тоглиться: вимкнений — рамка, увімкнений — градієнт) |
| `AInputField` | ◌ див. нижче | поле 46, фон `#0C0708`, рамка `line_white_7`, радіус 15; іконка лінка ліворуч, `Paste` праворуч |
| `ASegmented` | `ARoundRect` × N | фон `card_170F12`, вибраний — `card2_21161A` + рамка `red` 30 % |
| `AToggle` | `ARoundRect` | 34×20, кружок 14; увімкнений — `red_FF2E4D` |
| `AProgressBar` | `ARoundRect` × 2 | висота 5, заповнення градієнтом `red → coral` |
| `AProgressRing` | `ShapeDrawer.arc` | коло 38, товщина 3, старт зверху, за годинниковою |
| `AEqBars` | 4 `ARoundRect` | індикатор «грає»: 3×(20…100 %) по висоті 16, `ping-pong` 0.9 с, зсуви фаз |
| `ATrackRow` | `AButtonBase` | висота ~62: `ACover` 48 + назва O 600/14 + рядок O 400/12 `artist · 3:24 · MP3` (формат M 600/10.5 `apricot`) + праворуч `⋮` або `AEqBars` |
| `AMiniPlayer` | група | 56 заввишки, радіус 16, градієнт `#3D0D18→#1F1317`, обкладинка 40, кнопка 36 біла; лінія прогресу 2 px знизу |
| `ATabBar` | група | 4 кнопки (іконка 20 + O 700/10.5); активна — біла з червоною іконкою |
| `ASheet` | `APopup` / `AConstraintLayout` | знизу, радіус 26 зверху, «ручка» 38×4; відкриття — slide 0.3 с, закриття — свайп вниз або тап по затемненню |
| `AToast` | група | пігулка внизу над таббаром (у прототипі — 112 px від низу), 2.4 с, fade |
| `AWaveform` | `ShapeDrawer` | 64 стовпчики, вибрані — `red`, інші — `white` 17 %; ручки: лінія 2 + коло r 7 (біле, обводка `red` 3) |
| `AVisualizer` | `ShapeDrawer` (v1) | 48 стовпчиків, градієнт `vizTop → red → redDeep`, віддзеркалення 38 % висоти з альфою 0.16, висота 58 |
| `AClipCard` | група | картка буфера на лаунчері (див. LauncherScreen) |
| `AAppIcon` | `AButtonBase` | 48 з радіусом 15 + підпис O 500/11 з тінню |

**`AInputField` — рішення ◌.** scene2d `TextField` не вміє MSDF. Два шляхи:
1. **Нативне поле поверх GL** (рекомендовано): `EditText` у `FrameLayout` над `GLSurfaceView`;
   позицію бере з `PlatformBridge` (координати актора → px). Перевага: системне меню
   «Вставити» без тосту Android 12+, нормальна клавіатура, вибір тексту.
2. Тап по полю → нативний діалог з `EditText` (як `MainActivity.showInput()` у T35). Простіше,
   але гірший UX.
Для пошуку в Library — той самий механізм.

---

## SplashScreen
- Фон `#070405` + `glow_radial` червоний, 60×38 % екрана, по центру трохи вище середини.
- Лого 96 по центру (тінь-світіння), під ним «Redwave» **D** 36, під ним `MUSIC DOWNLOADER`
  **M** 600/11, трекінг 0.34em, `muted`.
- Внизу (64 від низу) 7 смужок 4×(4…28), `red`, ping-pong 0.9 с із затримкою 110 мс на кожну.
- Анімація входу: лого scale 0.86→1 + alpha за 0.9 с (`cubic(.2,.8,.2,1)` ≈ `Interpolation.pow3Out`).
- Тривалість: поки вантажиться, але не менше 1.2 с. Далі `Onboarding` або `AppScreen(HOME)`.

## OnboardingScreen
- Фото `onboarding_N` згори на 62 % висоти, повільний Ken Burns: scale 1.14→1 за 14 с.
  Знизу фото — фейд у фон (`fade_vertical`, тонований `background`), від 28 до 63 % висоти.
- `Skip` (слайди 1–2): скляна пігулка праворуч зверху (44 від верху з урахуванням статус-бару).
- Плаваюча скляна картка на 33 % висоти, ширина до 290, «пливе» ±6 за 4.5 с:
  1) рядок лінка (**M** 12, іконка link `redHi`) + прогрес-бар, що наповнюється 6→100 % за 3.2 с по колу
     + `MP3 · 4.8 MB` / `Downloading…`;
  2) зелений чип `No Wi‑Fi needed` + `128 tracks` / `2.4 GB saved` (**D** 22) + `AEqBars`;
  3) міні-картка буфера (eyebrow **M**, `Audio link found`, URL, червона кнопка).
  Скло: фон `rgba(22,10,14,.66)` + розмиття — у GL дорого; v1: напівпрозорий фон без blur,
  v2: `ABlurBack` під карткою.
- Низ: `01 / 03` (**M** 600/11.5, `redHi` + `muted`), заголовок **D** 31 у 2 рядки, друге слово
  — градієнт red→peach (v1: `redHi`), опис **O** 14 `muted`, макс. ~31 символ у рядку.
- Слайди 1–2: крапки (активна 24×7 червона, інші 7×7 білі 22 %) + кругла кнопка «→» 60.
  Слайд 3: крапки + `AButtonRed` «Set as Home screen» + `AButtonGhost` «Maybe later».
- Свайп вліво/вправо (поріг 45) гортає. «Назад» — попередній слайд.
- `Set as Home screen` → `bridge.requestDefaultHome {}` → незалежно від відповіді `onboarded = true`
  → `AppScreen(HOME)`.

## AppScreen · HomeTab
Скрол-колонка (`AScrollPane` + вертикальний `AAutoLayout`), бокові 16, проміжок 18, верх 44
(під статус-бар — `safeStatusBarUI`).
1. Шапка: лого 30 + «Redwave» **D** 19; праворуч `AIconButton` дзвіночок з червоною крапкою 7.
2. Привітання `Copy.Home.greeting(hour)` **M** 600/12 uppercase `muted` + заголовок **D** 29
   «What are we / downloading?» (друге слово — градієнт).
3. Картка вставки (радіус 24, фон `card` + червоний відблиск згори, рамка red 25 %):
   `AInputField` + `Paste` → блок помилки (якщо є) → `AButtonRed` «Download» → чипи джерел.
   - Помилки (фон `red_10`, рамка red 35 %, іконка 18): Invalid / Blocked / NotAudio — тексти в `Copy.Errors`.
   - Перевірка WebPage: замість помилки — «Checking the link…» зі спінером до відповіді `probe`.
4. Черга завантажень (лише якщо є): картка `card`, заголовок «Downloading **N**», рядки
   `ACover` 44 + назва + `%` (**M** 600/12 `redHi`) + `AProgressBar` + `4.1 of 5.2 MB · 1.8 MB/s · MP3` (**M** 11).
5. «Recently added» + «See all» → карусель (горизонтальний `AScrollPane`, snap) 6 останніх пісень:
   `ACover` 124 + назва O 600/13 + артист O 11.5.
6. Три плитки: `tracks` / `on device` / `offline` (число **D** 18).
7. Промо Discover: висота 150, фото `feat_vinyl` з затемненням зліва, чип CC, «Free music. 100% legal.» **D** 24
   (❓ формулювання), «Browse Discover ›». Тап → вкладка Discover.

## AppScreen · DiscoverTab
1. «Discover» **D** 30 + чип `Creative Commons` (іконка shield).
2. Підзаголовок O 13.5 `muted`.
3. Настрої — горизонтальні `AChip(MOOD)`: All, Chill, Lo-fi, Workout, Focus, Live, Rock.
4. Підбірки — горизонтальна карусель карток 250×172 (16:11), радіус 22: фото + затемнення
   знизу, чип настрою (скло) зліва зверху, назва **D** 21, підпис O 12, кругла кнопка ▶ 40.
   Тап = вибрати настрій.
5. «Trending this week» + `N tracks`. Рядки: номер **M** 700/13, `ACover` 48, назва, `artist · CC BY`
   (ліцензія O 700 `pink`), праворуч кнопка: ⬇ (рамка червона) → `AProgressRing` → ✓ (зелена, тап — грати).
6. «Podcasts»: картки `ACover` 60 + назва + `Weekly · N episodes` + ›. Тап → `FeedSheet`.

## AppScreen · LibraryTab
1. «Library» **D** 30 + `AIconButton` сортування.
2. Пошук (`AInputField` без Paste), фільтр по назві й артисту; порожньо → `No matches for “…”`.
3. `ASegmented`: `Songs N` / `Podcasts N`.
4. `AButtonRed(sm)` «Shuffle all» + `Recently added` праворуч.
5. Список `ATrackRow` (нові зверху); той, що грає, — назва `redHi` + `AEqBars`.
6. Плашка сховища: `Music/Redwave` + `486 MB · 63.2 GB free` + `AProgressBar` (частка від місця).

## AppScreen · RingtoneTab
1. «Ringtone» **D** 30 + чип `Maker` (ножиці). Підзаголовок.
2. Трек: картка `ACover` 52 + назва/артист/тривалість + `Change` (наступна пісня).
3. Хвиля (картка, радіус 22): `0:00 · drag the handles · 3:46` (**M** 10.5), `AWaveform` 320×110,
   під нею `START / LENGTH / END` (**M** 10 підписи, **D** 18 значення, довжина — `redHi`).
   Драг: найближча ручка, межі `RingtoneCut.drag()` (3…40 с).
4. Два `AToggle`: Fade in / Fade out.
5. `ASegmented` ×3: Ringtone / Alarm / Notification.
6. `Preview` (ghost, ▶/⏸, до 12 с у прототипі; в апці — вся довжина фрагмента) + `Save ringtone` (red).
   Save: `canWriteSettings()` ні → пояснення + `openWriteSettings()`; так → `saveRingtone()` → тост.

## AMiniPlayer + ATabBar (низ AppScreen)
- Міні-плеєр над таббаром лише коли є `now`. Тап → `PlayerScreen`; кнопка → play/pause.
- Таббар: Home / Discover / Library / Ringtone. «Назад» на не-Home вкладці → Home;
  на Home → Launcher (роль наша) або `moveTaskToBack`.

## PlayerScreen
- Фон: обкладинка, розмита (`ABlur`) + затемнення зверху 25 % → 72 % на середині → фон знизу.
- Верх: `AIconButton` ⌄ (закрити), `PLAYING FROM / Library` (**M** 9.5 uppercase + O 700/13), ⋮.
- Обкладинка: ширина min(78 %, 268), радіус 22, тінь. «Пульс»: `scale 1 + bass·0.035`,
  червоне світіння `glow_radial` з альфою `0.25 + bass·0.4`.
- Назва **D** 23 (одна стрічка, обрізка «…») + артист O 13.5 `muted`; праворуч ♥ (активне — червоне).
- `AVisualizer` 58.
- Перемотка: бар 5 + кругла ручка 13 з ореолом; час `1:23` / `-2:01` (**M** 600/11). Тап по бару = seek.
- Керування: shuffle · ⏮ · `APlayButton` · ⏭ · repeat (активні — `redHi`).
- Панель дій (скляна, радіус 18): Equalizer → `EqSheet`; Ringtone → `AppScreen(RINGTONE)` з цим треком;
  Sleep (0 → 15 → 30 → 60 → 0 хв, тост); Share → `shareTrack()`.
- Анімація входу: slide-up з-під міні-плеєра 0.3 с.

## EqSheet
- Заголовок «Equalizer» + `AToggle` On/Off.
- Пресети `AChip(MOOD)`: Flat, Bass Boost, Vocal, Lo-fi, Rock (+ Custom, коли рухали повзунок).
- 5 рядків: частота (**M** 11.5) · повзунок −12…+12 · значення `+4 dB` (**M** 12).
- `Done`. Зміни — одразу в `bridge.setEqualizer()`, збереження в `AppState`.

## FeedSheet
- Шапка: `ACover` 58 + назва **D** 20 + `host · RSS · N latest`.
- Епізоди: назва O 600/13.5, `47:20 · 41 MB · MP3`, праворуч ⬇ / `%` / ✓.
- Дані: `fetchText(url)` → `RssParser.parse()`; поки вантажиться — спінер; помилка — текст + Retry.

## LauncherScreen
- Фон `launcher_bg.jpg` (вже розмитий і затемнений), на весь екран під статус-бар.
- Годинник **C** 62 (оновлювати раз на хвилину по `System.currentTimeMillis`), дата O 14 білим 75 %
  (`EEEE, MMMM d`, англ.).
- `AClipCard` (якщо `ClipGate` дав лінк): градієнт red 35 % → темний, рамка red 45 %,
  eyebrow `LINK FROM CLIPBOARD` (**M** 700/10.5) + чип `DRIVE`/`MP3`/`RSS`, заголовок O 700/16,
  URL **M** 11.5 у 2–3 рядки, `Download` / `Choose episode` + `Dismiss`.
  Поява: зсув +12 і scale 0.98 → 1 за 0.4 с. Download — качаємо одразу тут, прогрес у віджеті нижче.
- Віджет завантажень (якщо є): скляна картка, `Downloading · N`, рядки з прогресом.
- Віджет плеєра: грає — обкладинка 52 + назва + кнопка; не грає — 4 останні обкладинки.
- Розпірка, `All apps` (ручка 36×4) → оверлей-сітка 4 колонки (усі застосунки, `AppsRepo`).
- Док: 5 іконок (Phone, Messages, **Redwave**, Browser, Camera — реальні дефолтні апки
  ролей Dialer/SMS/Browser + камера; Redwave по центру), скляна підкладка радіус 26.
- «Назад» — нічого. Довгий тап по іконці — ◌ меню (видалити/інфо) у v2.

---

## Анімації (загальні)
- Перехід між екранами: `TIME_ANIM_SCREEN = 0.27 с` (як T35): `animShow`/`animHide` альфою
  + зсув 10 по Y (`.s.enter` у прототипі: 0.32 с, `pow3Out`).
- Кнопки: натиск scale 0.97 за 0.08 с, відпускання — назад.
- Прогрес-бари: плавне наближення до цільового значення (lerp 0.25 с), не стрибками.
- `prefers-reduced-motion` у прототипі ≈ системне «Вимкнути анімації» (`ANIMATOR_DURATION_SCALE = 0`)
  — ◌ поважати в v2.
