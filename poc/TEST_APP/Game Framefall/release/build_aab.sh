#!/bin/sh
# Підписаний AAB для Google Play: sh release/build_aab.sh  (з теки Game Framefall)
# Ключ — release/keyFramefall.jks, alias і пароль — release/meta-data.txt. Результат — market/framefall v<versionCode>.aab
set -e
HERE=$(cd "$(dirname "$0")/.." && pwd)
cd "$HERE/Framefall"
sh ./gradlew :app:testDebugUnitTest :app:bundleRelease -q
VC=$(grep -E "versionCode *=" app/build.gradle.kts | grep -oE "[0-9]+" | head -1)
ALIAS=$(grep '^alias:' "$HERE/release/meta-data.txt" | sed 's/^alias: *//')
PASS=$(grep '^pass:' "$HERE/release/meta-data.txt" | sed 's/^pass: *//')
OUT="$HERE/market/framefall v$VC.aab"
cp app/build/outputs/bundle/release/app-release.aab "$OUT"
jarsigner -keystore "$HERE/release/keyFramefall.jks" -storepass "$PASS" -keypass "$PASS" "$OUT" "$ALIAS" >/dev/null
jarsigner -verify "$OUT" | grep -q "jar verified" && echo "OK → $OUT"
keytool -printcert -jarfile "$OUT" | grep "SHA256:" | head -1
