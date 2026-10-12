"""Step 2: classify apps from title + description -> data/apps.json"""
import json, os, re
from datetime import datetime, timezone

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RAW = os.path.join(BASE, 'data', 'raw_apps.json')
OUT = os.path.join(BASE, 'data', 'apps.json')

I = re.I
PLATFORMS = {
    'youtube': r'\byou ?tube\b|\byt\b(?! ?music)',
    'instagram': r'\binsta(gram)?\b|\big\b(?= ?(reels?|stor|video|post|download))|\breels?\b',
    'facebook': r'\bface ?book\b|\bfb\b',
    'tiktok': r'\btik ?tok\b|\btt\b(?= ?video)',
    'twitter_x': r'\btwitter\b|\btweets?\b|\bx\.com\b|\bx ?\(twitter\)|\(formerly twitter\)|\btwitter ?x\b',
    'whatsapp': r'\bwhats ?app\b|\bwa status|\bwhatsapp business\b|\bstatus saver\b',
    'vimeo': r'\bvimeo\b',
    'dailymotion': r'\bdaily ?motion\b',
    'pinterest': r'\bpinterest\b',
    'snapchat': r'\bsnap ?chat\b',
    'telegram': r'\btelegram\b',
    'likee': r'\blikee\b',
    'twitch': r'\btwitch\b',
    'others': r'\b(tumblr|reddit|linkedin|imgur|9gag|bilibili|vk\b|vkontakte|ok\.ru|soundcloud|bandcamp|kwai|moj\b|josh\b|sharechat|roposo|chingari|mx takatak|triller|threads|bluesky|rumble|ted\b|bigo|helo\b|snack ?video|imdb|flickr|vine|douyin|weibo|zili|lemon8|capcut)\b',
}
YT_NOT_SUPPORTED = (r'(you ?tube|\byt\b)[^.\n]{0,80}\b(not (supported|support|allowed|available|possible)|is prohibited|are prohibited|can ?not|cannot|can\'t|unable|doesn\'t|does not|don\'t|do not|restrict|prohibit|polic(y|ies)|terms of service|tos\b|excluded)'
                    r'|(not (support|allow)(ed)?|cannot|can\'t|unable to|doesn\'t|does not|do not|don\'t|prohibited|no) (to )?(download(ing)?|save|saving|support)?[^.\n]{0,40}(from )?(you ?tube|\byt\b)'
                    r'|(except|excluding|other than|apart from|but not)[^.\n]{0,15}(you ?tube|\byt\b)'
                    r'|(you ?tube|\byt\b)[^.\n]{0,20}(excluded|is not included)')
