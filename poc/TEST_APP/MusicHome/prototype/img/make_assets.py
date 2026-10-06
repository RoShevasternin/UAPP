# Обробка фото для прототипу Redwave.
# Запуск: python3 make_assets.py <тека з img/raw/<StockSnapID>.img> <тека виводу>
# Сирі фото тягнулись через https://api.openverse.org/v1/images/<openverse-id>/thumb/?full_size=true
import sys, os
from PIL import Image, ImageOps, ImageEnhance, ImageFilter, ImageDraw
base = sys.argv[1]; raw = os.path.join(base, 'img/raw'); out = sys.argv[2]

def load(i): return Image.open(os.path.join(raw, i + '.img')).convert('RGB')

def square(im, cx=0.5, cy=0.5, size=480):
    w, h = im.size; s = min(w, h)
    x = int(max(0, min(w - s, cx * w - s / 2))); y = int(max(0, min(h - s, cy * h - s / 2)))
    return im.crop((x, y, x + s, y + s)).resize((size, size), Image.LANCZOS)

def box(im, ratio, cx=0.5, cy=0.5, width=720):
    w, h = im.size
    if w / h > ratio: cw, ch = int(h * ratio), h
    else: cw, ch = w, int(w / ratio)
    x = int(max(0, min(w - cw, cx * w - cw / 2))); y = int(max(0, min(h - ch, cy * h - ch / 2)))
    return im.crop((x, y, x + cw, y + ch)).resize((width, int(width / ratio)), Image.LANCZOS)

def lerp(a, b, t): return tuple(int(a[k] + (b[k] - a[k]) * t) for k in range(3))
STOPS = [(0, (10, 3, 6)), (0.35, (92, 6, 22)), (0.65, (214, 26, 48)), (0.85, (255, 92, 92)), (1, (255, 226, 218))]
def duotone(im, contrast=1.15):
    g = ImageEnhance.Contrast(ImageOps.grayscale(im)).enhance(contrast)
    lut = []
    for c in range(3):
        for v in range(256):
            t = v / 255
            for k in range(len(STOPS) - 1):
                if STOPS[k][0] <= t <= STOPS[k + 1][0]:
                    tt = (t - STOPS[k][0]) / (STOPS[k + 1][0] - STOPS[k][0]); lut.append(lerp(STOPS[k][1], STOPS[k + 1][1], tt)[c]); break
    return Image.merge('RGB', [g.point(lut[c * 256:(c + 1) * 256]) for c in range(3)])

def grade(im, amt=0.22):
    # легкий червоний спліт-тон, щоб обкладинки жили в одній палітрі
    return Image.blend(im, duotone(im, 1.0), amt)

COVERS = {
 'neon-heart': ('1MB7WMS2HX', .72, .5, 0), 'smoke': ('F66MXRQS1K', .48, .45, .15), 'slow-spin': ('DSU2OBEWB3', .5, .5, .1),
 'night-drive': ('QVGSXCGO6J', .5, .5, .15), 'bass-theory': ('WKV40ATLIY', .5, .5, .25), 'hands-up': ('4LZRZTD1MC', .45, .5, .2),
 'stage-lights': ('L695JG265J', .5, .45, .15), 'backlit': ('5GPP7A6LVW', .55, .5, .2), 'blue-room': ('N06ELOLAT9', .5, .5, .35),
 'needle-drop': ('PU9HHZB5QW', .5, .5, .3), 'indie-hour': ('8KQC04JDT0', .5, .5, .2), 'late-talk': ('Q0LOIKU7GM', .45, .5, .2),
 'monochrome': ('1Y69ONYCCZ', .5, .5, .3), 'golden-hour': ('D14AE6DFF7', .5, .5, .15), 'brass': ('RJWNF1GVHZ', .5, .5, .15),
 'white-noise': ('45C794A760', .35, .5, .3)
}
for name, (i, cx, cy, amt) in COVERS.items():
    im = square(load(i), cx, cy, 480)
    if amt: im = grade(im, amt)
    im.save(os.path.join(out, f'c-{name}.jpg'), quality=80, optimize=True, progressive=True)

# онбординг — квадратні, дуотон / природні
duotone(square(load('5FGWJW4Z5D'), .5, .5, 720)).save(os.path.join(out, 'onb-3.jpg'), quality=82, optimize=True, progressive=True)
# Discover — банери
duotone(box(load('FZE048NY32'), 16/10, .5, .5, 720)).save(os.path.join(out, 'feat-live.jpg'), quality=80, optimize=True, progressive=True)
box(load('QVGSXCGO6J'), 16/10, .5, .5, 720).save(os.path.join(out, 'feat-night.jpg'), quality=80, optimize=True, progressive=True)
duotone(box(load('NAGEIGCCTD'), 16/10, .5, .5, 720), 1.25).save(os.path.join(out, 'feat-vinyl.jpg'), quality=80, optimize=True, progressive=True)
print('done')
duotone(square(load('1Y69ONYCCZ'), .5, .5, 720), 1.3).save(os.path.join(out, 'onb-2.jpg'), quality=82, optimize=True, progressive=True)
grade(square(load('0RN44JI11G'), .36, .5, 720), .45).save(os.path.join(out, 'onb-1.jpg'), quality=82, optimize=True, progressive=True)
