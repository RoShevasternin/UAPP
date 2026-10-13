# Framefall: Video Downloader

Третя апка `poc/TEST_APP` після Redwave і Driftglass, категорія **Tools**. Зберігає відео з легальних
посилань і відкритих каталогів. Має Studio: обрізка, звук M4A, GIF або MP4-петля, частини для Status,
кадр, стиснення. Як головний екран (роль HOME) показує живий відеофон із власного відео, картку з
посиланням із буфера і «Continue watching».

- **Живий прототип:** https://claude.ai/artifact/5L7TpzbpbrTptLSVq66UJw (копія — `prototype/`)
- **Стан на 13.10.2026:** Android-проєкт є — `Framefall/` (пакет `com.vidtools.framefall`), зібраний з каркаса
  Redwave з бізнес-логікою UAPP (сервер, `atk`, реклама, білий / чорний режим), перевірений на Redmi (debug і release).
  Правила, плейсменти й що віддати серверній команді — `Framefall/CLAUDE.md`.

## Що в теці

| Шлях | Що |
|---|---|
| `prototype/index.html` | прототип: телефон з 10 сценаріями тест-плану, буфер із 13 тестовими посиланнями, журнал Android, дослідження, 6 кадрів маркету, бренд, план переносу |
| `prototype/img`, `prototype/media` | справжні фрагменти відкритих фільмів (Blender CC BY, NASA, Internet Archive — суспільне надбання); `media/SOURCES.md` — титри, `media/make_media.sh` — як відтворити |
| `prototype/tools/walk.js` | прогін прототипу в Playwright зі скріншотами |
| `research/` | ринок Play США (909 апок, 31 тис. відгуків), правила Google, легальні джерела, Android-факти, дані й скрипти |
| `Framefall/` | Android-проєкт (LibGDX), див. `Framefall/CLAUDE.md` |
| `assets/` | іконки атласу, іконка лаунчера, текстури, петлі відеофону, скрипти (`assets/README.md`) |
| `assets/brand/` | лого SVG і PNG 512, квадратна іконка під маску Play |
| `market/icon_512.png` | іконка для Play Console |

## Пропозиції в прототипі (VELDAN ще не погодив)

- **Назва:** Framefall: Video Downloader (27/30). Kinora зайнята в Play. Варіант у стилі Redwave —
  Framefall: Video Home Launcher (30/30).
- **Бренд:** тепле світло кінопроєктора, градієнт бурштин → помаранч. Іконка — кадр плівки зі стрілкою «play ↓».
- **Джерела:** прямі посилання, Drive, Dropbox, OneDrive, HLS без захисту, Internet Archive, NASA,
  Wikimedia Commons, PeerTube. Блокуються YouTube, Instagram, Facebook, TikTok, X, Vimeo, Twitch та
  стрімінги. Стріми з DRM чи AES отримують чесну відмову.
- **Головний екран:** вмикається за згодою людини (екран-пояснення, Maybe later), відеофон з власного
  відео, лаунчер як у Driftglass.
- **Відкриті рішення** (розділ «Перенос» у прототипі): пакет, реклама, home_required, MP3/GIF, що
  йде у v1, а що у v1.1.
