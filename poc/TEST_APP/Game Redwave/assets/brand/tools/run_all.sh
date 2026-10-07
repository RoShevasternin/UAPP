#!/bin/sh
# Перегенерувати всю графіку Redwave з HTML-прототипу.
#   sh run_all.sh [шлях/до/prototype/index.html] [тека Redwave]
# Без аргументів бере ../../../prototype/index.html і пише в ../../.. (відносно цієї теки).
# Контакт-лист для огляду: $SHEET або ${TMPDIR:-/tmp}/redwave_contact_sheet.png; проміжні файли: $WORK (за замовчуванням тимчасова тека).
# Не задавай TMPDIR з дуже довгим шляхом: з ним Chromium падав (SIGTRAP), найімовірніше через задовгий шлях до сокета профілю.
set -e
HERE=$(cd "$(dirname "$0")" && pwd)
PROTO=${1:-$HERE/../../../prototype/index.html}
OUT=${2:-$HERE/../../..}
SHEET=${SHEET:-${TMPDIR:-/tmp}/redwave_contact_sheet.png}
node "$HERE/render_brand.js" --proto "$PROTO" --out "$OUT" ${WORK:+--work "$WORK"}
python3 "$HERE/finalize.py" --out "$OUT" --sheet "$SHEET"
