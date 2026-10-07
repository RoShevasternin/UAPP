# Шрифти Redwave — MSDF у форматі T35

Готові MTSDF-атласи для майбутньої LibGDX-апки **Redwave: Music Downloader**.
Формат **той самий, що в донора** `agust/Game T35/Mindora Self Test/app/src/main/assets/font/msdf/`,
тож `MsdfFont` / `MsdfManager` / `AMsdfLabel` із T35 читають їх без жодних змін.

Згенеровано 06.10.2026, msdf-atlas-gen v1.4.0 (msdfgen v1.13.0), fontTools 4.66.1.

## Ролі

| Роль | Шрифт | Файли | Що ним малюємо |
|---|---|---|---|
| Display | Archivo Expanded ExtraBold, wdth 118 / wght 800 | `Archivo-ExpandedExtraBold` | **лише фіксовані англійські заголовки** з коду («What are we downloading?») |
| Годинник лаунчера | Archivo Expanded Bold, wdth 105 / wght 700 | `Archivo-ExpandedBold` | цифри годинника / дати |
| UI / основний текст | Onest 400 / 500 / 600 / 700 | `Onest-Regular` / `-Medium` / `-SemiBold` / `-Bold` | **увесь динамічний текст**: назви треків, артисти, назви подкастів, імена файлів, кнопки, описи |
| Моно | JetBrains Mono 400 / 600 | `JetBrainsMono-Regular` / `-SemiBold` | таймери, розміри файлів, швидкість, технічні мітки |

> ⚠️ **Ніколи не малювати Archivo текст від користувача** — теги ID3, імена файлів,
> назви з RSS, вміст буфера. В Archivo **немає кирилиці** (і `₴`): українська чи російська назва
> треку просто зникне — `MsdfFont` не має fallback, відсутній гліф не малюється взагалі.
> Усе, що приходить ззовні, — тільки **Onest**.

Прототип (`prototype/index.html`) і далі використовує Figtree для UI — це нормально, в апку
Figtree не постачаємо: у нього теж немає кирилиці.

## Що де лежить

```
fonts/
├── charset.txt        набір символів (синтаксис msdf-atlas-gen)
├── msdf/              <Name>.json + <Name>.png — ЦЕ копіюємо в апку
├── ttf/               статичні інстанси .ttf + OFL-Archivo.txt, OFL-Onest.txt, OFL-JetBrainsMono.txt
└── tools/             скрипти, якими все зроблено (Linux + macOS)
```

## Як підключити в апку

1. Скопіювати **вміст `msdf/`** у `app/src/main/assets/font/msdf/` (16 файлів).
   `.ttf` в APK не потрібні — рантайм бере лише JSON + PNG.
2. Зареєструвати в `MsdfManager` так само, як у T35:

```kotlin
    // ── Display: Archivo Expanded — ЛИШЕ фіксовані англійські рядки, без кирилиці! ────────
    val fontArchivo_ExpandedExtraBold = MsdfFont(
        "font/msdf/Archivo-ExpandedExtraBold.json",
        "font/msdf/Archivo-ExpandedExtraBold.png",
    )
    val fontArchivo_ExpandedBold = MsdfFont(
        "font/msdf/Archivo-ExpandedBold.json",
        "font/msdf/Archivo-ExpandedBold.png",
    )

    // ── UI / body: Onest — увесь динамічний текст (ID3, файли, RSS) ────────
    val fontOnest_Regular = MsdfFont(
        "font/msdf/Onest-Regular.json",
        "font/msdf/Onest-Regular.png",
    )
    val fontOnest_Medium = MsdfFont(
        "font/msdf/Onest-Medium.json",
        "font/msdf/Onest-Medium.png",
    )
    val fontOnest_SemiBold = MsdfFont(
        "font/msdf/Onest-SemiBold.json",
        "font/msdf/Onest-SemiBold.png",
    )
    val fontOnest_Bold = MsdfFont(
        "font/msdf/Onest-Bold.json",
        "font/msdf/Onest-Bold.png",
    )

    // ── Mono: JetBrains Mono — таймери, розміри, мітки ────────
    val fontJetBrainsMono_Regular = MsdfFont(
        "font/msdf/JetBrainsMono-Regular.json",
        "font/msdf/JetBrainsMono-Regular.png",
    )
    val fontJetBrainsMono_SemiBold = MsdfFont(
        "font/msdf/JetBrainsMono-SemiBold.json",
        "font/msdf/JetBrainsMono-SemiBold.png",
    )

    override fun dispose() {
        disposeAll(
            fillShader,
            strokeShader,
            shadowShader,
            innerShader,

            fontArchivo_ExpandedExtraBold,
            fontArchivo_ExpandedBold,

            fontOnest_Regular,
            fontOnest_Medium,
            fontOnest_SemiBold,
            fontOnest_Bold,

            fontJetBrainsMono_Regular,
            fontJetBrainsMono_SemiBold,
        )
    }
```

