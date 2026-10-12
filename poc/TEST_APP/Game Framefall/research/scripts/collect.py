"""Step 1: collect app set from Google Play US (search + similar one hop)."""
import json, time, random, os, sys
from google_play_scraper import search, app
from google_play_scraper.features.app import app as _app
import requests, re

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(BASE, 'data')
RAW = os.path.join(DATA, 'raw_apps.json')

QUERIES = ["video downloader", "all video downloader", "video downloader app", "hd video downloader",
           "video saver", "download video", "video download manager", "video downloader browser",
           "m3u8 downloader", "hls downloader", "video downloader for social media", "story saver",
           "status saver", "reels downloader", "tiktok downloader", "video downloader facebook",
           "insta video downloader", "twitter video downloader", "video downloader no watermark",
           "private video downloader", "video player downloader", "video home screen", "video launcher",
           "video wallpaper launcher", "home screen video wallpaper", "video to mp3", "video cutter",
           "video to gif"]

def retry(fn, *a, **kw):
    for i in range(6):
        try:
            return fn(*a, **kw)
        except Exception as e:
            msg = str(e)
            if 'not found' in msg.lower() or 'NotFound' in type(e).__name__:
                return None
            wait = (2 ** i) + random.random()
            print(f'  retry {i} {type(e).__name__}: {msg[:100]} wait {wait:.1f}', flush=True)
            time.sleep(wait)
    return None

SESSION = requests.Session()
SESSION.headers['User-Agent'] = 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/124 Safari/537.36'

def similar_ids(app_id):
    """Parse the 'Similar apps' cluster from the details page, follow the cluster link."""
    url = f'https://play.google.com/store/apps/details?id={app_id}&hl=en&gl=us'
    for i in range(5):
        r = SESSION.get(url, timeout=30)
        if r.status_code == 429:
            time.sleep(2 ** i * 3); continue
        break
    if r.status_code != 200:
        return []
    html = r.text
    ids = []
    si = html.find('Similar apps')
    m = re.compile(r'"(/store/apps/collection/cluster\?gsr=[^"]+)"').search(html, si) if si >= 0 else None
    if m:
        curl = 'https://play.google.com' + m.group(1).replace('\\u003d', '=').replace('\\u0026', '&') + '&hl=en&gl=us'
        time.sleep(0.5)
        r2 = SESSION.get(curl, timeout=30)
        if r2.status_code == 200:
            ids = re.findall(r'/store/apps/details\?id=([A-Za-z0-9_.]+)', r2.text)
    out = []
    for x in ids:
        if x != app_id and x not in out:
            out.append(x)
    return out

def fetch(app_id):
    d = retry(app, app_id, lang='en', country='us')
    if not d:
        return None
    keep = ['appId', 'title', 'developer', 'developerId', 'genre', 'genreId', 'installs', 'minInstalls',
            'realInstalls', 'score', 'ratings', 'reviews', 'histogram', 'released', 'lastUpdatedOn', 'updated',
            'containsAds', 'offersIAP', 'inAppProductPrice', 'price', 'free', 'contentRating', 'summary',
            'description', 'developerWebsite', 'privacyPolicy', 'version', 'url']
    return {k: d.get(k) for k in keep}

def main():
    store = json.load(open(RAW)) if os.path.exists(RAW) else {}
    found = {}  # appId -> list of sources
    for q in QUERIES:
        res = retry(search, q, lang='en', country='us', n_hits=250) or []
        print(f'{q!r}: {len(res)}', flush=True)
        for rank, r in enumerate(res):
            found.setdefault(r['appId'], []).append(f'q:{q}#{rank+1}')
        time.sleep(1 + random.random())
    for aid in list(found):
        if aid not in store:
            d = fetch(aid)
            time.sleep(0.4 + random.random() * 0.4)
            if d:
                store[aid] = d
        if aid in store:
            store[aid]['found_by'] = sorted(set(store[aid].get('found_by', []) + found[aid]))
    json.dump(store, open(RAW, 'w'))
    print('after search:', len(store), flush=True)

    # one hop similar for top ~40 by installs (video-related only, to avoid browsers/players dragging noise? keep all top40)
    top = sorted(store.values(), key=lambda a: -(a.get('realInstalls') or a.get('minInstalls') or 0))
    # take top 40 among apps whose title/description mention download
    cand = [a for a in top if re.search(r'download|saver|save video', (a['title'] + ' ' + (a.get('summary') or '')), re.I)][:40]
    print('similar seeds:', [a['appId'] for a in cand], flush=True)
    for a in cand:
        sims = similar_ids(a['appId'])
        print(f"similar {a['appId']}: {len(sims)}", flush=True)
        time.sleep(1 + random.random())
        for s in sims:
            if s not in store:
                d = fetch(s)
                time.sleep(0.4 + random.random() * 0.4)
                if not d:
                    continue
                store[s] = d
                store[s]['found_by'] = []
            if f'similar:{a["appId"]}' not in store[s]['found_by']:
                store[s]['found_by'].append(f'similar:{a["appId"]}')
        json.dump(store, open(RAW, 'w'))
    print('total:', len(store))

if __name__ == '__main__':
    main()
