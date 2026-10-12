#!/usr/bin/env python3
"""Feature graphic 1024×500 + icon_512 без альфи для Google Play.
   <venv з Pillow>/bin/python render_feature.py
Банер: лого, назва, підзаголовок і три кадри з market/raw (режим StoreDemo — лише наші демо-апки).
Іконка: assets/brand/logo_512.png на повний квадрат без альфи (маску скруглення Play накладає сам)."""
import os, subprocess, tempfile
from PIL import Image
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
HERE = os.path.dirname(os.path.abspath(__file__))
MARKET = os.path.abspath(os.path.join(HERE, ".."))
ROOT = os.path.abspath(os.path.join(MARKET, ".."))
FONTS = os.path.join(ROOT, "assets/fonts/ttf")
tmp = tempfile.mkdtemp()

# кадри без статус-бару й навігації (сповіщення людини)
crops = []
for raw in ("01_home.png", "04_studio.png", "02_discover.png"):
    im = Image.open(os.path.join(MARKET, "raw", raw)).convert("RGB")
    p = os.path.join(tmp, raw); im.crop((0, 49, im.width, im.height - 94)).save(p); crops.append(p)

html = f'''<!doctype html><html><head><style>
@font-face{{font-family:G;src:url("file://{FONTS}/Geologica-ExtraBold.ttf");font-weight:800}}
@font-face{{font-family:G;src:url("file://{FONTS}/Geologica-Bold.ttf");font-weight:700}}
html,body{{margin:0;width:1024px;height:500px;overflow:hidden}}
.bg{{position:absolute;inset:0;background:
 radial-gradient(420px 320px at 12% 10%, rgba(95,242,209,.35), transparent 70%),
 radial-gradient(520px 420px at 80% 30%, rgba(150,110,255,.45), transparent 70%),
 radial-gradient(520px 360px at 60% 110%, rgba(255,122,168,.30), transparent 70%), #070b10}}
.brand{{position:absolute;left:64px;top:118px;font-family:G;color:#fff}}
.brand img{{width:118px;height:118px;border-radius:28px;display:block;box-shadow:0 18px 50px rgba(0,0,0,.5)}}
.name{{margin-top:26px;font-weight:800;font-size:76px;letter-spacing:-1.5px;line-height:1}}
.tag{{margin-top:16px;font-weight:700;font-size:21px;letter-spacing:5px;color:#5ff2d1}}
.ph{{position:absolute;width:178px;height:372px;border-radius:30px;overflow:hidden;background:#000;
 box-shadow:0 30px 70px rgba(0,0,0,.6), 0 0 0 7px #0d1117, 0 0 0 8px rgba(255,255,255,.12)}}
.ph img{{width:100%;display:block}}
</style></head><body><div class="bg"></div>
<div class="brand"><img src="file://{ROOT}/assets/brand/logo_512.png"><div class="name">Driftglass</div><div class="tag">LIVE HOME SCREEN</div></div>
<div class="ph" style="left:566px;top:96px;transform:rotate(-8deg)"><img src="file://{crops[2]}"></div>
<div class="ph" style="left:800px;top:96px;transform:rotate(8deg)"><img src="file://{crops[1]}"></div>
<div class="ph" style="left:683px;top:62px;z-index:2"><img src="file://{crops[0]}"></div>
</body></html>'''
page = os.path.join(tmp, "f.html"); open(page, "w").write(html)
dst = os.path.join(MARKET, "feature_1024x500.png")
subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                "--window-size=1024,500", "--virtual-time-budget=3000", f"--screenshot={dst}", "file://" + page],
               check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
Image.open(dst).convert("RGB").save(dst, optimize=True)
print("feature_1024x500.png")

# іконка: шари адаптивної іконки (фон + передній план) на повний квадрат, без альфи —
# маску скруглення Play накладає сам (logo_512 має власні прозорі кути — не годиться)
B = os.path.join(ROOT, "assets/brand")
ipage = os.path.join(tmp, "icon.html")
open(ipage, "w").write(f'''<html><body style="margin:0;width:512px;height:512px;position:relative">
<img src="file://{B}/ic_launcher_background.svg" style="position:absolute;inset:0;width:512px;height:512px">
<img src="file://{B}/ic_launcher_foreground.svg" style="position:absolute;inset:0;width:512px;height:512px"></body></html>''')
idst = os.path.join(MARKET, "icon_512.png")
subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                "--window-size=512,512", f"--screenshot={idst}", "file://" + ipage],
               check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
Image.open(idst).convert("RGB").save(idst, optimize=True)
print("icon_512.png")
