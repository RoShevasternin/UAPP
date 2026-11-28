// ─────────────────────────────────────────────────────────────────────────────
// Рамки нотаток і картки нотаток (з 28.11.2026, build 27).
//
// Рамка прив'язана до того, що реально видно на екрані прототипу, а не до координат:
//   at:  "текст" | ["текст", …]  — елемент(и) з цим текстом (найглибший, видимий); кілька — об'єднуються
//   row: true | висота           — розширити до рядка списку (іконка + назва + підпис + шеврон); число — до картки такої висоти
//   grow: [ліво, верх, право, низ] — дорізати рамку в px кадру (наприклад, захопити іконку зліва)
//   box: [x, y, w, h]            — запасний варіант: фіксовані координати кадру 375×812 (ілюстрації, клавіатура)
// Позиція рахується щокадру → рамка йде за скролом і анімаціями; що прокручено з поля зору, обрізається/ховається.
// Натиснули нотатку → екран сам прокручується до її місця.
// ─────────────────────────────────────────────────────────────────────────────
import { useEffect, useRef, useState } from "react";
import { KINDS, examplesOf } from "./notes.js";

const norm = (s) => (s || "").replace(/[\s\u00a0\u2028]+/g, " ").trim();
const visible = (el) => !el.checkVisibility || el.checkVisibility({ opacityProperty: true, visibilityProperty: true });

// найглибший елемент, чий текст містить рядок (рядок може йти через кілька <span> одного абзацу).
// Перевага — тому, що зараз видно на екрані; далі — прокрученому за край списку (до нього можна докрутити);
// схованих аркушів за межами екрана не беремо.
export function findText(root, t) {
  const want = norm(t);
  if (!root || !want) return null;
  let later = null;
  for (const el of root.querySelectorAll("*")) {
    if (!norm(el.textContent).includes(want)) continue;
    if ([...el.children].some((c) => norm(c.textContent).includes(want))) continue;
    if (!visible(el)) continue;
    const r = el.getBoundingClientRect();
    if (r.width < 1 || r.height < 1) continue;
    if (clipped(el, root.parentElement)) return el;
    if (!later && scrollerOf(el, root.parentElement)) later = el;
  }
  return later;
}
// від тексту вгору до контейнера рядка: поки предок не вищий за рядок і не ширший за картку
function rowUp(el, root, s, maxH = 100) {
  let cur = el;
  for (let p = el.parentElement; p && p !== root; p = p.parentElement) {
    const r = p.getBoundingClientRect();
    if (r.height > maxH * s || r.width > 352 * s) break;
    cur = p;
  }
  return cur;
}
export function anchorEls(n, root, s) {
  if (!n.at || !root) return [];
  const list = Array.isArray(n.at) ? n.at : [n.at];
  return list.map((t) => { const el = findText(root, t); return el && n.row ? rowUp(el, root, s, typeof n.row === "number" ? n.row : 100) : el; }).filter(Boolean);
}
// видима частина елемента: перетин з усіма предками, що обрізають вміст (скрол, clip)
function clipped(el, stop) {
  let r = el.getBoundingClientRect(), x0 = r.left, y0 = r.top, x1 = r.right, y1 = r.bottom;
  for (let p = el.parentElement; p && p !== stop; p = p.parentElement) {
    const cs = getComputedStyle(p);
    if (cs.overflowX === "visible" && cs.overflowY === "visible") continue;
    const q = p.getBoundingClientRect();
    x0 = Math.max(x0, q.left); y0 = Math.max(y0, q.top); x1 = Math.min(x1, q.right); y1 = Math.min(y1, q.bottom);
  }
  return x1 - x0 > 2 && y1 - y0 > 2 ? { x0, y0, x1, y1 } : null;
}
const scrollerOf = (el, stop) => {
  for (let p = el.parentElement; p && p !== stop; p = p.parentElement) {
    const cs = getComputedStyle(p);
    if (/(auto|scroll)/.test(cs.overflowY) && p.scrollHeight > p.clientHeight + 2) return p;
  }
  return null;
};

