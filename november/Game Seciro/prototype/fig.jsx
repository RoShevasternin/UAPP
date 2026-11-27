// ─────────────────────────────────────────────────────────────────────────────
// Рендерер кадрів Figma (з ряду 3): малює екран прямо з дерева шарів claude/figma/r<N>/<кадр>.json.
//  • прямокутники, картки, рамки, тіні, градієнти, тексти — кодом, числа один в один з Figma;
//  • усе векторне (іконки, щити, ілюстрації) — PNG @3x, відрендерений самою Figma (поле png, позиція rx/ry);
//  • поведінку додає екран через overrides: {[id або ім'я шару]: false | {onClick, className, style, children} | (node, el) => el}.
// Координати: ax/ay — від лівого верхнього кута кадру; дитина ставиться як (ax − ax батька).
// ─────────────────────────────────────────────────────────────────────────────
import { FRAMES, PNG } from "./.tools/fig.gen.js";

export const frameOf = (id) => FRAMES[id];
export { PNG };

const rgba = (hex, a = 1) => {
  const n = parseInt(hex.slice(1), 16);
  return `rgba(${(n >> 16) & 255},${(n >> 8) & 255},${n & 255},${+a.toFixed(3)})`;
};

// Лінійний градієнт Figma → CSS: точки ручок із gradientTransform, проєкція на лінію CSS-градієнта
function linear(p, w, h) {
  const [[a, b, c], [d, e, f]] = p.t;
  const det = a * e - b * d;
  const inv = (x, y) => [(e * (x - c) - b * (y - f)) / det, (-d * (x - c) + a * (y - f)) / det];
  const [sx, sy] = inv(0, 0.5), [ex, ey] = inv(1, 0.5);
  const dx = (ex - sx) * w, dy = (ey - sy) * h;
  const ang = Math.atan2(dx, -dy);
  const ux = Math.sin(ang), uy = -Math.cos(ang);
  const L = Math.abs(w * ux) + Math.abs(h * uy);
  const tOf = (x, y) => ((x * w - w / 2) * ux + (y * h - h / 2) * uy) / L + 0.5;
  const t0 = tOf(sx, sy), t1 = tOf(ex, ey);
  const stops = p.stops.map(([hex, al, pos]) => `${rgba(hex, al * (p.a ?? 1))} ${((t0 + pos * (t1 - t0)) * 100).toFixed(2)}%`);
  return `linear-gradient(${(ang * 180 / Math.PI).toFixed(2)}deg, ${stops.join(", ")})`;
}

const background = (fills, w, h) => {
  if (!fills || !fills.length) return undefined;
  // у Figma верхня заливка — остання; у CSS — перша
  return [...fills].reverse().map((p) => p.solid ? `linear-gradient(${rgba(p.solid, p.a)}, ${rgba(p.solid, p.a)})`
    : p.grad === "GRADIENT_LINEAR" ? linear(p, w, h)
    : p.grad === "GRADIENT_RADIAL" ? `radial-gradient(${p.stops.map(([hx, al, pos]) => `${rgba(hx, al)} ${pos * 100}%`).join(", ")})` : "transparent").join(", ");
};

const shadows = (n) => {
  const out = [];
  for (const e of n.fx || []) {
    if (e.type === "DROP_SHADOW") out.push(`${e.o.x}px ${e.o.y}px ${e.r}px ${e.s || 0}px ${rgba(e.c[0], e.c[1])}`);
    if (e.type === "INNER_SHADOW") out.push(`inset ${e.o.x}px ${e.o.y}px ${e.r}px ${e.s || 0}px ${rgba(e.c[0], e.c[1])}`);
  }
  // рамка: INSIDE — inset (не зсуває вміст, як у Figma), CENTER — половина всередину й назовні, OUTSIDE — назовні
  const s = n.strokes && n.strokes[0];
  if (s && s.solid) {
    const col = rgba(s.solid, s.a);
    if (Array.isArray(n.sw)) {
      const [t, r, b, l] = n.sw;
      if (t) out.push(`inset 0 ${t}px 0 0 ${col}`); if (b) out.push(`inset 0 -${b}px 0 0 ${col}`);
      if (l) out.push(`inset ${l}px 0 0 0 ${col}`); if (r) out.push(`inset -${r}px 0 0 0 ${col}`);
    } else if (n.sw) {
      if (n.sa === "OUTSIDE") out.push(`0 0 0 ${n.sw}px ${col}`);
      else if (n.sa === "CENTER") out.push(`inset 0 0 0 ${n.sw / 2}px ${col}`, `0 0 0 ${n.sw / 2}px ${col}`);
      else out.push(`inset 0 0 0 ${n.sw}px ${col}`);
    }
  }
  return out.length ? out.join(", ") : undefined;
};

const blur = (n) => {
  let back = null, layer = null;
  for (const e of n.fx || []) {
    if (e.type === "BACKGROUND_BLUR") back = `blur(${e.r / 2}px)`;
    if (e.type === "GLASS") back = `blur(${e.r}px) saturate(1.5)`;
    if (e.type === "LAYER_BLUR") layer = `blur(${e.r / 2}px)`;
  }
  return { back, layer };
};

