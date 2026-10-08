---
name: wallpaper-home-analysis
description: Конкурентний аналіз 08.10.2026 «шпалерні апки з роллю HOME» для нової апки-шпалер (за методикою Redwave) — артефакт, висновки, як оновити
metadata:
  type: project
---

08.10.2026 VELDAN задумав нову апку **шпалер з роллю «Додому»** за методикою [[redwave-app]] і попросив знайти в Play
шпалерні апки, на які у відгуках скаржаться «змінила головний екран / просила такий дозвіл».

**Артефакт:** https://claude.ai/artifact/9qYGx1Jur2dUUv9Na12Z16 (приватний; «Wallpaper Home Takeover», УКР/РУС/ENG,
«Підготував VELDAN»). Розділи: демо «перша хвилина» в телефоні з перемикачем «як у конкурентів / чесний варіант»,
scatter 955 апок (встановлення × частка скарг, клік → відгуки), картки шпалерних апок, студії + графік «місяців від
виходу → встановлення», теплова карта тем скарг, правила Google + таймлайн блокувань, висновки, «Тонкощі» ✔/◌/перевірка.

**Метод:** Play США, 1 361 апка з пошуку/схожих/каталогів розробників; 1 047 з ≥20 відгуками, 171 636 відгуків
(~200 найсвіжіших на апку, batchexecute `UsvDTd`); скарги — regex на 4 теми (forced/changed/uninstall/ads) + «virus».
Пастка: голе «launcher»/«wallpaper changed» дає шум (справжні лаунчери, апки-змінювачі шпалер) — не рахувати.

**Висновки:**
- Шпалерних апок із помітними скаргами лише 2 з 407, обидві Tools і обидві **не пишуть «лаунчер»** ні в назві, ні в описі:
  Easy Wallpaper – HD & Video (`com.novabrain.easy.wallpaper`, вийшла 06.07.2026, 1M+, 3,0★, 9,5%) і Live Wallpaper: 4K & Video
  (`com.easyapp.tool.live.wallpaper`, 25.12.2025, 1M+). Ще Live Launcher (чесно лаунчер, ті самі скарги) і Themify (скарги 2025).
- Масово схема — ігри/утиліти «<Гра> Home App / Home Screen / Launcher»: Hadiz Studio, Mr.Bunn, Gamnest, Hero Fighting Games,
  Homescreen Apps; 38 апок, у 26 скарг ≥5%; 10M+ за 5–13 місяців (Buns and Bounce — за 4,7); категорії Personalization/Tools.
- Рекомендація ◌ — «чесний варіант» (Launcher у назві, екран-пояснення з Maybe later, Uninstall, без реклами поверх чужих апок);
  більшість уже є в Redwave (home_required, is_uninstall).

**How to apply:** оновлення — `Artifact read` URL → правка → publish з `url`. Скрипти й дані були в scratchpad сесії і зникнуть
(`gp.py` reviews, `themes.py`, `build_data.py`, `template.html`). Копію в репо — лише на прохання ([[artifact-then-save-on-request]]).
Межа з [[sms-competitor-analysis]]: технічний блупринт прихованого захоплення лаунчера не робимо.