export function Marks({ notes, hl, scale, rootRef, appRef, screen }) {
  const [rects, setRects] = useState([]);
  const els = useRef([]);
  useEffect(() => {
    let raf, last = -1e9, prev = "", alive = true;
    const measure = () => {
      const root = rootRef.current, app = appRef.current;
      if (!root || !app) return;
      const t = performance.now();
      if (t - last > 400) { els.current = notes.map((n) => anchorEls(n, root, scale)); last = t; }
      const a = app.getBoundingClientRect();
      const out = notes.map((n, i) => {
        if (n.at) {
          const parts = (els.current[i] || []).filter((e) => e.isConnected).map((e) => clipped(e, app)).filter(Boolean);
          if (!parts.length) return null;
          const g = n.grow || [0, 0, 0, 0];
          const x0 = Math.min(...parts.map((p) => p.x0)), y0 = Math.min(...parts.map((p) => p.y0));
          const x1 = Math.max(...parts.map((p) => p.x1)), y1 = Math.max(...parts.map((p) => p.y1));
          return [(x0 - a.left) / scale - g[0], (y0 - a.top) / scale - g[1], (x1 - x0) / scale + g[0] + g[2], (y1 - y0) / scale + g[1] + g[3]].map((v) => Math.round(v));
        }
        return n.box || null;
      });
      const key = JSON.stringify(out);
      if (key !== prev) { prev = key; setRects(out); }
    };
    // щокадру (плавно за анімацією й скролом) + запасний таймер (коли вкладка у фоні або кадри не йдуть) + одразу при скролі
    const loop = () => { if (!alive) return; measure(); raf = requestAnimationFrame(loop); };
    raf = requestAnimationFrame(loop);
    const iv = setInterval(measure, 200);
    const onScroll = () => measure();
    document.addEventListener("scroll", onScroll, true);
    measure();
    return () => { alive = false; cancelAnimationFrame(raf); clearInterval(iv); document.removeEventListener("scroll", onScroll, true); };
  }, [notes, scale, screen]);
  // натиснули нотатку — прокрутити екран так, щоб її місце було посередині
  useEffect(() => {
    if (hl === null || !notes[hl] || !notes[hl].at) return;
    const root = rootRef.current, el = anchorEls(notes[hl], root, scale)[0];
    const sc = el && scrollerOf(el, root.parentElement);
    if (!sc) return;
    const er = el.getBoundingClientRect(), sr = sc.getBoundingClientRect();
    const dy = (er.top + er.height / 2 - (sr.top + sr.height / 2)) / scale;
    if (Math.abs(dy) > 20) sc.scrollTo({ top: sc.scrollTop + dy, behavior: "smooth" });
  }, [hl]);
  return (
    <div style={{ position: "absolute", inset: 0, zIndex: 35, pointerEvents: "none" }}>
      {notes.map((n, i) => { const b = rects[i]; return b && (
        <div key={i} className="mark" style={{ left: b[0] - 6, top: b[1] - 5, width: b[2] + 12, height: b[3] + 10,
          borderColor: KINDS[n.kind].color, background: hl === i ? KINDS[n.kind].color + "2e" : "transparent", opacity: hl === null || hl === i ? 1 : 0.3 }}>
          <span style={{ background: KINDS[n.kind].color }}>{i + 1}</span>
        </div>); })}
    </div>
  );
}

