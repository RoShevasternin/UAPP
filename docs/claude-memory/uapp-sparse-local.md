---
name: uapp-sparse-local
description: З 24.11.2026 локальний UAPP — sparse-клон (лише корінь, docs, november); старі місяці й poc лише на GitHub
metadata:
  type: project
---

24.11.2026 VELDAN попросив прибрати UAPP з Мака, але щоб усе лишилось на GitHub. Тепер
`/Users/admin/Apps/UAPP` — partial + sparse клон (`--filter=blob:none`, no-cone):
`/*` крім `agust/ april_may/ june/ july/ etalon/ poc/`. Нові теки (november, далі december…)
підхоплюються автоматично і пушаться як звичайно.

**Why:** звільнити місце (було 2,7 ГБ, стало ~12 МБ), а історію й файли тримати на GitHub.

**How to apply:** якщо треба стара гра — повернути теку, напр. `git sparse-checkout add '/june/'`
(або все: `git sparse-checkout disable`). Не дивуйся, що шляхів з CLAUDE.md (еталон
`june/Game T1-1`, poc/TEST_APP) немає на диску — спершу повернути теку. Див. [[uapp-git-backup-public-repo]].
