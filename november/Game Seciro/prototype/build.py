#!/usr/bin/env python3
# ─────────────────────────────────────────────────────────────────────────────
# Збірка прототипу Seciro: seciro.jsx → seciro.html (одна сторінка, як в Orbit Dash).
#
#   python3 prototype/build.py            # зібрати
#   python3 prototype/build.py --check    # лише перевірити, що збирається
#
# Node не потрібен: esbuild — нативний бінарник, React — готові пакети з npm; build.py качає
# їх у prototype/.tools/ при першому запуску (у git не йдуть). Версії як в Orbit Dash.
#
# Ілюстрації й іконки — SVG прямо з Figma (claude/figma/art, claude/figma/icons): esbuild
# вбудовує їх у сторінку як data: URI, тож сторінка самодостатня.
#
# Сторінка = shell.html + бандл на місці <!--BUNDLE-->. Публікує Claude на claude.ai тим
# самим URL (посилання — у claude/README.md). BUILD у seciro.jsx піднімати на кожну зміну.
# ─────────────────────────────────────────────────────────────────────────────
import io, os, platform, subprocess, sys, tarfile, urllib.request

HERE  = os.path.dirname(os.path.abspath(__file__))
TOOLS = os.path.join(HERE, ".tools")
NM    = os.path.join(TOOLS, "node_modules")
SRC   = os.path.join(HERE, "seciro.jsx")
SHELL = os.path.join(HERE, "shell.html")
OUT   = os.path.join(HERE, "seciro.html")

ESBUILD_PKG = {
    ("Darwin", "arm64"):  "@esbuild/darwin-arm64",
    ("Darwin", "x86_64"): "@esbuild/darwin-x64",
    ("Linux",  "x86_64"): "@esbuild/linux-x64",
    ("Linux",  "aarch64"): "@esbuild/linux-arm64",
}.get((platform.system(), platform.machine()))
if not ESBUILD_PKG:
    sys.exit(f"esbuild: немає пакета під {platform.system()} {platform.machine()}")

PACKAGES = {
    "react":     "18.3.1",
    "react-dom": "18.3.1",
    "scheduler": "0.23.2",
    ESBUILD_PKG: "0.28.2",
}
ESBUILD = os.path.join(NM, *ESBUILD_PKG.split("/"), "bin", "esbuild")


def fetch(name, version):
    """Пакет із npm у node_modules — без npm: tgz з реєстру, розпакувати."""
    dst = os.path.join(NM, name)
    if os.path.isdir(dst):
        return
    base = name.split("/")[-1]
    url = f"https://registry.npmjs.org/{name}/-/{base}-{version}.tgz"
    print("fetch", name, version)
    data = urllib.request.urlopen(url).read()
    with tarfile.open(fileobj=io.BytesIO(data), mode="r:gz") as tar:
        for m in tar.getmembers():
            m.name = m.name.split("/", 1)[1] if "/" in m.name else ""
            if m.name and not m.name.startswith("/") and ".." not in m.name.split("/"):
                tar.extract(m, dst)
    if name.startswith("@esbuild/"):
        os.chmod(ESBUILD, 0o755)


def gen_layers():
    """Ілюстрації онбордингу пошарово (claude/figma/layers) → .tools/layers.gen.js: {кадр: [{src, name, x, y, w, h}]}."""
    import json
    base = os.path.join(HERE, "..", "claude", "figma")
    data = json.load(open(os.path.join(base, "layers", "layers.json")))
    lines, out = [], {}
    n = 0
    for fid, layers in data.items():
        out[fid] = []
        for l in layers:
            var = f"L{n}"; n += 1
            lines.append(f"import {var} from {json.dumps(os.path.abspath(os.path.join(base, l['file'])))};")
            out[fid].append((var, l))
    body = ",\n".join(
        json.dumps(fid) + ": [" + ", ".join(
            "{src: %s, name: %s, x: %s, y: %s, w: %s, h: %s}" % (v, json.dumps(l["name"]), l["x"], l["y"], l["w"], l["h"]) for v, l in ls) + "]"
        for fid, ls in out.items())
    with open(os.path.join(TOOLS, "layers.gen.js"), "w") as f:
        f.write("\n".join(lines) + "\nexport default {\n" + body + "\n};\n")


