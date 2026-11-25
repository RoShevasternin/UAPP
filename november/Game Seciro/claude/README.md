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
- **Якість (build 16, після зауважень VELDAN):** звіряти ДВА режими: `proto-diff.py` (статичні пресети) і
  `proto-diff.py --live` (живі компоненти з обгортками; анімації вимкнено, rAF підмінено). Сторінка claude.ai має
  `img{max-width:100%}` — інструменти імітують це, а `.app img{max-width:none}` у shell обов'язковий (без нього
  зникали кнопки-картинки). Прокручені низи (скрол Dashboard, список локацій) перевіряти окремо — там живуть обрізані шари.
- **Переходи між екранами:** два шари (`leaving` + `StaticScreen`): вперед — новий заїжджає справа, старий іде на −30 % і
  тьмяніє; назад — дзеркально; між станами одного потоку (пейджер, вхід, VPN) — живий компонент без переходу.
- **Хвилі VPN:** процедурні (`Ripple`) за компонентом «elipse anim» Figma: 5 кіл 112 → 848 (крок 184), лінійно 5 с
  (підключення — 2.4× швидше), прозорість 0.09 підібрано по пікселях кадру.
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

## Залежності й питання до менеджера

**Кіт для менеджера** (3 мови, VELDAN): https://claude.ai/artifact/Hj3FPMn8KawTvA1gCBkAUa — вкладки «Прототип»
(посилання + мініатюри), «Робимо самі», «Від вас потрібно» (9 питань: факт ✔/◌ + що потрібно; відповіді «Є / Немає / Не знаю»
+ коментар зберігаються в db сторінки, колекція `answers/<id>`: api, subs, vpn, web, breach, qr, support, design, play).
Менеджеру потрібен доступ Contributor або Editor. Прочитати відповіді: ArtifactData list `answers`. Джерела фактів:
VpnService Play, HIBP від $4.39/міс, Safe Browsing не комерційний → Web Risk. Копію в репо — лише на прохання VELDAN.
- Інструкція Safari → **Google Chrome** (рішення VELDAN 24.11): логотип з «Apps Logo Set» (5:9492), кроки «Use secure DNS»;
  у текстах рендерера Safari → Chrome (`SUBST` у fig.jsx). Кроки залежать від того, чи буде наш DNS-сервер (питання в звіті).
- «vpn btn» 5:8681: варіанти 2→5 — дуга обертається за годинниковою (прототип: `vpn-arc-spin`).

## Чи все це реалізовне на Android (оцінка 24.11)

- UI, анімації, переходи, пейджер, аркуші — так, LibGDX (Scene2D + шейдери, як Orbit Dash).
- VPN — так: WireGuard у `VpnService`; системний дозвіл Android перед першим підключенням обов'язковий.
- Web Protection — НЕ як у макеті (розширення Safari на Android немає). Реально: фільтр DNS / доменів усередині нашого ж
  `VpnService` (Android дозволяє лише один VPN одночасно → блокування й VPN — один сервіс).
- QR-сканер — так: CameraX + ML Kit (штрихкоди) + перевірка посилання (Google Safe Browsing / Web Risk API, сервер).
  Камера — нативний шар Android поверх LibGDX.
- Storage Cleaner — так: MediaStore (READ_MEDIA_IMAGES / VIDEO), видалення через системне підтвердження `createDeleteRequest`.
- Breach Check — так, через API витоків (напр. Have I Been Pwned — платний ключ) і краще через наш сервер.
- Password Generator — так, локально (SecureRandom).
- Перевірки «Protection review» — Android-відповідники: блокування екрана (`KeyguardManager.isDeviceSecure`), біометрія
  (`BiometricManager`), рівень патча (`Build.VERSION.SECURITY_PATCH`), Play Protect, режим розробника / USB-налагодження.
- Вхід за поштою з кодом і перевірка підписки — потрібен бекенд (серверна команда); оплата — Google Play Billing.

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

## Документація сервера Seciro (прочитано 25.11)

Доступ: адмінка `account.seciro.app/admin` → Docs (VELDAN входить сам; адмінку лише читаємо). Сам текст API
**не комітимо** — репо UAPP публічне; локальна копія лише у scratchpad сесії.

- Один сервер, лише продакшн, тестових акаунтів немає — тестуємо на своїй пошті (ліміти коду: 1/хв, 3/15 хв).
- Вхід за кодом, сесія + продовження, перевірка доступу (сервер сам вирішує екрани: пейвол, закінчилась,
  збій оплати, пристрій вийшов, оновіть апку), Breach Check (на сервері), кабінет за одноразовим посиланням,
  телеметрія, знімок пристрою — **готові**.
- Блокери для Android: (1) сервер знає лише iOS-апки — потрібен Android-ідентифікатор; (2) RevenueCat лише App Store —
  потрібна Android-апка й продукти Play; (3) VPN віддає IPsec PSK + логін/пароль (iOS-формат) — потрібен IKEv2
  (Android 11+, системний профіль) або WireGuard (Android 10+); (4) немає джерела для фільтра Scam & Ad Blocker
  (у Chrome — «Безпечний DNS», потрібна адреса) і для вердикту QR (safe/warning/unsafe).
- Модулі без підписки: Protection Review, Password Generator; решта замкнені (у Figma стану замка немає).
- Кіт для VELDAN: https://claude.ai/artifact/EEF6zF7nZwhr2Vk9cTxsR3 (що це, статус модулів, тестові дані).
- Кіт менеджера (v6, 25.11) — 3 мови, 7 питань менеджеру + 6 пунктів для дизайнера (id у db: xapp, billing, vpn, testacc, filter, qrcheck, extras, d_lock, d_qr, d_status, d_alerts, d_web, d_android); раніше (v4): 8 питань після документації (текст + скріни, відповідь у полі `note`) — id відповідей у db `answers`:
  `xapp, billing, vpn, testacc, filter, qrcheck, extras, design`.
- Немає у Figma (25.11): замкнені модулі, QR safe/warning/не-посилання/без камери, «оновіть апку», «збій оплати», «пристрій вийшов», «немає інтернету», екрани Alerts і Get help. «Hide Cookie Prompts» на Android неможливий (DNS не ховає банери).
- Кіт дизайнера (25.11): https://claude.ai/artifact/PCBWKfSi31ZxWoQc3hFeHL — що домалювати (+ дозволи Android, іконка й матеріали Google Play), відповіді в db `answers`. У кіті менеджера тепер лише 7 питань.
- З 25.11 кіти менеджера й дизайнера лише для читання (без полів відповіді): відповіді приходять у чат VELDAN.
