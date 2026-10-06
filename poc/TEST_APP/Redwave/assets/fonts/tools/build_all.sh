#!/bin/sh
# Повний цикл одним запуском:
#   завантажити VF → статичні інстанси → (зібрати msdf-atlas-gen, якщо нема) →
#   MTSDF-атласи → перевірка JSON/PNG → прев'ю.
#
#   sh build_all.sh
#
# Змінні (необов'язкові):
#   OUT=<тека>               куди писати ttf/ і msdf/   (за замовчуванням — тека fonts/, де лежить цей tools/;
#                            старі *.ttf, OFL-*.txt, *.json, *.png у них видаляються перед генерацією)
#   WORK=<тека>              тимчасова тека (вихідники, venv, збірка) — $TMPDIR/redwave-fonts
#   MSDF_ATLAS_GEN=<бінарник> готовий msdf-atlas-gen; інакше шукається в PATH, інакше збирається
#   T35_REF_JSON=<json>      еталон структури (за замовчуванням — Montserrat-Bold.json із T35, якщо репо поруч)
#
# Linux і macOS. На macOS бінарник msdf-atlas-gen збирається окремо (build_msdf_atlas_gen.sh
# зробить це сам; потрібні `brew install cmake freetype libpng`).
set -eu

HERE=$(cd "$(dirname "$0")" && pwd)
FONTS=$(cd "$HERE/.." && pwd)
OUT=${OUT:-$FONTS}
WORK=${WORK:-${TMPDIR:-/tmp}/redwave-fonts}
mkdir -p "$WORK" "$OUT/ttf" "$OUT/msdf"

py() { "$WORK/venv/bin/python" -I "$@"; }

echo "== 1/6 Завантаження шрифтів"
sh "$HERE/fetch_fonts.sh" "$WORK/src"

echo "== 2/6 venv (fonttools, pillow)"
if [ ! -x "$WORK/venv/bin/python" ]; then
    python3 -m venv "$WORK/venv"
    "$WORK/venv/bin/pip" install -q fonttools pillow
fi

echo "== 3/6 Статичні інстанси"
# Чистимо старі результати, щоб не лишилися атласи шрифтів, яких уже немає в наборі
rm -f "$OUT/ttf"/*.ttf "$OUT/ttf"/OFL-*.txt "$OUT/msdf"/*.json "$OUT/msdf"/*.png
py "$HERE/make_instances.py" "$WORK/src" "$OUT/ttf"
# Явний список: у кеші $WORK/src можуть лежати ліцензії шрифтів, які вже не постачаємо
for lic in OFL-Archivo.txt OFL-Onest.txt OFL-JetBrainsMono.txt; do
    cp "$WORK/src/$lic" "$OUT/ttf/"
done

echo "== 4/6 msdf-atlas-gen"
GEN=${MSDF_ATLAS_GEN:-}
if [ -z "$GEN" ]; then
    if command -v msdf-atlas-gen >/dev/null 2>&1; then
        GEN=$(command -v msdf-atlas-gen)
    else
        GEN="$WORK/msdf-atlas-gen-build/bin/msdf-atlas-gen"
        [ -x "$GEN" ] || sh "$HERE/build_msdf_atlas_gen.sh" "$WORK"
    fi
fi
echo "   $GEN"

echo "== 5/6 MTSDF-атласи"
MSDF_ATLAS_GEN="$GEN" sh "$HERE/make_msdf.sh" "$OUT/ttf" "$OUT/msdf"

echo "== 6/6 Перевірка + прев'ю"
REF=${T35_REF_JSON:-"$FONTS/../../../../../agust/Game T35/Mindora Self Test/app/src/main/assets/font/msdf/Montserrat-Bold.json"}
if [ -f "$REF" ]; then
    py "$HERE/verify_msdf.py" "$OUT/msdf" "$REF" || echo "   (WARN «немає гліфів: я ґ є ї і» для Archivo — очікувано: в Archivo немає кирилиці)"
else
    py "$HERE/verify_msdf.py" "$OUT/msdf" || echo "   (WARN «немає гліфів: я ґ є ї і» для Archivo — очікувано)"
fi
py "$HERE/preview.py" "$OUT/ttf" "$OUT/msdf" "$WORK/preview.png"
echo "Готово. Прев'ю: $WORK/preview.png"
