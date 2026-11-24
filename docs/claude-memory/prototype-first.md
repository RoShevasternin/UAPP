---
name: prototype-first
description: Нові апки (Seciro та далі) — спершу клікабельний прототип на claude.ai, VELDAN проклацує, лише потім Kotlin і тести (десктоп → Android по USB)
metadata:
  type: feedback
---

VELDAN 24.11.2026: «важливе правило — спочатку все в прототип, потім реалізація і тести», як в Orbit Dash.

**Why:** VELDAN хоче сам поклацати екрани й потік до того, як на них піде робота в коді; правки в
прототипі дешеві, у Kotlin — ні.

**How to apply:** кожен новий екран — спершу в `prototype/` гри (React → `build.py` → публікація на claude.ai
тим самим URL), чекати прийняття; лише тоді перенос у Kotlin + LibGDX → `:desktop:run` на Маці → телефон по USB.
Seciro: [[vpn-app-new-type]]; зразок — `Game Orbit Dash PRIVATE/prototype`.
