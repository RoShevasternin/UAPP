#!/usr/bin/env python3
"""Redwave — доводка й перевірка після render_brand.js.

    python3 finalize.py [--out <тека Redwave>] [--sheet <contact_sheet.png>]

1. market/*.png → RGB без альфи (Google Play не приймає прозорість в іконці, кадрах і feature graphic).
2. Скріншоти рівно 1080×1920: якщо рендер дав ±1 px — обрізає/доповнює, більше — помилка.
3. Перевіряє розміри всіх файлів, що іконки білі й не порожні, і що гліф foreground
   влазить у безпечне коло Ø66dp з запасом.
4. --sheet: збирає контакт-лист (підписи англійською — дефолтний шрифт PIL без кирилиці) (іконки, лого, шари лаунчера, Play-іконка, feature, кадри) для огляду.
Потрібен лише Pillow.
"""
import argparse, glob, math, os, sys
from PIL import Image, ImageDraw, ImageFont

ap = argparse.ArgumentParser()
ap.add_argument("--out", default=os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "../../..")))
ap.add_argument("--sheet", default=None)
a = ap.parse_args()
OUT = os.path.abspath(a.out)
J = lambda *p: os.path.join(OUT, *p)
ICONS, BRAND, RES, MARKET = J("assets/all/icons"), J("assets/brand"), J("assets/brand/android-res"), J("market")
BG = (10, 6, 7)
errors, notes = [], []

def err(m): errors.append(m); print("ERROR:", m)

# ---------- 1–2. маркет ----------
def flatten(path):
    im = Image.open(path)
    if im.mode in ("RGBA", "LA", "P"):
        im = im.convert("RGBA")
        lo = im.getchannel("A").getextrema()[0]
        if lo < 255: notes.append(f"{os.path.basename(path)}: була прозорість (min alpha {lo}) — підкладено #0a0607")
        flat = Image.new("RGB", im.size, BG); flat.paste(im, mask=im.getchannel("A")); im = flat
        im.save(path, optimize=True)
    return Image.open(path)

for f in sorted(glob.glob(os.path.join(MARKET, "*.png"))):
    flatten(f)

for i in range(1, 7):
    p = os.path.join(MARKET, f"screenshot_{i:02d}.png")
    if not os.path.exists(p): err(f"немає {p}"); continue
    im = Image.open(p)
    if im.size != (1080, 1920):
        dw, dh = 1080 - im.size[0], 1920 - im.size[1]
        if max(abs(dw), abs(dh)) > 1: err(f"{p}: {im.size}, а треба 1080×1920"); continue
        fixed = Image.new("RGB", (1080, 1920), im.getpixel((0, 0)))
        fixed.paste(im.crop((0, 0, min(1080, im.size[0]), min(1920, im.size[1]))), (0, 0))
        fixed.save(p, optimize=True); notes.append(f"{os.path.basename(p)}: {im.size} → 1080×1920")

# ---------- 3. розміри ----------
expect = {os.path.join(MARKET, "icon_512.png"): (512, 512), os.path.join(MARKET, "feature_1024x500.png"): (1024, 500)}
expect.update({os.path.join(MARKET, f"screenshot_{i:02d}.png"): (1080, 1920) for i in range(1, 7)})
for s in (512, 192, 96, 48): expect[os.path.join(BRAND, f"logo_{s}.png")] = (s, s)
expect[os.path.join(BRAND, "logo_glyph_white_512.png")] = (512, 512)
DENS = {"mdpi": 1, "hdpi": 1.5, "xhdpi": 2, "xxhdpi": 3, "xxxhdpi": 4}
for d, k in DENS.items():
    for n in ("background", "foreground", "monochrome"):
        expect[os.path.join(RES, f"mipmap-{d}", f"ic_launcher_{n}.png")] = (round(108 * k),) * 2
    for n in ("ic_launcher", "ic_launcher_round"):
        expect[os.path.join(RES, f"mipmap-{d}", n + ".png")] = (round(48 * k),) * 2
for p, sz in expect.items():
    if not os.path.exists(p): err(f"немає {os.path.relpath(p, OUT)}"); continue
    im = Image.open(p)
    if im.size != sz: err(f"{os.path.relpath(p, OUT)}: {im.size} ≠ {sz}")
    if p.startswith(MARKET) and im.mode != "RGB": err(f"{os.path.relpath(p, OUT)}: mode {im.mode}, треба RGB")