const WEIGHT = { Thin: 100, Light: 300, Regular: 400, Medium: 500, "Semi Bold": 600, SemiBold: 600, Bold: 700, "Extra Bold": 800, Heavy: 800, Black: 900 };
// SF Pro (шрифт Apple) на Android не можна → Inter Tight (найближчий; див. claude/README.md)
const family = (f) => f.startsWith("SF Pro") ? '"Inter Tight", "Inter", sans-serif' : `"${f}", "Inter", sans-serif`;
const CASE = { UPPER: "uppercase", LOWER: "lowercase", TITLE: "capitalize" };

// Апка під Android: назви з iOS-макета, що вже мають Android-відповідник (рішення VELDAN 24.11: Safari → Google Chrome)
const SUBST = [[/Safari/g, "Chrome"]];
const subst = (t) => SUBST.reduce((s, [a, b]) => s.replace(a, b), t);

function Text({ n, style }) {
  const t = { ...n.t, runs: n.t.runs.map((r) => ({ ...r, t: subst(r.t) })) }, r0 = t.runs[0];
  const lh = r0.lh ? `${r0.lh}px` : "normal";
  const wrap = t.auto === "WIDTH_AND_HEIGHT" ? "pre" : "pre-line";
  const valign = t.valign === "CENTER" ? "center" : t.valign === "BOTTOM" ? "flex-end" : "flex-start";
  // рамка нижча за рядок = у Figma увімкнено leading trim (від висоти великої літери до базової лінії) — той самий трим у CSS
  const trim = r0.lh && n.h < r0.lh - 0.5 ? { textBox: "trim-both cap alphabetic" } : {};
  return (
    <div style={{ ...style, display: "flex", flexDirection: "column", justifyContent: valign }}>
      <div style={{ ...trim, textAlign: t.align === "JUSTIFIED" ? "justify" : t.align.toLowerCase(), whiteSpace: wrap, lineHeight: lh,
        fontFamily: family(r0.f), fontWeight: WEIGHT[r0.st] || 400, fontSize: r0.sz }}>
        {t.runs.map((r, i) => {
          const c = r.c && r.c[0];
          return (
            <span key={i} style={{ fontFamily: family(r.f), fontWeight: WEIGHT[r.st] || 400, fontSize: r.sz, letterSpacing: r.ls ? `${r.ls}px` : undefined,
              lineHeight: r.lh ? `${r.lh}px` : undefined, color: c && c.solid ? rgba(c.solid, c.a) : undefined, textTransform: CASE[r.cs],
              ...(r.d === "UNDERLINE" ? { textDecoration: "underline", textDecorationThickness: "0.5px", textUnderlineOffset: `${(r.sz * 0.214).toFixed(1)}px`, textDecorationSkipInk: "auto" } : {}),
              ...(r.d === "STRIKETHROUGH" ? { textDecoration: "line-through" } : {}) }}>{r.t}</span>
          );
        })}
      </div>
    </div>
  );
}

const SKIP = (n) => n.name === "system bar" || (n.name === "Home" && n.type === "RECTANGLE");

