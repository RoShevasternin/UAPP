---
name: uapp-onboarding-once
description: UAPP — онбординг/візард перед меню показується лише при першому запуску; прапорець ставиться коли юзер уперше дійшов до меню (правило для всіх апок, 2026-09-09)
metadata:
  type: feedback
---

При міграції апки до еталону: якщо перед меню є онбординг (`Select_1_Screen` у T1 тощо), він має показуватись **один раз**. Прапорець `Onboarding.markDone()` — у `MainScreen.show()`, читання в `LoaderScreen` при виборі першого екрана; `MainScreen` додати в `noAdScreens`.

**Why:** користувач 2026-09-09 (T1 with ADS) попросив зробити так і явно сказав записати в інтеграцію, бо стосується решти апок.

**How to apply:** процедура `docs/procedures/ETALON_MIGRATION.md`, розділ 5б — робити за нею, не питати. Див. [[uapp-balance-reset-default]].
