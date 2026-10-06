#!/usr/bin/env python3
"""
Перевірка MSDF-атласів: JSON парситься, розмір атласу в JSON = розміру PNG,
параметри атласу як у T35, наявність ключових гліфів (A я ґ є ї і …), структура ключів = еталонна.

    python3 verify_msdf.py <MSDF_DIR> [REFERENCE_JSON]

REFERENCE_JSON — будь-який JSON із T35 (напр. .../font/msdf/Montserrat-Bold.json):
порівнюються ключі верхнього рівня, atlas, metrics, гліфів і кернінгу.
Лише стандартна бібліотека (розмір PNG читається з IHDR).
"""
import json
import struct
import sys
from pathlib import Path

EXPECT_ATLAS = {"type": "mtsdf", "distanceRange": 32, "distanceRangeMiddle": 0, "size": 48, "yOrigin": "top"}
MUST_HAVE = {"A": 0x41, "я": 0x44F, "ґ": 0x491, "є": 0x454, "ї": 0x457, "і": 0x456, "…": 0x2026}


def png_size(p: Path):
    with p.open("rb") as f:
        head = f.read(33)
    assert head[:8] == b"\x89PNG\r\n\x1a\n" and head[12:16] == b"IHDR", f"{p.name}: не PNG"
    w, h, depth, ctype = struct.unpack(">IIBB", head[16:26])
    return w, h, depth, {2: "RGB", 6: "RGBA", 0: "GRAY", 4: "GRAYA", 3: "PALETTE"}.get(ctype, ctype)


def shape(d: dict):
    """Множини ключів на кожному рівні — для порівняння з T35."""
    glyph_keys = set()
    pb_keys, ab_keys, kern_keys = set(), set(), set()
    for g in d.get("glyphs", []):
        glyph_keys |= set(g)
        pb_keys |= set(g.get("planeBounds", {}))
        ab_keys |= set(g.get("atlasBounds", {}))
    for k in d.get("kerning", []):
        kern_keys |= set(k)
    return {
        "top": list(d),
        "atlas": list(d.get("atlas", {})),
        "metrics": list(d.get("metrics", {})),
        "glyph": sorted(glyph_keys),
        "planeBounds": sorted(pb_keys),
        "atlasBounds": sorted(ab_keys),
        "kerning": sorted(kern_keys),
    }


def main() -> int:
    if len(sys.argv) < 2:
        print(__doc__)
        return 2
    out = Path(sys.argv[1])
    ref = shape(json.loads(Path(sys.argv[2]).read_text())) if len(sys.argv) > 2 else None
    ok = True
    for js in sorted(out.glob("*.json")):
        d = json.loads(js.read_text(encoding="utf-8"))
        a = d["atlas"]
        w, h, depth, ctype = png_size(js.with_suffix(".png"))
        codes = {g["unicode"] for g in d["glyphs"]}
        issues = []
        for k, v in EXPECT_ATLAS.items():
            if a.get(k) != v:
                issues.append(f"atlas.{k}={a.get(k)!r} (очікується {v!r})")
        if (a["width"], a["height"]) != (w, h):
            issues.append(f"JSON {a['width']}x{a['height']} ≠ PNG {w}x{h}")
        if ctype != "RGBA" or depth != 8:
            issues.append(f"PNG {depth}-bit {ctype} (очікується 8-bit RGBA)")
        if w > 2048 or h > 2048:
            issues.append("атлас > 2048")
        missing = [ch for ch, cp in MUST_HAVE.items() if cp not in codes]
        if missing:
            issues.append("немає гліфів: " + " ".join(missing))
        if ref is not None:
            s = shape(d)
            for k in ref:
                # kerning у T35 порожній — порівнюємо лише якщо є з чим
                if k == "kerning" and not (ref[k] and s[k]):
                    continue
                if s[k] != ref[k]:
                    issues.append(f"структура {k}: {s[k]} ≠ T35 {ref[k]}")
        status = "OK  " if not issues else "WARN"
        ok &= not issues
        print(f"{status} {js.stem:28s} {w}x{h}  glyphs={len(codes):3d}  kerning={len(d.get('kerning', []))}"
              + ("" if not issues else "\n       " + "\n       ".join(issues)))
    return 0 if ok else 1


if __name__ == "__main__":
    sys.exit(main())
