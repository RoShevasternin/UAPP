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

Стан на 01.10.2026: **у T5 вставлено** (і там же оновлено всі бібліотеки: AGP 9.4.1, Gradle 9.8.0, TikTok 1.7.1 та ін.), debug і release перевірено на Redmi. **Еталон відкладено** рішенням VELDAN («поки тільки T5, бо ми в ньому працюємо») — кроки 8–14 лежать на сторінці. Release у T5 без signingConfig: для тесту на девайсі підписувати debug-ключем (zipalign + apksigner). Далі: серверна команда додає блок `meta` → Test events в Events Manager → Data safety у Play Console → бекпорт у решту мігрованих апок окремими патчами.

Пов'язане: [[uapp-patch-page-flow]], [[uapp-server-patch-backport]].

Патч 02 (01.10.2026, **вставлено**, Meta приймає install/activate з тестовими ключами VELDAN): тестові ключі для debug з `local.properties` (`meta.testAppId`, `meta.testClientToken`) + покрокова інструкція отримання ключів: https://claude.ai/artifact/KFsw6S28gUK1xdfpY6AVN8. У Gradle-скрипті `java.util.Properties` не резолвиться (`java` — розширення) — тільки `import java.util.Properties` угорі файлу.

Тестова Meta App — на особистому акаунті VELDAN; бойову створять на рекламному акаунті. Тоді: ключі бойової → сервер, тестові рядки з `local.properties` прибрати, тестову App видалити, щоб не було двох застосунків з одним пакетом.

**Рішення VELDAN 01.10.2026 (після перевірки):** тестових ключів у проєкті не тримаємо — патч 02 відкочено, `local.properties` чистий. Ключі Meta — тільки з сервера; немає блоку `meta` — Meta мовчки пропускається, як TikTok. Не пропонувати знову локальні/зашиті ключі.

Документація для розробників інших апок (01.10.2026): https://claude.ai/artifact/26iqp2YAqPhQjSYeC9oo3D — 7 кроків з підстановкою пакета, блок сервера, ключі, перевірка, пастки. Копія — `docs/for-devs/meta-sdk-01-10-2026/index.html`. Бекпорт в інші апки робити за нею.
