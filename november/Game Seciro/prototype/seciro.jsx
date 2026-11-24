// ─────────────────────────────────────────────────────────────────────────────
// Seciro — клікабельний прототип (джерело правди для екранів і переходів).
// Правило: спершу екран тут → VELDAN проклацує → лише тоді Kotlin + LibGDX.
//
// Усі числа — з Figma «UAPP 2.0» / секція «Seciro All-in-One» (5:1685), кадр 375×812.
// Знімки шарів — claude/figma/json/<node>.json, ілюстрації — claude/figma/art (SVG з Figma 1:1).
// Позиції — абсолютні координати кадру, як у Figma, щоб звіряти «піксель у піксель».
// ─────────────────────────────────────────────────────────────────────────────
import { useEffect, useRef, useState } from "react";

import logoSplash from "../claude/figma/art/5-2530_logo_5-2554.svg";
import logoHeader from "../claude/figma/art/5-2620_logo_5-2651.svg";
import pwHeader from "../claude/figma/art/5-2948_logo_5-3052.svg";
import imgOnb1 from "../claude/figma/art/5-2620_img_5-2655.svg";
import shape2 from "../claude/figma/art/5-2557_Shape_Set_5-2558.svg";
import img2 from "../claude/figma/art/5-2557_img_5-2592.svg";
import shape3 from "../claude/figma/art/5-2683_Shape_Set_5-2684.svg";
import img3 from "../claude/figma/art/5-2683_img_5-2685.svg";
import shape4 from "../claude/figma/art/5-2747_Shape_Set_5-2748.svg";
import img4 from "../claude/figma/art/5-2747_img_5-2749.svg";
import shape5 from "../claude/figma/art/5-2815_Shape_Set_5-2816.svg";
import img5 from "../claude/figma/art/5-2815_img_5-2817.svg";
import shape6 from "../claude/figma/art/5-2874_Shape_Set_5-2875.svg";
import img6 from "../claude/figma/art/5-2874_img_5-2876.svg";
import imgPw from "../claude/figma/art/5-2948_img_5-2950.svg";
import icVpnLock from "../claude/figma/icons/vpn-lock.svg";
import icSecurity from "../claude/figma/icons/security.svg";
import icQr from "../claude/figma/icons/qr-code.svg";
import icSparkles from "../claude/figma/icons/sparkles.svg";
import icEmail from "../claude/figma/icons/alternate-email.svg";
import icKey from "../claude/figma/icons/vpn-key.svg";
import icShield from "../claude/figma/icons/shield.svg";
// Кадри Figma @2x — для звірки поруч із телефоном (і накладанням поверх)
import fr01 from "../claude/figma/frames/r1_01_5-2530.png";
import fr02 from "../claude/figma/frames/r1_02_5-2620.png";
import fr03 from "../claude/figma/frames/r1_03_5-2557.png";
import fr04 from "../claude/figma/frames/r1_04_5-2683.png";
import fr05 from "../claude/figma/frames/r1_05_5-2747.png";
import fr06 from "../claude/figma/frames/r1_06_5-2815.png";
import fr07 from "../claude/figma/frames/r1_07_5-2874.png";
import fr08 from "../claude/figma/frames/r1_08_5-2948.png";
import fr09 from "../claude/figma/frames/r1_09_5-3055.png";
import fr10 from "../claude/figma/frames/r1_10_5-3162.png";

export const BUILD = 3;   // піднімати на кожну публікацію

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
const Art = ({ src, x, y }) => <img src={src} alt="" draggable={false} style={{ position: "absolute", left: x, top: y, pointerEvents: "none" }} />;
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
      style={{ borderRadius: 100, background: prime ? C.blue : C.bg, border: prime ? "none" : `1px solid ${a(C.blue, .15)}`,
        display: "flex", alignItems: "center", justifyContent: "center", padding: "16px 24px",
        fontFamily: FONT_TEXT, fontSize: 16, lineHeight: "20px", color: prime ? C.white : C.blue }}>
      {children}
    </Abs>
  );
}

// navig — крапки сторінок онбордингу: активна 16×4, решта 4×4 з α .25
const Dots = ({ y, active }) => (
  <Abs x={145.5} y={y} w={84} h={16} style={{ display: "flex", gap: 10, padding: 6, alignItems: "center" }}>
    {[0, 1, 2, 3, 4].map((i) => (
      <span key={i} style={{ width: i === active ? 16 : 4, height: 4, borderRadius: 2, background: C.black, opacity: i === active ? 1 : .25,
        transition: "width .25s ease" }} />
    ))}
  </Abs>
);

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

