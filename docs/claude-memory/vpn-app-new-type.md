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
