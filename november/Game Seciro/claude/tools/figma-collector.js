// ─────────────────────────────────────────────────────────────────────────────
// Збирач кадрів Figma для рендерера прототипу Seciro (v8, 26.11.2026: + дуги ELLIPSE). БЕЗ Figma MCP.
// Як запускати: вбудований браузер → вкладка файлу Figma (залогінений) → javascript_tool:
//   1) вставити весь цей файл (визначає window.__tar / __save / __row7);
//   2) await window.__row7(['5:xxxx', …], 'r5', 'r5')  → у ~/Downloads падає seciro_r5_v7.tar
//      (якщо панель браузера схована — файл лишається як ~/Downloads/.Q6L2SF6YDW.com.anthropic.claudefordesktop.*)
//   3) tar -xf … -C claude/figma  →  python3 claude/tools/fig-post.py r5  →  python3 prototype/build.py
// Що робить: дерево шарів (ax/ay від кадру, заливки, рамки, тіні, тексти з runs); векторне → PNG @3x (ім'я = хеш).
// Обрізані кадром шари (низ скролу, сховані аркуші) Figma не експортує як слід → двійник за підписом серед видимих
// шарів ряду; інакше головний компонент у масштабі (іконка Material — маска + колір екземпляра, поле tint).
// Якщо 'figma' «закрився» (plugin has already been closed) — перезавантажити вкладку й вставити файл знову.
// ─────────────────────────────────────────────────────────────────────────────
window.__tar = (files) => {
    const enc = new TextEncoder(); const blocks = [];
    const oct = (n, len) => n.toString(8).padStart(len - 1, '0') + '\0';
    for (const f of files) {
      const h = new Uint8Array(512); const put = (s, off) => h.set(enc.encode(s), off);
      put(f.name.slice(0, 99), 0); put('0000644\0', 100); put('0000000\0', 108); put('0000000\0', 116);
      put(oct(f.data.length, 12), 124); put(oct(Math.floor(Date.now()/1000), 12), 136);
      put('        ', 148); put('0', 156); put('ustar\0', 257); put('00', 263);
      let sum = 0; for (const b of h) sum += b; put(sum.toString(8).padStart(6, '0') + '\0 ', 148);
      blocks.push(h, f.data); const pad = (512 - f.data.length % 512) % 512; if (pad) blocks.push(new Uint8Array(pad));
    }
    blocks.push(new Uint8Array(1024)); return new Blob(blocks, {type: 'application/x-tar'});
  };

window.__save = (blob, name) => { const url = URL.createObjectURL(blob); const a = document.createElement('a'); a.href = url; a.download = name; document.body.appendChild(a); a.click(); a.remove(); setTimeout(()=>URL.revokeObjectURL(url), 30000); };

