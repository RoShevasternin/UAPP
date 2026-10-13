---
name: framefall-app
description: Framefall: Video Downloader — третя апка poc/TEST_APP; 13.10.2026 Android-проєкт з каркаса Redwave (businesModule, реклама, білий/чорний режим), пакет com.vidtools.framefall, відео через OES-текстуру, Studio на Transformer
metadata:
  type: project
---

12.10.2026 VELDAN попросив дослідити й зробити прототип Video Downloader «теж з хом скрін, як Redwave» —
назва, іконка, усе необхідне. Тека: `poc/TEST_APP/Game Framefall/` (README, `prototype/`, `research/`,
`assets/brand/`, `market/icon_512.png`). Методика та сама, що в [[redwave-app]] і [[driftglass-app]].

**Прототип v1:** https://claude.ai/artifact/5L7TpzbpbrTptLSVq66UJw (копія `prototype/index.html`, медіа в `img/`
і `media/` публікуються через `files`). Сторінка українською, UI апки англійською. Телефон: системний лаунчер →
онбординг 3 → пояснення ролі (Maybe later, прапорець home_required) → системний діалог → наш Home
(відеофон, картка буфера, Continue watching, папки, свайп — усі апки, меню фону) → Save / Discover / Library /
Studio, плеєр, PiP. Studio має 7 інструментів: Trim, Extract audio, GIF/MP4-петля, Split for Status, кадр,
Compress, Home background. Буфер — 13 тестових посилань, журнал Android, 10 тестів. Нижче на сторінці:
дослідження, 6 кадрів маркету, бренд, перенос.

**Рішення в прототипі (пропозиції):**
- Назва Framefall: Video Downloader (27/30); варіант Framefall: Video Home Launcher (30/30).
  Kinora зайнята в Play (4 апки), вільні запасні — Cinedrop, Moviedrop, Glowframe.
- Бренд: бурштин → помаранч (#FFC94D → #FF8A2A → #F2511B) на теплому чорному. Іконка — кадр плівки з
  перфорацією і «play ↓». Шрифти: Unbounded, Onest, JetBrains Mono (у всіх є кирилиця).
- Медіа — справжні відкриті фільми: Blender CC BY, NASA й Internet Archive у суспільному надбанні; фон Home —
  петлі Orion/Sintel.

**Ключові факти з дослідження** (`research/`):
- Гібриди «завантажувач + лаунчер» уже є: лідер All Video Downloader: Launcher — 9,2 млн з 07.2025.
  Лише 3 з 21 гібридів попереджають про зміну головного екрана; у відгуках «SCAM ALERT».
- Disruptive Ads прямо забороняє рекламу на кнопку Home. Це стосується й AD_MODE Redwave.
- HOME-Activity не може в PiP: потрібна окрема PlayerActivity. У Android немає енкодерів MP3 і GIF.
  Transformer офіційно не приймає HLS.
- Pexels, Coverr, Mixkit і Videvo не підходять за умовами. У v1 — Internet Archive (вибраний список), NASA,
  Blender, Commons.

**Відкриті рішення:** назва й пакет (пропозиція `com.framefall.video`), реклама, home_required (пропозиція
false), MP3 (LAME) і GIF, HLS / PeerTube / системні живі шпалери — у v1 чи v1.1.

**13.10.2026 — Android-проєкт `Framefall/` (VELDAN: «по цій же логіці, що Redwave»):** скопійовано каркас
Redwave, музичне прибрано. Пакет `com.vidtools.framefall` — бо сервер ключує картку за ОСТАННІМ сегментом пакета
(`com.framefall.downloader` дістав би картку Redwave `downloader`). Картки `framefall` ще немає → шаблон
`custom_google` → чорний режим для всіх. Вкладки Save / Discover / Library / Studio; відео — ExoPlayer у
SurfaceTexture → OES-текстура в GDX (`AVideo`), живий відеофон на Home; Discover — `assets/catalog.json` з прямими
лінками archive.org / NASA; Studio — Transformer + свій GifEncoder. Firebase-проєкту немає (google-services опційний).
Перевірено на Redmi debug + release: онбординг, роль, опт-ін, app_open / front / back, «Додому» в обох режимах,
збереження прямого лінка й сторінки архіву (шторка якості), плеєр VP9 1080p і повний екран, усі 7 інструментів Studio.
Відкрите: картка `framefall` і TikTok-ключі від серверної команди, Firebase, свій AdMob App ID, Privacy Policy, маркет-кадри.
13.10.2026 кіт публікації (VELDAN: «все необхідне для публікації»): назва **Framefall: Video Home Launcher**, акаунт
CAPSLOCK LTD ([[test-app-new-play-accounts]]), ключ `release/keyFramefall.jks`, `sh release/build_aab.sh` → `market/framefall v1.aab`,
`market/LISTING.md` (тексти, Data safety з нашим сервісом + AdMob + TikTok + FCM), 6 кадрів з реальних екранів Redmi
(`market/tools/render.sh`), Privacy Policy `privacy-policy/` (ще не задеплоєна на Cloudflare).
Деталі — `Framefall/CLAUDE.md`. Пов'язане: [[redwave-app]].

**How to apply:** правки прототипу — `Artifact read` URL → правка → publish з `url` (разом з `files`, якщо
змінились медіа) → перезаписати `prototype/index.html` (зверху doctype + charset + коментар з URL).
Скрипти ринку перезапускаються з `research/scripts/` (google-play-scraper 1.2.7).
