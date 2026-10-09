#!/usr/bin/env python3
"""Лого й іконки Driftglass з SVG (той самий малюнок, що LOGO у prototype/index.html):
   python3 render_brand.py <Game Driftglass dir>
→ assets/brand/*.svg, assets/brand/android-res/mipmap-*/…, app textures/logo.png, market/icon_512.png.
Рендер — Google Chrome без вікна. Арка-«дверний проріз» = дім + вікно; усередині — живе перетікання."""
import os, subprocess, sys, tempfile
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
ROOT = sys.argv[1]
DEFS = '''<defs>
 <linearGradient id="B" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#10202B"/><stop offset="1" stop-color="#04070A"/></linearGradient>
 <linearGradient id="A" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#5FF2D1"/><stop offset=".48" stop-color="#7B8CFF"/><stop offset="1" stop-color="#FF7AA8"/></linearGradient>
 <radialGradient id="G" cx=".32" cy=".22" r=".62"><stop offset="0" stop-color="#fff" stop-opacity=".6"/><stop offset=".6" stop-color="#fff" stop-opacity="0"/></radialGradient>
 <radialGradient id="H" cx=".5" cy="1" r=".6"><stop offset="0" stop-color="#5FF2D1" stop-opacity=".45"/><stop offset="1" stop-color="#5FF2D1" stop-opacity="0"/></radialGradient>
 <clipPath id="C"><path d="M150 410V238a106 106 0 0 1 212 0v172z"/></clipPath>
</defs>'''
BG = '<rect width="512" height="512" fill="url(#B)"/><ellipse cx="256" cy="420" rx="190" ry="70" fill="url(#H)"/>'
ART = '''<g clip-path="url(#C)">
 <rect x="130" y="120" width="252" height="300" fill="url(#A)"/>
 <path d="M130 268c40-34 84-38 126-8s88 28 126-6v166H130z" fill="#0B1424" opacity=".28"/>
 <path d="M130 322c46-28 90-24 128 4s84 22 124-8v102H130z" fill="#0B1424" opacity=".42"/>
 <path d="M130 372c48-20 92-14 130 6s80 14 122-6v48H130z" fill="#0B1424" opacity=".55"/>
 <ellipse cx="214" cy="200" rx="86" ry="62" fill="url(#G)"/>
</g>
<path d="M150 410V238a106 106 0 0 1 212 0v172" fill="none" stroke="#fff" stroke-opacity=".55" stroke-width="7"/>
<rect x="118" y="406" width="276" height="12" rx="6" fill="#5FF2D1" opacity=".9"/>'''
MONO = '<path d="M150 410V238a106 106 0 0 1 212 0v172z" fill="#fff"/><rect x="118" y="406" width="276" height="12" rx="6" fill="#fff"/>'
def svg(body, rx=0, clip_circle=False):
    shape = f'<clipPath id="R"><circle cx="256" cy="256" r="256"/></clipPath>' if clip_circle else (f'<clipPath id="R"><rect width="512" height="512" rx="{rx}"/></clipPath>' if rx else '')
    g = f'<g clip-path="url(#R)">{body}</g>' if shape else body
    return f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">{DEFS}<defs>{shape}</defs>{g}</svg>'
# адаптивна іконка: 108dp, безпечна зона 66dp → арку масштабуємо в центр
FG_SCALE = 0.86
FULL   = svg(BG + ART, rx=114)
ROUND  = svg(BG + ART, clip_circle=True)
BGONLY = svg(BG)
FG     = svg(f'<g transform="translate(256 262) scale({FG_SCALE}) translate(-256 -276)">{ART}</g>')
MONOFG = svg(f'<g transform="translate(256 262) scale({FG_SCALE}) translate(-256 -276)">{MONO}</g>')
tmp = tempfile.mkdtemp()
def render(svgtext, size, dst):
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    html = os.path.join(tmp, "x.html")
    open(html, "w").write(f'<!doctype html><html><body style="margin:0;background:transparent"><div style="width:{size}px;height:{size}px">{svgtext.replace("<svg ", f"<svg width=\"{size}\" height=\"{size}\" ", 1)}</div></body></html>')
    subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                    "--default-background-color=00000000", f"--window-size={size},{size}", f"--screenshot={dst}", "file://" + html],
                   check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
brand = os.path.join(ROOT, "assets/brand")
os.makedirs(brand, exist_ok=True)
for n, s in {"logo.svg": FULL, "ic_launcher_round.svg": ROUND, "ic_launcher_background.svg": BGONLY, "ic_launcher_foreground.svg": FG, "ic_launcher_monochrome.svg": MONOFG}.items():
    open(os.path.join(brand, n), "w").write(s)
render(FULL, 512, os.path.join(brand, "logo_512.png"))
render(FULL, 512, os.path.join(ROOT, "market/icon_512.png"))
render(FULL, 512, os.path.join(ROOT, "Driftglass/app/src/main/assets/textures/logo.png"))
DPI = {"mdpi": 1, "hdpi": 1.5, "xhdpi": 2, "xxhdpi": 3, "xxxhdpi": 4}
for d, k in DPI.items():
    res = os.path.join(brand, "android-res", f"mipmap-{d}")
    render(FULL,  int(48 * k), os.path.join(res, "ic_launcher.png"))
    render(ROUND, int(48 * k), os.path.join(res, "ic_launcher_round.png"))
    for n, s in (("ic_launcher_background", BGONLY), ("ic_launcher_foreground", FG), ("ic_launcher_monochrome", MONOFG)):
        render(s, int(108 * k), os.path.join(res, n + ".png"))
    print(d)