for x in ("ic_launcher.xml", "ic_launcher_round.xml"):
    if not os.path.exists(os.path.join(RES, "mipmap-anydpi-v26", x)): err("немає mipmap-anydpi-v26/" + x)

# іконки атласу: RGBA, білі, не порожні
icons = sorted(glob.glob(os.path.join(ICONS, "*.png")))
for p in icons:
    im = Image.open(p).convert("RGBA")
    want = (192, 192) if os.path.basename(p) == "ph_glyph.png" else (96, 96)
    if im.size != want: err(f"{os.path.basename(p)}: {im.size} ≠ {want}")
    al = im.getchannel("A")
    if al.getbbox() is None: err(f"{os.path.basename(p)}: порожня"); continue
    data = im.get_flattened_data() if hasattr(im, "get_flattened_data") else im.getdata()
    px = [c for c in data if c[3] > 24]
    worst = min(min(c[:3]) for c in px)
    if worst < 235: err(f"{os.path.basename(p)}: не білий піксель (min RGB {worst})")

# foreground: гліф у безпечному колі Ø66dp
for d, k in DENS.items():
    p = os.path.join(RES, f"mipmap-{d}", "ic_launcher_foreground.png")
    if not os.path.exists(p): continue
    im = Image.open(p).convert("RGBA"); W = im.size[0]; c = W / 2
    al = im.getchannel("A"); bb = al.getbbox()
    rmax = 0.0
    for y in range(bb[1], bb[3]):
        for x in range(bb[0], bb[2]):
            if al.getpixel((x, y)) > 8:
                rmax = max(rmax, math.hypot(x + .5 - c, y + .5 - c))
    dp = 108 / W
    bw, bh = (bb[2] - bb[0]) * dp, (bb[3] - bb[1]) * dp
    cx, cy = ((bb[0] + bb[2]) / 2) * dp, ((bb[1] + bb[3]) / 2) * dp
    line = f"foreground {d}: bbox {bw:.1f}×{bh:.1f}dp, центр ({cx:.1f},{cy:.1f})dp, найдальша точка {rmax * dp:.1f}dp від центру (межа 33dp)"
    print(line)
    if d == "xxxhdpi": notes.append(line)
    if rmax * dp > 33: err(f"{d}: гліф виходить за безпечне коло Ø66dp")

print(f"Перевірено {len(expect)} файлів + {len(icons)} іконок.")
for n in notes: print("NOTE:", n)