BRAND_IN_TITLE = r'\b(you ?tube|insta(gram)?|face ?book|\bfb\b|tik ?tok|twitter|whats ?app|vimeo|daily ?motion|pinterest|snap ?chat|telegram|likee|twitch|reels?|tumblr|reddit|threads|twitter ?x|for x)\b'
FEATURES = {
    'built_in_browser': r'built[- ]?in (web )?browser|in[- ]?app browser|(private|incognito|fast|secure|web|integrated|own|embedded) browser|browser (with|to|and)|browse (the )?(web|internet|sites|websites|any|and|&)|surf (the )?(web|internet)|^[^\n]*browser',
    'auto_detect': r'auto(matic(ally)?)?[- ]?detect|detects? (videos?|media) (automatically|on)|automatic(ally)? (recogni[sz]e|find|sniff|detect)|smart detect|sniff|auto[- ]?download',
    'm3u8_hls': r'\bm3u8\b|\bhls\b|live ?stream download|\bmpd\b|\bdash stream',
    'background_download': r'background download|download(s|ing)? in (the )?background|in the background|while (you )?(browse|browsing|using other apps)',
    'multiple_downloads': r'multiple (downloads?|videos?|files?) (at (the same|one) time|simultaneous|at once|concurrent)|(simultaneous|parallel|concurrent|batch|bulk) download|download (multiple|several|many) (videos?|files?)( at once| simultaneous| at the same time)?|multi[- ]?(thread|task)(ed|ing)? download',
    'private_vault': r'private (folder|vault|space|album|gallery|downloads?|files?)|\bvault\b|password[- ]?protect|password|lock (your )?(videos?|files|folder)|\bpin (code|lock)\b|hide (your )?(videos?|downloads?|files)|safe folder|secret folder|fingerprint',
    'incognito_browser': r'incognito|private (browser|browsing|mode|tab)',
    'video_player': r'(video|media|hd|built[- ]?in|powerful|mp4) player|play (videos? )?offline|watch (them|videos?)? ?offline|player for',
    'mp3_audio': r'\bmp3\b|extract (the )?audio|convert[^.\n]{0,25}(audio|music)|audio (converter|extract)|to audio\b|music download',
    'cutter_trim': r'\bcutter\b|\btrim(mer|ming)?\b|\bcut (videos?|clips?)\b|\bclip (your )?videos?\b|\bvideo cutter\b',
    'gif': r'\bgifs?\b',
    'status_saver': r'status saver|save (whats ?app )?status|statuses|story saver|save stor(y|ies)|stories? download',
    'pip': r'picture[- ]in[- ]picture|\bpip\b|floating (window|player|video|popup)|pop[- ]?up (play|window)',
    'video_wallpaper': r'(video|live|animated) wallpapers?|wallpaper video|set (a |any )?video as (your )?(wall|lock|home)',
    'launcher_home': r'\blauncher\b|default home( app| screen)?|home ?screen (app|replacement|launcher)|replace (your )?(home ?screen|launcher)|set (it )?as (your )?(default )?(home|launcher)',
    'watermark_free': r'(no|without|remove|free of|w/o) (the )?watermarks?|watermark[- ]free',
    'hd_4k': r'\b(hd|4k|1080p?|720p?|full hd|uhd|2k|8k)\b',
    'adblock': r'ad ?block|block (pop-?up )?ads|ads? blocker|block annoying ads',
    'cast_tv': r'\bcast\b|chromecast|to tv\b|smart tv|screen mirror',
}
LAUNCHER = r'\blauncher\b|default home( app| screen)?|home ?screen (app|replacement|launcher)|replace (your )?(home ?screen|launcher)|set (it )?as (your )?(default )?(home|launcher)'
# core-purpose downloader: title or summary or first part of description speaks of downloading/saving videos
DL_CORE = (r'video ?download|downloader|download (any |all |hd |your |the |online |social |free )?(videos?|reels?|stor(y|ies)|statuse?s?|media|clips?|movies?)'
           r'|save (any |all |hd |your |the |online )?(videos?|reels?|stor(y|ies)|statuse?s?|media|clips?)|video saver|story saver|status saver|reels? saver|\bsaver\b|repost|save & repost')

TITLE_EXTRA = r'download \w+ videos?|videosaver|savevideo|download no watermark|\bsaver\b|\bsave\w* (video|reel|story|status)|downloader'
GENERAL_DM = {'com.esaba.downloader', 'com.tech.downloader', 'com.downloader.tv.installer.xapk', 'com.carsu.download.downloader', 'com.dv.adm', 'org.freedownloadmanager.fdm', 'com.tdm.manager', 'com.flagg.manager.download.tv'}
FORCE_NOT_DL = {'com.lenovo.anyshare.gps', 'com.photobox.nbexppll'}

def youtube_stance(full):
    sents = [x for x in re.split(r'(?<=[.!?])\s+|\n|<br>|•', full) if re.search(PLATFORMS['youtube'], x, I)]
    if not sents:
        return 'not_mentioned'
    kinds = set()
    for x in sents:
        if re.search(YT_NOT_SUPPORTED, x, I) or re.search(r'prohibit|not (allowed|permitted)|restricted|violat|against', x, I):
            kinds.add('not_supported')
        elif re.search(r'affiliat|trademark|property of|endorse|sponsor|belong|associated|registered|owned by|brand', x, I):
            kinds.add('disclaimer_only')
        else:
            kinds.add('named_as_supported')
    for k in ('not_supported', 'named_as_supported', 'disclaimer_only'):
        if k in kinds:
            return k

def hit(p, text):
    return bool(re.search(p, text, I))

def months_since(datestr, ref=datetime(2026, 10, 12)):
    if not datestr:
        return None
    for fmt in ('%b %d, %Y', '%d %b %Y'):
        try:
            d = datetime.strptime(datestr, fmt)
            return max((ref - d).days / 30.44, 0.5), d.strftime('%Y-%m-%d')
        except ValueError:
            pass
    return None

