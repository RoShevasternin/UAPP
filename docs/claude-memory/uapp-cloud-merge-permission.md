---
name: uapp-cloud-merge-permission
description: Хмарна сесія Claude (claude.ai/code) може сама відкривати PR і мержити свою гілку в main без окремого питання — дозвіл VELDAN від 06.10.2026
metadata:
  type: feedback
---

06.10.2026 VELDAN дозволив: у хмарних сесіях (claude.ai/code, GitHub через MCP) Claude **сам
комітить, пушить, відкриває PR у `main` і мержить**, коли пачка змін готова. Окремо не питати.

**Why:** користувач не хоче щоразу тиснути «Compare & pull request» на GitHub. Сказав: «можеш сам».

**How to apply:**
- Працюємо в гілці сесії → PR у `main` → merge (метод `merge`, без переписування історії).
  Перед мержем `git fetch origin main`: якщо `main` пішов уперед — спершу влити його в гілку.
- Локальний Claude на Маці **пушити не може** (у git немає логіна, див. [[memory-in-repo]]).
  `git pull` публічного репо там працює, а пушить VELDAN через GitHub Desktop.
- У проєктах із власним `CLAUDE.md` «не роби git-операцій» (напр. T35 Mindora) — локально не чіпати git.
