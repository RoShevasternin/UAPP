#!/usr/bin/env node
/*
 * Redwave — рендер графіки з HTML-прототипу (іконки атласу, лого, лаунчер-іконки, Google Play).
 *
 * Запуск (з будь-якої теки):
 *   node render_brand.js [--proto <prototype/index.html>] [--out <тека Redwave>]
 *                        [--work <тимчасова тека>] [--chrome <шлях до chrome>]
 *                        [--only icons,logo,launcher,market,feature,shots] [--time 2026-10-06T09:41:00]
 *
 * За замовчуванням --proto = ../../../prototype/index.html, --out = ../../.. (відносно цього файлу).
 * Іконки (P, F), GLYPH і LOGO() читаються прямо з <script> прототипу — змінили там, перезапустили тут.
 * Після рендеру запусти finalize.py (знімає альфу з маркет-PNG, перевіряє розміри, збирає контакт-лист)
 * або просто run_all.sh.
 *
 * Потрібно: Node 18+, playwright (локально або глобально, `npm i -g playwright`), Chromium,
 * доступ до fonts.googleapis.com (шрифти Archivo / Figtree / JetBrains Mono).
 */
"use strict";
const fs = require("fs"), path = require("path"), os = require("os"), vm = require("vm");
const { execSync } = require("child_process");

/* ---------- аргументи ---------- */
const argv = process.argv.slice(2), opt = {};
for (let i = 0; i < argv.length; i++) {
  const a = argv[i];
  if (a.startsWith("--")) { const k = a.slice(2); const v = argv[i + 1] && !argv[i + 1].startsWith("--") ? argv[++i] : true; opt[k] = v; }
}
const HERE = __dirname;
const PROTO = path.resolve(opt.proto || path.join(HERE, "../../../prototype/index.html"));
const OUT = path.resolve(opt.out || path.join(HERE, "../../.."));
const WORK = path.resolve(opt.work || fs.mkdtempSync(path.join(os.tmpdir(), "redwave-brand-")));
const ONLY = new Set(String(opt.only || "icons,logo,launcher,market,feature,shots").split(","));
const CHROME = opt.chrome || process.env.CHROME_PATH ||
  ["/opt/pw-browsers/chromium-1194/chrome-linux/chrome"].find(p => fs.existsSync(p));

const D = {
  icons: path.join(OUT, "assets/all/icons"),
  brand: path.join(OUT, "assets/brand"),
  svg: path.join(OUT, "assets/brand/svg"),
  svgIcons: path.join(OUT, "assets/brand/svg/icons"),
  res: path.join(OUT, "assets/brand/android-res"),
  market: path.join(OUT, "market"),
};
const mk = d => fs.mkdirSync(d, { recursive: true });
Object.values(D).forEach(mk); mk(WORK);

function loadPlaywright() {
  try { return require("playwright"); }
  catch (e) { return require(path.join(execSync("npm root -g").toString().trim(), "playwright")); }
}

/* ---------- читаємо прототип ---------- */
const html = fs.readFileSync(PROTO, "utf8");
// Вирізає JS-літерал, що починається з першої "{" після маркера (з урахуванням рядків у лапках).
function grabObject(marker) {
  const at = html.indexOf(marker);
  if (at < 0) throw new Error("Не знайдено в прототипі: " + marker);
  let i = html.indexOf("{", at), depth = 0, q = null;
  const start = i;
  for (; i < html.length; i++) {
    const c = html[i];
    if (q) { if (c === "\\") i++; else if (c === q) q = null; continue; }
    if (c === "'" || c === '"' || c === "`") q = c;
    else if (c === "{") depth++;
    else if (c === "}" && --depth === 0) return html.slice(start, i + 1);
  }
  throw new Error("Незакритий блок: " + marker);
}
const P = vm.runInNewContext("(" + grabObject("const P=") + ")");
const F = vm.runInNewContext("(" + grabObject("const F=") + ")");
const logoSrc = "function LOGO(s)" + grabObject("function LOGO(s)");
const LOGO = vm.runInNewContext("let gid=0;" + logoSrc + ";LOGO");
const GLYPH = vm.runInNewContext(html.match(/const GLYPH=('[^\n]*?');/)[1]);

