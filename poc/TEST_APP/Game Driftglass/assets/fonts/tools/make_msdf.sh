#!/bin/sh
# MTSDF-атласи у форматі T35 (як у Redwave: mtsdf, size 48, pxrange 32, yorigin top) з ../ttf/*.ttf.
#   python3 make_instances.py "../src/Geologica[CRSV,SHRP,slnt,wght].ttf" ../ttf   (потрібен fonttools)
#   sh make_msdf.sh                                           (потрібен msdf-atlas-gen у PATH)
# Onest і JetBrains Mono — готові з «Game Redwave/assets/fonts/msdf» (той самий формат).
set -eu
HERE=$(cd "$(dirname "$0")" && pwd)
OUT="$HERE/../msdf"; CHARSET="$HERE/../charset.txt"
mkdir -p "$OUT"
for ttf in "$HERE"/../ttf/*.ttf; do
    name=$(basename "$ttf" .ttf); echo "── $name"
    msdf-atlas-gen -font "$ttf" -charset "$CHARSET" -type mtsdf -size 48 -pxrange 32 -yorigin top -square4 \
        -format png -imageout "$OUT/$name.png" -json "$OUT/$name.json"
done
