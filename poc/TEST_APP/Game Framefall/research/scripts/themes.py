"""Step 3b: classify reviews into themes with regexes -> data/reviews.json + data/review_stats.json"""
import json, os, re
from collections import Counter

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
APPS = os.path.join(BASE, 'data', 'apps.json')
RAW = os.path.join(BASE, 'data', 'raw_reviews.json')

I = re.I
NEG = r"(n'?t|not|never|no longer|stopp?ed|unable to|can ?not|cant|wont|dont|didnt|doesnt|isnt)"
THEMES = {
    'not_working': (
        r"(does ?n'?t|did ?n'?t|do ?n'?t|is ?n'?t|are ?n'?t|not|never|no longer|wo ?n'?t|ca ?n'?t|can ?not|cannot|unable to|fails? to|failed to|stop+ed|quit) "
        r"(\w+ ){0,2}(work|working|download|downloading|save|saving|load|loading|open|detect|find|function)"
        r"|stop+ed working|not working|doesnt work|dont work|didnt work|won'?t work|\bfail(ed|s|ing|ure)?\b|\berrors?\b|\buseless\b|\bbroken\b(?! into)|\bglitch|keeps? (crash|closing)|\bcrash(es|ed|ing)?\b"
        r"|nothing (happens|works)|no download|(video|link|url) (not|isn'?t) (found|supported|valid)|invalid (link|url)|stuck (at|on)|(?<![\d.])0 ?%|waste of (time|space|storage)|does nothing|\bdoesn'?t even\b|(option|button|feature) (is )?not (available|showing|there)|not even (play|load|open|download|show)|fake (app|application)"),
    'youtube': (r"\b(you ?tube|yt)\b"),  # refined below: complaint-ish context
    'platform_broken': (r"(doesn'?t|don'?t|won'?t|can'?t|not) (do|support|work (on|with|for)) (ig|insta(gram)?|tik ?tok|fb|face ?book|twitter|x|reels?)\b|"
        r"\b(insta(gram)?|ig|tik ?tok|face ?book|fb|twitter|x\.com|reels?|pinterest|snap ?chat|threads)\b[^.!?\n]{0,60}"
        r"(not (work|download|support|sav)|doesn'?t (work|download)|don'?t (work|download)|won'?t (work|download)|stop+ed|no longer|fail|error|can'?t|cannot|isn'?t working|broken|anymore)"
        r"|(not (working|downloading)|doesn'?t (work|download)|can'?t download|stop+ed working|no longer works?|unable to download|fails?|cannot download|won'?t download)[^.!?\n]{0,40}\b(from |on |for |with )?(insta(gram)?|ig|tik ?tok|face ?book|fb|twitter|reels?|pinterest)\b"),
    'ads': (r"\bads?\b|\badds\b|\badverts?\b|\badvertis|commercials?|pop-?ups?|\badd?s every\b|too many adds"),
    'paywall': (r"\bpremium\b|subscri|\bpay(ing|wall|ment)?\b|\bpaid\b|\bvip\b|\bpro version\b|\bpurchase|\bmoney\b|\bcharg(e|ed|ing)\b|\bfree trial\b|\$\d|\bprice\b|\bcost"),
    'slow': (r"\bslow(ly|er|est)?\b|takes? (forever|ages|so long|too long|a long time)|(?<!no )\blag(gy|s|ging)?\b|speed is (very |too |so )?(slow|low|bad)"),
    'quality': (r"low[- ]?(quality|res)|bad quality|poor quality|blurr?y|pixel+ated|no hd|not hd|only (360|480|720|sd)|\b(no|without|missing|lost) (sound|audio)|sound (is )?(missing|gone|not)|audio (is )?(missing|gone|not)|no audio|quality (is )?(bad|poor|low|terrible|drops)|(choose|select|pick) (the )?(quality|resolution)|resolution"),
    'privacy_vault': (r"private (folder|vault|space|album|file|gallery)|\bvault\b|hidden folder|secret folder|\block(ed)? (folder|videos?|files?)|folder (password|lock|pin)|(password|pin) (for|of|on) (the )?(private|folder|vault)|\bhide (my |the )?(videos?|files?|downloads?)"),
    'browser': (r"\bbrowser\b|\bbrows(e|ing)\b|in[- ]?app (web|search)|search engine|\bgoogle search\b|\bsearch bar\b"),
    'virus_malware': (r"(?<!no )(?<!zero )(?<!without )(?<!free of )\bvirus(?! ?free)|\bmalware|\bspyware|\btrojan|\bhack(ed|er|ing)?\b|\bscam\b|\bphishing|\bsuspicious|\bunsafe\b|\bredirect(s|ed|ing)? (me )?to|\bstole|\bdangerous"),
    'home_launcher': (r"\blauncher\b|home ?screen|default home|\bhome (app|page) (changed|replaced)|changed my (home|wallpaper|launcher)|lock ?screen|\bwallpaper\b"),
    'removed_or_banned': (r"(?<!be )(?<!get )removed from (the )?(play|google|store)|(app|it) (is|'s|came) back (on|in|to) (the )?(play|store|google)|(glad|happy) (it'?s|its|the app is|you'?re) back|taken down|\bbanned\b|disappeared from|got removed|was removed|deleted from (the )?(play ?store|store)|no longer (on|in) (the )?(play ?store|store)|re-?upload"),
    'praise': (r"\b(works?|working) (great|perfect(ly)?|fine|well|good|like a charm|flawless(ly)?|smooth(ly)?)\b|\bgreat app\b|\bgood app\b|\bbest app\b|\bawesome\b|\bexcellent\b|\blove (it|this|the app)\b|\bamazing\b|\bperfect\b|\bhighly recommend|\bvery (good|useful|helpful|nice)\b|\bnice app\b|\buseful app\b|\bfantastic\b|\bthe best\b"),
}
YT_COMPLAINT = (r"\b(you ?tube|yt)\b[^.!?\n]{0,60}(n'?t|not|no |cannot|can ?not|unable|won'?t|doesn'?t|isn'?t|support|block|restrict|allow|polic|except|wish|please|add|why)"
                r"|(n'?t|not|cannot|can ?not|unable to|won'?t|doesn'?t|no|except|wish|please add|add|why|only)[^.!?\n]{0,60}\b(you ?tube|yt)\b")
