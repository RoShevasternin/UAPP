"""Step 3a: fetch newest ~200 reviews for apps with >=20 ratings (top ~80 video downloaders by installs first)."""
import json, os, time, random, sys
from google_play_scraper import reviews, Sort

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
APPS = os.path.join(BASE, 'data', 'apps.json')
RAW = os.path.join(BASE, 'data', 'raw_reviews.json')
LIMIT_APPS = int(sys.argv[1]) if len(sys.argv) > 1 else 110

apps = json.load(open(APPS))
store = json.load(open(RAW)) if os.path.exists(RAW) else {}

def rel(a):
    c = a['classification']
    return c['is_video_downloader'] or c['is_launcher_app'] and 'video_wallpaper' in c['features'] or 'video_wallpaper' in c['features'] and c['mentions_launcher']

cands = [a for a in apps if (a.get('ratings') or 0) >= 20 and rel(a)]
cands.sort(key=lambda a: -(a.get('realInstalls') or a.get('minInstalls') or 0))
todo = cands[:LIMIT_APPS]
if len(sys.argv) > 2:  # explicit extra ids (video+launcher set)
    ids = sys.argv[2].split(',')
    todo = [a for a in apps if a['appId'] in ids and ((a.get('ratings') or 0) >= 20 or a['classification'].get('launcher_hybrid'))]
print('candidates', len(cands), 'todo', len(todo), flush=True)
for k, a in enumerate(todo):
    aid = a['appId']
    if aid in store:
        continue
    res = None
    for i in range(6):
        try:
            res, _ = reviews(aid, lang='en', country='us', sort=Sort.NEWEST, count=200)
            break
        except Exception as e:
            w = 2 ** i * 2 + random.random()
            print('  retry', aid, type(e).__name__, str(e)[:80], w, flush=True)
            time.sleep(w)
    res = res or []
    store[aid] = [{'reviewId': r['reviewId'], 'text': r.get('content') or '', 'score': r.get('score'),
                   'date': r['at'].strftime('%Y-%m-%d') if r.get('at') else None,
                   'thumbsUp': r.get('thumbsUpCount'), 'appVersion': r.get('reviewCreatedVersion')} for r in res]
    print(k, aid, len(res), flush=True)
    if k % 5 == 0:
        json.dump(store, open(RAW, 'w'))
    time.sleep(1 + random.random())
json.dump(store, open(RAW, 'w'))
print('done', len(store))
