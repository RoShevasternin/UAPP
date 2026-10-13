# Framefall — графіка й медіа

Згенеровано 13.10.2026 скриптами з `tools/`. Іконки й лого беруться з прототипу
(`prototype/index.html`, об'єкт `const P={...}`) і `brand/logo.svg`, кадри й відео — з оригіналів
відкритих фільмів по HTTP. Після змін у прототипі досить перезапустити скрипти (див. кінець файлу).

Бренд: тепле світло кінопроєктора. Градієнт по діагоналі бурштин `#FFC94D` → помаранч `#FF8A2A` →
полум'я `#F2511B`. Решта кольорів: ink `#0A0705`, card `#16100C`, paper `#FFF6EC`.

## Іконки атласу — `all/icons/` (60 PNG)

Джерело для атласу `all` (TexturePacker GUI або `gdx-tools`, як у Redwave: Trim *None*, padding ≥ 2).
У коді регіон береться за іменем файлу без `.png`.

| Що | Розмір | Примітка |
|---|---|---|
| 51 лінійна `ic_*` | 96×96 | сітка 24 × 4, лінія 8 px (2 одиниці), круглі кінці й з'єднання |
| `ic_play_fill`, `ic_pause_fill`, `ic_more` | 96×96 | залиті; у `ic_more` три кола r 1.6 |
| `ph_glyph` | 192×192 | заглушка обкладинки без мініатюри: `film`, лінія 16 px |

- Усі білі (#FFFFFF) на прозорому, зі згладженням. Колір задаємо в коді тонуванням
  (`actor.color`): білий множиться на будь-який колір.
- Перекладені імена з прототипу: `save` → `ic_download`, `cam` → `ic_camera`, `homeic` → `ic_home`,
  `note` → `ic_file`, `chev` → `ic_chev_right`, `back` → `ic_chev_left`, `up` → `ic_chev_up`,
  `rew`/`fwd` → `ic_rew10`/`ic_fwd10` (цифри «10» жирним sans-serif усередині).
- Нові, яких у прототипі нема: `ic_chev_down`, `ic_expand`, `ic_volume`, `ic_mute`, `ic_plus`
  (шляхи — у `tools/render_brand.mjs`, об'єкт `EXTRA`).
- Без змін скопійовані з Redwave (`Game Redwave/assets/all/icons/`): `ic_heart ic_ban ic_wifi_off
  ic_moon ic_timer ic_sliders`. У них RGB повністю прозорих пікселів чорний (у наших — білий);
  видимі пікселі однаково чисто білі.
- `tools/check_icons.sh` перевіряє розмір, білизну видимих пікселів і непорожню альфу та збирає
  контакт-лист на темному тлі.

## Лого й іконки Android — `textures/logo.png`, `brand/android-res/`

| Файл | Розмір | Що |
|---|---|---|
| `textures/logo.png` | 192×192 | `brand/logo.svg`: скруглений квадрат rx 14/48, прозорі кути (сплеш, онбординг) |
| `mipmap-*/ic_launcher_background.png` | 108/162/216/324/432 | градієнт на весь квадрат без скруглення; розтягнутий на видимі 72dp, тож кути після маски як у лого |
| `mipmap-*/ic_launcher_foreground.png` | ті самі | біла рамка-кадр, перфорація, трикутник «play ↓» і смужка градієнтом; решта прозора |
| `mipmap-*/ic_launcher_monochrome.png` | ті самі | той самий гліф одним білим кольором: рамка, а перфорація, трикутник і смужка — прозорі отвори |
| `mipmap-*/ic_launcher.png` | 48/72/96/144/192 | повне лого (скруглений квадрат) для Android ≤ 7.1 |
| `mipmap-*/ic_launcher_round.png` | ті самі | градієнтне коло з гліфом по центру |
| `drawable/ic_notification.xml` | 24dp | VectorDrawable, один білий шлях `evenOdd`: рамка мінус перфорація, трикутник і смужка |

- Адаптивна іконка: полотно 108dp, безпечне коло Ø66dp. Гліф масштабовано ×1.86 від лого: рамка
  ≈ 54×52dp, найдальша точка скругленого кута — 32.5dp від центру, тобто всередині безпечного кола.
  При рамці 60dp кути виходили б на 36.1dp, тобто за безпечне коло (у круглій масці — впритул до краю).
- Адаптивні XML (`mipmap-anydpi-v26`) уже є в проєкті, тут їх нема. Вміст `android-res/`
  копіюється в `app/src/main/res/` (старі `ic_launcher*` шаблону, особливо `.webp`, видалити).

## Текстури — `textures/`

| Файл | Розмір | Звідки |
|---|---|---|
| `onboarding/onboarding_1.jpg` | 720×720 | Sintel, 11:34 — героїня на краю скелі над горами |
| `onboarding/onboarding_2.jpg` | 720×720 | Tears of Steel, 1:44 — корабель над дахами Амстердама в прицілі (з 1080p MKV) |
| `onboarding/onboarding_3.jpg` | 720×720 | NASA Orion, 1:28 — серп Землі за бортом, без логотипа NASA |
| `discover/feat_sintel.jpg` | 720×405 | Sintel, 12:00 — героїня йде до гір, проліски на передньому плані |
| `launcher/launcher_bg.jpg` | 540×1170 | перший кадр петлі `video/bg_orion.mp4` |
| `thumbs/th_*.jpg` (9) | 480×270 | копії з `prototype/img/`: bbb, sintel, tos, ed, llama, moon, nosf, notld, orion |
| `fx/glow_radial.png`, `fx/fade_vertical.png` | 256×256, 4×256 | нейтральні білі маски з `Framefall/app/src/main/assets/textures/fx/` |

JPEG — `-q:v 3` (≈ якість 85).

## Відеофон головного екрана — `video/`

| Файл | Що |
|---|---|
| `bg_orion.mp4` | Orion з 1:22, кроп 19.5:9 зі зсувом 0.62; ≈ 1.07 МБ |
| `bg_sintel.mp4` | Sintel з 11:29, кроп 19.5:9 зі зсувом 0.70; ≈ 1.08 МБ |
| `bg_orion.jpg`, `bg_sintel.jpg` | постери — перший кадр, 540×1170 |

540×1170, 24 fps, H.264 Main, yuv420p, **без звукової доріжки**, `+faststart`, 10 с = 5 с уперед +
5 с навпаки («бумеранг»), тож петля без шва. CRF підбирається автоматично: найменший із 18…34, за якого
файл ≤ 1.2 МБ (зараз обидва вийшли на CRF 18). Кроп за висотою — 332 px (Orion) і 402 px (Sintel),
тож картинка трохи збільшена до 540 по ширині.

## Джерела й ліцензії

| Фільм | Автор | Ліцензія | Титр |
|---|---|---|---|
| Sintel (2010), Tears of Steel (2012), Big Buck Bunny (2008), Caminandes 3: Llamigos (2016) | Blender Foundation | CC BY 3.0 | © Blender Foundation \| www.blender.org |
| Elephants Dream (2006) | Orange Open Movie Project (Blender Foundation) | CC BY | © Orange Open Movie Project \| www.elephantsdream.org |
| A Trip to the Moon (1902, Méliès), Nosferatu (1922, Murnau), Night of the Living Dead (1968, Romero) | — | суспільне надбання | — |
| Earth from Orion, Artemis I (2022) | NASA | суспільне надбання (NASA imagery) | без логотипа NASA і без натяку на схвалення NASA |

Оригінали: Internet Archive (`archive.org/download/Sintel`, `Tears-of-Steel`, `ElephantsDream`,
`BigBuckBunny_124`, `CaminandesLlamigos`, `ATripToTheMoon20Fps`, `Nosferatu_DVD_quality`,
`night_of_the_living_dead_dvd`) і NASA Image Library (`jsc2022m000270`). Титри CC BY треба показати
в апці (екран «Про апку» / ліцензії) і в описі маркету, якщо кадри йдуть у скріншоти.

## Як перегенерувати — `tools/`

```sh
cd "poc/TEST_APP/Game Framefall"
node assets/tools/render_brand.mjs [--work /tmp/ff_render]   # іконки, лого, лаунчер, ic_notification.xml
sh   assets/tools/check_icons.sh [/tmp/ff_icons_sheet.png]   # перевірка + контакт-лист
sh   assets/tools/make_media.sh                              # кадри, відеофон, копії (ONLY=onboarding|feat|video|copy)
```

| Файл | Що робить |
|---|---|
| `render_brand.mjs` | Node 18+ без npm-залежностей. Витягує `P` з прототипу, складає всі SVG на один аркуш, робить один скріншот Google Chrome headless (`--default-background-color=00000000`), ріже аркуш ffmpeg-ом (`crop`, для білих — `lutrgb=255`). Копіює 6 іконок із Redwave. Пише `ic_notification.xml` |
| `check_icons.sh` | розмір, білизна, альфа; контакт-лист 10 × N на темному тлі |
| `make_media.sh` | ffmpeg з `-ss` перед `-i` (по HTTP читає тільки потрібне), до 5 повторів на запит (archive.org інколи відповідає 5XX). Для MKV — передкрутка на 15 с, інакше перший кадр зелений |

Потрібні `ffmpeg`/`ffprobe`, Google Chrome (`/Applications/Google Chrome.app`), інтернет.
