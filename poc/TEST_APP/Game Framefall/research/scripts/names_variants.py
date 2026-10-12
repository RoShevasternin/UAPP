"""Extra check: spaced variants of names that looked free (e.g. 'Clip Wave'), appended into names.json."""
import json, os, re, time, random
from google_play_scraper import search
BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
P = os.path.join(BASE, 'data', 'names.json')
VAR = {'Framefall': 'Frame Fall', 'Cinedrop': 'Cine Drop', 'Clipwave': 'Clip Wave', 'Lumenreel': 'Lumen Reel',
       'Moviedrop': 'Movie Drop', 'Glowframe': 'Glow Frame', 'Vidwave': 'Vid Wave', 'Beamline': 'Beam Line', 'Frameport': 'Frame Port'}
norm = lambda s: re.sub(r'[^a-z0-9]', '', s.lower())
d = json.load(open(P))
for n, v in VAR.items():
    res = search(v, lang='en', country='us', n_hits=60) or []
    hits = [{'appId': r['appId'], 'title': r['title'], 'developer': r['developer'], 'installs': r.get('installs')}
            for r in res if norm(n) in norm(r['title']) or norm(n) in norm(r['developer'] or '')]
    near = [{'appId': r['appId'], 'title': r['title'], 'installs': r.get('installs')} for r in res
            if re.search(r'\b' + v.split()[0] + r'\b.{0,3}\b' + v.split()[1], r['title'], re.I) and r not in hits]
    d[n]['spaced_variant'] = {'query': v, 'collisions': hits, 'phrase_matches': near[:8]}
    print(n, v, [(h['title'], h['installs']) for h in hits], [(x['title'], x['installs']) for x in near[:5]])
    time.sleep(1.5 + random.random())
json.dump(d, open(P, 'w'), indent=1, ensure_ascii=False)
