# Framefall — Privacy Policy (Cloudflare)

Адреса: **https://framefall-privacy.caps89temitop89.workers.dev/privacy** ◌ — у Play Console (App content → Privacy policy)
і в апці (`AppFlags.DEFAULT_PRIVACY_URL`, кнопка Settings → Privacy Policy). Субдомен `caps89temitop89` — припущення:
Cloudflare дає його новому акаунту при першому Worker; справжню адресу видно після Deploy.

Розробник — **CAPSLOCK LTD**, контакт — **caps89temitop89@gmail.com** (Temitope Oluwadamilola Lasade Anderson;
VELDAN, 13.10.2026: нові апки — на нові акаунти). Зроблено за зразком Driftglass, 12 розділів.

**Не згадуємо (рішення VELDAN 12.10.2026):** керування функціями з сервера і Custom Tab. Наш сервіс описано тим, що він
справді отримує (app instance ID, джерело встановлення з Play, версія, базові події, токен пушів) і навіщо (робота апки,
аналітика, кампанії, пуші) — так само, як у Data safety (`../market/LISTING.md` §4). Політика й анкета мають збігатися.

`public/privacy.html` і `public/index.html` — однаковий текст (корінь сайту теж відкриває політику).

## Як опублікувати вперше (~3 хв)

Акаунт Cloudflare — новий, на **caps89temitop89@gmail.com** (CAPSLOCK LTD). Якщо акаунта ще немає — зареєструвати
безкоштовний на цю пошту.

1. https://dash.cloudflare.com → увійти → **Compute (Workers) → Workers & Pages**. При першому Worker Cloudflare
   попросить субдомен `*.workers.dev` — вибрати **`caps89temitop89`** (тоді адреса збіжеться з уже вшитою в апку).
2. **Create** → варіант із завантаженням статичних файлів (drag and drop), без Git.
3. **Name:** `framefall-privacy` — від назви залежить адреса.
4. Перетягнути теку **`privacy-policy/public`** → **Deploy**.
5. Відкрити `https://framefall-privacy.caps89temitop89.workers.dev/privacy` з телефона — має бути «Framefall Privacy Policy».

Якщо адреса вийшла **інша** (субдомен зайнятий або варіант Pages `*.pages.dev`): поміняти її в
`Framefall/app/src/main/java/com/vidtools/framefall/core/config/AppFlags.kt` (`DEFAULT_PRIVACY_URL`) і в
`../market/LISTING.md`, перезібрати AAB (`../release/build_aab.sh`) — **до** завантаження в Play.

### Запасний шлях — CLI

```bash
cd "Game Framefall/privacy-policy"
npx wrangler login        # увійти в акаунт caps89temitop89@gmail.com
npx wrangler deploy       # Worker framefall-privacy, файли з ./public
```

## Як оновити

1. Змінити текст у `public/privacy.html`, оновити «Effective date», скопіювати в `public/index.html`.
2. Cloudflare → Workers & Pages → `framefall-privacy` → **New deployment** → перетягнути теку `public` → Deploy.
