---
name: redwave-app
description: Redwave Music Downloader — тестова апка Tools у poc/TEST_APP/Game Redwave (апка — у вкладеній Redwave/); з 07.10.2026 Android-проєкт LibGDX є і працює на девайсі (усі 5 екранів, Ф0–Ф8 у першому проході)
metadata:
  type: project
---

**Redwave: Music Downloader** — нова апка категорії Tools, не гра парку і **без еталона**
(правила `poc/TEST_APP/CLAUDE.md`). Тека: `/Users/admin/Apps/UAPP/poc/TEST_APP/Game Redwave/`
(до 06.10.2026 — `MusicHome`, до 07.10.2026 — `TEST_APP/Redwave`). З 07.10.2026 структура як в
іграх парку: **сама апка — лише в `Game Redwave/Redwave/`** (Android-проєкт + її `CLAUDE.md`),
а `PORTING.md`, `SCREENS.md`, `prototype/`, `port-kit/`, `assets/`, `market/`, `test-media/` —
поруч у `Game Redwave/`. Raw-лінки тест-треків — з `Game%20Redwave` у шляху. Завантажувач з легальних лінків + CC-каталог, плеєр з EQ і
візуалізатором, різак рингтонів, власний головний екран (роль HOME) з карткою лінка з буфера.

**Прототип v2:** https://claude.ai/artifact/1WQgAQ5KYh7M3EHovXgLMC (копія `prototype/`), 8 екранів,
тест-план 7 сценаріїв, 6 кадрів 9:16. VELDAN підтвердив назву й попросив підготувати перенос
(06.10.2026). Наступний крок — **локальна сесія на Маці з девайсом**: створити Android-проєкт у
`Game Redwave/Redwave/` і переносити фазами Ф0–Ф9.

**Перед роботою прочитати:** `Game Redwave/Redwave/CLAUDE.md` → `PORTING.md` (архітектура, що брати з T35,
маніфест, пастки ✔/◌, фази з чекпоінтами, чекліст, Play) → `SCREENS.md` (5 екранів + шторки +
компоненти з розмірами). На старті спитати «❓ Відкриті рішення» з `PORTING.md` §1 — насамперед
**applicationId** (пропозиція `com.redwave.downloader`, так названо пакет у port-kit).

**Що вже готово (06.10.2026, хмарна сесія):**
- `port-kit/` — чиста Kotlin-логіка + `PlatformBridge` (контракт GDX↔Android), 31 JVM-тест
  (`gradle test`): LinkResolver, AudioSniffer, ClipGate, RssParser, моделі/JSON, RingtoneCut,
  EqCurve, SpectrumAnalyzer, Copy (тексти UI), GameColor, Constants (`px()`: 1 px прототипу = 1.074 wu).
- `assets/fonts/` — MSDF у форматі T35 (msdf-atlas-gen mtsdf 48/32): Archivo Expanded (лише англ.
  заголовки — **без кирилиці**), **Onest** (UI і весь текст користувача), JetBrains Mono. Figtree з
  прототипу в апку не йде — немає кирилиці.
- `assets/all/icons/` (40 PNG), `assets/brand/` (лого, mipmap-* адаптивна іконка), `assets/textures/`,
  `market/` (скріни 1080×1920, іконка 512, feature 1024×500).
- `test-media/` — наші згенеровані треки CC0 (mp3/m4a/flac/ogg/wav, з тегами й без), RSS,
  `catalog.json`; прямі лінки з GitHub raw — `test-media/links.md`. raw віддає аудіо як `audio/*`,
  а `.rss/.json` — як `text/plain`.

**Ключові технічні рішення:** одна Activity (HOME+LAUNCHER+SEND, singleTask, без трампліну
StartActivity); музика — Media3 у MediaSessionService, не Gdx.audio; візуалізатор через
TeeAudioProcessor (без RECORD_AUDIO); буфер читати в onWindowFocusChanged + ClipGate за timestamp
(тост Android 12+); бібліотека — свій індекс у DataStore; «Назад» на лаунчері нічого не робить.

**Стан на 07.10.2026 (локальна сесія з Redmi):** пакет `com.redwave.downloader`, атлас — gdx-tools
(`:app:packAtlas`). Зроблено й перевірено на девайсі: сплеш, онбординг, Home (поле = нативний EditText
поверх GL, помилки, черга, карусель, плитки), Discover (каталог з GitHub raw, кнопка ⬇/кільце/✓, підбірки),
Library, Player (Media3, візуалізатор через Tee, EQ — `Equalizer.setBandLevel` у лозі), Ringtone (піки
MediaCodec), Launcher (годинник, картка буфера, віджети, док із дефолтних апок, сітка). Не перевірено:
збереження рингтону (потрібен WRITE_SETTINGS — вмикає VELDAN), реальний буфер (adb на A13 не пише; є
debug-екстра `redwave.debug_clip`), роль HOME через діалог, фон з вимкненим екраном, FeedSheet.
Пастки, знайдені на девайсі: дві MainActivity (HOME-таск) → трамплін для іконки; raw віддає
Content-Disposition з шляхом; `.apply { setBounds(...height...) }` бере height самого актора.

