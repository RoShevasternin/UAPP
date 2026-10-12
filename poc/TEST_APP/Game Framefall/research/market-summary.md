# Video downloaders on Google Play (US), market scan, 12.10.2026

Method:
- Play US, `hl=en gl=us`, `google-play-scraper` 1.2.7, plus "Similar apps" (one hop from 40 seeds).
- 36 queries in total: 28 base and 8 extra for launcher hybrids.
- Raw tables and quotes: `data/analysis.md`. Data: `data/`. Scripts: `scripts/`.
- "Fact" means counted from data; *Hypothesis* means interpretation.

## Size

- **909 apps.** Of them, **253 are true video downloaders** with 3.35B installs:
  - generic: 129 apps, 2.41B installs;
  - social or story savers: 85, 0.27B;
  - WhatsApp status savers: 28, 0.66B;
  - m3u8/HLS: 11, 2.2M.
- Categories of downloaders: Tools 121, Video Players & Editors 105, Productivity 14, Social 8.
- Ads in 75% of downloaders (89% of those with ≥1M installs). IAP in 55% (76%). 1% are paid.
- The **top 25 hold 84.5%** of downloader installs. The top 9 are 100M+ apps released 2016–2022:
  - All Video Downloader & Player, 390M;
  - Video Downloader (Simple Design), 333M;
  - InShot All Video Downloader, 278M;
  - InShot Video Downloader, 238M;
  - Status Download (Shree Ganesha Labs), 203M.
- 93 downloaders (37%) were released since 10.2025. 15 of them reached ≥100K installs and 5 reached ≥1M.

## Downloader + launcher hybrids (21 apps, 13 of them video downloaders, 14.7M installs)

| App | Developer | Installs | ★ | Released |
|---|---|---|---|---|
| All Video Downloader: Launcher | The Lucky App | 9.2M (~610K/mo) | 4.05 | 2025-07-21 |
| Volume Control Sound Launcher | AtomApplications | 3.2M | 4.56 | 2025-12-03 |
| Video Downloader & Launcher | hubMate Apps | 2.2M | 3.95 | 2026-03-01 |
| Flivio Video Player All Format | TheWeirdos Lab | 1.9M | 3.41 | 2025-12-31 |
| All Video Downloader, Launcher | OMRUP INFOTECH | 1.1M | 3.98 | 2026-04-11 |
| HD Video Downloader & Launcher | Tools Apps Dev. | 745K | 4.24 | 2026-04-09 |
| All Video Downloader & Home ×6 | 6 accounts | 1.6K–387K | — | — |

- Only 3 of 21 disclose the home-screen change. 20 of 21 show ads, and 14 of 21 host their privacy policy on blogspot.
- Hybrid reviews (n=660, 11 apps): home takeover 2.4%, "virus/scam" 3.0%. For plain downloaders the scam rate is 0.5%.
- Top review on the leader, 1,196 👍: "SCAM ALERT. There is no reason for a video player to have a hard requirement to change the default home page…"
- *Hypothesis:* the HOME role is used for retention and ad surface, not as a feature. No app combines a legal-sources-only downloader with an opt-in, disclosed launcher.

## YouTube and brands

- **YouTube:**
  - Top 25: none claims support, 5 say "not supported", 20 don't mention it.
  - Across all 253: 2 small apps claim support.
  - YouTube complaints are 0.5% of downloader reviews.
- **Brand in the title:** top 25: 1; top 50: 4; all 253: 44 (17%). The leaders use generic titles and don't name platforms even in descriptions.

## Features (description claims, n=253 / top 50)

| Feature | All | Top 50 | Feature | All | Top 50 |
|---|---|---|---|---|---|
| HD/4K | 63% | 72% | mp3 | 23% | 32% |
| player | 53% | 62% | no watermark | 22% | 18% |
| status saver | 35% | 42% | gif | 13% | 18% |
| browser | 31% | 42% | m3u8/HLS | 7% | 4% |
| auto-detect | 28% | 38% | launcher | 6% | 2% |
| multi download | 28% | 38% | cutter | 4% | 0% |
| private folder | 25% | 40% | video wallpaper | 3% | 4% |
| background dl | 24% | 44% | PiP | 2% | 4% |

## Review themes (downloaders, n=25,324; multi-label; 59% matched no theme)

| Theme | All | 1–2★ |
|---|---|---|
| not working | 5.9% | 27.8% |
| ads | 7.5% | 22.3% |
| paywall | 1.8% | 6.4% |
| virus/scam | 0.5% | 2.5% |
| youtube | 0.5% | 1.8% |
| slow | 0.6% | 2.0% |
| quality / no sound | 0.2% | 0.8% |
| private folder | ~0% | ~0% |

## Name check (Play US search, not a trademark check)

| Name | Result |
|---|---|
| Framefall, Cinedrop, Clipwave, Lumenreel, Moviedrop, Glowframe | free |
| Beamline, Frameport | near-free (one 0–1+ app) |
| Beamdrop, Framevault | weak conflict |
| Vidwave, Reelvault, Clipvault, Kinora, Nightframe | taken / conflict |

## Limits

- Installs are `realInstalls`. Play hides rating and release date for 17 apps with ≥100K installs, and returned no reviews for 10 of them.
- Only the newest ≤200 reviews per app were taken, and the theme regexes are English-only.
- Release dates may be re-publish dates.
- Search returns about 30 results per query, so the long tail is undercounted.
- Features come from descriptions; no app was tested on a device.