window.__row7 = async (ids, dir, tag) => {
    const LS = String.fromCharCode(0x2028);
    const hex = c => '#' + [c.r,c.g,c.b].map(v => Math.round(v*255).toString(16).padStart(2,'0')).join('');
    const paint = p => p.visible === false ? null : p.type === 'SOLID' ? {solid: hex(p.color), a: +(p.opacity ?? 1).toFixed(3)}
      : p.type.startsWith('GRADIENT') ? {grad: p.type, stops: p.gradientStops.map(s => [hex(s.color), +s.color.a.toFixed(3), +s.position.toFixed(4)]), t: p.gradientTransform.map(r=>r.map(v=>+v.toFixed(5))), a: +(p.opacity ?? 1).toFixed(3)}
      : p.type === 'IMAGE' ? {image: p.imageHash, mode: p.scaleMode} : {other: p.type};
    const paints = ps => Array.isArray(ps) ? ps.map(paint).filter(Boolean) : undefined;
    const eff = e => e.visible ? {type: e.type, r: e.radius, c: e.color ? [hex(e.color), +e.color.a.toFixed(3)] : undefined, o: e.offset, s: e.spread} : null;
    const VEC = new Set(['VECTOR','BOOLEAN_OPERATION','STAR','LINE','ELLIPSE','POLYGON']);
    const hasText = n => n.type === 'TEXT' || ('children' in n && n.children.some(c => c.visible && hasText(c)));
    const hasRot = n => !!n.rotation || ('children' in n && n.children.some(c => c.visible && hasRot(c)));
    const hasVec = n => VEC.has(n.type) || n.type === 'INSTANCE' || hasRot(n) || ('children' in n && n.children.some(c => c.visible && hasVec(c))) || ('fills' in n && Array.isArray(n.fills) && n.fills.some(f => f.type === 'IMAGE' && f.visible !== false));
    const clipped = n => { const r = n.absoluteRenderBounds; if (!r) return true; const b = n.absoluteBoundingBox;
      if (r.width < b.width - 0.5 || r.height < b.height - 0.5) return true;
      for (let p = n.parent; p && p.type !== 'PAGE' && p.type !== 'DOCUMENT'; p = p.parent) if (p.clipsContent && p.absoluteBoundingBox) { const q = p.absoluteBoundingBox;
        if (b.x < q.x - 0.5 || b.y < q.y - 0.5 || b.x + b.width > q.x + q.width + 0.5 || b.y + b.height > q.y + q.height + 0.5) return true; }
      return false; };
    const sig = n => JSON.stringify([n.type, Math.round(n.width), Math.round(n.height), n.name, 'fills' in n ? paints(n.fills) : 0, 'strokes' in n ? [paints(n.strokes), n.strokeWeight === figma.mixed ? 'm' : n.strokeWeight] : 0,
      'effects' in n ? n.effects.map(eff) : 0, 'cornerRadius' in n ? (n.cornerRadius === figma.mixed ? 'm' : n.cornerRadius) : 0, n.opacity, n.rotation || 0, n.mainComponent ? n.mainComponent.id : 0,
      'children' in n ? n.children.filter(c => c.visible).map(sig) : 0]);
    const files = [], seen = new Map(), bySig = new Map(); const stat = {node: 0, twin: 0, tint: 0, comp: 0, css: 0};
    const hash = b => { let h = 2166136261; for (let i = 0; i < b.length; i += 7) h = Math.imul(h ^ b[i], 16777619); return (h >>> 0).toString(36) + b.length.toString(36); };
    const put = bytes => { const k = hash(bytes); let file = seen.get(k); if (!file) { file = `${dir}/${k}.png`; seen.set(k, file); files.push({name: file, data: bytes}); } return file; };
    const frames = []; for (const id of ids) frames.push(await figma.getNodeByIdAsync(id));
    // Прохід 1: видимі (не обрізані) векторні шари → підпис → картинка + зсув рендеру
    for (const f of frames) for (const n of f.findAll(x => x.visible && !hasText(x) && hasVec(x))) {
      if (clipped(n)) continue; const s = sig(n); if (bySig.has(s)) continue;
      let bytes; try { bytes = await n.exportAsync({format: 'PNG', constraint: {type: 'SCALE', value: 3}}); } catch (e) { continue; }
      const r = n.absoluteRenderBounds, b = n.absoluteBoundingBox;
      bySig.set(s, {png: put(bytes), dx: r.x - b.x, dy: r.y - b.y, w: r.width, h: r.height});
    }
    let fb;
    const ser = async (n) => {
      const ab = n.absoluteBoundingBox;
      const o = {id: n.id, name: n.name, type: n.type, ax: +(ab.x - fb.x).toFixed(2), ay: +(ab.y - fb.y).toFixed(2), w: +n.width.toFixed(2), h: +n.height.toFixed(2)};
      if (n.visible === false) { o.hidden = true; return o; }
      if ('opacity' in n && n.opacity !== 1) o.op = +n.opacity.toFixed(3);
      if (n.rotation) o.rot = +n.rotation.toFixed(2);
      let mc = null;
      if (n.type === 'INSTANCE') { mc = await n.getMainComponentAsync(); o.comp = mc ? (mc.parent?.type === 'COMPONENT_SET' ? mc.parent.name + ' / ' + mc.name : mc.name) : null; }
      if (!hasText(n) && hasVec(n)) {
        const hit = bySig.get(sig(n));
        if (hit) { o.png = hit.png; o.rx = +(o.ax + hit.dx).toFixed(2); o.ry = +(o.ay + hit.dy).toFixed(2); o.rw = +hit.w.toFixed(2); o.rh = +hit.h.toFixed(2); if (clipped(n)) { o.fallback = 'twin'; stat.twin++; } else stat.node++; return o; }
        if (mc) {
          const mr = mc.absoluteRenderBounds, mb = mc.absoluteBoundingBox;
          if (mr) { let bytes = null; try { bytes = await mc.exportAsync({format: 'PNG', constraint: {type: 'SCALE', value: 3 * n.width / mc.width}}); } catch (e) {}
            if (bytes) { const sx = n.width / mc.width, sy = n.height / mc.height;
              o.png = put(bytes); o.rx = +(o.ax + (mr.x - mb.x) * sx).toFixed(2); o.ry = +(o.ay + (mr.y - mb.y) * sy).toFixed(2); o.rw = +(mr.width * sx).toFixed(2); o.rh = +(mr.height * sy).toFixed(2);
              // іконка Material з одним кольором → маска + колір з екземпляра
              const v = n.name.startsWith('material-symbols:') ? n.findOne(x => VEC.has(x.type) && x.visible && Array.isArray(x.fills) && x.fills[0] && x.fills[0].type === 'SOLID') : null;
              if (v) { o.tint = hex(v.fills[0].color); o.tintA = +(v.fills[0].opacity ?? 1).toFixed(3); stat.tint++; } else stat.comp++;
              o.fallback = 'comp'; return o; } }
        }
        stat.css++;
      }
      if ('fills' in n) { const f = paints(n.fills); if (f && f.length) o.fills = f; }
      if ('strokes' in n && n.strokes.length) { o.strokes = paints(n.strokes); o.sw = n.strokeWeight === figma.mixed ? [n.strokeTopWeight, n.strokeRightWeight, n.strokeBottomWeight, n.strokeLeftWeight] : n.strokeWeight; o.sa = n.strokeAlign; }
      if ('cornerRadius' in n) { const r = n.cornerRadius === figma.mixed ? [n.topLeftRadius, n.topRightRadius, n.bottomRightRadius, n.bottomLeftRadius] : n.cornerRadius; if (r) o.r = r; }
      if ('effects' in n && n.effects.length) { const fx = n.effects.map(eff).filter(Boolean); if (fx.length) o.fx = fx; }
      if ('clipsContent' in n && n.clipsContent) o.clip = true;
      if (n.type === 'ELLIPSE' && n.arcData && Math.abs(n.arcData.endingAngle - n.arcData.startingAngle) < 6.28) { o.arc = [+n.arcData.startingAngle.toFixed(5), +n.arcData.endingAngle.toFixed(5)]; o.cap = n.strokeCap; }  // дуга (шкала): кути, як у Figma (v8)
      if (n.type === 'TEXT') {
        const segs = n.getStyledTextSegments(['fontName','fontSize','fills','textDecoration','textCase','letterSpacing','lineHeight']);
        o.t = {auto: n.textAutoResize, align: n.textAlignHorizontal, valign: n.textAlignVertical, trim: n.leadingTrim,
          runs: segs.map(s => ({t: s.characters.split(LS).join('\n'), f: s.fontName.family, st: s.fontName.style, sz: s.fontSize, c: paints(s.fills), d: s.textDecoration, cs: s.textCase,
            ls: s.letterSpacing.unit === 'PERCENT' ? s.letterSpacing.value / 100 * s.fontSize : s.letterSpacing.value, lh: s.lineHeight.unit === 'AUTO' ? null : s.lineHeight.unit === 'PERCENT' ? s.lineHeight.value / 100 * s.fontSize : s.lineHeight.value}))};
        return o;
      }
      if ('children' in n) o.children = await Promise.all(n.children.map(ser));
      return o;
    };
    const enc = new TextEncoder();
    for (let i = 0; i < frames.length; i++) { const f = frames[i]; fb = f.absoluteBoundingBox;
      files.push({name: `frames/${tag}_${String(i+1).padStart(2,'0')}_${ids[i].replace(':','-')}.png`, data: await f.exportAsync({format:'PNG', constraint:{type:'SCALE', value:2}})});
      files.push({name: `${dir}/${ids[i].replace(':','-')}.json`, data: enc.encode(JSON.stringify(await ser(f)))}); }
    window.__save(window.__tar(files), `seciro_${dir}_v7.tar`);
    return {files: files.length, pngs: seen.size, stat};
  };
