"""Supplementary queries targeting downloader+launcher hybrids (added beyond the required list)."""
import json, time, random, sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from collect import retry, fetch, RAW
from google_play_scraper import search
EXTRA = ['video downloader launcher', 'downloader launcher', 'video downloader home', 'music home launcher',
         'home launcher downloader', 'video player home launcher', 'downloader home screen', 'smart home launcher video']
store = json.load(open(RAW))
for q in EXTRA:
    res = retry(search, q, lang='en', country='us', n_hits=250) or []
    print(q, len(res), flush=True)
    for rank, r in enumerate(res):
        aid = r['appId']
        if aid not in store:
            d = fetch(aid); time.sleep(0.5 + random.random() * 0.4)
            if not d: continue
            store[aid] = d; store[aid]['found_by'] = []
        tag = f'x:{q}#{rank+1}'
        if tag not in store[aid]['found_by']:
            store[aid]['found_by'].append(tag)
    time.sleep(1.5)
json.dump(store, open(RAW, 'w'))
print('total', len(store))
