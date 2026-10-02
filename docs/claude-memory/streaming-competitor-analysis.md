---
name: streaming-competitor-analysis
description: "Звіт 02.10.2026 «Стрімінги за рекламу» (вертикаль 2, Тарас): артефакт, висновки, копія в репо, як оновити"
metadata:
  type: project
---

02.10.2026 скоротили й перевірили дослідження Тараса.

- Оригінал Тараса: https://streaming-rewards-research.mrkolombo.chatgpt.site/
- Наш звіт для Ігоря: https://claude.ai/artifact/CPjEDiKFgmF2cdMFouV4z7 (приватний; потрібен Share)
- Мови: УКР / РУС / ENG; `#ru` / `#en` в посиланні.
- Копія: `docs/research/streaming-02-10-2026/`: `index.html` і `data/` (`apps.json`, `reviews-negative.json` — 1 996 відгуків, `search.json` — 10 запитів, 195 карток).

**Висновки:**
- «Netflix за рекламу» напряму ніхто не дає. Пошук «free netflix», «free spotify premium» і схожі запити в Play США видає лише офіційні застосунки. Стрімінги згадують в описах 5 reward-застосунків як одну з карток.
- Ринок — reward-платформи (Cash'em All 80 млн, Mode 58 млн, Mistplay 56 млн, JustPlay 40 млн, Freecash 35 млн). Гроші приносять ігрові офери, а не реклама: у Freecash один офер у Monopoly Go оцінено до $215.
- Калькулятор: картка $15 при eCPM $15 і половині доходу на нагороду дорівнює 2 000 роликів, тобто ≈17 годин реклами.
- Правила:
  - AdMob: подарункові картки — це «direct monetary», за rewarded-рекламу їх давати не можна за жодних умов;
  - монетки всередині гри дозволені з умовами;
  - Spotify більше не продає подарункові картки.
- У 30–66 % негативних відгуків пишуть «не віддали». У Google Opinion Rewards таких 7 %, це контроль.
- **Рекомендація ◌:** у RBX-ігри картки не додаємо. Якщо йти у вертикаль, то окремою reward-платформою з офервол-партнером, карткою Google Play і пілотом на одному ринку.

**Як оновити.** Робочі файли лежали в scratchpad сесії: `body.html` і `extra.css`; CSS бралося з TikTok-звіту. Джерело правди — артефакт: `Artifact read` → `publish` з `url`. Після правки перезберегти й теку.

Див. [[uapp-verticals-briefs]], [[tiktok-competitor-analysis]], [[artifact-then-save-on-request]].
