# Driftglass — Privacy Policy (Cloudflare)

Адреса: **https://driftglass-privacy.0927920793ja.workers.dev/privacy** ◌ — у Play Console (App content → Privacy policy)
і в апці (`AppFlags.DEFAULT_PRIVACY_URL`, кнопка Settings → Privacy policy). Субдомен `0927920793ja` — припущення:
Cloudflare дає його новому акаунту при першому Worker; справжню адресу видно після Deploy.

Розробник — **Strategic Access Ventures** (Organization account, Play Account ID 5142898478012444545),
контакт — **0927920793ja@gmail.com** (VELDAN, 12.10.2026: нові апки — на нові акаунти).
Зроблено за зразком Redwave (`Game Redwave/privacy-policy/`): ті самі 11 розділів, текст під Driftglass.

**Не згадуємо (рішення VELDAN 12.10.2026):** Remote Config як такий і Custom Tab. Firebase лишається одним загальним
пунктом («Google Firebase … to keep the app working correctly» + installation ID): цей SDK справді збирає installation ID
і IP, а в Data safety (`market/LISTING.md` §4) він стоїть як Device IDs → App functionality — політика й анкета мають
збігатися, інакше Play відхиляє. Custom Tab даних не збирає (сторінку вантажить браузер) — його в політиці може не бути.

`public/privacy.html` і `public/index.html` — однаковий текст (корінь сайту теж відкриває політику).
`/privacy.html` Cloudflare сам переводить на `/privacy`.

## Як опублікувати вперше (як Redwave, ~3 хв)

Акаунт Cloudflare — новий, на **0927920793ja@gmail.com** (Strategic Access Ventures), не той, де `redwave-privacy`.
Якщо акаунта ще немає — зареєструвати безкоштовний на цю пошту.

1. https://dash.cloudflare.com → увійти → ліве меню **Compute (Workers) → Workers & Pages**
   (у старому меню — просто **Workers & Pages**). Новий акаунт при першому Worker попросить вибрати субдомен
   `*.workers.dev` — краще `0927920793ja` (тоді адреса збіжеться з уже вшитою в апку).
2. Кнопка **Create** (або **Create application**).
3. Вибрати варіант завантаження статичних файлів без Git — так само, як створювали `redwave-privacy`
   (у дашборді він підписаний як завантаження / drag and drop файлів) ◌ — назви кнопок Cloudflare час від часу міняє.
4. **Project / Worker name:** `driftglass-privacy` — саме так, від назви залежить адреса.
5. Перетягнути теку **`privacy-policy/public`** (саму теку з `index.html` і `privacy.html`) у поле завантаження → **Deploy**.
6. Відкрити адресу, яку покаже Cloudflare, + `/privacy` — має бути «Driftglass Privacy Policy». Якщо субдомен не `0927920793ja` — див. нижче.

Якщо адреса вийшла **інша** (напр. `driftglass-privacy.pages.dev` — це варіант Pages, теж підходить, або назва зайнята):
- Firebase → Remote Config → `driftglass_config` → додати поле `"privacy_url": "<нова адреса>"` → Publish
  (апка підхопить без нової збірки);
- поміняти адресу в `market/LISTING.md` (§3) і `AppFlags.DEFAULT_PRIVACY_URL` (до наступної збірки).

### Запасний шлях — CLI (якщо в дашборді немає завантаження файлів)

```bash
cd "Game Driftglass/privacy-policy"
npx wrangler login        # відкриє браузер — увійти в акаунт 0927920793ja@gmail.com
npx wrangler deploy       # бере wrangler.jsonc: Worker driftglass-privacy, файли з ./public
```

Наприкінці wrangler надрукує адресу `https://driftglass-privacy.<субдомен>.workers.dev`.

## Як оновити

1. Змінити текст у `public/privacy.html`, оновити «Effective date», скопіювати в `public/index.html`.
2. Cloudflare → Workers & Pages → `driftglass-privacy` → **New deployment** (або **Deployments → Upload**) →
   перетягнути теку `public` → Deploy. Або `npx wrangler deploy` з цієї теки.

Адреса не змінюється. Текст має збігатися з анкетою Data safety у Play Console (`market/LISTING.md`, §4).

## Що перевірити після публікації

- [ ] Сторінка відкривається з телефона без входу в акаунт.
- [ ] Апка: Settings → Privacy policy відкриває саме її.
- [ ] Play Console → App content → Privacy policy — та сама адреса.