// Складники лого: кольори градієнта, радіус, група гліфа (смуги + стрілка).
const logo48 = LOGO(48);
const STOPS = [...logo48.matchAll(/<stop offset="([\d.]+)" stop-color="(#[0-9a-fA-F]{3,8})"\/>/g)].map(m => ({ o: m[1], c: m[2] }));
const RX = +logo48.match(/<rect[^>]*rx="([\d.]+)"/)[1];
const LOGO_VB = +logo48.match(/viewBox="0 0 ([\d.]+) /)[1];
const GLYPH_G = logo48.match(/<g fill="none"[\s\S]*?<\/g>/)[0];
const GLYPH_SW = +GLYPH_G.match(/stroke-width="([\d.]+)"/)[1];

const NAME_MAP = { chevD: "chev_down", chevR: "chev_right", chevL: "chev_left", wifiOff: "wifi_off" };
const snake = n => NAME_MAP[n] || n.replace(/([a-z0-9])([A-Z])/g, "$1_$2").toLowerCase();
const XMLNS = 'xmlns="http://www.w3.org/2000/svg"';
const strokeSvg = (inner, s = 24) => `<svg ${XMLNS} width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="#FFFFFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">${inner}</svg>`;
const fillSvg = (inner, s = 24) => `<svg ${XMLNS} width="${s}" height="${s}" viewBox="0 0 24 24" fill="#FFFFFF" stroke="none">${inner}</svg>`;
const glyphSvg = (s = 24) => GLYPH.replace("<svg ", `<svg ${XMLNS} width="${s}" height="${s}" `).replace(/stroke="#fff"/i, 'stroke="#FFFFFF"');
const gradDefs = (id, attrs) => `<defs><linearGradient id="${id}" ${attrs}>${STOPS.map(s => `<stop offset="${s.o}" stop-color="${s.c}"/>`).join("")}</linearGradient></defs>`;
const logoSvg = (s, rx = RX) => `<svg ${XMLNS} width="${s}" height="${s}" viewBox="0 0 ${LOGO_VB} ${LOGO_VB}">${gradDefs("g", 'x1="0" y1="0" x2="1" y2="1"')}<rect width="${LOGO_VB}" height="${LOGO_VB}" rx="${rx}" fill="url(#g)"/>${GLYPH_G}</svg>`;
const logoRoundSvg = s => { const h = LOGO_VB / 2; return `<svg ${XMLNS} width="${s}" height="${s}" viewBox="0 0 ${LOGO_VB} ${LOGO_VB}">${gradDefs("g", 'x1="0" y1="0" x2="1" y2="1"')}<circle cx="${h}" cy="${h}" r="${h}" fill="url(#g)"/>${GLYPH_G}</svg>`; };

/* ---------- рендер ---------- */
const write = (f, s) => { fs.writeFileSync(f, s); };
const made = [];
async function shotSvg(page, svg, w, h, file) {
  await page.setViewportSize({ width: w, height: h });
  await page.setContent(`<!doctype html><html><head><style>html,body{margin:0;padding:0;background:transparent;overflow:hidden}svg{display:block;width:${w}px;height:${h}px}</style></head><body>${svg}</body></html>`);
  await page.screenshot({ path: file, omitBackground: true, clip: { x: 0, y: 0, width: w, height: h } });
  made.push(path.relative(OUT, file));
}

(async () => {
  const pw = loadPlaywright();
  const proxy = process.env.HTTPS_PROXY || process.env.https_proxy;
  const browser = await pw.chromium.launch({ executablePath: CHROME, proxy: proxy ? { server: proxy } : undefined });
  const page = await browser.newPage({ deviceScaleFactor: 1 });

  // Габарити гліфа лого (заливка getBBox + половина товщини лінії: круглі кінці/з'єднання).
  await page.setContent(`<svg ${XMLNS} width="480" height="480" viewBox="0 0 ${LOGO_VB} ${LOGO_VB}">${GLYPH_G}</svg>`);
  const bb = await page.evaluate(sw => { const b = document.querySelector("g").getBBox(); return { x: b.x - sw / 2, y: b.y - sw / 2, w: b.width + sw, h: b.height + sw }; }, GLYPH_SW);
  const glyphAt = (canvas, targetW) => { // гліф у центрі полотна canvas×canvas, ширина рамки = targetW
    const k = targetW / bb.w, tx = canvas / 2 - k * (bb.x + bb.w / 2), ty = canvas / 2 - k * (bb.y + bb.h / 2);
    return `<g transform="translate(${tx.toFixed(4)} ${ty.toFixed(4)}) scale(${k.toFixed(6)})">${GLYPH_G}</g>`;
  };
  console.log("glyph bbox (logo units):", JSON.stringify(bb));

  /* (a) іконки атласу */
  if (ONLY.has("icons")) {
    for (const [n, inner] of Object.entries(P)) {
      const name = "ic_" + snake(n);
      write(path.join(D.svgIcons, name + ".svg"), strokeSvg(inner) + "\n");
      await shotSvg(page, strokeSvg(inner, 96), 96, 96, path.join(D.icons, name + ".png"));
    }
    for (const [n, inner] of Object.entries(F)) {
      const name = "ic_" + snake(n) + "_fill";
      write(path.join(D.svgIcons, name + ".svg"), fillSvg(inner) + "\n");
      await shotSvg(page, fillSvg(inner, 96), 96, 96, path.join(D.icons, name + ".png"));
    }
    write(path.join(D.svgIcons, "ph_glyph.svg"), glyphSvg() + "\n");
    await shotSvg(page, glyphSvg(192), 192, 192, path.join(D.icons, "ph_glyph.png"));
  }

  /* (b) лого */
  if (ONLY.has("logo")) {
    write(path.join(D.brand, "logo.svg"), logoSvg(LOGO_VB) + "\n");
    for (const s of [512, 192, 96, 48]) await shotSvg(page, logoSvg(s), s, s, path.join(D.brand, `logo_${s}.png`));
    const gw = `<svg ${XMLNS} width="512" height="512" viewBox="0 0 512 512">${glyphAt(512, 512 * 0.8)}</svg>`;
    write(path.join(D.svg, "logo_glyph_white.svg"), gw + "\n");
    await shotSvg(page, gw, 512, 512, path.join(D.brand, "logo_glyph_white_512.png"));
  }

  /* (c) лаунчер-іконки Android */
  if (ONLY.has("launcher")) {
    const FG_DP = 54; // ширина рамки гліфа в dp (ціль 52–56; безпечна зона — коло Ø66dp)
    // Градієнт розтягнутий на видиму частину 72×72dp (18..90), а не на все полотно 108dp,
    // щоб після маски іконка мала ті ж кольори по кутах, що й лого.
    const bgSvg = `<svg ${XMLNS} width="108" height="108" viewBox="0 0 108 108">${gradDefs("g", 'gradientUnits="userSpaceOnUse" x1="18" y1="18" x2="90" y2="90"')}<rect width="108" height="108" fill="url(#g)"/></svg>`;
    const fgSvg = `<svg ${XMLNS} width="108" height="108" viewBox="0 0 108 108">${glyphAt(108, FG_DP)}</svg>`;
    write(path.join(D.svg, "ic_launcher_background.svg"), bgSvg + "\n");
    write(path.join(D.svg, "ic_launcher_foreground.svg"), fgSvg + "\n");
    const DENS = { mdpi: 1, hdpi: 1.5, xhdpi: 2, xxhdpi: 3, xxxhdpi: 4 };
    for (const [d, f] of Object.entries(DENS)) {
      const dir = path.join(D.res, "mipmap-" + d); mk(dir);
      const a = Math.round(108 * f), l = Math.round(48 * f);
      await shotSvg(page, bgSvg, a, a, path.join(dir, "ic_launcher_background.png"));
      await shotSvg(page, fgSvg, a, a, path.join(dir, "ic_launcher_foreground.png"));
      await shotSvg(page, fgSvg, a, a, path.join(dir, "ic_launcher_monochrome.png"));
      await shotSvg(page, logoSvg(l), l, l, path.join(dir, "ic_launcher.png"));
      await shotSvg(page, logoRoundSvg(l), l, l, path.join(dir, "ic_launcher_round.png"));
    }
    const any = path.join(D.res, "mipmap-anydpi-v26"); mk(any);
    const xml = `<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@mipmap/ic_launcher_background" />
    <foreground android:drawable="@mipmap/ic_launcher_foreground" />
    <monochrome android:drawable="@mipmap/ic_launcher_monochrome" />
</adaptive-icon>
`;
    write(path.join(any, "ic_launcher.xml"), xml);
    write(path.join(any, "ic_launcher_round.xml"), xml);
    made.push("assets/brand/android-res/mipmap-anydpi-v26/ic_launcher.xml", "assets/brand/android-res/mipmap-anydpi-v26/ic_launcher_round.xml");
  }

  /* (d) Google Play: іконка 512 без прозорості */
  if (ONLY.has("market")) {
    const sq = logoSvg(512, 0);
    write(path.join(D.svg, "play_icon_512.svg"), sq + "\n");
    await shotSvg(page, sq, 512, 512, path.join(D.market, "icon_512.png"));
  }

  /* (d) Feature graphic 1024×500 — шаблон feature_graphic.html */
  if (ONLY.has("feature")) {
    const imgDir = path.join(path.dirname(PROTO), "img");
    const tpl = fs.readFileSync(path.join(HERE, "feature_graphic.html"), "utf8")
      .replace("<!--LOGO-->", LOGO(150).replace("<svg ", '<svg class="logo" '))
      .replace(/\{\{IMG\}\}/g, "file://" + imgDir);
    const f = path.join(WORK, "feature_graphic.html"); write(f, tpl);
    const fp = await browser.newPage({ viewport: { width: 1024, height: 500 }, deviceScaleFactor: 1 });
    await fp.goto("file://" + f, { waitUntil: "networkidle" });
    await fp.evaluate(() => document.fonts.ready);
    await fp.waitForFunction(() => document.body.dataset.ready === "1", null, { timeout: 15000 });
    await fp.evaluate(() => Promise.all([...document.images].map(i => i.decode().catch(() => null))));
    const fam = await fp.evaluate(() => [...document.fonts].filter(x => x.status === "loaded").map(x => x.family));
    if (!fam.includes("Archivo")) throw new Error("Archivo не завантажився (feature graphic): " + fam.join(","));
    await fp.waitForTimeout(300);
    await fp.screenshot({ path: path.join(D.market, "feature_1024x500.png"), clip: { x: 0, y: 0, width: 1024, height: 500 } });
    made.push("market/feature_1024x500.png");
    await fp.close();
  }

  /* (e) скріншоти маркету 1080×1920 з #shots прототипу */
  if (ONLY.has("shots")) {
    const wrap = path.join(WORK, "proto"); mk(wrap);
    write(path.join(wrap, "index.html"), '<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head><body>' + html + "</body></html>");
    const link = path.join(wrap, "img");
    try { fs.rmSync(link, { recursive: true, force: true }); } catch (e) {}
    fs.symlinkSync(path.join(path.dirname(PROTO), "img"), link);
    const ctx = await browser.newContext({ viewport: { width: 1080, height: 1280 }, deviceScaleFactor: 3, reducedMotion: "reduce" });
    const sp = await ctx.newPage();
    // Годинник лаунчера в кадрі 06 бере реальний час — фіксуємо 9:41, як у статус-барі кадрів.
    await sp.clock.setFixedTime(new Date(opt.time || "2026-10-06T09:41:00"));
    sp.on("pageerror", e => console.warn("page error:", e.message));
    await sp.goto("file://" + path.join(wrap, "index.html"), { waitUntil: "networkidle" });
    // Кадри — фіксовано в лівому верхньому куті, 3×2 по 360×640 CSS px без зазорів: цілі координати → рівно 1080×1920 при DSF 3.
    // FIX прототипу: правило нотаток `.f{color:var(--ok)}` (зелена галочка ✔) зачіпає й залиті іконки `svg.ic.f`
    // (play/pause/prev/next стають зеленими). У кадрах повертаємо їм колір батька, як задумано.
    await sp.addStyleTag({ content: "#shots svg.ic.f{color:inherit!important}" });
    await sp.addStyleTag({ content: "#shots{position:fixed!important;left:0!important;top:0!important;z-index:99999!important;margin:0!important;gap:0!important;grid-template-columns:repeat(3,360px)!important;width:max-content!important}.shot{border-radius:0!important;box-shadow:none!important;width:360px!important;height:640px!important}" });
    await sp.evaluate(() => new Promise(r => requestAnimationFrame(() => requestAnimationFrame(r))));
    await sp.evaluate(() => window.dispatchEvent(new Event("resize")));
    await sp.evaluate(() => document.fonts.ready);
    // Дочекатися фонових картинок (обкладинки — background-image).
    await sp.evaluate(async () => {
      const urls = new Set();
      document.querySelectorAll("#shots *").forEach(el => { const b = getComputedStyle(el).backgroundImage; (b.match(/url\("([^"]+)"\)/g) || []).forEach(u => urls.add(u.slice(5, -2))); });
      await Promise.all([...urls].map(u => new Promise(r => { const i = new Image(); i.onload = i.onerror = r; i.src = u; })));
      await Promise.all([...document.querySelectorAll("#shots img")].map(i => i.decode().catch(() => null)));
    });
    const fam = await sp.evaluate(() => [...new Set([...document.fonts].filter(x => x.status === "loaded").map(x => x.family))]);
    for (const need of ["Archivo", "Figtree", "JetBrains Mono"]) if (!fam.includes(need)) throw new Error(need + " не завантажився: " + fam.join(","));
    // Масштаб телефону в кадрі: ResizeObserver прототипу ставить --k = ширина*0.72/370. Перевіряємо й за потреби ставимо самі.
    const ks = await sp.evaluate(() => [...document.querySelectorAll(".shot")].map(s => {
      const want = (s.clientWidth * .72 / 370).toFixed(4); if (s.style.getPropertyValue("--k") !== want) s.style.setProperty("--k", want);
      const r = s.getBoundingClientRect(); return { k: s.style.getPropertyValue("--k"), x: r.x, y: r.y, w: r.width, h: r.height };
    }));
    console.log("shots:", JSON.stringify(ks));
    await sp.waitForTimeout(700);
    const shots = sp.locator("#shots .shot");
    const n = await shots.count();
    for (let i = 0; i < n; i++) {
      const file = path.join(D.market, `screenshot_${String(i + 1).padStart(2, "0")}.png`);
      const b = ks[i];
      await sp.screenshot({ path: file, clip: { x: b.x, y: b.y, width: b.w, height: b.h } });
      made.push(path.relative(OUT, file));
    }
    // Підписи кадрів — для market/README.md
    const caps = await sp.evaluate(() => [...document.querySelectorAll(".shot .cap")].map(c => ({ title: c.querySelector("h3").innerText.replace(/\s+/g, " ").trim(), sub: c.querySelector("p").innerText.trim() })));
    write(path.join(WORK, "captions.json"), JSON.stringify(caps, null, 1));
    console.log("captions:", JSON.stringify(caps));
    await ctx.close();
  }

  await browser.close();
  console.log("work dir:", WORK);
  console.log(made.length + " files:\n  " + made.join("\n  "));
})().catch(e => { console.error(e); process.exit(1); });
