"""Step 5: build tables used in SUMMARY.md -> data/analysis.md (raw tables) + data/analysis.json"""
import json, os, re, random
from collections import Counter

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
D = lambda f: os.path.join(BASE, 'data', f)
apps = json.load(open(D('apps.json')))
byid = {a['appId']: a for a in apps}
revs = json.load(open(D('reviews.json')))
rstats = json.load(open(D('review_stats.json')))
names = json.load(open(D('names.json')))

def inst(a):
    return a.get('realInstalls') or a.get('minInstalls') or 0

def fmt(n):
    if n is None:
        return '-'
    if n >= 1e9: return f'{n/1e9:.1f}B'
    if n >= 1e6: return f'{n/1e6:.1f}M'
    if n >= 1e3: return f'{n/1e3:.0f}K'
    return str(n)

def bucket(m):
    m = m or 0
    for lim, lab in ((1e8, '100M+'), (1e7, '10M–100M'), (1e6, '1M–10M'), (1e5, '100K–1M'), (1e4, '10K–100K')):
        if m >= lim: return lab
    return '<10K'

out = []
P = out.append
dl = [a for a in apps if a['classification']['is_video_downloader']]
dl.sort(key=lambda a: -inst(a))
P(f'## 1. Set\napps total: {len(apps)}; true video downloaders: {len(dl)}; general download managers: {sum(a["classification"]["is_general_download_manager"] for a in apps)}; secondary downloaders (player/browser w/ download feature): {sum(a["classification"]["secondary_downloader"] for a in apps)}')
src = Counter('search' if any(x.startswith('q:') for x in a.get('found_by', [])) else 'similar' for a in apps)
P(f'source: {dict(src)}; DL from search: {sum(1 for a in dl if any(x.startswith("q:") for x in a.get("found_by", [])))}')
P('subtypes among DL: ' + str(Counter(a['classification']['subtype'] for a in dl)))
order = ['100M+', '10M–100M', '1M–10M', '100K–1M', '10K–100K', '<10K']
ba = Counter(bucket(a['minInstalls']) for a in apps); bd = Counter(bucket(a['minInstalls']) for a in dl)
P('| installs bucket | all apps | video downloaders |\n|---|---|---|')
for b in order: P(f'| {b} | {ba[b]} | {bd[b]} |')
ga = Counter(a['genre'] for a in apps); gd = Counter(a['genre'] for a in dl)
P('| category | all apps | video downloaders |\n|---|---|---|')
for g, n in gd.most_common(): P(f'| {g} | {ga[g]} | {n} |')
P(f'| (other categories, all apps) | {sum(n for g, n in ga.items() if g not in gd)} | 0 |')
def share(xs, f): return f'{sum(1 for a in xs if f(a))}/{len(xs)} ({100*sum(1 for a in xs if f(a))/max(len(xs),1):.0f}%)'
for lab, xs in (('all', apps), ('DL', dl), ('DL >=1M', [a for a in dl if a['minInstalls'] >= 1e6])):
    P(f'{lab}: ads {share(xs, lambda a: a["containsAds"])}, IAP {share(xs, lambda a: a["offersIAP"])}, paid {share(xs, lambda a: (a.get("price") or 0) > 0)}')
P(f'sum installs DL (realInstalls where given): {fmt(sum(inst(a) for a in dl))}; realInstalls available for {sum(1 for a in dl if a.get("realInstalls"))}/{len(dl)}')
P(f'content ratings DL: {Counter(a["contentRating"] for a in dl)}')

P('\n## 2. Top 25 DL')
P('| # | appId | title | developer | installs | score | released | updated | category | platforms named | notable features |\n|---|---|---|---|---|---|---|---|---|---|---|')
for i, a in enumerate(dl[:25], 1):
    c = a['classification']
    feats = [f for f in c['features'] if f not in ('hd_4k',)]
    P(f"| {i} | `{a['appId']}` | {a['title']} | {a['developer']} | {fmt(inst(a))} | {a['score'] and round(a['score'],2)} | {c['released_iso']} | {c.get('updated_iso')} | {a['genre']} | {', '.join(c['platforms']) or '-'} | {', '.join(feats)} |")

