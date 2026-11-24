---
name: vpn-app-new-type
description: З 24.11.2026 нова VPN-апка за підпискою — новий тип, лише Kotlin + LibGDX, без businesModule/Redwave; WireGuard, Android 10
metadata:
  type: project
---

24.11.2026 VELDAN почав нову апку для компанії: VPN за підпискою, «дуже якісні апки». Це
**абсолютно новий тип** — не як парк RBX і не як TEST_APP.

- Від старого беремо лише архітектуру **Kotlin + LibGDX**. Каркас Redwave, `businesModule`,
  конфіг-сервер, рекламу й процедуру міграції **не використовувати**.
- Протокол: рекомендовано **WireGuard** (`com.wireguard.android:tunnel`, Apache 2.0) у
  `VpnService` в android-модулі; LibGDX — лише екрани, міст `connect/disconnect/status`.
- IPsec: формат сервера «PSK + логін + пароль» (L2TP/Xauth) апка підняти не може; якщо треба —
  тільки IKEv2 (сертифікат сервера, Android 11+). Серверній команді відповіли м'яко: рекомендуємо
  WireGuard, IKEv2 можливий. Не казати, що IKEv2 застарів — це неправда.
- minSdk: **Android 10** (з IKEv2 — 11).
- LibGDX: вимикати безперервний рендер (батарея), обережно з полями вводу.

**Why:** преміум-продукт за підпискою — стабільність VPN важливіша за все.
**How to apply:** нову апку не будувати з Redwave/еталона; тека — у `november/`.

## Дизайн і асети (24.11.2026)

- **Еталон якості — `/Users/admin/Apps/Game Orbit Dash PRIVATE`** (окремий репо): шрифти MSDF
  (`assets/font/gen-*.command`, `msdf-atlas-gen`), фігури SVG → `msdfgen` → атлас
  (`Orbit Dash/docs/msdf-shapes.md`, `msdf-usage.md`), «група = фрейм Figma» (`SizeScaler`),
  Layer Blur з Figma 1:1 (`docs/vfx.md`). Брати звідти підходи, не код Redwave.
- **Figma MCP НЕ використовувати** — VELDAN не хоче платних трат; Starter ≈ 8–20 викликів/міс
  (досвід Orbit Dash, `docs/figma-workflow.md`). `whoami` — безкоштовний.
- Шлях без MCP: (1) SVG компонента = джерело чисел (Copy as SVG / експорт), скріншот — лише
  композиція, числа з нього не міряти; (2) самому відкривати файл у вбудованому браузері;
  ◌ перевірити на першому файлі: глобальний `figma` у консолі сторінки (`exportAsync` SVG/PNG) —
  тоді експорт без рук; якщо ні — Export / Copy as SVG руками або Chrome-розширенням.
- Число, якого немає в макеті, не вигадувати — читати з SVG або питати.
- **PNG з Figma — качаю сам** (VELDAN 24.11: «серйозний проєкт, вигляд як у Figma»): оригінал
  заливки, якщо картинка вставлена як image fill, інакше експорт шару у 3x; джерела —
  у репо поруч з проєктом; пакування — безкоштовним TexturePacker з libGDX (gdx-tools), не
  CLI TexturePacker (вотермарка, див. [[mindora-texturepacker-cli]]); фільтр Linear + mipmaps;
  перевірка — скріншот з телефона поруч зі скріншотом фрейму.
- **Безкоштовний доступ до Figma (пошук 24.11.2026):** (A) основний — вбудований браузер +
  глобальний `figma` у консолі сторінки (офіційно «в кожному вікні Figma»; у view-only його
  прибирали → потрібен доступ на редагування); експорт `exportAsync` SVG/PNG і оригінали
  заливок `getImageByHash(...).getBytesAsync()`; перевірити на першому файлі. (B) запасний —
  Talk to Figma MCP (github grab/cursor-talk-to-figma-mcp, MIT, плагін у Community, без токена
  й лімітів; PNG віддає base64 «limited»; потрібні bun + websocket). Не брати figma-console-mcp
  і Framelink — потребують особистий токен і частково REST (Starter ≈ 6 запитів/міс).

## Figma-файл і перевірений доступ (24.11.2026)

- Файл **UAPP 2.0** (`figma.com/design/Dy3ZtwlDhwDW5QtJQFWsZI`), сторінка Seciro, секція
  **«Seciro All-in-One» `5:1685`** — 75 екранів 375×812 (iPhone-макет). Порядок — рядами: зліва
  направо, потім наступний ряд нижче (сортувати за y, потім x). Ряди: splash/онбординг/paywall →
  Sign in + code + підписка скінчилась → Dashboard/scan/Result → VPN → Web Protection →
  Safe QR Scanner → Storage Cleaner → Breach Check → Password Generator → Settings/My Account.
  Тобто апка — **безпека «все в одному»**, VPN лише одна з функцій.
- ✔ **Шлях A працює**: вбудований браузер уже залогінений, `window.figma` є (editorType figma,
  повний доступ). `exportAsync` SVG_STRING / PNG 3x, `getImageByHash` — усе ок. Великі файли —
  Blob + `<a download>` → падають у `~/Downloads` → `mv` у проєкт. Перевірено: VPN `5:3826` @3x =
  1125×2436, 537 КБ. Скріншот (`computer screenshot`) на Figma-канвасі зависає — не потрібен.
- Шрифти: Inter Medium/Bold, Inter Tight Medium/Bold (OFL — можна); SF Pro (≈31 текст, переважно
  статус-бар iOS) — ліцензія Apple лише для Apple-платформ → на Android замінити Inter.
- Ефекти: DROP_SHADOW ×486, BACKGROUND_BLUR ×25, GLASS ×5, LAYER_BLUR ×2 — тіні/блюр
  запікати або робити як `VfxTexture`+`BlurEffect` Orbit Dash. Растрових заливок лише 5
  (Safari-іконка 256, логотипи апок 512, 2 «img»). Решта — вектор (MSDF/SVG).
- Статус-бар 9:41 і home-індикатор — частина iOS-макета, на Android не малювати.

## Проєкт (24.11.2026)

- Тека: `november/Game Seciro/` → `claude/` (README з правилами, `figma/` дані, `tools/`), `prototype/`,
  `Seciro/` (Gradle: core / desktop / android, ще не збирався), `release/`, `market/results/{icons,screenshots,aab}`.
- Прототип: https://claude.ai/artifact/9z6HEgMhdhdYMEeTSKiwwZ (акаунт overchenkolila), ряд 1 готовий (build 2).
  Вбудований браузер НЕ залогінений у claude.ai — перевірка знімком `claude/tools/proto-shot.sh` (headless Chrome).
- Деталі й відкриті питання — `november/Game Seciro/claude/README.md`.
- Точність прототипу (24.11, зауваження VELDAN): звіряти `claude/tools/proto-diff.py`, а не на око; уроки
  (PNG @3x замість SVG, U+2028 у текстах Figma, Inter opsz 14 + antialiased, inset-рамки) — у `claude/README.md`.
  Кадр Figma — зліва від прототипу, панель кнопок — справа (VELDAN).