// ── Екрани ──
function Splash({ go }) {
  useEffect(() => { const t = setTimeout(() => go("onb1"), 2200); return () => clearTimeout(t); }, []);
  return (
    <>
      <Art src={logoSplash} x={88.5} y={378} />
      {/* loader — дуга 40×40, обведення 4, синя */}
      <Abs x={167.5} y={540} w={40} h={40} style={{ animation: "spin 1s linear infinite" }}>
        <svg width="40" height="40" viewBox="0 0 40 40"><circle cx="20" cy="20" r="18" fill="none" stroke={C.blue} strokeWidth="4" strokeDasharray="84.8 28.3" /></svg>
      </Abs>
      <T x={16} y={687} w={343} size={20} lh={24} title>Mobile Security & Private VPN</T>
      <T x={16} y={727} w={343} size={14} lh={20} color={C.grey}>Complete protection for your device, personal data, online privacy, and digital life.</T>
    </>
  );
}

const ONB = [
  { id: "onb2", node: "5:2557", shape: shape2, img: img2, imgY: 182, dot: 0, title: "Your iPhone,\nProtected",
    text: "Run a full security scan to keep your iPhone safe, private, and running at its best.", cta: "Continue" },
  { id: "onb3", node: "5:2683", shape: shape3, img: img3, imgY: 181, dot: 1, title: "Private Internet\nAccess",
    text: "Browse securely with encrypted connections that keep your online activity truly private.", cta: "Continue" },
  { id: "onb4", node: "5:2747", shape: shape4, img: img4, imgY: 182, dot: 2, title: "Block Online\nThreats",
    text: "Block dangerous websites, hidden trackers, and phishing attacks before they reach you.", cta: "Continue" },
  { id: "onb5", node: "5:2815", shape: shape5, img: img5, imgY: 182, dot: 3, title: "Smart QR\nProtection",
    text: "Scan QR codes for malicious links before opening websites, files, or online content.", cta: "Continue" },
  { id: "onb6", node: "5:2874", shape: shape6, img: img6, imgY: 182, dot: 4, title: "Free Up\nMore Space",
    text: "Remove duplicate photos and large videos to free up space on your iPhone.", cta: "Get Started" },
];

function Onb1({ go }) {
  return (
    <>
      <Art src={imgOnb1} x={0.5} y={85} />
      <Art src={logoHeader} x={0} y={52} />
      <T x={32} y={443} w={311} size={32} lh={36} title>{"Protection\nMade Simple"}</T>
      <T x={32} y={531} w={311} size={14} lh={20} color={C.grey}>Run one quick check to see what needs attention across your privacy, browsing, and storage.</T>
      <Btn kind="third" y={623} onClick={() => go("onb2")}>I'm new here</Btn>
      <Btn y={695} onClick={() => go("signin")}>I already have an account</Btn>
      <Dots y={767} active={0} />
    </>
  );
}

function OnbPage({ p, go, next }) {
  return (
    <>
      <Art src={p.shape} x={50} y={76} />
      <Art src={p.img} x={16} y={p.imgY} />
      <Art src={logoHeader} x={0} y={52} />
      <T x={32} y={535} w={311} size={32} lh={36} title>{p.title}</T>
      <T x={32} y={623} w={311} size={14} lh={20} color={C.grey}>{p.text}</T>
      <Btn y={695} onClick={() => go(next)}>{p.cta}</Btn>
      <Dots y={767} active={p.dot} />
    </>
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
        background: on ? C.white : a(C.blue, .15), border: `1px solid ${on ? C.blue : a(C.blue, .15)}`, transition: "background .15s, border-color .15s" }}>
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
      <Art src={imgPw} x={93.75} y={49.75} />
      <Art src={pwHeader} x={0} y={52} />
      {/* хіт-зони поверх SVG шапки: хрестик і Restore */}
      <Abs x={8} y={64} w={40} h={40} className="tap" role="button" aria-label="Close" onClick={() => go("dashboard")} style={{ borderRadius: 20 }} />
      <Abs x={306} y={66} w={61} h={36} className="tap" role="button" onClick={() => toast("Restoring purchases… No active subscription found on this Google account.")} />
      <T x={16} y={227} w={343} size={32} lh={36} title><span style={{ color: C.blue }}>One App.</span>{"\nFewer Digital Worries."}</T>
      <T x={16} y={315} w={343} size={12} lh={16}>Unlock every Seciro tool and keep your connection, browsing, links, and storage under control</T>
      <Carousel>{FEATURES.map((f) => <FeatureCard key={f.title} f={f} />)}</Carousel>
      <Abs x={16} y={459} w={343} h={114} style={{ display: "flex", gap: 8 }} role="radiogroup">
        {PLANS.map((x) => <PlanCard key={x.id} p={x} on={x.id === plan} onClick={() => setPlan(x.id)} />)}
      </Abs>
      <T x={0} y={589} w={375} size={14} lh={18}>Auto-Renewable, Cancel Anytime</T>
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

