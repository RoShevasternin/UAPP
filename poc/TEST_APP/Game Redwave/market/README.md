# Redwave — графіка для Google Play

Що вписати в Play Console (назва, описи, категорія, Data safety, рейтинг) — [LISTING.md](LISTING.md).

Згенеровано 06.10.2026 з прототипу `prototype/index.html` скриптом
`assets/brand/tools/render_brand.js` + `finalize.py` (запуск: `sh assets/brand/tools/run_all.sh`).
Усі файли — PNG без альфа-каналу (RGB, 24 біт), саме так їх приймає Play Console.
08.10.2026 перегенеровано банер і кадри під назву **Redwave: Music Home Launcher** (порядок і підписи нижче).
Без глобального playwright: `npm i playwright` у тимчасовій теці, далі
`NODE_PATH=<тека>/node_modules node render_brand.js --only feature,shots --time 2026-10-06T09:41:00 --chrome "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"`,
потім `finalize.py` (потрібен Pillow, напр. у venv).

| Файл | Розмір | Поле в Play Console |
|---|---|---|
| `icon_512.png` | 512×512 | App icon |
| `feature_1024x500.png` | 1024×500 | Feature graphic |
| `screenshot_01.png` … `screenshot_06.png` | 1080×1920 (9:16) | Phone screenshots |

## Іконка `icon_512.png`

Квадрат на весь кадр: градієнт лого #ff5468 → #c2001f і білий гліф (смуги + стрілка
«завантажити») у тих самих пропорціях, що в лого. Скруглення й тінь **не малюємо**:
Play сам накладає маску зі скругленими кутами.

## Feature graphic `feature_1024x500.png`

У стилі прототипу: майже чорний фон #0a0607 з червоним світлом, лого 150 px, назва
«Redwave» (Archivo, ширина 118 %, жирність 800), під нею «MUSIC HOME LAUNCHER» (до 08.10.2026 — «MUSIC DOWNLOADER») з розрядкою
на ширину слова. Праворуч віяло з чотирьох обкладинок: `c-neon-heart`, `c-night-drive`,
`c-smoke`, `c-bass-theory` з `prototype/img`. Це фото CC0 зі StockSnap, джерела в
`prototype/img/SOURCES.md`. Іншого тексту чи обіцянок на банері немає. Шаблон —
`assets/brand/tools/feature_graphic.html`.

## Скріншоти

Шість кадрів 9:16 з галереї `#shots` прототипу. Це ті самі екрани, що в живому телефоні, зі
статичним станом. Підписи англійською, бо маркет США.

| Файл | Заголовок | Підзаголовок | Екран |
|---|---|---|---|
| `screenshot_01.png` | Paste any link. *Get the track.* | MP3, M4A, FLAC, Google Drive, Dropbox and podcast feeds | Home: поле з лінком, кнопка Download, черга завантажень |
| `screenshot_02.png` | Copy a link. *Press Home.* | Your Home screen catches audio links you copy anywhere | Launcher (роль HOME): годинник, картка лінка з буфера, віджет плеєра |
| `screenshot_03.png` | Free music. *Yours to keep.* | Creative Commons tracks, each with its license | Discover: настрої, підбірки, CC-треки з ліцензіями |
| `screenshot_04.png` | Feel every *beat.* | Live visualizer and a 5-band equalizer | Player: обкладинка, візуалізатор, прогрес, керування |
| `screenshot_05.png` | Your library. *Offline.* | No Wi‑Fi? Your music still plays | Library: пошук, пісні/подкасти, список треків |
| `screenshot_06.png` | Ringtones in *seconds.* | Cut any track, add fades, set it as your ringtone | Ringtone: хвиля з ручками, fade in/out, тип звуку |

*Курсив* — друга половина заголовка, у кадрі вона світло-коралова (#ffc6b8).

### Тонкощі

- ✔ «100% legal» (колишній кадр 02) замінено на «Yours to keep» 08.10.2026 — і в кадрі, і на банері
  Home в апці (`Copy.Home.PROMO_TITLE`). Підписи кадрів — `SHOTS` у прототипі.
- ✔ Виправлено при рендері, прототип не змінювали: у CSS прототипу правило нотаток
  `.f{color:var(--ok)}` (зелена галочка ✔) зачіпає й залиті іконки `svg.ic.f`, і
  play/pause/prev/next виходять зеленими. Скрипт повертає їм колір батька. У самому
  прототипі варто звузити правило до `.notes .f`.
- ✔ Годинник лаунчера в кадрі 02 бере поточний час. При рендері його зафіксовано на 9:41
  (параметр `--time`), як у статус-барі всіх кадрів. Через це на Home у кадрі 01 видно
  «Good morning».
- Імена артистів і треків вигадані. Лінк Google Drive у кадрі 02 — приклад.
- Кожен кадр рендериться рівно 360×640 CSS px при deviceScaleFactor 3. Телефон у кадрі
  масштабує той самий ResizeObserver, що в прототипі (`--k = 0.7005`). Кути кадрів прямі,
  без тіні.
