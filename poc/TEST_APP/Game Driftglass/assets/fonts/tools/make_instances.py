#!/usr/bin/env python3
"""Статичні інстанси Geologica з варіативного шрифту (fontTools): Light 300, Bold 700, ExtraBold 800.
   python3 make_instances.py <Geologica-VF.ttf> <OUT_DIR>
msdf-atlas-gen з Homebrew ігнорує -varfont (заміряно 09.10.2026: три ваги давали той самий атлас),
тому спершу робимо статичні .ttf, а потім make_msdf.sh."""
import sys
from pathlib import Path
from fontTools.ttLib import TTFont
from fontTools.varLib.instancer import instantiateVariableFont

INST = [("Geologica-Light", 300), ("Geologica-Bold", 700), ("Geologica-ExtraBold", 800)]
src, out = Path(sys.argv[1]), Path(sys.argv[2])
out.mkdir(parents=True, exist_ok=True)
for name, w in INST:
    vf = TTFont(src)
    axes = {"wght": w}
    for a in vf["fvar"].axes:
        if a.axisTag != "wght":
            axes[a.axisTag] = a.defaultValue
    f = instantiateVariableFont(vf, axes, inplace=False)
    for tag in ("STAT", "DSIG"):
        if tag in f: del f[tag]
    f.save(out / f"{name}.ttf")
    print(name, axes)
