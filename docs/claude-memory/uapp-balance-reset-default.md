---
name: uapp-balance-reset-default
description: При міграції апки до еталону баланс існуючих гравців НЕ переносимо у Wallet — завжди з нуля (рішення користувача 2026-09-09 для всього парку)
metadata: 
  node_type: memory
  type: feedback
  originSessionId: a7be42b9-84b0-490a-8d12-eb48fbce3907
  modified: 2026-09-09T07:14:38.210Z
---

Питання №2 процедури ETALON_MIGRATION («перенести баланс чи з нуля») закрито для всього парку: **з нуля**. Своє сховище балансу (DS_Coin / PlayerData.rbx тощо) прибирається, Wallet стартує з 0.

**Why:** користувач 2026-09-09 (міграція T1 with ADS, `com.rbuxrds.counterds`) відповів «з нуля» і явно сказав, що це відповідь і для решти апок.

**How to apply:** не питати більше про баланс; лишаються тільки `google-services.json` (перевірити package_name) і повідомлення схеми диплінка. Див. [[uapp-employer-and-repo]].