export function FigNode({ n, px = 0, py = 0, ov = {}, raw }) {
  if (!n || n.hidden || SKIP(n)) return null;
  const o = ov[n.id] ?? ov[n.name];
  if (o === false) return null;
  const pos = { position: "absolute", left: n.ax - px, top: n.ay - py, width: n.w, height: n.h };
  let el;
  if (n.png && n.tint) {
    // обрізана іконка Material: форма з компонента, колір — з екземпляра (маска)
    const u = `url("${PNG[n.png]}")`;
    el = <div style={{ position: "absolute", left: n.rx - px, top: n.ry - py, width: n.rw, height: n.rh, opacity: (n.op ?? 1) * (n.tintA ?? 1), pointerEvents: "none",
      background: n.tint, WebkitMaskImage: u, maskImage: u, WebkitMaskSize: "100% 100%", maskSize: "100% 100%" }} />;
  } else if (n.png) {
    el = <img src={PNG[n.png]} alt="" draggable={false}
      style={{ position: "absolute", left: n.rx - px, top: n.ry - py, width: n.rw, height: n.rh, opacity: n.op, pointerEvents: "none" }} />;
  } else if (n.t) {
    el = <Text n={n} style={{ ...pos, opacity: n.op }} />;
  } else if (n.arc && n.strokes && n.strokes[0] && n.strokes[0].solid) {
    // дуга (шкала): кути Figma — від endingAngle до startingAngle за годинниковою (y донизу), рамка всередину.
    // Повернутий шар (rot): у JSON ax/ay — кут його обвідної рамки; центр = середина обвідної, поворот — навколо центру.
    // m — лінійна частина relativeTransform Figma [a, b, c, d] (буває з віддзеркаленням, det −1) — точніша за rot.
    // Поза режимом звірки дуга «домальовується» при появі (клас arc-draw; .raw вимикає анімації).
    const sw = n.sw || 1, rr = n.w / 2 - sw / 2, cx = n.w / 2, cy = n.h / 2;
    const [s0, e0] = n.arc, a0 = e0, a1 = s0 > e0 ? s0 : s0 + 2 * Math.PI;
    const p = (a) => `${(cx + rr * Math.cos(a)).toFixed(3)} ${(cy + rr * Math.sin(a)).toFixed(3)}`;
    const t = Math.abs(((n.rot || 0) * Math.PI) / 180), bw = n.w * Math.abs(Math.cos(t)) + n.h * Math.abs(Math.sin(t)), bh = n.w * Math.abs(Math.sin(t)) + n.h * Math.abs(Math.cos(t));
    const left = n.rot ? n.ax - px + bw / 2 - n.w / 2 : n.ax - px, top = n.rot ? n.ay - py + bh / 2 - n.h / 2 : n.ay - py;
    el = (
      <svg style={{ position: "absolute", left, top, width: n.w, height: n.h, overflow: "visible", opacity: n.op, pointerEvents: "none",
        transform: n.m ? `matrix(${n.m.join(",")},0,0)` : n.rot ? `rotate(${-n.rot}deg)` : undefined }} viewBox={`0 0 ${n.w} ${n.h}`}>
        <path d={`M ${p(a0)} A ${rr} ${rr} 0 ${a1 - a0 > Math.PI ? 1 : 0} 1 ${p(a1)}`} fill="none" stroke={rgba(n.strokes[0].solid, n.strokes[0].a)}
          strokeWidth={sw} strokeLinecap={n.cap === "ROUND" ? "round" : "butt"} pathLength="1" className="arc-draw" />
      </svg>
    );
  } else {
    const { back, layer } = blur(n);
    const r = Array.isArray(n.r) ? n.r.map((v) => `${v}px`).join(" ") : n.r ? `${n.r}px` : undefined;
    const scroll = n.name === "scroll";
    el = (
      <div data-fig={n.name} style={{ ...pos, background: background(n.fills, n.w, n.h), borderRadius: r, boxShadow: shadows(n), opacity: n.op,
        overflow: scroll ? "hidden auto" : n.clip ? "hidden" : undefined, backdropFilter: back || undefined, WebkitBackdropFilter: back || undefined,
        filter: layer || undefined }} className={scroll ? "fig-scroll" : undefined}>
        {scroll
          ? <div style={{ position: "relative", height: Math.max(...(n.children || []).map((c) => c.ay - n.ay + c.h), n.h) }}>
              {(n.children || []).map((c) => <FigNode key={c.id} n={c} px={n.ax} py={n.ay} ov={ov} raw={raw} />)}
            </div>
          : (n.children || []).map((c) => <FigNode key={c.id} n={c} px={n.ax} py={n.ay} ov={ov} raw={raw} />)}
      </div>
    );
  }
  if (typeof o === "function") return o(n, el, pos);
  if (o && typeof o === "object") {
    const { onClick, className, style, children } = o;
    return (
      <div onClick={onClick} className={className} role={onClick ? "button" : undefined}
        style={{ position: "absolute", left: n.ax - px, top: n.ay - py, width: n.w, height: n.h, cursor: onClick ? "pointer" : undefined, ...style }}>
        {/* вміст шару — у власних координатах обгортки */}
        <div style={{ position: "absolute", left: -(n.ax - px), top: -(n.ay - py), width: 0, height: 0 }}>{el}</div>
        {children}
      </div>
    );
  }
  return el;
}

// Цілий кадр: корінь 375×812 (тло кадру) + діти
export function FigFrame({ id, ov, raw, children }) {
  const n = FRAMES[id];
  return (
    <div style={{ position: "absolute", inset: 0, background: background(n.fills, n.w, n.h) }}>
      {(n.children || []).map((c) => <FigNode key={c.id} n={c} px={0} py={0} ov={ov} raw={raw} />)}
      {children}
    </div>
  );
}

// Пошук шару в кадрі (для перенесення аркушів між кадрами тощо)
export function findNode(n, pred) {
  if (!n) return null;
  if (pred(n)) return n;
  for (const c of n.children || []) { const r = findNode(c, pred); if (r) return r; }
  return null;
}

// Текст із заміненим вмістом у стилі шару Figma (таймер, швидкості…): той самий шрифт/кегль/колір, без переносу,
// рамка розширена симетрично до вирівнювання, щоб довший рядок не переносився й не зсувався
export function FigText({ n, px = 0, py = 0, text, style }) {
  const r0 = n.t.runs[0], pad = 80, al = n.t.align;
  const left = n.ax - px - (al === "CENTER" ? pad : al === "RIGHT" ? 2 * pad : 0);
  const node = { ...n, t: { ...n.t, auto: "WIDTH_AND_HEIGHT", runs: [{ ...r0, t: text }] } };
  return <Text n={node} style={{ position: "absolute", left, top: n.ay - py, width: n.w + (al === "LEFT" ? 2 * pad : 2 * pad), height: n.h,
    fontVariantNumeric: "tabular-nums", ...style }} />;
}
