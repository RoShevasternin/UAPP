# Seciro — усе для роботи Claude

Апка безпеки «все в одному» за підпискою: VPN, Web Protection, QR-сканер, Storage Cleaner,
Breach Check, генератор паролів. Kotlin + LibGDX. Початок — 24.11.2026 (VELDAN).

## Головне правило: спершу прототип

1. Екран спершу робимо в **прототипі** (`../prototype/`) і публікуємо на claude.ai.
2. VELDAN проклацує й править. Поки прототип не прийнято, у Kotlin екран не переносимо.
3. Перенос у `../Seciro/` (Kotlin + LibGDX) → перевірка на **десктопі** (`sh ./gradlew :desktop:run`,
   вікно 375×812) → лише потім **реальний Android по USB**.

## Теки гри

| тека | що там |
|---|---|
| `claude/` | це: правила, дані з Figma, інструменти Claude |
| `prototype/` | клікабельний прототип (React): `seciro.jsx` → `build.py` → `seciro.html` |
| `Seciro/` | сам проєкт: `core` (уся апка без Android), `desktop` (вікно Мака), `android` (Play) |
| `release/` | для VELDAN: ключі `.jks`, паролі, усе для підпису |
| `market/` | для VELDAN: те, що публікується — `results/icons`, `results/screenshots`, `results/aab` |

## Прототип

- Живий: https://claude.ai/artifact/9z6HEgMhdhdYMEeTSKiwwZ (акаунт overchenkolila). Оновлювати — публікація
  того самого файлу `prototype/seciro.html`; `BUILD` у `seciro.jsx` +1 на кожну публікацію.
- Збірка: `python3 prototype/build.py` (esbuild і React качає сам у `prototype/.tools/`, не в git).
- Поруч із телефоном — кадр Figma того самого екрана (paywall — під обраний тариф), «Compare» накладає
  Figma поверх прототипу з прозорістю. Новий екран → кадр у `figma/frames` і `frame` у `SCREENS` обов'язково.
- Окремий екран за посиланням: `…/artifact/…#paywall` (id — `SCREENS` у `seciro.jsx`).
- **Звірка піксель у піксель:** `python3 claude/tools/proto-diff.py [екран]` → таблиця різниці з Figma і
  `claude/tools/out/diff-<екран>.png` (Figma | прототип | різниця). Норма після build 6: онбординг 0.3–0.6 %,
  paywall ~1 %. Новий екран — додати в `SHOTS`.
- **Уроки точності (24.11):** ілюстрації — PNG @3x з Figma (`figma/art3x`, позиції в `map.json`), бо SVG-експорт
  Figma губить обрізання круглих рамок; тексти брати з `chars` Figma дослівно — там бувають ручні переноси U+2028
  (Shift+Enter) → `\n`; Inter — `opsz` 14 + `-webkit-font-smoothing: antialiased` (інакше вужче й жирніше за Figma);
  стиль тексту — перевіряти й `textDecoration` / `textCase` (`getStyledTextSegments`): у JSON шарів їх немає, так
  пропустили підкреслене «Use a different email»; підкреслення AUTO у Figma для Inter 14 = 3 px нижче базової, 0.5 px
  (браузерне auto вище й товще — заміряно по кадру); рамка «всередину» — `inset box-shadow`, не `border` (border зсуває вміст на 1 px). Те саме врахувати в Kotlin.
- **Рух (build 7):** онбординг — один пейджер (вступ + 5 сторінок): свайп пальцем / мишею з пружиною (k 210, згасання 0.92),
  гумовий край, змах за інерцією; крапки й підпис кнопки — за безперервною позицією; ілюстрації пошарово
  (`figma/layers`, роль шару з імені / розміру: пляма, земля, велике, дрібне, іскра) з'являються каскадом за прогресом
  свайпу + «дихання» в спокої; splash → логотип летить у шапку; paywall — каскад знизу. У спокої кадр = Figma
  (`proto-diff.py`), рух — `proto-swipe-test.py` (headless «палець»). Кадр посеред свайпу: `#onb1@1.5!raw`.
- Знімок без claude.ai: `sh claude/tools/proto-shot.sh paywall /tmp/pw.png 900 1000` (headless Chrome).
- Зроблено: ряд 1 — splash → онбординг (intro + 5) → paywall (3 тарифи, карусель можливостей, мок вікна Google Play).
  Ряд 2 (build 9) — вхід за поштою: Sign in → код з 6 цифр → Verifying → активна / скінчилась / немає підписки,
  «Can't connect» аркушем. Клавіатура — картинка з Figma (iOS) з клікабельними клавішами + фізична клавіатура Мака.
  Відповіді «сервера» — у панелі (Server answers). Автотест потоку — `claude/tools/proto-auth-test.py`.
  Ряд 3 (build 13) — Dashboard (гаразд / 2 проблеми), сканування (кільце + чекліст за 7 варіантами компонента
  checklist), Result (пройдено / 2 проблеми) з аркушами-інструкціями Safari і Software Update. Панель: «Scan finds».
  Автотест — `claude/tools/proto-r3-test.py`.
  Ряд 4 (build 15) — VPN: перший вхід → знайомство → системний дозвіл Android (мок, у Figma немає) → підключення →
  таймер і живі швидкості → список локацій (вибір, зірочки, Done = перепідключення) → вимкнення. Автотест —
  `claude/tools/proto-vpn-test.py`. Далі: ряд 5 — Web Protection (8 кадрів).

