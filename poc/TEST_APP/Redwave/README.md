# Redwave: Music Downloader

Тестова апка категорії **Tools** (не гра парку). Завантажувач музики з **легальних** посилань
+ безкоштовний CC-каталог, плеєр з еквалайзером і візуалізатором, різак рингтонів і власний
головний екран (роль HOME): скопіював лінк будь-де → «Додому» → картка «Download».

- **Живий прототип:** https://claude.ai/artifact/1WQgAQ5KYh7M3EHovXgLMC (копія — `prototype/`)
- **Стан на 06.10.2026:** прототип v2 погоджено, усе підготовлено до переносу в LibGDX.
  Android-проєкт ще не створено — він з'явиться в `Redwave/` на першій локальній сесії.

## З чого почати локальну сесію

1. `git pull` у `/Users/admin/Apps/UAPP`.
2. Прочитати `CLAUDE.md` (правила цієї теки) → `PORTING.md` (план і пастки) → `SCREENS.md` (екрани).
3. Поставити VELDAN питання з `PORTING.md` → «❓ Відкриті рішення» (насамперед пакет).
4. Ф0 з `PORTING.md` → перша збірка на девайсі.

## Що в теці

| Шлях | Що | Розмір |
|---|---|---|
| `CLAUDE.md` | правила проєкту для Claude: стек, конвенції, збірка, git | — |
| `PORTING.md` | архітектура, T35-донор, Gradle, маніфест, Android-бік з пастками, фази Ф0–Ф9, чекліст, Play | — |
| `SCREENS.md` | 5 екранів + шторки + бібліотека компонентів з розмірами | — |
| `prototype/` | HTML-прототип v2, фото CC0 (`img/SOURCES.md`, `make_assets.py`) | 0.7 МБ |
| `port-kit/` | чиста Kotlin-логіка + `PlatformBridge` + 31 тест (`gradle test`) | — |
| `assets/fonts/` | MSDF у форматі T35: Archivo Expanded (заголовки, без кирилиці), Onest (UI, з кирилицею), JetBrains Mono; TTF, OFL, скрипти | 6.9 МБ |
| `assets/all/icons/` | 40 іконок UI, білі, 96 px (+ `ph_glyph` 192) — джерело атласу `all` | — |
| `assets/brand/` | лого, адаптивна іконка по `mipmap-*`, SVG, скрипти рендеру | — |
| `assets/textures/` | онбординг, банери Discover, фон лаунчера, світіння | 0.2 МБ |
| `market/` | Google Play: 6 скрінів 1080×1920, іконка 512, feature 1024×500 | 4.3 МБ |
| `test-media/` | наші згенеровані треки (CC0) у 6 форматах, подкаст-фід, `catalog.json`, `links.md` з прямими лінками для тестів | 8.8 МБ |

## Рішення, ухвалені 06.10.2026

- Назва **Redwave: Music Downloader**, червоний бренд, лого — еквалайзер зі стрілкою.
- Джерела — **лише легальні**: прямі аудіофайли, Google Drive, Dropbox, OneDrive, RSS,
  CC-каталог. YouTube, Spotify, SoundCloud, Apple Music, Deezer, VK, TikTok — блок.
- Функція Home — **картка лінка з буфера**; апка працює й без ролі HOME.
- Стек — LibGDX з інструментами UAPP, **без еталона** (`../CLAUDE.md`).
- UI-шрифт — **Onest** замість Figtree з прототипу: у Figtree й Archivo немає кирилиці, а назви
  треків з тегів бувають українські.
- Візуалізатор — без `RECORD_AUDIO` (Media3 TeeAudioProcessor), бібліотека — свій індекс, не MediaStore.
