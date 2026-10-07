# Redwave — тестові посилання

Готові лінки для перевірки апки на телефоні: скопіювати → вставити на головній (або
«Додому» → картка з буфера). Згруповано за тим, як апка **має** відреагувати.

> **Увага.** Лінки запрацюють лише після мержу гілки в `main`. До того
> raw.githubusercontent.com на кожен з них відповідає `404: Not Found`. Після пушу в `main`
> CDN кешує файл до 5 хвилин (`cache-control: max-age=300`), тож свіжі зміни видно не одразу.
> Перевірити після мержу: `sh tools/check_raw.sh`.

На GitHub кожен блок коду має кнопку «копіювати» — зручно відкрити цю сторінку на самому
телефоні.

Офлайн усе прогнано через логіку з `../port-kit` (копія з тимчасовим тестом, сам `port-kit` не
змінювали): `LinkResolver` — прямі файли, untitled і 404 → `DirectAudio`, `.rss` → `PodcastFeed`,
YouTube/Spotify → `Blocked`, github.com і `catalog.json` → `WebPage`, github.com `blob/…mp3` →
`DirectAudio` (його відсікає `AudioSniffer` за `text/html`); `AudioSniffer` визнає
всі 11 аудіофайлів аудіо і за Content-Type raw, і за першими байтами при
`application/octet-stream`; `RssParser` читає фід (2 епізоди, `length` = розмір файлу, 60 с);
`CatalogCodec` читає каталог (8 треків, 3 підбірки, `sizeBytes` = розмір файлу).

## 1. Прямі файли → завантаження

Очікуємо: розпізнано як аудіо, завантажено, назва/артист/обкладинка — з тегів файлу.
Content-Type — те, що raw реально віддає для цього розширення (перевірено, див. розділ 8).

**MP3** · 45 с · 128 kbps · ID3v2.3 + обкладинка · `audio/mpeg`
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/neon-heart.mp3
```
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/night-drive.mp3
```
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/stage-lights.mp3
```
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/needle-drop.mp3
```

**M4A (AAC)** · 45 с · 128 kbps · теги + обкладинка · `audio/mp4` — «Blue Room», Sable Quartet
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/blue-room.m4a
```

**FLAC** · 45 с · lossless · Vorbis comments + обкладинка · `audio/flac` — «Into the Smoke», Ash & Ember
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/into-the-smoke.flac
```

**OGG Vorbis** · 45 с · q4 · теги **без** вбудованої обкладинки · `audio/ogg` — «White Noise», Static Bloom.
Перевірка фолбеку: апка показує заглушку (або обкладинку з каталогу), а не падає.
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/white-noise.ogg
```

**WAV** · 25 с · PCM 16 біт моно · **без тегів** · `audio/wav` → назва з імені файлу «Bass Theory»
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/bass-theory.wav
```

## 2. Файл без тегів → назва з імені файлу

`untitled_demo-track.mp3` — 20 с, MP3 без ID3 узагалі (файл починається одразу з MPEG-кадру
`FF FB`, а не з `ID3`). Очікуємо назву **«Untitled Demo Track»** (як `titleFrom()` у прототипі:
без розширення, `-`/`_` → пробіл, кожне слово з великої), артист порожній / «Unknown artist»,
обкладинка — заглушка.
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/untitled_demo-track.mp3
```

## 3. RSS подкасту → шит фіду

Очікуємо: шит «Indie Hour (test feed)» з обкладинкою і двома епізодами (Ep. 112, Ep. 111,
по 1:00); кнопка завантаження епізоду бере `<enclosure>` (`audio/mpeg`, розмір у байтах
точно збігається з файлом). Raw віддає `.rss` як текст (`text/plain; charset=utf-8`, не
`application/rss+xml`) — визначати фід за розширенням або за початком тіла (`<?xml` / `<rss`).
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/indie-hour.rss
```
Самі епізоди (mono 64 kbps, ID3 + обкладинка) — можна вставити й напряму:
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/podcast/indie-hour-ep112.mp3
```
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/podcast/indie-hour-ep111.mp3
```

