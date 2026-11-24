#!/usr/bin/env python3
# Автотест ряду 4 (VPN): Dashboard → VPN → знайомство → дозвіл Android → підключення → таймер → зміна локації → вимкнення.
#   python3 claude/tools/proto-vpn-test.py
import html as H, json, os, re, subprocess, tempfile
HERE = os.path.dirname(os.path.abspath(__file__)); G = os.path.normpath(os.path.join(HERE, "..", ".."))
page = open(os.path.join(G, "prototype", "seciro.html"), encoding="utf-8").read()
page = re.sub(r'<link rel="stylesheet" href="https://fonts\.googleapis\.com[^"]+">', lambda m: '<link rel="stylesheet" href="file://' + os.path.join(HERE, ".fonts", "fonts.css") + '">', page)
pre = '<style>*,*::before,*::after{transition:none!important;animation:none!important}</style><script>window.requestAnimationFrame=cb=>setTimeout(()=>cb(performance.now()),16);window.cancelAnimationFrame=id=>clearTimeout(id);try{localStorage.clear()}catch(e){}</script>'
test = r'''<script>
const log=[]; const done=()=>document.body.setAttribute('data-log', JSON.stringify(log));
window.addEventListener('error', e=>{log.push('ERROR '+e.message); done();});
const sleep=ms=>new Promise(r=>setTimeout(r,ms));
const now=()=>(document.querySelector('.note b')?.parentElement.textContent||'').replace('Now: ','').split(' · Figma')[0];
const app=()=>document.querySelector('.app');
const byText=(t)=>[...app().querySelectorAll('*')].filter(e=>e.children.length===0 && e.textContent.trim()===t).pop();
const tapAt=async (el,ms=800)=>{ const r=el.getBoundingClientRect(); const x=r.left+r.width/2, y=r.top+r.height/2; const t=document.elementFromPoint(x,y); if(!t){ log.push('  !! nothing at '+Math.round(x)+','+Math.round(y)+' (viewport '+innerWidth+'x'+innerHeight+')'); el.click(); } else t.click(); await sleep(ms); };
const tap=async (t,ms=800)=>{ const e=byText(t); if(!e){log.push('  !! no «'+t+'»'); return;} await tapAt(e,ms); };
const power=async (ms=800)=>{ const b=app().querySelector('.vpn-btn-pop, .vpn-btn-connecting'); if(!b){log.push('  !! no power'); return;} b.click(); await sleep(ms); };
const cardText=()=>{ const t=[...app().querySelectorAll('*')].filter(e=>e.children.length===0 && /, (US|GB|FR|DE)$|Fastest Connection/.test(e.textContent.trim())); return t.map(e=>e.textContent.trim()).join(' | '); };
(async()=>{ try {
  await sleep(1200); log.push('start: '+now());
  await tap('VPN',900); log.push('tap VPN row: '+now());
  await power(900); log.push('power (first time): '+now());
  if(0) log.push('matches: '+[...app().querySelectorAll('*')].filter(e=>e.children.length===0 && e.textContent.trim()==='Connect to VPN').map(e=>{const r=e.getBoundingClientRect(); return Math.round(r.left)+','+Math.round(r.top)}).join(' ; ')+' | app rect '+JSON.stringify(app().getBoundingClientRect()));
  await tap('Connect to VPN',700); log.push('Connect to VPN → Android dialog: '+(byText('Connection request')?'shown':'missing'));

  await tap('OK',600); log.push('OK: '+now());
  await sleep(2400); log.push('after 2.4s: '+now()+' | card: '+cardText());
  await sleep(2100);
  const tm=[...app().querySelectorAll('div')].map(d=>d.textContent).find(s=>/^\d\d:\d\d:\d\d$/.test(s)); log.push('timer: '+tm);
  await tap('Change a Location',900); log.push('Change a Location: '+now());
  await tap('London, GB',400); log.push('pick London: '+now());
  await tap('Done',2600); log.push('Done → '+now()+' | card: '+cardText());
  await power(900); log.push('power (connected) → '+now());
  await power(2600); log.push('power again (not first) → '+now());
} catch(e) { log.push('EXC '+e.message); } done(); })();
</script>'''
with tempfile.TemporaryDirectory() as t:
    open(t + "/p.html", "w", encoding="utf-8").write('<!doctype html><html><head><meta charset="utf-8"><style>img{max-width:100%}</style>' + pre + '</head><body style="margin:0">' + page + test + '</body></html>')
    out = subprocess.run(["/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--headless=new", "--disable-gpu", f"--user-data-dir={t}/p",
                          "--window-size=1440,1000", "--virtual-time-budget=60000", "--allow-file-access-from-files", "--dump-dom",
                          f"file://{t}/p.html#dashboard"], capture_output=True, text=True).stdout
m = re.search(r'data-log="([^"]*)"', out)
print("\n".join(json.loads(H.unescape(m.group(1)))) if m else "no log (тест не встиг або впав)")