### Рендерер кадрів Figma (з ряду 3) — `prototype/fig.jsx`

Екран малюється прямо з дерева шарів: збирач v2 у вкладці Figma (див. `figma/README.md`) кладе
`figma/r<N>/<кадр>.json` (ax/ay від кадру, заливки, рамки, тіні, тексти з runs) і PNG @3x усього векторного
(дедуплікація за хешем). `build.py` → `.tools/fig.gen.js`. Поведінку додає екран через overrides
(`{[id|ім'я]: false | {onClick, className, style} | (node, el, pos) => el}`). Leading trim Figma → `text-box: trim-both cap alphabetic`.
Ряди 1–2 зроблено вручну (лишаються як є); з ряду 3 — лише рендерер + поведінка.
Пастки (ряд 4): шари, обрізані кадром (низ скрол-списків), не експортуються → брати PNG головного компонента
(`r4/missing.json`, `r4/flags.json`); екземпляр компонента ≠ компонент за полями (прапори) → видимі брати з екземпляра,
обрізані — з дитини компонента; м'які градієнти PNG важать мегабайти → JPEG на тлі кадру (`build.py` / ручний крок);
кадри Figma в колонці порівняння — JPEG-копії (`.tools/frames`), межа артефакту 16 МБ. У тестах headless вимикати
CSS-переходи й анімації (інакше елементи «застигають» посеред руху).

## Figma

- Файл «UAPP 2.0», секція **Seciro All-in-One `5:1685`**, 75 кадрів 375×812. Порядок — рядами зліва направо,
  потім ряд нижче. Карта рядів — у пам'яті `vpn-app-new-type`.
- **Без Figma MCP** (платний ліміт). Шлях: вбудований браузер уже залогінений → у консолі сторінки є `figma`
  (Plugin API) → `exportAsync` SVG/PNG, оригінали картинок `getImageByHash`. Багато файлів — **одним tar**
  (див. `figma/README.md`): кілька завантажень підряд браузер губить.
- `figma/frames/` — PNG кадрів @2x (еталон для звірки), `figma/json/` — дерево шарів з числами,
  `figma/art/` — ілюстрації SVG 1:1, `figma/icons/` — Material-іконки (колір задає код).

## Дизайн → код (як в Orbit Dash)

- Число, якого немає в макеті, не вигадуємо — читаємо з `figma/json` або питаємо.
- Шрифти: Inter Medium / Bold, Inter Tight Medium / Bold → MSDF. SF Pro з макета (ліцензія Apple) → **Inter Tight**
  з інтервалом під ширину Figma (рядок «Auto-Renewable…»: −0.13 px = 203 px, як у Figma) — так на вигляд один в один.
- Статус-бар «9:41» і home-індикатор — частина iOS-макета, на Android їх малює система.
- Тіні (~500) і блюр — запікати або `VfxTexture` + `BlurEffect` за зразком Orbit Dash
  (`/Users/admin/Apps/Game Orbit Dash PRIVATE/Orbit Dash/docs/vfx.md`, `msdf-shapes.md`).

## Відкриті питання до VELDAN

- Ряд 3 увесь про iPhone: Safari, Software Update iOS, Face ID, «Your iOS is up to date». Для Android перевірки
  й інструкції треба переписати (Chrome / Play Protect / оновлення Android / розблокування) — питання до дизайну.
- Описки в кадрі 5:1868: «Rewiew your settings», «Need attantion» (у 5:1784 правильно — у прототипі беремо правильно).

- Кадр «Verifying…» (5:4502) — поле коду з 8 клітинок, решта кадрів — 6. Зроблено 6 (схоже на описку в макеті).
- «No subscription on this email» — після пошти чи після коду? Текст «If this email has a subscription, we’ve sent a code»
  означає, що без підписки код не надсилається → у прототипі показуємо одразу після пошти.

- Тексти макета кажуть «iPhone» («Your iPhone, Protected», «…space on your iPhone») — для Android замінити.
- `applicationId` поки `com.seciro.app` — остаточний треба до першого завантаження в Play.
- Ціни й пробний період з макета ($0.99 за 3 дні, $8.99 / $29.99 / $69.99) — у Play задаються в консолі.
