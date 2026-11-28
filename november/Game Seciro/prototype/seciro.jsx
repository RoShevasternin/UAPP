// ─────────────────────────────────────────────────────────────────────────────
// Seciro — клікабельний прототип (джерело правди для екранів і переходів).
// Правило: спершу екран тут → VELDAN проклацує → лише тоді Kotlin + LibGDX.
//
// Усі числа — з Figma «UAPP 2.0» / секція «Seciro All-in-One» (5:1685), кадр 375×812.
// Знімки шарів — claude/figma/json/<node>.json, ілюстрації — claude/figma/art (SVG з Figma 1:1).
// Позиції — абсолютні координати кадру, як у Figma, щоб звіряти «піксель у піксель».
// ─────────────────────────────────────────────────────────────────────────────
import { Component, useEffect, useRef, useState } from "react";
import LAYERS from "./.tools/layers.gen.js";

import logoSplash from "../claude/figma/art3x/5-2530_logo_5-2554.png";
import logoHeader from "../claude/figma/art3x/5-2620_logo_5-2651.png";
import pwHeader from "../claude/figma/art3x/5-2948_logo_5-3052.png";
import shape2 from "../claude/figma/art3x/5-2557_Shape_Set_5-2558.png";
import shape3 from "../claude/figma/art3x/5-2683_Shape_Set_5-2684.png";
import shape4 from "../claude/figma/art3x/5-2747_Shape_Set_5-2748.png";
import shape5 from "../claude/figma/art3x/5-2815_Shape_Set_5-2816.png";
import shape6 from "../claude/figma/art3x/5-2874_Shape_Set_5-2875.png";
import imgPw from "../claude/figma/art3x/5-2948_img_5-2950.png";
import icVpnLock from "../claude/figma/icons/vpn-lock.svg";
import icSecurity from "../claude/figma/icons/security.svg";
import icQr from "../claude/figma/icons/qr-code.svg";
import icSparkles from "../claude/figma/icons/sparkles.svg";
import icEmail from "../claude/figma/icons/alternate-email.svg";
import icKey from "../claude/figma/icons/vpn-key.svg";
import icShield from "../claude/figma/icons/shield.svg";
import icSettings from "../claude/figma/icons/settings.svg";
// Кадри Figma @2x — для звірки поруч із телефоном (і накладанням поверх)
import fr01 from "./.tools/frames/r1_01_5-2530.jpg";
import fr02 from "./.tools/frames/r1_02_5-2620.jpg";
import fr03 from "./.tools/frames/r1_03_5-2557.jpg";
import fr04 from "./.tools/frames/r1_04_5-2683.jpg";
import fr05 from "./.tools/frames/r1_05_5-2747.jpg";
import fr06 from "./.tools/frames/r1_06_5-2815.jpg";
import fr07 from "./.tools/frames/r1_07_5-2874.jpg";
import fr08 from "./.tools/frames/r1_08_5-2948.jpg";
import fr09 from "./.tools/frames/r1_09_5-3055.jpg";
import fr10 from "./.tools/frames/r1_10_5-3162.jpg";

// Ряд 2 — вхід
import hdrBack from "../claude/figma/art3x/5-4374_logo_5-4424.png";
import imgSignin from "../claude/figma/art3x/5-4374_img_5-4376.png";
import imgOffline from "../claude/figma/art3x/5-5047_img_5-5108.png";
import imgCode from "../claude/figma/art3x/5-4433_img_5-4435.png";
import imgEnded from "../claude/figma/art3x/5-4668_img_5-4670.png";
import imgNone from "../claude/figma/art3x/5-4722_img_5-4724.png";
import kbAlpha from "../claude/figma/keyboard/kb-alpha.png";
import kbNum from "../claude/figma/keyboard/kb-numeric.png";
import icMail from "../claude/figma/icons/mail.svg";
import icInfo from "../claude/figma/icons/info.svg";
import r2f01 from "./.tools/frames/r2_01_5-4374.jpg";
import r2f02 from "./.tools/frames/r2_02_5-4923.jpg";
import r2f03 from "./.tools/frames/r2_03_5-4985.jpg";
import r2f04 from "./.tools/frames/r2_04_5-5047.jpg";
import r2f05 from "./.tools/frames/r2_05_5-4433.jpg";
import r2f06 from "./.tools/frames/r2_06_5-4775.jpg";
import r2f07 from "./.tools/frames/r2_07_5-4846.jpg";
import r2f08 from "./.tools/frames/r2_08_5-4502.jpg";
import r2f09 from "./.tools/frames/r2_09_5-4589.jpg";
import r2f10 from "./.tools/frames/r2_10_5-4668.jpg";
import r2f11 from "./.tools/frames/r2_11_5-4722.jpg";

// Ряд 3 — рендерер з даних Figma
import { FigFrame, FigNode, FigText, frameOf, findNode, PNG } from "./fig.jsx";
import { NOTES, KINDS, NEW, NEW_BUILD, NEW_LABEL, statusOf, examplesOf } from "./notes.js";
import r3f01 from "./.tools/frames/r3_01_5-1686.jpg";
import r3f02 from "./.tools/frames/r3_02_5-8170.jpg";
import r3f03 from "./.tools/frames/r3_03_5-2114.jpg";
import r3f04 from "./.tools/frames/r3_04_5-1784.jpg";
import r3f05 from "./.tools/frames/r3_05_5-1868.jpg";
import r3f06 from "./.tools/frames/r3_06_5-1995.jpg";
import r3f07 from "./.tools/frames/r3_07_5-3269.jpg";
import r4f01 from "./.tools/frames/r4_01_5-3826.jpg";
import r4f02 from "./.tools/frames/r4_02_5-3427.jpg";
import r4f03 from "./.tools/frames/r4_03_5-3521.jpg";
import r4f04 from "./.tools/frames/r4_04_5-3695.jpg";
import r4f05 from "./.tools/frames/r4_05_5-7547.jpg";
import r4f06 from "./.tools/frames/r4_06_5-7646.jpg";
import r4f07 from "./.tools/frames/r4_07_5-7916.jpg";
import r4f08 from "./.tools/frames/r4_08_5-8043.jpg";
import r5f01 from "./.tools/frames/r5_01_5-3919.jpg";
import r5f02 from "./.tools/frames/r5_02_5-6791.jpg";
import r5f03 from "./.tools/frames/r5_03_5-7412.jpg";
import r5f04 from "./.tools/frames/r5_04_5-7024.jpg";
import r5f05 from "./.tools/frames/r5_05_5-7121.jpg";
import r5f06 from "./.tools/frames/r5_06_5-7218.jpg";
import r5f07 from "./.tools/frames/r5_07_5-7315.jpg";
import r5f08 from "./.tools/frames/r5_08_5-6927.jpg";
import r6f01 from "./.tools/frames/r6_01_5-4054.jpg";
import r6f02 from "./.tools/frames/r6_02_5-6417.jpg";
import r6f03 from "./.tools/frames/r6_03_5-6490.jpg";
import r6f04 from "./.tools/frames/r6_04_5-6569.jpg";
import r6f05 from "./.tools/frames/r6_05_5-6619.jpg";
import r6f06 from "./.tools/frames/r6_06_5-6705.jpg";
import r7f01 from "./.tools/frames/r7_01_5-4126.jpg";
import r7f02 from "./.tools/frames/r7_02_5-6268.jpg";
import r7f03 from "./.tools/frames/r7_03_5-8199.jpg";
import r7f04 from "./.tools/frames/r7_04_5-6106.jpg";
import r7f05 from "./.tools/frames/r7_05_5-6203.jpg";
import r7f06 from "./.tools/frames/r7_06_5-8234.jpg";
import r7f07 from "./.tools/frames/r7_07_5-8296.jpg";
import r8f01 from "./.tools/frames/r8_01_5-4268.jpg";
import r8f02 from "./.tools/frames/r8_02_5-5869.jpg";
import r8f03 from "./.tools/frames/r8_03_5-5542.jpg";
import r8f04 from "./.tools/frames/r8_04_5-5976.jpg";
import r8f05 from "./.tools/frames/r8_05_5-6041.jpg";
import r8f06 from "./.tools/frames/r8_06_5-8265.jpg";
import r8f07 from "./.tools/frames/r8_07_5-5649.jpg";
import r8f08 from "./.tools/frames/r8_08_5-5705.jpg";
import r8f09 from "./.tools/frames/r8_09_5-5781.jpg";
import r9f01 from "./.tools/frames/r9_01_5-5141.jpg";
import r9f02 from "./.tools/frames/r9_02_5-5443.jpg";
import r9f03 from "./.tools/frames/r9_03_5-5239.jpg";
import r9f04 from "./.tools/frames/r9_04_5-5341.jpg";
import r10f01 from "./.tools/frames/r10_01_5-2466.jpg";
import r10f02 from "./.tools/frames/r10_02_5-2193.jpg";
import r10f03 from "./.tools/frames/r10_03_5-2343.jpg";
import ringFull from "../claude/figma/extra/loading_last_mask.png";
import loaderRing from "../claude/figma/extra/loader_1_2_2_1.png";
import loaderOk from "../claude/figma/extra/loader_1_2_2_12.png";
import mediaPhotoOn from "../claude/figma/extra/media_photo_on.jpg";
import mediaPhotoOff from "../claude/figma/extra/media_photo_off.jpg";
import mediaVideoOn from "../claude/figma/extra/media_video_on.jpg";
import mediaVideoOff from "../claude/figma/extra/media_video_off.jpg";
import r4f09 from "./.tools/frames/r4_09_5-7597.jpg";
import r4f10 from "./.tools/frames/r4_10_5-7781.jpg";
import scanTrack from "../claude/figma/scan/track.png";
import scanFull from "../claude/figma/scan/progress-full.png";
import scanRowScan from "../claude/figma/scan/row-scanning.png";
import scanRowOk from "../claude/figma/scan/row-ok.png";
import btnArc from "../claude/figma/vpnbtn/arc.png";
import logoChrome from "../claude/figma/logos/chrome_5-9492.png";
import btnX1 from "../claude/figma/vpnbtn/x1.png";
import btnX2 from "../claude/figma/vpnbtn/x2.png";

export const BUILD = 23;   // піднімати на кожну публікацію

// ── Палітра Figma ──
const C = {
  bg: "#f1f4f9", blue: "#0b62da", grey: "#737373", black: "#000000", white: "#ffffff",
  green: "#39ac60", greenT: "#2eb85c", purple: "#7c39ac", orange: "#f2a60d", red: "#d92635",
};
const a = (hex, alpha) => hex + Math.round(alpha * 255).toString(16).padStart(2, "0");
const FONT_TITLE = '"Inter Tight", "Inter", sans-serif';
const FONT_TEXT = '"Inter", sans-serif';

// ── Цеглинки ──
const Abs = ({ x, y, w, h, style, children, ...rest }) => (
  <div style={{ position: "absolute", left: x, top: y, width: w, height: h, ...style }} {...rest}>{children}</div>
);
// Ілюстрація — PNG @3x, відрендерений самою Figma (SVG-експорт Figma губить обрізання круглих рамок), w — ширина в юнітах кадру
const Art = ({ src, x, y, w }) => <img src={src} alt="" draggable={false} style={{ position: "absolute", left: x, top: y, width: w, pointerEvents: "none" }} />;
// Іконка Material з Figma як маска: колір задає код (у SVG лапки й # треба екранувати для CSS url())
const maskUrl = (src) => `url("${src.replace(/"/g, "%22").replace(/#/g, "%23")}")`;
const Icon = ({ src, color, size = 24 }) => (
  <span style={{ display: "block", width: size, height: size, background: color, WebkitMaskImage: maskUrl(src), maskImage: maskUrl(src),
    WebkitMaskSize: "contain", maskSize: "contain", WebkitMaskRepeat: "no-repeat", maskRepeat: "no-repeat", flex: "none" }} />
);
// Текст Figma: шрифт / кегль / інтерліньяж один в один
const T = ({ x, y, w, size, lh, color = C.black, title, align = "center", children, style }) => (
  <Abs x={x} y={y} w={w} style={{ fontFamily: title ? FONT_TITLE : FONT_TEXT, fontWeight: 500, fontSize: size, lineHeight: `${lh}px`,
    color, textAlign: align, whiteSpace: "pre-line", ...style }}>{children}</Abs>
);

// Btn — компонент Figma «Btn», Size=56: Prime (синя) і Third (контурна)
function Btn({ kind = "prime", y, children, onClick }) {
  const prime = kind === "prime";
  return (
    <Abs x={16} y={y} w={343} h={56} className="tap" onClick={onClick} role="button"
      style={{ borderRadius: 100, background: prime ? C.blue : C.bg, boxShadow: prime ? "none" : `inset 0 0 0 1px ${a(C.blue, .15)}`,
        display: "flex", alignItems: "center", justifyContent: "center", padding: "16px 24px",
        fontFamily: FONT_TEXT, fontSize: 16, lineHeight: "20px", color: prime ? C.white : C.blue }}>
      {children}
    </Abs>
  );
}

// Статус-бар Android (в макеті — iOS «9:41»; на Android малює система, тут — лише щоб кадр читався як телефон)
const StatusBar = () => (
  <Abs x={0} y={0} w={375} h={52} style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "4px 24px 0" }}>
    <span style={{ fontFamily: FONT_TEXT, fontSize: 14, color: C.black }}>9:41</span>
    <svg width="58" height="14" viewBox="0 0 58 14" aria-hidden="true">
      <path d="M2 12 L12 2 L12 12 Z" fill="#000" />
      <path d="M19 6.2a9.5 9.5 0 0 1 13 0L25.5 13Z" fill="#000" />
      <rect x="38" y="2" width="18" height="10" rx="2.5" fill="none" stroke="#000" strokeWidth="1.4" />
      <rect x="40" y="4" width="12" height="6" rx="1.2" fill="#000" />
    </svg>
  </Abs>
);
const GestureBar = () => <Abs x={120.5} y={799} w={134} h={5} style={{ borderRadius: 100, background: C.black }} />;

// ── Рух: пружина й криві ──
const clamp = (v, a = 0, b = 1) => Math.min(b, Math.max(a, v));
const easeOut = (t) => 1 - Math.pow(1 - t, 3);
const backOut = (t) => { const c1 = 1.70158, c3 = c1 + 1; return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2); };
const REDUCED = typeof matchMedia !== "undefined" && matchMedia("(prefers-reduced-motion: reduce)").matches;

// Пружина для пейджера: критично згасаюча, без «гумовості» на кінці (stiffness 210, damping 2√k·0.92)
function springTo(from, to, v0, onFrame, onDone) {
  if (REDUCED) { onFrame(to); onDone && onDone(); return () => {}; }
  const k = 210, c = 2 * Math.sqrt(k) * 0.92;
  let x = from, v = v0, last = performance.now(), raf;
  const step = (now) => {
    const dt = Math.min(0.032, (now - last) / 1000); last = now;
    const a = -k * (x - to) - c * v; v += a * dt; x += v * dt;
    if (Math.abs(x - to) < 0.0006 && Math.abs(v) < 0.01) { onFrame(to); onDone && onDone(); return; }
    onFrame(x); raf = requestAnimationFrame(step);
  };
  raf = requestAnimationFrame(step);
  return () => cancelAnimationFrame(raf);
}

// ── Екрани ──
// Splash: 2.2 с, далі великий логотип зменшується й підіймається точно на місце логотипа шапки онбордингу
function Splash({ go, raw }) {
  const [leaving, setLeaving] = useState(false);
  useEffect(() => {
    if (raw) return;
    const t1 = setTimeout(() => setLeaving(true), 2200);
    const t2 = setTimeout(() => go("onb1", "none"), 2200 + 640);
    return () => { clearTimeout(t1); clearTimeout(t2); };
  }, []);
  const fade = { transition: "opacity .28s ease", opacity: leaving ? 0 : 1 };
  return (
    <>
      {/* центр 187.5,406 → центр логотипа шапки 187.5,84; масштаб 83/198 */}
      <Abs x={88.5} y={378} w={198} h={56} style={{ transition: "transform .64s cubic-bezier(.65,0,.35,1)",
        transform: leaving ? "translateY(-322px) scale(.4192)" : "none" }}>
        <img src={logoSplash} alt="" draggable={false} style={{ width: 198, display: "block" }} />
      </Abs>
      <Abs x={167.5} y={540} w={40} h={40} style={{ ...fade, animation: raw ? "none" : "spin 1s linear infinite" }}>
        <svg width="40" height="40" viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="none" stroke={C.blue} strokeWidth="4" strokeDasharray="84.8 28.3" /></svg>
      </Abs>
      <T x={16} y={687} w={343} size={20} lh={24} title style={fade}>Mobile Security & Private VPN</T>
      <T x={16} y={727} w={343} size={14} lh={20} color={C.grey} style={fade}>Complete protection for your device, personal data, online privacy, and digital life.</T>
    </>
  );
}

// Сторінки пейджера: 0 — вступ (дві кнопки), 1…5 — можливості (крапки 0…4)
const PAGES = [
  { id: "onb1", node: "5:2620", layers: "5:2620", title: "Protection\nMade Simple", titleY: 443,
    text: "Run one quick check to see what needs attention across your privacy, browsing,\nand storage.", textY: 531 },
  { id: "onb2", node: "5:2557", layers: "5:2557", shape: shape2, title: "Your iPhone,\nProtected", titleY: 535,
    text: "Run a full security scan to keep your iPhone safe, private, and running at its best.", textY: 623 },
  { id: "onb3", node: "5:2683", layers: "5:2683", shape: shape3, title: "Private Internet\nAccess", titleY: 535,
    text: "Browse securely with encrypted connections that keep your online activity truly private.", textY: 623 },
  { id: "onb4", node: "5:2747", layers: "5:2747", shape: shape4, title: "Block Online\nThreats", titleY: 535,
    text: "Block dangerous websites, hidden trackers, and phishing attacks before they reach you.", textY: 623 },
  { id: "onb5", node: "5:2815", layers: "5:2815", shape: shape5, title: "Smart QR\nProtection", titleY: 535,
    text: "Scan QR codes for malicious links before opening websites, files, or online content.", textY: 623 },
  { id: "onb6", node: "5:2874", layers: "5:2874", shape: shape6, title: "Free Up\nMore Space", titleY: 535,
    text: "Remove duplicate photos and large videos to free up space on your iPhone.", textY: 623 },
];
const ONB_IDS = PAGES.map((p) => p.id);

// Роль шару — з імені й розміру в Figma: пляма фону, лінія землі, велике (телефон, щит), дрібне (іконки), іскра
function roleOf(l) {
  if (l.name === "Shape Set") return "blob";
  if (l.h <= 2.5 && l.w >= 300) return "ground";
  if (Math.max(l.w, l.h) >= 90) return "big";
  if (/^Vector( 26)?$/.test(l.name) && Math.max(l.w, l.h) <= 32) return "spark";
  return "small";
}
function pageLayers(p) {
  const ls = (LAYERS[p.layers] || []).map((l) => ({ ...l, role: roleOf(l) }));
  if (p.shape) ls.unshift({ src: p.shape, name: "Shape Set", x: 50, y: 76, w: 325, h: 375, role: "blob" });
  // порядок каскаду: пляма → земля → великі → дрібні (порядок малювання — як у Figma)
  const rank = { blob: 0, ground: 1, big: 2, small: 3, spark: 4 };
  const order = ls.map((l, i) => [rank[l.role] * 100 + i, i]).sort((a, b) => a[0] - b[0]).map(([, i]) => i);
  order.forEach((i, k) => (ls[i].k = k));
  return ls;
}

// Шар ілюстрації: зовнішня обгортка — рух від свайпу (паралакс + поява за прогресом), внутрішня — «дихання» в спокої
function Layer({ l, d, s, n, raw }) {
  const delta = 0.032;
  // ілюстрація проявляється в другій половині підльоту (s > .3) — на середині свайпу дві сторінки майже не перетинаються
  const s2 = clamp((s - 0.3) / 0.7);
  const p = clamp((s2 - l.k * delta) / (1 - (n - 1) * delta));
  const par = { blob: 0.9, ground: 1, big: 1, small: 1.18 + (l.k % 3) * 0.07, spark: 1.3 }[l.role];
  let tf = `translateX(${(d * 375 * par).toFixed(2)}px)`, op = 1;
  if (l.role === "blob") { const e = easeOut(p); tf += ` scale(${0.8 + 0.2 * e})`; op = e * e; }
  else if (l.role === "ground") { tf += ` scaleX(${easeOut(p)})`; }
  else if (l.role === "big") { const e = easeOut(p); tf += ` translateY(${(1 - e) * 34}px)`; op = e; }
  else { const e = backOut(p); tf += ` scale(${0.35 + 0.65 * e})`; op = clamp(p * 1.6); }
  const idle = raw ? "" : { blob: "breathe", small: "float", spark: "twinkle", big: "", ground: "" }[l.role];
  return (
    <div style={{ position: "absolute", left: l.x, top: l.y, width: l.w, height: l.h, transform: tf, opacity: op,
      transformOrigin: l.role === "ground" ? "0 50%" : "50% 50%", willChange: "transform, opacity", pointerEvents: "none" }}>
      <img src={l.src} alt="" draggable={false} className={idle}
        style={{ width: l.w, height: l.h, display: "block", animationDelay: `${-(l.k * 0.73) % 4}s` }} />
    </div>
  );
}

function Page({ p, i, pos, enter, raw, onNew }) {
  const d = i - pos;
  if (Math.abs(d) >= 1.2) return null;
  const s = clamp(1 - Math.abs(d)) * enter;
  const ls = pageLayers(p);
  // текст: заголовок і опис рухаються швидше за ілюстрацію й гаснуть раніше — відчуття глибини
  // текст сусідніх сторінок ніколи не видно разом: старий гасне до середини свайпу, новий проявляється після
  const tIn = (lag) => clamp((s - 0.5 - lag) / (0.5 - lag));
  const tTitle = { transform: `translateX(${d * 375 * 0.55}px) translateY(${(1 - easeOut(tIn(0))) * 16}px)`, opacity: easeOut(tIn(0)) };
  const tText = { transform: `translateX(${d * 375 * 0.7}px) translateY(${(1 - easeOut(tIn(0.08))) * 16}px)`, opacity: easeOut(tIn(0.08)) };
  return (
    <>
      {ls.map((l, k) => <Layer key={k} l={l} d={d} s={s} n={ls.length} raw={raw} />)}
      <T x={32} y={p.titleY} w={311} size={32} lh={36} title style={tTitle}>{p.title}</T>
      <T x={32} y={p.textY} w={311} size={14} lh={20} color={C.grey} style={tText}>{p.text}</T>
      {i === 0 && (
        <div style={{ position: "absolute", inset: 0, transform: `translateX(${d * 375 * 0.7}px)`, opacity: easeOut(tIn(0.12)), pointerEvents: "none" }}>
          <div style={{ pointerEvents: "auto" }}><Btn kind="third" y={623} onClick={onNew}>I'm new here</Btn></div>
        </div>
      )}
    </>
  );
}