P('\n## 3. Fast risers (released >= 2025-10-01, >=100K installs) — all apps in set')
fr = [a for a in apps if (a['classification']['released_iso'] or '') >= '2025-10-01' and inst(a) >= 1e5]
fr.sort(key=lambda a: -inst(a) / a['classification']['months_since_release'])
P('| appId | title | developer | DL? | installs | released | months | installs/month | score | ratings | category |\n|---|---|---|---|---|---|---|---|---|---|---|')
for a in fr:
    c = a['classification']
    P(f"| `{a['appId']}` | {a['title']} | {a['developer']} | {('Y' if c['is_video_downloader'] else 'n') + (' +launcher' if c['launcher_hybrid'] else '')} | {fmt(inst(a))} | {c['released_iso']} | {c['months_since_release']} | {fmt(int(inst(a)/c['months_since_release']))} | {a['score'] and round(a['score'],2)} | {a['ratings']} | {a['genre']} |")
fr2 = [a for a in apps if (a['classification']['released_iso'] or '') >= '2025-10-01' and inst(a) >= 1e4 and inst(a) < 1e5 and a['classification']['is_video_downloader']]
P('DL released since 2025-10 with 10K–100K: ' + '; '.join(f"{a['title']} ({fmt(inst(a))}, {a['classification']['released_iso']})" for a in sorted(fr2, key=lambda a: -inst(a))))
P('DL release-year distribution: ' + str(sorted(Counter((a['classification']['released_iso'] or 'none')[:4] for a in dl).items())))

P('\n## 4. YouTube stance + brands in title')
for lab, xs in (('top25', dl[:25]), ('top50', dl[:50]), ('all DL', dl)):
    P(f"{lab}: {dict(Counter(a['classification']['youtube_stance'] for a in xs))}; brand in title {share(xs, lambda a: a['classification']['brand_in_title'])}")
P('brands used in titles (DL): ' + str(Counter(b for a in dl for b in a['classification']['brands_in_title']).most_common()))
P('"tube" in title without "youtube" (DL): ' + '; '.join(f"{a['title']} ({fmt(inst(a))})" for a in dl if re.search(r'tube', a['title'], re.I) and not re.search('you ?tube', a['title'], re.I)))
P('YouTube named as supported: ' + '; '.join(f"{a['appId']} ({fmt(inst(a))})" for a in dl if a['classification']['youtube_stance'] == 'named_as_supported'))
P('platforms named (DL): ' + str(Counter(p for a in dl for p in a['classification']['platforms']).most_common()))

P('\n## 5. Feature frequency (DL)')
fc = Counter(f for a in dl for f in a['classification']['features'])
top50 = dl[:50]
fc50 = Counter(f for a in top50 for f in a['classification']['features'])
P('| feature | all DL (n=%d) | top-50 DL |\n|---|---|---|' % len(dl))
for f, n in fc.most_common(): P(f'| {f} | {n} ({100*n/len(dl):.0f}%) | {fc50[f]} ({100*fc50[f]/len(top50):.0f}%) |')

P('\n## 6. Review themes')
THEMES = list(rstats['global_share'])
pa = rstats['per_app']
def grp(name, pred):
    rs = [r for r in revs if pred(r)]
    return name, rs
groups = [grp('video downloaders', lambda r: byid.get(r['appId'], {}).get('classification', {}).get('is_video_downloader')),
          grp('launcher hybrids (DL/music/volume + launcher)', lambda r: byid.get(r['appId'], {}).get('classification', {}).get('launcher_hybrid')),
          grp('pure launchers', lambda r: pa.get(r['appId'], {}).get('kind') == 'pure_launcher'),
          grp('wallpaper/widget apps', lambda r: pa.get(r['appId'], {}).get('kind') == 'wallpaper')]
P('| theme | ' + ' | '.join(f'{g} (n={len(rs)}, apps={len(set(r["appId"] for r in rs))})' for g, rs in groups) + ' | DL 1–2★ only | DL 4–5★ only |')
P('|---' * (len(groups) + 3) + '|')
dlr = groups[0][1]
lo = [r for r in dlr if r['score'] <= 2]; hi = [r for r in dlr if r['score'] >= 4]
for k in THEMES:
    P(f'| {k} | ' + ' | '.join(f"{100*sum(k in r['themes'] for r in rs)/max(len(rs),1):.1f}%" for g, rs in groups) + f" | {100*sum(k in r['themes'] for r in lo)/max(len(lo),1):.1f}% | {100*sum(k in r['themes'] for r in hi)/max(len(hi),1):.1f}% |")
