# Policy, legal sources and Android facts for a legal video downloader with a HOME role

Collected 12.10.2026 (web + live `curl` checks). ✔ = primary source read or checked live; ◌ = inference or secondary source.

## A. Google Play policy and platform terms

- ✔ The Developer Program Policy is "effective September 30, 2026": https://support.google.com/googleplay/android-developer/answer/18258653
- ✔ **IP → Encouraging infringement** (answer/9888072). It forbids "Streaming apps that allow users to download a local copy of copyrighted content without authorization". A store listing whose description or screenshots encourage such downloads also violates it. Movie and TV marketing images in listings are an example violation, so use only PD or CC footage in screenshots.
- ✔ **YouTube is not named in Play policy.** Enforcement goes through Device and Network Abuse: "Apps that access or use a service or API in a manner that violates its terms of service". Developer rejection reports cite exactly this ◌ (github.com/PierfrancescoSoffritti/android-youtube-player/issues/192).
- ✔ **Impersonation** (answer/9888374) and **Metadata** (answer/9898842):
  - No brand logos or names without permission.
  - Titles are limited to 30 characters.
  - A download symbol is allowed when the app really downloads ◌.
  - ◌ A Google trademark notice took down "InPlan - Planner for Instagram" in 2022 (news.ycombinator.com/item?id=33584470).
- ✔ **Disruptive ads** (answer/9857753). Example violations:
  - "Ads that are triggered by the home button or other features explicitly designed for exiting the app";
  - an ad appearing on the home screen after leaving the app;
  - interstitials that can't be closed after 15 s;
  - video interstitials before the splash screen;
  - lock-screen monetization.
- ✔ **Deceptive device settings changes.** Changes to "the presentation of apps on the homescreen" need the user's knowledge and consent and must be easily reversible.
- ✔ §3.2: the app must disclose download size before downloading.
- ✔ The **Photo/Video permissions** policy limits `READ_MEDIA_VIDEO` to cases the system pickers can't cover. Use the Photo Picker or `ACTION_VIEW` instead.
- ✔ **UGC policy:** clients for UGC platforms must moderate. This matters for PeerTube.
- ✔ **Platform terms:**
  - YouTube ToS forbids downloading "except … as expressly authorized by the Service". YouTube API policies forbid offline copies.
  - Instagram and Facebook forbid automated collection.
  - TikTok forbids downloading its content without consent.
  - X ToS (effective 09.10.2026) charges $15,000 per 1M posts against anyone who induces or "knowingly facilitate[s]" scraping.
- ✔ **Practice:** AFTVnews "Downloader" (50M+) was suspended twice in 2023 after DMCA notices because its built-in browser could open a piracy site. It was restored both times (torrentfreak.com/?p=235348).

## B. Legal video sources (live checks 12.10.2026)

| Source | Licence | Access | Verdict |
|---|---|---|---|
| Internet Archive | PD Mark / CC, `licenseurl` set by uploader ◌; skip `is_dark:true` items | no key; `archive.org/metadata/{id}`, `archive.org/download/{id}/{file}` → 302 → 200, byte ranges | curated whitelist |
| NASA Image Library | PD in US; insignia/logo not PD; no endorsement | no key; `images-api.nasa.gov`, `images-assets.nasa.gov/video/{id}/{id}~orig/~mobile.mp4` (https works) | yes, credit "NASA" |
| Blender open movies | CC BY 3.0 (BBB, Sintel, ToS), Spring CC BY 4.0 ◌; ToS soundtrack CC BY-ND 3.0 | `download.blender.org` (BBB now zip-only), Commons, Archive | yes, credits |
| Wikimedia Commons | per file via `extmetadata` | needs descriptive User-Agent (empty UA → 403), 429 on bursts; VP9 transcodes 240p–1080p | yes |
| PeerTube | `licence`, `downloadEnabled` | `/api/v1/videos/{uuid}` → `fileDownloadUrl` (302 → presigned, 24 h); 50 calls/10 s; separate audio/video files since 6.3 | v1.1, curated instances |
| Pixabay | no attribution; no mass/standalone redistribution | key; 100 req/min; cache 24 h; show "from Pixabay" | optional |
| Pexels | free licence, but "New API key issuance is currently paused"; wallpaper apps unsupported | key; 200/h | no |
| Coverr, Mixkit, Videvo/Freepik | ban competing services or redistribution | — | no |
| Vimeo | download only by owner; API without token → 401 | OAuth `video_files` | block |
| Cloud links, RSS | user's own files | Dropbox `dl=1`; Drive public link with virus-scan confirm ◌; OneDrive anonymous trick broke in 2024 ◌; RSS `video/*` enclosures (often tracking redirects) | yes, "only your own or with permission" |