3. Ліцензія: OFL 1.1 вимагає, щоб текст ліцензії йшов разом зі шрифтом. Атласи — похідні
   від шрифтів, тож тексти `ttf/OFL-*.txt` варто покласти в апку (екран «Open-source licenses»
   або `assets/licenses/`). Reserved Font Name у жодному з трьох OFL не заявлено, тому назви
   інстансів «Archivo Expanded …», «Onest …» тощо дозволені.

## Атласи

Усі: `type mtsdf`, `distanceRange 32`, `distanceRangeMiddle 0`, `size 48`, `yOrigin top`,
PNG 8-bit RGBA, квадрат зі стороною, кратною 4 (як у T35: 640, 856, 868, 884, 888).

| Шрифт | Атлас, px | Гліфів | PNG, B | JSON, B | TTF, B | lineHeight | ascender | descender |
|---|---|---|---|---|---|---|---|---|
| Archivo-ExpandedExtraBold | 968×968 | 210 | 606 630 | 48 757 | 121 196 | 1.088 | −0.878 | 0.21 |
| Archivo-ExpandedBold | 912×912 | 210 | 571 258 | 48 538 | 121 064 | 1.088 | −0.878 | 0.21 |
| Onest-Regular | 1088×1088 | 300 | 735 026 | 68 952 | 98 268 | 1.275 | −0.97 | 0.305 |
| Onest-Medium | 1108×1108 | 300 | 823 814 | 69 303 | 98 536 | 1.275 | −0.97 | 0.305 |
| Onest-SemiBold | 1104×1104 | 300 | 818 565 | 69 317 | 98 556 | 1.275 | −0.97 | 0.305 |
| Onest-Bold | 1128×1128 | 300 | 759 053 | 68 895 | 98 516 | 1.275 | −0.97 | 0.305 |
| JetBrainsMono-Regular | 1084×1084 | 303 | 671 239 | 70 408 | 115 132 | 1.32 | −1.02 | 0.3 |
| JetBrainsMono-SemiBold | 1092×1092 | 303 | 657 309 | 70 491 | 115 144 | 1.32 | −1.02 | 0.3 |

Разом `msdf/` ≈ 6.0 MB, `ttf/` ≈ 0.9 MB.

## Покриття charset (310 кодів)

| Шрифт | Чого немає |
|---|---|
| Archivo (обидва) | **уся кирилиця** U+0400–045F, Ґ ґ (U+0490/0491), `₴` U+20B4, `✓` U+2713 — 100 кодів |
| Onest (усі 4) | `¤ ¦ ¬ µ` (U+00A4/A6/AC/B5), м'який перенос U+00AD (невидимий), `‑` U+2011, рідкісні Ѐ Ѝ ѐ ѝ — 10 кодів |
| JetBrains Mono (обидва) | `₴` U+20B4, `✓` U+2713, `‑` U+2011, Ѐ Ѝ ѐ ѝ — 7 кодів |

- ✔ Onest має всю українську абетку (`я ґ є ї і`, перевірено в JSON), апостроф `ʼ` U+02BC,
  `₴ „ ‰ № ✓ € ™ ← → ↑ ↓`.
- ✔ Ѐ Ѝ ѐ ѝ в українській і російській не вживаються — пропуск не важливий.
- ◌ Ціни з `₴` малювати Onest, не JetBrains Mono (у моно гривні немає).
- ◌ Нерозривний дефіс U+2011 є лише в Archivo; для Onest/моно у рядках краще звичайний `-`.

