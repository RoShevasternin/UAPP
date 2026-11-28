---
name: seciro-handoff
description: Seciro — де зупинились 27.11.2026 і як продовжити з іншого акаунта: посилання, де лежать кіти (поза git), що чекаємо, наступний крок
metadata:
  type: project
---

**28.11.2026 (кінець):** прототип build 26 — Safari→Chrome прибрано (наша вигадка), нотатки «що в макеті → що не так → як має бути», вибір → Віталію; раніше build 25 (усі нотатки: Android вирішуємо ми; Terms/Privacy — Custom Tabs, адреси питаємо). Нові кіти з цього ж акаунта: Віталію https://claude.ai/artifact/Hrc1XkwZj1xi8mdf2zcxar (12 карток: продукт/сервер, адреси Terms/Privacy + «до відома» з нашими рішеннями), дизайнеру https://claude.ai/artifact/KFzcN17fT4P3jyekWv1WCT (11 пунктів, точний EN). Збірка кітів — `~/Apps/Seciro-private/tools/kit_build.py`. Понеділок 30.11 — дзвінок з Віталієм по питаннях.

**28.11.2026:** прототип build 23 опубліковано з другого акаунта як НОВИЙ артефакт: https://claude.ai/artifact/WjU9yjLoHRUh3B3iTH5a1P (старий 9z6H… — build 22, інший акаунт). Build 23: Protection Review (result-ok / result-issues) — по нотатці на кожен рядок (Web Protection, оновлення, блокування екрана, Face ID → біометрія: апка бачить лише «біометрія є», не відбиток чи обличчя) + 3 ясні питання Віталію з A/B/C; sheet-safari — «що точно / що ні» + питання про спосіб захисту сайтів (A VPN — радимо / B Chrome Secure DNS / C Android Private DNS). Нотатки тепер з переносами рядків (\n). VELDAN просить: питання й завдання — простіше, з прикладами, і окремо «як буде на Android». Кіти (картки 5, 8, пункт 10 дизайнеру) ще кажуть «відбиток пальця» — не синхронізовано.

**Стан на 27.11.2026 (кінець сесії, ліміт):**
- Прототип build 22 (усі 73 екрани = 75 кадрів Figma, сітка «Усі екрани»): https://claude.ai/artifact/9z6HEgMhdhdYMEeTSKiwwZ — код у репо `november/Game Seciro/prototype/`, збірка `python3 prototype/build.py`, тести `claude/tools/proto-*-test.py`.
- Кіт Віталію v16: https://claude.ai/artifact/Hj3FPMn8KawTvA1gCBkAUa · кіт дизайнеру v11: https://claude.ai/artifact/PCBWKfSi31ZxWoQc3hFeHL.
- Тексти кітів і копії сторінок — `/Users/admin/Apps/Seciro-private/` (поза git, бо репо публічне): `kits/mgr.json`, `kits/des.json`, `kits/kit-content.py get|put`, README там. Старі вихідники збирача (scratchpad) зникли — правимо JSON усередині сторінки.
- Кіти й прототип належать акаунту, де їх створено: з іншого акаунта на ті самі URL не опублікувати — або новий артефакт (і дати VELDAN нове посилання), або правки робити з того акаунта.
- Чекаємо: відповіді Віталія (10 карток, [[seciro-open-questions]]), екрани дизайнера (12 пунктів), сервер (VPN Android, покупки Google Play).
- План пошуку дублікатів фото/відео (на телефоні, без сервера) — `november/Game Seciro/claude/README.md` § «Storage Cleaner на Android».

**Наступний крок:** VELDAN говорить з Віталієм по прототипу → потім Kotlin (`Seciro/`: core / desktop / android) → десктоп → USB ([[prototype-first]]). Перед Kotlin прочитати `november/Game Seciro/claude/README.md`.
Пов'язане: [[vpn-app-new-type]], [[seciro-kit-style]], [[seciro-build-by-design]].
