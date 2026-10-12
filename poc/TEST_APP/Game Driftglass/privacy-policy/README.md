# Driftglass — Privacy Policy (Cloudflare)

Адреса (план): **https://driftglass-privacy.oyutetijep68.workers.dev/privacy** — у Play Console (App content → Privacy policy)
і в апці (`AppFlags.DEFAULT_PRIVACY_URL`, кнопка Settings → Privacy policy; інша адреса без збірки — поле `privacy_url` у Remote Config).
Розробник — STAR ADS LLC, контакт — oyutetijep68@gmail.com. Зроблено 12.10.2026 за зразком Redwave.

❗ **Ще не викладено.** Як у Redwave: Cloudflare (акаунт oyutetijep68@gmail.com) → Workers & Pages → Create →
Upload static files → назва проєкту **`driftglass-privacy`** → перетягнути теку `public` → Deploy. Адреса стане такою, як вище.
Якщо назва зайнята й адреса вийде інша — поміняти `AppFlags.DEFAULT_PRIVACY_URL` (або `privacy_url` у Remote Config) і `market/LISTING.md`.

`public/privacy.html` і `public/index.html` — однаковий текст (корінь сайту теж відкриває політику).

## Як оновити

1. Змінити текст у `public/privacy.html`, оновити «Effective date», скопіювати в `public/index.html`.
2. Cloudflare → Workers & Pages → `driftglass-privacy` → **New deployment** → перетягнути теку `public`.

Текст має збігатися з анкетою Data safety у Play Console (`market/LISTING.md`, §4).
