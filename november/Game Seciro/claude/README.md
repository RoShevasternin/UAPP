# Seciro — усе для роботи Claude

Апка безпеки «все в одному» за підпискою: VPN, Web Protection, QR-сканер, Storage Cleaner,
Breach Check, генератор паролів. Kotlin + LibGDX. Початок — 24.11.2026 (VELDAN).

## Головне правило: спершу прототип

1. Екран спершу робимо в **прототипі** (`../prototype/`) і публікуємо на claude.ai.
2. VELDAN проклацує й править. Поки прототип не прийнято, у Kotlin екран не переносимо.
3. Перенос у `../Seciro/` (Kotlin + LibGDX) → перевірка на **десктопі** (`sh ./gradlew :desktop:run`,
   вікно 375×812) → лише потім **реальний Android по USB**.

## Теки гри

| тека | що там |
|---|---|
| `claude/` | це: правила, дані з Figma, інструменти Claude |
| `prototype/` | клікабельний прототип (React): `seciro.jsx` → `build.py` → `seciro.html` |
| `Seciro/` | сам проєкт: `core` (уся апка без Android), `desktop` (вікно Мака), `android` (Play) |
| `release/` | для VELDAN: ключі `.jks`, паролі, усе для підпису |
| `market/` | для VELDAN: те, що публікується — `results/icons`, `results/screenshots`, `results/aab` |

## Прототип

- Живий: https://claude.ai/artifact/9z6HEgMhdhdYMEeTSKiwwZ (акаунт overchenkolila). Оновлювати — публікація
  того самого файлу `prototype/seciro.html`; `BUILD` у `seciro.jsx` +1 на кожну публікацію.
- Збірка: `python3 prototype/build.py` (esbuild і React качає сам у `prototype/.tools/`, не в git).
- Окремий екран за посиланням: `…/artifact/…#paywall` (id — `SCREENS` у `seciro.jsx`).
- Знімок без claude.ai: `sh claude/tools/proto-shot.sh paywall /tmp/pw.png 900 1000` (headless Chrome).
- Зроблено: ряд 1 — splash → онбординг (intro + 5) → paywall (3 тарифи, карусель можливостей, мок вікна Google Play).
  Далі: ряд 2 — Sign in, код, «підписка скінчилась», «немає підписки на цю пошту».

## Figma

- Файл «UAPP 2.0», секція **Seciro All-in-One `5:1685`**, 75 кадрів 375×812. Порядок — рядами зліва направо,
  потім ряд нижче. Карта рядів — у пам'яті `vpn-app-new-type`.
- **Без Figma MCP** (платний ліміт). Шлях: вбудований браузер уже залогінений → у консолі сторінки є `figma`
  (Plugin API) → `exportAsync` SVG/PNG, оригінали картинок `getImageByHash`. Багато файлів — **одним tar**
  (див. `figma/README.md`): кілька завантажень підряд браузер губить.
- `figma/frames/` — PNG кадрів @2x (еталон для звірки), `figma/json/` — дерево шарів з числами,
  `figma/art/` — ілюстрації SVG 1:1, `figma/icons/` — Material-іконки (колір задає код).

## Дизайн → код (як в Orbit Dash)

- Число, якого немає в макеті, не вигадуємо — читаємо з `figma/json` або питаємо.
- Шрифти: Inter Medium / Bold, Inter Tight Medium / Bold → MSDF. SF Pro з макета (ліцензія Apple) → Inter.
- Статус-бар «9:41» і home-індикатор — частина iOS-макета, на Android їх малює система.
- Тіні (~500) і блюр — запікати або `VfxTexture` + `BlurEffect` за зразком Orbit Dash
  (`/Users/admin/Apps/Game Orbit Dash PRIVATE/Orbit Dash/docs/vfx.md`, `msdf-shapes.md`).

## Відкриті питання до VELDAN

- Тексти макета кажуть «iPhone» («Your iPhone, Protected», «…space on your iPhone») — для Android замінити.
- `applicationId` поки `com.seciro.app` — остаточний треба до першого завантаження в Play.
- Ціни й пробний період з макета ($0.99 за 3 дні, $8.99 / $29.99 / $69.99) — у Play задаються в консолі.
