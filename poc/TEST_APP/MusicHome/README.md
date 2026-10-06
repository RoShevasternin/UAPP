# Music Home — тестова апка (категорія Tools)

Завантажувач музики з **легальних** посилань + власний головний екран (роль HOME).
Скопіював лінк будь-де → натиснув «Додому» → на Home картка «Завантажити».

- **Прототип (живий):** https://claude.ai/artifact/1WQgAQ5KYh7M3EHovXgLMC
- **Копія прототипу:** `prototype/index.html` (звичайний HTML/JS, відкривається в браузері)
- **Основа для ролі HOME:** `poc/HomeLauncher-PoC`

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
