#!/bin/sh
# Збирає msdf-atlas-gen (Chlumsky) з вихідників — без vcpkg / Skia / Artery Font / SVG.
#
#   sh build_msdf_atlas_gen.sh [WORK_DIR]       (за замовчуванням $TMPDIR/redwave-fonts)
#   → бінарник: WORK_DIR/msdf-atlas-gen-build/bin/msdf-atlas-gen
#
# Залежності:
#   Linux:  cmake, g++, make, libfreetype-dev, libpng-dev (zlib тягнеться з libpng)
#   macOS:  Xcode Command Line Tools + `brew install cmake freetype libpng`
#           (бінарник під macOS треба зібрати окремо — linux-збірка там не запуститься)
#
# Версія зафіксована тим самим комітом, яким згенеровано атласи в ../msdf
# (msdf-atlas-gen v1.4.0 + msdfgen v1.13.0).
set -eu

WORK=${1:-${TMPDIR:-/tmp}/redwave-fonts}
REF=${MSDF_ATLAS_GEN_REF:-6148900d59423059bafde2f51a0cb303184404bd}
SRC="$WORK/msdf-atlas-gen"
BUILD="$WORK/msdf-atlas-gen-build"
mkdir -p "$WORK"

if [ ! -d "$SRC/.git" ]; then
    git clone https://github.com/Chlumsky/msdf-atlas-gen "$SRC"
fi
git -C "$SRC" checkout -q "$REF"
git -C "$SRC" submodule update --init --recursive

# Якщо git до github.com не пускає — запасний шлях (вручну):
#   curl -L -o mag.tgz https://codeload.github.com/Chlumsky/msdf-atlas-gen/tar.gz/<REF>
#   curl -L -o mg.tgz  https://codeload.github.com/Chlumsky/msdfgen/tar.gz/84f183a6c8137c42abc5728b29ed274bab3edeb0
#   розпакувати mag.tgz → $SRC, а mg.tgz → $SRC/msdfgen

EXTRA=""
if [ "$(uname)" = "Darwin" ] && command -v brew >/dev/null 2>&1; then
    EXTRA="-DCMAKE_PREFIX_PATH=$(brew --prefix)"
fi
JOBS=$( (nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4) | head -1)

# shellcheck disable=SC2086
cmake -S "$SRC" -B "$BUILD" -DCMAKE_BUILD_TYPE=Release $EXTRA \
    -DMSDF_ATLAS_USE_VCPKG=OFF \
    -DMSDF_ATLAS_USE_SKIA=OFF \
    -DMSDF_ATLAS_NO_ARTERY_FONT=ON \
    -DMSDF_ATLAS_MSDFGEN_EXTERNAL=OFF \
    -DMSDFGEN_USE_VCPKG=OFF \
    -DMSDFGEN_USE_SKIA=OFF \
    -DMSDFGEN_DISABLE_SVG=ON \
    -DMSDFGEN_USE_OPENMP=OFF \
    -DMSDFGEN_BUILD_STANDALONE=OFF
cmake --build "$BUILD" -j "$JOBS"

echo "Готово → $BUILD/bin/msdf-atlas-gen"
"$BUILD/bin/msdf-atlas-gen" -help 2>&1 | head -1
