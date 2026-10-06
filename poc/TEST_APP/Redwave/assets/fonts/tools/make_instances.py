#!/usr/bin/env python3
"""
Статичні інстанси з варіативних шрифтів (fontTools.varLib.instancer).

    python3 make_instances.py <SRC_DIR> <OUT_DIR>

SRC_DIR — тека з Archivo[wdth,wght].ttf, Onest[wght].ttf, JetBrainsMono[wght].ttf
(див. fetch_fonts.sh). OUT_DIR — куди класти статичні .ttf.

Потрібен лише fonttools:  python3 -m venv .venv && .venv/bin/pip install fonttools

Імена в таблиці name виставляються вручну (а не updateFontNames=True): ширина
Archivo 118 / 105 не є іменованим значенням STAT, тож автоматичне перейменування
дає невизначені імена. Схема — стандартна для статичних сімей:
  ID1/ID2   — legacy-сім'я (RIBBI): «Onest Medium» + «Regular», «Onest» + «Bold»
  ID16/ID17 — типографічна сім'я/стиль: «Onest» + «Medium»
  ID4 повне ім'я, ID6 PostScript-ім'я = ім'я файла без .ttf
"""
import os
import sys
from pathlib import Path

from fontTools.ttLib import TTFont
from fontTools.varLib.instancer import instantiateVariableFont

# ім'я файла (= PostScript), джерело, осі (user-space), типогр. сім'я, типогр. стиль
INSTANCES = [
    ("Archivo-ExpandedExtraBold", "Archivo[wdth,wght].ttf", {"wdth": 118, "wght": 800}, "Archivo Expanded", "ExtraBold"),
    ("Archivo-ExpandedBold",      "Archivo[wdth,wght].ttf", {"wdth": 105, "wght": 700}, "Archivo Expanded", "Bold"),
    ("Onest-Regular",             "Onest[wght].ttf",         {"wght": 400},              "Onest",            "Regular"),
    ("Onest-Medium",              "Onest[wght].ttf",         {"wght": 500},              "Onest",            "Medium"),
    ("Onest-SemiBold",            "Onest[wght].ttf",         {"wght": 600},              "Onest",            "SemiBold"),
    ("Onest-Bold",                "Onest[wght].ttf",         {"wght": 700},              "Onest",            "Bold"),
    ("JetBrainsMono-Regular",     "JetBrainsMono[wght].ttf", {"wght": 400},              "JetBrains Mono",   "Regular"),
    ("JetBrainsMono-SemiBold",    "JetBrainsMono[wght].ttf", {"wght": 600},              "JetBrains Mono",   "SemiBold"),
]

RIBBI = {"Regular", "Bold"}


def set_names(font: TTFont, ps_name: str, family: str, style: str, axes: dict) -> None:
    name = font["name"]
    # Прибираємо старі записи імен 1-6, 16, 17, 21, 22, 25 (25 — VF PostScript prefix)
    for nid in (1, 2, 3, 4, 6, 16, 17, 21, 22, 25):
        name.removeNames(nameID=nid)

    if style in RIBBI:
        legacy_family, legacy_style = family, style
    else:
        legacy_family, legacy_style = f"{family} {style}", "Regular"
    full = f"{family} {style}"
    version = (name.getDebugName(5) or "Version 1.000").replace("Version ", "").split(";")[0]
    axes_str = ", ".join(f"{k}={v}" for k, v in axes.items())

    for nid, val in (
        (1, legacy_family),
        (2, legacy_style),
        (3, f"{version};REDWAVE;{ps_name}"),
        (4, full),
        (6, ps_name),
        (16, family),
        (17, style),
    ):
        name.setName(val, nid, 3, 1, 0x409)  # Windows, Unicode BMP, en-US
    # Mac-записи лишаємо мінімальні, щоб старі рушії теж бачили імена
    name.setName(legacy_family, 1, 1, 0, 0)
    name.setName(legacy_style, 2, 1, 0, 0)
    name.setName(full, 4, 1, 0, 0)
    name.setName(ps_name, 6, 1, 0, 0)

    # ID10 Description — звідки інстанс
    name.setName(f"Static instance of the variable font at {axes_str} (made with fontTools for Redwave).",
                 10, 3, 1, 0x409)

    # Стиль-біти: Bold → fsSelection.BOLD + macStyle.bold, інакше REGULAR
    os2, head = font["OS/2"], font["head"]
    sel = os2.fsSelection & ~((1 << 0) | (1 << 5) | (1 << 6))   # без italic/bold/regular
    if legacy_style == "Bold":
        sel |= 1 << 5
        head.macStyle = (head.macStyle & ~0b11) | 0b01
    else:
        sel |= 1 << 6
        head.macStyle = head.macStyle & ~0b11
    os2.fsSelection = sel


def main() -> int:
    if len(sys.argv) != 3:
        print(__doc__)
        return 2
    src, out = Path(sys.argv[1]), Path(sys.argv[2])
    # Фіксована дата в head.created/modified → повторний запуск дає байт-у-байт ті самі .ttf
    os.environ.setdefault("SOURCE_DATE_EPOCH", "1791244800")   # 2026-10-06 00:00 UTC
    out.mkdir(parents=True, exist_ok=True)

    for ps_name, src_file, axes, family, style in INSTANCES:
        vf = TTFont(src / src_file)
        font = instantiateVariableFont(vf, axes, inplace=False)
        for tag in ("STAT", "DSIG"):            # STAT посилається на VF-імена, DSIG вже невалідний
            if tag in font:
                del font[tag]
        set_names(font, ps_name, family, style, axes)
        font["name"].removeUnusedNames(font)     # хвости іменованих інстансів VF (ID 256+)
        dst = out / f"{ps_name}.ttf"
        font.save(dst)
        os2 = font["OS/2"]
        print(f"  {dst.name:34s} {axes}  usWeightClass={os2.usWeightClass} usWidthClass={os2.usWidthClass}"
              f"  glyphs={len(font.getGlyphOrder())}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
