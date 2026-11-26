#!/usr/bin/env python3
# Автотест потоку входу (ряд 2): друкує пошту й код «клавіатурою», тисне кнопки, перемикає відповіді сервера.
#   python3 claude/tools/proto-auth-test.py
import html as H, json, os, re, subprocess, tempfile
HERE = os.path.dirname(os.path.abspath(__file__)); G = os.path.normpath(os.path.join(HERE, "..", ".."))
page = open(os.path.join(G, "prototype", "seciro.html"), encoding="utf-8").read()
page = re.sub(r'<link rel="stylesheet" href="https://fonts\.googleapis\.com[^"]+">', lambda m: '<link rel="stylesheet" href="file://' + os.path.join(HERE, ".fonts", "fonts.css") + '">', page)
pre = '<script>window.requestAnimationFrame=cb=>setTimeout(()=>cb(performance.now()),16);window.cancelAnimationFrame=id=>clearTimeout(id);try{localStorage.clear()}catch(e){}</script>'
test = r'''<script>
const log=[]; const done=()=>document.body.setAttribute('data-log', JSON.stringify(log));
window.addEventListener('error', e=>{log.push('ERROR '+e.message); done();});
const sleep=ms=>new Promise(r=>setTimeout(r,ms));
const now=()=>(document.querySelector('.scr-list [aria-current="true"] .nm')?.textContent||'');
const type=async s=>{ for(const ch of s){ window.dispatchEvent(new KeyboardEvent('keydown',{key:ch})); await sleep(30);} };
const btn=t=>[...document.querySelectorAll('[role=button], .pbtn, .tap')].find(e=>e.textContent.trim()===t);
const click=async (t,ms=900)=>{ const b=btn(t); if(!b){log.push('  !! no button «'+t+'»'); return;} b.click(); await sleep(ms); };
const field=()=>[...document.querySelectorAll('div')].find(d=>d.style.cursor==='text');
const focus=async()=>{ const f=field(); f.dispatchEvent(new PointerEvent('pointerdown',{bubbles:true})); await sleep(500); };
(async()=>{ try {
  await sleep(1500); log.push('start: '+now());
  await focus(); log.push('tap field: '+now());
  await type('name'); log.push('typed "name" (invalid): '+now()+' | Continue opacity '+btn('Continue')?.style.opacity);
  await type('@example.com'); log.push('typed full email: '+now()+' | Continue opacity '+btn('Continue')?.style.opacity);
  await click('No internet',100); await click('Continue',1300); log.push('Continue + No internet: '+now());
  await click('Code sent',100); await click('Retry',1600); log.push('Retry + Code sent: '+now());
  await sleep(600); log.push('auto focus code: '+now());
  await click('Wrong code',100); await type('123456'); await sleep(2400); log.push('6 digits + Wrong code: '+now());
  await focus(); await type('7'); log.push('type after error: '+now());
  await click('Ended',100); await type('65432'); await sleep(2400); log.push('6 digits + Ended: '+now());
  await click('Try another email',900); log.push('Try another email: '+now());
  await click('No subscription',100); await type('x'); window.dispatchEvent(new KeyboardEvent('keydown',{key:'Enter'})); await sleep(1300); log.push('Enter + No subscription: '+now());
  await click('See plans',900); log.push('See plans: '+now());
} catch(e) { log.push('EXC '+e.message+' '+e.stack); } done(); })();
</script>'''
with tempfile.TemporaryDirectory() as t:
    open(t + "/p.html", "w", encoding="utf-8").write('<!doctype html><html><head><meta charset="utf-8"><style>img{max-width:100%}</style>' + pre + '</head><body style="margin:0">' + page + test + '</body></html>')
    out = subprocess.run(["/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--headless=new", "--disable-gpu", f"--user-data-dir={t}/p",
                          "--window-size=1440,1000", "--virtual-time-budget=60000", "--allow-file-access-from-files", "--dump-dom",
                          f"file://{t}/p.html#signin"], capture_output=True, text=True).stdout
m = re.search(r'data-log="([^"]*)"', out)
print("\n".join(json.loads(H.unescape(m.group(1)))) if m else "no log (тест не встиг або впав)")
