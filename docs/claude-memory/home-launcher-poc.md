---
name: home-launcher-poc
description: PoC-лендінг (артефакт) з тестами Android-можливостей — Тест 1 Default Home App, Тест 2 сповіщення при зарядці, Тест 3 асистент, Тест 4 плаваюче вікно; кожен тест = відео + таймлайн + інтерактивний телефон
metadata:
  type: project
---

**Home Launcher PoC** — внутрішній PoC: застосунок стає Default Home App (CATEGORY_HOME)
і отримує кожне натискання «Додому». Це не гра парку і не міграція до еталона.

**Kotlin-проєкт:** `/Users/admin/Apps/UAPP/poc/HomeLauncher-PoC` (пакет `com.example.homelauncher`,
minSdk 24, targetSdk 37). Уже зроблено: intent-filter HOME+DEFAULT (+LAUNCHER для зручності),
`isDefaultHomeApp()` через `RoleManager.isRoleHeld(ROLE_HOME)` / `resolveActivity` на старих,
`requestDefaultHome()` → системний діалог ролі, фолбек `ACTION_HOME_SETTINGS`; сітка всіх
застосунків (LauncherApps, всі профілі); лічильник HOME в `onNewIntent`; на кожен `onResume`
вискакує картка з мемом (`res/drawable/home_banner.png` = файл з Downloads «Вставлено 2026-09-30 о 17.51.07.png»)
і текстом «Ето шок ти глянь как можно»; тап по картці відкриває Custom Tab. На 30.09.2026 тека `poc/` ще не в git.

**Презентація (артефакт, 30.09.2026):** https://claude.ai/artifact/VfY4JwiBzZEnt7Gvis7D5M
— Tailwind, УКР/РУС/ENG, підпис «Підготував VELDAN». Розділи: банер + заголовок (строго в 1 рядок,
підганяється JS `fitOneLine`) → відео з девайса в рамці телефона + таймлайн з перемоткою
(0:03/0:10/0:18/0:26/0:42/0:50) → інтерактивний телефон (кроки: запит ролі → відкрити
застосунок → «Додому», журнал подій `onNewIntent`/`onResume`) → 2 шматки коду.
Медіа опубліковані як файли артефакту: `media/banner.jpg`, `media/card.jpg`, `media/poster.jpg`,
`media/demo.mp4` (оригінал `Screenrecorder-2026-09-30-18-32-03-134.mp4` 78 МБ стиснутий ffmpeg до 2.8 МБ, 432 px).

**Тест 2 — сповіщення при зарядці (додано 02.10.2026, той самий артефакт).**
Проєкт `/Users/admin/Apps/UAPP/poc/ChargingDreamPoC` (пакет `com.example.chargingdreampoc`).
DreamService (заставку) **викинуто повністю** — не працює: вмикається вручну, стартує лише по
тайм-ауту простою, а не на кабель, і на MIUI/Samsung функцію вирізано. Замість неї:
foreground-сервіс (`specialUse`) → `registerReceiver(ACTION_POWER_CONNECTED)` **в рантаймі**
(маніфест-ресивер мертвий з API 26) → звичайне сповіщення (fullScreenIntent прибрано, бо
background-запуск Activity заблоковано з API 29). Користувач бачить рівно 1 діалог —
`POST_NOTIFICATIONS`; `FOREGROUND_SERVICE*` — install-time, без вікна. Ціна: вічне тихе
сповіщення сервісу. На MIUI потрібен ручний **Автозапуск**, інакше змах/ребут вбиває детекцію.
Медіа артефакту: `media/charging.mp4` (14.5 МБ → 1.8 МБ, crf 28, 540 px), `media/charging-poster.jpg`.

**Тест 3 — асистент за замовчуванням (додано 02.10.2026, той самий артефакт).**
Проєкт `/Users/admin/Apps/UAPP/poc/AssistRolePoC` (пакет `com.example.assistrolepoc`).
Суть: `intent-filter ACTION_ASSIST` робить апку доступною в «Цифровий помічник за
умовчанням»; після вибору довге натискання «Додому» відкриває нас **замість Google**.
Три помилки типової спеки: константа — `ROLE_ASSISTANT`, а не `ROLE_ASSIST` (не
компілюється); ролі **в маніфесті не оголошуються**; `Settings.Secure` ключ —
`assistant`, а `voice_interaction_service` порожній без `VoiceInteractionService`.
На цьому Redmi роль видалась із коду (`cmd role get-role-holders` підтвердив), але
фолбек на `ACTION_VOICE_INPUT_SETTINGS` лишено — із детекцією «діалогу не було» за
часом відповіді (<700 мс). Найдешевший з трьох механізмів: **0 рантайм-дозволів,
0 фонових сервісів**. Мінус: асистент у системі один — користувач втрачає Gemini.
Медіа: `media/assist.mp4` (10.9 МБ → 1.4 МБ), `media/assist-poster.jpg`.

