#!/usr/bin/env python3
# Автотест ряду 6 (Safe QR Scanner): Dashboard → сканер → знайомство → скан → перевірка → Access blocked → копіювання → назад → галерея.
#   python3 claude/tools/proto-qr-test.py
import html as H, json, os, re, subprocess, tempfile
HERE = os.path.dirname(os.path.abspath(__file__)); G = os.path.normpath(os.path.join(HERE, "..", ".."))
page = open(os.path.join(G, "prototype", "seciro.html"), encoding="utf-8").read()
page = re.sub(r'<link rel="stylesheet" href="https://fonts\.googleapis\.com[^"]+">', lambda m: '<link rel="stylesheet" href="file://' + os.path.join(HERE, ".fonts", "fonts.css") + '">', page)
pre = '<style>*,*::before,*::after{transition:none!important;animation:none!important}</style><script>window.requestAnimationFrame=cb=>setTimeout(()=>cb(performance.now()),16);window.cancelAnimationFrame=id=>clearTimeout(id);try{localStorage.clear()}catch(e){}</script>'
test = r'''<script>
const log=[]; const done=()=>document.body.setAttribute('data-log', JSON.stringify(log));
window.addEventListener('error', e=>{log.push('ERROR '+e.message); done();});
const sleep=ms=>new Promise(r=>setTimeout(r,ms));
const now=()=>(document.querySelector('.scr-list [aria-current="true"] .nm')?.textContent||'');
const app=()=>document.querySelector('.app');
const byText=(t)=>[...app().querySelectorAll('*')].filter(e=>e.children.length===0 && e.textContent.trim()===t).pop();
const tapAt=async (el,ms=800)=>{ const r=el.getBoundingClientRect(); const x=r.left+r.width/2, y=r.top+r.height/2; const t=document.elementFromPoint(x,y); if(!t){ log.push('  !! nothing at '+Math.round(x)+','+Math.round(y)+' (viewport '+innerWidth+'x'+innerHeight+')'); el.click(); } else t.click(); await sleep(ms); };
const tap=async (t,ms=800)=>{ const e=byText(t); if(!e){log.push('  !! no «'+t+'»'); return;} await tapAt(e,ms); };
const power=async (ms=800)=>{ const b=app().querySelector('.vpn-btn-pop, .vpn-btn-connecting'); if(!b){log.push('  !! no power'); return;} b.click(); await sleep(ms); };
const cardText=()=>{ const t=[...app().querySelectorAll('*')].filter(e=>e.children.length===0 && /, (US|GB|FR|DE)$|Fastest Connection/.test(e.textContent.trim())); return t.map(e=>e.textContent.trim()).join(' | '); };
(async()=>{ try {
  const vis=(t)=>{ const e=byText(t); if(!e) return false; const r=e.getBoundingClientRect(); const a=app().getBoundingClientRect(); return r.top<a.bottom-5 && r.bottom>a.top; };
  await sleep(1200); log.push('start: '+now());
  await tap('Safe QR Scanner',1500); log.push('tap row: '+now()+' | intro visible: '+vis('Scan a QR Code'));
  await tap('Scan a QR Code',900); log.push('Scan a QR Code → '+now());
  await sleep(1600); log.push('after 1.6s → '+now()+' | spinner sheet: '+vis('Validating...'));
  await sleep(1800); log.push('after 3.4s → '+now()+' | blocked: '+vis('Access blocked'));
  const cp=[...app().querySelectorAll('[role=button]')].find(e=>{const r=e.getBoundingClientRect(); return Math.abs(r.width-20*r.width/r.width)>=0 && r.width<40 && r.height<40 && r.top>300;});
  if(cp){ cp.click(); await sleep(600); } log.push('copy → '+now());
  await sleep(1800); log.push('after copy 1.8s → '+now());
  await tap('Back to Safety',900); log.push('Back to Safety → '+now()+' | blocked visible: '+vis('Access blocked'));
  await tap('From gallery',800); log.push('From gallery → '+now());
  await sleep(1800); log.push('after gallery → '+now());
  } catch(e) { log.push('EXC '+e.message); } done(); })();
</script>'''
with tempfile.TemporaryDirectory() as t:
    open(t + "/p.html", "w", encoding="utf-8").write('<!doctype html><html><head><meta charset="utf-8"><style>img{max-width:100%}</style>' + pre + '</head><body style="margin:0">' + page + test + '</body></html>')
    out = subprocess.run(["/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--headless=new", "--disable-gpu", f"--user-data-dir={t}/p",
                          "--window-size=1440,1000", "--virtual-time-budget=60000", "--allow-file-access-from-files", "--dump-dom",
                          f"file://{t}/p.html#dashboard"], capture_output=True, text=True).stdout
m = re.search(r'data-log="([^"]*)"', out)
print("\n".join(json.loads(H.unescape(m.group(1)))) if m else "no log (тест не встиг або впав)")
