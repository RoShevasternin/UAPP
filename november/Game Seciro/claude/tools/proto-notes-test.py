#!/usr/bin/env python3
# Перевірка нотаток прототипу (build 27+): проходить усі екрани й для кожної нотатки з `at`
# дивиться, чи знайшлась рамка на екрані (і де). Плюс — дамп видимих текстів екрана для написання прив'язок.
#   python3 claude/tools/proto-notes-test.py            # звіт: екран · нотатка · рамка / «НЕ ЗНАЙДЕНО»
#   python3 claude/tools/proto-notes-test.py --texts    # ще й усі тексти кожного екрана з координатами (JSON у stdout)
import html as H, json, os, re, subprocess, sys, tempfile
HERE = os.path.dirname(os.path.abspath(__file__)); G = os.path.normpath(os.path.join(HERE, "..", ".."))
TEXTS = "--texts" in sys.argv
page = open(os.path.join(G, "prototype", "seciro.html"), encoding="utf-8").read()
page = re.sub(r'<link rel="stylesheet" href="https://fonts\.googleapis\.com[^"]+">', lambda m: '<link rel="stylesheet" href="file://' + os.path.join(HERE, ".fonts", "fonts.css") + '">', page)
pre = '<script>window.LIM=' + os.environ.get('LIM','999') + ';window.requestAnimationFrame=cb=>setTimeout(()=>cb(performance.now()),16);window.cancelAnimationFrame=id=>clearTimeout(id);try{localStorage.clear()}catch(e){}</script>'
test = r'''<script>
const out={}; const done=()=>document.body.setAttribute('data-log', JSON.stringify(out));
window.addEventListener('error', e=>{out.__err=(out.__err||'')+e.message; done();});
const sleep=ms=>new Promise(r=>setTimeout(r,ms));
const norm=s=>(s||'').replace(/[\s\u00a0\u2028]+/g,' ').trim();
const WANT_TEXTS=%s;
(async()=>{ try {
  await sleep(1200);
  const items=[...document.querySelectorAll('.scr-list button')];
  for (let k=0;k<Math.min(items.length,+(window.LIM||999));k++){
    const b=[...document.querySelectorAll('.scr-list button')][k]; const name=b.querySelector('.nm').textContent; b.click(); await sleep(1500); document.getAnimations().forEach(x=>{try{ if(x.effect&&x.effect.getComputedTiming().iterations!==Infinity) x.finish(); }catch(e){}}); await sleep(300);
    const app=document.querySelector('.phone-clip .app'), a=app.getBoundingClientRect(), sc=a.width/375;
    const marks=[...app.querySelectorAll('.mark')].map(m=>{const r=m.getBoundingClientRect(); return [m.textContent, Math.round((r.left-a.left)/sc+4), Math.round((r.top-a.top)/sc+4), Math.round(r.width/sc-8), Math.round(r.height/sc-8)];});
    const notes=[...document.querySelectorAll('.notes li')].map(li=>({n:li.querySelector('.num').textContent, t:(li.querySelector('.nb-t')||li.querySelector('.nb-p')||{}).textContent||''}));
    const rec={marks, notes};
    if (WANT_TEXTS){
      const root=app.querySelector('[class^="scr-in"]'); const seen=[];
      for (const el of root.querySelectorAll('*')){ const t=norm(el.textContent); if(!t||t.length>160) continue;
        if([...el.children].some(c=>norm(c.textContent)===t)) continue;
        const kids=[...el.children].filter(c=>norm(c.textContent)); if(kids.length>1 && kids.some(c=>c.tagName!=='SPAN')) continue;
        if(el.checkVisibility && !el.checkVisibility({opacityProperty:true,visibilityProperty:true})) continue;
        const r=el.getBoundingClientRect(); if(r.width<1) continue;
        seen.push([t, Math.round((r.left-a.left)/sc), Math.round((r.top-a.top)/sc), Math.round(r.width/sc), Math.round(r.height/sc)]); }
      rec.texts=seen;
    }
    out[name]=rec;
  }
} catch(e) { out.__exc=e.message; } done(); })();
</script>''' % ("true" if TEXTS else "false")
with tempfile.TemporaryDirectory() as t:
    open(t + "/p.html", "w", encoding="utf-8").write('<!doctype html><html><head><meta charset="utf-8"><style>img{max-width:100%}[hidden]{display:none!important}</style>' + pre + '</head><body style="margin:0">' + page + test + '</body></html>')
    res = subprocess.run(["/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--headless=new", "--disable-gpu", f"--user-data-dir={t}/p",
                          "--window-size=1600,1000", "--virtual-time-budget=400000", "--allow-file-access-from-files", "--dump-dom",
                          f"file://{t}/p.html#splash"], capture_output=True, text=True, timeout=900).stdout
m = re.search(r'data-log="([^"]*)"', res)
if not m: sys.exit("no log (тест не встиг або впав)")
data = json.loads(H.unescape(m.group(1)))
if TEXTS or "--json" in sys.argv: print(json.dumps(data, ensure_ascii=False)); sys.exit()
bad = 0
for name, rec in data.items():
    if name.startswith("__"): print(name, rec); bad += 1; continue
    got = {m[0] for m in rec["marks"]}
    for n in rec["notes"]:
        has = n["n"] in got
        mk = next((m for m in rec["marks"] if m[0] == n["n"]), None)
        print(f"{name:34} {n['n']:>2} {'✓ ' + str(mk[1:]) if mk else '·'}  {n['t'][:70]}")
print("помилок:", bad)