**Тест 4 — плаваюче вікно (додано 02.10.2026, той самий артефакт).**
Проєкт `/Users/admin/Apps/UAPP/poc/OverlayPoC` (пакет `com.example.overlaypoc`).
`SYSTEM_ALERT_WINDOW` + `TYPE_APPLICATION_OVERLAY` + `FLAG_NOT_FOCUSABLE`, два стани:
кулька 56dp (чат-голова) ↔ картка з банером. Перемикання стану — **removeView +
addView**, бо `updateViewLayout` уміє лише переміряти вже додану в'юху; невидалена
в'юха тримає токен вікна до кінця процесу. Тап і драг розрізняються за
`scaledTouchSlop`; позиція кульки живе в полях сервісу (інакше стрибає на старт
після згортання). Довгий тап по кульці = `stopSelf` (бо після згортання ✕ недоступний).
Дозвіл накладання попутно знімає заборону background-activity-start (API 29+) —
саме тому тап по картці відкриває Custom Tab напряму.

**MIUI/HyperOS: сторінку дозволу накладання для своєї апки відкрити не можна.**
Заміряно 02.10.2026 на Redmi (API 36, Ukr): `ACTION_MANAGE_OVERLAY_PERMISSION` з
`package:` URI **не падає** — відкривається `Settings$OverlaySettingsActivity` і
ігнорує URI, показуючи список усіх апок. Тому «try-catch фолбек на MIUI» зі
стандартної спеки ніколи не спрацює. Перевірені й відкинуті варіанти:
`com.miui.securitycenter/.permcenter.permissions.PermissionsEditorActivity` +
`extra_pkgname` (резолвиться, веде на сторінку апки, але там лише «Інші дозволи» —
4 перемикачі MIUI, тумблера накладання немає); `Settings$AppDrawOverlaySettingsActivity`
(не існує); `SubSettings` + `:settings:show_fragment` (відкривається порожнім).
Тумблер належить `com.android.settings`, апку в списку юзер шукає сам.
Підсумковий порядок у `MainActivity.requestOverlayPermission()`: стоковий інтент з
URI → MIUI editor (резолвиться через `<queries><package com.miui.securitycenter>`) →
голий список. Медіа: `media/overlay.mp4` (9.5 МБ → 1.2 МБ, crf 28, 540 px),
`media/overlay-poster.jpg`. Таймлайн: 4 · 6.8 · 13.8 · 15.7 · 18.2 · 22 · 24.8 ·
26.5 · 28.3 · 32 с.

**Стиль, який сподобався користувачу:** телефон-мокап з кнопками + кроки праворуч + журнал подій;
токени світла/темна тема, шрифти Unbounded / Onest / JetBrains Mono, акцент зелений.
Кожен новий тест = блок «чому попередній підхід не вийшов» + відео + таймлайн + інтерактив +
таблиця дозволів + «коли працює / коли ні».

**Таймлайн робити покадрово:** момент події шукати по відео через `ffmpeg` (контактні аркуші
`fps=1,tile=`, потім 4 fps навколо потрібної секунди). У Тесті 2 орієнтир — блискавка на іконці
батареї у статус-барі: зникла 17.2 с, з'явилась 25.8 с, сповіщення виїхало 26.5 с. Ключові рядки
таймлайну підсвічувати (клас `key`, бурштиновий), щоб менеджер одразу бачив причину й наслідок.

**How to apply:** в новій сесії спершу `Artifact read` цього URL (дає актуальний HTML), правити його
і публікувати з `url` — медіа-файли, яких не передаєш, зберігаються. Нове відео — стискати ffmpeg
(ліміт 15 МБ на файл). Копію в репо класти лише коли скаже «зберегти» ([[artifact-then-save-on-request]]).
