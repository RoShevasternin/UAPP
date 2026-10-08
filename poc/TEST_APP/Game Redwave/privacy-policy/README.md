# Redwave — Privacy Policy (Cloudflare)

Адреса: **https://redwave-privacy.oyutetijep68.workers.dev/privacy** — у Play Console (App content → Privacy policy) і в апці
(`RemoteFlags.DEFAULT_PRIVACY_URL`, кнопка Settings → Privacy Policy). Розробник — STAR ADS LLC,
контакт — oyutetijep68@gmail.com. Викладено 07.10.2026 у Cloudflare (Workers & Pages → `redwave-privacy`,
акаунт oyutetijep68@gmail.com). `/privacy.html` Cloudflare сам переводить на `/privacy`.

08.10.2026 додано розділ 5 «Advertising (Google AdMob)» і дозвіл Advertising ID (у Cloudflare — після New deployment).

`public/privacy.html` і `public/index.html` — однаковий текст (корінь сайту теж відкриває політику).

## Як оновити

1. Змінити текст у `public/privacy.html`, оновити «Effective date», скопіювати в `public/index.html`.
2. Cloudflare → Workers & Pages → `redwave-privacy` → **New deployment** → перетягнути теку `public`.

Адреса не змінюється. Текст має збігатися з анкетою Data safety у Play Console.
Іншу адресу без нової збірки можна задати полем `privacy_url` у Remote Config.
