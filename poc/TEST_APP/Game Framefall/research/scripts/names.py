"""Step 4: name collision check on Google Play US."""
import json, os, time, random, re
from google_play_scraper import search

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
NAMES = ['Kinora', 'Framefall', 'Beamdrop', 'Framevault', 'Cinedrop', 'Clipwave', 'Vidwave', 'Reelvault',
         'Frameport', 'Lumenreel', 'Moviedrop', 'Beamline', 'Glowframe', 'Nightframe', 'Clipvault']

def norm(s):
    return re.sub(r'[^a-z0-9]', '', s.lower())

out = {}
for n in NAMES:
    res = None
    for i in range(5):
        try:
            res = search(n, lang='en', country='us', n_hits=60); break
        except Exception as e:
            time.sleep(2 ** i + random.random())
    res = res or []
    hits, near = [], []
    for r in res:
        t = r.get('title') or ''
        dev = r.get('developer') or ''
        rec = {'appId': r['appId'], 'title': t, 'developer': dev, 'installs': r.get('installs'),
               'score': r.get('score')}
        # exact token containment, ignoring spaces/case ("Clip Wave" == "Clipwave")
        if norm(n) in norm(t) or norm(n) in norm(dev) or norm(n) in norm(r['appId']):
            rec['starts_with'] = norm(t).startswith(norm(n))
            rec['match_in'] = [k for k, v in (('title', t), ('developer', dev), ('appId', r['appId'])) if norm(n) in norm(v)]
            hits.append(rec)
    out[n] = {'results_returned': len(res), 'collisions': hits,
              'top5_results': [{'appId': r['appId'], 'title': r['title'], 'installs': r.get('installs')} for r in res[:5]]}
    print(n, len(res), [(h['title'], h['installs']) for h in hits], flush=True)
    time.sleep(1.5 + random.random())
json.dump(out, open(os.path.join(BASE, 'data', 'names.json'), 'w'), indent=1, ensure_ascii=False)
