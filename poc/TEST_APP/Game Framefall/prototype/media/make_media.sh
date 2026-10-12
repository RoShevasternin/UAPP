#!/bin/sh
# Відтворює медіа прототипу з оригіналів (ffmpeg читає потрібні шматки по HTTP, весь файл не качає).
# Запуск: sh make_media.sh   (у цій теці; кадри пише в ../img)
set -e
AR=https://archive.org/download
ORION="http://images-assets.nasa.gov/video/jsc2022m000270_Orion_First_Imagery_Timelapse_and_Original_221117/jsc2022m000270_Orion_First_Imagery_Timelapse_and_Original_221117~large.mp4"
SINTEL="$AR/Sintel/sintel-2048-stereo.mp4"
BBB="$AR/BigBuckBunny_124/Content/big_buck_bunny_720p_surround.mp4"

# вертикальна петля «вперед-назад» для фону головного екрана: url, старт, зсув кропу 0..1, файл
boom() { ffmpeg -nostdin -loglevel error -y -ss "$2" -t 5 -i "$1" -an -filter_complex \
  "[0:v]crop=ih*9/19.5:ih:(iw-ih*9/19.5)*$3:0,scale=432:936,fps=24,split[a][b];[b]reverse[r];[a][r]concat=n=2:v=1:a=0,format=yuv420p" \
  -c:v libx264 -crf 29 -preset slow -movflags +faststart "$4"; }
boom "$ORION" 82 0.62 bg_orion.mp4
boom "$SINTEL" 689 0.70 bg_sintel.mp4
ffmpeg -nostdin -loglevel error -y -ss 1 -i bg_orion.mp4 -frames:v 1 -q:v 4 ../img/bg_orion.jpg
ffmpeg -nostdin -loglevel error -y -ss 1 -i bg_sintel.mp4 -frames:v 1 -q:v 4 ../img/bg_sintel.jpg

# фрагмент для плеєра зі звуком
ffmpeg -nostdin -loglevel error -y -ss 113 -t 16 -i "$BBB" -vf "scale=640:360,fps=24,format=yuv420p" \
  -c:v libx264 -crf 27 -preset slow -c:a aac -b:a 96k -ac 2 -movflags +faststart clip_bbb.mp4
ffmpeg -nostdin -loglevel error -y -i clip_bbb.mp4 -vf "fps=12/16,scale=96:54,tile=12x1" -frames:v 1 -q:v 5 ../img/strip_bbb.jpg

# 10-секундні фрагменти, мініатюри 480×270 і смуги кадрів для Studio: назва, url, старт фрагмента, кадр мініатюри
clip() { ffmpeg -nostdin -loglevel error -y -ss "$3" -t 10 -i "$2" \
  -vf "scale=480:270:force_original_aspect_ratio=increase,crop=480:270,fps=24,format=yuv420p" \
  -c:v libx264 -crf 30 -preset slow -c:a aac -b:a 64k -ac 2 -movflags +faststart "v_$1.mp4"
  ffmpeg -nostdin -loglevel error -y -ss "$4" -i "$2" -frames:v 1 \
  -vf "scale=480:270:force_original_aspect_ratio=increase,crop=480:270" -q:v 4 "../img/th_$1.jpg"
  ffmpeg -nostdin -loglevel error -y -i "v_$1.mp4" -vf "fps=12/10,scale=80:45,tile=12x1" -frames:v 1 -q:v 5 "../img/strip_$1.jpg"; }
clip sintel "$SINTEL" 688 700
clip tos "$AR/Tears-of-Steel/tears_of_steel_720p.mp4" 25 30
clip ed "$AR/ElephantsDream/ed_hd.mp4" 325 330
clip llama "$AR/CaminandesLlamigos/Caminandes_%20Llamigos-1080p.mp4" 94 100
clip moon "$AR/ATripToTheMoon20Fps/TripToTheMoon20Fps.mp4" 415 420
clip orion "$ORION" 82 84
clip nosf "$AR/Nosferatu_DVD_quality/nosferatu-2of5.ogv" 698 700
clip notld "$AR/night_of_the_living_dead_dvd/VTS_01_1.mp4" 1598 1600
ffmpeg -nostdin -loglevel error -y -ss 120 -i "$BBB" -frames:v 1 \
  -vf "scale=480:270:force_original_aspect_ratio=increase,crop=480:270" -q:v 4 ../img/th_bbb.jpg
echo done