revs_all = revs
revs = dlr
P(f'DL reviews: n={len(dlr)} from {len(set(r["appId"] for r in dlr))} apps; date range {min(r["date"] for r in dlr)}..{max(r["date"] for r in dlr)}; median date {sorted(r["date"] for r in dlr)[len(dlr)//2]}')
P(f'DL star mix in sample: {dict(sorted(Counter(r["score"] for r in dlr).items()))}')
P(f'1–2★ share of sampled reviews: {100*len(lo)/len(revs):.1f}%; reviews without any theme: {100*sum(not r["themes"] for r in revs)/len(revs):.1f}%')
pa = rstats['per_app']
elig = {k: v for k, v in pa.items() if v['n'] >= 50 and v.get('is_video_downloader')}
P('home_launcher theme, all apps with any hit: ' + '; '.join(f"{v['title']} ({k}) {100*v['share']['home_launcher']:.1f}% of {v['n']}" for k, v in sorted(pa.items(), key=lambda kv: -kv[1]['share']['home_launcher']) if v['share']['home_launcher'] > 0))
for th in ('not_working', 'ads', 'paywall', 'platform_broken', 'youtube', 'virus_malware'):
    P(f'\nTop 10 by {th} (apps with >=50 sampled reviews):\n| appId | title | installs | n | {th} | avg★ sample |\n|---|---|---|---|---|---|')
    for aid, v in sorted(elig.items(), key=lambda kv: -kv[1]['share'][th])[:10]:
        if v['share'][th] == 0: break
        P(f"| `{aid}` | {v['title']} | {fmt(v['installs'])} | {v['n']} | {100*v['share'][th]:.1f}% | {v['avg_stars_sample']} |")
random.seed(7)
P('\nQuotes:')
for th in ('not_working', 'youtube', 'platform_broken', 'ads', 'paywall', 'quality', 'slow', 'virus_malware', 'privacy_vault', 'browser', 'home_launcher', 'removed_or_banned', 'praise'):
    pool = revs_all if th == 'home_launcher' else revs
    xs = [r for r in pool if th in r['themes'] and 25 <= len(r['text']) <= 260]
    xs.sort(key=lambda r: -(r['thumbsUp'] or 0))
    seen, picked = set(), []
    for r in xs:
        if r['appId'] in seen: continue
        seen.add(r['appId']); picked.append(r)
        if len(picked) >= 9: break
    P(f'\n### {th} (n={sum(th in r["themes"] for r in pool)})')
    for r in picked:
        P(f"- \"{r['text'].strip()}\" — `{r['appId']}`, {r['score']}★, {r['date']}, 👍{r['thumbsUp']}")

P('\n## 7. Video + launcher / home screen / video wallpaper')
vl = [a for a in apps if a['classification']['launcher_hybrid'] or (a['classification']['is_launcher_app'] and a['genreId'] == 'PERSONALIZATION') or (a['classification']['is_video_downloader'] and ('video_wallpaper' in a['classification']['features'] or a['classification']['mentions_launcher'])) or ('video_wallpaper' in a['classification']['features'] and re.search('video', a['title'], re.I) and a['genreId'] == 'PERSONALIZATION')]
vl.sort(key=lambda a: -inst(a))
P('| appId | title | developer | installs | score | ratings | released | DL? | hybrid? | launcher? | disclosure? | video wallpaper? | summary | review n | not_working | ads | home_launcher | virus |\n|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|')
for a in vl:
    c = a['classification']; s = pa.get(a['appId'])
    sh = (lambda k: f"{100*s['share'][k]:.1f}%" if s else '-')
    P(f"| `{a['appId']}` | {a['title']} | {a['developer']} | {fmt(inst(a))} | {a['score'] and round(a['score'],2)} | {a['ratings']} | {c['released_iso']} | {'Y' if c['is_video_downloader'] else ''} | {'Y' if c['launcher_hybrid'] else ''} | {'Y' if c['is_launcher_app'] or 'launcher_home' in c['features'] else ''} | {'Y' if c['launcher_disclosure'] else ''} | {'Y' if 'video_wallpaper' in c['features'] else ''} | {(a['summary'] or '')[:90]} | {s['n'] if s else '-'} | {sh('not_working')} | {sh('ads')} | {sh('home_launcher')} | {sh('virus_malware')} |")

P('\n## 8. Names')
P('| candidate | collisions (title / developer / installs / match in) |\n|---|---|')
for n, v in names.items():
    cs = v['collisions']
    txt = '; '.join(h['title'] + ' / ' + h['developer'] + ' / ' + str(h['installs']) + ' / ' + ','.join(h['match_in']) for h in cs) or 'none'
    P(f"| {n} | {txt} |")

open(D('analysis.md'), 'w').write('\n'.join(out))
print('\n'.join(out)[:3000])