// ── копіювання: кнопка біля кожного рядка тексту для дизайнера ──
function copyText(t, done) {
  const fallback = () => {
    const ta = document.createElement("textarea"); ta.value = t; ta.style.position = "fixed"; ta.style.opacity = "0";
    document.body.appendChild(ta); ta.select();
    try { document.execCommand("copy"); done(true); } catch (e) { done(false); }
    ta.remove();
  };
  try { navigator.clipboard.writeText(t).then(() => done(true), fallback); } catch (e) { fallback(); }
}
function CopyBtn({ text, small }) {
  const [st, setSt] = useState(null);
  return (
    <button className={"cp-btn" + (small ? " sm" : "")} title="Копіювати" aria-label={"Копіювати: " + text}
      onClick={(e) => { e.stopPropagation(); copyText(text, (ok) => { setSt(ok ? "✓" : "✕"); setTimeout(() => setSt(null), 1400); }); }}>
      {st || (small ? "⧉" : "Копіювати")}</button>
  );
}
// один текст — рядок з кнопкою; блок з кількох рядків (заголовок / текст / кнопки) — кнопка на кожен рядок і на весь блок
function CopyLine({ label, text }) {
  const lines = text.split("\n");
  return (
    <div className="cp-item">
      <div className="cp-head">{label && <span className="cp-lab">{label}</span>}{lines.length > 1 && <CopyBtn text={text} />}</div>
      {lines.length > 1
        ? lines.map((l, i) => <div key={i} className="cp-line"><code className="cp-txt">{l}</code><CopyBtn text={l} small /></div>)
        : <div className="cp-line"><code className="cp-txt">{text}</code><CopyBtn text={text} /></div>}
    </div>
  );
}

// ── картка нотатки ──
//   t     — заголовок: що це за місце
//   now   — що в макеті (iPhone), дослівно
//   why   — чому на Android так не можна / що не так
//   fix   — як має бути (рішення; для питання — наша пропозиція)
//   opts  — варіанти для Віталія: [["A", "текст", true /* пропонуємо */], …]
//   copy  — тексти для дизайнера (EN): [["Назва", "текст"], …] або ["текст", …]
//   text  — короткий вільний текст (для «Так і буде»)
export function NoteBody({ n, onEx, exOn }) {
  const copies = (n.copy || []).map((c) => (Array.isArray(c) ? c : [null, c]));
  return (
    <div className="nb">
      <span className="kind" style={{ color: KINDS[n.kind].color }}>{KINDS[n.kind].label}</span>
      {n.t && <b className="nb-t">{n.t}</b>}
      {n.text && <p className="nb-p">{n.text}</p>}
      {n.now && <p className="nb-p"><span className="nb-k">У макеті</span>{n.now}</p>}
      {n.why && <p className="nb-p"><span className="nb-k">{n.kind === "ask" ? "Чому питаємо" : "Чому інакше"}</span>{n.why}</p>}
      {n.fix && <p className="nb-p"><span className="nb-k">{n.kind === "ask" ? "Пропонуємо" : n.kind === "android" ? "Як буде" : "Що зробити"}</span>{n.fix}</p>}
      {n.opts && (
        <div className="nb-opts">
          {n.opts.map(([k, t, rec], j) => (
            <div key={j} className={"nb-opt" + (rec ? " rec" : "")}><b>{k}</b><span>{t}</span>{rec && <em>пропонуємо</em>}</div>))}
        </div>
      )}
      {copies.length > 0 && (
        <div className="nb-copy">
          <span className="nb-k">Текст для макета (EN) — копіюйте</span>
          {copies.map(([l, t], j) => <CopyLine key={j} label={l} text={t} />)}
          
        </div>
      )}
      {onEx && examplesOf(n).length > 0 && (
        <span className="ex-btns">{examplesOf(n).map(([src, cap], k) => (
          <button key={k} className="pbtn sm" aria-pressed={exOn(k)} onClick={(e) => { e.stopPropagation(); onEx(k); }}>
            Приклад{examplesOf(n).length > 1 ? ` ${"ABCDE"[k]}` : ""}: {cap} →</button>))}</span>
      )}
    </div>
  );
}
export const noteKey = (n) => (n.t || "") + "|" + (n.now || n.text || "");
