#!/usr/bin/env node
// Framefall — рендер іконок атласу, лого, іконки лаунчера й іконки сповіщення.
// Без npm-залежностей: Google Chrome (headless) робить один скріншот-аркуш,
// ffmpeg ріже його на окремі PNG.
//
//   node assets/tools/render_brand.mjs [--work <тимчасова тека>] [--chrome <шлях>]
//
// Джерела: `const P={...}` з prototype/index.html (іконки 24×24) і assets/brand/logo.svg.
import { readFileSync, writeFileSync, mkdirSync, copyFileSync, existsSync } from "node:fs";
import { execFileSync } from "node:child_process";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { tmpdir } from "node:os";

const HERE = dirname(fileURLToPath(import.meta.url));
const FF = resolve(HERE, "../..");                       // .../Game Framefall
const ASSETS = join(FF, "assets");
const arg = (k, d) => { const i = process.argv.indexOf(k); return i > 0 ? process.argv[i + 1] : d; };
const WORK = resolve(arg("--work", join(tmpdir(), "ff_render")));
const CHROME = arg("--chrome", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
const FFMPEG = arg("--ffmpeg", "ffmpeg");
const REDWAVE_ICONS = resolve(FF, "../Game Redwave/assets/all/icons");
mkdirSync(WORK, { recursive: true });

// ---------- 1. Шляхи іконок із прототипу ----------
const html = readFileSync(join(FF, "prototype/index.html"), "utf8");
const m = html.match(/const P=(\{[\s\S]*?\n\});/);
if (!m) throw new Error("const P={...} не знайдено в prototype/index.html");
const P = Function('"use strict";return (' + m[1] + ")")();

const EXTRA = {
  chevDown: '<path d="m6 9 6 6 6-6"/>',
  expand: '<path d="M15 3h6v6M9 21H3v-6M21 3l-7 7M3 21l7-7"/>',
  volume: '<path d="M11 5 6 9H2v6h4l5 4z"/><path d="M15.5 8.5a5 5 0 0 1 0 7M19 5a10 10 0 0 1 0 14"/>',
  mute: '<path d="M11 5 6 9H2v6h4l5 4z"/><path d="m22 9-6 6M16 9l6 6"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  // крапки «ще»: залиті кола r=1.6 (без обводки)
  moreFill: '<circle cx="12" cy="5" r="1.6"/><circle cx="12" cy="12" r="1.6"/><circle cx="12" cy="19" r="1.6"/>',
};
const SRC = { ...P, ...EXTRA };

// вихідне ім'я ← ключ, режим: s = обводка 2, f = заливка
const ICONS = [
  ["ic_download", "save"], ["ic_compass", "compass"], ["ic_grid", "grid"], ["ic_wand", "wand"],
  ["ic_link", "link"], ["ic_clip", "clip"], ["ic_search", "search"], ["ic_check", "check"],
  ["ic_x", "x"], ["ic_gear", "gear"], ["ic_share", "share"], ["ic_more", "moreFill", "f"],
  ["ic_play_fill", "play", "f"], ["ic_pause_fill", "pause", "f"], ["ic_rew10", "rew"], ["ic_fwd10", "fwd"],
  ["ic_pip", "pip"], ["ic_scissors", "scissors"], ["ic_music", "music"], ["ic_gif", "gif"],
  ["ic_split", "split"], ["ic_image", "image"], ["ic_compress", "compress"], ["ic_film", "film"],
  ["ic_shield", "shield"], ["ic_lock", "lock"], ["ic_info", "info"], ["ic_globe", "globe"],
  ["ic_cloud", "cloud"], ["ic_chev_right", "chev"], ["ic_chev_left", "back"], ["ic_chev_up", "up"],
  ["ic_chev_down", "chevDown"], ["ic_phone", "phone"], ["ic_msg", "msg"], ["ic_camera", "cam"],
  ["ic_folder", "folder"], ["ic_trash", "trash"], ["ic_loop", "loop"], ["ic_gauge", "gauge"],
  ["ic_bell", "bell"], ["ic_wifi", "wifi"], ["ic_alert", "alert"], ["ic_ext", "ext"],
  ["ic_home", "homeic"], ["ic_layers", "layers"], ["ic_file", "note"], ["ic_clock", "clock"],
  ["ic_star", "star"], ["ic_expand", "expand"], ["ic_volume", "volume"], ["ic_mute", "mute"],
  ["ic_plus", "plus"],
];
const COPY_FROM_REDWAVE = ["ic_heart", "ic_ban", "ic_wifi_off", "ic_moon", "ic_timer", "ic_sliders"];

const iconSvg = (key, mode, px) => {
  const body = SRC[key];
  if (!body) throw new Error("немає іконки " + key);
  const paint = mode === "f"
    ? 'fill="#fff" stroke="none"'
    : 'fill="none" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"';
  return `<svg xmlns="http://www.w3.org/2000/svg" width="${px}" height="${px}" viewBox="0 0 24 24" style="color:#fff" ${paint}>${body}</svg>`;
};

// ---------- 2. Лого й шари лаунчера ----------
const GRAD = (id, attrs = 'x1="0" y1="0" x2="1" y2="1"') =>
  `<linearGradient id="${id}" ${attrs}><stop offset="0" stop-color="#FFC94D"/><stop offset=".55" stop-color="#FF8A2A"/><stop offset="1" stop-color="#F2511B"/></linearGradient>`;
const SP = (x, y) => `<rect x="${x}" y="${y}" width="2.6" height="2.6" rx=".8"/>`;
const HOLES =                                       // перфорація, трикутник «play ↓», смужка (координати лого 48×48)
  SP(11.6, 13.2) + SP(11.6, 19.8) + SP(11.6, 26.4) + SP(33.8, 13.2) + SP(33.8, 19.8) + SP(33.8, 26.4) +
  '<path d="M18.6 16.6h10.8a1.6 1.6 0 0 1 1.35 2.45l-5.4 8.6a1.6 1.6 0 0 1-2.7 0l-5.4-8.6a1.6 1.6 0 0 1 1.35-2.45z"/>' +
  '<rect x="18" y="31" width="12" height="3" rx="1.5"/>';
const FRAME = '<rect x="9.5" y="10" width="29" height="28" rx="6.5"/>';
const LOGO_SVG = readFileSync(join(ASSETS, "brand/logo.svg"), "utf8");
const sized = (svg, px) => svg.replace(/width="\d+" height="\d+"/, `width="${px}" height="${px}"`);

// гліф у координатах лого (центр 24,24), кольоровий: біла рамка + градієнтні отвори
const glyphColor = (id) => `<g fill="#fff">${FRAME}</g><g fill="url(#${id})">${HOLES}</g>`;
// одноколірний: біла рамка, отвори прозорі (маска)
const glyphMono = (id) =>
  `<mask id="${id}" maskUnits="userSpaceOnUse" x="0" y="0" width="48" height="48"><g fill="#fff">${FRAME}</g><g fill="#000">${HOLES}</g></mask>` +
  `<rect x="0" y="0" width="48" height="48" fill="#fff" mask="url(#${id})"/>`;

// Адаптивна іконка: полотно 108dp. Масштаб гліфа S (одиниць лого → dp): найдальша точка
// скругленої рамки = S·(√(8²+7.5²)+6.5) = S·17.47 → при S=1.86 це 32.5dp < 33dp (безпечне коло).
const S = 1.86;
const T = `translate(54 54) scale(${S}) translate(-24 -24)`;
const L_BG = (px) => `<svg xmlns="http://www.w3.org/2000/svg" width="${px}" height="${px}" viewBox="0 0 108 108"><defs>${GRAD("b", 'gradientUnits="userSpaceOnUse" x1="18" y1="18" x2="90" y2="90"')}</defs><rect width="108" height="108" fill="url(#b)"/></svg>`;
const L_FG = (px) => `<svg xmlns="http://www.w3.org/2000/svg" width="${px}" height="${px}" viewBox="0 0 108 108"><defs>${GRAD("g")}</defs><g transform="${T}">${glyphColor("g")}</g></svg>`;
const L_MONO = (px) => `<svg xmlns="http://www.w3.org/2000/svg" width="${px}" height="${px}" viewBox="0 0 108 108"><defs></defs><g transform="${T}">${glyphMono("mm")}</g></svg>`;
const L_ROUND = (px) => `<svg xmlns="http://www.w3.org/2000/svg" width="${px}" height="${px}" viewBox="0 0 48 48"><defs>${GRAD("g")}</defs><circle cx="24" cy="24" r="24" fill="url(#g)"/>${glyphColor("g")}</svg>`;

const DENS = [["mdpi", 1], ["hdpi", 1.5], ["xhdpi", 2], ["xxhdpi", 3], ["xxxhdpi", 4]];

// ---------- 3. Список усього, що рендеримо ----------
const items = [];   // {out, svg, w, h, white, opaque}
const ICON_DIR = join(ASSETS, "all/icons");
for (const [out, key, mode] of ICONS) items.push({ out: join(ICON_DIR, out + ".png"), svg: iconSvg(key, mode || "s", 96), w: 96, h: 96, white: true });
items.push({ out: join(ICON_DIR, "ph_glyph.png"), svg: iconSvg("film", "s", 192), w: 192, h: 192, white: true });
items.push({ out: join(ASSETS, "textures/logo.png"), svg: sized(LOGO_SVG, 192), w: 192, h: 192 });
for (const [d, k] of DENS) {
  const dir = join(ASSETS, "brand/android-res/mipmap-" + d);
  const a = Math.round(108 * k), b = Math.round(48 * k);
  items.push({ out: join(dir, "ic_launcher_background.png"), svg: L_BG(a), w: a, h: a, opaque: true });
  items.push({ out: join(dir, "ic_launcher_foreground.png"), svg: L_FG(a), w: a, h: a });
  items.push({ out: join(dir, "ic_launcher_monochrome.png"), svg: L_MONO(a), w: a, h: a, white: true });
  items.push({ out: join(dir, "ic_launcher.png"), svg: sized(LOGO_SVG, b), w: b, h: b });
  items.push({ out: join(dir, "ic_launcher_round.png"), svg: L_ROUND(b), w: b, h: b });
}

// ---------- 4. Аркуш: полиці шириною 1600 px, один скріншот Chrome ----------
const PAGE_W = 1600, GAP = 4;
let x = 0, y = 0, shelf = 0;
for (const it of items) {
  if (x + it.w > PAGE_W) { x = 0; y += shelf + GAP; shelf = 0; }
  it.x = x; it.y = y; x += it.w + GAP; shelf = Math.max(shelf, it.h);
}
const PAGE_H = y + shelf;
// у кожного SVG свої id градієнтів/масок — щоб не конфліктували на одній сторінці, робимо унікальними
let uid = 0;
const uniq = (svg) => { const n = ++uid; return svg.replace(/id="(\w+)"/g, `id="$1_${n}"`).replace(/url\(#(\w+)\)/g, `url(#$1_${n})`); };
const page = `<!doctype html><html><head><meta charset="utf-8"><style>html,body{margin:0;padding:0;background:transparent;overflow:hidden}
svg{position:absolute;display:block}text{font-family:"Helvetica Neue",Helvetica,Arial,sans-serif}</style></head><body>` +
  items.map((it) => uniq(it.svg).replace("<svg ", `<svg style="left:${it.x}px;top:${it.y}px;color:#fff" `)).join("\n") + "</body></html>";
const pageFile = join(WORK, "sheet.html"), shot = join(WORK, "sheet.png");
writeFileSync(pageFile, page);
execFileSync(CHROME, ["--headless=new", "--disable-gpu", "--hide-scrollbars", "--force-device-scale-factor=1",
  "--default-background-color=00000000", "--user-data-dir=" + join(WORK, "chrome-profile"),
  `--window-size=${PAGE_W},${PAGE_H}`, "--screenshot=" + shot, "file://" + pageFile], { stdio: "ignore" });

// ---------- 5. Нарізка ----------
for (const it of items) {
  mkdirSync(dirname(it.out), { recursive: true });
  let vf = `crop=${it.w}:${it.h}:${it.x}:${it.y},format=rgba`;
  if (it.white) vf += ",lutrgb=r=255:g=255:b=255";        // чистий білий, лишається тільки альфа
  const pix = it.opaque ? "rgb24" : "rgba";
  execFileSync(FFMPEG, ["-nostdin", "-loglevel", "error", "-y", "-i", shot, "-vf", vf + ",format=" + pix, "-frames:v", "1", it.out]);
}
for (const n of COPY_FROM_REDWAVE) copyFileSync(join(REDWAVE_ICONS, n + ".png"), join(ICON_DIR, n + ".png"));

// ---------- 6. Іконка сповіщення (VectorDrawable 24dp) ----------
// Гліф лого в 24×24: x' = 0.75x − 6, y' = 0.75y − 6. Заливка evenOdd: рамка мінус отвори.
const f = (v) => +v.toFixed(3);
const rr = (x0, y0, w, h, r) => {                  // скруглений прямокутник як підшлях
  const X = (v) => f(0.75 * v - 6), Y = X, R = f(0.75 * r);
  const a = X(x0), b = Y(y0), c = X(x0 + w), d = Y(y0 + h);
  return `M${f(a + R)},${b}H${f(c - R)}A${R},${R} 0,0 1,${c},${f(b + R)}V${f(d - R)}A${R},${R} 0,0 1,${f(c - R)},${d}` +
    `H${f(a + R)}A${R},${R} 0,0 1,${a},${f(d - R)}V${f(b + R)}A${R},${R} 0,0 1,${f(a + R)},${b}Z`;
};
const tri = "M7.95,6.45h8.1a1.2,1.2 0,0 1,1.013,1.838l-4.05,6.45a1.2,1.2 0,0 1,-2.025,0l-4.05,-6.45a1.2,1.2 0,0 1,1.013,-1.838Z";
const notifPath = [rr(9.5, 10, 29, 28, 6.5),
  ...[[11.6, 13.2], [11.6, 19.8], [11.6, 26.4], [33.8, 13.2], [33.8, 19.8], [33.8, 26.4]].map(([px, py]) => rr(px, py, 2.6, 2.6, 0.8)),
  tri, rr(18, 31, 12, 3, 1.5)].join("");
const NOTIF = `<?xml version="1.0" encoding="utf-8"?>
<!-- Framefall: силует лого (кадр плівки з трикутником «play ↓») для іконки сповіщення. Згенеровано assets/tools/render_brand.mjs -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#FFFFFFFF"
        android:fillType="evenOdd"
        android:pathData="${notifPath}" />
</vector>
`;
mkdirSync(join(ASSETS, "brand/android-res/drawable"), { recursive: true });
writeFileSync(join(ASSETS, "brand/android-res/drawable/ic_notification.xml"), NOTIF);
// SVG-двійник для перевірки очима
writeFileSync(join(WORK, "ic_notification_preview.svg"),
  `<svg xmlns="http://www.w3.org/2000/svg" width="240" height="240" viewBox="0 0 24 24"><path fill="#fff" fill-rule="evenodd" d="${notifPath}"/></svg>`);

console.log(`готово: ${items.length} PNG з аркуша ${PAGE_W}×${PAGE_H}, ${COPY_FROM_REDWAVE.length} скопійовано з Redwave, ic_notification.xml`);
