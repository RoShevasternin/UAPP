# Redwave — тестові медіа

Справжні файли для перевірки апки «Redwave: Music Downloader» на телефоні: прямі аудіо
всіх форматів, файл без тегів, RSS-фід подкасту і JSON-каталог для Discover. Лежать у
публічному репо й качаються через raw.githubusercontent.com:

```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/
```

Готові лінки й очікувана поведінка апки — **[links.md](links.md)**. Лінки працюють лише після
мержу гілки в `main`.

## Ліцензія

- **Аудіо** згенероване нами: лоу-фай синтезатор із прототипу
  (`../prototype/index.html`), відрендерений офлайн. Чужих семплів і записів немає. Віддаємо як
  **CC0 1.0** (суспільне надбання), тому в каталозі в усіх треків `license: "CC0"`. Артисти й
  назви вигадані (ті самі, що в прототипі).
- **Обкладинки** — CC0-фото з StockSnap.io з нашою кольорокорекцією, джерела й ID — у
  [../prototype/img/SOURCES.md](../prototype/img/SOURCES.md).

## Файли

| Файл | Формат | Тривалість | Бітрейт | Розмір | Теги | Обкладинка в файлі |
|---|---|---|---|---|---|---|
| `neon-heart.mp3` | MP3, стерео 44.1 кГц | 0:45 | 128 kbps CBR | 736 588 Б (719 КіБ) | так, ID3v2.3 | так (APIC) |
| `night-drive.mp3` | MP3, стерео 44.1 кГц | 0:45 | 128 kbps CBR | 749 529 Б (732 КіБ) | так, ID3v2.3 | так (APIC) |
| `stage-lights.mp3` | MP3, стерео 44.1 кГц | 0:45 | 128 kbps CBR | 754 877 Б (737 КіБ) | так, ID3v2.3 | так (APIC) |
| `needle-drop.mp3` | MP3, стерео 44.1 кГц | 0:45 | 128 kbps CBR | 746 548 Б (729 КіБ) | так, ID3v2.3 | так (APIC) |
| `blue-room.m4a` | AAC-LC у MP4 (M4A), faststart | 0:45 | 128 kbps | 742 341 Б (725 КіБ) | так, iTunes-атоми | так (`covr`) |
| `into-the-smoke.flac` | FLAC 16 біт, стерео 44.1 кГц | 0:45 | ~211 kbps (lossless) | 1 192 350 Б (1.14 МіБ) | так, Vorbis comments | так (PICTURE) |
| `white-noise.ogg` | Ogg Vorbis q4, стерео 44.1 кГц | 0:45 | ~77 kbps (VBR) | 433 886 Б (424 КіБ) | так, Vorbis comments | **ні** (навмисно) |
| `bass-theory.wav` | WAV PCM 16 біт, **моно** 44.1 кГц | 0:25 | 705.6 kbps | 2 205 044 Б (2.10 МіБ) | **ні** | ні |
| `untitled_demo-track.mp3` | MP3, стерео 44.1 кГц | 0:20 | 128 kbps CBR | 320 991 Б (313 КіБ) | **ні** (жодного ID3) | ні |
| `podcast/indie-hour-ep112.mp3` | MP3, моно 44.1 кГц | 1:00 | 64 kbps CBR | 497 664 Б (486 КіБ) | так, ID3v2.3 | так (APIC) |
| `podcast/indie-hour-ep111.mp3` | MP3, моно 44.1 кГц | 1:00 | 64 kbps CBR | 497 660 Б (486 КіБ) | так, ID3v2.3 | так (APIC) |
| `indie-hour.rss` | RSS 2.0 + `itunes:` | — | — | 3 174 Б | — | `itunes:image` |
| `catalog.json` | JSON, `version: 1` | — | — | 4 848 Б | — | посилання на `covers/` |
| `covers/*.jpg` | JPEG 480×480, 9 шт. | — | — | 189 871 Б разом | — | — |

Усього тека — близько 8.7 МіБ (разом з `tools/` і цими md).

**Теги** (усі файли з «так»): title, artist, album `Redwave Test Media`, date `2026`,
genre `Lo-fi`, comment `Generated for Redwave testing · CC0`. Епізоди подкасту: artist
`Indie Hour`, album `Indie Hour (test feed)`, genre `Podcast`. У MP3 — ID3v2.3, текст у UTF-16,
рік у `TYER`, коментар у `COMM` (eng), обкладинка — `APIC` type 3 (front cover), ID3v1 немає.

**Що чим перевіряється**

- 4 MP3 + M4A + FLAC + OGG + WAV — усі формати, які апка обіцяє качати; у M4A `moov` стоїть
  перед `mdat` (можна грати, поки качається).
