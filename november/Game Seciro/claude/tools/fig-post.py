#!/usr/bin/env python3
# Після вивантаження ряду: важкі PNG (м'які градієнти, > 400 КБ) → JPEG на тлі кадру, посилання в JSON оновлюються.
#   python3 claude/tools/fig-post.py r4 [r5 ...]
import glob, json, os, sys
from PIL import Image
BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "figma")
for d in sys.argv[1:]:
    bg = (241, 244, 249)
    ren = {}
    for p in glob.glob(os.path.join(BASE, d, "*.png")):
        if os.path.getsize(p) <= 400_000: continue
        im = Image.open(p).convert("RGBA"); flat = Image.new("RGBA", im.size, bg + (255,)); flat.alpha_composite(im)
        q = p[:-4] + ".jpg"; flat.convert("RGB").save(q, quality=86, optimize=True, progressive=True)
        ren[f"{d}/{os.path.basename(p)}"] = f"{d}/{os.path.basename(q)}"
        print(os.path.basename(p), os.path.getsize(p) // 1024, "KB →", os.path.getsize(q) // 1024, "KB"); os.remove(p)
    for jf in glob.glob(os.path.join(BASE, d, "*.json")):
        s = open(jf).read(); s2 = s
        for a, b in ren.items(): s2 = s2.replace('"' + a + '"', '"' + b + '"')
        if s2 != s: open(jf, "w").write(s2)
