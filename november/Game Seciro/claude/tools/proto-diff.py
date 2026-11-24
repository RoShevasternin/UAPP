#!/usr/bin/env python3
# Звірка прототипу з Figma піксель у піксель (без людини):
#   python3 claude/tools/proto-diff.py [екран ...]     → claude/tools/out/diff-<екран>.png + таблиця
# Кожен екран знімається headless Chrome у режимі #<екран>!raw (лише апка 375×812, @2x) і віднімається від
# кадру Figma @2x (claude/figma/frames). Статус-бар (верхні 52 юніти) не рахуємо: на Android він системний.
import os, re, subprocess, sys, tempfile
from PIL import Image, ImageChops

HERE = os.path.dirname(os.path.abspath(__file__))
GAME = os.path.normpath(os.path.join(HERE, "..", ".."))
PAGE = os.path.join(GAME, "prototype", "seciro.html")
FRAMES = os.path.join(GAME, "claude", "figma", "frames")
OUT = os.path.join(HERE, "out")
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
SHOTS = {  # екран → кадр Figma
    "splash": "r1_01_5-2530.png", "onb1": "r1_02_5-2620.png", "onb2": "r1_03_5-2557.png", "onb3": "r1_04_5-2683.png",
    "onb4": "r1_05_5-2747.png", "onb5": "r1_06_5-2815.png", "onb6": "r1_07_5-2874.png",
    "paywall~weekly": "r1_08_5-2948.png", "paywall~monthly": "r1_09_5-3055.png", "paywall~yearly": "r1_10_5-3162.png",
}

def shot(screen, tmp):
    page = os.path.join(tmp, "page.html")
    with open(page, "w") as f:
        f.write('<!doctype html><html><head><meta charset="utf-8"><style>:root{color-scheme:light}body{margin:0}</style></head><body>')
        # шрифти — з локальної копії (claude/tools/.fonts, з того самого URL Google), інакше headless не завжди встигає їх довантажити
        html = open(PAGE, encoding="utf-8").read()
        local = '<link rel="stylesheet" href="file://' + os.path.join(HERE, ".fonts", "fonts.css") + '">'
        html = re.sub(r'<link rel="stylesheet" href="https://fonts\.googleapis\.com[^"]+">', lambda m: local, html)
        f.write(html); f.write("</body></html>")
    out = os.path.join(tmp, "shot.png")
    subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", f"--user-data-dir={tmp}/prof",
                    "--window-size=375,812", "--force-device-scale-factor=2", "--virtual-time-budget=8000", "--allow-file-access-from-files", "--run-all-compositor-stages-before-draw",
                    f"--screenshot={out}", f"file://{page}#{screen}!raw"], capture_output=True)
    return Image.open(out).convert("RGB")

def main():
    os.makedirs(OUT, exist_ok=True)
    names = sys.argv[1:] or list(SHOTS)
    print(f"{'екран':18} {'різниця, %':>10} {'пікселів >24':>13}")
    for name in names:
        with tempfile.TemporaryDirectory() as tmp:
            proto = shot(name, tmp)
        fig = Image.open(os.path.join(FRAMES, SHOTS[name])).convert("RGB").resize(proto.size)
        top = 52 * 2
        d = ImageChops.difference(proto, fig).crop((0, top, proto.width, proto.height))
        g = d.convert("L"); hist = g.histogram()
        n = sum(hist); mean = sum(i * c for i, c in enumerate(hist)) / n / 255 * 100
        bad = sum(hist[25:]) / n * 100
        print(f"{name:18} {mean:10.2f} {bad:12.2f}%")
        # картинка: Figma | прототип | різниця (підсилена)
        heat = g.point(lambda v: min(255, v * 4)).convert("RGB")
        sheet = Image.new("RGB", (proto.width * 3 + 40, proto.height - top), "white")
        sheet.paste(fig.crop((0, top, fig.width, fig.height)), (0, 0)); sheet.paste(proto.crop((0, top, proto.width, proto.height)), (proto.width + 20, 0))
        sheet.paste(heat, (proto.width * 2 + 40, 0))
        sheet.save(os.path.join(OUT, f"diff-{name.replace('~', '-')}.png"))

if __name__ == "__main__":
    main()