## 4. catalog.json → Discover (dev-каталог)

Очікуємо: Discover підтягує 8 CC0-треків (MP3 ×4, M4A, FLAC, OGG, WAV) і 3 підбірки
(Live Energy / Night Drive / Vinyl Sessions), фільтр за настроєм працює. Raw віддає JSON як
`text/plain; charset=utf-8` (і з `content-encoding: gzip`, якщо клієнт його просить) —
парсити незалежно від Content-Type. Це URL для dev-каталогу Discover, а не лінк для вставки на
головній: там `LinkResolver` з `port-kit` бачить його як `WebPage`, і правильна реакція —
«це не аудіофайл».
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/catalog.json
```

## 5. Заборонені джерела → «не дозволяє завантаження»

Очікуємо: одразу (без мережевого запиту) повідомлення, що сервіс не дозволяє завантаження;
нічого не качається.
```
https://youtu.be/q9Xb3rT_lofi
```
```
https://open.spotify.com/track/4uLU6hMCjMI75M1A2tKUQC
```

## 6. Вебсторінка → «це не аудіофайл»

Очікуємо: `text/html` → повідомлення «це не аудіофайл» (або «на сторінці не знайдено аудіо»).
```
https://github.com/RoShevasternin/UAPP
```
Підступний варіант: шлях закінчується на `.mp3`, але це HTML-сторінка перегляду файлу на
github.com (`text/html; charset=utf-8`). Якщо апка вірить лише розширенню — скачає HTML під
виглядом MP3. Правильно — відмовити за Content-Type / першими байтами.
```
https://github.com/RoShevasternin/UAPP/blob/main/poc/TEST_APP/Game%20Redwave/test-media/neon-heart.mp3
```
А з `?raw=true` github.com робить два редиректи `302` (`…/raw/refs/heads/main/…` → raw) і
віддає справжній MP3 (`audio/mpeg`). Перевірка, що апка йде за редиректами:
```
https://github.com/RoShevasternin/UAPP/blob/main/poc/TEST_APP/Game%20Redwave/test-media/neon-heart.mp3?raw=true
```

## 7. Неіснуючий файл → обробка помилки

Очікуємо: зрозуміле повідомлення про помилку (файл не знайдено), без «битого» запису в
бібліотеці. Raw: `HTTP 404`, `content-type: text/plain; charset=utf-8`, `content-length: 14`,
тіло `404: Not Found`.
```
https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media/missing-track.mp3
```

## 8. Як raw.githubusercontent.com віддає файли (перевірено 06.10.2026)

Перевіряли `curl -sI` (HEAD) і GET з `Range` на наявних файлах: цей репо (`main.mp3`,
`main.ogg`, `all.png`, `*.json`, `*.html`, `AndroidManifest.xml`, `poc/concept/video.mp4`) і публічні
репо з тестовими семплами (`mathiasbynens/small`, `chromium/chromium` → `media/test/data`,
`mozilla/gecko-dev` → `dom/media/test`, `rafaelreis-hotmart/Audio-Sample-files`,
`anars/blank-audio`).

| Розширення | Content-Type на raw | Де перевірено |
|---|---|---|
| `.mp3` | `audio/mpeg` | UAPP `…/Mindora Self Test/…/music/main.mp3`, `anars/blank-audio`, `mathiasbynens/small/mp3.mp3` |
| `.m4a` | `audio/mp4` | `chromium/…/sfx.m4a`, `gecko-dev/…/small-shot.m4a` |
| `.flac` | `audio/flac` | `chromium/…/sfx.flac`, `gecko-dev/…/small-shot.flac` |
| `.ogg` | `audio/ogg` | UAPP `…/FF Miner Skin Tool/…/music/main.ogg`, `chromium/…/sfx-opus.ogg` |
| `.opus` | `audio/ogg` | `gecko-dev/…/test-1-mono.opus` |
| `.wav` | `audio/wav` | `mathiasbynens/small/wav.wav`, `Audio-Sample-files/sample.wav` |
| `.aac` | `audio/aac` | `chromium/…/bear-audio-main-aac.aac` |
| `.webm` | `audio/webm` | `mathiasbynens/small/webm.webm` |
| `.mp4` | **`application/octet-stream`** | UAPP `poc/concept/video.mp4`, `chromium/…/bear-flac.mp4`, `Audio-Sample-files/sample.mp4` |
| `.adts` | **`application/octet-stream`** | `chromium/…/sfx.adts` |
| `.jpg` / `.png` / `.svg` | `image/jpeg` / `image/png` / `image/svg+xml` | `mathiasbynens/small`, UAPP `all.png` |
| `.json` `.xml` `.html` `.md` | `text/plain; charset=utf-8` | UAPP `*.json`, `AndroidManifest.xml`, `docs/…/01.html`, `CLAUDE.md` |
| `.rss` | не перевірено напряму (публічного `.rss` на raw не знайшли); очікувано `text/plain; charset=utf-8`, як і решта тексту | — |
| неіснуючий файл | `404`, `text/plain; charset=utf-8`, `content-length: 14`, тіло `404: Not Found` | — |

Спільне для всіх відповідей:

- **HEAD працює**: `200` з правильним `content-length` і тим самим Content-Type, що й GET.
- `accept-ranges: bytes`; `Range: bytes=0-15` → `206` + `content-range: bytes 0-15/<розмір>` —
  можна докачувати й читати перші байти без завантаження всього файлу.
- `x-content-type-options: nosniff`, `access-control-allow-origin: *`, `etag`,
  `cache-control: max-age=300`.
- Бінарні файли не стискаються (`content-encoding` немає навіть з `Accept-Encoding: gzip`), тож
  `content-length` = розмір файлу. **Текст** (`.json`, `.rss`, …) з `Accept-Encoding: gzip`
  приходить як `content-encoding: gzip` зі стиснутою довжиною — OkHttp розпаковує сам, і тоді
  `contentLength()` = `-1`.
- github.com `…/blob/…` — HTML-сторінка; `…/blob/…?raw=true` → `302` на `…/raw/refs/heads/…` →
  `302` на raw.

**Висновок для логіки «це аудіо?».** Припущення «raw віддає бінарники як
`application/octet-stream`» для аудіо **не підтвердилось**: raw ставить тип за розширенням, і всі
наші формати (`.mp3 .m4a .flac .ogg .opus .wav .aac`) приходять як `audio/*`. `octet-stream`
буває для розширень, яких raw не вважає аудіо (`.mp4`, `.adts`), а на інших хостингах (Dropbox,
Google Drive, S3, самописні CDN) — постійно. Тому порядок такий:

1. `Content-Type: audio/*` (і `application/ogg`, `video/mp4` для `.m4a`) → аудіо.
2. `application/octet-stream`, `binary/octet-stream` або порожній → дивимось розширення (з URL
   або `Content-Disposition: filename=`) **і** перші байти (через `Range: bytes=0-15`):
   `49 44 33` (`ID3`) → MP3 з тегами; `FF Ex/Fx` з layer ≠ 00 → MP3 без тегів (як
   `untitled_demo-track.mp3`, `FF FB`); `FF F1` / `FF F9` → AAC ADTS; `66 4C 61 43` (`fLaC`);
   `4F 67 67 53` (`OggS`); `52 49 46 46 … 57 41 56 45` (`RIFF….WAVE`); `?? ?? ?? ?? 66 74 79 70`
   (`ftyp` на зсуві 4; бренд `M4A ` → аудіо, `isom`/`mp42` — може бути й відео).
3. `text/html` → «не аудіофайл», **навіть якщо URL закінчується на `.mp3`** (кейс з github.com
   `blob`).
4. `text/plain`/`text/xml`/`application/*xml` з `.rss`/`.xml` або тілом `<?xml`/`<rss` → RSS;
   `.json` → каталог. На Content-Type тексту з raw не покладатися — там завжди `text/plain`.
