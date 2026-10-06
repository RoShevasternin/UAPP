# Великі текстури Redwave для LibGDX (assets/textures/**), з фото прототипу (CC0, див. prototype/img/SOURCES.md).
# Запуск з теки Redwave:  python3 assets/textures/tools/make_textures.py
import math, os
from PIL import Image, ImageFilter, ImageEnhance, ImageDraw

SRC, OUT = 'prototype/img', 'assets/textures'

def save(im, path, q=84):
    p = os.path.join(OUT, path)
    if p.endswith('.png'): im.save(p, optimize=True)
    else: im.convert('RGB').save(p, quality=q, optimize=True, progressive=True)

# ── Онбординг: EnumTextureGroup("textures/onboarding", "onboarding", 3) ──────
for i in (1, 2, 3):
    save(Image.open(f'{SRC}/onb-{i}.jpg'), f'onboarding/onboarding_{i}.jpg')

# ── Discover: банери підбірок 16:10 ─────────────────────────────────────────
for n in ('live', 'night', 'vinyl'):
    save(Image.open(f'{SRC}/feat-{n}.jpg'), f'discover/feat_{n}.jpg')

# ── Лаунчер: розмитий концерт, вже затемнений (як .ln-bg у прототипі) ────────
src = Image.open(f'{SRC}/onb-3.jpg').convert('RGB')
W, H = 540, 1170
s = max(W / src.width, H / src.height)
im = src.resize((math.ceil(src.width * s), math.ceil(src.height * s)), Image.LANCZOS)
im = im.crop(((im.width - W) // 2, (im.height - H) // 2, (im.width - W) // 2 + W, (im.height - H) // 2 + H))
im = ImageEnhance.Color(im.filter(ImageFilter.GaussianBlur(16))).enhance(1.2)
shade = Image.new('L', (1, H))
for y in range(H): shade.putpixel((0, y), int(255 * (0.35 + 0.40 * y / (H - 1))))
im = Image.composite(Image.new('RGB', (W, H), (8, 4, 5)), im, shade.resize((W, H)))
save(im, 'launcher/launcher_bg.jpg', 82)

# ── FX: біле радіальне світіння (тонується червоним) і вертикальний фейд ─────
N = 256
glow = Image.new('L', (N, N))
for y in range(N):
    for x in range(N):
        d = math.hypot(x - N / 2 + .5, y - N / 2 + .5) / (N / 2)
        glow.putpixel((x, y), int(255 * max(0.0, 1 - d) ** 2.2))
white = Image.new('RGBA', (N, N), (255, 255, 255, 0)); white.putalpha(glow)
save(white, 'fx/glow_radial.png')

fade = Image.new('RGBA', (4, 256), (255, 255, 255, 0))
for y in range(256): 
    for x in range(4): fade.putpixel((x, y), (255, 255, 255, int(255 * (y / 255) ** 1.4)))
save(fade, 'fx/fade_vertical.png')   # 0 зверху → 1 знизу; тонувати кольором фону
print('ok')
