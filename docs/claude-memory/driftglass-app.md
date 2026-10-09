---
name: driftglass-app
description: Driftglass: Live Home Screen — друга тестова апка poc/TEST_APP (живі шейдерні шпалери + роль HOME); LibGDX-апка є (09.10.2026), AD_MODE, лише EN, лаунчер з папками
metadata:
  type: project
---

08.10.2026 VELDAN попросив апку шпалер з роллю «Додому» за методикою [[redwave-app]] (прототип → окремий проєкт у poc/TEST_APP → перенос).
Основа — аналіз конкурентів [[wallpaper-home-analysis]].

**Прототип v2 (09.10.2026, УКР / РУС / ENG):** https://claude.ai/artifact/XXRnR3cEUCuAS5h3Mb7QkW — перемикач мови міняє і сторінку, і апку
(інтерфейс, системні діалоги, назви апок, дату, кадри стору, назву/опис для Play); назви шпалер — бренд, не перекладаються.
Копія — `poc/TEST_APP/Game Driftglass/prototype/index.html`.

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

**LibGDX-апка (09.10.2026, коміт 65218e4, не запушено):** `poc/TEST_APP/Game Driftglass/Driftglass`, пакет `com.driftglass.home`,
стоїть на Redmi. Деталі й команди — її `CLAUDE.md`. Рішення VELDAN 09.10.2026:
- **AD_MODE як у Redwave** (Custom Tab на «Додому» / після «Недавніх», роль обов'язкова, без Uninstall). Remote Config немає —
  вмикається лише в debug (Settings → Debug або adb). Для release потрібне джерело прапорців — спитати VELDAN.
- **UI лише англійською** (переклади лишились у коді, перемикача немає).
- **Лаунчер «як звикли»:** авто-папки Google/Tools/Games/Social/Media (розкладку MIUI скопіювати не можна).
  Головний екран — **одна сторінка, без гортання вбік** (VELDAN не хоче сторінок); усі апки з тими ж папками —
  свайпом угору, вертикальний скрол. Ігри без категорії (казино, 1win) не потрапляють у Games.
