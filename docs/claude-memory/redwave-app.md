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
