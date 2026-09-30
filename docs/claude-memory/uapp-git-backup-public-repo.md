---
name: uapp-git-backup-public-repo
description: "UAPP git = повна резервна копія: усе, включно з .jks і google-services.json, іде на GitHub; не йдуть лише build і сміття"
metadata:
  node_type: memory
  type: project
  originSessionId: 0fab2234-9eb3-4f63-8c89-bd65c69e0fe6
  modified: 2026-09-29T12:26:16.971Z
---

GitHub для користувача — це резервна копія на випадок, «коли ноут згорить». Тому все з `/Users/admin/Apps/UAPP` має потрапляти в репо `github.com/RoShevasternin/UAPP`.

- **Ідуть у git:** `.jks` / `.keystore` і `google-services.json`. Це рішення користувача, прямо: «хай ідуть на GitHub». Не додавати їх у `.gitignore` і не виключати з комітів.
- **Не йдуть:** `build/`, `.gradle/`, `.idea/`, `.cxx/`, `*.apk`, логи, `local.properties` (там лише шлях до SDK), `.DS_Store`. Усе це вже є в `.gitignore`.
- **Нові теки** (дослідження тощо) називати англійською, дату писати так: `sms-29-09-2026`.

**Why:** Користувач хоче, щоб на GitHub було все потрібне для відновлення проєктів, зокрема ключі підпису й конфіги Firebase.

**How to apply:**
- Комітимо все, крім переліченого сміття, і не питаємо окремо про ключі чи конфіги.
- 29.09.2026 я попередив, що репо публічне. Користувач рішення знає, повторно не переконувати.
- `.jks` зараз лежать поза UAPP: `~/Apps/*/release/`. Переносити їх в UAPP лише на прохання користувача.
