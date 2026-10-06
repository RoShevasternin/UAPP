#!/usr/bin/env python3
"""Encode the rendered WAVs into the Redwave test-media set, tag them, copy covers,
write catalog.json + indie-hour.rss and print a verification table.

    python build.py <wav_dir> [<test_media_dir>]

Needs: ffmpeg/ffprobe (libmp3lame, aac, flac, libvorbis) and `pip install mutagen`.
<wav_dir> is what `node render.js <wav_dir>` produced.
"""
import json
import os
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET
from datetime import datetime, timezone
from email.utils import format_datetime
from xml.sax.saxutils import escape

from mutagen.flac import FLAC, Picture
from mutagen.id3 import APIC, COMM, ID3, TALB, TCON, TDRC, TIT2, TPE1
from mutagen.mp4 import MP4, MP4Cover
from mutagen.oggvorbis import OggVorbis

BASE = "https://raw.githubusercontent.com/RoShevasternin/UAPP/main/poc/TEST_APP/Redwave/test-media/"
HERE = os.path.dirname(os.path.abspath(__file__))
IMG = os.path.normpath(os.path.join(HERE, "..", "..", "prototype", "img"))

ALBUM = "Redwave Test Media"
YEAR = "2026"
GENRE = "Lo-fi"
COMMENT = "Generated for Redwave testing · CC0"

# id = synth seed (render.js), wav = rendered file, out = published file name
TRACKS = [
    dict(id="t1", title="Neon Heart",     artist="Mira Vale",           cover="c-neon-heart.jpg",   wav="neon-heart",     out="neon-heart.mp3",     fmt="MP3",  mood=["Chill", "Lo-fi"]),
    dict(id="t2", title="Night Drive",    artist="Kaito Mori",          cover="c-night-drive.jpg",  wav="night-drive",    out="night-drive.mp3",    fmt="MP3",  mood=["Lo-fi"]),
    dict(id="k1", title="Stage Lights",   artist="The Northern Static", cover="c-stage-lights.jpg", wav="stage-lights",   out="stage-lights.mp3",   fmt="MP3",  mood=["Live", "Rock"]),
    dict(id="k4", title="Needle Drop",    artist="Crate Diggers Union", cover="c-needle-drop.jpg",  wav="needle-drop",    out="needle-drop.mp3",    fmt="MP3",  mood=["Lo-fi", "Chill"]),
    dict(id="k3", title="Blue Room",      artist="Sable Quartet",       cover="c-blue-room.jpg",    wav="blue-room",      out="blue-room.m4a",      fmt="M4A",  mood=["Chill", "Focus"]),
    dict(id="t3", title="Into the Smoke", artist="Ash & Ember",         cover="c-smoke.jpg",        wav="into-the-smoke", out="into-the-smoke.flac", fmt="FLAC", mood=["Focus"]),
    dict(id="k8", title="White Noise",    artist="Static Bloom",        cover="c-white-noise.jpg",  wav="white-noise",    out="white-noise.ogg",    fmt="OGG",  mood=["Focus", "Lo-fi"]),
    dict(id="t5", title="Bass Theory",    artist="Lowkey Lab",          cover="c-bass-theory.jpg",  wav="bass-theory",    out="bass-theory.wav",    fmt="WAV",  mood=["Workout"]),
]
EPISODES = [
    dict(id="e112", n=112, title="Ep. 112 · Bedroom pop on a budget", wav="ep112", out="podcast/indie-hour-ep112.mp3",
         date=datetime(2026, 10, 5, 7, 0, tzinfo=timezone.utc),
         summary="How to record a bedroom-pop EP with one mic and free plugins. (Test episode: 60 s of generated lo-fi audio.)"),
    dict(id="e111", n=111, title="Ep. 111 · Mixing vocals at home", wav="ep111", out="podcast/indie-hour-ep111.mp3",
         date=datetime(2026, 9, 28, 7, 0, tzinfo=timezone.utc),
         summary="EQ, compression and reverb for home vocals. (Test episode: 60 s of generated lo-fi audio.)"),
]
FEATURED = [
    dict(id="f-live",  title="Live Energy",    mood="Live",  cover="covers/stage-lights.jpg", subtitle="stage recordings"),
    dict(id="f-night", title="Night Drive",    mood="Lo-fi", cover="covers/night-drive.jpg",  subtitle="synth & lo-fi"),
    dict(id="f-vinyl", title="Vinyl Sessions", mood="Chill", cover="covers/needle-drop.jpg",  subtitle="warm & analog"),
]


def run(*cmd):
    subprocess.run(cmd, check=True)


def ffmpeg(*args):
    run("ffmpeg", "-hide_banner", "-loglevel", "error", "-y", *args)


def probe(path):
    out = subprocess.run(["ffprobe", "-v", "error", "-show_format", "-show_streams", "-of", "json", path],
                         check=True, capture_output=True, text=True).stdout
    return json.loads(out)