def gen_frames():
    """Кадри Figma для колонки порівняння: PNG @2x → JPEG у .tools/frames (сторінка легша в рази; proto-diff бере PNG)."""
    from PIL import Image
    src = os.path.join(HERE, "..", "claude", "figma", "frames")
    dst = os.path.join(TOOLS, "frames"); os.makedirs(dst, exist_ok=True)
    for name in os.listdir(src):
        if not name.endswith(".png"): continue
        a, b = os.path.join(src, name), os.path.join(dst, name[:-4] + ".jpg")
        if os.path.exists(b) and os.path.getmtime(b) >= os.path.getmtime(a): continue
        im = Image.open(a).convert("RGB")
        # колонка порівняння ~430 css px: 640 px вистачає й на ретині; так сторінка тримається під 16 МБ (усі 75 кадрів)
        if im.width > 640: im = im.resize((640, round(im.height * 640 / im.width)), Image.LANCZOS)
        im.save(b, quality=80, optimize=True, progressive=True)


def gen_fig():
    """Кадри для рендерера (claude/figma/r3 …: <кадр>.json + PNG векторних шарів) → .tools/fig.gen.js:
    FRAMES {id: дерево}, PNG {"r3/<hash>.png": src}."""
    import json, glob
    base = os.path.join(HERE, "..", "claude", "figma")
    lines, frames, pngs = [], [], {}
    for d in sorted(glob.glob(os.path.join(base, "r[0-9]*"))):
        for jf in sorted(glob.glob(os.path.join(d, "*.json"))):
            var = "F" + os.path.basename(jf)[:-5].replace("-", "_")
            lines.append(f"import {var} from {json.dumps(os.path.abspath(jf))};")
            frames.append((os.path.basename(jf)[:-5].replace("-", ":"), var))
            def walk(n):
                if n.get("png"): pngs[n["png"]] = os.path.abspath(os.path.join(base, n["png"]))
                for c in n.get("children", []): walk(c)
            walk(json.load(open(jf)))
    for i, (k, path) in enumerate(sorted(pngs.items())):
        lines.append(f"import P{i} from {json.dumps(path)};")
    body = "export const FRAMES = {" + ", ".join(f"{json.dumps(k)}: {v}" for k, v in frames) + "};\n"
    body += "export const PNG = {" + ", ".join(f"{json.dumps(k)}: P{i}" for i, (k, _) in enumerate(sorted(pngs.items()))) + "};\n"
    with open(os.path.join(TOOLS, "fig.gen.js"), "w") as f:
        f.write("\n".join(lines) + "\n" + body)


def bundle():
    entry = os.path.join(TOOLS, "entry.jsx")
    with open(entry, "w") as f:
        f.write('import { createRoot } from "react-dom/client";\n'
                f'import Seciro from {SRC!r};\n'
                'const mount = () => createRoot(document.getElementById("root")).render(<Seciro />);\n'
                'const ready = document.fonts ? document.fonts.ready : Promise.resolve();\n'
                'Promise.race([ready, new Promise((r) => setTimeout(r, 2500))]).catch(() => {}).then(mount);\n')
    out = os.path.join(TOOLS, "bundle.js")
    env = dict(os.environ, NODE_PATH=NM)
    r = subprocess.run([ESBUILD, entry, "--bundle", "--minify", "--format=iife", "--charset=utf8",
                        "--jsx=automatic", "--target=es2020", "--loader:.svg=dataurl", "--loader:.png=dataurl", "--loader:.jpg=dataurl",
                        "--define:process.env.NODE_ENV=\"production\"",
                        f"--outfile={out}", "--log-level=warning"],
                       env=env, capture_output=True, encoding="utf-8", errors="replace")
    if r.returncode != 0:
        sys.exit(r.stderr)
    if r.stderr.strip():
        print(r.stderr.strip())
    return open(out, encoding="utf-8").read()


def main():
    os.makedirs(NM, exist_ok=True)
    for name, ver in PACKAGES.items():
        fetch(name, ver)
    gen_layers()
    gen_frames()
    gen_fig()
    js = bundle()
    if "--check" in sys.argv:
        print("ok, bundle", len(js) // 1024, "KB")
        return
    shell = open(SHELL, encoding="utf-8").read()
    page = shell.replace("<!--BUNDLE-->", "<script>" + js.replace("</script", "<\\/script") + "</script>")
    open(OUT, "w", encoding="utf-8").write(page)
    print("→", OUT, len(page) // 1024, "KB")


if __name__ == "__main__":
    main()
