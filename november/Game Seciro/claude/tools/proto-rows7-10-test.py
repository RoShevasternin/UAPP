#!/usr/bin/env python3
# Автотест рядів 7–10 і зведення: Storage Cleaner, Breach Check, Password Generator, Settings / My Account.
#   python3 claude/tools/proto-rows7-10-test.py
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
  const vis=(t)=>{ const e=byText(t); if(!e) return false; const r=e.getBoundingClientRect(); const a=app().getBoundingClientRect(); return r.top<a.bottom-5 && r.bottom>a.top && getComputedStyle(e).visibility!=='hidden'; };
  const inApp=(e)=>{ const r=e.getBoundingClientRect(), a=app().getBoundingClientRect(); return r.top>=a.top-2 && r.bottom<=a.bottom+2 && r.left>=a.left-2 && r.right<=a.right+2; };
  const tapV=async (t,ms=800)=>{ const all=[...app().querySelectorAll('*')].filter(e=>e.children.length===0 && e.textContent.trim()===t && inApp(e)); const e=all[0]; if(!e){log.push('  !! no visible «'+t+'»'); return;} await tapAt(e,ms); };
  const nav=async (name,ms=900)=>{ const b=[...document.querySelectorAll('.scr-list button')].find(x=>x.querySelector('.nm')&&x.querySelector('.nm').textContent===name); if(!b){log.push('  !! no list item '+name);return;} b.click(); await sleep(ms); };
  const type=async (str)=>{ for(const c of str){ window.dispatchEvent(new KeyboardEvent('keydown',{key:c,bubbles:true})); await sleep(15);} await sleep(200); };
  await sleep(1200); log.push('start: '+now());
  // Storage Cleaner
  await tap('Storage Cleaner',1400); log.push('Storage row → '+now()+' | intro: '+vis('Scan Storage'));
  await tap('Scan Storage',900); log.push('Scan Storage → '+now());
  await sleep(4400); log.push('after scan → '+now());
  await tap('Screenshots',900); log.push('Screenshots → '+now());
  const da=[...app().querySelectorAll('*')].filter(e=>e.children.length===0 && e.textContent.trim()==='Deselect All').sort((a,b)=>a.getBoundingClientRect().top-b.getBoundingClientRect().top);
  log.push('Deselect All count: '+da.length); if(da[0]) await tapAt(da[0],600);
  log.push('after Deselect All (top) → Select All shown: '+!!byText('Select All'));
  await tap('Clean selected',600); log.push('Clean selected with none → '+now());
  const sa=byText('Select All'); if(sa) await tapAt(sa,500);
  await tap('Clean selected',800); log.push('Clean selected → '+now());
  await sleep(3200); log.push('after clean → '+now()+' | complete: '+vis('Cleanup Complete!'));
  await tap('Good',900); log.push('Good → '+now());
  // Breach Check
  await nav('Dashboard',900);
  await tap('Breach Check',1400); log.push('Breach row → '+now()+' | intro: '+vis('Check My Email'));
  await tap('Check My Email',1000); log.push('Check My Email → '+now());
  await type('vlad@seciro.app'); log.push('typed → '+now());
  await tap('Check for Breaches',900); log.push('Check → '+now());
  await sleep(3000); log.push('after check → '+now());
  const lk=byText('LinkedIn — 2021'); if(lk) await tapAt(lk,900); log.push('LinkedIn card → '+now());
  await tap('Done',900); log.push('Done (detail) → '+now());
  await tap('Done',900); log.push('Done (results) → '+now());
  // Password Generator
  await nav('Password Generator · first open',1400); log.push('Password (list) → '+now()+' | intro: '+vis('Generate a Password'));
  await tap('Generate a Password',1200);
  const pw=()=>{ const e=[...app().querySelectorAll('*')].find(x=>x.children.length===0 && /^[A-Za-z0-9!#$%&*?@]{8,24}$/.test(x.textContent.trim()) && x.getBoundingClientRect().top<300); return e?e.textContent.trim():'?'; };
  const p1=pw(); log.push('generated: len '+p1.length+' → '+now());
  await tap('Regenerate',900); const p2=pw(); log.push('regenerate changed: '+(p1!==p2)+' len '+p2.length);
  const sym=[...app().querySelectorAll('[role=switch]')].find(e=>e.textContent.includes('Symbols')); if(sym){ sym.click(); await sleep(700); }
  log.push('symbols off → has symbol: '+/[!#$%&*?@]/.test(pw())+' | status: '+(vis('Strong')?'Strong':vis('Medium')?'Medium':vis('Weak')?'Weak':'?'));
  await tap('Copy Password',500); log.push('Copy → '+now());
  // Settings, My Account
  await nav('Dashboard',900);
  await tap('Settings',1000); log.push('Settings tab → '+now());
  await tapV('My Account',900); log.push('My Account (settings) sheet: '+(()=>{const e=byText('Manage Subscription'); return !!e && inApp(e);})());
  await tapV('Done',800); log.push('Done → sheet visible: '+(()=>{const e=byText('Manage Subscription'); return !!e && inApp(e);})());
  await tap('Home',1000); log.push('Home tab → '+now());
  const ic=[...app().querySelectorAll('[role=button]')].find(e=>{const r=e.getBoundingClientRect(), a=app().getBoundingClientRect(); return r.top-a.top<130*a.height/812 && r.left-a.left>300*a.width/375;});
  if(ic){ ic.click(); await sleep(900); } log.push('account icon → '+now()+' | sheet: '+vis('Manage Subscription'));
  await tap('Done',900); log.push('Done → '+now());
  // Зведення
  const sb=[...document.querySelectorAll('.sumbar button')]; log.push('summary buttons: '+sb.map(b=>b.textContent).join(' | '));
  sb[0].click(); await sleep(400); const items=document.querySelectorAll('.sum-list li').length; log.push('manager list items: '+items);
  const chip=document.querySelector('.sum-scr button'); const nm=chip&&chip.textContent; chip&&chip.click(); await sleep(900); log.push('chip «'+nm+'» → '+now()+' | overlay closed: '+!document.querySelector('.sum-overlay'));
  } catch(e) { log.push('EXC '+e.message); } done(); })();
</script>'''
with tempfile.TemporaryDirectory() as t:
    open(t + "/p.html", "w", encoding="utf-8").write('<!doctype html><html><head><meta charset="utf-8"><style>img{max-width:100%}</style>' + pre + '</head><body style="margin:0">' + page + test + '</body></html>')
    out = subprocess.run(["/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", "--headless=new", "--disable-gpu", f"--user-data-dir={t}/p",
                          "--window-size=1440,1000", "--virtual-time-budget=120000", "--allow-file-access-from-files", "--dump-dom",
                          f"file://{t}/p.html#dashboard"], capture_output=True, text=True).stdout
m = re.search(r'data-log="([^"]*)"', out)
print("\n".join(json.loads(H.unescape(m.group(1)))) if m else "no log (тест не встиг або впав)")