def cover_name(c):
    return c[2:] if c.startswith("c-") else c


def tag_id3(path, title, artist, album, genre, cover):
    tags = ID3()
    tags.add(TIT2(encoding=3, text=title))
    tags.add(TPE1(encoding=3, text=artist))
    tags.add(TALB(encoding=3, text=album))
    tags.add(TDRC(encoding=3, text=YEAR))          # becomes TYER after update_to_v23()
    tags.add(TCON(encoding=3, text=genre))
    tags.add(COMM(encoding=3, lang="eng", desc="", text=COMMENT))
    with open(cover, "rb") as f:
        tags.add(APIC(encoding=3, mime="image/jpeg", type=3, desc="Cover", data=f.read()))
    tags.update_to_v23()                           # TDRC -> TYER (save() alone does not convert frames)
    tags.save(path, v2_version=3, v1=0)            # v2.3 (UTF-16 text), no ID3v1


def picture(cover):
    p = Picture()
    p.type, p.mime, p.desc = 3, "image/jpeg", "Cover"
    p.width, p.height, p.depth = 480, 480, 24
    with open(cover, "rb") as f:
        p.data = f.read()
    return p


def main():
    wav_dir = os.path.abspath(sys.argv[1])
    out_dir = os.path.abspath(sys.argv[2] if len(sys.argv) > 2 else os.path.join(HERE, ".."))
    os.makedirs(os.path.join(out_dir, "covers"), exist_ok=True)
    os.makedirs(os.path.join(out_dir, "podcast"), exist_ok=True)
    W = lambda n: os.path.join(wav_dir, n + ".wav")
    O = lambda n: os.path.join(out_dir, n)

    # covers
    for t in TRACKS:
        shutil.copyfile(os.path.join(IMG, t["cover"]), O("covers/" + cover_name(t["cover"])))
    shutil.copyfile(os.path.join(IMG, "c-indie-hour.jpg"), O("covers/indie-hour.jpg"))

    common = ["-map_metadata", "-1", "-fflags", "+bitexact", "-flags:a", "+bitexact"]
    for t in TRACKS:
        src, dst, cov = W(t["wav"]), O(t["out"]), os.path.join(IMG, t["cover"])
        if t["fmt"] == "MP3":
            ffmpeg("-i", src, *common, "-c:a", "libmp3lame", "-b:a", "128k", "-id3v2_version", "0", dst)
            tag_id3(dst, t["title"], t["artist"], ALBUM, GENRE, cov)
        elif t["fmt"] == "M4A":
            ffmpeg("-i", src, *common, "-c:a", "aac", "-b:a", "128k", "-movflags", "+faststart", dst)
            m = MP4(dst)
            m.delete()
            m["\xa9nam"], m["\xa9ART"], m["\xa9alb"] = [t["title"]], [t["artist"]], [ALBUM]
            m["\xa9day"], m["\xa9gen"], m["\xa9cmt"] = [YEAR], [GENRE], [COMMENT]
            with open(cov, "rb") as f:
                m["covr"] = [MP4Cover(f.read(), imageformat=MP4Cover.FORMAT_JPEG)]
            m.save()
        elif t["fmt"] == "FLAC":
            ffmpeg("-i", src, *common, "-c:a", "flac", "-compression_level", "8", dst)
            m = FLAC(dst)
            m.delete()
            m.clear_pictures()
            for k, v in dict(TITLE=t["title"], ARTIST=t["artist"], ALBUM=ALBUM, DATE=YEAR, GENRE=GENRE, COMMENT=COMMENT).items():
                m[k] = v
            m.add_picture(picture(cov))
            m.save()
        elif t["fmt"] == "OGG":
            ffmpeg("-i", src, *common, "-c:a", "libvorbis", "-q:a", "4", dst)
            m = OggVorbis(dst)
            m.delete()
            for k, v in dict(TITLE=t["title"], ARTIST=t["artist"], ALBUM=ALBUM, DATE=YEAR, GENRE=GENRE, COMMENT=COMMENT).items():
                m[k] = v
            m.save()
        elif t["fmt"] == "WAV":
            shutil.copyfile(src, dst)  # 16-bit PCM, plain 44-byte header, no LIST/INFO tags

    # untitled file: no ID3 at all -> app must take the title from the file name
    ffmpeg("-i", W("untitled-demo"), *common, "-c:a", "libmp3lame", "-b:a", "128k", "-id3v2_version", "0",
           O("untitled_demo-track.mp3"))

    # podcast episodes: mono 64 kbps
    for e in EPISODES:
        dst = O(e["out"])
        ffmpeg("-i", W(e["wav"]), *common, "-ac", "1", "-c:a", "libmp3lame", "-b:a", "64k", "-id3v2_version", "0", dst)
        tag_id3(dst, e["title"], "Indie Hour", "Indie Hour (test feed)", "Podcast", O("covers/indie-hour.jpg"))

    # catalog.json
    def dur(p):
        return float(probe(p)["format"]["duration"])

    cat = {"version": 1, "tracks": [], "featured": []}
    for t in TRACKS:
        p = O(t["out"])
        cat["tracks"].append(dict(id=t["id"], title=t["title"], artist=t["artist"], license="CC0", mood=t["mood"],
                                  durationSec=round(dur(p)), sizeBytes=os.path.getsize(p), format=t["fmt"],
                                  url=BASE + t["out"], cover=BASE + "covers/" + cover_name(t["cover"])))
    for f in FEATURED:
        n = sum(1 for t in TRACKS if f["mood"] in t["mood"])
        cat["featured"].append(dict(id=f["id"], title=f["title"],
                                    subtitle=f"{n} track{'s' if n != 1 else ''} · {f['subtitle']}",
                                    mood=f["mood"], cover=BASE + f["cover"]))
    with open(O("catalog.json"), "w", encoding="utf-8") as fh:
        json.dump(cat, fh, ensure_ascii=False, indent=2)
        fh.write("\n")
    json.load(open(O("catalog.json"), encoding="utf-8"))

    # indie-hour.rss
    img = BASE + "covers/indie-hour.jpg"
    link = "https://github.com/RoShevasternin/UAPP/tree/main/poc/TEST_APP/Redwave/test-media"
    items = []
    for e in EPISODES:
        p = O(e["out"])
        s = round(dur(p))
        items.append(f"""    <item>
      <title>{escape(e['title'])}</title>
      <description>{escape(e['summary'])}</description>
      <guid isPermaLink="false">redwave-test-indie-hour-{e['id']}</guid>
      <pubDate>{format_datetime(e['date'], usegmt=True)}</pubDate>
      <enclosure url="{BASE + e['out']}" length="{os.path.getsize(p)}" type="audio/mpeg"/>
      <itunes:duration>{s // 3600:02d}:{s % 3600 // 60:02d}:{s % 60:02d}</itunes:duration>
      <itunes:episode>{e['n']}</itunes:episode>
      <itunes:episodeType>full</itunes:episodeType>
      <itunes:explicit>false</itunes:explicit>
      <itunes:image href="{img}"/>
    </item>""")
    rss = f"""<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd" xmlns:atom="http://www.w3.org/2005/Atom">
  <channel>
    <title>Indie Hour (test feed)</title>
    <link>{link}</link>
    <atom:link href="{BASE}indie-hour.rss" rel="self" type="application/rss+xml"/>
    <description>Test podcast feed for the Redwave app: two short episodes of generated lo-fi audio (CC0). Not a real show.</description>
    <language>en</language>
    <copyright>CC0 1.0 — generated for Redwave testing</copyright>
    <lastBuildDate>{format_datetime(EPISODES[0]['date'], usegmt=True)}</lastBuildDate>
    <image>
      <url>{img}</url>
      <title>Indie Hour (test feed)</title>
      <link>{link}</link>
    </image>
    <itunes:author>Indie Hour</itunes:author>
    <itunes:summary>Test podcast feed for the Redwave app.</itunes:summary>
    <itunes:image href="{img}"/>
    <itunes:category text="Music"/>
    <itunes:explicit>false</itunes:explicit>
    <itunes:type>episodic</itunes:type>
{chr(10).join(items)}
  </channel>
</rss>
"""
    with open(O("indie-hour.rss"), "w", encoding="utf-8") as fh:
        fh.write(rss)
    ET.parse(O("indie-hour.rss"))

    # report
    print(f"{'file':34} {'codec':10} {'ch':>2} {'sr':>6} {'kbps':>5} {'sec':>6} {'bytes':>9} pic  tags")
    files = [t["out"] for t in TRACKS] + ["untitled_demo-track.mp3"] + [e["out"] for e in EPISODES]
    for f in files:
        j = probe(O(f))
        a = next(s for s in j["streams"] if s["codec_type"] == "audio")
        pic = any(s.get("disposition", {}).get("attached_pic") for s in j["streams"])
        tags = {**j["format"].get("tags", {}), **a.get("tags", {})}
        tags = {k.lower(): v for k, v in tags.items()}
        br = int(j["format"].get("bit_rate", 0)) // 1000
        print(f"{f:34} {a['codec_name']:10} {a['channels']:>2} {a['sample_rate']:>6} {br:>5} "
              f"{float(j['format']['duration']):6.2f} {os.path.getsize(O(f)):>9} {'yes' if pic else 'no ':3}  "
              + ", ".join(f"{k}={tags[k]}" for k in ("title", "artist", "album", "date", "genre", "comment") if k in tags))
    total = 0
    for root, dirs, fs in os.walk(out_dir):
        for f in fs:
            total += os.path.getsize(os.path.join(root, f))
    print(f"TOTAL test-media: {total} bytes ({total / 1048576:.2f} MiB)")


if __name__ == "__main__":
    main()