def classify(a):
    title = a.get('title') or ''
    summ = a.get('summary') or ''
    desc = a.get('description') or ''
    full = f'{title}\n{summ}\n{desc}'
    head = f'{title}\n{summ}\n{desc[:600]}'
    c = {}
    # core purpose: title says downloader/saver (tier "title"), or summary does and the title is not a player/editor/browser
    t_dl = hit(DL_CORE, title) or hit(TITLE_EXTRA, title)
    s_dl = hit(DL_CORE, summ)
    other_core = hit(r'\b(player|recorder|editor|browser|converter|mp3|vpn|cutter|maker)\b', title) or a.get('genreId') == 'COMMUNICATION'
    general_dm = hit(r'download manager|torrent|\bfiles? download|\bIDM\b', title) or a['appId'] in GENERAL_DM
    not_video = hit(r'\b(music|mp3|song|audio|ringtone|torrent|apk|file|wallpaper|photo|image|pdf|book|subtitle)s? ?(download|saver)', title) and not hit(r'video|reel|story|stories|status|tiktok|insta|clip', title)
    if a['appId'] in FORCE_NOT_DL:
        tier = None
    elif t_dl and not not_video and not general_dm and not hit(r'\bbrowser\b', title) or (hit(r'\bbrowser\b', title) and hit(r'video ?download', title)):
        tier = 'title'
    elif s_dl and not other_core and not general_dm and not not_video:
        tier = 'summary'
    else:
        tier = None
    c['is_video_downloader'] = tier is not None
    c['downloader_evidence'] = tier
    c['is_general_download_manager'] = bool(general_dm)
    c['secondary_downloader'] = (not c['is_video_downloader']) and bool(s_dl or hit(r'video ?download(er|ing)?|download (any |all |online )?videos?', desc))
    if not c['is_video_downloader']:
        c['subtype'] = None
    elif hit(r'status', title) and not hit(r'video ?downloader|reel|story|stories', title):
        c['subtype'] = 'whatsapp_status_saver'
    elif hit(r'm3u8|\bhls\b', title):
        c['subtype'] = 'm3u8_hls'
    elif hit(BRAND_IN_TITLE + r'|\btt\b|\bins\b|\big\b|stor(y|ies)', title):
        c['subtype'] = 'social_single_or_story'
    else:
        c['subtype'] = 'general'
    c['platforms'] = sorted(k for k, p in PLATFORMS.items() if hit(p, full))
    c['youtube_stance'] = youtube_stance(full)
    c['says_youtube_not_supported'] = c['youtube_stance'] == 'not_supported'
    c['brand_in_title'] = hit(BRAND_IN_TITLE, title)
    c['brands_in_title'] = sorted(set(m.group(0).lower() for m in re.finditer(BRAND_IN_TITLE, title, I)))
    c['features'] = sorted(k for k, p in FEATURES.items() if hit(p, full))
    c['mentions_launcher'] = hit(LAUNCHER, full)
    c['is_launcher_app'] = bool(hit(r'\blauncher\b', title + ' ' + summ) or (hit(r'\bhome\b', title) and hit(r'\blauncher\b', desc)) or (a.get('genreId') == 'PERSONALIZATION' and hit(r'\blauncher\b', desc[:400])))
    c['launcher_hybrid'] = c['is_launcher_app'] and (c['is_video_downloader'] or c['secondary_downloader'] or hit(r'download|music|mp3|player|video|volume|sound|audio|browser', title + ' ' + summ)) and not hit(r'\btheme', title)
    c['launcher_disclosure'] = hit(r'(may|will|might) (change|reorganize|rearrange|replace|modify)[^.\n]{0,40}(home|layout|launcher)|layout of your home ?screen|as a launcher app|works as a launcher|this app is a launcher|set (it )?as (your )?default (home|launcher)|default home', desc)
    ms = months_since(a.get('released'))
    c['released_iso'] = ms[1] if ms else None
    c['months_since_release'] = round(ms[0], 1) if ms else None
    if a.get('updated'):
        c['updated_iso'] = datetime.fromtimestamp(a['updated'], timezone.utc).strftime('%Y-%m-%d')
    return c

def main():
    raw = json.load(open(RAW))
    out = []
    for aid, a in raw.items():
        a = dict(a)
        a['classification'] = classify(a)
        out.append(a)
    out.sort(key=lambda a: -(a.get('realInstalls') or a.get('minInstalls') or 0))
    json.dump(out, open(OUT, 'w'), indent=1, ensure_ascii=False)
    n = sum(a['classification']['is_video_downloader'] for a in out)
    print('apps', len(out), 'video downloaders', n)

if __name__ == '__main__':
    main()