# ---------- 4. контакт-лист ----------
if a.sheet:
    def font(sz):
        try: return ImageFont.load_default(size=sz)
        except TypeError: return ImageFont.load_default()
    F12, F16, F22 = font(12), font(16), font(22)
    SW = 1760
    sheet = Image.new("RGB", (SW, 4000), (24, 20, 22))
    dr = ImageDraw.Draw(sheet)
    def checker(w, h, a=(52, 46, 50), b=(70, 62, 66), s=8):
        t = Image.new("RGB", (w, h), a); d = ImageDraw.Draw(t)
        for yy in range(0, h, s):
            for xx in range((yy // s) % 2 * s, w, 2 * s): d.rectangle((xx, yy, xx + s - 1, yy + s - 1), fill=b)
        return t
    def put(im, x, y, label=None, bgc=True):
        im = im.convert("RGBA")
        if bgc: sheet.paste(checker(*im.size), (x, y))
        sheet.paste(im, (x, y), im)
        if label: dr.text((x, y + im.size[1] + 4), label, fill=(200, 190, 194), font=F12)
    y = 16
    dr.text((16, y), "Redwave - asset contact sheet (check)", fill=(255, 255, 255), font=F22); y += 40
    dr.text((16, y), f"Atlas icons assets/all/icons ({len(icons)}), 96x96 (ph_glyph 192), white on transparent", fill=(255, 160, 170), font=F16); y += 26
    cols, cell = 15, 114
    for i, p in enumerate(icons):
        im = Image.open(p)
        if im.size[0] != 96: im = im.resize((96, 96), Image.LANCZOS)
        put(im, 16 + (i % cols) * cell, y + (i // cols) * (cell + 8), os.path.basename(p)[:-4][:17])
    y += math.ceil(len(icons) / cols) * (cell + 8) + 10

    dr.text((16, y), "Logo (assets/brand)", fill=(255, 160, 170), font=F16); y += 26
    x = 16
    for n, s in (("logo_512.png", 256), ("logo_192.png", 192), ("logo_96.png", 96), ("logo_48.png", 48), ("logo_glyph_white_512.png", 256)):
        im = Image.open(os.path.join(BRAND, n)); im2 = im.resize((s, s), Image.LANCZOS) if im.size[0] != s else im
        put(im2, x, y, f"{n} {im.size[0]}px"); x += s + 24
    y += 256 + 34

    dr.text((16, y), "Launcher xxxhdpi (432 px = 108dp): layers; on foreground - 66dp safe-zone circle and 72dp visible square", fill=(255, 160, 170), font=F16); y += 26
    d4 = os.path.join(RES, "mipmap-xxxhdpi")
    bg, fg, mono = (Image.open(os.path.join(d4, f"ic_launcher_{n}.png")).convert("RGBA") for n in ("background", "foreground", "monochrome"))
    S4 = 432; k = S4 / 108
    fgv = fg.copy(); dv = ImageDraw.Draw(fgv)
    dv.ellipse((S4 / 2 - 33 * k, S4 / 2 - 33 * k, S4 / 2 + 33 * k, S4 / 2 + 33 * k), outline=(0, 255, 200, 255), width=2)
    dv.rectangle((18 * k, 18 * k, 90 * k, 90 * k), outline=(255, 220, 0, 255), width=2)
    comp = Image.alpha_composite(bg, fg)
    def masked(im, shape):
        m = Image.new("L", im.size, 0); md = ImageDraw.Draw(m)
        box = (18 * k, 18 * k, 90 * k, 90 * k)  # маска лаунчера бере центральні 72dp
        if shape == "circle": md.ellipse(box, fill=255)
        else: md.rounded_rectangle(box, radius=72 * k * .22, fill=255)
        out = Image.new("RGBA", im.size, (0, 0, 0, 0)); out.paste(im, mask=m)
        return out.crop(tuple(int(v) for v in box))
    x = 16
    for im, lab in ((bg.resize((216, 216)), "background"), (fgv.resize((216, 216), Image.LANCZOS), "foreground + zones"),
                    (masked(comp, "circle").resize((216, 216), Image.LANCZOS), "mask: circle"),
                    (masked(comp, "squircle").resize((216, 216), Image.LANCZOS), "mask: squircle"),
                    (masked(Image.alpha_composite(Image.new("RGBA", mono.size, (60, 50, 70, 255)), mono), "circle").resize((216, 216), Image.LANCZOS), "monochrome (themed)")):
        put(im, x, y, lab); x += 216 + 20
    for n in ("ic_launcher.png", "ic_launcher_round.png"):
        im = Image.open(os.path.join(d4, n)); put(im, x, y, f"legacy {n} 192px"); x += 192 + 20
    y += 216 + 34

    dr.text((16, y), "Google Play (market/): icon_512 (shown at 256) and feature graphic 1024x500 (1:1)", fill=(255, 160, 170), font=F16); y += 26
    put(Image.open(os.path.join(MARKET, "icon_512.png")).resize((256, 256), Image.LANCZOS), 16, y, "icon_512.png", bgc=False)
    put(Image.open(os.path.join(MARKET, "feature_1024x500.png")), 16 + 256 + 24, y, "feature_1024x500.png", bgc=False)
    y += 500 + 34

    dr.text((16, y), "Screenshots market/screenshot_01..06 (1080x1920, shown at 270x480)", fill=(255, 160, 170), font=F16); y += 26
    for i in range(1, 7):
        im = Image.open(os.path.join(MARKET, f"screenshot_{i:02d}.png")).resize((270, 480), Image.LANCZOS)
        put(im, 16 + (i - 1) * 290, y, f"screenshot_{i:02d}.png", bgc=False)
    y += 480 + 30
    sheet.crop((0, 0, SW, y)).save(a.sheet)
    print("contact sheet:", a.sheet, (SW, y))

if errors:
    print(f"{len(errors)} помилок"); sys.exit(1)
print("OK")
