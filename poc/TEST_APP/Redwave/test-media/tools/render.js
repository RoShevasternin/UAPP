// Renders the prototype's lo-fi synth to 16-bit WAV files in headless Chromium.
//   node render.js <out_dir>
// Env: CHROMIUM=/path/to/chrome   (optional; Playwright's own Chromium is used otherwise)
// Needs Playwright (global install is fine).
const path = require('path');
const fs = require('fs');
let pw;
try { pw = require('playwright'); }
catch (e) { pw = require(require('child_process').execSync('npm root -g').toString().trim() + '/playwright'); }

// id is the synth seed — keep ids exactly as in the prototype / catalog.
const JOBS = [
  { id: 't1',   file: 'neon-heart',      seconds: 45 },
  { id: 't2',   file: 'night-drive',     seconds: 45 },
  { id: 'k1',   file: 'stage-lights',    seconds: 45 },
  { id: 'k4',   file: 'needle-drop',     seconds: 45 },
  { id: 'k3',   file: 'blue-room',       seconds: 45 },
  { id: 't3',   file: 'into-the-smoke',  seconds: 45 },
  { id: 'k8',   file: 'white-noise',     seconds: 45 },
  { id: 't5',   file: 'bass-theory',     seconds: 25, channels: 1 },          // WAV kept mono to save space
  { id: 't2',   file: 'untitled-demo',   seconds: 20 },
  { id: 'e112', file: 'ep112',           seconds: 60, kind: 'podcast', channels: 1 },
  { id: 'e111', file: 'ep111',           seconds: 60, kind: 'podcast', channels: 1 },
];

(async () => {
  const out = path.resolve(process.argv[2] || 'wav');
  fs.mkdirSync(out, { recursive: true });
  const browser = await pw.chromium.launch({ executablePath: process.env.CHROMIUM || undefined, args: ['--autoplay-policy=no-user-gesture-required'] });
  const page = await browser.newPage();
  page.on('console', m => console.log('[page]', m.text()));
  page.on('pageerror', e => console.error('[page error]', e.message));
  await page.goto('file://' + path.join(__dirname, 'synth.html'));
  for (const j of JOBS) {
    const t0 = Date.now();
    const r = await page.evaluate(o => window.renderTrack(o), { id: j.id, kind: j.kind || 'song', seconds: j.seconds, channels: j.channels || 2 });
    const buf = Buffer.from(r.wavB64, 'base64');
    fs.writeFileSync(path.join(out, j.file + '.wav'), buf);
    console.log(`${j.file}.wav  id=${j.id} bpm=${r.bpm} root=${r.root} rawPeak=${r.peakBefore.toFixed(3)} gain=${r.gain.toFixed(3)} bytes=${buf.length} ${Date.now() - t0}ms`);
  }
  await browser.close();
})().catch(e => { console.error(e); process.exit(1); });
