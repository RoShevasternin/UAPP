---
name: uapp-naive-push-permission
description: UAPP — перед міграцією перевіряти MainActivity на наївний requestNotificationPermission() в onCreate; його треба замінити опт-іном через Biz.runStartupFlow
metadata:
  type: project
---

Деякі апки парку (знайдено в T9 `RBX RUSH`, 2026-09-09) уже мають у `MainActivity.onCreate` голий `requestNotificationPermission()` — системний діалог на сплеші. Android дає його один раз за встановлення: два відмови = дозвіл випалено назавжди.

При міграції такий виклик **прибирати** й заміняти опт-іном з нагородою (`Biz.runStartupFlow`), launcher лишати в активіті з колбеком.

**How to apply:** на початку кожної міграції `grep -n "requestNotificationPermission\|POST_NOTIFICATIONS" MainActivity.kt`. Див. [[uapp-onboarding-once]].