## Відповідність формату T35

Перевірено `tools/verify_msdf.py` проти `T35/.../Montserrat-Bold.json`:

- ✔ ключі верхнього рівня `atlas, metrics, glyphs, kerning` — у тому ж порядку;
- ✔ `atlas`: `type, distanceRange, distanceRangeMiddle, size, width, height, yOrigin`;
- ✔ `metrics`: `emSize, lineHeight, ascender, descender, underlineY, underlineThickness`;
- ✔ гліф: `unicode, advance` (+ `planeBounds{left,top,right,bottom}` і `atlasBounds{…}`
  у видимих), `atlasBounds` на пів-пікселях, гліфи впорядковані за `unicode`;
- ✔ `atlas.width/height` = розміру PNG; PNG 8-bit RGBA; усі коди ≤ U+FFFF (`MsdfFont` бере 0..65535);
- ✔ `kerning: []` — як у T35. msdf-atlas-gen бере кернінг через FreeType лише з таблиці `kern`,
  а в цих шрифтах (як і в Montserrat/Karla з T35) кернінг лише в GPOS. ◌ Для великих заголовків
  Archivo (пари `Wa`, `Te`, `av`) це може бути помітно; лікується конвертацією GPOS → `kern`
  перед генерацією — зараз не робилося, щоб лишитися 1:1 з T35.

Візуально перевірено (`tools/preview.py`, рядок
`Redwave — What are we downloading? 0:24 Привіт ґ є ї пʼять ₴ „9‰“`): текст, зібраний з атласу
(median(r,g,b) ≥ 0.5), збігається з рендером `.ttf`; Archivo Expanded ExtraBold помітно ширший
і важчий за Bold; в Onest уся кирилиця, `ʼ ₴ „ ‰` на місці.

## Набір символів (`charset.txt`)

```
[0x20, 0x7E],                       ASCII
[0xA0, 0xFF],                       Latin-1
[0x400, 0x45F], 0x490, 0x491,       кирилиця + Ґ ґ
0x2018 0x2019 0x201C 0x201D         ‘ ’ “ ”
0x2022 0x2026 0x2013 0x2014 0x2011  • … – — ‑
0x20AC 0x20B4 0x2116 0x2122         € ₴ № ™
0x201E 0x2030 0x02BC                „ ‰ ʼ (український апостроф)
0x2190 0x2192 0x2191 0x2193         ← → ↑ ↓
0x2713                              ✓
```
Разом 310 кодів; чого немає у шрифті — msdf-atlas-gen пропускає («Missing N codepoints»).

## Звідки шрифти

`https://raw.githubusercontent.com/google/fonts/main/ofl/` (github.com через проксі закритий,
raw — відкритий). Усі — SIL OFL 1.1.

| Файл | sha256 (завантажено 06.10.2026) | Версія |
|---|---|---|
| `archivo/Archivo[wdth,wght].ttf` | `0e094a7d3c7c4c25cf1310c4b30014f1dae9332220b1c2c88f4fa996f0b05053` | 2.001 |
| `onest/Onest[wght].ttf` | `966c5c29b4755da84b6854d5c21dd4eaa2420225d0e9874de602de176d4a9f31` | 2.001 |
| `jetbrainsmono/JetBrainsMono[wght].ttf` | `48715a42ec242c21e9f02692891e147d022299a52e48d5e413e1a942193ffeda` | 2.211 |

