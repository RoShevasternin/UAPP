#!/usr/bin/env python3
"""Кадри Google Play 1080×1920 зі скрінів телефона (market/raw/*.png, режим StoreDemo).
   <venv з Pillow>/bin/python compose_shots.py
1) Pillow: зрізає статус-бар (49 px) і навігацію (94 px) Redmi 720×1650 — там сповіщення людини;
2) HTML-шаблон: підпис зверху + телефон, чистий статус-бар 9:41 (наш, не з телефона);
3) Chrome без вікна → PNG, Pillow → RGB без альфи (так приймає Play Console)."""
import os, subprocess, tempfile
from PIL import Image
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
HERE = os.path.dirname(os.path.abspath(__file__))
MARKET = os.path.abspath(os.path.join(HERE, ".."))
FONTS = os.path.abspath(os.path.join(MARKET, "../assets/fonts/ttf"))
STATUS, NAV = 49, 94
# файл: (raw, заголовок, акцент, підзаголовок)
SHOTS = [
    ("screenshot_01.png", "01_home.png",     "Your home screen,", "alive.",            "Living wallpapers drawn in real time behind your apps"),
    ("screenshot_02.png", "02_discover.png", "Wallpapers that",   "never repeat.",     "24 originals: aurora, liquid, waves, glass, gradient"),
    ("screenshot_03.png", "03_preview.png",  "See it",            "before you set it.", "Preview with your clock and icons, apply in one tap"),
    ("screenshot_04.png", "04_studio.png",   "Make one that's",   "yours.",            "Pick a style and palette, tune motion and grain"),
    ("screenshot_05.png", "05_my_glass.png", "Shifts with",       "your day.",         "Dawn, day, dusk and night, or a new one daily"),
    ("screenshot_06.png", "06_folder.png",   "Tidy by",           "design.",           "Apps sort into folders. Swipe up for all apps"),
]
STATUS_SVG = '''<svg width="150" height="30" viewBox="0 0 150 30" fill="#fff">
<path d="M20 22.5 L2 8 A26 26 0 0 1 38 8 Z" transform="translate(42 2) scale(.62)"/>
<g transform="translate(78 6)"><rect x="0" y="12" width="4" height="6" rx="1"/><rect x="6" y="8" width="4" height="10" rx="1"/><rect x="12" y="4" width="4" height="14" rx="1"/><rect x="18" y="0" width="4" height="18" rx="1"/></g>
<g transform="translate(106 6)"><rect x="0" y="0" width="34" height="18" rx="5" fill="none" stroke="#fff" stroke-width="2"/><rect x="3" y="3" width="26" height="12" rx="2.5"/><rect x="36" y="6" width="3" height="6" rx="1"/></g></svg>'''

def html(raw_path, h1, accent, sub):
    return f'''<!doctype html><html><head><style>
@font-face{{font-family:G;src:url("file://{FONTS}/Geologica-ExtraBold.ttf");font-weight:800}}
@font-face{{font-family:G;src:url("file://{FONTS}/Geologica-Light.ttf");font-weight:300}}
@font-face{{font-family:G;src:url("file://{FONTS}/Geologica-Bold.ttf");font-weight:700}}
html,body{{margin:0;width:1080px;height:1920px;overflow:hidden;background:#070b10}}
.bg{{position:absolute;inset:0;background:
 radial-gradient(700px 520px at 18% 6%, rgba(95,242,209,.30), transparent 70%),
 radial-gradient(760px 600px at 92% 22%, rgba(150,110,255,.30), transparent 70%),
 radial-gradient(900px 700px at 50% 100%, rgba(255,122,168,.18), transparent 70%), #070b10}}
.cap{{position:absolute;left:84px;right:84px;top:92px;font-family:G;color:#fff}}
h1{{margin:0;font-weight:800;font-size:82px;line-height:1.04;letter-spacing:-1.5px}}
h1 em{{font-style:normal;background:linear-gradient(90deg,#5ff2d1,#7bb8ff 55%,#b48cff);-webkit-background-clip:text;color:transparent}}
p{{margin:24px 0 0;font-weight:300;font-size:34px;line-height:1.3;color:rgba(255,255,255,.72)}}
.phone{{position:absolute;left:{(1080-677)//2}px;top:410px;width:677px;height:1440px;border-radius:78px;background:#0d1117;
 box-shadow:0 40px 120px rgba(0,0,0,.6), 0 0 0 2px rgba(255,255,255,.10) inset;padding:16px;box-sizing:border-box}}
.scr{{position:relative;width:645px;height:1408px;border-radius:64px;overflow:hidden;background:#000}}
.scr img{{position:absolute;left:0;top:0;width:645px}}
.sb{{position:absolute;left:0;right:0;top:0;height:57px;display:flex;align-items:center;justify-content:space-between;padding:0 38px 0 48px;
 font-family:G;font-weight:700;font-size:25px;color:#fff;text-shadow:0 1px 4px rgba(0,0,0,.35)}}
</style></head><body><div class="bg"></div>
<div class="cap"><h1>{h1}<br><em>{accent}</em></h1><p>{sub}</p></div>
<div class="phone"><div class="scr"><img src="file://{raw_path}"><div class="sb"><span>9:41</span>{STATUS_SVG}</div></div></div>
</body></html>'''

tmp = os.environ.get("SHOTS_TMP") or tempfile.mkdtemp()
for out, raw, h1, accent, sub in SHOTS:
    im = Image.open(os.path.join(MARKET, "raw", raw)).convert("RGB")
    w, h = im.size
    # статус-бар: замінюємо смугою з ряду під ним (фон лишається, сповіщень немає); навігацію зрізаємо
    body = im.crop((0, STATUS, w, h - NAV))
    # гладка смуга (розмита верхівка кадру), без вертикальних смуг від розтягнутого одного ряду
    pad = body.crop((0, 0, w, 40)).resize((16, 2), Image.BOX).resize((w, 64), Image.BICUBIC)
    # 64 + 1507 = 1571 px → у кадрі 645 × 1407
    clean = Image.new("RGB", (w, 64 + body.height)); clean.paste(pad, (0, 0)); clean.paste(body, (0, 64))
    cp = os.path.join(tmp, raw); clean.save(cp)
    page = os.path.join(tmp, out + ".html"); open(page, "w").write(html(cp, h1, accent, sub))
    dst = os.path.join(MARKET, out)
    subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                    "--window-size=1080,1920", "--virtual-time-budget=3000", f"--screenshot={dst}", "file://" + page],
                   check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    Image.open(dst).convert("RGB").save(dst, optimize=True)
    print(out)
