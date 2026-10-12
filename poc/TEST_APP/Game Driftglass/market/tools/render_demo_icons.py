#!/usr/bin/env python3
"""Іконки вигаданих апок для режиму скрінів Google Play (android/StoreDemo.kt).
   python3 render_demo_icons.py <out_dir>      (за замовчуванням — ../../Driftglass/app/src/debug/assets/demo_icons)
Гліфи — Material Symbols Rounded (Google, Apache 2.0) з Google Fonts, фон — наш градієнт.
Жодних чужих логотипів і брендів: лише загальні піктограми. Рендер — Google Chrome без вікна."""
import os, subprocess, sys, tempfile
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
HERE = os.path.dirname(os.path.abspath(__file__))
# id: (гліф Material Symbols, колір 1, колір 2)
ICONS = {
  "phone": ("call", "#34d27b", "#139a55"), "messages": ("chat_bubble", "#5aa8ff", "#2a6fe0"),
  "browser": ("language", "#ffb547", "#f0703a"), "camera": ("photo_camera", "#8e9bb0", "#4a5568"),
  "calculator": ("calculate", "#ff9f5a", "#f06a2a"), "clock": ("schedule", "#3d4a63", "#1d2537"),
  "notes": ("sticky_note_2", "#ffd84d", "#f2b100"), "recorder": ("mic", "#ff6b7d", "#e0334a"),
  "files": ("folder", "#5ec8ff", "#2b8fe0"), "compass": ("explore", "#47d6c4", "#14998a"),
  "weather": ("partly_cloudy_day", "#6ec6ff", "#3a86e8"), "scanner": ("document_scanner", "#7c8cff", "#4a55d6"),
  "music": ("music_note", "#ff5fa2", "#c2338a"), "video": ("smart_display", "#ff6a5c", "#d63a3a"),
  "podcasts": ("podcasts", "#b07cff", "#7a3fe0"), "radio": ("radio", "#ff9a6b", "#e0603a"),
  "chat": ("sms", "#40d9a0", "#16a072"), "friends": ("group", "#5c9dff", "#3a5fe0"),
  "forum": ("forum", "#ffb35c", "#e07a2a"), "puzzle": ("extension", "#9a7cff", "#5d3fd6"),
  "chess": ("chess", "#566178", "#232a3a"), "cards": ("style", "#3fc77a", "#178a4f"),
  "racing": ("sports_score", "#ff7a59", "#d6402a"), "gallery": ("photo_library", "#ffb84d", "#ff6f61"),
  "calendar": ("calendar_month", "#5a8cff", "#3657d6"), "maps": ("map", "#4fd1a1", "#2a9d78"),
  "settings": ("settings", "#8a94a8", "#4a5468"), "fitness": ("fitness_center", "#ff6b6b", "#d63a5a"),
  "books": ("menu_book", "#ffa94d", "#e07020"), "wallet": ("account_balance_wallet", "#3cc9b3", "#167f8a"),
  "translate": ("translate", "#5aa0ff", "#2f5fd6"), "shop": ("shopping_bag", "#ff7ab0", "#d6457f"),
  "travel": ("flight", "#4cc3ff", "#2a7ce0"), "recipes": ("restaurant", "#ffb04d", "#e0742a"),
}
out = sys.argv[1] if len(sys.argv) > 1 else os.path.join(HERE, "../../Driftglass/app/src/debug/assets/demo_icons")
os.makedirs(out, exist_ok=True)
tmp = tempfile.mkdtemp()
S = 192
for name, (glyph, c1, c2) in ICONS.items():
    html = os.path.join(tmp, name + ".html")
    open(html, "w").write(f'''<!doctype html><html><head>
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Rounded:opsz,wght,FILL,GRAD@48,500,1,0" rel="stylesheet">
<style>html,body{{margin:0;background:transparent}}
.t{{width:{S}px;height:{S}px;border-radius:{int(S*0.27)}px;background:linear-gradient(145deg,{c1},{c2});display:flex;align-items:center;justify-content:center;
box-shadow:inset 0 2px 0 rgba(255,255,255,.18)}}
.g{{font-family:'Material Symbols Rounded';font-size:{int(S*0.56)}px;color:#fff;line-height:1;font-variation-settings:'FILL' 1,'wght' 500,'opsz' 48}}</style></head>
<body><div class="t"><span class="g">{glyph}</span></div></body></html>''')
    dst = os.path.join(out, f"{name}.png")
    subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                    "--default-background-color=00000000", f"--window-size={S},{S}", "--virtual-time-budget=6000",
                    f"--screenshot={dst}", "file://" + html], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    print(name)
