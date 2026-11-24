#!/bin/sh
# Знімок прототипу без claude.ai: headless Chrome з тимчасовим профілем.
#   sh claude/tools/proto-shot.sh <екран> <out.png> [ширина висота]
# Екран — id з SCREENS у prototype/seciro.jsx (splash, onb1…onb6, paywall).
# Сторінку обгортаємо в doctype, як це робить claude.ai при публікації (інакше quirks mode).
set -e
HERE="$(cd "$(dirname "$0")/../.." && pwd)"
SCREEN="$1"; OUT="$2"; W="${3:-900}"; H="${4:-960}"
TMP="$(mktemp -d)"
{ printf '<!doctype html><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">'
  printf '<style>:root{color-scheme:light}body{margin:0;font:14px system-ui}img{max-width:100%%}[hidden]{display:none!important}</style></head><body>'
  cat "$HERE/prototype/seciro.html"; printf '</body></html>'; } > "$TMP/page.html"
"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" --headless=new --disable-gpu --hide-scrollbars \
  --user-data-dir="$TMP/profile" --window-size="$W,$H" --virtual-time-budget=5000 \
  --screenshot="$OUT" "file://$TMP/page.html#$SCREEN" >/dev/null 2>&1
rm -rf "$TMP"
echo "$OUT"