Статичні інстанси — `fontTools.varLib.instancer` (координати user-space: `wdth=118` = CSS
`font-stretch:118%`). Імена в таблиці `name` виставлені вручну (ID1/2 — RIBBI, ID16/17 —
типографічні, ID6 = ім'я файла), бо 118 / 105 — не іменовані ширини в STAT і
`updateFontNames=True` дає невизначені імена. STAT і DSIG видалені, `head.modified`
зафіксовано (`SOURCE_DATE_EPOCH`) — повторний запуск дає байт-у-байт ті самі `.ttf`.

## Як перегенерувати

Одним запуском (Linux або macOS):

```sh
cd "poc/TEST_APP/Game Redwave/assets/fonts/tools"
sh build_all.sh
```

Він: качає шрифти → venv з `fonttools pillow` → **видаляє старі** `ttf/*.ttf`, `ttf/OFL-*.txt`,
`msdf/*.json`, `msdf/*.png` → інстанси в `../ttf` → якщо немає `msdf-atlas-gen` у PATH /
`$MSDF_ATLAS_GEN` — збирає його → атласи в `../msdf` → перевірка → прев'ю в
`$TMPDIR/redwave-fonts/preview.png`. Повторний запуск дає ті самі файли байт-у-байт.
`verify_msdf.py` завершиться з WARN для Archivo («немає гліфів: я ґ є ї і») — це очікувано.

Покроково:

```sh
W=${TMPDIR:-/tmp}/redwave-fonts
sh fetch_fonts.sh "$W/src"
python3 -m venv "$W/venv" && "$W/venv/bin/pip" install fonttools pillow
"$W/venv/bin/python" make_instances.py "$W/src" ../ttf
cp "$W/src"/OFL-Archivo.txt "$W/src"/OFL-Onest.txt "$W/src"/OFL-JetBrainsMono.txt ../ttf/
sh build_msdf_atlas_gen.sh "$W"                       # → $W/msdf-atlas-gen-build/bin/msdf-atlas-gen
MSDF_ATLAS_GEN="$W/msdf-atlas-gen-build/bin/msdf-atlas-gen" sh make_msdf.sh ../ttf ../msdf
python3 verify_msdf.py ../msdf "<T35>/app/src/main/assets/font/msdf/Montserrat-Bold.json"
"$W/venv/bin/python" preview.py ../ttf ../msdf "$W/preview.png"
```

Сама команда генерації одного шрифта (так само, як у T35):

```sh
msdf-atlas-gen -font Onest-Regular.ttf -charset charset.txt \
  -type mtsdf -size 48 -pxrange 32 -yorigin top -square4 \
  -format png -imageout Onest-Regular.png -json Onest-Regular.json
```

`-square4` — це й так дефолт msdf-atlas-gen (квадрат, сторона кратна 4); вказано явно.

Додати шрифт: рядок у `INSTANCES` у `make_instances.py` (+ `fetch` у `fetch_fonts.sh` і OFL
у списку в `build_all.sh`), далі `sh build_all.sh`.

### Збірка msdf-atlas-gen

`build_msdf_atlas_gen.sh` клонує `github.com/Chlumsky/msdf-atlas-gen` на коміті
`6148900d` (v1.4.0, сабмодуль msdfgen `84f183a6`) і збирає CMake без vcpkg / Skia /
Artery Font / SVG:

```
-DMSDF_ATLAS_USE_VCPKG=OFF -DMSDF_ATLAS_USE_SKIA=OFF -DMSDF_ATLAS_NO_ARTERY_FONT=ON
-DMSDF_ATLAS_MSDFGEN_EXTERNAL=OFF -DMSDFGEN_USE_VCPKG=OFF -DMSDFGEN_USE_SKIA=OFF
-DMSDFGEN_DISABLE_SVG=ON -DMSDFGEN_USE_OPENMP=OFF -DMSDFGEN_BUILD_STANDALONE=OFF
```

- Linux: `cmake g++ make libfreetype-dev libpng-dev`.
- macOS: Xcode CLT + `brew install cmake freetype libpng`; скрипт сам додає
  `CMAKE_PREFIX_PATH=$(brew --prefix)`. **Бінарник під macOS треба зібрати окремо** —
  linux-збірка там не запуститься.

| Скрипт | Що робить |
|---|---|
| `tools/build_all.sh` | увесь цикл одним запуском (з очищенням старих результатів) |
| `tools/fetch_fonts.sh` | качає VF + OFL з raw.githubusercontent.com |
| `tools/make_instances.py` | статичні інстанси + імена (fontTools) |
| `tools/build_msdf_atlas_gen.sh` | клон + збірка msdf-atlas-gen |
| `tools/make_msdf.sh` | атласи для всіх `.ttf` з прапорцями T35 |
| `tools/verify_msdf.py` | JSON парситься, атлас = PNG, параметри й структура = T35, є `A я ґ є ї і …` |
| `tools/preview.py` | прев'ю: `.ttf` поруч із текстом, зібраним з атласу |