- `white-noise.ogg` — теги є, вбудованої обкладинки немає → заглушка / обкладинка з каталогу.
- `bass-theory.wav` і `untitled_demo-track.mp3` — без тегів → назва з імені файлу
  («Bass Theory», «Untitled Demo Track»). Untitled починається з MPEG-кадру `FF FB`, а не з
  `ID3` — перевірка розпізнавання MP3 за першими байтами.
- `indie-hour.rss` — 2 епізоди з точними `length` в `<enclosure>`, `itunes:duration`
  `00:01:00`, `pubDate` у форматі RFC 822.
- `catalog.json` — 8 треків (`id`, `title`, `artist`, `license`, `mood`, `durationSec`,
  `sizeBytes`, `format`, `url`, `cover`) і 3 підбірки (`featured`: `id`, `title`, `subtitle`,
  `mood`, `cover`). `id` треків = id з прототипу (`t1`, `t2`, `k1`, `k4`, `k3`, `t3`, `k8`, `t5`).

Як raw віддає ці типи (Content-Type, Range, HEAD) — у [links.md](links.md#8-як-rawgithubusercontentcom-віддає-файли-перевірено-06102026).

## Музика

Синтезатор — функції `hash`, `rng`, `mtof`, `CHORD`, `PROGS`, `songOf`, `env`, `osc`, `noise`,
`schedStep` з прототипу, без змін. Трек задається рядком `id`: з нього `songOf()` бере тональність,
гармонічну прогресію, темп і мелодію.

| id | Трек | BPM | id | Трек | BPM |
|---|---|---|---|---|---|
| `t1` | Neon Heart | 80 | `t3` | Into the Smoke | 93 |
| `t2` | Night Drive (і untitled) | 85 | `k8` | White Noise | 90 |
| `k1` | Stage Lights | 80 | `t5` | Bass Theory | 79 |
| `k4` | Needle Drop | 80 | `e112` | Ep. 112 (подкаст) | 70 |
| `k3` | Blue Room | 96 | `e111` | Ep. 111 (подкаст) | 70 |

Рендер: `OfflineAudioContext(2, 44100 × секунди, 44100)`, шина → master gain 0.5 → вихід (без
еквалайзера й аналізатора прототипу), усі 16-ті кроки (`60 / bpm / 4` с) розписані наперед,
потім fade-in і fade-out по 1.5 с, нормалізація піку до −1 dBFS, 16-бітний WAV. `Math.random`
замінено на сідований `rng`, тож шум і хай-хети щоразу ті самі; Chromium усе одно дає різницю
±1 LSB у кількох сотнях семплів між запусками, тобто файли відтворюються «на слух», не біт у біт.

WAV і подкаст — моно (синтезатор і так моно, ліво = право), щоб тека вмістилась у ~9 МБ.

## Як перегенерувати

Потрібні Node + Playwright (Chromium), ffmpeg/ffprobe з `libmp3lame`, `aac`, `flac`,
`libvorbis`, Python 3 з `mutagen`.

```sh
cd "poc/TEST_APP/Game Redwave/test-media/tools"

# 1) синтезатор → WAV (11 файлів, ~50 с)
node render.js /tmp/redwave-wav
#    якщо Playwright без свого Chromium: CHROMIUM=/шлях/до/chrome node render.js /tmp/redwave-wav

# 2) WAV → MP3/M4A/FLAC/OGG/WAV, теги, обкладинки, catalog.json, indie-hour.rss + таблиця перевірки
python3 -m venv /tmp/redwave-venv && /tmp/redwave-venv/bin/pip install mutagen
/tmp/redwave-venv/bin/python build.py /tmp/redwave-wav ..

# 3) після мержу в main — що реально віддає raw (статус, Content-Type, довжина)
sh check_raw.sh
```

`build.py` переписує всі медіа, `catalog.json` і `indie-hour.rss` разом, тож `sizeBytes` у
каталозі й `length` у RSS завжди збігаються з файлами. Файли руками не міняти — після будь-якої
зміни аудіо чи тегів перезапустити `build.py`.

| Скрипт | Що робить |
|---|---|
| `tools/synth.html` | синтезатор з прототипу + `renderTrack()` (офлайн-рендер, фейди, нормалізація, WAV) |
| `tools/render.js` | Playwright відкриває `synth.html` і зберігає WAV для кожного id |
| `tools/build.py` | кодування ffmpeg, теги mutagen, копіювання обкладинок, каталог, RSS, перевірка ffprobe |
| `tools/check_raw.sh` | HEAD на кожен файл на raw після мержу |
