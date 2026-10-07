# Redwave — графіка бренду

Згенеровано 06.10.2026 з прототипу `prototype/index.html` скриптами з `tools/`.
Іконки, гліф-заглушка і лого читаються прямо з `<script>` прототипу (`P`, `F`, `GLYPH`,
`LOGO()`), тож після змін у прототипі досить перезапустити рендер (див. кінець файлу).

## Іконки атласу — `assets/all/icons/`

Джерело для атласу `all` (пакується в `app/src/main/assets/atlas/all.atlas` + `all.png`).

| Що | Розмір | Примітка |
|---|---|---|
| 35 лінійних `ic_*.png` | 96×96 | сітка 24 × 4, лінія 8 px, круглі кінці |
| 4 залиті `ic_play_fill`, `ic_pause_fill`, `ic_prev_fill`, `ic_next_fill` | 96×96 | |
| `ph_glyph.png` | 192×192 | гліф-заглушка обкладинки без фото; більший, бо в плеєрі обкладинка велика |

- Усі білі (#FFFFFF) на прозорому, зі згладженням. Колір задаємо в коді тонуванням
  (`actor.color` / `batch.color`): білий множиться на будь-який колір.
- Лінійні: `ic_home ic_compass ic_library ic_scissors ic_download ic_link ic_clip ic_search
  ic_check ic_x ic_sliders ic_moon ic_share ic_heart ic_more ic_bell ic_cloud ic_shield
  ic_chev_down ic_chev_right ic_chev_left ic_shuffle ic_repeat ic_wifi_off ic_timer ic_file
  ic_rss ic_globe ic_ban ic_phone ic_msg ic_camera ic_gear ic_image ic_folder`.
- Імена з прототипу перекладені в snake_case: `chevD` → `ic_chev_down`, `chevR` → `ic_chev_right`,
  `chevL` → `ic_chev_left`, `wifiOff` → `ic_wifi_off`, решта `ic_<ім'я>`.
- `ic_heart` лише контур. Залите серце в прототипі — той самий контур із `fill`, тож для
  нього знадобиться окремий спрайт, якщо він буде в апці.

## Вектор — `svg/` і `logo.svg`

- `svg/icons/*.svg` — ті самі іконки й `ph_glyph` у векторі 24×24 (білий stroke 2 або білий
  fill), щоб перерендерити в іншому розмірі. В Android Studio їх можна імпортувати як
  VectorDrawable (*New → Vector Asset → Local file*). Наприклад, маленька іконка сповіщення
  плеєра для Media3.
- `logo.svg` — лого 48×48: скруглений квадрат rx 14, діагональний градієнт #ff5468 → #c2001f,
  білі смуги й стрілка (лінія 3.4).
- `svg/logo_glyph_white.svg`, `svg/ic_launcher_background.svg`, `svg/ic_launcher_foreground.svg`,
  `svg/play_icon_512.svg` — вихідники для PNG нижче.

## Лого — PNG

| Файл | Розмір | Що |
|---|---|---|
| `logo_512.png` | 512×512 | лого, прозорі кути (скруглення rx 14/48) |
| `logo_192.png` | 192×192 | те саме |
| `logo_96.png` | 96×96 | те саме |
| `logo_48.png` | 48×48 | те саме |
| `logo_glyph_white_512.png` | 512×512 | лише білі смуги й стрілка на прозорому, гліф займає 80 % ширини |

Для сплеша чи онбордингу в LibGDX — окремою текстурою (`textures/`) або в атлас.
`logo_glyph_white_512` підходить, коли фон червоний.

## Іконка лаунчера Android — `android-res/`

Адаптивна іконка: полотно 108dp, видима після маски частина — центральні 72dp, безпечна
зона — коло Ø66dp.

| Тека | background / foreground / monochrome | ic_launcher / ic_launcher_round |
|---|---|---|
| `mipmap-mdpi` | 108×108 | 48×48 |
| `mipmap-hdpi` | 162×162 | 72×72 |
| `mipmap-xhdpi` | 216×216 | 96×96 |
| `mipmap-xxhdpi` | 324×324 | 144×144 |
| `mipmap-xxxhdpi` | 432×432 | 192×192 |
| `mipmap-anydpi-v26` | `ic_launcher.xml`, `ic_launcher_round.xml` | |

- `ic_launcher_background.png`: градієнт лого на весь квадрат, без скруглення. Градієнт
  розтягнутий на видимі 72dp, а не на все полотно 108dp, тож після маски кути мають ті самі
  кольори, що й лого.
- `ic_launcher_foreground.png`: білий гліф по центру, рамка ≈ 54×46dp. Найдальша точка
  гліфа на 28.4dp від центру, тобто всередині безпечного кола (радіус 33dp) із запасом
  ≈ 4.6dp. `finalize.py` перевіряє це на кожній щільності.
- `ic_launcher_monochrome.png`: той самий білий гліф. Його беруть тематичні іконки Android 13+,
  і від нього потрібна лише альфа.
- `ic_launcher.png` — лого (скруглений квадрат). `ic_launcher_round.png` — те саме лого,
  обрізане по колу. Обидва потрібні лише на Android 7.1 і старших.
- XML: `background` і `foreground` + `<monochrome>` посилаються на `@mipmap/…`.
  `<monochrome>` вимагає compileSdk ≥ 33. На старіших API Android цей тег ігнорує.

## Як скопіювати в Android-проєкт

1. **Іконка лаунчера.** Вміст `android-res/` (усі теки `mipmap-*`) скопіювати в
   `app/src/main/res/`. Можна й окремим набором, як у донорі T35: покласти в
   `app/src/main/res/launcher/` і в `app/build.gradle.kts` написати
   `res.directories += setOf("src/main/res", "src/main/res/launcher")`.
   - Перед копіюванням видалити старі `ic_launcher*` шаблону, особливо `.webp` з тими самими
     іменами, бо інакше збірка впаде з «Duplicate resources». `values/ic_launcher_background.xml`
     із кольором більше не потрібен: XML посилається на PNG-фон.
   - У маніфесті: `android:icon="@mipmap/ic_launcher"` і `android:roundIcon="@mipmap/ic_launcher_round"`.
2. **Іконки → атлас `all`.** `assets/all/icons/` — джерело для TexturePacker.
   - ⚠️ TexturePacker CLI у компанії працює без ліцензії. Будь-який запуск з командного рядка
     з `.tps` вшиває в спрайти червоні водяні знаки «please purchase a license». Тому атлас
     пакує VELDAN у **TexturePacker GUI**: формат libGDX, вихід
     `app/src/main/assets/atlas/all.atlas` + `all.png`. Радимо Trim mode *None*, щоб іконки
     лишились 96×96 і центрувались однаково, і Shape padding ≥ 2. Префікс підтеки в імені
     регіону (`icons/ic_home` чи `ic_home`) задає опція *Prepend folder name*.
   - Безкоштовна альтернатива — TexturePacker із LibGDX `gdx-tools`, він водяних знаків не
     ставить:
     `java -cp gdx.jar:gdx-tools.jar com.badlogic.gdx.tools.texturepacker.TexturePacker assets/all app/src/main/assets/atlas all`.
     Поруч у `assets/all/pack.json` варто покласти `{"paddingX":2,"paddingY":2,"duplicatePadding":true,"stripWhitespaceX":false,"stripWhitespaceY":false,"filterMin":"Linear","filterMag":"Linear","flattenPaths":true}`.
     `flattenPaths` прибирає з імен префікс `icons/`.
   - У коді регіон береться за іменем файлу без `.png`: `ic_home`, `ic_play_fill`, `ph_glyph`.

## Як перегенерувати — `tools/`

```
sh assets/brand/tools/run_all.sh [шлях/до/prototype/index.html] [тека Redwave]
```

Без аргументів скрипт бере `prototype/index.html` цієї апки й пише туди ж, де лежать ці файли.
Потрібні Node 18+, Playwright з Chromium (`npm i -g playwright && npx playwright install chromium`),
Python 3 з Pillow та інтернет (Google Fonts: Archivo, Figtree, JetBrains Mono).

| Файл | Що робить |
|---|---|
| `render_brand.js` | рендерить усе з прототипу. Параметри: `--proto`, `--out`, `--work`, `--chrome`, `--time`, `--only icons,logo,launcher,market,feature,shots` |
| `feature_graphic.html` | шаблон feature graphic 1024×500 для Google Play |
| `finalize.py` | знімає альфу з маркет-PNG, перевіряє розміри, білизну іконок і безпечну зону, збирає контакт-лист (`--sheet`) |
| `run_all.sh` | обидва кроки підряд |

Маркетингові файли (`market/`) описані в `market/README.md`.
