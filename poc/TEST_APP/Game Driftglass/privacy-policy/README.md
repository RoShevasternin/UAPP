# Driftglass — Privacy Policy (Cloudflare)

Адреса: **https://driftglass-privacy.oyutetijep68.workers.dev/privacy** — у Play Console (App content → Privacy policy)
і в апці (`AppFlags.DEFAULT_PRIVACY_URL`, кнопка Settings → Privacy policy). Розробник — STAR ADS LLC,
контакт — oyutetijep68@gmail.com. Зроблено 12.10.2026 за зразком Redwave (`Game Redwave/privacy-policy/`):
ті самі 11 розділів, текст під Driftglass (шпалери на пристрої, Firebase Remote Config, AdMob, Custom Tab, роль HOME).

`public/privacy.html` і `public/index.html` — однаковий текст (корінь сайту теж відкриває політику).
`/privacy.html` Cloudflare сам переводить на `/privacy`.

## Як опублікувати вперше (як Redwave, ~3 хв)

Акаунт Cloudflare — **oyutetijep68@gmail.com** (той самий, де живе `redwave-privacy`).

1. https://dash.cloudflare.com → увійти → ліве меню **Compute (Workers) → Workers & Pages**
   (у старому меню — просто **Workers & Pages**).
2. Кнопка **Create** (або **Create application**).
3. Вибрати варіант завантаження статичних файлів без Git — так само, як створювали `redwave-privacy`
   (у дашборді він підписаний як завантаження / drag and drop файлів) ◌ — назви кнопок Cloudflare час від часу міняє.
4. **Project / Worker name:** `driftglass-privacy` — саме так, від назви залежить адреса.
5. Перетягнути теку **`privacy-policy/public`** (саму теку з `index.html` і `privacy.html`) у поле завантаження → **Deploy**.
6. Відкрити https://driftglass-privacy.oyutetijep68.workers.dev/privacy — має бути «Driftglass Privacy Policy».

Якщо адреса вийшла **інша** (напр. `driftglass-privacy.pages.dev` — це варіант Pages, теж підходить, або назва зайнята):
- Firebase → Remote Config → `driftglass_config` → додати поле `"privacy_url": "<нова адреса>"` → Publish
  (апка підхопить без нової збірки);
- поміняти адресу в `market/LISTING.md` (§3) і `AppFlags.DEFAULT_PRIVACY_URL` (до наступної збірки).

### Запасний шлях — CLI (якщо в дашборді немає завантаження файлів)

```bash
cd "Game Driftglass/privacy-policy"
npx wrangler login        # відкриє браузер — увійти в акаунт oyutetijep68@gmail.com
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
