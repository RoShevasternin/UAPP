#!/usr/bin/env python3
# Автотест пейджера онбордингу: headless Chrome «тягне пальцем» і тисне кнопки, друкує позицію й екран.
#   python3 claude/tools/proto-swipe-test.py
# requestAnimationFrame підміняється таймером: у headless з virtual time справжній rAF не крутиться.
import html as H, json, os, re, subprocess, tempfile
HERE = os.path.dirname(os.path.abspath(__file__)); G = os.path.normpath(os.path.join(HERE, "..", ".."))
page = open(os.path.join(G, "prototype", "seciro.html"), encoding="utf-8").read()
page = re.sub(r'<link rel="stylesheet" href="https://fonts\.googleapis\.com[^"]+">', lambda m: '<link rel="stylesheet" href="file://' + os.path.join(HERE, ".fonts", "fonts.css") + '">', page)
pre = '<script>window.requestAnimationFrame=cb=>setTimeout(()=>cb(performance.now()),16);window.cancelAnimationFrame=id=>clearTimeout(id);</script>'
test = r'''<script>
const log=[]; const done=()=>document.body.setAttribute('data-log', JSON.stringify(log));
window.addEventListener('error', e=>{log.push('ERROR '+e.message); done();});
const sleep=ms=>new Promise(r=>setTimeout(r,ms));
const P=()=>(window.__seciroPos??'?').toString().slice(0,5);
const now=()=>(document.querySelector('.note b')?.parentElement.textContent||'').replace('Now: ','');
async function drag(dx, steps, ms){
  const el=[...document.querySelectorAll('div')].find(d=>d.style.cursor==='grab'||d.style.cursor==='grabbing');
  const r=el.getBoundingClientRect(); const x0=r.left+r.width*0.6, y=r.top+r.height*0.4;
  const ev=(t,x)=>el.dispatchEvent(new PointerEvent(t,{bubbles:true,clientX:x,clientY:y,pointerId:1,pointerType:'touch',isPrimary:true}));
  ev('pointerdown',x0); for(let i=1;i<=steps;i++){await sleep(ms); ev('pointermove',x0+dx*i/steps);} const mid=P(); ev('pointerup',x0+dx); await sleep(1200); return mid+' -> '+P()+'  | '+now();
}
async function click(text){ const b=[...document.querySelectorAll('[role=button]')].find(e=>e.textContent.includes(text)); if(!b) return 'no button '+text; b.click(); await sleep(1200); return 'tap «'+text+'» -> '+P()+' | '+now(); }
(async()=>{ try {
  await sleep(2000);
  log.push('slow drag 260px left:  '+await drag(-260,20,16));
  log.push('fast flick 60px left:  '+await drag(-60,4,8));
  log.push('slow drag 150px right: '+await drag(150,20,16));
  log.push('slow drag 320px right: '+await drag(320,20,16));
  log.push('pull past first page: '+await drag(400,20,16));
  log.push(await click("I'm new here"));
  log.push(await click('Continue'));
} catch(e) { log.push('EXC '+e.message); } done(); })();
</script>'''
with tempfile.TemporaryDirectory() as t:
    open(t + "/p.html", "w", encoding="utf-8").write('<!doctype html><html><head><meta charset="utf-8">' + pre + '</head><body style="margin:0">' + page + test + '</body></html>')
    out = subprocess.run(["/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--headless=new", "--disable-gpu", f"--user-data-dir={t}/p",
                          "--window-size=1440,1000", "--virtual-time-budget=40000", "--allow-file-access-from-files", "--dump-dom",
                          f"file://{t}/p.html#onb2"], capture_output=True, text=True).stdout
m = re.search(r'data-log="([^"]*)"', out)
print("\n".join(json.loads(H.unescape(m.group(1)))) if m else "no log (тест не встиг або впав)")
