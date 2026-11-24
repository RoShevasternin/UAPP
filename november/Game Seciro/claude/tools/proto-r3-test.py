#!/usr/bin/env python3
# Автотест ряду 3: Dashboard → Run Protection Check → сканування → результат → аркуш → Done.
#   python3 claude/tools/proto-r3-test.py
import html as H, json, os, re, subprocess, tempfile
HERE = os.path.dirname(os.path.abspath(__file__)); G = os.path.normpath(os.path.join(HERE, "..", ".."))
page = open(os.path.join(G, "prototype", "seciro.html"), encoding="utf-8").read()
page = re.sub(r'<link rel="stylesheet" href="https://fonts\.googleapis\.com[^"]+">', lambda m: '<link rel="stylesheet" href="file://' + os.path.join(HERE, ".fonts", "fonts.css") + '">', page)
pre = '<script>window.requestAnimationFrame=cb=>setTimeout(()=>cb(performance.now()),16);window.cancelAnimationFrame=id=>clearTimeout(id);try{localStorage.clear()}catch(e){}</script>'
test = r'''<script>
const log=[]; const done=()=>document.body.setAttribute('data-log', JSON.stringify(log));
window.addEventListener('error', e=>{log.push('ERROR '+e.message); done();});
const sleep=ms=>new Promise(r=>setTimeout(r,ms));
const now=()=>(document.querySelector('.note b')?.parentElement.textContent||'').replace('Now: ','').split(' · Figma')[0];
const app=()=>document.querySelector('.app');
const btn=(t,exact=true)=>[...app().querySelectorAll('[role=button]')].find(e=>exact? e.textContent.trim()===t : e.textContent.includes(t));
const pbtn=t=>[...document.querySelectorAll('.pbtn')].find(e=>e.textContent.trim()===t);
const click=async (t,ms=900,exact=true)=>{ const b=btn(t,exact); if(!b){log.push('  !! no «'+t+'»'); return;} b.click(); await sleep(ms); };
const pct=()=>[...app().querySelectorAll('div')].map(d=>d.textContent).find(t=>/^\d+%$/.test(t));
(async()=>{ try {
  await sleep(1500); log.push('start: '+now());
  pbtn('2 issues').click(); await click('Run Protection Check',2000); log.push('after 2s: '+now()+' | ring '+pct());
  await sleep(6000); log.push('after scan: '+now());
  await click('Web Protection',900,false); log.push('tap Web Protection: '+now());
  await click('Got it',900); log.push('Got it: '+now());
  await click('Software Update',900,false); log.push('tap Software Update: '+now());
  await click('Got it',900); await click('Done',900); log.push('Done: '+now());
  await click('Web Protection',900,false); log.push('dashboard issue row: '+now()+' | sheet visible: '+!!btn('Got it'));
  await click('Got it',700); pbtn('Nothing').click(); await click('Run Protection Check',8000); log.push('scan with Nothing: '+now());
  await click('Done',900); log.push('Done: '+now());
} catch(e) { log.push('EXC '+e.message); } done(); })();
</script>'''
with tempfile.TemporaryDirectory() as t:
    open(t + "/p.html", "w", encoding="utf-8").write('<!doctype html><html><head><meta charset="utf-8">' + pre + '</head><body style="margin:0">' + page + test + '</body></html>')
    out = subprocess.run(["/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--headless=new", "--disable-gpu", f"--user-data-dir={t}/p",
                          "--window-size=1440,1000", "--virtual-time-budget=60000", "--allow-file-access-from-files", "--dump-dom",
                          f"file://{t}/p.html#dashboard"], capture_output=True, text=True).stdout
m = re.search(r'data-log="([^"]*)"', out)
print("\n".join(json.loads(H.unescape(m.group(1)))) if m else "no log (тест не встиг або впав)")
