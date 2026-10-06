---
name: music-home-test-app
description: Music Home — тестова апка категорії Tools (завантажувач музики з легальних лінків + роль HOME); прототип-артефакт і тека poc/TEST_APP/MusicHome
metadata:
  type: project
---

**Music Home** (06.10.2026) — нова апка в категорію Tools, не гра парку. Ідея VELDAN:
«All Music Downloader & Home». Тека: `poc/TEST_APP/MusicHome/` (README + `prototype/index.html`).
Прототип (артефакт): https://claude.ai/artifact/1WQgAQ5KYh7M3EHovXgLMC — телефон-мокап,
сценарій 5 кроків, журнал подій з іменами Android API, таблиця джерел, нотатки «перевірити на
девайсі» і «як ляже на LibGDX». UI апки англійською, пояснення українською.

Рішення користувача: джерела **тільки легальні** (прямі аудіо, Drive, Dropbox, OneDrive, RSS;
YouTube/Spotify/SoundCloud тощо — ніколи); функція Home = **картка «лінк з буфера»** на
головному екрані. Прототип — будь-якою технологією (зроблено vanilla HTML/JS); при
перенесенні апка буде **на LibGDX з інструментами UAPP, але БЕЗ еталона** (06.10.2026: «це новий
тип апки» — жодних businesModule/adsmodule, ETALON_MIGRATION не застосовується). Правила теки —
`poc/TEST_APP/CLAUDE.md`; донор інструментів — `agust/Game T35/Mindora Self Test`.

**v2 (06.10.2026):** робоча назва **Redwave: Music Downloader** (червоний бренд, лого — еквалайзер
зі стрілкою). Прототип переписано: 8 екранів (splash, onboarding, home, discover, library, player з
EQ і візуалізатором на Web Audio, ringtone maker, launcher), тест-план 7 сценаріїв, 6 кадрів 9:16
для Play. Фото — CC0 StockSnap через Openverse (stocksnap.io напряму з контейнера ріже, працює
`api.openverse.org/v1/images/<id>/thumb/?full_size=true`); список — `prototype/img/SOURCES.md`.
Figma generate_image не чіпали — платно, кредити користувача.

**How to apply:** правки прототипу — `Artifact read` URL → publish з `url`, потім перезаписати
копію `prototype/index.html`. Основа ролі HOME — [[home-launcher-poc]].
