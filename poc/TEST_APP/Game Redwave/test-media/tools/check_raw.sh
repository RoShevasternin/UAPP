#!/bin/sh
# After the merge to main: HEAD every test-media file on raw.githubusercontent.com and
# print status / Content-Type / Content-Length next to the local size and first bytes.
#   sh check_raw.sh
BASE="https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Game%20Redwave/test-media"
DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$DIR" || exit 1
for f in neon-heart.mp3 night-drive.mp3 stage-lights.mp3 needle-drop.mp3 blue-room.m4a into-the-smoke.flac \
         white-noise.ogg bass-theory.wav untitled_demo-track.mp3 podcast/indie-hour-ep112.mp3 \
         podcast/indie-hour-ep111.mp3 indie-hour.rss catalog.json covers/neon-heart.jpg no-such-file.mp3; do
  h=$(curl -sSI "$BASE/$f" | tr -d '\r')
  st=$(printf '%s\n' "$h" | awk 'toupper($1) ~ /^HTTP/ {s=$2} END {print s}')
  ct=$(printf '%s\n' "$h" | awk -F': ' 'tolower($1)=="content-type" {print $2}')
  cl=$(printf '%s\n' "$h" | awk -F': ' 'tolower($1)=="content-length" {print $2}')
  loc=$( [ -f "$f" ] && wc -c < "$f" | tr -d ' ' || echo "-")
  magic=$( [ -f "$f" ] && head -c 4 "$f" | od -A n -t x1 | tr -d ' \n' || echo "-")
  printf '%-30s %-4s %-32s len=%-9s local=%-9s magic=%s\n' "$f" "$st" "$ct" "$cl" "$loc" "$magic"
done
