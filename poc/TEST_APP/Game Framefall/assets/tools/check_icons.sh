#!/bin/sh
# Перевірка іконок атласу: розмір, чистий білий RGB, альфа не порожня; контакт-лист на темному тлі.
#   sh assets/tools/check_icons.sh [вихідний контакт-лист.png]
# Друкує по рядку на файл: ім'я  розмір  чи білі видимі пікселі  max(alpha); «!!» — якщо щось не так.
cd "$(dirname "$0")/.." || exit 1
SHEET="${1:-/tmp/ff_icons_sheet.png}"
TMP="$(mktemp -d)"
stat1() { # файл площина → "min max"
  ffmpeg -nostdin -loglevel error -i "$1" -vf "format=rgba,extractplanes=$2,signalstats,metadata=print:file=-" -f null - \
    | awk -F= '/YMIN/{mn=$2}/YMAX/{mx=$2}END{print mn+0, mx+0}'; }
bad=0
for f in all/icons/*.png; do
  n=$(basename "$f" .png)
  wh=$(ffprobe -v error -select_streams v:0 -show_entries stream=width,height -of csv=s=x:p=0 "$f")
  want=96x96; [ "$n" = ph_glyph ] && want=192x192
  amax=$(stat1 "$f" a | cut -d' ' -f2)
  # білизна видимих пікселів: накладання на чорне до і після примусового білого RGB мають збігатися
  # (RGB повністю прозорих пікселів не важить — у копій з Redwave він чорний)
  psnr=$(ffmpeg -nostdin -i "$f" -filter_complex "color=black:s=$wh[k1];color=black:s=$wh[k2];[0]split[a][b];[b]format=rgba,lutrgb=r=255:g=255:b=255[w];[k1][a]overlay=format=rgb:shortest=1[o1];[k2][w]overlay=format=rgb:shortest=1[o2];[o1][o2]psnr" -frames:v 1 -f null - 2>&1 | sed -n 's/.*average:\([^ ]*\).*/\1/p')
  flag=""
  [ "$wh" != "$want" ] && flag="!! size"
  [ "$amax" -lt 255 ] && flag="$flag !! alpha"
  [ "$psnr" != inf ] && flag="$flag !! not-white"
  [ -n "$flag" ] && bad=$((bad+1))
  printf '%-16s %-8s white=%-4s amax=%-3s %s\n' "$n" "$wh" "$([ "$psnr" = inf ] && echo yes || echo no)" "$amax" "$flag"
  # клітинка 120×120 на темному тлі (ph_glyph зменшено до 96)
  ffmpeg -nostdin -loglevel error -y -f lavfi -i color=0x1a1410:s=120x120 -i "$f" \
    -filter_complex "[1]scale=96:96[i];[0][i]overlay=12:12" -frames:v 1 "$TMP/$n.png"
done
cnt=$(ls "$TMP"/*.png | wc -l | tr -d ' ')
cols=10; rows=$(( (cnt + cols - 1) / cols ))
ffmpeg -nostdin -loglevel error -y -pattern_type glob -framerate 1 -i "$TMP/*.png" \
  -vf "tile=${cols}x${rows}:color=0x1a1410" -frames:v 1 "$SHEET"
rm -rf "$TMP"
echo "файлів: $cnt, проблем: $bad, контакт-лист: $SHEET"
