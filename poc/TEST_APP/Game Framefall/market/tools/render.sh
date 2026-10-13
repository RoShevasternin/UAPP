#!/bin/sh
# Рендер графіки Google Play з market.html (headless Chrome) + ffmpeg: PNG без альфи, як просить Play Console.
#   sh market/tools/render.sh      (з теки Game Framefall)
# Сирі кадри з Redmi — market/raw/*.png (720×1600), підписи — масив SHOTS у market.html.
set -e
HERE=$(cd "$(dirname "$0")" && pwd); OUT="$HERE/.."
CHROME="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
PROFILE=$(mktemp -d)
shot() { "$CHROME" --headless=new --disable-gpu --hide-scrollbars --user-data-dir="$PROFILE" --allow-file-access-from-files \
  --force-device-scale-factor=1 --window-size="$2" --virtual-time-budget=3000 --screenshot="$3" "file://$HERE/market.html?$1" >/dev/null 2>&1; }
for i in 1 2 3 4 5 6; do
  shot "shot=$i" 1080,1920 "$PROFILE/s$i.png"
  ffmpeg -loglevel error -y -i "$PROFILE/s$i.png" -pix_fmt rgb24 "$OUT/screenshot_0$i.png"
done
shot "feature" 1024,500 "$PROFILE/f.png"
ffmpeg -loglevel error -y -i "$PROFILE/f.png" -pix_fmt rgb24 "$OUT/feature_1024x500.png"
rm -rf "$PROFILE"
echo "OK → $OUT/screenshot_01..06.png, feature_1024x500.png"
