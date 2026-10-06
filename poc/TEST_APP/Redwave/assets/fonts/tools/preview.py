#!/usr/bin/env python3
"""
Візуальна перевірка: для кожного шрифта — рядок, намальований із .ttf (FreeType/PIL),
і поруч той самий рядок, зібраний З MSDF-АТЛАСУ (median(r,g,b) ≥ 0.5, як у шейдері).
Якщо атлас/JSON зіпсовані — друга колонка це одразу покаже.

    python3 preview.py <TTF_DIR> <MSDF_DIR> <OUT.png>

Потрібен Pillow:  .venv/bin/pip install pillow
"""
import json
import sys
from pathlib import Path

from PIL import Image, ImageChops, ImageDraw, ImageFont

TEXT = "Redwave — What are we downloading? 0:24 Привіт ґ є ї пʼять ₴ „9‰“"
PX = 34            # кегль прев'ю
ROW_H = 64
BG, FG, MUTED = (16, 12, 14), (245, 240, 240), (150, 140, 145)


def msdf_line(json_path: Path, png_path: Path, text: str, px: int) -> Image.Image:
    d = json.loads(json_path.read_text(encoding="utf-8"))
    s_atlas = d["atlas"]["size"]
    m = d["metrics"]
    atlas = Image.open(png_path).convert("RGBA")
    r, g, b, _ = atlas.split()
    # median(r,g,b) = max(min(r,g), min(max(r,g), b))
    med = ImageChops.lighter(ImageChops.darker(r, g), ImageChops.darker(ImageChops.lighter(r, g), b))
    glyphs = {gl["unicode"]: gl for gl in d["glyphs"]}

    width = int(sum(glyphs.get(ord(c), {"advance": 0.5})["advance"] for c in text) * px) + 20
    height = int(m["lineHeight"] * px) + 10
    out = Image.new("L", (width, height), 0)
    baseline = abs(m["ascender"]) * px + 5
    pen = 10.0
    for ch in text:
        gl = glyphs.get(ord(ch))
        if gl is None:                       # немає гліфа — рамка-«тофу», як і в рантаймі буде порожньо
            ImageDraw.Draw(out).rectangle([pen + 2, baseline - px * 0.7, pen + px * 0.45, baseline], outline=90)
            pen += px * 0.5
            continue
        pb, ab = gl.get("planeBounds"), gl.get("atlasBounds")
        if pb and ab:
            crop = med.crop((int(ab["left"] - 0.5), int(ab["top"] - 0.5),
                             int(ab["right"] + 0.5), int(ab["bottom"] + 0.5)))
            w = max(1, round((pb["right"] - pb["left"]) * px))
            h = max(1, round((pb["bottom"] - pb["top"]) * px))
            crop = crop.resize((w, h), Image.BILINEAR)
            # поріг 0.5 зі згладжуванням ~1px (як smoothstep у шейдері)
            # dist_px = (v/255 - 0.5) * range_px → alpha = clamp(dist_px + 0.5)
            range_px = d["atlas"]["distanceRange"] * px / s_atlas
            crop = crop.point(lambda v: max(0, min(255, int((v - 127.5) * range_px + 127.5))))
            x = round(pen + pb["left"] * px)
            y = round(baseline + pb["top"] * px)
            out.paste(ImageChops.lighter(out.crop((x, y, x + w, y + h)), crop), (x, y))
        pen += gl["advance"] * px
    return out


def main() -> int:
    if len(sys.argv) != 4:
        print(__doc__)
        return 2
    ttf_dir, msdf_dir, out_path = Path(sys.argv[1]), Path(sys.argv[2]), Path(sys.argv[3])
    ttfs = sorted(ttf_dir.glob("*.ttf"))
    label_font = ImageFont.truetype(str(next(p for p in ttfs if "JetBrainsMono-Regular" in p.name)), 14)

    rows = []
    for ttf in ttfs:
        f = ImageFont.truetype(str(ttf), PX)
        l, t, rgt, btm = f.getbbox(TEXT)
        left = Image.new("L", (rgt + 20, ROW_H), 0)
        ImageDraw.Draw(left).text((10, 8), TEXT, font=f, fill=255)
        js, png = msdf_dir / f"{ttf.stem}.json", msdf_dir / f"{ttf.stem}.png"
        right = msdf_line(js, png, TEXT, PX) if js.exists() else Image.new("L", (10, ROW_H), 0)
        rows.append((ttf.stem, left, right))

    col_w = max(r[1].width for r in rows)
    col2_w = max(r[2].width for r in rows)
    W = 20 + col_w + 40 + col2_w + 20
    H = 40 + len(rows) * (ROW_H + 26) + 10
    img = Image.new("RGB", (W, H), BG)
    dr = ImageDraw.Draw(img)
    dr.text((20, 12), "TTF (FreeType)", font=label_font, fill=MUTED)
    dr.text((20 + col_w + 40, 12), "MSDF atlas → median ≥ 0.5", font=label_font, fill=MUTED)
    y = 40
    for name, left, right in rows:
        dr.text((20, y), name, font=label_font, fill=MUTED)
        y += 20
        img.paste(Image.new("RGB", left.size, FG), (20, y), left)
        img.paste(Image.new("RGB", right.size, FG), (20 + col_w + 40, y), right)
        y += ROW_H + 6
    out_path.parent.mkdir(parents=True, exist_ok=True)
    img.save(out_path)
    print(f"→ {out_path} ({W}x{H})")
    return 0


if __name__ == "__main__":
    sys.exit(main())