// navig: крапки за безперервною позицією — активна тягнеться 4 → 16 рівно за пальцем (сума ширин стала, ряд не стрибає)
const Dots = ({ y, pos }) => (
  <Abs x={145.5} y={y} w={84} h={16} style={{ display: "flex", gap: 10, padding: 6, alignItems: "center" }}>
    {[0, 1, 2, 3, 4].map((j) => {
      const t = clamp(1 - Math.abs(pos - j));
      return <span key={j} style={{ width: 4 + 12 * t, height: 4, borderRadius: 2, background: C.black, opacity: 0.25 + 0.75 * t, flex: "none" }} />;
    })}
  </Abs>
);

// Пейджер онбордингу: свайп пальцем / мишею, пружина, кнопка гортає тією ж пружиною
function Onboarding({ index, onIndex, go, raw, at }) {
  const [pos, setPos] = useState(at ?? index);
  const [enter, setEnter] = useState(raw ? 1 : 0);
  const posRef = useRef(index), stop = useRef(() => {}), drag = useRef(null);
  const set = (v) => { posRef.current = v; setPos(v); window.__seciroPos = v; };   // __seciroPos — для автотесту свайпу
  const settle = (to, v0 = 0) => {
    stop.current();
    stop.current = springTo(posRef.current, to, v0, set, () => onIndex(to));
  };
  // поява першого екрана (після splash) — та сама хореографія, що й від свайпу
  useEffect(() => { if (raw) return; return springTo(0, 1, 0, setEnter); }, []);
  // вибір сторінки ззовні (панель, ← →)
  useEffect(() => { if (raw) return; if (Math.abs(posRef.current - index) > 0.001) settle(index); }, [index]);
  useEffect(() => () => stop.current(), []);

  const down = (e) => { stop.current(); drag.current = { x0: e.clientX, p0: posRef.current, t: performance.now(), x: e.clientX, v: 0, moved: false, id: e.pointerId, el: e.currentTarget }; };
  const move = (e) => {
    const g = drag.current; if (!g) return;
    const scale = g.el.getBoundingClientRect().width / 375;
    const dx = (e.clientX - g.x0) / scale;
    if (!g.moved && Math.abs(dx) > 6) { g.moved = true; try { g.el.setPointerCapture(g.id); } catch {} }
    if (!g.moved) return;
    const now = performance.now(), dt = Math.max(1, now - g.t);
    g.v = 0.8 * g.v + 0.2 * (-(e.clientX - g.x) / scale / 375) / (dt / 1000); g.t = now; g.x = e.clientX;
    let p = g.p0 - dx / 375;
    if (p < 0) p = p * 0.3; if (p > PAGES.length - 1) p = PAGES.length - 1 + (p - PAGES.length + 1) * 0.3;   // гумовий край
    set(p);
  };
  const up = () => {
    const g = drag.current; drag.current = null; if (!g || !g.moved) return;
    // куди доїхати: за позицією + інерцією; не далі сусідньої сторінки; швидкий змах гортає навіть короткий рух
    const from = Math.round(g.p0);
    let to = Math.round(posRef.current + clamp(g.v * 0.18, -0.6, 0.6));
    to = clamp(to, from - 1, from + 1);
    if (Math.abs(g.v) > 0.6 && to === from) to = from + Math.sign(g.v);
    to = clamp(to, 0, PAGES.length - 1);
    settle(to, g.v);
  };

  const cur = Math.round(pos);
  const cta = (i) => (i === 0 ? "I already have an account" : i === PAGES.length - 1 ? "Get Started" : "Continue");
  const onCta = () => {
    if (cur === 0) go("signin");
    else if (cur === PAGES.length - 1) go("paywall", "up");
    else settle(cur + 1);
  };
  // підпис кнопки перетікає між сусідніми сторінками
  const lo = Math.floor(clamp(pos, 0, PAGES.length - 1)), hi = Math.min(PAGES.length - 1, lo + 1), f = clamp(pos - lo);
  // підпис міняється у вузькому вікні посередині свайпу: старий іде вгору, новий приходить знизу, не перекриваючись
  const sw = clamp((f - 0.5) / 0.22, -1, 1);
  const labels = cta(lo) === cta(hi) ? [[cta(lo), 1, 0]] : [[cta(lo), clamp(-sw), -6 * (1 - clamp(-sw))], [cta(hi), clamp(sw), 6 * (1 - clamp(sw))]];
  const fixedIn = { opacity: clamp(enter * 1.4 - 0.4), transform: `translateY(${(1 - easeOut(enter)) * 14}px)` };

  return (
    <div style={{ position: "absolute", inset: 0, touchAction: "pan-y", cursor: drag.current?.moved ? "grabbing" : "grab" }}
      onPointerDown={down} onPointerMove={move} onPointerUp={up} onPointerCancel={up}>
      {PAGES.map((p, i) => <Page key={p.id} p={p} i={i} pos={pos} enter={enter} raw={raw} onNew={() => settle(1)} />)}
      <Art src={logoHeader} w={375} x={0} y={52} />
      <Abs x={16} y={695} w={343} h={56} className="tap" role="button" onClick={onCta}
        style={{ ...fixedIn, borderRadius: 100, background: C.blue, color: C.white, fontFamily: FONT_TEXT, fontSize: 16, lineHeight: "20px" }}>
        {labels.map(([t, o, y]) => (
          <span key={t} style={{ position: "absolute", inset: 0, display: "flex", alignItems: "center", justifyContent: "center", opacity: o,
            transform: `translateY(${y}px)` }}>{t}</span>
        ))}
      </Abs>
      <div style={fixedIn}><Dots y={767} pos={Math.max(0, pos - 1)} /></div>
    </div>
  );
}

// ── Paywall ──
const FEATURES = [
  { icon: icVpnLock, color: C.blue, title: "Private VPN", text: "Browse securely anywhere" },
  { icon: icSecurity, color: C.green, bg: C.greenT, title: "Scam & Ad Blocker", text: "Block scams, trackers & ads" },
  { icon: icQr, color: C.orange, title: "QR Link Checker", text: "Check links before opening" },
  { icon: icSparkles, color: C.purple, title: "Duplicate Cleaner", text: "Free up space fast" },
  { icon: icEmail, color: C.red, title: "Breach Check", text: "See if your data was leaked" },
  { icon: icKey, color: C.grey, title: "Password Generator", text: "Create strong passwords instantly" },
  { icon: icShield, color: C.blue, title: "Protection Review", text: "See your security status" },
];
const PLANS = [
  { id: "weekly", name: "Weekly", price: "$8.99", day: "$1.29/day", tag: "3 day free trial", tagColor: C.green, tagBg: C.greenT,
    cta: "Try 3 Days for $0.99", node: "5:2948" },
  { id: "monthly", name: "Monthly", price: "$29.99", day: "$0.99/day", tag: "Popular", tagColor: C.blue, tagBg: C.blue,
    cta: "Subscribe for $29.99 / month", node: "5:3055" },
  { id: "yearly", name: "Yearly", price: "$69.99", day: "$0.19/day", tag: "Saving", tagColor: C.purple, tagBg: C.purple,
    cta: "Subscribe for $69.99 / year", node: "5:3162" },
];

// Карусель можливостей: гортається пальцем і мишею (на Маці — перетягуванням)
function Carousel({ children }) {
  const ref = useRef(null);
  const drag = useRef(null);
  return (
    <div ref={ref} className="hscroll" style={{ position: "absolute", left: 0, top: 371, width: 375, height: 92, paddingLeft: 16 }}
      onPointerDown={(e) => { if (e.pointerType === "mouse") drag.current = { x: e.clientX, s: ref.current.scrollLeft }; }}
      onPointerMove={(e) => { if (drag.current) ref.current.scrollLeft = drag.current.s - (e.clientX - drag.current.x); }}
      onPointerUp={() => (drag.current = null)} onPointerLeave={() => (drag.current = null)}>
      <div style={{ display: "flex", gap: 10, width: "max-content", paddingRight: 16, paddingBottom: 20 }}>{children}</div>
    </div>
  );
}

function FeatureCard({ f }) {
  return (
    <div style={{ display: "flex", gap: 12, alignItems: "center", padding: 16, height: 72, background: C.white, borderRadius: 16,
      boxShadow: `0 4px 16px ${a("#084191", .1)}`, flex: "none" }}>
      <div style={{ width: 40, height: 40, borderRadius: 12, background: a(f.bg || f.color, .15), display: "flex", alignItems: "center", justifyContent: "center" }}>
        <Icon src={f.icon} color={f.color} />
      </div>
      <div style={{ display: "flex", flexDirection: "column", gap: 4 }}>
        <span style={{ fontSize: 14, lineHeight: "20px", color: C.black, whiteSpace: "nowrap" }}>{f.title}</span>
        <span style={{ fontSize: 12, lineHeight: "16px", color: C.grey, whiteSpace: "nowrap" }}>{f.text}</span>
      </div>
    </div>
  );
}

// «Plans rev»: on — біла з синьою рамкою, тег суцільний; off — 15 % синього, тег 15 % свого кольору
function PlanCard({ p, on, onClick }) {
  return (
    <div className="tap" role="radio" aria-checked={on} onClick={onClick}
      style={{ width: 109, height: 114, borderRadius: 16, padding: "8px 16px 16px", display: "flex", flexDirection: "column", alignItems: "center", gap: 8,
        background: on ? C.white : a(C.blue, .15),
        // рамка Figma «всередину» не зсуває вміст — тому тінь-обвідка, а не border
        boxShadow: `inset 0 0 0 1px ${on ? C.blue : a(C.blue, .15)}`, transition: "background .15s, box-shadow .15s" }}>
      <span style={{ height: 18, borderRadius: 12, padding: "2px 8px", fontSize: 9, lineHeight: "14px", whiteSpace: "nowrap",
        background: on ? p.tagColor : a(p.tagBg, .15), color: on ? C.white : p.tagColor }}>{p.tag}</span>
      <span style={{ fontSize: 14, lineHeight: "20px", textAlign: "center", whiteSpace: "pre-line" }}>{`${p.name}\n${p.price}`}</span>
      <span style={{ fontSize: 12, lineHeight: "16px", color: C.grey }}>{p.day}</span>
    </div>
  );
}

function Paywall({ go, toast, plan, setPlan }) {
  const [sheet, setSheet] = useState(false);
  const p = PLANS.find((x) => x.id === plan);
  return (
    <>
      <Art src={imgPw} w={187.667} x={93.75} y={49.75} />
      <Art src={pwHeader} w={375} x={0} y={52} />
      {/* хіт-зони поверх SVG шапки: хрестик і Restore */}
      <Abs x={8} y={64} w={40} h={40} className="tap" role="button" aria-label="Close" onClick={() => go("dashboard")} style={{ borderRadius: 20 }} />
      <Abs x={306} y={66} w={61} h={36} className="tap" role="button" onClick={() => toast("Restoring purchases… No active subscription found on this Google account.")} />
      <T x={16} y={227} w={343} size={32} lh={36} title><span style={{ color: C.blue }}>One App.</span>{"\nFewer Digital Worries."}</T>
      <T x={16} y={315} w={343} size={12} lh={16}>Unlock every Seciro tool and keep your connection, browsing, links, and storage under control</T>
      <Carousel>{FEATURES.map((f) => <FeatureCard key={f.title} f={f} />)}</Carousel>
      <Abs x={16} y={459} w={343} h={114} style={{ display: "flex", gap: 8 }} role="radiogroup">
        {PLANS.map((x) => <PlanCard key={x.id} p={x} on={x.id === plan} onClick={() => setPlan(x.id)} />)}
      </Abs>
      {/* У Figma — SF Pro Display Medium 14 (шрифт Apple, на Android не можна). Найближчий з макета — Inter Tight;
          інтервал −0.13 px дає ту саму ширину рядка, що й Figma: 203 px (заміряно 24.11) */}
      <T x={0} y={589} w={375} size={14} lh={18} title style={{ letterSpacing: "-0.13px" }}>Auto-Renewable, Cancel Anytime</T>
      <Btn kind="third" y={623} onClick={() => go("signin")}>I already have an account</Btn>
      <Btn y={695} onClick={() => setSheet(true)}>{p.cta}</Btn>
      <Abs x={0} y={767} w={375} h={16} style={{ display: "flex", gap: 36, justifyContent: "center", opacity: .75, fontSize: 12, lineHeight: "16px", color: C.grey }}>
        <span className="tap" onClick={() => toast("Opens Terms of Use")}>Terms of Use</span>
        <span className="tap" onClick={() => toast("Opens Privacy Policy")}>Privacy Policy</span>
      </Abs>
      {sheet && <PlaySheet plan={p} onClose={() => setSheet(false)} onBuy={() => { setSheet(false); go("dashboard"); }} />}
    </>
  );
}

// Імітація вікна оплати Google Play — лише щоб пройти потік; справжнє вікно малює Play
function PlaySheet({ plan, onClose, onBuy }) {
  return (
    <Abs x={0} y={0} w={375} h={812} className="enter-fade" style={{ background: "rgba(0,0,0,.4)", zIndex: 20 }} onClick={onClose}>
      <div onClick={(e) => e.stopPropagation()} style={{ position: "absolute", left: 0, right: 0, bottom: 0, background: "#fff", borderRadius: "24px 24px 0 0",
        padding: "20px 24px 36px", display: "flex", flexDirection: "column", gap: 14, animation: "sheet .28s cubic-bezier(.2,.8,.2,1)" }}>
        <div style={{ fontSize: 11, letterSpacing: ".08em", textTransform: "uppercase", color: C.grey }}>Google Play · mock</div>
        <div style={{ fontSize: 18, lineHeight: "24px" }}>Seciro Premium — {plan.name}</div>
        <div style={{ fontSize: 14, lineHeight: "20px", color: C.grey }}>
          {plan.id === "weekly" ? "3 days for $0.99, then $8.99 / week" : plan.id === "monthly" ? "$29.99 / month" : "$69.99 / year"} · Cancel anytime in Google Play
        </div>
        <div className="tap" onClick={onBuy} style={{ marginTop: 6, height: 48, borderRadius: 100, background: "#01875f", color: "#fff", display: "flex",
          alignItems: "center", justifyContent: "center", fontSize: 15 }}>Subscribe</div>
      </div>
    </Abs>
  );
}

