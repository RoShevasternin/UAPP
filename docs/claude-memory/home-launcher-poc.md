---
name: home-launcher-poc
description: Home Launcher PoC (poc/HomeLauncher-PoC) — Kotlin-лаунчер + інтерактивна презентація-артефакт з телефоном; далі додаємо інтерактиви до інших функцій
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

**Стиль, який сподобався користувачу:** телефон-мокап з кнопками + кроки праворуч + журнал подій;
токени світла/темна тема, шрифти Unbounded / Onest / JetBrains Mono, акцент зелений.

**Далі (план від 30.09.2026):** додавати інтерактиви до інших функцій лаунчера в тому ж стилі.

**How to apply:** в новій сесії спершу `Artifact read` цього URL (дає актуальний HTML), правити його
і публікувати з `url` — медіа-файли, яких не передаєш, зберігаються. Нове відео — стискати ffmpeg
(ліміт 15 МБ на файл). Копію в репо класти лише коли скаже «зберегти» ([[artifact-then-save-on-request]]).