UI duties:
- a source badge, licence badge and author on every item;
- credits saved with the file;
- the Save button hidden when PeerTube reports `downloadEnabled=false`;
- a neutral blocked screen without third-party logos.

## C. Android facts

- ✔ **Downloads:**
  - `DownloadManager` → `DIRECTORY_MOVIES` needs no storage permission on Android 10+.
  - MediaStore writes go to `RELATIVE_PATH Movies/<App>` with `IS_PENDING`.
  - User-initiated data transfer jobs (API 34) are meant for downloads.
  - On Android 15+, dataSync and mediaProcessing foreground services are capped at 6 h per 24 h.
- ✔ **HLS/DASH:**
  - Transformer officially supports only progressive inputs; adaptive streams are Known Issue #10943 (developer.android.com/media/media3/transformer/troubleshooting).
  - The Media3 offline `DownloadService` writes to a cache, not a file.
  - Plan: our own segment download → concat → Transformer remux to MP4 (no re-encode when formats match).
  - ffmpeg-kit is retired (github.com/arthenica/ffmpeg-kit).
- ✔ **Refuse protected streams:**
  - HLS: any `EXT-X-KEY` METHOD other than NONE, or `EXT-X-SESSION-KEY` (RFC 8216).
  - DASH: `ContentProtection` / Widevine `edef8ba9-79d6-4ace-a3c8-27dcd51d21ed`, PlayReady `9a04f079-…`, FairPlay `94ce86fb-…` (dashif.org).
  - MP4: `encv`/`enca`/`pssh` boxes ◌.
- ✔ **Media3 1.11.1** (10.09.2026):
  - Muxers: `InAppMp4Muxer` is the default; WebM, Ogg and Wav muxers need a custom `Muxer.Factory`.
  - Track removal: `setRemoveAudio`, and `setRemoveVideo` (1.11).
  - Trimming: `ClippingConfiguration`. Trim optimisation works only for single MP4 inputs, and the MP4 edit-list trim leaves the cut part inside the file (privacy).
  - Speed: `setSpeed` (1.9; pitch-keeping in 1.11).
  - Resize: `Presentation.createForHeight`.
  - Frame grab: `FrameExtractor` (1.10).
- ✔ **The platform has no MP3 or GIF encoder** (supported-formats table). MP3 needs LAME (LGPL) ◌. For GIF, use our own encoder or offer an MP4 loop.
- ✔ **PiP:** AOSP `ActivityRecord.supportsPictureInPicture()` requires `isActivityTypeStandardOrUndefined()`, so a HOME activity can never enter PiP. Use a separate `PlayerActivity` (own taskAffinity, singleTask).
- ✔ **Live wallpaper:** `WallpaperService` behind `BIND_WALLPAPER`, set through `ACTION_CHANGE_LIVE_WALLPAPER` + `EXTRA_LIVE_WALLPAPER_COMPONENT` (the system preview asks the user to confirm). Whether it also applies to the lock screen depends on the OEM ◌. A muted loop inside our own launcher needs neither of these.
- ◌ **Status length:** WhatsApp Status allows 90 s (it was 60 s); Instagram Stories 60 s. Make the chunk length configurable, default 60 s.
- ✔ **Clipboard:**
  - Android 10+: only the focused app can read it.
  - Android 12+: `getPrimaryClip()` shows a toast, `getPrimaryClipDescription()` does not.
  - Android 13+: sensitive flag; auto-clear after ~1 h ◌.

## Implications

1. No platform brands in the title, description, screenshots or keywords.
2. Block-list the YouTube, Meta, TikTok, X, Vimeo, Twitch and Dailymotion hosts and streaming services, with a neutral explanation.
3. No in-app browser that detects video in v1. Use paste link, Share and the text-selection menu instead.
4. Refuse protected streams; never decrypt.
5. Catalog: curated Internet Archive PD, NASA, Blender and Commons; PeerTube later.
6. Attribution UI everywhere.
7. Downloads: `DownloadManager`/OkHttp with resume and a size shown up front. Write through MediaStore, with no media read permission.
8. HLS: our own segments → Transformer remux. Pin Media3 1.11.1.
9. Editing with Media3 only. MP3 and GIF need extra encoders.
10. The launcher background video is user-owned or PD/CC footage, paused when invisible, with no ads.
11. Ads: never on Home or exit, nothing on the lock screen or wallpaper, interstitials closeable within 15 s.
12. Picture-in-picture needs a separate non-HOME `PlayerActivity`.