// Заглушка для екранів наступних рядів Figma
function Next({ title, note, back }) {
  return (
    <Abs x={0} y={0} w={375} h={812} style={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", gap: 12, padding: 32 }}>
      <div style={{ fontFamily: FONT_TITLE, fontSize: 28, lineHeight: "32px" }}>{title}</div>
      <div style={{ fontSize: 14, lineHeight: "20px", color: C.grey, textAlign: "center" }}>{note}</div>
      <div className="tap" onClick={back} style={{ marginTop: 12, padding: "12px 24px", borderRadius: 100, border: `1px solid ${a(C.blue, .15)}`, color: C.blue }}>Back</div>
    </Abs>
  );
}

// ── Ряд 2: вхід за поштою, код, стани підписки ──
// Кадри Figma: Sign in 5:4374 / 5:4923 / 5:4985, Can't connect 5:5047, код 5:4433 / 5:4775 / 5:4846, Verifying 5:4502,
// помилка коду 5:4589, підписка скінчилась 5:4668, немає підписки 5:4722. Числа — claude/figma/json/<кадр>.json.
// Клавіатура — картинка з Figma (у макеті iOS; на Android буде системна) з клікабельними клавішами за їхніми координатами.
const AUTH_IDS = ["signin", "signin-typing", "signin-filled", "offline", "code", "code-typing", "code-filled", "verifying", "code-error", "sub-ended", "no-sub"];
const AUTH_PRESET = {
  "signin": { step: "email", email: "", focus: false },
  "signin-typing": { step: "email", email: "", focus: true },
  "signin-filled": { step: "email", email: "name@example.com", focus: true },
  "offline": { step: "email", email: "name@example.com", focus: false, sheet: "offline" },
  "code": { step: "code", email: "name@example.com", code: "", focus: false, timer: 0 },
  "code-typing": { step: "code", email: "name@example.com", code: "", focus: true, timer: 29 },
  "code-filled": { step: "code", email: "name@example.com", code: "123457", focus: true, timer: 29 },
  "verifying": { step: "code", email: "name@example.com", code: "123456", focus: false, timer: 29, sheet: "verifying" },
  "code-error": { step: "code", email: "name@example.com", code: "123456", focus: false, timer: 0, error: true },
  "sub-ended": { step: "ended", email: "name@example.com" },
  "no-sub": { step: "none", email: "name@example.com" },
};
const authId = (s) => s.step === "ended" ? "sub-ended" : s.step === "none" ? "no-sub"
  : s.step === "email" ? (s.sheet === "offline" ? "offline" : !s.focus ? "signin" : s.email ? "signin-filled" : "signin-typing")
  : s.sheet === "verifying" ? "verifying" : s.error ? "code-error" : !s.focus ? "code" : s.code ? "code-filled" : "code-typing";
const EMAIL_OK = /^[^@\s]+@[^@\s]+\.[a-z]{2,}$/i;

// Клавіші літерної клавіатури (кадр 5:4923, Keyboards → рядки): x — від лівого краю, y — верх рядка в кадрі
const KB_ROWS = [
  { y: 536, keys: "qwertyuiop".split("").map((c, i) => [c, [0, 37.5, 74.04, 112.5, 149.04, 186.54, 224.04, 260.58, 299.04, 334.58][i], 32]) },
  { y: 590.3, keys: "asdfghjkl".split("").map((c, i) => [c, [18.27, 55.77, 93.27, 129.77, 168.27, 204.81, 242.31, 279.81, 317.31][i], 32]) },
  { y: 644.6, keys: [["shift", 0, 42.3], ..."zxcvbnm".split("").map((c, i) => [c, [55.77, 93.27, 129.77, 168.27, 204.81, 242.31, 279.81][i], 32]), ["del", 325, 42.3]] },
  { y: 698.9, keys: [["123", 0, 87.5], ["space", 93.27, 180.8], ["return", 279.81, 87.5]] },
];
// Цифрова (кадр 5:4775): три стовпці по 117, рядки по ~53
const NUM_ROWS = [532.5, 585.7, 638.9, 692.4].map((y, r) => ({ y, keys: (r < 3 ? [1, 2, 3].map((n) => String(r * 3 + n)) : ["", "0", "del"]).map((c, i) => [c, [6, 129.1, 252.2][i] - 4, 117]) }));

function Keyboard({ kind, show, onKey }) {
  const [hit, setHit] = useState(null);
  const rows = kind === "num" ? NUM_ROWS : KB_ROWS;
  return (
    <Abs x={0} y={486} w={375} h={326} style={{ transform: show ? "none" : "translateY(330px)", transition: "transform .42s cubic-bezier(.2,.8,.2,1)",
      backdropFilter: "blur(26px)", WebkitBackdropFilter: "blur(26px)", zIndex: 5 }}>
      <img src={kind === "num" ? kbNum : kbAlpha} alt="" draggable={false} style={{ width: 375, display: "block" }} />
      {rows.map((row) => row.keys.map(([c, x, w]) => c && (
        <div key={c} onPointerDown={(e) => { e.preventDefault(); setHit(c); onKey(c); }} onPointerUp={() => setHit(null)} onPointerLeave={() => setHit(null)}
          style={{ position: "absolute", left: 4 + x, top: row.y - 486, width: w, height: kind === "num" ? 46 : 42.3, borderRadius: 5, cursor: "pointer",
            background: hit === c ? "rgba(0,0,0,.14)" : "transparent", transition: "background .12s" }} />
      )))}
    </Abs>
  );
}

const Caret = ({ h = 20, color = C.blue }) => <span className="caret" style={{ display: "inline-block", width: 1, height: h, background: color, flex: "none" }} />;

// Поле пошти (картка «Favorites» кадру 5:4374): мітка, поле 311×40 r12, підказка
function EmailCard({ email, focus, onFocus }) {
  return (
    <Abs x={16} y={259} w={343} h={128} style={{ background: C.white, borderRadius: 16, boxShadow: `0 4px 16px ${a("#084191", .1)}` }}>
      <Abs x={24} y={16} w={200} h={16} style={{ fontSize: 12, lineHeight: "16px", color: focus ? C.blue : C.grey, transition: "color .2s" }}>Enter email</Abs>
      <Abs x={16} y={40} w={311} h={40} onPointerDown={(e) => { e.stopPropagation(); onFocus(); }}
        style={{ borderRadius: 12, background: C.white, boxShadow: `inset 0 0 0 1px ${focus ? C.blue : a(C.grey, .15)}`, transition: "box-shadow .2s", cursor: "text",
          display: "flex", alignItems: "center", gap: 8, padding: "0 12px" }}>
        <span style={{ opacity: focus ? 1 : .5, transition: "opacity .2s" }}><Icon src={icMail} color={focus ? C.blue : C.grey} size={20} /></span>
        <span style={{ display: "flex", alignItems: "center", fontSize: 14, lineHeight: "20px", whiteSpace: "nowrap", overflow: "hidden" }}>
          {email ? <span style={{ color: C.black }}>{email}</span> : null}
          {focus && <Caret />}
          {!email && <span style={{ color: C.grey, opacity: .5 }}>name@example.com</span>}
        </span>
      </Abs>
      <Abs x={24} y={96} w={300} h={16} style={{ display: "flex", gap: 4, alignItems: "center", opacity: .75, fontSize: 12, lineHeight: "16px", color: C.grey }}>
        <Icon src={icInfo} color={C.grey} size={16} />Enter a valid email address.
      </Abs>
    </Abs>
  );
}

// Шість клітинок коду (кадр 5:4433): 48.5×40, крок 52.5; помилка — червоні (5:4589)
function CodeCard({ code, focus, error, onFocus, shake }) {
  const cur = Math.min(code.length, 5);
  const H = error ? 240 : 192, lineY = error ? 144 : 96;
  return (
    <Abs x={16} y={259} w={343} h={H} style={{ background: C.white, borderRadius: 16, boxShadow: `0 4px 16px ${a("#084191", .1)}`, transition: "height .3s cubic-bezier(.2,.8,.2,1)" }}>
      <Abs x={24} y={16} w={200} h={16} style={{ fontSize: 12, lineHeight: "16px", color: C.grey }}>Enter code</Abs>
      <Abs x={16} y={40} w={311} h={40} className={shake ? "shake" : ""} onPointerDown={(e) => { e.stopPropagation(); onFocus(); }} style={{ display: "flex", gap: 4, cursor: "text" }}>
        {[0, 1, 2, 3, 4, 5].map((i) => {
          const on = focus && !error && i === cur;
          const d = code[i];
          return (
            <div key={i} style={{ width: 48.5, height: 40, borderRadius: 12, flex: "none", display: "flex", alignItems: "center", justifyContent: "center",
              background: error ? a(C.red, .15) : C.white, boxShadow: `inset 0 0 0 1px ${error ? a(C.red, .15) : on ? C.blue : a(C.grey, .15)}`,
              transition: "background .25s, box-shadow .2s", fontFamily: FONT_TITLE, fontSize: 20, lineHeight: "24px", color: error ? C.red : C.black }}>
              {d && <span key={d + i} className="digit">{d}</span>}
              {on && <Caret h={24} />}
            </div>
          );
        })}
      </Abs>
      {error && (
        <Abs x={24} y={96} w={300} h={32} className="enter-fade" style={{ display: "flex", gap: 4, opacity: .75, fontSize: 12, lineHeight: "16px", color: C.red }}>
          <Icon src={icInfo} color={C.red} size={16} /><span style={{ width: 275 }}>{"Invalid or expired code. Try again or request a\nnew one."}</span>
        </Abs>
      )}
      <Abs x={16} y={lineY} w={311} h={1} style={{ background: a(C.grey, .15), transition: "top .3s cubic-bezier(.2,.8,.2,1)" }} />
      <Abs x={16} y={lineY + 16} w={311} h={64} style={{ transition: "top .3s cubic-bezier(.2,.8,.2,1)" }}>
        <div className="tap" data-act="other" style={{ margin: "0 auto", width: 139, fontSize: 14, lineHeight: "20px", color: C.blue, textAlign: "center",
          /* у Figma: UNDERLINE, товщина й відступ AUTO, skip ink. «auto» браузера ≠ «auto» Figma — заміряно по кадру @2x (24.11):
             лінія на 3 px нижче базової, товщина 0.5 px */
          textDecoration: "underline", textDecorationThickness: "0.5px", textUnderlineOffset: "3px", textDecorationSkipInk: "auto" }}>Use a different email</div>
        <div style={{ marginTop: 12, fontSize: 12, lineHeight: "16px", color: C.grey, textAlign: "center", whiteSpace: "pre-line" }}>{"Didn't get it? Check spam, or make sure\nit's the email from your purchase."}</div>
      </Abs>
    </Abs>
  );
}

// Аркуш знизу поверх розмитого екрана (blackout: чорний 25 % + розмиття фону)
function Sheet({ y, children, open }) {
  return (
    <>
      <Abs x={0} y={0} w={375} h={812} style={{ background: "rgba(0,0,0,.25)", backdropFilter: "blur(8px)", WebkitBackdropFilter: "blur(8px)", zIndex: 10,
        opacity: open ? 1 : 0, transition: "opacity .3s ease", pointerEvents: open ? "auto" : "none" }} />
      <Abs x={0} y={y} w={375} h={812 - y} style={{ background: C.white, borderRadius: "24px 24px 0 0", zIndex: 11,
        transform: open ? "none" : `translateY(${812 - y + 10}px)`, transition: "transform .45s cubic-bezier(.2,.9,.25,1)" }}>{children}</Abs>
    </>
  );
}

function Auth({ preset, onState, go, back, scen, toast, raw }) {
  const [s, setS] = useState(() => ({ step: "email", email: "", code: "", focus: false, timer: 0, ...AUTH_PRESET[preset] }));
  const [shake, setShake] = useState(false);
  const [busy, setBusy] = useState(false);
  // обробник клавіатури живе довше за один рендер — тому стан і відповіді «сервера» читаємо з ref, не з замикання
  const sRef = useRef(s); sRef.current = s;
  const scenRef = useRef(scen); scenRef.current = scen;
  const busyRef = useRef(false);
  const upd = (p) => setS((o) => ({ ...o, ...(typeof p === "function" ? p(o) : p) }));
  // вибір кадру в панелі → стан
  useEffect(() => { if (authId(sRef.current) !== preset) setS((o) => ({ ...o, error: false, sheet: null, ...AUTH_PRESET[preset] })); }, [preset]);
  // стан → панель і кадр Figma
  useEffect(() => { const id = authId(s); if (id !== preset) onState(id); }, [s]);
  // «Resend in Ns»
  useEffect(() => { if (raw || !(s.timer > 0)) return; const t = setTimeout(() => upd((o) => ({ timer: o.timer - 1 })), 1000); return () => clearTimeout(t); }, [s.timer]);

  const submitEmail = () => {
    if (!EMAIL_OK.test(sRef.current.email) || busyRef.current) return;
    busyRef.current = true; setBusy(true); upd({ focus: false });
    setTimeout(() => {
      busyRef.current = false; setBusy(false);
      const ans = scenRef.current.email;
      if (ans === "offline") upd({ sheet: "offline" });
      else if (ans === "none") upd({ step: "none", sheet: null });
      else { upd({ step: "code", code: "", error: false, sheet: null, timer: 30 }); setTimeout(() => upd({ focus: true }), 450); }
    }, 650);
  };
  const verify = (code) => {
    upd({ code, focus: false, sheet: "verifying" });
    setTimeout(() => {
      const ans = scenRef.current.code;
      if (ans === "wrong") { upd({ sheet: null, error: true }); setShake(true); setTimeout(() => setShake(false), 500); }
      else if (ans === "ended") upd({ sheet: null, step: "ended" });
      else { upd({ sheet: null }); go("dashboard", "up"); }
    }, 1600);
  };
  const key = (c) => {
    const o = sRef.current;
    if (o.step === "email") {
      if (c === "del") upd({ email: o.email.slice(0, -1) });
      else if (c === "return") submitEmail();
      else if (c === "123") upd({ email: o.email + (o.email.includes("@") ? "." : "@") });
      else if (c.length === 1 && /[a-z0-9@._+-]/i.test(c)) upd({ email: o.email + c.toLowerCase() });
    } else if (o.step === "code") {
      if (c === "del") upd({ code: o.code.slice(0, -1), error: false });
      else if (/^\d$/.test(c)) {
        const base = o.error ? "" : o.code;
        if (base.length >= 6) return;
        const code = base + c;
        upd({ code, error: false });
        if (code.length === 6) setTimeout(() => verify(code), 250);
      }
    }
  };
  // фізична клавіатура (на Маці)
  useEffect(() => {
    if (raw) return;
    const k = (e) => {
      const o = sRef.current; if (!o.focus || o.sheet) return;
      if (e.key === "Backspace") key("del"); else if (e.key === "Enter") key("return"); else if (e.key.length === 1) key(e.key); else return;
      e.preventDefault();
    };
    window.addEventListener("keydown", k); return () => window.removeEventListener("keydown", k);
  }, []);

  const shift = s.focus ? (s.step === "code" ? 193 : 129) : 0;
  const okEmail = EMAIL_OK.test(s.email);
  const onBack = () => { if (s.sheet) return; if (s.step === "email") back(); else upd({ step: "email", focus: true, error: false, code: "" }); };
  const result = s.step === "ended" || s.step === "none";

  return (
    <div style={{ position: "absolute", inset: 0 }} className={raw ? "raw" : ""}>
      {/* вміст під шапкою: при клавіатурі плавно їде вгору (5:4923 — на 129, 5:4775 — на 193) */}
      <Abs x={0} y={116} w={375} h={696} style={{ overflow: "hidden" }} onPointerDown={() => s.focus && upd({ focus: false })}>
        <div key={s.step} className={raw ? "" : "enter-fwd"} style={{ position: "absolute", inset: 0, transform: `translateY(${-shift}px)`, transition: "transform .42s cubic-bezier(.2,.8,.2,1)" }}
          onClick={(e) => { if (e.target.closest("[data-act=other]")) upd({ step: "email", focus: true, error: false, code: "" }); }}>
          {s.step === "email" && (<>
            <Art src={imgSignin} w={172} x={101.5} y={16} />
            <T x={16} y={167} w={343} size={20} lh={24} title>Sign in to Seciro</T>
            <T x={16} y={203} w={343} size={14} lh={20} color={C.grey}>{"Enter the email you used when\nyou purchased your subscription."}</T>
            <EmailCard email={s.email} focus={s.focus} onFocus={() => upd({ focus: true })} />
          </>)}
          {s.step === "code" && (<>
            <Art src={imgCode} w={171} x={102} y={16} />
            <T x={16} y={167} w={343} size={20} lh={24} title>Check your email</T>
            <T x={16} y={203} w={343} size={14} lh={20} color={C.grey}>
              {"If this email has a subscription, we’ve sent a code to "}<span style={{ color: C.blue }}>{s.email}.</span>{" It expires in 60 minutes."}
            </T>
            <CodeCard code={s.code} focus={s.focus} error={s.error} shake={shake} onFocus={() => upd({ focus: true, ...(s.error ? { error: false, code: "" } : {}) })} />
          </>)}
          {result && (<>
            <div className={raw ? "" : "pop-in"}><Art src={s.step === "ended" ? imgEnded : imgNone} w={171} x={102} y={16} /></div>
            <T x={16} y={167} w={343} size={20} lh={24} title>{s.step === "ended" ? "Your subscription has ended" : "No subscription on this email"}</T>
            <T x={16} y={203} w={343} size={14} lh={20} color="#737b8c">{s.step === "ended"
              ? "Your subscription ended on July 18, 2026.\nRenew to keep your iPhone protected."
              : "We couldn’t find a subscription linked to this email. Try the email you used to subscribe."}</T>
            <Btn y={271} onClick={() => go("paywall", "up")}>See plans</Btn>
            <Btn kind="third" y={343} onClick={() => upd({ step: "email", focus: true, code: "", error: false })}>Try another email</Btn>
          </>)}
        </div>
      </Abs>
      <Art src={hdrBack} w={375} x={0} y={52} />
      <Abs x={8} y={60} w={52} h={52} className="tap" role="button" aria-label="Back" onClick={onBack} style={{ borderRadius: 26 }} />

      {!result && (
        <Abs x={16} y={s.focus ? 414 : 727} w={343} h={56} style={{ transition: "top .42s cubic-bezier(.2,.8,.2,1)", zIndex: 6 }}>
          {s.step === "email" ? (
            <div className="tap" role="button" onClick={submitEmail} style={{ position: "absolute", inset: 0, borderRadius: 100, background: C.blue, opacity: okEmail ? 1 : .25,
              transition: "opacity .25s", display: "flex", alignItems: "center", justifyContent: "center", color: C.white, fontSize: 16, lineHeight: "20px",
              cursor: okEmail ? "pointer" : "default" }}>
              {busy ? <svg width="22" height="22" viewBox="0 0 40 40" style={{ animation: "spin 1s linear infinite" }}><circle cx="20" cy="20" r="17" fill="none" stroke="#fff" strokeWidth="4" strokeDasharray="80 27" /></svg> : "Continue"}
            </div>
          ) : s.timer > 0 ? (
            <div style={{ position: "absolute", inset: 0, borderRadius: 100, background: C.bg, boxShadow: `inset 0 0 0 1px ${a(C.blue, .15)}`, display: "flex",
              alignItems: "center", justifyContent: "center", fontSize: 16, lineHeight: "20px", color: C.blue }}>
              <span style={{ opacity: .5, fontVariantNumeric: "tabular-nums" }}>Resend in {s.timer}s</span>
            </div>
          ) : (
            <div className="tap" role="button" onClick={() => { upd({ timer: 30, code: "", error: false }); toast("New code sent to " + s.email); }}
              style={{ position: "absolute", inset: 0, borderRadius: 100, background: C.blue, display: "flex", alignItems: "center", justifyContent: "center",
                color: C.white, fontSize: 16, lineHeight: "20px" }}>Resend code</div>
          )}
        </Abs>
      )}

      <Keyboard kind={s.step === "code" ? "num" : "alpha"} show={s.focus && !s.sheet && !result} onKey={key} />

      <Sheet y={392} open={s.sheet === "offline"}>
        <Art src={imgOffline} w={172} x={101.5} y={48} />
        <T x={16} y={207} w={343} size={32} lh={36} title>Can't connect.</T>
        <T x={16} y={259} w={343} size={14} lh={20} color={C.grey}>Check your internet and try again.</T>
        <Btn y={327} onClick={() => { upd({ sheet: null }); setTimeout(submitEmail, 350); }}>Retry</Btn>
      </Sheet>
      <Sheet y={559} open={s.sheet === "verifying"}>
        <Abs x={167.5} y={48} w={40} h={40} style={{ animation: raw ? "none" : "spin 1s linear infinite" }}>
          <svg width="40" height="40" viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="none" stroke={C.blue} strokeWidth="4" strokeDasharray="84.8 28.3" /></svg>
        </Abs>
        <T x={16} y={136} w={343} size={20} lh={24} title>Verifying...</T>
        <T x={16} y={172} w={343} size={14} lh={20} color={C.grey}>Checking your subscription…</T>
      </Sheet>
    </div>
  );
}

// ── Ряд 3: Dashboard, сканування, результат (рендерер fig.jsx + поведінка) ──
const R3 = { "dashboard": "5:1686", "scan": "5:8170", "result-ok": "5:2114", "result-issues": "5:1784", "sheet-safari": "5:1868", "sheet-update": "5:1995", "dash-issues": "5:3269" };
const R3_IDS = Object.keys(R3);
const textOf = (n) => n.t ? n.t.runs.map((r) => r.t).join("") : (n.children || []).map(textOf).filter(Boolean).join(" / ");
const firstText = (n) => { const t = findNode(n, (x) => !!x.t); return t ? textOf(t) : ""; };
const btnWith = (frame, label) => findNode(frame, (x) => (x.comp || "").startsWith("Btn") && textOf(x) === label);
const labelsIn = (frame, menuName = "menu") => {
  const out = []; const walk = (n, inMenu) => { if (n.name === "Label" && inMenu) out.push(n); (n.children || []).forEach((c) => walk(c, inMenu || n.name === menuName)); };
  walk(frame, false); return out;
};

// Аркуш-інструкція: розмите тло + аркуш знизу з іншого кадру Figma (5:1868 — Safari, 5:1995 — Software Update)
const SHEETS = {
  safari: { frame: "5:1868", sheet: (f) => findNode(f, (x) => x.id === "5:1936") },
  update: { frame: "5:1995", sheet: (f) => findNode(f, (x) => x.name.startsWith("How to") && x.type === "FRAME") },
};
// Рядки інструкції для Android: Chrome → Settings → Privacy and security → Use secure DNS → адреса Seciro
const MUTED = C.grey, STRONG = C.black;
const Runs = ({ x, y, w, parts }) => (
  <div style={{ position: "absolute", left: x, top: y, width: w, fontSize: 14, lineHeight: "20px", color: MUTED, whiteSpace: "pre-line" }}>
    {parts.map(([t, b], i) => <span key={i} style={{ color: b ? STRONG : MUTED }}>{t}</span>)}
  </div>
);
const Tile = ({ x, y, children, app }) => (
  <div style={{ position: "absolute", left: x, top: y, width: 40, height: 40, borderRadius: app ? 10 : 12, display: "flex", alignItems: "center", justifyContent: "center",
    background: app ? C.white : a(C.grey, .15), boxShadow: app ? `0 4px 16px ${a("#084191", .12)}` : "none" }}>{children}</div>
);
function chromeOv() {
  // позиції — відносно батька шару (pos), як і решта рендерера; двохрядкові тексти ставимо з верху рядка (−10, якщо шар був однорядковим)
  const runs = (parts, dy = 0, w = 259) => (n, e, pos) => <Runs x={pos.left} y={pos.top + dy} w={w} parts={parts} />;
  const tile = (src) => (n, e, pos) => <Tile x={pos.left} y={pos.top}><Icon src={src} color={C.grey} /></Tile>;
  return {
    // логотип Chrome — з набору «Apps Logo Set» у Figma (5:9492, App=Chrome), той самий стиль плитки, що й Safari (рендер 56×56 зі зсувом −8/−4)
    "5:1943": (n, e, pos) => <img src={logoChrome} alt="" style={{ position: "absolute", left: pos.left - 8, top: pos.top - 4, width: 56, height: 56 }} />,
    "5:1944": runs([["Open "], ["Chrome", 1], [" and tap "], ["⋮", 1], [" in the\ntop-right corner."]]),
    "5:1947": tile(icSettings),
    "5:1949": runs([["Tap "], ["Settings", 1], [", then\n"], ["Privacy and security", 1], ["."]]),
    "5:1958": tile(icShield),
    "5:1961": runs([["Tap "], ["Use secure DNS", 1], [", then choose\n"], ["Custom", 1], ["."]], -10),
    "5:1964": tile(icVpnLock),
    "5:1966": runs([["Paste the "], ["Seciro address", 1], [" we copied\nfor you."]], -10),
    "5:1975": runs([["Done! "], ["Chrome", 1], [" now blocks dangerous\nsites before they load."]]),
  };
}
function FigSheet({ kind, open, onClose, raw }) {
  const f = frameOf(SHEETS[kind].frame), sheet = SHEETS[kind].sheet(f), black = findNode(f, (x) => x.name === "blackout");
  const ok = btnWith(sheet, "Got it");
  const extra = kind === "safari" ? chromeOv() : {};
  return (
    <div style={{ position: "absolute", inset: 0, zIndex: 20, pointerEvents: open ? "auto" : "none" }}>
      <div onClick={onClose} style={{ position: "absolute", inset: 0, opacity: open ? 1 : 0, transition: raw ? "none" : "opacity .3s ease" }}>
        <FigNode n={black} ov={{}} />
      </div>
      <div style={{ position: "absolute", inset: 0, transform: open ? "none" : `translateY(${812 - sheet.ay + 12}px)`, transition: raw ? "none" : "transform .5s cubic-bezier(.2,.9,.25,1)", pointerEvents: "none" }}>
        <div style={{ pointerEvents: open ? "auto" : "none" }}><FigNode n={sheet} ov={{ ...extra, [ok.id]: { onClick: onClose, className: "tap" } }} /></div>
      </div>
    </div>
  );
}

// Dashboard (5:1686 — усе гаразд, 5:3269 — є проблеми)
function Dashboard({ issues, go, toast, raw, account, onState = () => {}, scen = {} }) {
  const id = issues ? "5:3269" : "5:1686", f = frameOf(id);
  const [sheet, setSheet] = useState(null);
  const ov = {};
  const run = btnWith(f, "Run Protection Check"); ov[run.id] = { onClick: () => go("scan"), className: "tap" };
  labelsIn(f).forEach((l, i) => { const t = firstText(l); const to = { "VPN": "vpn-first", "Web Protection": "web-first", "Safe QR Scanner": "qr-first", "Storage Cleaner": "st-first", "Breach Check": "br-first", "Password Generator": "pw-first" }[t];
    ov[l.id] = { onClick: () => to ? go(to) : toast(t), className: raw ? "" : "tap rise", style: { animationDelay: `${0.12 + i * 0.05}s` } }; });
  const help = btnWith(f, "Get help"); ov[help.id] = { onClick: () => toast("Opens support chat"), className: "tap" };
  const icon = findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")); ov[icon.id] = { onClick: () => onState(scen.account === "guest" ? "account-guest" : "account"), className: "tap" };
  findNode(f, (x) => x.name === "Menubar").children.forEach((b) => {
    const t = textOf(b); if (t === "Settings") ov[b.id] = { onClick: () => go("settings", "fade"), className: "tap" };
    else if (t === "Alerts") ov[b.id] = { onClick: () => toast("Alerts — no screen in Figma (question for the manager)"), className: "tap" };
  });
  if (issues) {
    ov["5:3385"] = false;   // аркуш, що в кадрі лежить під екраном, — показуємо свій, з анімацією
    ov["5:3289"] = { onClick: () => setSheet("safari"), className: "tap" };
    ov["5:3297"] = { onClick: () => go("br-found"), className: "tap" };
  }
  const hero = findNode(f, (x) => x.name === "shield");
  if (hero && !raw) ov[hero.id] = (n, el) => <div className="float-slow">{el}</div>;
  return (
    <>
      <FigFrame id={id} ov={ov} raw={raw} />
      {issues && <FigSheet kind="safari" open={sheet === "safari"} onClose={() => setSheet(null)} raw={raw} />}
      {!issues && <AccountSheet open={!!account} guest={account === "guest"} onClose={() => onState("dashboard")} go={go} toast={toast} raw={raw} />}
    </>
  );
}

// Сканування (5:8170 + 7 варіантів компонента checklist): кільце наповнюється плавно, пункти — Waiting → Scanning… → OK
function Scan({ go, back, result, raw }) {
  const f = frameOf("5:8170");
  const rows = findNode(f, (x) => x.name === "checklist").children.filter((c) => c.type === "FRAME");
  const [p, setP] = useState(0);
  useEffect(() => {
    if (raw) return;
    const T = 1050 * rows.length, t0 = performance.now(); let raf;
    const step = (now) => { const v = Math.min(1, (now - t0) / T); setP(v); if (v < 1) raf = requestAnimationFrame(step); else setTimeout(() => go(result === "issues" ? "result-issues" : "result-ok"), 650); };
    raf = requestAnimationFrame(step); return () => cancelAnimationFrame(raf);
  }, []);
  const cur = Math.min(rows.length - 1, Math.floor(p * rows.length)), allDone = p >= 1;
  const ov = {};
  const ld = findNode(f, (x) => x.name === "loading");
  ov[ld.id] = (n, el, pos) => (
    <div style={{ ...pos }} className={raw ? "" : "breathe-soft"}>
      <img src={scanTrack} alt="" style={{ position: "absolute", inset: 0, width: 192, height: 192 }} />
      {/* заповнення: повна синя «пляма» (варіант 5 компонента) під конічною маскою — від 9 год. за годинниковою, як сектори в Figma */}
      <img src={scanFull} alt="" style={{ position: "absolute", inset: 0, width: 192, height: 192,
        WebkitMaskImage: `conic-gradient(from 270deg, #000 ${p * 360}deg, transparent ${p * 360}deg)`, maskImage: `conic-gradient(from 270deg, #000 ${p * 360}deg, transparent ${p * 360}deg)` }} />
      <div style={{ position: "absolute", left: 48, top: 78, width: 96, height: 36, textAlign: "center", fontFamily: FONT_TITLE, fontWeight: 500, fontSize: 32,
        lineHeight: "36px", color: "#0857d0", fontVariantNumeric: "tabular-nums" }}>{Math.round(p * 100)}%</div>
    </div>
  );
  rows.forEach((r, k) => {
    const st = allDone || k < cur ? "ok" : k === cur ? "scan" : "wait";
    if (raw && st === "scan" && k === 0) return;   // кадр Figma = варіант 1 як є
    const name = textOf(r.children[1]), wait = r.children[0];
    ov[r.id] = (n, el, pos) => (
      <div style={{ ...pos }}>
        {st === "wait" ? <FigNode n={wait} px={r.ax} py={r.ay} /> : st === "scan"
          ? <img src={scanRowScan} alt="" className="spin-arc" style={{ position: "absolute", left: 0, top: 0, width: 24, height: 24 }} />
          : <img src={scanRowOk} alt="" className="pop-icon" style={{ position: "absolute", left: 0, top: 0, width: 24, height: 24 }} />}
        <div style={{ position: "absolute", left: 36, top: 2, fontSize: 14, lineHeight: "20px", color: st === "wait" ? C.grey : C.black, transition: "color .3s" }}>{name}</div>
        <div key={st} className={st === "wait" ? "" : "enter-fade"} style={{ position: "absolute", right: 0, top: 2, fontSize: 14, lineHeight: "20px",
          color: st === "wait" ? a(C.grey, .15) : st === "scan" ? C.blue : C.green }}>{st === "wait" ? "Waiting" : st === "scan" ? "Scanning..." : "OK"}</div>
      </div>
    );
  });
  const bk = findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")); ov[bk.id] = { onClick: back, className: "tap" };
  return <FigFrame id="5:8170" ov={ov} raw={raw} />;
}

// Результат (5:2114 — усе пройдено, 5:1784 — 2 проблеми; аркуші — 5:1868 / 5:1995)
function Result({ preset, onState, go, toast, raw }) {
  const issues = preset !== "result-ok";
  const id = issues ? "5:1784" : "5:2114", f = frameOf(id);
  const [sheet, setSheet] = useState(preset === "sheet-safari" ? "safari" : preset === "sheet-update" ? "update" : null);
  useEffect(() => { setSheet(preset === "sheet-safari" ? "safari" : preset === "sheet-update" ? "update" : null); }, [preset]);
  useEffect(() => { if (!issues) return; const want = sheet === "safari" ? "sheet-safari" : sheet === "update" ? "sheet-update" : "result-issues"; if (want !== preset) onState(want); }, [sheet]);
  const home = () => go(issues ? "dash-issues" : "dashboard", "fade");
  const ov = {};
  const done = btnWith(f, "Done"); ov[done.id] = { onClick: home, className: "tap" };
  const bk = findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")); ov[bk.id] = { onClick: home, className: "tap" };
  labelsIn(f).forEach((l, i) => {
    const t = firstText(l);
    const act = issues && t === "Web Protection" ? () => setSheet("safari") : issues && t === "Software Update" ? () => setSheet("update") : () => toast(`${t}: passed`);
    ov[l.id] = { onClick: act, className: raw ? "" : "tap rise", style: { animationDelay: `${0.15 + i * 0.06}s` } };
  });
  const hero = findNode(f, (x) => x.name === "shield" || (x.png && x.ay < 250 && x.w > 80));
  if (hero && !raw) ov[hero.id] = (n, el) => <div className="pop-in">{el}</div>;
  return (
    <>
      <FigFrame id={id} ov={ov} raw={raw} />
      {issues && <FigSheet kind="safari" open={sheet === "safari"} onClose={() => setSheet(null)} raw={raw} />}
      {issues && <FigSheet kind="update" open={sheet === "update"} onClose={() => setSheet(null)} raw={raw} />}
    </>
  );
}

// ── Ряд 4: VPN (10 кадрів) — машина станів на рендерері ──
// Кадри: 5:3826 перший вхід · 5:3427 знайомство · 5:3521 вимкнено · 5:3695 локації · 5:7547 підключення · 5:7646 підключено (NY)
//        5:7916 / 5:8043 локації з вибором NY / Paris · 5:7597 / 5:7781 підключення / підключено Paris
const VPN_IDS = ["vpn-first", "vpn-intro", "vpn-off", "vpn-loc", "vpn-connecting", "vpn-connected", "vpn-loc-ny", "vpn-loc-paris", "vpn-connecting-paris", "vpn-connected-paris"];
const VPN_PRESET = {
  "vpn-first": { conn: "off", first: true }, "vpn-intro": { conn: "off", first: true, intro: true }, "vpn-off": { conn: "off", first: false },
  "vpn-loc": { conn: "off", first: false, sheet: true, pick: "Fastest Connection" }, "vpn-connecting": { conn: "connecting", first: false, loc: "Fastest Connection" },
  "vpn-connected": { conn: "connected", first: false, loc: "New York, US" }, "vpn-loc-ny": { conn: "connected", first: false, loc: "New York, US", sheet: true, pick: "New York, US" },
  "vpn-loc-paris": { conn: "connected", first: false, loc: "New York, US", sheet: true, pick: "Paris, FR" }, "vpn-connecting-paris": { conn: "connecting", first: false, loc: "Paris, FR" },
  "vpn-connected-paris": { conn: "connected", first: false, loc: "Paris, FR" },
};
const vpnId = (s) => s.android ? "vpn-intro" : s.intro ? "vpn-intro" : s.sheet ? (s.pick === "Paris, FR" ? "vpn-loc-paris" : s.pick === "New York, US" ? "vpn-loc-ny" : "vpn-loc")
  : s.conn === "connecting" ? (s.loc === "Paris, FR" ? "vpn-connecting-paris" : "vpn-connecting")
  : s.conn === "connected" ? (s.loc === "Paris, FR" ? "vpn-connected-paris" : "vpn-connected") : s.first ? "vpn-first" : "vpn-off";
const ELIPSE = { off: "r4/1ynu23q36wxe.jpg", on: "r4/1qnnk6w39cba.jpg" };
const STAR = { on: "r4/1rr91rm110.png", off: "r4/8vv4lg12y.png" };
// Хвилі VPN — компонент «elipse anim» Figma: 5 кіл (112 → 848, крок 184), за 5 с лінійно ростуть на крок (smart animate
// варіантів 1 → 2), найменше проявляється, найбільше гасне. Кожне коло — радіальний градієнт: прозоре до ½ радіуса, до краю — колір.
// Загальна прозорість 0.09 — підібрано по пікселях кадру 5:3521. Центр — 187,352.
function Ripple({ conn }) {
  const [t, setT] = useState(0);
  const speed = useRef(1); speed.current = conn === "connecting" ? 2.4 : 1;
  useEffect(() => { let raf, last = performance.now(), acc = 0;
    const step = (now) => { acc += (now - last) / 5000 * speed.current; last = now; setT(acc % 1); raf = requestAnimationFrame(step); };
    raf = requestAnimationFrame(step); return () => cancelAnimationFrame(raf); }, []);
  const on = conn === "connected";
  const ring = (rgb, k) => {
    const d = 112 + 184 * (k + t);
    const op = k === 0 ? t : k === 4 ? 1 - t : 1;
    return <div key={k} style={{ position: "absolute", left: 187 - d / 2, top: 352 - d / 2, width: d, height: d, borderRadius: "50%", opacity: op * 0.09,
      background: `radial-gradient(circle closest-side, rgba(${rgb},.01) 0%, rgba(${rgb},.01) 50%, rgba(${rgb},1) 100%)` }} />;
  };
  return (
    <div style={{ position: "absolute", left: 0, top: 0, width: 375, height: 812, pointerEvents: "none", overflow: "hidden" }}>
      <div style={{ position: "absolute", inset: 0, opacity: on ? 0 : 1, transition: "opacity .9s ease" }}>{[4, 3, 2, 1, 0].map((k) => ring("11,97,218", k))}</div>
      <div style={{ position: "absolute", inset: 0, opacity: on ? 1 : 0, transition: "opacity .9s ease" }}>{[4, 3, 2, 1, 0].map((k) => ring("57,172,96", k))}</div>
    </div>
  );
}
const hhmmss = (s) => [s / 3600, (s / 60) % 60, s % 60].map((v) => String(Math.floor(v)).padStart(2, "0")).join(":");

function Vpn({ preset, onState, back, toast, raw }) {
  const [s, setS] = useState(() => ({ conn: "off", loc: "Fastest Connection", first: true, intro: false, sheet: false, pick: null, android: false, favs: ["New York, US"], ...VPN_PRESET[preset] }));
  const upd = (p) => setS((o) => ({ ...o, ...(typeof p === "function" ? p(o) : p) }));
  const sRef = useRef(s); sRef.current = s;
  useEffect(() => { if (vpnId(sRef.current) !== preset) setS((o) => ({ ...o, intro: false, sheet: false, android: false, ...VPN_PRESET[preset] })); }, [preset]);
  useEffect(() => { const id = vpnId(s); if (id !== preset) onState(id); }, [s]);
  // таймер і живі швидкості
  const [now, setNow] = useState(0), [since, setSince] = useState(0), [spd, setSpd] = useState([124.1, 236.4]);
  useEffect(() => { if (raw || s.conn !== "connected") return; setSince(Date.now()); const t = setInterval(() => {
    setNow(Date.now()); setSpd(([d, u]) => [Math.max(40, d + (Math.random() - 0.5) * 18), Math.max(60, u + (Math.random() - 0.5) * 26)]); }, 1000); return () => clearInterval(t); }, [s.conn, s.loc]);
  const connect = (loc) => { upd({ conn: "connecting", loc, sheet: false, intro: false, android: false, first: false }); setTimeout(() => upd((o) => o.conn === "connecting" ? { conn: "connected" } : {}), 1900); };
  const power = () => {
    const o = sRef.current;
    if (o.conn === "off") { if (o.first) upd({ intro: true }); else connect(o.loc); }
    else upd({ conn: "off" });
  };

  // базовий кадр під стан (Paris — свої кадри; решта міст — кадри NY з підміненою карткою)
  const paris = s.loc === "Paris, FR";
  const baseId = s.conn === "connected" ? (paris ? "5:7781" : "5:7646") : s.conn === "connecting" ? (paris ? "5:7597" : "5:7547") : s.first ? "5:3826" : "5:3521";
  const f = frameOf(baseId);
  const sheetFrame = frameOf("5:3695"), sheetNode = findNode(sheetFrame, (x) => x.id === "5:3750"), sheetBlack = findNode(sheetFrame, (x) => x.name === "blackout");
  const rows = []; (function walk(n) { if (n.name === "label" && findNode(n, (x) => x.name === "flags")) rows.push(n); (n.children || []).forEach(walk); })(sheetNode);
  const rowOf = (name) => rows.find((r) => firstText(r) === name);
  const ov = {};
  // фонові кола: синє ↔ зелене перетікання + «дихання»
  const el = findNode(f, (x) => x.name === "elipse anim");
  ov[el.id] = (n, e) => !raw ? <Ripple conn={s.conn} /> : (
    <div style={{ position: "absolute", left: 0, top: 0, width: 375, height: 776, pointerEvents: "none" }}>
      <img src={PNG[ELIPSE.off]} alt="" style={{ position: "absolute", inset: 0, width: 375, height: 776, opacity: s.conn === "connected" ? 0 : 1, transition: "opacity .8s ease" }} />
      <img src={PNG[ELIPSE.on]} alt="" style={{ position: "absolute", inset: 0, width: 375, height: 776, opacity: s.conn === "connected" ? 1 : 0, transition: "opacity .8s ease" }} />
    </div>
  );
  const vb = findNode(f, (x) => x.name === "vpn btn");
  ov[vb.id] = (s.conn === "connecting" && !raw) ? (n, e, pos) => {
    // «Connecting»: статична дуга з кадру прикрита синім диском, поверх — та сама дуга з Figma, що обертається, і хрестик
    const px = n.ax - pos.left, py = n.ay - pos.top;
    return (
      <div onClick={power} className="tap" role="button" style={{ ...pos, borderRadius: "50%", cursor: "pointer" }}>
        <div style={{ position: "absolute", left: -(n.ax - px) + 0, top: -(n.ay - py), width: 0, height: 0 }}>
          {e}
          <div style={{ position: "absolute", left: 187 - px - 28, top: 352 - py - 28, width: 56, height: 56, borderRadius: "50%", background: "#0b62da" }} />
          <img src={btnArc} alt="" className="vpn-arc-spin" style={{ position: "absolute", left: 163 - px, top: 328 - py, width: 48, height: 48 }} />
          <img src={btnX1} alt="" style={{ position: "absolute", left: 178.51 - px, top: 343.51 - py, width: 16.97, height: 16.97 }} />
          <img src={btnX2} alt="" style={{ position: "absolute", left: 178.51 - px, top: 343.51 - py, width: 16.97, height: 16.97 }} />
        </div>
      </div>
    );
  } : { onClick: power, className: raw ? "" : "tap vpn-btn-pop", style: { borderRadius: "50%" } };
  const bk = findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")); ov[bk.id] = { onClick: back, className: "tap" };
  const btn = findNode(f, (x) => (x.comp || "").startsWith("Btn") && /Location/.test(textOf(x)) && x.ay > 700);
  if (btn) ov[btn.id] = s.conn === "connecting" ? { style: { opacity: 1 } } : { onClick: () => upd({ sheet: true, pick: sRef.current.loc }), className: "tap" };
  if (!raw && s.conn === "connected") {
    const tm = findNode(f, (x) => x.t && /^\d\d:\d\d:\d\d$/.test(textOf(x)));
    if (tm) ov[tm.id] = (n, e, pos) => <FigText n={n} px={n.ax - pos.left} py={n.ay - pos.top} text={hhmmss(Math.max(0, (now - since) / 1000))} />;
    findNode(f, (x) => x.name === "Frame" && x.w === 343 && x.h === 72).children.forEach((card, i) => {
      const v = findNode(card, (x) => x.t && /MB\/s/.test(textOf(x)));
      ov[v.id] = (n, e, pos) => <FigText n={n} px={n.ax - pos.left} py={n.ay - pos.top} text={`${spd[i].toFixed(1)} MB/s`} />;
    });
  }
  // картка «Connect to / Connected to»: інше місто — рядок цього міста зі списку локацій, без зірочки
  const card = findNode(f, (x) => x.name === "label" && x.ay > 590 && x.ay < 620);
  const wantCard = s.conn === "off" ? null : (s.loc === "Fastest Connection" && s.conn === "connecting") ? null : (s.loc === "Fastest Connection" ? "New York, US" : s.loc);
  if (card && wantCard && wantCard !== firstText(card)) {
    const r = rowOf(wantCard);
    if (r) ov[card.id] = (n, e, pos) => (
      <div style={{ position: "absolute", left: pos.left - r.ax, top: pos.top - r.ay, width: 0, height: 0 }}>
        <FigNode n={r} ov={{ [findNode(r, (x) => (x.comp || "").includes("star")).id]: false }} />
      </div>
    );
  }
  ["5:3885", "5:3575", "5:3609", "5:7695", "5:7830"].forEach((id) => (ov[id] = false));   // сховані під екраном аркуші кадрів — показуємо свої

  // аркуш локацій (5:3695) з вибором і зірочками
  const sov = {};
  rows.forEach((r) => {
    const name = firstText(r), on = s.pick === name;
    // вибраний рядок — синя рамка 1 px поверх (як у кадрах 5:7916 / 5:8043); клік по рядку — вибір
    // рамка рядка — за станом вибору (у базовому кадрі 5:3695 синьою позначено «Fastest», її не успадковуємо)
    sov[r.id] = (n, e, pos) => (
      <>
        <FigNode n={{ ...r, strokes: [{ solid: "#0b62da", a: 0.15 }] }} px={r.ax - pos.left} py={r.ay - pos.top} ov={(({ [r.id]: _, ...rest }) => rest)(sov)} />
        <div onClick={() => upd({ pick: name })} style={{ ...pos, zIndex: 1, borderRadius: 16, cursor: "pointer",
          boxShadow: `inset 0 0 0 1px ${on ? C.blue : "transparent"}`, transition: "box-shadow .2s" }} />
      </>
    );
    const st = findNode(r, (x) => (x.comp || "").includes("star"));
    if (st) { const fav = s.favs.includes(name); sov[st.id] = (n, e, pos) => (
      <img src={PNG[fav ? STAR.on : STAR.off]} alt="" className="tap" onClick={(ev) => { ev.stopPropagation(); upd((o) => ({ favs: o.favs.includes(name) ? o.favs.filter((x) => x !== name) : [...o.favs, name] })); }}
        style={{ ...pos, zIndex: 2, cursor: "pointer" }} />); }
  });
  const done = findNode(sheetNode, (x) => x.t && textOf(x) === "Done");
  sov[done.id] = { onClick: () => { const o = sRef.current; if (o.pick && (o.pick !== o.loc || o.conn === "off")) connect(o.pick); else upd({ sheet: false }); }, className: "tap" };
  // знайомство (5:3427)
  const iFrame = frameOf("5:3427"), intro = findNode(iFrame, (x) => x.id === "5:3487"), iBlack = findNode(iFrame, (x) => x.id === "5:3469");
  const iov = { [btnWith(intro, "Connect to VPN").id]: { onClick: () => upd({ android: true }), className: "tap" } };

  return (
    <>
      <FigFrame id={baseId} ov={ov} raw={raw} />
      {/* знайомство з VPN */}
      <div style={{ position: "absolute", inset: 0, zIndex: 20, pointerEvents: s.intro ? "auto" : "none" }}>
        <div onClick={() => upd({ intro: false, android: false })} style={{ position: "absolute", inset: 0, opacity: s.intro ? 1 : 0, transition: raw ? "none" : "opacity .3s" }}><FigNode n={iBlack} /></div>
        <div style={{ position: "absolute", inset: 0, transform: s.intro ? "none" : `translateY(${812 - intro.ay + 12}px)`, transition: raw ? "none" : "transform .5s cubic-bezier(.2,.9,.25,1)", pointerEvents: "none" }}>
          <div style={{ pointerEvents: s.intro ? "auto" : "none" }}><FigNode n={intro} ov={iov} /></div>
        </div>
      </div>
      {/* системне вікно Android «запит на VPN» — у Figma його немає; на телефоні його показує система, не апка */}
      {s.android && (
        <div className="enter-fade" style={{ position: "absolute", left: 0, top: 0, width: 375, height: 812, zIndex: 40, background: "rgba(0,0,0,.35)" }}>
          <div style={{ position: "absolute", left: 28, top: 260, width: 319, background: "#f3f4f8", borderRadius: 28, padding: "24px 24px 16px", fontFamily: "Roboto, Inter, sans-serif" }}>
            <div style={{ fontSize: 10, letterSpacing: ".08em", textTransform: "uppercase", color: "#8a8f99", marginBottom: 10 }}>Android system dialog · mock</div>
            <div style={{ fontSize: 22, lineHeight: "28px", color: "#1b1c1f", marginBottom: 14 }}>Connection request</div>
            <div style={{ fontSize: 14, lineHeight: "20px", color: "#44474e" }}>Seciro wants to set up a VPN connection that allows it to monitor network traffic. Only accept if you trust the source.</div>
            <div style={{ display: "flex", justifyContent: "flex-end", gap: 8, marginTop: 20 }}>
              <div className="tap" onClick={() => upd({ android: false, intro: false })} style={{ padding: "10px 14px", color: C.blue, fontSize: 14 }}>Cancel</div>
              <div className="tap" onClick={() => connect(sRef.current.loc)} style={{ padding: "10px 14px", color: C.blue, fontSize: 14 }}>OK</div>
            </div>
          </div>
        </div>
      )}
      {/* список локацій */}
      <div style={{ position: "absolute", inset: 0, zIndex: 20, pointerEvents: s.sheet ? "auto" : "none" }}>
        <div onClick={() => upd({ sheet: false })} style={{ position: "absolute", inset: 0, opacity: s.sheet ? 1 : 0, transition: raw ? "none" : "opacity .3s" }}><FigNode n={sheetBlack} /></div>
        <div style={{ position: "absolute", inset: 0, transform: s.sheet ? "none" : `translateY(${812 - sheetNode.ay + 12}px)`, transition: raw ? "none" : "transform .5s cubic-bezier(.2,.9,.25,1)", pointerEvents: "none" }}>
          <div style={{ pointerEvents: s.sheet ? "auto" : "none" }}><FigNode n={sheetNode} ov={sov} /></div>
        </div>
      </div>
    </>
  );
}

// знайомства модулів показуються лише раз за сесію (як «перший вхід» у справжній апці); Restart у панелі скидає
const SEEN = new Set();

// ── Ряд 5: Web Protection (8 кадрів) ──
// Кадри: 5:3919 перший вхід (5/5) · 5:6791 знайомство «Start Protection» · 5:7412 5/5 · 5:7024 4/5 · 5:7121 3/5 · 5:7218 2/5 · 5:7315 1/5 · 5:6927 0/5
// Рівень = скільки з 5 фільтрів увімкнено. Базовий кадр — за рівнем (картка рівня й «Protect Your Device»), рядок кожного фільтра —
// з кадру «усе ввімкнено» (5:7412) або «усе вимкнено» (5:6927) за його станом, з перетіканням. Головний перемикач: усе ввімк. ↔ усе вимк.
const WEB_IDS = ["web-first", "web-intro", "web-on", "web-4", "web-3", "web-2", "web-1", "web-0"];
const WEB_LEVEL = ["5:6927", "5:7315", "5:7218", "5:7121", "5:7024", "5:7412"];
const WEB_FILTERS = ["General Ads", "Hide Cookie Prompts", "Stop Tracking", "Block Adult Content", "Block Gambling"];
const WEB_PRESET = {
  "web-first": { f: [1, 1, 1, 1, 1], first: true, intro: false }, "web-intro": { f: [1, 1, 1, 1, 1], first: true, intro: true },
  "web-on": { f: [1, 1, 1, 1, 1], first: false, intro: false }, "web-4": { f: [1, 1, 1, 1, 0], first: false, intro: false },
  "web-3": { f: [1, 1, 1, 0, 0], first: false, intro: false }, "web-2": { f: [1, 1, 0, 0, 0], first: false, intro: false },
  "web-1": { f: [1, 0, 0, 0, 0], first: false, intro: false }, "web-0": { f: [0, 0, 0, 0, 0], first: false, intro: false },
};
const webCount = (f) => f.filter(Boolean).length;
const webId = (s) => s.intro ? "web-intro" : webCount(s.f) === 5 ? (s.first ? "web-first" : "web-on") : `web-${webCount(s.f)}`;
// Перемикач (компонент Figma «Switch»: 48×28, кулька 24 з відступом 2; увімк. — #0b62da, вимк. — rgba(114,114,114,.15))
function Switch({ on, x, y, raw }) {
  const t = raw ? "none" : "background-color .25s ease";
  return (
    <div style={{ position: "absolute", left: x, top: y, width: 48, height: 28, borderRadius: 14, background: on ? "#0b62da" : "rgba(114,114,114,.15)", transition: t, pointerEvents: "none" }}>
      <div style={{ position: "absolute", left: 2, top: 2, width: 24, height: 24, borderRadius: 12, background: "#fff",
        transform: on ? "translateX(20px)" : "none", transition: raw ? "none" : "transform .28s cubic-bezier(.3,.9,.3,1.05)" }} />
    </div>
  );
}
// сірі доріжки шкали («bg», прозорість 0.15): PNG з Figma вже несе цю прозорість — вдруге не множимо
const WEB_BG = { bg: (n, el, pos) => <FigNode n={{ ...n, op: undefined }} px={n.ax - pos.left} py={n.ay - pos.top} /> };
const webRow = (frameId, name) => findNode(frameOf(frameId), (x) => x.name === "Label" && firstText(x) === name);

function Web({ preset, onState, back, raw }) {
  const [s, setS] = useState(() => ({ ...WEB_PRESET[preset], f: [...WEB_PRESET[preset].f] }));
  const sRef = useRef(s); sRef.current = s;
  useEffect(() => { if (webId(sRef.current) !== preset) setS({ ...WEB_PRESET[preset], f: [...WEB_PRESET[preset].f] }); }, [preset]);
  useEffect(() => { const id = webId(s); if (id !== preset) onState(id); }, [s]);
  // перший вхід: знайомство виїжджає саме (як 5:3919 → 5:6791)
  useEffect(() => { if (raw || preset !== "web-first" || SEEN.has("web")) return; const t = setTimeout(() => { SEEN.add("web"); setS((o) => (o.first && !o.intro ? { ...o, intro: true } : o)); }, 650); return () => clearTimeout(t); }, []);
  const n = webCount(s.f), all = n === 5;
  // один постійний кадр (у всіх рівнів однакова розкладка): інакше при кожному перемиканні екран монтується заново й «зникає».
  // Картка рівня, перемикачі й іконки — за станом поверх нього.
  const baseId = "5:7412";
  const f = frameOf(baseId);
  const ov = {};
  const bk = findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")); ov[bk.id] = { onClick: back, className: "tap" };
  // картка рівня: ОДИН фон, у якого плавно змінюється колір (зелений / жовтий / червоний), а вміст рівнів (шкала, іконка, тексти)
  // перетікає поверх нього. Раніше перетікали цілі напівпрозорі картки — посередині фон блід і «мерехтів».
  const hero = findNode(f, (x) => x.name === "Hero");
  const heroOf = (lv) => findNode(frameOf(WEB_LEVEL[lv]), (x) => x.name === "Hero");
  const cur = heroOf(n), tint = (p) => { const v = parseInt(p.solid.slice(1), 16); return `rgba(${v >> 16},${(v >> 8) & 255},${v & 255},${p.a})`; };
  ov[hero.id] = (h, e, pos) => (
    <div style={{ ...pos, borderRadius: cur.r, overflow: "hidden", pointerEvents: "none", background: tint(cur.fills[0]),
      boxShadow: `inset 0 0 0 ${cur.sw}px ${tint(cur.strokes[0])}`, transition: raw ? "none" : "background-color .45s ease, box-shadow .45s ease" }}>
      {WEB_LEVEL.map((fid, lv) => { const hn = heroOf(lv);
        return <div key={fid} style={{ position: "absolute", inset: 0, opacity: lv === n ? 1 : 0, transition: raw ? "none" : "opacity .3s ease" }}>
          {(hn.children || []).map((c) => <FigNode key={c.id} n={c} px={hn.ax} py={hn.ay} ov={WEB_BG} />)}</div>; })}
    </div>
  );
  // рядок: увімкнений і вимкнений варіанти з двох кадрів (іконка, тексти) перетікають; перемикач — живий, кулька їде
  const row = (target, name, on, onClick) => {
    const a = webRow("5:7412", name), b = webRow("5:6927", name);
    const sw = findNode(a, (x) => x.name === "Switch"), noSw = { [sw.id]: false, [findNode(b, (x) => x.name === "Switch").id]: false };
    ov[target.id] = (r, e, pos) => (
      <div onClick={raw ? undefined : onClick} role="switch" aria-checked={on} className={raw ? "" : "tap-soft"} style={{ ...pos, cursor: "pointer" }}>
        <div style={{ position: "absolute", inset: 0, opacity: on ? 1 : 0, transition: raw ? "none" : "opacity .25s ease" }}><FigNode n={a} px={a.ax} py={a.ay} ov={noSw} /></div>
        <div style={{ position: "absolute", inset: 0, opacity: on ? 0 : 1, transition: raw ? "none" : "opacity .25s ease" }}><FigNode n={b} px={b.ax} py={b.ay} ov={noSw} /></div>
        <Switch on={on} x={sw.ax - a.ax} y={sw.ay - a.ay} raw={raw} />
      </div>
    );
  };
  const master = webRow(baseId, "Protect Your Device");
  // головний перемикач: фільтри вмикаються / вимикаються каскадом, один за одним
  const cascade = () => { const to = webCount(sRef.current.f) === 5 ? 0 : 1;
    setS((o) => ({ ...o, first: false }));
    [0, 1, 2, 3, 4].forEach((i) => setTimeout(() => setS((o) => ({ ...o, f: o.f.map((v, j) => (j === i ? to : v)) })), raw ? 0 : 70 * i)); };
  row(master, "Protect Your Device", all, cascade);
  WEB_FILTERS.forEach((name, i) => row(webRow(baseId, name), name, !!s.f[i], () => setS((o) => ({ ...o, first: false, f: o.f.map((v, j) => (j === i ? (v ? 0 : 1) : v)) }))));
  // поява: картка рівня й два блоки знизу вгору з невеликою затримкою (лише при вході)
  if (!raw) findNode(f, (x) => x.name === "Frame" && x.children && x.children.some((c) => c.name === "menu")).children.forEach((c, i) => {
    if (c.name === "menu") ov[c.id] = { className: "rise", style: { animationDelay: `${0.08 + i * 0.07}s` } }; });
  const pop = findNode(f, (x) => x.name === "popup"); if (pop) ov[pop.id] = false;   // аркуш під екраном кадру — показуємо свій, з анімацією
  // знайомство (5:6791)
  const iFrame = frameOf("5:6791"), intro = findNode(iFrame, (x) => x.name === "popup"), iBlack = findNode(iFrame, (x) => x.name === "blackout");
  const close = () => setS((o) => ({ ...o, intro: false, first: false }));
  const iov = { [btnWith(intro, "Start Protection").id]: { onClick: () => setS((o) => ({ ...o, intro: false, first: false, f: [1, 1, 1, 1, 1] })), className: "tap" } };
  const ill = findNode(intro, (x) => x.name === "img"); if (ill && !raw) iov[ill.id] = (n, el) => <div className="float-slow">{el}</div>;
  return (
    <>
      <FigFrame id={baseId} ov={ov} raw={raw} />
      <div style={{ position: "absolute", inset: 0, zIndex: 20, pointerEvents: s.intro ? "auto" : "none" }}>
        <div onClick={close} style={{ position: "absolute", inset: 0, opacity: s.intro ? 1 : 0, transition: raw ? "none" : "opacity .3s" }}><FigNode n={iBlack} /></div>
        <div style={{ position: "absolute", inset: 0, transform: s.intro ? "none" : `translateY(${812 - intro.ay + 12}px)`, transition: raw ? "none" : "transform .5s cubic-bezier(.2,.9,.25,1)", pointerEvents: "none" }}>
          <div style={{ pointerEvents: s.intro ? "auto" : "none" }}><FigNode n={intro} ov={iov} /></div>
        </div>
      </div>
    </>
  );
}

// ── Ряд 6: Safe QR Scanner (6 кадрів) ──
// 5:4054 перший вхід · 5:6417 знайомство · 5:6490 сканер · 5:6569 перевірка (спінер) · 5:6619 «Access blocked» · 5:6705 + «Copied!»
// База — 5:6490 (сканер); аркуші й «Copied!» — з інших кадрів поверх, з анімацією. Перший скан іде сам; далі — дотик до рамки.
const QR_IDS = ["qr-first", "qr-intro", "qr-scan", "qr-check", "qr-blocked", "qr-copied"];
const QR_PRESET = {
  "qr-first": { first: true, intro: false, phase: "idle", copied: false }, "qr-intro": { first: true, intro: true, phase: "idle", copied: false },
  "qr-scan": { first: false, intro: false, phase: "scan", copied: false, focus: true }, "qr-check": { first: false, intro: false, phase: "check", copied: false },
  "qr-blocked": { first: false, intro: false, phase: "blocked", copied: false }, "qr-copied": { first: false, intro: false, phase: "blocked", copied: true },
};
const qrId = (s) => s.intro ? "qr-intro" : s.phase === "check" ? "qr-check" : s.phase === "blocked" ? (s.copied ? "qr-copied" : "qr-blocked") : s.phase === "scan" ? "qr-scan" : "qr-first";

// Аркуш знизу з іншого кадру: затемнення + аркуш виїжджає (як у VPN / Web Protection)
function FigSheetOf({ open, black, node, ov, onClose, raw, z = 20 }) {
  return (
    <div style={{ position: "absolute", inset: 0, zIndex: z, pointerEvents: open ? "auto" : "none" }}>
      <div onClick={onClose} style={{ position: "absolute", inset: 0, opacity: open ? 1 : 0, transition: raw ? "none" : "opacity .3s" }}>{black && <FigNode n={black} />}</div>
      <div style={{ position: "absolute", inset: 0, transform: open ? "none" : `translateY(${812 - node.ay + 12}px)`, transition: raw ? "none" : "transform .5s cubic-bezier(.2,.9,.25,1)", pointerEvents: "none" }}>
        <div style={{ pointerEvents: open ? "auto" : "none" }}><FigNode n={node} ov={ov} /></div>
      </div>
    </div>
  );
}

function Qr({ preset, onState, back, toast, raw }) {
  const [s, setS] = useState(() => ({ seg: 0, auto: true, ...QR_PRESET[preset] }));
  const sRef = useRef(s); sRef.current = s;
  const upd = (p) => setS((o) => ({ ...o, ...(typeof p === "function" ? p(o) : p) }));
  useEffect(() => { if (qrId(sRef.current) !== preset) setS((o) => ({ ...o, ...QR_PRESET[preset] })); }, [preset]);
  useEffect(() => { const id = qrId(s); if (id !== preset) onState(id); }, [s]);
  // перший вхід: знайомство виїжджає саме
  useEffect(() => { if (raw || preset !== "qr-first" || SEEN.has("qr")) return; const t = setTimeout(() => { SEEN.add("qr"); upd((o) => (o.first && !o.intro ? { intro: true } : {})); }, 650); return () => clearTimeout(t); }, []);
  // сканування → «знайшли код» → перевірка → результат
  useEffect(() => { if (raw) return;
    if (s.phase === "scan" && s.auto) { const a = setTimeout(() => upd({ focus: true }), 800), t = setTimeout(() => upd((o) => (o.phase === "scan" ? { phase: "check" } : {})), 2100); return () => { clearTimeout(a); clearTimeout(t); }; }
    if (s.phase === "check") { const t = setTimeout(() => upd((o) => (o.phase === "check" ? { phase: "blocked" } : {})), 1500); return () => clearTimeout(t); }
  }, [s.phase, s.auto]);
  useEffect(() => { if (raw || !s.copied) return; const t = setTimeout(() => upd({ copied: false }), 1700); return () => clearTimeout(t); }, [s.copied]);

  const f = frameOf("5:6490");
  const ov = {};
  ov[findNode(f, (x) => x.name === "popup").id] = false;
  ov[findNode(f, (x) => x.name === "Access blocked").id] = false;
  ov[findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")).id] = { onClick: back, className: "tap" };
  // вкладки Scan / From gallery: підсвітка активної переїжджає
  const [b0, b1] = findNode(f, (x) => x.name === "Menubar").children;
  const seg = (b, i) => (n, e, pos) => (
    <>
      {i === 0 && <div style={{ position: "absolute", left: pos.left + s.seg * (b1.ax - b0.ax), top: pos.top, width: n.w, height: n.h, borderRadius: 20,
        background: "rgba(11,98,218,.15)", transition: raw ? "none" : "left .3s cubic-bezier(.3,.9,.3,1)" }} />}
      <div onClick={raw ? undefined : () => { if (i === 1) { upd({ seg: 1, phase: "check", copied: false }); toast("On the phone: Android photo picker opens"); } else upd({ seg: 0, phase: "scan", auto: false, focus: true }); }}
        className={raw ? "" : "tap"} style={{ ...pos, opacity: s.seg === i ? 1 : 0.4, transition: raw ? "none" : "opacity .25s ease", cursor: "pointer" }}>
        <FigNode n={{ ...n, fills: undefined, op: undefined }} px={n.ax} py={n.ay} />
      </div>
    </>
  );
  ov[b0.id] = seg(b0, 0); ov[b1.id] = seg(b1, 1);
  // рамка камери: спершу розмито (5:4054), камера фокусується на коді (5:6490); лінія сканування, поки шукаємо; дотик — сканувати ще раз
  const cam = findNode(f, (x) => x.name === "scan here"), camBlur = findNode(frameOf("5:4054"), (x) => x.name === "scan here");
  const sharp = s.phase !== "idle" && !(s.phase === "scan" && !s.focus);
  ov[cam.id] = (n, e, pos) => (
    <div onClick={raw ? undefined : () => upd({ phase: "scan", auto: true, focus: false, seg: 0 })} style={{ ...pos, cursor: "pointer" }}>
      <div style={{ position: "absolute", left: -pos.left, top: -pos.top }}><FigNode n={camBlur} /></div>
      <div style={{ position: "absolute", left: -pos.left, top: -pos.top, opacity: sharp ? 1 : 0, filter: raw || sharp ? "none" : "blur(6px)", transition: raw ? "none" : "opacity .7s ease, filter .7s ease" }}>{e}</div>
      {!raw && s.phase === "scan" && <div className="qr-line" />}
    </div>
  );
  // знайомство (5:6417)
  const iF = frameOf("5:6417"), intro = findNode(iF, (x) => x.name === "popup"), iBlack = findNode(iF, (x) => x.name === "blackout");
  const iov = { [btnWith(intro, "Scan a QR Code").id]: { onClick: () => upd({ intro: false, first: false, phase: "scan", auto: true, focus: false }), className: "tap" } };
  const ill = findNode(intro, (x) => x.name === "img"); if (ill && !raw) iov[ill.id] = (n, el) => <div className="float-slow">{el}</div>;
  // перевірка (5:6569): спінер обертається
  const cF = frameOf("5:6569"), check = findNode(cF, (x) => x.name === "Access blocked"), cBlack = findNode(cF, (x) => x.name === "blackout");
  const cov = {}; const ld = findNode(check, (x) => x.name === "loader"); if (ld && !raw) cov[ld.id] = (n, el) => <div className="spin-soft" style={{ position: "absolute", left: 0, top: 0, width: 0, height: 0 }}>{el}</div>;
  // результат (5:6619): «Back to Safety», «Continue to Site», копіювання посилання
  const rF = frameOf("5:6619"), res = findNode(rF, (x) => x.name === "Access blocked"), rBlack = findNode(rF, (x) => x.name === "blackout");
  const rov = {};
  rov[btnWith(res, "Back to Safety").id] = { onClick: () => upd({ phase: "scan", auto: false, copied: false, focus: true }), className: "tap" };
  const cont = findNode(res, (x) => x.t && textOf(x) === "Continue to Site");
  rov[cont.id] = { onClick: () => { upd({ phase: "scan", auto: false }); toast("Opens the site in the browser"); }, className: "tap" };
  const cp = findNode(res, (x) => (x.comp || "").includes("content-copy")); rov[cp.id] = { onClick: () => upd({ copied: true }), className: "tap" };
  const toastN = findNode(frameOf("5:6705"), (x) => x.name === "Copied!" && x.type === "FRAME");
  return (
    <>
      <FigFrame id="5:6490" ov={ov} raw={raw} />
      <FigSheetOf open={s.intro} black={iBlack} node={intro} ov={iov} onClose={() => upd({ intro: false, first: false })} raw={raw} />
      <FigSheetOf open={s.phase === "check"} black={cBlack} node={check} ov={cov} raw={raw} />
      <FigSheetOf open={s.phase === "blocked"} black={rBlack} node={res} ov={rov} onClose={() => upd({ phase: "scan", auto: false })} raw={raw} />
      <div style={{ position: "absolute", inset: 0, zIndex: 30, pointerEvents: "none", transform: s.copied ? "none" : "translateY(-140px)", transition: raw ? "none" : "transform .45s cubic-bezier(.2,.9,.25,1)" }}>
        <FigNode n={toastN} />
      </div>
    </>
  );
}

// ── Спільне для рядів 7–10 ──
const nodeText = (root, text) => findNode(root, (x) => x.t && textOf(x) === text);
// аркуш-знайомство з кадру Figma (popup + blackout) — сам виїжджає при першому вході
function useAutoIntro(key, first, raw, open) { useEffect(() => { if (raw || !first || SEEN.has(key)) return; const t = setTimeout(() => { SEEN.add(key); open(); }, 650); return () => clearTimeout(t); }, []); }
// прогрес «хвилясте кільце» (компонент Figma «loading»): бліда доріжка + синє кільце, що заповнюється за годинниковою; відсотки рахують
function WaveRing({ n, pos, p }) {
  const shape = n.children.find((c) => c.name === "shape"), num = findNode(n, (x) => x.t && textOf(x) === "0%");
  const deg = Math.max(0, Math.min(100, p)) * 3.6, m = `conic-gradient(#000 ${deg}deg, transparent ${deg}deg)`;
  return (
    <div style={{ ...pos, pointerEvents: "none" }}>
      <img src={PNG[shape.png]} alt="" style={{ position: "absolute", left: shape.rx - n.ax, top: shape.ry - n.ay, width: shape.rw, height: shape.rh }} />
      <img src={ringFull} alt="" style={{ position: "absolute", left: 0, top: 0, width: n.w, height: n.h, WebkitMaskImage: m, maskImage: m }} />
      <FigText n={num} px={n.ax} py={n.ay} text={`${Math.round(p)}%`} />
    </div>
  );
}
// прогрес 0 → 100 за ms (раз на запуск фази); onDone — коли дійшов
function useProgress(active, ms, raw, onDone) {
  const [p, setP] = useState(0);
  useEffect(() => {
    if (!active || raw) { setP(0); return; }
    let raf, t0 = performance.now();
    const step = (now) => { const k = Math.min(1, (now - t0) / ms); setP(100 * (1 - Math.pow(1 - k, 1.6))); if (k < 1) raf = requestAnimationFrame(step); else setTimeout(onDone, 380); };
    raf = requestAnimationFrame(step); return () => cancelAnimationFrame(raf);
  }, [active]);
  return p;
}
// число, що «набігає» від 0 при появі (лише поза режимом звірки)
function useCountUp(to, raw, ms = 900) {
  const [v, setV] = useState(raw ? to : 0);
  useEffect(() => { if (raw) { setV(to); return; } let raf, t0 = performance.now();
    const step = (now) => { const k = Math.min(1, (now - t0) / ms); setV(to * (1 - Math.pow(1 - k, 3))); if (k < 1) raf = requestAnimationFrame(step); };
    raf = requestAnimationFrame(step); return () => cancelAnimationFrame(raf); }, [to]);
  return v;
}
// поява блоків знизу з затримкою (картки, рядки)
const riseIn = (ov, nodes, raw, base = 0.06, stepS = 0.06) => { if (raw) return; nodes.forEach((c, i) => { if (c && !ov[c.id]) ov[c.id] = { className: "rise", style: { animationDelay: `${base + i * stepS}s` } }; }); };

// ── Ряд 7: Storage Cleaner (7 кадрів) ──
// 5:4126 перший вхід · 5:6268 знайомство · 5:8199 сканування · 5:6106 результат сканування · 5:6203 скріншоти (вибір) · 5:8234 очищення · 5:8296 готово
const ST_IDS = ["st-first", "st-intro", "st-scan", "st-ready", "st-shots", "st-clean", "st-done"];
const ST_PRESET = {
  "st-first": { phase: "idle", first: true, intro: false }, "st-intro": { phase: "idle", first: true, intro: true },
  "st-scan": { phase: "scan" }, "st-ready": { phase: "ready" }, "st-shots": { phase: "shots", cat: "Screenshots" },
  "st-clean": { phase: "clean" }, "st-done": { phase: "done" },
};
const ST_SEL = [1, 1, 0, 1, 1, 0, 0, 0];   // вибір у кадрі 5:6203 (в обох блоках однаковий)
const stId = (s) => s.intro ? "st-intro" : s.phase === "idle" ? "st-first" : `st-${s.phase}`;
const ST_CATS = ["Screenshots", "Duplicate Photos", "Similar Photos", "Similar Videos"];

function Storage({ preset, onState, back, toast, raw }) {
  const [s, setS] = useState(() => ({ phase: "idle", first: false, intro: false, cat: "Screenshots", sel: [...ST_SEL, ...ST_SEL], ...ST_PRESET[preset] }));
  const sRef = useRef(s); sRef.current = s;
  const upd = (p) => setS((o) => ({ ...o, ...(typeof p === "function" ? p(o) : p) }));
  useEffect(() => { if (stId(sRef.current) !== preset) setS((o) => ({ ...o, intro: false, ...ST_PRESET[preset] })); }, [preset]);
  useEffect(() => { const id = stId(s); if (id !== preset) onState(id); }, [s]);
  useAutoIntro("st", preset === "st-first", raw, () => upd((o) => (o.phase === "idle" && !o.intro ? { intro: true } : {})));
  const scanP = useProgress(s.phase === "scan", 3600, raw, () => upd((o) => (o.phase === "scan" ? { phase: "ready" } : {})));
  const cleanP = useProgress(s.phase === "clean", 2600, raw, () => upd((o) => (o.phase === "clean" ? { phase: "done" } : {})));

  // шкала зайнятої пам'яті: дуги домальовуються, відсотки набігають
  const gaugeOv = (f, ov, pct) => {
    const amt = findNode(f, (x) => x.t && /^\d+$/.test(textOf(x)) && x.w < 60);
    if (amt) ov[amt.id] = (n, e, pos) => <GaugeNum n={n} pos={pos} to={pct} raw={raw} />;
  };
  let body;
  if (s.phase === "idle" || s.phase === "ready") {
    const fid = s.phase === "idle" ? "5:4126" : "5:6106", f = frameOf(fid), ov = {};
    ov[findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")).id] = { onClick: back, className: "tap" };
    const pop = findNode(f, (x) => x.name === "popup"); if (pop) ov[pop.id] = false;
    ov[btnWith(f, "Clean up storage").id] = { onClick: () => upd({ phase: "shots", cat: "Screenshots" }), className: "tap" };   // як у Figma: далі вибір → «Clean selected»
    labelsIn(f).forEach((l, i) => { const t = firstText(l); ov[l.id] = { onClick: () => upd({ phase: "shots", cat: t }), className: raw ? "" : "tap rise", style: { animationDelay: `${0.14 + i * 0.05}s` } }; });
    gaugeOv(f, ov, 87);
    const iF = frameOf("5:6268"), intro = findNode(iF, (x) => x.name === "popup"), iBlack = findNode(iF, (x) => x.name === "blackout");
    const iov = { [btnWith(intro, "Scan Storage").id]: { onClick: () => upd({ intro: false, first: false, phase: "scan" }), className: "tap" } };
    const ill = findNode(intro, (x) => x.name === "img"); if (ill && !raw) iov[ill.id] = (n, el) => <div className="float-slow">{el}</div>;
    body = (<>
      <FigFrame id={fid} ov={ov} raw={raw} />
      <FigSheetOf open={s.intro} black={iBlack} node={intro} ov={iov} onClose={() => upd({ intro: false, first: false })} raw={raw} />
    </>);
  } else if (s.phase === "scan") {
    const f = frameOf("5:8199"), ov = {};
    ov[findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")).id] = { onClick: () => upd({ phase: "idle" }), className: "tap" };
    ov[findNode(f, (x) => x.name === "loading").id] = (n, e, pos) => <WaveRing n={n} pos={pos} p={scanP} />;
    // список категорій: кожна по черзі «Scanning…» → «OK», решта «Waiting» (варіанти компонента «checklist 3»)
    const list = findNode(f, (x) => x.name === "checklist 3");
    const rows = list.children.filter((c) => c.type === "FRAME");
    rows.forEach((r, i) => {
      const st = raw ? (i === 0 ? "scan" : "wait") : scanP >= (i + 1) * 25 ? "ok" : scanP >= i * 25 ? "scan" : "wait";
      const anyDone = !raw && scanP >= 25;
      ov[r.id] = (n, e, pos) => <CheckRow n={n} pos={pos} st={st} dimName={st === "wait" && !anyDone} />;
    });
    body = <FigFrame id="5:8199" ov={ov} raw={raw} />;
  } else if (s.phase === "shots") {
    const f = frameOf("5:6203"), ov = {};
    ov[findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")).id] = { onClick: () => upd({ phase: "ready" }), className: "tap" };
    const video = s.cat === "Similar Videos";
    // заголовок — назва категорії (дизайн є лише для Screenshots)
    const ttl = findNode(f, (x) => x.name === "logo").children.find((c) => c.name === "label");
    const ttlText = findNode(ttl, (x) => !!x.t);
    if (s.cat !== "Screenshots") ov[ttlText.id] = (n, e, pos) => <FigText n={n} px={n.ax - pos.left} py={n.ay - pos.top} text={s.cat} />;
    // сітка: кожен рядок — дві мініатюри; дотик — вибрати / зняти
    let k = 0;
    const blocks = findNode(f, (x) => x.name === "Frame" && x.children && x.children.some((c) => c.name === "block")).children.filter((b) => b.name === "block");
    blocks.forEach((b) => {
      b.children.filter((c) => c.name === "frame").forEach((fr) => {
        const idx = [k, k + 1]; k += 2;
        ov[fr.id] = (n, e, pos) => (
          <div style={{ ...pos }}>
            {idx.map((j, m) => <Media key={j} x={m ? 175.5 : 0} on={!!s.sel[j]} video={video} raw={raw} onTap={() => upd((o) => ({ sel: o.sel.map((v, q) => (q === j ? (v ? 0 : 1) : v)) }))} />)}
          </div>
        );
      });
      // «Deselect All» блоку
      const da = nodeText(b, "Deselect All"); const from = blocks.indexOf(b) * 8;
      const anyB = s.sel.slice(from, from + 8).some(Boolean);
      if (da) ov[da.id] = (n, e, pos) => (
        <div onClick={raw ? undefined : () => upd((o) => ({ sel: o.sel.map((v, q) => (q >= from && q < from + 8 ? (anyB ? 0 : 1) : v)) }))} className="tap" style={{ ...pos, cursor: "pointer" }}>
          <div style={{ position: "absolute", left: -pos.left, top: -pos.top }}><FigText n={n} px={n.ax - pos.left} py={n.ay - pos.top} text={anyB ? "Deselect All" : "Select All"} /></div>
        </div>);
    });
    const any = s.sel.some(Boolean);
    const daTop = findNode(findNode(f, (x) => x.name === "logo"), (x) => x.t && textOf(x) === "Deselect All");
    if (daTop) ov[daTop.id] = (n, e, pos) => (
      <div onClick={raw ? undefined : () => upd((o) => ({ sel: o.sel.map(() => (any ? 0 : 1)) }))} className="tap" style={{ ...pos, cursor: "pointer" }}>
        <div style={{ position: "absolute", left: -pos.left, top: -pos.top }}><FigText n={n} px={n.ax - pos.left} py={n.ay - pos.top} text={any ? "Deselect All" : "Select All"} /></div>
      </div>);
    const cb = btnWith(f, "Clean selected");
    ov[cb.id] = { onClick: any ? () => upd({ phase: "clean" }) : undefined, className: any ? "tap" : "", style: { opacity: any ? 1 : 0.25, transition: "opacity .25s" } };
    body = <FigFrame id="5:6203" ov={ov} raw={raw} />;
  } else if (s.phase === "clean") {
    const f = frameOf("5:8234"), ov = {};
    ov[findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")).id] = { onClick: () => upd({ phase: "ready" }), className: "tap" };
    ov[findNode(f, (x) => x.name === "loading").id] = (n, e, pos) => <WaveRing n={n} pos={pos} p={cleanP} />;
    body = <FigFrame id="5:8234" ov={ov} raw={raw} />;
  } else {
    const f = frameOf("5:8296"), ov = {};
    ov[findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")).id] = { onClick: () => upd({ phase: "ready" }), className: "tap" };
    ov[btnWith(f, "Good").id] = { onClick: () => upd({ phase: "ready" }), className: "tap" };
    const img = findNode(f, (x) => x.name === "img"); if (img && !raw) ov[img.id] = (n, el) => <div className="pop-in">{el}</div>;
    gaugeOv(f, ov, 75);
    riseIn(ov, [findNode(f, (x) => x.name === "Frame 1171276172"), findNode(f, (x) => x.name === "Frame" && x.h === 152)], raw, 0.15, 0.08);
    body = <FigFrame id="5:8296" ov={ov} raw={raw} />;
  }
  return body;
}
// число в центрі шкали (87 / 75), що набігає
function GaugeNum({ n, pos, to, raw }) { const v = useCountUp(to, raw); return <FigText n={n} px={n.ax - pos.left} py={n.ay - pos.top} text={String(Math.round(v))} />; }
// рядок списку сканування: іконка (спінер / ✓ / годинник) + назва + статус кольором як у варіантах «checklist 3»
function CheckRow({ n, pos, st, dimName }) {
  const icon = n.children[0], name = n.children[1], stat = n.children[2];
  const col = st === "ok" ? "rgba(57,172,96,1)" : st === "scan" ? "#0b62da" : "rgba(115,115,115,.15)";
  const label = st === "ok" ? "OK" : st === "scan" ? "Scanning..." : "Waiting";
  const nameNode = { ...name, t: { ...name.t, runs: name.t.runs.map((r) => ({ ...r, c: [{ solid: dimName ? "#737373" : "#000000", a: 1 }] })) } };
  const statNode = { ...stat, t: { ...stat.t, runs: [{ ...stat.t.runs[0], t: label, c: undefined }] } };
  return (
    <div style={{ ...pos }}>
      <div style={{ position: "absolute", left: icon.ax - n.ax, top: icon.ay - n.ay, width: 24, height: 24 }}>
        {st === "scan" && <img src={loaderRing} alt="" className="spin-arc" style={{ width: 24, height: 24, display: "block" }} />}
        {st === "ok" && <img src={loaderOk} alt="" className="pop-in-soft" style={{ width: 24, height: 24, display: "block" }} />}
        {st === "wait" && <img src={PNG[icon.png]} alt="" style={{ width: 24, height: 24, display: "block" }} />}
      </div>
      <FigNode n={nameNode} px={n.ax} py={n.ay} />
      <div style={{ color: col, transition: "color .3s" }}><FigNode n={statNode} px={n.ax} py={n.ay} /></div>
    </div>
  );
}
// мініатюра (компонент Figma «media»: фото / відео, вибрано / ні) — у рядку сітки, x від лівого краю рядка
function Media({ x, on, video, onTap, raw }) {
  const src = video ? [mediaVideoOff, mediaVideoOn] : [mediaPhotoOff, mediaPhotoOn];
  return (
    <div onClick={raw ? undefined : onTap} className={raw ? "" : "tap"} style={{ position: "absolute", left: x, top: 0, width: 167.5, height: 167.5, cursor: "pointer" }}>
      <img src={src[0]} alt="" style={{ position: "absolute", left: -16, top: -12, width: 199.5, height: 199.5, maxWidth: "none" }} />
      <img src={src[1]} alt="" style={{ position: "absolute", left: -16, top: -12, width: 199.5, height: 199.5, maxWidth: "none", opacity: on ? 1 : 0, transition: raw ? "none" : "opacity .2s ease" }} />
    </div>
  );
}

// ── Ряд 8: Breach Check (9 кадрів) ──
// 5:4268 перший вхід · 5:5869 знайомство · 5:5542 поле · 5:5976 поле + клавіатура · 5:6041 пошта введена · 5:8265 перевірка · 5:5649 витоків немає
// 5:5705 знайдено 2 · 5:5781 подробиці LinkedIn. Відповідь «сервера» — у панелі (Breach Check finds).
const BR_IDS = ["br-first", "br-intro", "br-idle", "br-typing", "br-filled", "br-check", "br-none", "br-found", "br-detail"];
const BR_PRESET = {
  "br-first": { phase: "input", first: true, intro: false, focus: false, email: "" }, "br-intro": { phase: "input", first: true, intro: true, focus: false, email: "" },
  "br-idle": { phase: "input", focus: false, email: "" }, "br-typing": { phase: "input", focus: true, email: "" },
  "br-filled": { phase: "input", focus: true, email: "name@example.com" }, "br-check": { phase: "check", email: "name@example.com" },
  "br-none": { phase: "none", email: "name@example.com" }, "br-found": { phase: "found", email: "name@example.com" }, "br-detail": { phase: "detail", email: "name@example.com" },
};
const brId = (s) => s.intro ? "br-intro" : s.phase !== "input" ? `br-${s.phase}` : s.focus ? (s.email ? "br-filled" : "br-typing") : s.first ? "br-first" : "br-idle";

function Breach({ preset, onState, back, go, toast, scen, raw }) {
  const [s, setS] = useState(() => ({ phase: "input", first: false, intro: false, focus: false, email: "", ...BR_PRESET[preset] }));
  const sRef = useRef(s); sRef.current = s;
  const scenRef = useRef(scen); scenRef.current = scen;
  const upd = (p) => setS((o) => ({ ...o, ...(typeof p === "function" ? p(o) : p) }));
  useEffect(() => { if (brId(sRef.current) !== preset) setS((o) => ({ ...o, intro: false, first: false, ...BR_PRESET[preset] })); }, [preset]);
  useEffect(() => { const id = brId(s); if (id !== preset) onState(id); }, [s]);
  useAutoIntro("br", preset === "br-first", raw, () => upd((o) => (o.phase === "input" && !o.intro ? { intro: true } : {})));
  const p = useProgress(s.phase === "check", 2400, raw, () => upd((o) => (o.phase === "check" ? { phase: scenRef.current.breach === "none" ? "none" : "found" } : {})));
  const ok = EMAIL_OK.test(s.email);
  const submit = () => { if (EMAIL_OK.test(sRef.current.email)) upd({ phase: "check", focus: false }); };
  const key = (c) => { const o = sRef.current; if (o.phase !== "input") return;
    if (c === "del") upd({ email: o.email.slice(0, -1) }); else if (c === "return") submit();
    else if (c === "123") upd({ email: o.email + (o.email.includes("@") ? "." : "@") });
    else if (c.length === 1 && /[a-z0-9@._+-]/i.test(c)) upd({ email: o.email + c.toLowerCase() }); };
  useEffect(() => { if (raw) return; const k = (e) => { const o = sRef.current; if (!o.focus || o.phase !== "input") return;
    if (e.key === "Backspace") key("del"); else if (e.key === "Enter") key("return"); else if (e.key.length === 1) key(e.key); else return; e.preventDefault(); };
    window.addEventListener("keydown", k); return () => window.removeEventListener("keydown", k); }, []);

  if (s.phase === "input") {
    const f = frameOf("5:5542"), fF = frameOf("5:5976"), tF = frameOf("5:6041"), ov = {};
    ov[findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")).id] = { onClick: back, className: "tap" };
    ["popup", "Keyboards"].forEach((nm) => { const x = findNode(f, (y) => y.name === nm); if (x) ov[x.id] = false; });
    // вміст при фокусі плавно їде вгору (5:5976 — на 151), ілюстрація ховається
    const tb = findNode(f, (x) => x.name === "textblock"), shift = findNode(fF, (x) => x.name === "text").ay - findNode(f, (x) => x.name === "text" && x.ay > 200).ay;
    const ill = tb.children.find((c) => c.name === "img");
    ov[ill.id] = (n, el) => <div style={{ opacity: s.focus ? 0 : 1, transition: raw ? "none" : "opacity .3s ease" }}>{el}</div>;
    ov[tb.id] = (n, el, pos) => (
      <div style={{ ...pos, overflow: "hidden" }} onPointerDown={() => s.focus && upd({ focus: false })}>
        <div style={{ position: "absolute", left: -pos.left, top: -pos.top, width: 375, height: 812, transform: s.focus ? `translateY(${shift}px)` : "none", transition: raw ? "none" : "transform .42s cubic-bezier(.2,.8,.2,1)" }}>{el}</div>
      </div>
    );
    // поле: рамка «у фокусі» (5:5976) поверх звичайної; текст — введена пошта + курсор або підказка
    const field = findNode(tb, (x) => x.name === "label" && x.h === 40), fieldFocus = findNode(fF, (x) => x.name === "label" && x.h === 40);
    const typed = findNode(tF, (x) => x.t && textOf(x) === "name@example.com"), fieldT = findNode(tF, (x) => x.name === "label" && x.h === 40), ph = findNode(field, (x) => !!x.t);
    ov[field.id] = (n, el, pos) => (
      <div style={{ ...pos, cursor: "text" }} onPointerDown={(e) => { e.stopPropagation(); if (!raw) upd({ focus: true, intro: false }); }}>
        <div style={{ position: "absolute", left: -pos.left, top: -pos.top }}>{el}</div>
        <div style={{ position: "absolute", left: -pos.left, top: -pos.top, opacity: s.focus ? 1 : 0, transition: raw ? "none" : "opacity .2s" }}>
          <FigNode n={{ ...fieldFocus, children: fieldFocus.children.filter((c) => !c.t && !(c.children || []).some((d) => d.t)) }} px={fieldFocus.ax - pos.left} py={fieldFocus.ay - pos.top} />
        </div>
        <div style={{ position: "absolute", left: typed.ax - fieldT.ax, top: typed.ay - fieldT.ay, height: 20, display: "flex", alignItems: "center", whiteSpace: "nowrap",
          font: '400 14px/20px "Inter", sans-serif', color: s.email ? "#000" : "rgba(115,115,115,.5)" }}>
          {s.email || (s.focus ? "" : null)}{s.focus && <Caret />}{!s.email && <span style={{ color: "rgba(115,115,115,.5)" }}>name@example.com</span>}
        </div>
      </div>
    );
    if (ph) ov[ph.id] = false;
    // кнопка: неактивна, доки пошта не схожа на справжню; при фокусі піднімається над клавіатурою
    const b = btnWith(f, "Check for Breaches"), bF = btnWith(fF, "Check for Breaches");
    // у кадрі кнопка вже напівпрозора (op 0.25) — малюємо її без власної прозорості, а прозорість задаємо станом
    ov[b.id] = (n, el, pos) => (
      <div onClick={ok ? submit : undefined} role="button" className={ok ? "tap" : ""} style={{ ...pos, cursor: ok ? "pointer" : "default", opacity: ok ? 1 : 0.25,
        transition: raw ? "none" : "opacity .25s" }}>
        <div style={{ position: "absolute", left: -pos.left, top: -pos.top }}><FigNode n={{ ...n, op: undefined }} px={n.ax - pos.left} py={n.ay - pos.top} /></div>
      </div>
    );
    // при фокусі кнопка піднімається над клавіатурою: рухаємо її рамку «btn» (вона обрізає вміст)
    const bWrap = findNode(f, (x) => x.name === "btn" && x.type === "FRAME");
    if (bWrap) ov[bWrap.id] = (n, el, pos) => <div style={{ position: "absolute", left: 0, top: 0, width: 0, height: 0, transform: s.focus ? `translateY(${bF.ay - b.ay}px)` : "none",
      transition: raw ? "none" : "transform .42s cubic-bezier(.2,.8,.2,1)" }}>{el}</div>;
    // мітка «Enter email» у фокусі синя (5:5976)
    const lab = nodeText(tb, "Enter email"), labF = nodeText(fF, "Enter email");
    if (lab && labF) ov[lab.id] = (n, el, pos) => <div style={{ position: "absolute", left: 0, top: 0, color: "inherit" }}>
      <div style={{ opacity: s.focus ? 0 : 1, transition: raw ? "none" : "opacity .2s" }}>{el}</div>
      <div style={{ position: "absolute", left: 0, top: 0, opacity: s.focus ? 1 : 0, transition: raw ? "none" : "opacity .2s" }}>
        <FigNode n={{ ...n, t: { ...n.t, runs: n.t.runs.map((r) => ({ ...r, c: labF.t.runs[0].c })) } }} px={n.ax - pos.left} py={n.ay - pos.top} /></div>
    </div>;
    const iF = frameOf("5:5869"), intro = findNode(iF, (x) => x.name === "popup"), iBlack = findNode(iF, (x) => x.name === "blackout");
    const iov = { [btnWith(intro, "Check My Email").id]: { onClick: () => { upd({ intro: false, first: false }); setTimeout(() => upd({ focus: true }), 380); }, className: "tap" } };
    const iill = findNode(intro, (x) => x.name === "img"); if (iill && !raw) iov[iill.id] = (n, el) => <div className="float-slow">{el}</div>;
    return (<>
      <FigFrame id="5:5542" ov={ov} raw={raw} />
      {!raw && <Keyboard kind="alpha" show={s.focus} onKey={key} />}
      {raw && s.focus && <FigNode n={findNode(fF, (x) => x.name === "Keyboards")} />}
      <FigSheetOf open={s.intro} black={iBlack} node={intro} ov={iov} onClose={() => upd({ intro: false, first: false })} raw={raw} />
    </>);
  }
  if (s.phase === "check") {
    const f = frameOf("5:8265"), ov = {};
    ov[findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")).id] = { onClick: () => upd({ phase: "input" }), className: "tap" };
    ov[findNode(f, (x) => x.name === "loading").id] = (n, e, pos) => <WaveRing n={n} pos={pos} p={p} />;
    return <FigFrame id="5:8265" ov={ov} raw={raw} />;
  }
  const fid = { none: "5:5649", found: "5:5705", detail: "5:5781" }[s.phase], f = frameOf(fid), ov = {};
  const bk = findNode(f, (x) => (x.comp || "").startsWith("Icon Btn"));
  ov[bk.id] = { onClick: s.phase === "detail" ? () => upd({ phase: "found" }) : () => upd({ phase: "input", focus: false }), className: "tap" };
  ov[btnWith(f, "Done").id] = { onClick: s.phase === "detail" ? () => upd({ phase: "found" }) : () => go("dashboard", "fade"), className: "tap" };
  if (s.phase === "none") {
    const t = findNode(f, (x) => x.t && /was not found/.test(textOf(x)));
    if (t && s.email && s.email !== "name@example.com") ov[t.id] = (n, e, pos) => <FigNode n={{ ...n, t: { ...n.t, runs: n.t.runs.map((r) => ({ ...r, t: r.t.replace("name@example.com", s.email) })) } }} px={n.ax - pos.left} py={n.ay - pos.top} />;
    const img = findNode(f, (x) => x.name === "img"); if (img && !raw) ov[img.id] = (n, el) => <div className="pop-in">{el}</div>;
  }
  if (s.phase === "found") {
    const cards = findNode(f, (x) => x.name === "Frame" && x.children && x.children.some((c) => c.name === "menu")).children.filter((c) => c.name === "menu");
    cards.forEach((c, i) => { ov[c.id] = { onClick: i === 0 ? () => upd({ phase: "detail" }) : () => toast("Canva: the same details screen, filled from the server"), className: raw ? "" : "tap rise", style: { animationDelay: `${0.12 + i * 0.08}s` } }; });
    const hero = findNode(f, (x) => x.name === "Hero"); if (hero && !raw) ov[hero.id] = { className: "rise", style: { animationDelay: "0.04s" } };
  }
  if (s.phase === "detail") {
    const cards = findNode(f, (x) => x.name === "Frame" && x.children && x.children.some((c) => c.name === "menu")).children.filter((c) => c.name === "menu");
    cards.forEach((c, i) => { const t = firstText(c); ov[c.id] = { onClick: () => toast(`${t} — what the chip does is a question for the manager`), className: raw ? "" : "tap rise", style: { animationDelay: `${0.1 + i * 0.07}s` } }; });
  }
  return <FigFrame id={fid} ov={ov} raw={raw} />;
}

// ── Ряд 9: Password Generator (4 кадри) ──
// 5:5141 перший вхід · 5:5443 знайомство · 5:5239 генератор · 5:5341 «Copied!». Стани Strong / Medium / Weak — варіанти компонента «password» (5:914 / 5:924 / 5:934).
const PW_IDS = ["pw-first", "pw-intro", "pw-on", "pw-copied"];
const PW_PRESET = { "pw-first": { first: true, intro: false, copied: false }, "pw-intro": { first: true, intro: true, copied: false }, "pw-on": { first: false, intro: false, copied: false }, "pw-copied": { first: false, intro: false, copied: true } };
const pwId = (s) => s.intro ? "pw-intro" : s.copied ? "pw-copied" : s.first ? "pw-first" : "pw-on";
const PW_MIN = 8, PW_MAX = 20;   // діапазон у дизайні не вказаний — за положенням повзунка (16 ≈ ⅔ доріжки)
const pwMake = (len, num, sym, up) => {
  const lo = "abcdefghijkmnopqrstuvwxyz", U = "ABCDEFGHJKLMNPQRSTUVWXYZ", N = "23456789", S = "!#$%&*?@";
  const sets = [lo, ...(up ? [U] : []), ...(num ? [N] : []), ...(sym ? [S] : [])], all = sets.join("");
  const r = (str) => str[Math.floor(Math.random() * str.length)];
  const out = sets.map(r); while (out.length < len) out.push(r(all));
  for (let i = out.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [out[i], out[j]] = [out[j], out[i]]; }
  return out.join("");
};
const pwStrength = (len, num, sym, up) => { const kinds = 1 + num + sym + up; return len >= 14 && kinds >= 3 ? 0 : len >= 10 && kinds >= 2 ? 1 : 2; };   // 0 Strong · 1 Medium · 2 Weak

function Password({ preset, onState, back, toast, raw }) {
  const [s, setS] = useState(() => ({ first: false, intro: false, copied: false, len: 16, num: 1, sym: 1, up: 1, pwd: "kV9#mRt2$wLq7!Xz", ...PW_PRESET[preset] }));
  const sRef = useRef(s); sRef.current = s;
  const upd = (p) => setS((o) => ({ ...o, ...(typeof p === "function" ? p(o) : p) }));
  useEffect(() => { if (pwId(sRef.current) !== preset) setS((o) => ({ ...o, ...PW_PRESET[preset] })); }, [preset]);
  useEffect(() => { const id = pwId(s); if (id !== preset) onState(id); }, [s]);
  useAutoIntro("pw", preset === "pw-first", raw, () => upd((o) => (!o.intro ? { intro: true } : {})));
  useEffect(() => { if (raw || !s.copied) return; const t = setTimeout(() => upd({ copied: false }), 1700); return () => clearTimeout(t); }, [s.copied]);
  // новий пароль: літери коротко «перебираються», потім стають на місце
  const [disp, setDisp] = useState(s.pwd);
  useEffect(() => { if (raw) { setDisp(s.pwd); return; } let k = 0; const all = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789!#$%&*?@";
    const t = setInterval(() => { k++; const fixed = Math.floor((k / 9) * s.pwd.length);
      setDisp(s.pwd.split("").map((c, i) => (i < fixed ? c : all[Math.floor(Math.random() * all.length)])).join("")); if (k >= 9) { clearInterval(t); setDisp(s.pwd); } }, 34);
    return () => clearInterval(t); }, [s.pwd]);
  const regen = (p = {}) => upd((o) => { const n = { ...o, ...p }; return { ...p, pwd: pwMake(n.len, n.num, n.sym, n.up) }; });
  const str = pwStrength(s.len, s.num, s.sym, s.up);

  const f = frameOf("5:5239"), ov = {};
  ov[findNode(f, (x) => (x.comp || "").startsWith("Icon Btn")).id] = { onClick: back, className: "tap" };
  ["popup", "Copied!"].forEach((nm) => { const x = findNode(f, (y) => y.name === nm && y.type === "FRAME"); if (x) ov[x.id] = false; });
  // картка пароля: текст (живий) + шкала й статус — три варіанти з дизайн-системи перетікають
  const card = findNode(f, (x) => (x.comp || "").startsWith("password"));
  const txt = findNode(card, (x) => !!x.t), vars = ["5:914", "5:924", "5:934"].map((id) => frameOf(id));
  ov[card.id] = (n, e, pos) => (
    <div style={{ ...pos }}>
      <FigNode n={{ ...n, children: [] }} px={n.ax - 0} py={n.ay - 0} ov={{}} />
      <div style={{ position: "absolute", left: 0, top: 0 }}>
        <FigText n={txt} px={n.ax} py={n.ay} text={disp} style={{ fontVariantLigatures: "none" }} />
      </div>
      {vars.map((v, i) => (
        <div key={i} style={{ position: "absolute", inset: 0, opacity: i === str ? 1 : 0, transition: raw ? "none" : "opacity .3s ease" }}>
          {v.children.filter((c) => !c.t).map((c) => <FigNode key={c.id} n={c} px={0} py={0} />)}
        </div>
      ))}
    </div>
  );
  // довжина: живий повзунок (доріжка 311, кулька 16, заливка до центру кульки)
  const lenT = findNode(f, (x) => x.t && /^Length/.test(textOf(x)));
  ov[lenT.id] = (n, e, pos) => <FigText n={n} px={n.ax - pos.left} py={n.ay - pos.top} text={`Length · ${s.len}`} />;
  const sl = findNode(f, (x) => x.name === "LENGTH");
  ov[sl.id] = (n, e, pos) => <Slider pos={pos} v={s.len} min={PW_MIN} max={PW_MAX} raw={raw} onChange={(len) => { if (len !== sRef.current.len) regen({ len }); }} />;
  // перемикачі цифр / символів / великих літер
  const rowsP = labelsIn(f, "options card");
  [["Numbers", "num"], ["Symbols", "sym"], ["Uppercase", "up"]].forEach(([name, k]) => {
    const r = rowsP.find((l) => firstText(l).includes(name) || textOf(l).includes(name)); if (!r) return;
    const sw = findNode(r, (x) => x.name === "Switch");
    ov[r.id] = (n, el, pos) => (
      <div onClick={raw ? undefined : () => regen({ [k]: sRef.current[k] ? 0 : 1 })} role="switch" aria-checked={!!s[k]} className={raw ? "" : "tap-soft"} style={{ ...pos, cursor: "pointer" }}>
        <div style={{ position: "absolute", left: -pos.left, top: -pos.top }}><FigNode n={{ ...n, children: n.children.filter((c) => c.name !== "Switch") }} px={n.ax - pos.left} py={n.ay - pos.top} /></div>
        <Switch on={!!s[k]} x={sw.ax - n.ax} y={sw.ay - n.ay} raw={raw} />
      </div>
    );
  });
  ov[btnWith(f, "Regenerate").id] = { onClick: () => regen(), className: "tap" };
  ov[btnWith(f, "Copy Password").id] = { onClick: () => { try { navigator.clipboard && navigator.clipboard.writeText(sRef.current.pwd); } catch (err) {} upd({ copied: true, first: false }); }, className: "tap" };
  const cop = findNode(frameOf("5:5341"), (x) => x.name === "Copied!" && x.type === "FRAME");
  const iF = frameOf("5:5443"), intro = findNode(iF, (x) => x.name === "popup"), iBlack = findNode(iF, (x) => x.name === "blackout");
  const iov = { [btnWith(intro, "Generate a Password").id]: { onClick: () => { upd({ intro: false, first: false }); setTimeout(() => regen(), 300); }, className: "tap" } };
  const ill = findNode(intro, (x) => x.name === "img"); if (ill && !raw) iov[ill.id] = (n, el) => <div className="float-slow">{el}</div>;
  return (<>
    <FigFrame id="5:5239" ov={ov} raw={raw} />
    <div style={{ position: "absolute", inset: 0, zIndex: 8, pointerEvents: "none", opacity: s.copied ? 1 : 0, transform: s.copied ? "none" : "translateY(8px) scale(.96)",
      transition: raw ? "none" : "opacity .25s ease, transform .3s cubic-bezier(.2,.9,.25,1)" }}><FigNode n={cop} /></div>
    <FigSheetOf open={s.intro} black={iBlack} node={intro} ov={iov} onClose={() => upd({ intro: false, first: false })} raw={raw} />
  </>);
}
// повзунок (як у кадрі: доріжка rgba(115,115,115,.15) 4 px, заливка й кулька #0b62da, тінь кульки)
function Slider({ pos, v, min, max, onChange, raw }) {
  const ref = useRef(null), W = pos.width, k = (v - min) / (max - min), cx = 2 + k * (W - 4);
  const set = (e) => { const r = ref.current.getBoundingClientRect(), sc = r.width / W; const x = (e.clientX - r.left) / sc; onChange(Math.round(min + Math.max(0, Math.min(1, (x - 2) / (W - 4))) * (max - min))); };
  return (
    <div ref={ref} style={{ ...pos, cursor: "pointer", touchAction: "none" }}
      onPointerDown={(e) => { if (raw) return; e.currentTarget.setPointerCapture(e.pointerId); set(e); }} onPointerMove={(e) => { if (e.buttons) set(e); }}>
      <div style={{ position: "absolute", left: 0, top: 6, width: W, height: 4, borderRadius: 2, background: "rgba(115,115,115,.15)" }} />
      <div style={{ position: "absolute", left: 0, top: 6, width: cx + 2, height: 4, borderRadius: 2, background: "#0b62da" }} />
      <div style={{ position: "absolute", left: cx - 8, top: 0, width: 16, height: 16, borderRadius: 8, background: "#0b62da", boxShadow: "0 2px 8px rgba(0,0,0,.2)" }} />
    </div>
  );
}

// ── Ряд 10: Settings і My Account (3 кадри) ──
// 5:2466 Settings (вкладка) · 5:2193 My Account (увійшов) · 5:2343 My Account (без входу). My Account — аркуш поверх головної.
const ACC_IDS = ["account", "account-guest"];
function AccountSheet({ open, guest, onClose, go, toast, raw }) {
  const F = frameOf(guest ? "5:2343" : "5:2193"), sheet = findNode(F, (x) => x.name === "My Account" && x.type === "FRAME" && x.ay > 50), black = findNode(F, (x) => x.name === "blackout");
  const ov = {}, t = (txt) => nodeText(sheet, txt);
  const row = (txt, fn) => { const n = t(txt); if (!n) return; const lab = findNode(sheet, (x) => (x.name === "Label" || x.name === "label") && x.children && findNode(x, (y) => y === n)); ov[(lab || n).id] = { onClick: fn, className: "tap" }; };
  ov[t("Done").id] = { onClick: onClose, className: "tap" };
  row("Manage Subscription", () => toast("Opens the account area in the browser (Google Play subscription → Google Play page)"));
  row("Restore Purchases", () => toast("Restoring purchases from Google Play…"));
  row("Sign Out", () => { onClose(); setTimeout(() => go("signin", "fade"), 350); });
  row("Delete Account", () => toast("First a warning that deleting does not cancel the subscription — no such screen in Figma"));
  row("Sign In", () => { onClose(); setTimeout(() => go("signin", "fade"), 350); });
  return <FigSheetOf open={open} black={black} node={sheet} ov={ov} onClose={onClose} raw={raw} z={25} />;
}

function Settings({ go, toast, scen, raw }) {
  const [acc, setAcc] = useState(false);
  const f = frameOf("5:2466"), ov = {};
  labelsIn(f).forEach((l, i) => {
    const t = firstText(l);
    const act = t === "My Account" ? () => setAcc(true) : t === "Language" ? () => toast("Language — no language screen in Figma")
      : t === "Contact" ? () => toast("Contact — what it opens is a question for the manager") : t === "Unsubscribe" ? () => toast("Opens cancellation: Google Play subscriptions page or the account area")
      : () => toast(`${t} — opens in the browser (Custom Tabs)`);
    ov[l.id] = { onClick: act, className: raw ? "" : "tap rise", style: { animationDelay: `${0.06 + i * 0.04}s` } };
  });
  const mb = findNode(f, (x) => x.name === "Menubar");
  mb.children.forEach((b) => { const t = textOf(b); if (t === "Home") ov[b.id] = { onClick: () => go("dashboard", "fade"), className: "tap" }; else if (t === "Alerts") ov[b.id] = { onClick: () => toast("Alerts — no screen in Figma (question for the manager)"), className: "tap" }; });
  return (<>
    <FigFrame id="5:2466" ov={ov} raw={raw} />
    <AccountSheet open={acc} guest={scen.account === "guest"} onClose={() => setAcc(false)} go={go} toast={toast} raw={raw} />
  </>);
}

// Група екрана: усередині групи компонент живий (пейджер, вхід, VPN…), між групами — перехід
const groupOf = (id) => ONB_IDS.includes(id) ? "onboarding" : AUTH_IDS.includes(id) ? "auth" : /^(result-issues|sheet)/.test(id) ? "result-issues" : VPN_IDS.includes(id) ? "vpn" : WEB_IDS.includes(id) ? "web" : QR_IDS.includes(id) ? "qr" : ST_IDS.includes(id) ? "st" : BR_IDS.includes(id) ? "br" : PW_IDS.includes(id) ? "pw" : ACC_IDS.includes(id) ? "dashboard" : id;
// Застиглий знімок екрана для шару, що йде (стан — як у пресеті кадру Figma)
function StaticScreen({ id, plan, scen }) {
  const nop = () => {};
  return (
    <div className="raw" style={{ position: "absolute", inset: 0 }}>
      {id === "splash" && <Splash go={nop} raw />}
      {ONB_IDS.includes(id) && <Onboarding index={ONB_IDS.indexOf(id)} onIndex={nop} go={nop} raw />}
      {id === "paywall" && <Paywall go={nop} toast={nop} plan={plan} setPlan={nop} />}
      {AUTH_IDS.includes(id) && <Auth preset={id} onState={nop} go={nop} back={nop} scen={scen} toast={nop} raw />}
      {(id === "dashboard" || id === "dash-issues" || ACC_IDS.includes(id)) && <Dashboard issues={id === "dash-issues"} account={id === "account" ? "user" : id === "account-guest" ? "guest" : null} go={nop} toast={nop} raw />}
      {ST_IDS.includes(id) && <Storage preset={id} onState={nop} back={nop} toast={nop} raw />}
      {BR_IDS.includes(id) && <Breach preset={id} onState={nop} back={nop} go={nop} toast={nop} scen={{ breach: "found", account: "user" }} raw />}
      {PW_IDS.includes(id) && <Password preset={id} onState={nop} back={nop} toast={nop} raw />}
      {id === "settings" && <Settings go={nop} toast={nop} scen={{ breach: "found", account: "user" }} raw />}
      {id === "scan" && <Scan go={nop} back={nop} result="ok" raw />}
      {/^(result|sheet)/.test(id) && <Result preset={id} onState={nop} go={nop} toast={nop} raw />}
      {VPN_IDS.includes(id) && <Vpn preset={id} onState={nop} back={nop} toast={nop} raw />}
      {WEB_IDS.includes(id) && <Web preset={id} onState={nop} back={nop} raw />}
      {QR_IDS.includes(id) && <Qr preset={id} onState={nop} back={nop} toast={nop} raw />}
    </div>
  );
}

// ── Карта екранів (порядок = рядами Figma) ──
const ONB_FRAMES = [fr02, fr03, fr04, fr05, fr06, fr07];
// Кадр paywall у Figma — свій на кожен обраний тариф
const PW_FRAMES = { weekly: [fr08, "5:2948"], monthly: [fr09, "5:3055"], yearly: [fr10, "5:3162"] };
const SCREENS = [
  { id: "splash", name: "Splash", node: "5:2530", frame: fr01 },
  ...PAGES.map((p, i) => ({ id: p.id, name: i ? `Onboarding · ${i}/5` : "Onboarding · intro", node: p.node, frame: ONB_FRAMES[i] })),
  { id: "paywall", name: "Paywall", node: "5:2948", frame: fr08 },
  { id: "signin", name: "Sign in", node: "5:4374", frame: r2f01 },
  { id: "signin-typing", name: "Sign in · typing", node: "5:4923", frame: r2f02 },
  { id: "signin-filled", name: "Sign in · email ready", node: "5:4985", frame: r2f03 },
  { id: "offline", name: "Can't connect", node: "5:5047", frame: r2f04 },
  { id: "code", name: "Check your email", node: "5:4433", frame: r2f05 },
  { id: "code-typing", name: "Code · typing", node: "5:4775", frame: r2f06 },
  { id: "code-filled", name: "Code · 6 digits", node: "5:4846", frame: r2f07 },
  { id: "verifying", name: "Verifying", node: "5:4502", frame: r2f08 },
  { id: "code-error", name: "Code · invalid", node: "5:4589", frame: r2f09 },
  { id: "sub-ended", name: "Subscription ended", node: "5:4668", frame: r2f10 },
  { id: "no-sub", name: "No subscription", node: "5:4722", frame: r2f11 },
  { id: "dashboard", name: "Dashboard", node: "5:1686", frame: r3f01 },
  { id: "scan", name: "Scanning", node: "5:8170", frame: r3f02 },
  { id: "result-ok", name: "Result · all passed", node: "5:2114", frame: r3f03 },
  { id: "result-issues", name: "Result · 2 issues", node: "5:1784", frame: r3f04 },
  { id: "sheet-safari", name: "Sheet · Safari setup", node: "5:1868", frame: r3f05 },
  { id: "sheet-update", name: "Sheet · Software Update", node: "5:1995", frame: r3f06 },
  { id: "dash-issues", name: "Dashboard · 2 issues", node: "5:3269", frame: r3f07 },
  { id: "vpn-first", name: "VPN · first open", node: "5:3826", frame: r4f01 },
  { id: "vpn-intro", name: "VPN · intro", node: "5:3427", frame: r4f02 },
  { id: "vpn-off", name: "VPN · off", node: "5:3521", frame: r4f03 },
  { id: "vpn-loc", name: "VPN · locations", node: "5:3695", frame: r4f04 },
  { id: "vpn-connecting", name: "VPN · connecting", node: "5:7547", frame: r4f05 },
  { id: "vpn-connected", name: "VPN · connected", node: "5:7646", frame: r4f06 },
  { id: "vpn-loc-ny", name: "VPN · locations · NY", node: "5:7916", frame: r4f07 },
  { id: "vpn-loc-paris", name: "VPN · locations · Paris", node: "5:8043", frame: r4f08 },
  { id: "vpn-connecting-paris", name: "VPN · connecting Paris", node: "5:7597", frame: r4f09 },
  { id: "vpn-connected-paris", name: "VPN · connected Paris", node: "5:7781", frame: r4f10 },
  { id: "web-first", name: "Web Protection · first open", node: "5:3919", frame: r5f01 },
  { id: "web-intro", name: "Web Protection · intro", node: "5:6791", frame: r5f02 },
  { id: "web-on", name: "Web Protection · 5/5", node: "5:7412", frame: r5f03 },
  { id: "web-4", name: "Web Protection · 4/5", node: "5:7024", frame: r5f04 },
  { id: "web-3", name: "Web Protection · 3/5", node: "5:7121", frame: r5f05 },
  { id: "web-2", name: "Web Protection · 2/5", node: "5:7218", frame: r5f06 },
  { id: "web-1", name: "Web Protection · 1/5", node: "5:7315", frame: r5f07 },
  { id: "web-0", name: "Web Protection · 0/5", node: "5:6927", frame: r5f08 },
  { id: "qr-first", name: "QR Scanner · first open", node: "5:4054", frame: r6f01 },
  { id: "qr-intro", name: "QR Scanner · intro", node: "5:6417", frame: r6f02 },
  { id: "qr-scan", name: "QR Scanner · scanning", node: "5:6490", frame: r6f03 },
  { id: "qr-check", name: "QR Scanner · validating", node: "5:6569", frame: r6f04 },
  { id: "qr-blocked", name: "QR Scanner · access blocked", node: "5:6619", frame: r6f05 },
  { id: "qr-copied", name: "QR Scanner · link copied", node: "5:6705", frame: r6f06 },
  { id: "st-first", name: "Storage Cleaner · first open", node: "5:4126", frame: r7f01 },
  { id: "st-intro", name: "Storage Cleaner · intro", node: "5:6268", frame: r7f02 },
  { id: "st-scan", name: "Storage Cleaner · scanning", node: "5:8199", frame: r7f03 },
  { id: "st-ready", name: "Storage Cleaner · results", node: "5:6106", frame: r7f04 },
  { id: "st-shots", name: "Storage Cleaner · screenshots", node: "5:6203", frame: r7f05 },
  { id: "st-clean", name: "Storage Cleaner · cleaning", node: "5:8234", frame: r7f06 },
  { id: "st-done", name: "Storage Cleaner · complete", node: "5:8296", frame: r7f07 },
  { id: "br-first", name: "Breach Check · first open", node: "5:4268", frame: r8f01 },
  { id: "br-intro", name: "Breach Check · intro", node: "5:5869", frame: r8f02 },
  { id: "br-idle", name: "Breach Check · email", node: "5:5542", frame: r8f03 },
  { id: "br-typing", name: "Breach Check · typing", node: "5:5976", frame: r8f04 },
  { id: "br-filled", name: "Breach Check · email ready", node: "5:6041", frame: r8f05 },
  { id: "br-check", name: "Breach Check · checking", node: "5:8265", frame: r8f06 },
  { id: "br-none", name: "Breach Check · no breaches", node: "5:5649", frame: r8f07 },
  { id: "br-found", name: "Breach Check · 2 breaches", node: "5:5705", frame: r8f08 },
  { id: "br-detail", name: "Breach Check · LinkedIn details", node: "5:5781", frame: r8f09 },
  { id: "pw-first", name: "Password Generator · first open", node: "5:5141", frame: r9f01 },
  { id: "pw-intro", name: "Password Generator · intro", node: "5:5443", frame: r9f02 },
  { id: "pw-on", name: "Password Generator", node: "5:5239", frame: r9f03 },
  { id: "pw-copied", name: "Password Generator · copied", node: "5:5341", frame: r9f04 },
  { id: "settings", name: "Settings", node: "5:2466", frame: r10f01 },
  { id: "account", name: "My Account · signed in", node: "5:2193", frame: r10f02 },
  { id: "account-guest", name: "My Account · signed out", node: "5:2343", frame: r10f03 },
];
const ORDER = SCREENS.map((s) => s.id);
const FIGMA_FRAMES = 75;   // кадрів у секції «Seciro All-in-One» (пейвол — 3 кадри, у прототипі один екран)
// ряди Figma (як читати макет: зліва направо, рядами вниз)
const ROWS = [
  { n: 1, name: "Онбординг і підписка" }, { n: 2, name: "Вхід за кодом і стан підписки" }, { n: 3, name: "Головна й перевірка захисту" },
  { n: 4, name: "VPN" }, { n: 5, name: "Web Protection" }, { n: 6, name: "Safe QR Scanner" }, { n: 7, name: "Storage Cleaner" },
  { n: 8, name: "Breach Check" }, { n: 9, name: "Password Generator" }, { n: 10, name: "Settings і My Account" },
];
const rowOf = (id) => id === "splash" || /^onb/.test(id) || id === "paywall" ? 1 : AUTH_IDS.includes(id) ? 2 : /^(dashboard|scan|result|sheet|dash-issues)/.test(id) ? 3
  : VPN_IDS.includes(id) ? 4 : WEB_IDS.includes(id) ? 5 : QR_IDS.includes(id) ? 6 : ST_IDS.includes(id) ? 7 : BR_IDS.includes(id) ? 8 : PW_IDS.includes(id) ? 9 : 10;
// Зведення нотаток за видом: кожен текст один раз + екрани, де він трапляється (у порядку екранів)
const SUMMARY = (() => { const out = { ask: [], design: [], android: [] };
  SCREENS.flatMap((sc) => (sc.id === "paywall" ? ["paywall"] : [sc.id])).concat(["onb1", "onb2", "onb3", "onb4", "onb5", "onb6"]).forEach((id) => (NOTES[id] || []).forEach((n) => {
    const list = out[n.kind]; if (!list) return; let it = list.find((x) => x.text === n.text); if (!it) { it = { text: n.text, screens: [] }; list.push(it); }
    if (!it.screens.includes(id)) it.screens.push(id); }));
  return out; })();
const store = {
  get: (k) => { try { return localStorage.getItem(k); } catch { return null; } },
  set: (k, v) => { try { localStorage.setItem(k, v); } catch {} },
};

function useScale() {
  // Два телефони (прототип + Figma) і панель: на широкому — в ряд, на вузькому — один під одним
  const calc = () => {
    const narrow = window.innerWidth <= 1000;
    const w = narrow ? Math.min(window.innerWidth - 32, 420) : Math.min(375 * 1.15, (window.innerWidth - 300 - 2 * 28 - 40 - 2 * 20) / 2);
    const h = narrow ? Infinity : window.innerHeight - 48 - 20 - 30;
    return Math.min(w / 375, h / 812);
  };
  const [s, setS] = useState(calc);
  useEffect(() => { const f = () => setS(calc()); window.addEventListener("resize", f); return () => window.removeEventListener("resize", f); }, []);
  return s;
}

// Якщо щось у прототипі впаде — не порожня сторінка, а пояснення й «Почати знову» (скидає збережений екран)
class Boundary extends Component {
  constructor(p) { super(p); this.state = { err: null }; }
  static getDerivedStateFromError(err) { return { err }; }
  render() {
    if (!this.state.err) return this.props.children;
    return (
      <div style={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", padding: 24, boxSizing: "border-box", fontFamily: "Inter, system-ui, sans-serif" }}>
        <div style={{ maxWidth: 420, background: "var(--panel)", color: "var(--ink)", border: "1px solid var(--line)", borderRadius: 18, padding: 22, display: "grid", gap: 12 }}>
          <b style={{ fontSize: 17 }}>Прототип натрапив на помилку</b>
          <span style={{ fontSize: 13, color: "var(--muted)" }}>{String(this.state.err && this.state.err.message || this.state.err)}</span>
          <button className="pbtn" onClick={() => { try { localStorage.removeItem("seciro.screen"); } catch (e) {} location.hash = ""; location.reload(); }}>Почати знову</button>
        </div>
      </div>
    );
  }
}
export default function Seciro() { return <Boundary><App /></Boundary>; }

function App() {
  // Екран з посилання (#paywall) важливіший за збережений
  // Локальна перевірка (claude/tools/proto-diff.py): #paywall~monthly!raw — лише екран 375×812 без обгортки й анімацій
  const RAW = /!raw$/.test(location.hash);
  const LIVE = /!live$/.test(location.hash);   // звірка живого режиму: живі компоненти без обгортки сторінки
  const fromHash = () => { const h = (location.hash || "").slice(1).split(/[~!@]/)[0]; return ORDER.includes(h) ? h : null; };
  const saved = store.get("seciro.screen");
  const [screen, setScreen] = useState(fromHash() || (ORDER.includes(saved) ? saved : "splash"));
  useEffect(() => { const f = () => { const h = fromHash(); if (h) { setDir("fade"); setScreen(h); } }; window.addEventListener("hashchange", f); return () => window.removeEventListener("hashchange", f); }, []);
  const [dir, setDir] = useState("fade");
  // відповіді «сервера» для потоку входу (панель → Server answers)
  const [scen, setScen] = useState({ email: "code", code: "active", scan: "ok", breach: "found", account: "user" });
  const [plan, setPlan] = useState(((location.hash.match(/~(\w+)/) || [])[1]) || "weekly");
  const [msg, setMsg] = useState(null);
  const [hist, setHist] = useState([]);
  const scale = useScale() ;

  // Перехід між екранами у два шари: старий екран лишається на 0.45 с і йде своєю анімацією (застиглий знімок стану)
  const [leaving, setLeaving] = useState(null);
  const leave = (from, to, how) => {
    if (groupOf(from) === groupOf(to) || how === "none") return;
    const k = Date.now(); setLeaving({ id: from, dir: how, k }); setTimeout(() => setLeaving((l) => (l && l.k === k ? null : l)), 460);
  };
  const go = (id, how) => {
    const d = how || (ORDER.indexOf(id) >= ORDER.indexOf(screen) ? "fwd" : "back");
    leave(screen, id, d); setDir(d); setEx(null); setHl(null);
    setHist((h) => [...h, screen]);
    setScreen(id); store.set("seciro.screen", id);
  };
  const back = () => { const prev = hist[hist.length - 1]; if (!prev) return; setEx(null); setHl(null); leave(screen, prev, "back"); setHist((h) => h.slice(0, -1)); setDir("back"); setScreen(prev); store.set("seciro.screen", prev); };
  const toast = (t) => { setMsg(t); clearTimeout(toast.t); toast.t = setTimeout(() => setMsg(null), 2600); };

  useEffect(() => {
    const k = (e) => {
      const i = ORDER.indexOf(screen);
      if (e.key === "ArrowRight" && i < ORDER.length - 1) go(ORDER[i + 1]);
      if (e.key === "ArrowLeft" && i > 0) go(ORDER[i - 1]);
    };
    window.addEventListener("keydown", k); return () => window.removeEventListener("keydown", k);
  });

  const cur = SCREENS.find((s) => s.id === screen);
  const onbIndex = ONB_IDS.indexOf(screen);
  // пейджер сам каже, на якій сторінці зупинився — панель і кадр Figma йдуть за ним
  const isAuth = AUTH_IDS.includes(screen);
  const onAuth = (id) => { setScreen(id); store.set("seciro.screen", id); };
  const onIndex = (i) => { if (ONB_IDS[i] !== screen) { setScreen(ONB_IDS[i]); store.set("seciro.screen", ONB_IDS[i]); } };
  const [frame, frameNode] = screen === "paywall" ? PW_FRAMES[plan] : [cur.frame, cur.node];
  // список екранів скролиться окремо; поточний екран сам підкручується в поле зору (лише список, не сторінку)
  const listRef = useRef(null);
  useEffect(() => {
    const ul = listRef.current, b = ul && ul.querySelector('[aria-current="true"]'); if (!b) return;
    const top = b.offsetTop - ul.offsetTop, bottom = top + b.offsetHeight;
    if (top < ul.scrollTop + 8) ul.scrollTo({ top: top - 8, behavior: "smooth" });
    else if (bottom > ul.scrollTop + ul.clientHeight - 8) ul.scrollTo({ top: bottom - ul.clientHeight + 8, behavior: "smooth" });
  }, [screen]);
  // нотатки до екрана: рамки на телефоні (вмик./вимк.), підсвічена нотатка, фільтр «лише нові»
  const [marks, setMarks] = useState(store.get("seciro.marks") !== "0");
  const [hl, setHl] = useState(null);
  const [onlyNew, setOnlyNew] = useState(false);
  const notes = NOTES[screen] || [];
  const [ex, setEx] = useState(null);   // { note, i } — приклад зліва замість кадру Figma
  const [sum, setSum] = useState(null);   // зведення: "ask" | "design" | "android"
  const [grid, setGrid] = useState(false);   // схема всіх екранів
  useEffect(() => { setHl(null); setEx(null); }, [screen]);
  // приклад лише якщо така нотатка є на цьому екрані (стан міг лишитися з попереднього екрана)
  const exItem = ex && notes[ex.note] ? examplesOf(notes[ex.note])[ex.i] || null : null;
  const slotW = 375 * scale + (window.innerWidth > 1000 ? 20 : 0);

  if (LIVE) return (
    <div className="app" style={{ width: 375, height: 812, position: "relative", overflow: "hidden", background: C.bg }}>
      {screen === "splash" && <Splash go={() => {}} />}
      {onbIndex >= 0 && <Onboarding index={onbIndex} onIndex={() => {}} go={() => {}} />}
      {screen === "paywall" && <Paywall go={() => {}} toast={() => {}} plan={plan} setPlan={() => {}} />}
      {isAuth && <Auth preset={screen} onState={() => {}} go={() => {}} back={() => {}} scen={scen} toast={() => {}} />}
      {(screen === "dashboard" || screen === "dash-issues" || ACC_IDS.includes(screen)) && <Dashboard issues={screen === "dash-issues"} account={screen === "account" ? "user" : screen === "account-guest" ? "guest" : null} go={() => {}} toast={() => {}} />}
      {ST_IDS.includes(screen) && <Storage preset={screen} onState={() => {}} back={() => {}} toast={() => {}} />}
      {BR_IDS.includes(screen) && <Breach preset={screen} onState={() => {}} back={() => {}} go={() => {}} toast={() => {}} scen={scen} />}
      {PW_IDS.includes(screen) && <Password preset={screen} onState={() => {}} back={() => {}} toast={() => {}} />}
      {screen === "settings" && <Settings go={() => {}} toast={() => {}} scen={scen} />}
      {screen === "scan" && <Scan go={() => {}} back={() => {}} result="ok" raw />}
      {/^(result|sheet)/.test(screen) && <Result preset={screen} onState={() => {}} go={() => {}} toast={() => {}} />}
      {VPN_IDS.includes(screen) && <Vpn preset={screen} onState={() => {}} back={() => {}} toast={() => {}} />}
      {WEB_IDS.includes(screen) && <Web preset={screen} onState={() => {}} back={() => {}} />}
      {QR_IDS.includes(screen) && <Qr preset={screen} onState={() => {}} back={() => {}} toast={() => {}} />}
      <GestureBar />
    </div>
  );
  if (RAW) return (
    <div className="app raw" style={{ width: 375, height: 812, position: "relative", overflow: "hidden", background: C.bg }}>
      {screen === "splash" && <Splash go={() => {}} raw />}
      {onbIndex >= 0 && <Onboarding index={onbIndex} onIndex={() => {}} go={() => {}} raw at={+((location.hash.match(/@([\d.]+)/) || [])[1] ?? onbIndex)} />}
      {screen === "paywall" && <Paywall go={() => {}} toast={() => {}} plan={plan} setPlan={() => {}} />}
      {isAuth && <Auth preset={screen} onState={() => {}} go={() => {}} back={() => {}} scen={scen} toast={() => {}} raw />}
      {(screen === "dashboard" || screen === "dash-issues" || ACC_IDS.includes(screen)) && <Dashboard issues={screen === "dash-issues"} account={screen === "account" ? "user" : screen === "account-guest" ? "guest" : null} go={() => {}} toast={() => {}} raw />}
      {ST_IDS.includes(screen) && <Storage preset={screen} onState={() => {}} back={() => {}} toast={() => {}} raw />}
      {BR_IDS.includes(screen) && <Breach preset={screen} onState={() => {}} back={() => {}} go={() => {}} toast={() => {}} scen={scen} raw />}
      {PW_IDS.includes(screen) && <Password preset={screen} onState={() => {}} back={() => {}} toast={() => {}} raw />}
      {screen === "settings" && <Settings go={() => {}} toast={() => {}} scen={scen} raw />}
      {screen === "scan" && <Scan go={() => {}} back={() => {}} result="ok" raw />}
      {/^(result|sheet)/.test(screen) && <Result preset={screen} onState={() => {}} go={() => {}} toast={() => {}} raw />}
      {VPN_IDS.includes(screen) && <Vpn preset={screen} onState={() => {}} back={() => {}} toast={() => {}} raw />}
      {WEB_IDS.includes(screen) && <Web preset={screen} onState={() => {}} back={() => {}} raw />}
      {QR_IDS.includes(screen) && <Qr preset={screen} onState={() => {}} back={() => {}} toast={() => {}} raw />}
      <GestureBar />
    </div>
  );

  return (
    <div className="stage">
      <div className="phone-slot" style={{ width: slotW }}>
        <div className="cap"><b>Prototype</b><span>click like in the app</span></div>
        <div className="phone-shell">
          <div className="phone-clip" style={{ width: 375 * scale, height: 812 * scale }}>
            <div className="app" style={{ width: 375, height: 812, position: "absolute", left: 0, top: 0, transform: `scale(${scale})`, transformOrigin: "0 0", background: C.bg }}>
              {leaving && (
                <div key={"out-" + leaving.k} className={`scr-out-${leaving.dir}`} style={{ position: "absolute", inset: 0, background: C.bg, pointerEvents: "none", overflow: "hidden" }}>
                  <StaticScreen id={leaving.id} plan={plan} scen={scen} />
                </div>
              )}
              <div key={groupOf(screen)} className={`scr-in-${dir}${dir === "up" ? " enter-up" : ""}`} style={{ position: "absolute", inset: 0, background: C.bg, overflow: "hidden" }}>
                {screen === "splash" && <Splash go={go} />}
                {onbIndex >= 0 && <Onboarding index={onbIndex} onIndex={onIndex} go={go} />}
                {screen === "paywall" && <Paywall go={go} toast={toast} plan={plan} setPlan={setPlan} />}
                {isAuth && <Auth preset={screen} onState={onAuth} go={go} back={back} scen={scen} toast={toast} />}
                {(screen === "dashboard" || screen === "dash-issues" || ACC_IDS.includes(screen)) && <Dashboard issues={screen === "dash-issues"} account={screen === "account" ? "user" : screen === "account-guest" ? "guest" : null} go={go} toast={toast} onState={onAuth} scen={scen} />}
                {ST_IDS.includes(screen) && <Storage preset={screen} onState={onAuth} back={back} toast={toast} />}
                {BR_IDS.includes(screen) && <Breach preset={screen} onState={onAuth} back={back} go={go} toast={toast} scen={scen} />}
                {PW_IDS.includes(screen) && <Password preset={screen} onState={onAuth} back={back} toast={toast} />}
                {screen === "settings" && <Settings go={go} toast={toast} scen={scen} />}
                {screen === "scan" && <Scan go={go} back={back} result={scen.scan} />}
                {/^(result|sheet)/.test(screen) && <Result preset={screen} onState={onAuth} go={go} toast={toast} />}
                {VPN_IDS.includes(screen) && <Vpn preset={screen} onState={onAuth} back={back} toast={toast} />}
                {WEB_IDS.includes(screen) && <Web preset={screen} onState={onAuth} back={back} />}
                {QR_IDS.includes(screen) && <Qr preset={screen} onState={onAuth} back={back} toast={toast} />}
              </div>
              <StatusBar />
              <GestureBar />
              {marks && notes.some((n) => n.box) && (
                <div style={{ position: "absolute", inset: 0, zIndex: 35, pointerEvents: "none" }}>
                  {notes.map((n, i) => n.box && (
                    <div key={i} className="mark" style={{ position: "absolute", left: n.box[0] - 3, top: n.box[1] - 3, width: n.box[2] + 6, height: n.box[3] + 6,
                      borderColor: KINDS[n.kind].color, background: hl === i ? KINDS[n.kind].color + "26" : "transparent", opacity: hl === null || hl === i ? 1 : 0.35 }}>
                      <span style={{ background: KINDS[n.kind].color }}>{i + 1}</span>
                    </div>
                  ))}
                </div>
              )}
              {msg && (
                <Abs x={24} y={84} w={327} style={{ zIndex: 30, background: "rgba(20,26,36,.92)", color: "#fff", borderRadius: 14, padding: "12px 16px",
                  fontSize: 13, lineHeight: "18px" }} className="enter-fade">{msg}</Abs>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Figma — зліва (order −1), прототип — посередині, поруч із панеллю кнопок */}
      <div className="phone-slot" style={{ width: slotW, order: -1 }}>
        {exItem ? (
          <div className="cap ex-cap"><b style={{ color: KINDS[notes[ex.note].kind].color }}>Приклад · нотатка {ex.note + 1}</b>
            <button className="pbtn sm" onClick={() => setEx(null)}>← Figma</button></div>
        ) : <div className="cap"><b>Figma</b><span>{frame ? `frame ${frameNode}` : "no frame yet"}</span></div>}
        <div className="phone-shell">
          <div className="phone-clip" style={{ width: 375 * scale, height: 812 * scale, background: C.bg }}>
            {exItem ? (() => { const [src, cap] = exItem; const wide = /fl_vpn/.test(cap) || cap.includes("перед першим");
              return <div className="ex-view"><img key={src} src={src} alt={cap} className="enter-fade" style={wide ? { width: "100%", height: "auto", marginTop: "35%" } : { width: "100%", height: "100%" }} />
                <div className="ex-label">{cap}<span>начерк — оформлення за дизайнером</span></div></div>; })()
            : frame
              ? <img src={frame} alt={`Figma frame ${frameNode}`} draggable={false} style={{ display: "block", width: "100%", height: "100%" }} />
              : <div className="noframe">This screen comes from a later row of the Figma.</div>}
          </div>
        </div>
      </div>

      {grid && (
        <div className="sum-overlay" onClick={() => setGrid(false)}>
          <div className="sum-card grid-card" onClick={(e) => e.stopPropagation()}>
            <div className="sum-head">
              <b>Усі екрани прототипу · {SCREENS.length}</b>
              <button className="pbtn sm" onClick={() => setGrid(false)}>Закрити</button>
            </div>
            <p className="note">{FIGMA_FRAMES} кадрів Figma = {SCREENS.length} екранів (пейвол — один екран з трьома тарифами). Натисни екран, щоб відкрити. Крапка — статус нотаток, NEW — додано в останній збірці.</p>
            {ROWS.map((r) => { const items = SCREENS.filter((sc) => rowOf(sc.id) === r.n); return (
              <section key={r.n} className="grid-row">
                <div className="grid-h"><b>{r.n}</b> {r.name} <span>· {items.length}</span></div>
                <div className="grid">
                  {items.map((sc) => (
                    <button key={sc.id} className={"gcell" + (sc.id === screen ? " cur" : "")} onClick={() => { setGrid(false); go(sc.id); }}>
                      <img src={sc.frame} alt="" loading="lazy" />
                      <span className="gname"><i className="dot" style={{ background: KINDS[statusOf(sc.id)].color }} />{sc.name}{NEW.includes(sc.id) && <em className="new">NEW</em>}</span>
                    </button>))}
                </div>
              </section>); })}
          </div>
        </div>
      )}
      {sum && (
        <div className="sum-overlay" onClick={() => setSum(null)}>
          <div className="sum-card" onClick={(e) => e.stopPropagation()}>
            <div className="sum-head">
              <b style={{ color: KINDS[sum].color }}>{sum === "ask" ? "Питання Влада (розробник) до Віталія (менеджер)" : sum === "design" ? "Завдання дизайнеру: що виправити або домалювати" : "Що на Android працює інакше (робить Влад)"}</b>
              <button className="pbtn sm" onClick={() => setSum(null)}>Закрити</button>
            </div>
            <p className="note">Кожен пункт — один раз; під ним екрани, де він трапляється (натисни, щоб перейти й подивитися рамку та приклад).</p>
            <ol className="sum-list">
              {SUMMARY[sum].map((it, i) => (
                <li key={i}><span className="num" style={{ background: KINDS[sum].color }}>{i + 1}</span>
                  <div><div style={{ whiteSpace: "pre-line" }}>{it.text}</div>
                    <div className="sum-scr">{it.screens.map((id) => { const sc = SCREENS.find((x) => x.id === id); return sc && (
                      <button key={id} className="pbtn sm" onClick={() => { setSum(null); go(id); }}>{sc.name}</button>); })}</div>
                  </div></li>
              ))}
            </ol>
          </div>
        </div>
      )}
      <aside className="panel" aria-label="Prototype screens">
        <div style={{ display: "flex", flexDirection: "column", gap: 4 }}>
          <h1>Seciro · prototype</h1>
          <p className="sub">Усі ряди 1–10 з Figma · build {BUILD}. Клацай у телефоні, як у застосунку. Крапка біля екрана — статус, нижче — нотатки.</p>
        </div>
        <div className="newbar">
          <span><b>Нове в build {NEW_BUILD}:</b> {NEW_LABEL}</span>
          <button className="pbtn sm" aria-pressed={onlyNew} onClick={() => setOnlyNew((v) => !v)}>{onlyNew ? "Усі екрани" : "Лише нові"}</button>
        </div>
        <button className="pbtn allbtn" onClick={() => setGrid(true)}>▦ Усі екрани · {SCREENS.length}<span>{FIGMA_FRAMES} кадрів Figma, 10 рядів</span></button>
        <div className="sumbar">
          <span>Зведення</span>
          {["ask", "design", "android"].map((k) => (
            <button key={k} className="pbtn sm" onClick={() => setSum(k)} style={{ borderColor: KINDS[k].color }}>
              <i className="dot" style={{ background: KINDS[k].color }} />{k === "ask" ? "Віталію" : k === "design" ? "Дизайнеру" : "Android"} · {SUMMARY[k].length}</button>))}
        </div>
        <div className="label">Screens</div>
        <ul className="scr-list" ref={listRef}>
          {SCREENS.filter((s) => !onlyNew || NEW.includes(s.id)).map((s) => (
            <li key={s.id}>
              <button aria-current={s.id === screen} onClick={() => go(s.id)}>
                <i className="dot" style={{ background: KINDS[statusOf(s.id)].color }} title={KINDS[statusOf(s.id)].label} />
                <span className="nm">{s.name}</span>
                {NEW.includes(s.id) && <em className="new">NEW</em>}
                <span className="nid">{s.node}</span>
              </button>
            </li>
          ))}
        </ul>
        <div className="row">
          <button className="pbtn" onClick={back} disabled={!hist.length}>← Back</button>
          <button className="pbtn" onClick={() => { SEEN.clear(); setHist([]); setPlan("weekly"); go("splash", "fade"); }}>Restart</button>
        </div>
        <div className="notes">
          <div className="label" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <span>Нотатки до екрана</span>
            <button className="pbtn sm" aria-pressed={marks} onClick={() => { setMarks((v) => { store.set("seciro.marks", v ? "0" : "1"); return !v; }); }}>Рамки {marks ? "увімк." : "вимк."}</button>
          </div>
          {notes.length === 0 && <p className="note">Нотаток ще немає.</p>}
          <ol>
            {notes.map((n, i) => (
              <li key={i} onMouseEnter={() => setHl(i)} onMouseLeave={() => setHl(null)} onClick={() => setHl(hl === i ? null : i)} className={hl === i ? "on" : ""}>
                <span className="num" style={{ background: n.box ? KINDS[n.kind].color : "transparent", color: n.box ? "#fff" : KINDS[n.kind].color, borderColor: KINDS[n.kind].color }}>{n.box ? i + 1 : "•"}</span>
                <div><span className="kind" style={{ color: KINDS[n.kind].color }}>{KINDS[n.kind].label}</span><span style={{ whiteSpace: "pre-line" }}>{n.text}</span>
                  {examplesOf(n).length > 0 && <span className="ex-btns">{examplesOf(n).map(([src, cap], k) => (
                    <button key={k} className="pbtn sm" aria-pressed={!!ex && ex.note === i && ex.i === k} onClick={(e) => { e.stopPropagation(); setEx(ex && ex.note === i && ex.i === k ? null : { note: i, i: k }); }}>
                      Приклад{examplesOf(n).length > 1 ? ` ${"ABCDE"[k]}` : ""} →</button>))}</span>}
                </div>
              </li>
            ))}
          </ol>
          <div className="legend">{Object.entries(KINDS).map(([k, v]) => <span key={k}><i className="dot" style={{ background: v.color }} />{v.label}</span>)}</div>
        </div>
        {isAuth && (<>
          <div className="label">Server answers</div>
          <div className="note" style={{ display: "flex", flexDirection: "column", gap: 6 }}>
            <span>After email</span>
            <div className="row">{[["code", "Code sent"], ["none", "No subscription"], ["offline", "No internet"]].map(([v, t]) => (
              <button key={v} className="pbtn" aria-pressed={scen.email === v} onClick={() => setScen((o) => ({ ...o, email: v }))}>{t}</button>))}</div>
            <span>After code</span>
            <div className="row">{[["active", "Active"], ["ended", "Ended"], ["wrong", "Wrong code"]].map(([v, t]) => (
              <button key={v} className="pbtn" aria-pressed={scen.code === v} onClick={() => setScen((o) => ({ ...o, code: v }))}>{t}</button>))}</div>
            <span>Type with the on-screen keys or your keyboard. On the letter keyboard “123” adds @, then dots.</span>
          </div>
        </>)}
        {BR_IDS.includes(screen) && (<>
          <div className="label">Breach Check finds</div>
          <div className="row">{[["none", "Nothing"], ["found", "2 breaches"]].map(([v, t]) => (
            <button key={v} className="pbtn" aria-pressed={scen.breach === v} onClick={() => setScen((o) => ({ ...o, breach: v }))}>{t}</button>))}</div>
        </>)}
        {(screen === "dashboard" || screen === "settings" || ACC_IDS.includes(screen)) && (<>
          <div className="label">Account</div>
          <div className="row">{[["user", "Signed in"], ["guest", "Signed out"]].map(([v, t]) => (
            <button key={v} className="pbtn" aria-pressed={scen.account === v} onClick={() => setScen((o) => ({ ...o, account: v }))}>{t}</button>))}</div>
        </>)}
        {(screen === "dashboard" || screen === "dash-issues" || screen === "scan") && (<>
          <div className="label">Scan finds</div>
          <div className="row">{[["ok", "Nothing"], ["issues", "2 issues"]].map(([v, t]) => (
            <button key={v} className="pbtn" aria-pressed={scen.scan === v} onClick={() => setScen((o) => ({ ...o, scan: v }))}>{t}</button>))}</div>
        </>)}
      </aside>
    </div>
  );
}