// ── Карта екранів (порядок = рядами Figma) ──
const ONB_FRAMES = [fr03, fr04, fr05, fr06, fr07];
// Кадр paywall у Figma — свій на кожен обраний тариф
const PW_FRAMES = { weekly: [fr08, "5:2948"], monthly: [fr09, "5:3055"], yearly: [fr10, "5:3162"] };
const SCREENS = [
  { id: "splash", name: "Splash", node: "5:2530", frame: fr01 },
  { id: "onb1", name: "Onboarding · intro", node: "5:2620", frame: fr02 },
  ...ONB.map((p, i) => ({ id: p.id, name: `Onboarding · ${i + 1}/5`, node: p.node, frame: ONB_FRAMES[i] })),
  { id: "paywall", name: "Paywall", node: "5:2948", frame: fr08 },
  { id: "signin", name: "Sign in (next row)", node: "5:4374", stub: true },
  { id: "dashboard", name: "Dashboard (later)", node: "5:1686", stub: true },
];
const ORDER = SCREENS.map((s) => s.id);
const store = {
  get: (k) => { try { return localStorage.getItem(k); } catch { return null; } },
  set: (k, v) => { try { localStorage.setItem(k, v); } catch {} },
};

function useScale() {
  // Два телефони (прототип + Figma) і панель: на широкому — в ряд, на вузькому — один під одним
  const calc = () => {
    const narrow = window.innerWidth <= 1000;
    const w = narrow ? Math.min(window.innerWidth - 32, 420) : Math.min(375 * 1.15, (window.innerWidth - 260 - 2 * 28 - 40 - 2 * 20) / 2);
    const h = narrow ? Infinity : window.innerHeight - 48 - 20 - 30;
    return Math.min(w / 375, h / 812);
  };
  const [s, setS] = useState(calc);
  useEffect(() => { const f = () => setS(calc()); window.addEventListener("resize", f); return () => window.removeEventListener("resize", f); }, []);
  return s;
}