**07.10.2026, друга половина дня:** роль HOME обов'язкова (без неї — екран-вимога); Custom Tab google.com
сам відкривається при поверненні на Home з іншої апки (не з лаунчера, не з наших системних екранів);
шестерня → Settings з debug-кнопкою повернути системний лаунчер; Library показує теку Music/Redwave,
«Delete file» видаляє з диска по-справжньому; Gradle 9.8 / AGP 9.4.1 / Kotlin 2.4.20 / OkHttp 5.5.
Пастка: при наданні ролі Android перезапускає MainActivity як HOME і колбек ролі гине — тому
navigateFirst сам ставить onboarded, якщо роль уже наша. Відкрите: копіювання в буфер через adb на
MIUI A13 не працює, тож картку буфера на реальному кліпі не перевірено; WRITE_SETTINGS не надано.

**How to apply:** зміни прототипу — `Artifact read` URL → publish з `url` → перезаписати
`prototype/index.html`. Зміни логіки — спершу в `port-kit` + тест, потім в апку. Основа ролі
HOME — [[home-launcher-poc]]; атласи пакує VELDAN у GUI — [[mindora-texturepacker-cli]];
хмара мержить сама — [[uapp-cloud-merge-permission]].

## Remote Config (рішення VELDAN 07.10.2026, зроблено того ж дня)
- Firebase-проєкт `redwave-original`, у апці лише Remote Config (BoM 34.19.0, плагін google-services 4.5.0).
- Параметр `redwave_config` (JSON): `enabled_url_ad` (до 08.10.2026 — `enabled`) + `url` — сторінка-реклама на «Додому»/після «Недавніх»;
  `home_required` — true: лише «Set as Home screen», false: ще й «Maybe later» (видна, але скромна);
  `is_uninstall` — довге натискання в лаунчері → App info / Uninstall; необов. `privacy_url`.
- Немає відповіді Firebase → enabled=false, home_required=false, is_uninstall=true. Перевірка на кожному вході.
- Settings: перемикач «Use as Home screen» (вимкнути = системний вибір лаунчера), Privacy Policy;
  debug-кнопку вибору лаунчера прибрано. Privacy Policy — `Game Redwave/privacy-policy/`, викладено на Cloudflare: https://redwave-privacy.oyutetijep68.workers.dev/privacy (проєкт redwave-privacy; розробник STAR ADS LLC, oyutetijep68@gmail.com; без згадки реклами на «Додому» — рішення VELDAN;
  прописано в RemoteFlags.DEFAULT_PRIVACY_URL).
- Попереджено: вимикати рекламу лише на час рев'ю — обман рев'ю (ризик бану акаунта).
- Debug AD_MODE (Settings → Debug, лише debug-збірка): реклама + роль обов'язкова + без Uninstall поверх Remote Config,
  перемикання перезапускає процес; на екрані-вимозі є «AD_MODE OFF». У Remote Config зараз «білі» значення.
- Назва в маркеті має містити Launcher/Home (рішення VELDAN), конкретну ще не обрано.

## Реклама AdMob (VELDAN, 07.10.2026, увечері — зроблено й перевірено на Redmi)
- App Open: відкриття (холодний старт з іконки, іконка Redwave на нашому лаунчері) + повернення в апку,
  якщо людини не було ≤ 1 год; не частіше раз на 3 хв від будь-якої повноекранної. Interstitial — кожні 2 треки.
- Лише в AppScreen/PlayerScreen — НЕ на лаунчері (повноекранна на головному екрані = disruptive ads), не на онбордингу.
- Вмикач — Remote Config `is_enable_admob` (08.10.2026): false / офлайн / без Firebase → AdMob немає зовсім
  (SDK не ініціалізується). AD_MODE вмикає й AdMob. `AD_ID` прописано в маніфесті явно.
- Зараз тестові блоки Google (play-services-ads 25.5.0). Перед релізом: свої ID, UMP, Data safety.
- Privacy Policy: 08.10.2026 додано розділ про AdMob (рекламний ID, IP, дані пристрою, відмова від персоналізації);
  реклама на «Додому» там і далі не згадується. Без UMP у тексті немає обіцянки вікна згоди.
- ◌ Тлумачення «повернувся протягом години» — буквальне: > 1 год поза апкою без холодного старту → без App Open.
  Якщо VELDAN мав на увазі інше — константа `AdPolicy.RETURN_WINDOW_MS` / `canAppOpenOnReturn`.

## Орієнтир для публікації (VELDAN, 07.10.2026)
- Cube Square: Space Launcher (`com.square.rush.cub.dash.runner`, Screening Developers) — пройшла модерацію:
  «Launcher» у назві, розділ «Desktop Launcher Features» в описі, категорія Games → Casual, «Contains ads»,
  Everyone, 500K+, Data safety з Location/Installed apps/Device IDs. У відгуках (07–09.2026): «force you to put it
  as home app», «pop up every time I opened any app» — той самий механізм, ризик блокування вже після публікації.
- Менеджер хоче категорію Tools. Акаунт — організація STAR ADS LLC (закритий тест 12×14 не обов'язковий).