NEG_PRAISE = r"(not|n'?t|never|used to|was|were|use to) (\w+ ){0,2}(great|perfect|good|best|work(s|ed)? (great|fine|well|perfect))"

def themes_of(text, kind='other', score=None):
    t = text or ''
    out = []
    for k, p in THEMES.items():
        if k == 'youtube':
            if re.search(YT_COMPLAINT, t, I):
                out.append(k)
            continue
        if k == 'home_launcher':
            # hijack = the app took over the HOME role / launcher (not wallpaper changes)
            hijack = re.search(r"(changed|replaced|took over|takes over|hijack\w*|forced?|set (itself|as)|became|becomes|changes?|mess(ed|ing)? with|trying to be) [^.!?\n]{0,40}(home ?screen|launcher|default (home|app|launcher)|\bhome (app|page)\b)"
                               r"|(home ?screen|launcher)[^.!?\n]{0,40}(changed|replaced|took over|hijack|forced|by itself|on its own|without (my )?permission|switcheroo|stopped)"
                               r"|needs? to be a launcher|(took|takes|take|taking) (over|control of) (my|your|the|ur|whole|entire) (phone|device)|take its own control|chang\\w* (all )?(my |the |ur |your )?(whole |entire )?(screen layout|homepage|home page|layout|ui\\b|interface|apps arrangement)|layout redesigned|(hard|difficult|can'?t|cannot|unable) to (uninstall|remove it)|switcheroo|becomes? (the )?default (app|launcher|home)|\bdefault home\b|requires? you to make (it )?(your )?default", t, I)
            mention = re.search(r"\blauncher\b|home ?screen|default home", t, I)
            if kind == 'pure_launcher':
                ok = bool(hijack)
            elif kind == 'wallpaper':  # "set on home screen" is the product here; only launcher/default-app takeovers count
                ok = bool(hijack and re.search(r"launcher|default (home|app|launcher)|home app", hijack.group(0), I))
            else:  # video downloaders, hybrids, other apps: hijack phrasing, or a launcher/home-screen mention in a <=3-star review
                ok = bool(hijack or (mention and (score or 5) <= 3))
            if ok:
                out.append(k)
            continue
        tt = re.sub(r"(do ?n'?t|do not|never|please don'?t|pls don'?t) (ever )?(download|install|use|get) (this|that|the|it)\b", ' ', t, flags=I) if k == 'not_working' else t
        if re.search(p, tt, I):
            out.append(k)
    # comparative praise: "other apps didn't work, this one does"
    if 'not_working' in out and (score or 0) >= 4 and re.search(r"other apps?|previous app|those (apps|were)|tried (many|other|several|so many|a lot)|unlike|this one (works|worked)", t, I):
        out.remove('not_working')
    if 'praise' in out and re.search(NEG_PRAISE, t, I):
        out.remove('praise')
    return out

def main():
    apps = {a['appId']: a for a in json.load(open(APPS))}
    raw = json.load(open(RAW))
    allrev = []
    stats = {}
    glob = Counter(); gn = 0
    for aid, revs in raw.items():
        a = apps.get(aid, {})
        c = a.get('classification', {})
        if c.get('launcher_hybrid'):
            kind = 'hybrid'
        elif c.get('is_launcher_app'):
            kind = 'pure_launcher'
        elif 'video_wallpaper' in c.get('features', []) or re.search(r'wallpaper|widget', a.get('title') or '', I):
            kind = 'wallpaper'
        else:
            kind = 'other'
        cnt = Counter()
        for r in revs:
            th = themes_of(r['text'], kind, r['score'])
            r2 = dict(r, appId=aid, themes=th)
            allrev.append(r2)
            cnt.update(th)
        n = len(revs)
        if n == 0:
            continue
        gn += n; glob.update(cnt)
        stats[aid] = {'title': a.get('title'), 'kind': kind, 'is_video_downloader': c.get('is_video_downloader'), 'n': n, 'installs': a.get('realInstalls') or a.get('minInstalls'),
                      'avg_stars_sample': round(sum(r['score'] for r in revs) / n, 2),
                      'share': {k: round(cnt[k] / n, 3) for k in THEMES}}
    json.dump(allrev, open(os.path.join(BASE, 'data', 'reviews.json'), 'w'), indent=0, ensure_ascii=False)
    json.dump({'global_n': gn, 'global_share': {k: round(glob[k] / gn, 3) for k in THEMES}, 'per_app': stats},
              open(os.path.join(BASE, 'data', 'review_stats.json'), 'w'), indent=1, ensure_ascii=False)
    print('reviews', gn, 'apps', len(stats))
    for k in THEMES:
        print(f'{k:18s} {glob[k]/gn:.3f}')

if __name__ == '__main__':
    main()
