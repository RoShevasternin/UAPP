---
name: redwave-app
description: Redwave Music Downloader — тестова апка Tools у poc/TEST_APP/Redwave; прототип погоджено, усе підготовлено до переносу в LibGDX (план, port-kit з тестами, шрифти, іконки, тестові треки); Android-проєкт ще не створено
metadata:
  type: project
---

**Redwave: Music Downloader** — нова апка категорії Tools, не гра парку і **без еталона**
(правила `poc/TEST_APP/CLAUDE.md`). Тека: `/Users/admin/Apps/UAPP/poc/TEST_APP/Redwave/`
(до 06.10.2026 звалась `MusicHome`). Завантажувач з легальних лінків + CC-каталог, плеєр з EQ і
візуалізатором, різак рингтонів, власний головний екран (роль HOME) з карткою лінка з буфера.

**Прототип v2:** https://claude.ai/artifact/1WQgAQ5KYh7M3EHovXgLMC (копія `prototype/`), 8 екранів,
тест-план 7 сценаріїв, 6 кадрів 9:16. VELDAN підтвердив назву й попросив підготувати перенос
(06.10.2026). Наступний крок — **локальна сесія на Маці з девайсом**: створити Android-проєкт у
`Redwave/Redwave/` і переносити фазами Ф0–Ф9.

**Перед роботою прочитати:** `Redwave/CLAUDE.md` → `PORTING.md` (архітектура, що брати з T35,
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

**How to apply:** зміни прототипу — `Artifact read` URL → publish з `url` → перезаписати
`prototype/index.html`. Зміни логіки — спершу в `port-kit` + тест, потім в апку. Основа ролі
HOME — [[home-launcher-poc]]; атласи пакує VELDAN у GUI — [[mindora-texturepacker-cli]];
хмара мержить сама — [[uapp-cloud-merge-permission]].
