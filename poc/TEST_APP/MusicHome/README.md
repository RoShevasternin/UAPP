# Redwave: Music Downloader — тестова апка (категорія Tools)

Робоча назва — **Redwave** (тека поки `MusicHome`). Завантажувач музики з **легальних**
посилань + безкоштовний CC-каталог, плеєр з еквалайзером, різак рингтонів і власний
головний екран (роль HOME): скопіював лінк будь-де → «Додому» → картка «Завантажити».

- **Прототип (живий):** https://claude.ai/artifact/1WQgAQ5KYh7M3EHovXgLMC
- **Копія прототипу:** `prototype/index.html` + `prototype/img/` (відкривається в браузері)
- **Звідки картинки:** `prototype/img/SOURCES.md` (CC0, StockSnap), скрипт обробки — `make_assets.py`
- **Основа для ролі HOME:** `poc/HomeLauncher-PoC`

## Екрани прототипу v2 (06.10.2026)

Splash → Onboarding (3 слайди, останній — запит ролі HOME) → Home (вставити лінк, черга,
нещодавні, статистика) → Discover (настрої, підбірки, CC-треки, подкасти RSS) → Library
(пошук, пісні/подкасти, місце) → Player (розмитий фон, візуалізатор, еквалайзер 5 смуг,
таймер сну) → Ringtone (хвиля з ручками, fade in/out, рингтон/будильник/сповіщення) →
Launcher (годинник, картка з буфера, віджет плеєра, док). Плюс 6 кадрів 9:16 для Google Play
і тест-план із 7 сценаріїв.

## Рішення (06.10.2026)

- Джерела — тільки легальні: прямі `.mp3/.m4a/.aac/.ogg/.opus/.flac/.wav`, Google Drive,
  Dropbox, OneDrive, RSS подкастів, інше — за `Content-Type: audio/*`.
  YouTube / Spotify / SoundCloud / Apple Music / Deezer / VK / TikTok — **ніколи**
  (бан у Google Play за IP-політику).
- Роль Home — картка «вставити лінк» з буфера. Читати буфер у
  `onWindowFocusChanged(true)`, не в `onResume`; новий кліп визначати за
  `ClipDescription.timestamp`, щоб не спамити тостом Android 12+.
- Апка працює й без ролі HOME («Not now»).

## Перенесення в апку

Апка буде на **LibGDX** з інструментами UAPP (екрани, актори, групи, шейдери, звук,
музика), **без еталона** — правила теки й донор інструментів описані в
`../CLAUDE.md`. Прототип лише показує UX і логіку (`resolveLink` переноситься як є).
Музика у фоні — через `MediaSessionService` (Media3), а не `Gdx.audio`: `Gdx.audio`
стає на паузу разом з апкою.
