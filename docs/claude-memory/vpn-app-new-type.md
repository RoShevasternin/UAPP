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
