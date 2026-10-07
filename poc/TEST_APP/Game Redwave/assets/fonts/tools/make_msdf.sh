#!/bin/sh
# Генерує MTSDF-атласи (PNG + JSON) для всіх .ttf — у форматі донора T35
# (agust/Game T35/Mindora Self Test/app/src/main/assets/font/msdf):
#   atlas.type "mtsdf", distanceRange 32, size 48, yOrigin "top",
#   квадратний атлас зі стороною, кратною 4 (-square4 — дефолт msdf-atlas-gen).
#
#   MSDF_ATLAS_GEN=/шлях/до/msdf-atlas-gen sh make_msdf.sh [TTF_DIR] [OUT_DIR]
#
# За замовчуванням: TTF_DIR = ../ttf, OUT_DIR = ../msdf, charset = ../charset.txt.
# Гліфи, яких немає у шрифті, msdf-atlas-gen пропускає (друкує «Missing N codepoints»).
set -eu

HERE=$(cd "$(dirname "$0")" && pwd)
TTF_DIR=${1:-$HERE/../ttf}
OUT_DIR=${2:-$HERE/../msdf}
CHARSET=${CHARSET:-$HERE/../charset.txt}
GEN=${MSDF_ATLAS_GEN:-msdf-atlas-gen}
mkdir -p "$OUT_DIR"

for ttf in "$TTF_DIR"/*.ttf; do
    name=$(basename "$ttf" .ttf)
    echo "── $name"
    "$GEN" \
        -font "$ttf" \
        -charset "$CHARSET" \
        -type mtsdf \
        -size 48 \
        -pxrange 32 \
        -yorigin top \
        -square4 \
        -format png \
        -imageout "$OUT_DIR/$name.png" \
        -json "$OUT_DIR/$name.json"
done
