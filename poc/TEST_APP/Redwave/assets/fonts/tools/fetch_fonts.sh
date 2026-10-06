#!/bin/sh
# Завантажує варіативні шрифти Archivo / Onest / JetBrains Mono (SIL OFL 1.1)
# з репозиторію google/fonts через raw.githubusercontent.com + їхні OFL.txt.
#
#   sh fetch_fonts.sh [SRC_DIR]          (за замовчуванням $TMPDIR/redwave-fonts/src)
#
# Працює на Linux і macOS (потрібен лише curl).
set -eu

SRC_DIR=${1:-${TMPDIR:-/tmp}/redwave-fonts/src}
BASE=https://raw.githubusercontent.com/google/fonts/main/ofl
mkdir -p "$SRC_DIR"

fetch() { # <url-шлях відносно BASE> <локальне ім'я>
    echo "  ↓ $2"
    curl -fsSL -o "$SRC_DIR/$2" "$BASE/$1"
}

# Дужки в іменах файлів кодуємо як %5B / %5D
fetch "archivo/Archivo%5Bwdth,wght%5D.ttf"         "Archivo[wdth,wght].ttf"
fetch "onest/Onest%5Bwght%5D.ttf"                   "Onest[wght].ttf"
fetch "jetbrainsmono/JetBrainsMono%5Bwght%5D.ttf"   "JetBrainsMono[wght].ttf"
fetch "archivo/OFL.txt"                              "OFL-Archivo.txt"
fetch "onest/OFL.txt"                                "OFL-Onest.txt"
fetch "jetbrainsmono/OFL.txt"                        "OFL-JetBrainsMono.txt"

echo "Готово → $SRC_DIR"
