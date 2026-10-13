#!/bin/sh
# Framefall — текстури й відеофон з оригіналів відкритих фільмів.
# ffmpeg читає з архівів по HTTP лише потрібні шматки (-ss перед -i), весь фільм не качає.
#   sh assets/tools/make_media.sh            # усе
#   ONLY=video sh assets/tools/make_media.sh # лише відео (onboarding|feat|video|copy)
# Потрібні ffmpeg/ffprobe та інтернет. archive.org іноді віддає 5XX — кожен запит повторюється до 5 разів.
set -e
cd "$(dirname "$0")/.."            # → assets/
A=$(pwd); FF=$(cd .. && pwd)
TMP=$(mktemp -d); trap 'rm -rf "$TMP"' EXIT
AR=https://archive.org/download
SINTEL="$AR/Sintel/sintel-2048-stereo.mp4"                     # 2048×872, 24 fps
TOS="$AR/Tears-of-Steel/tears_of_steel_1080p.mkv"               # 1920×800 (у *_720p.mp4 насправді лише 862×360)
ORION="http://images-assets.nasa.gov/video/jsc2022m000270_Orion_First_Imagery_Timelapse_and_Original_221117/jsc2022m000270_Orion_First_Imagery_Timelapse_and_Original_221117~large.mp4"
# Orion 1280×720: корисна картинка лише x 160…994 (cropdetect), по боках чорне. Логотипа NASA в кадрах нема.
MAXB=1200000                                                   # стеля розміру відеофону, байт
want() { [ -z "$ONLY" ] || [ "$ONLY" = "$1" ]; }
retry() { n=0; until "$@"; do n=$((n+1)); [ $n -ge 5 ] && return 1; echo "  повтор $n: $*" >&2; sleep 3; done; }
ff() { ffmpeg -nostdin -loglevel error -y "$@"; }

# кадр: url, секунда, фільтр, файл. Для MKV — передкрутка: -ss на 15 с раніше + 15 с декодування,
# бо пошук по HTTP у MKV потрапляє не на ключовий кадр і дає зелене сміття.
frame() { case "$1" in
  *.mkv) retry ff -ss "$(($2-15))" -i "$1" -ss 15 -frames:v 1 -vf "$3" -q:v 3 "$4" ;;
  *)     retry ff -ss "$2" -i "$1" -frames:v 1 -vf "$3" -q:v 3 "$4" ;; esac; }

if want onboarding; then
  mkdir -p textures/onboarding
  # 1 — Sintel на краю скелі над горами (кроп праворуч, де героїня)
  frame "$SINTEL" 694 "crop=872:872:1040:0,scale=720:720:flags=lanczos" textures/onboarding/onboarding_1.jpg
  # 2 — Tears of Steel: корабель над дахами Амстердама в прицілі
  frame "$TOS" 104 "crop=800:800:560:0,scale=720:720:flags=lanczos" textures/onboarding/onboarding_2.jpg
  # 3 — Orion: серп Землі за бортом (квадрат у правій частині корисної області)
  frame "$ORION" 88.5 "crop=672:672:318:0,scale=720:720:flags=lanczos" textures/onboarding/onboarding_3.jpg
fi

if want feat; then
  mkdir -p textures/discover
  # Sintel іде до гір, на передньому плані проліски; 2.35:1 → 16:9
  frame "$SINTEL" 720 "crop=1550:872:240:0,scale=720:405:flags=lanczos" textures/discover/feat_sintel.jpg
fi

# Вертикальна петля «вперед-назад» 540×1170 (19.5:9), 24 fps, 5 с + 5 с навпаки, без звуку.
# url, старт, зсув кропу 0..1, ім'я. Спершу якісний проміжний сегмент, потім найменший CRF (найкраща якість), що влазить у ≤ MAXB.
boom() {
  seg="$TMP/$4_seg.mp4"
  retry ff -ss "$2" -t 5 -i "$1" -an \
    -vf "crop=ih*9/19.5:ih:(iw-ih*9/19.5)*$3:0,scale=540:1170:flags=lanczos,fps=24,format=yuv420p" \
    -c:v libx264 -crf 8 -preset fast "$seg"
  for crf in 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34; do
    ff -i "$seg" -an -filter_complex "[0:v]split[a][b];[b]reverse[r];[a][r]concat=n=2:v=1:a=0,format=yuv420p" \
      -c:v libx264 -profile:v main -crf $crf -preset veryslow -g 48 -tag:v avc1 -movflags +faststart "video/$4.mp4"
    sz=$(wc -c < "video/$4.mp4" | tr -d ' ')
    [ "$sz" -le $MAXB ] && break
  done
  echo "  video/$4.mp4: CRF $crf, $sz байт"
  ff -i "video/$4.mp4" -frames:v 1 -q:v 3 "video/$4.jpg"     # постер — перший кадр петлі
}
if want video; then
  mkdir -p video textures/launcher
  boom "$ORION" 82 0.62 bg_orion
  boom "$SINTEL" 689 0.70 bg_sintel
  cp video/bg_orion.jpg textures/launcher/launcher_bg.jpg
fi

if want copy; then
  mkdir -p textures/thumbs textures/fx
  for n in bbb sintel tos ed llama moon nosf notld orion; do cp "$FF/prototype/img/th_$n.jpg" textures/thumbs/; done
  cp "$FF/Framefall/app/src/main/assets/textures/fx/glow_radial.png" "$FF/Framefall/app/src/main/assets/textures/fx/fade_vertical.png" textures/fx/
fi
echo готово
