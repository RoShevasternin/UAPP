---
name: uapp-meta-sdk
description: Задача 01.10.2026 — Meta (Facebook) SDK поруч із TikTok; патч 01 (T5 RBX Boss + еталон), рішення VELDAN, що далі
metadata:
  type: project
---

01.10.2026 прийшла задача підключити Meta SDK до апок, де вже стоїть TikTok SDK. Зроблено як **патч 01**: https://claude.ai/artifact/DT1VQTRqKQiqfpZrRRpi43, запис — `docs/patches/01-meta-sdk.md`.

Рішення VELDAN:
- ключі (App ID + Client Token) приходять із **нашого конфіг-сервера**, блок `meta {app_id, client_token}` поруч із `tiktok`; App Secret ніде в апці;
- спершу **еталон**, потім бекпорт; **T5 RBX Boss** іде разом з еталоном, бо VELDAN зараз на ній;
- події **як у TikTok**: `activateApp` + `fb_mobile_achievement_unlocked` на головний екран.

Технічно: `facebook-core` 18.3.0; `MetaManager` (OneTime, `isReady`); `MetaAnalyticsProvider`; старт двічі — з кешу в `App.onCreate` (щоб SDK бачив сесію) і після конфігу в `MainActivity`. Без ключів у маніфесті `FacebookInitProvider` пише «Failed to auto initialize» — це нормально.

Стан на 01.10.2026: сторінка чекає вибору VELDAN. Далі: серверна команда додає блок `meta` → Test events в Events Manager → Data safety у Play Console → бекпорт у решту мігрованих апок окремими патчами.

Пов'язане: [[uapp-patch-page-flow]], [[uapp-server-patch-backport]].
