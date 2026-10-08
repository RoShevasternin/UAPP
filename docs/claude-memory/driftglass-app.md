---
name: driftglass-app
description: Driftglass: Live Home Screen — друга тестова апка poc/TEST_APP (живі шейдерні шпалери + роль HOME), прототип 08.10.2026; що вирішено й що далі
metadata:
  type: project
---

08.10.2026 VELDAN попросив апку шпалер з роллю «Додому» за методикою [[redwave-app]] (прототип → окремий проєкт у poc/TEST_APP → перенос).
Основа — аналіз конкурентів [[wallpaper-home-analysis]].

**Прототип v1:** https://claude.ai/artifact/XXRnR3cEUCuAS5h3Mb7QkW (джерело — scratchpad сесії; у репо ще НЕ скопійовано —
чекаємо, поки VELDAN погодить назву; потім `poc/TEST_APP/Game Driftglass/prototype/index.html`).

**Рішення в прототипі (пропозиції, VELDAN ще не погодив):**
- Назва **Driftglass: Live Home Screen** (28/30), категорія Personalization; «Home Screen» у назві навмисно (чесний варіант з аналізу).
  Lumora/Prism/Aura зайняті шпалерними апками. Іконка — арка-дверний проріз з живим перетіканням (SVG у прототипі).
- Шпалери генеруються шейдерами (GLSL ES 1.00 → `assets/shader/wallpaper/*.frag` без змін): aurora, liquid, waves, glass, mesh;
  uniform-и `u_res,u_time,u_c1..u_c4,u_scale,u_grain,u_seed,u_par`. 8 палітр, 24 шпалери в каталозі.
- Екрани: сплеш, 3 онбординги, пояснення ролі з «Maybe later», системний діалог, Home (годинник, скляна картка shuffle/♡/customize,
  сітка, док, свайп → усі апки), Discover, Preview, Studio, My Glass (Auto-shuffle, Day cycle 4 слоти), Settings, системний лаунчер.
- Без ролі: Studio + статичні шпалери через WallpaperManager; Uninstall у довгому натисканні; battery saver 30 fps при < 20%.
- Сітку старого лаунчера скопіювати не можна: на Redmi `com.android.launcher.permission.READ_SETTINGS` = signature|privileged.

**Відкриті рішення:** пакет (пропозиція `com.driftglass.home`), home_required, реклама (якщо так — лише в галереї/Studio), lock screen за замовчуванням.
