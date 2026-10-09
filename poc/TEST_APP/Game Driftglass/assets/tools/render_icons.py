#!/usr/bin/env python3
"""Іконки атласу (білі, 96 px, лінія 2 на сітці 24) з тих самих шляхів, що в прототипі (ICON у prototype/index.html).
   python3 render_icons.py <out_dir>
Рендер — Google Chrome без вікна (--headless --screenshot), без Python-бібліотек.
Після рендеру: cd ../Driftglass && sh ./gradlew :app:packAtlas"""
import os, subprocess, sys, tempfile
CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
ICONS = {
  "home":'<path d="M4 11l8-7 8 7v9a1 1 0 0 1-1 1h-5v-6h-4v6H5a1 1 0 0 1-1-1z"/>',
  "sparkle":'<path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z"/><path d="M19 15l.8 2.2L22 18l-2.2.8L19 21l-.8-2.2L16 18l2.2-.8z"/>',
  "sliders":'<path d="M4 7h10M18 7h2M4 17h4M12 17h8"/><circle cx="16" cy="7" r="2"/><circle cx="10" cy="17" r="2"/>',
  "layers":'<path d="M12 3l9 5-9 5-9-5z"/><path d="M3 13l9 5 9-5"/>',
  "gear":'<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.8.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1.1 1.7 1.7 0 0 0-.3-1.8l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.8.3H9a1.7 1.7 0 0 0 1-1.5V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5 1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.8V9a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z"/>',
  "heart":'<path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.6a5.5 5.5 0 0 0 0-7.8z"/>',
  "heart_fill":'<path fill="#fff" d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.6a5.5 5.5 0 0 0 0-7.8z"/>',
  "shuffle":'<path d="M16 3h5v5M4 20L21 3M21 16v5h-5M15 15l6 6M4 4l5 5"/>',
  "back":'<path d="M15 18l-6-6 6-6"/>',
  "chev_right":'<path d="M9 18l6-6-6-6"/>',
  "search":'<circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/>',
  "check":'<path d="M20 6L9 17l-5-5"/>',
  "dice":'<rect x="3" y="3" width="18" height="18" rx="4"/><circle cx="8.5" cy="8.5" r="1.2" fill="#fff"/><circle cx="15.5" cy="15.5" r="1.2" fill="#fff"/><circle cx="15.5" cy="8.5" r="1.2" fill="#fff"/><circle cx="8.5" cy="15.5" r="1.2" fill="#fff"/>',
  "sun":'<circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/>',
  "moon":'<path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8z"/>',
  "info":'<circle cx="12" cy="12" r="9"/><path d="M12 16v-4M12 8h.01"/>',
  "trash":'<path d="M3 6h18M8 6V4h8v2M6 6l1 14h10l1-14"/>',
  "lock":'<rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0"/>',
  "bolt":'<path d="M13 2L4 14h7l-1 8 9-12h-7z"/>',
  "plus":'<path d="M12 5v14M5 12h14"/>',
  "grid":'<rect x="3" y="3" width="7" height="7" rx="2"/><rect x="14" y="3" width="7" height="7" rx="2"/><rect x="3" y="14" width="7" height="7" rx="2"/><rect x="14" y="14" width="7" height="7" rx="2"/>',
  "wave":'<path d="M2 12c2.5-3 5-3 7.5 0s5 3 7.5 0 3.5-2 5-1"/><path d="M2 17c2.5-3 5-3 7.5 0s5 3 7.5 0 3.5-2 5-1"/>',
  "drop":'<path d="M12 3s7 7.5 7 12a7 7 0 0 1-14 0c0-4.5 7-12 7-12z"/>',
  "hex":'<path d="M12 2l8.7 5v10L12 22l-8.7-5V7z"/>',
  "aur":'<path d="M3 18c3-8 6-12 9-12s6 4 9 12"/><path d="M7 18c2-5 3.5-7 5-7s3 2 5 7"/>',
  "mesh":'<circle cx="8" cy="9" r="5"/><circle cx="16" cy="15" r="5"/>',
  "cycle":'<path d="M21 12a9 9 0 1 1-3-6.7L21 8"/><path d="M21 3v5h-5"/>',
  "globe":'<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
  "x":'<path d="M18 6L6 18M6 6l12 12"/>',
  "image":'<rect x="3" y="3" width="18" height="18" rx="3"/><circle cx="9" cy="9" r="2"/><path d="M21 15l-5-5L5 21"/>',
}
out = sys.argv[1]; os.makedirs(out, exist_ok=True)
tmp = tempfile.mkdtemp()
for name, body in ICONS.items():
    html = os.path.join(tmp, name + ".html")
    open(html, "w").write(f'<!doctype html><html><body style="margin:0;background:transparent"><svg xmlns="http://www.w3.org/2000/svg" width="96" height="96" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="display:block">{body}</svg></body></html>')
    dst = os.path.join(out, f"ic_{name}.png")
    subprocess.run([CHROME, "--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
                    "--default-background-color=00000000", "--window-size=96,96", f"--screenshot={dst}", "file://" + html],
                   check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    print("ic_" + name)
