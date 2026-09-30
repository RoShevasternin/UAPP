---
name: uapp-employer-and-repo
description: UAPP is the company Rostislav works for; ~/Apps/UAPP is one git repo holding many separate games/apps built for it.
metadata:
  type: project
---

UAPP — це компанія, в якій працює користувач. Тека `~/Apps/UAPP` — один git-репозиторій, що містить багато **окремих, не пов'язаних між собою** ігор/додатків для цієї компанії, розкладених по місяцях/назвах (`agust/Game T35/Mindora Self Test`, `june/Game T1-1/...` тощо).

**Why:** пам'ять Claude Code прив'язана до кореня git-репо (`-Users-admin-Apps-UAPP`), тож усі ігри ділять одну теку пам'яті, хоча технічно це різні проєкти.

**How to apply:** факти про конкретну гру записувати з префіксом проєкту в `name` (напр. `t35-...`), щоб не переносити їх помилково на іншу гру. Спільними вважати лише факти про компанію, стек і робочі звички.
