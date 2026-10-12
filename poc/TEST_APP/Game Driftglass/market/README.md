# Driftglass — графіка для Google Play

Що вписати в Play Console (назва, описи, категорія, Data safety, рейтинг) — [LISTING.md](LISTING.md).

Зроблено 12.10.2026. Усі файли — PNG без альфа-каналу (RGB), саме так їх приймає Play Console.

| Файл | Розмір | Поле в Play Console |
|---|---|---|
| `icon_512.png` | 512×512 | App icon |
| `feature_1024x500.png` | 1024×500 | Feature graphic |
| `screenshot_01.png` … `screenshot_06.png` | 1080×1920 (9:16) | Phone screenshots |
| `raw/*.png` | 720×1650 | джерело кадрів — скріни з Redmi (не завантажувати в Play) |

## Звідки кадри

На відміну від Redwave (кадри з HTML-прототипу), тут — **справжні екрани апки з телефона**, бо лаунчер з папками
вже відрізняється від прототипу. Щоб у кадр не потрапили апки людини, debug-збірка має режим **StoreDemo**
(`Driftglass/app/src/main/java/com/driftglass/home/android/StoreDemo.kt`):

- замість апок телефона — 30 вигаданих із загальними назвами (Calculator, Music, Shop…) і док Phone / Messages / Browser / Camera;
- іконки — наші: гліфи **Material Symbols Rounded** (Google, Apache 2.0) на нашому градієнті, PNG лише в debug-асетах
  (`app/src/debug/assets/demo_icons`), у release їх немає;
- годинник лаунчера й перегляду — 9:41.

Статус-бар телефона (сповіщення людини) й навігацію `compose_shots.py` зрізає, зверху малює чистий «9:41».

## Як перезняти

```bash
export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"
cd "Game Driftglass/Driftglass" && sh ./gradlew assembleDebug && adb install -r -t app/build/outputs/apk/debug/app-debug.apk
adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME -n com.driftglass.home/.MainActivity --ez driftglass.store_demo true
# пройти екрани й зняти: adb exec-out screencap -p > ../market/raw/01_home.png  (02_discover, 03_preview, 04_studio, 05_my_glass, 06_folder)
adb shell am start -n com.driftglass.home/.MainActivity --ez driftglass.store_demo false    # вимкнути
cd ../market/tools
python3 -m venv /tmp/dg-venv && /tmp/dg-venv/bin/pip install pillow
/tmp/dg-venv/bin/python compose_shots.py      # screenshot_01…06
/tmp/dg-venv/bin/python render_feature.py     # feature_1024x500 + icon_512
python3 render_demo_icons.py                  # лише якщо міняли набір демо-апок
```

Шаблон кадру: темний фон з бірюзовим, фіолетовим і рожевим світлом (кольори лого), заголовок Geologica ExtraBold
82 px, друга половина — градієнт #5FF2D1 → #7BB8FF → #B48CFF, підзаголовок Geologica Light 34 px, телефон 677×1440.

## Скріншоти

| Файл | Заголовок | Підзаголовок | Екран |
|---|---|---|---|
| `screenshot_01.png` | Your home screen, *alive.* | Living wallpapers drawn in real time behind your apps | Лаунчер: Northern Hush, папки, док |
| `screenshot_02.png` | Wallpapers that *never repeat.* | 24 originals: aurora, liquid, waves, glass, gradient | Discover |
| `screenshot_03.png` | See it *before you set it.* | Preview with your clock and icons, apply in one tap | Preview: Northern Hush з «привидом» головного екрана |
| `screenshot_04.png` | Make one that's *yours.* | Pick a style and palette, tune motion and grain | Studio: Liquid + Orchid |
| `screenshot_05.png` | Shifts with *your day.* | Dawn, day, dusk and night, or a new one daily | My Glass: Auto-shuffle Daily, Day cycle увімкнено |
| `screenshot_06.png` | Tidy by *design.* | Apps sort into folders. Swipe up for all apps | Лаунчер: відкрита папка Tools |

*Курсив* — друга половина заголовка, у кадрі вона градієнтна.

## Іконка й банер

- `icon_512.png` — шари адаптивної іконки (`assets/brand/ic_launcher_background.svg` + `_foreground.svg`) на повний квадрат.
  Скруглення й тінь не малюємо: Play сам накладає маску.
- `feature_1024x500.png` — лого, «Driftglass», «LIVE HOME SCREEN» і три кадри з `raw/` (Discover, головний екран, Studio).
  Іншого тексту чи обіцянок на банері немає.
