# Framefall — графіка й файли для Google Play

Що вписати в Play Console (назва, описи, категорія, App content, Data safety, рейтинг, реліз) — [LISTING.md](LISTING.md).

| Файл | Розмір | Поле в Play Console |
|---|---|---|
| `framefall v1.aab` | — | Production / Testing → Create release (1.0.0, versionCode 1) |
| `icon_512.png` | 512×512 | App icon |
| `feature_1024x500.png` | 1024×500 | Feature graphic |
| `screenshot_01.png` … `screenshot_06.png` | 1080×1920 (9:16) | Phone screenshots |

Усі PNG — RGB без альфа-каналу, як приймає Play Console.

## Скріншоти

Справжні екрани апки з Redmi 10C (13.10.2026, `raw/*.png`, 720×1600), вставлені в рамку телефона з підписом.
Статус-бар MIUI замінено намальованим (демо-режим статус-бару MIUI ігнорує). Фільми — відкриті: Blender Foundation
(CC BY 3.0), NASA і класика в суспільному надбанні; посилання в кадрі 01 — справжній файл Elephants Dream на archive.org.

| Файл | Заголовок | Підзаголовок | Екран |
|---|---|---|---|
| `screenshot_01.png` | Copy a link. *Press Home.* | Your Home screen catches video links you copy anywhere | Home (роль HOME): відеофон Orion, картка лінка, Continue watching |
| `screenshot_02.png` | Paste a link. *Keep the video.* | Video files, Google Drive, Dropbox, Internet Archive and NASA | Save: поле, черга Saving, Continue watching |
| `screenshot_03.png` | Cut, loop, *pull the sound.* | Trim, make GIFs, save M4A audio or split a video for Status | Studio → Trim |
| `screenshot_04.png` | Watch it *anywhere.* | Your videos play offline and pick up where you left off | Плеєр з оверлеєм |
| `screenshot_05.png` | Open movies. *Yours to keep.* | Blender films and public-domain classics, credits included | Discover |
| `screenshot_06.png` | Everything *in one place.* | Movies, sound and Studio creations, right on your phone | Library |

*Курсив* — друга половина заголовка, у кадрі бурштиновий градієнт.

## Як перегенерувати

```bash
cd "poc/TEST_APP/Game Framefall"
sh market/tools/render.sh        # market.html (підписи — масив SHOTS) → headless Chrome → ffmpeg rgb24
```

Сирі кадри — `raw/` (замінити файл з тим самим ім'ям і перезапустити). Шрифти — `tools/fonts/` (OFL, ліцензії поруч).
Іконка `icon_512.png` — з прототипу, той самий логотип, що й іконка лаунчера (`../assets/brand/`).