export default function Seciro() {
  // Екран з посилання (#paywall) важливіший за збережений
  const fromHash = () => { const h = (location.hash || "").slice(1); return ORDER.includes(h) ? h : null; };
  const saved = store.get("seciro.screen");
  const [screen, setScreen] = useState(fromHash() || (ORDER.includes(saved) ? saved : "splash"));
  useEffect(() => { const f = () => { const h = fromHash(); if (h) { setDir("fade"); setScreen(h); } }; window.addEventListener("hashchange", f); return () => window.removeEventListener("hashchange", f); }, []);
  const [dir, setDir] = useState("fade");
  const [plan, setPlan] = useState("weekly");
  const [msg, setMsg] = useState(null);
  const [hist, setHist] = useState([]);
  const scale = useScale();

  const go = (id, how) => {
    setDir(how || (ORDER.indexOf(id) >= ORDER.indexOf(screen) ? "fwd" : "back"));
    setHist((h) => [...h, screen]);
    setScreen(id); store.set("seciro.screen", id);
  };
  const back = () => { const prev = hist[hist.length - 1]; if (!prev) return; setHist((h) => h.slice(0, -1)); setDir("back"); setScreen(prev); store.set("seciro.screen", prev); };
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
  const onb = ONB.find((p) => p.id === screen);
  const nextOf = (id) => ORDER[ORDER.indexOf(id) + 1];
  const [frame, frameNode] = screen === "paywall" ? PW_FRAMES[plan] : [cur.frame, cur.node];
  const [overlay, setOverlay] = useState(0);   // 0 — вимкнено, інакше прозорість кадру Figma поверх телефона
  const slotW = 375 * scale + (window.innerWidth > 1000 ? 20 : 0);

  return (
    <div className="stage">
      <div className="phone-slot" style={{ width: slotW }}>
        <div className="cap"><b>Prototype</b><span>click like in the app</span></div>
        <div className="phone-shell">
          <div className="phone-clip" style={{ width: 375 * scale, height: 812 * scale }}>
            <div className="app" style={{ width: 375, height: 812, position: "absolute", left: 0, top: 0, transform: `scale(${scale})`, transformOrigin: "0 0", background: C.bg }}>
              <div key={screen} className={`enter-${dir}`} style={{ position: "absolute", inset: 0 }}>
                {screen === "splash" && <Splash go={(id) => go(id, "fade")} />}
                {screen === "onb1" && <Onb1 go={go} />}
                {onb && <OnbPage p={onb} go={go} next={onb.id === "onb6" ? "paywall" : nextOf(onb.id)} />}
                {screen === "paywall" && <Paywall go={go} toast={toast} plan={plan} setPlan={setPlan} />}
                {screen === "signin" && <Next title="Sign in" note="Next row of the Figma: email sign-in, 6-digit code, subscription ended, no subscription on this email." back={back} />}
                {screen === "dashboard" && <Next title="Dashboard" note="Comes after the sign-in row: home screen with the security scan and every tool." back={back} />}
              </div>
              <StatusBar />
              <GestureBar />
              {msg && (
                <Abs x={24} y={84} w={327} style={{ zIndex: 30, background: "rgba(20,26,36,.92)", color: "#fff", borderRadius: 14, padding: "12px 16px",
                  fontSize: 13, lineHeight: "18px" }} className="enter-fade">{msg}</Abs>
              )}
            </div>
            {overlay > 0 && frame && (
              <img src={frame} alt="" draggable={false} style={{ position: "absolute", inset: 0, width: "100%", height: "100%", opacity: overlay, pointerEvents: "none", zIndex: 40 }} />
            )}
          </div>
        </div>
      </div>

      <div className="phone-slot" style={{ width: slotW }}>
        <div className="cap"><b>Figma</b><span>{frame ? `frame ${frameNode}` : "no frame yet"}</span></div>
        <div className="phone-shell">
          <div className="phone-clip" style={{ width: 375 * scale, height: 812 * scale, background: C.bg }}>
            {frame
              ? <img src={frame} alt={`Figma frame ${frameNode}`} draggable={false} style={{ display: "block", width: "100%", height: "100%" }} />
              : <div className="noframe">This screen comes from a later row of the Figma.</div>}
          </div>
        </div>
      </div>

      <aside className="panel" aria-label="Prototype screens">
        <div style={{ display: "flex", flexDirection: "column", gap: 4 }}>
          <h1>Seciro · prototype</h1>
          <p className="sub">Row 1 of the Figma: splash → onboarding → paywall. Click inside the phone like in the app.</p>
        </div>
        <div className="label">Screens</div>
        <ul className="scr-list">
          {SCREENS.map((s) => (
            <li key={s.id}>
              <button aria-current={s.id === screen} onClick={() => go(s.id)}>
                <span>{s.name}</span><span className="nid">{s.node}</span>
              </button>
            </li>
          ))}
        </ul>
        <div className="row">
          <button className="pbtn" onClick={back} disabled={!hist.length}>← Back</button>
          <button className="pbtn" onClick={() => { setHist([]); setPlan("weekly"); go("splash", "fade"); }}>Restart</button>
        </div>
        <div className="label">Compare</div>
        <div className="row" role="radiogroup" aria-label="Figma overlay on the prototype">
          {[[0, "Off"], [0.5, "Overlay 50%"], [1, "Figma only"]].map(([v, t]) => (
            <button key={t} className="pbtn" aria-pressed={overlay === v} onClick={() => setOverlay(v)}>{t}</button>
          ))}
        </div>
        <label className="note" htmlFor="ovl" style={{ display: "flex", alignItems: "center", gap: 8 }}>
          <span>Overlay</span>
          <input id="ovl" type="range" min="0" max="1" step="0.05" value={overlay} onChange={(e) => setOverlay(+e.target.value)} style={{ flex: 1, minWidth: 0 }} />
          <span style={{ fontVariantNumeric: "tabular-nums", width: 34, textAlign: "right" }}>{Math.round(overlay * 100)}%</span>
        </label>
        <p className="note"><b>Now:</b> {cur.name} · Figma {frameNode}{screen === "paywall" ? ` · plan ${plan}` : ""}</p>
        <p className="note">Overlay puts the Figma frame on top of the prototype: any shift shows as a double edge. The status bar differs on purpose (Figma has iOS, Android draws its own). ← → keys step through screens. Build {BUILD}.</p>
      </aside>
    </div>
  );
}
