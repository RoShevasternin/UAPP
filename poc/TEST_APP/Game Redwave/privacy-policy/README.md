# Redwave — Privacy Policy (GitHub Pages)

Сторінка для Google Play і кнопки **Settings → Privacy Policy** в апці. Розробник — STAR ADS LLC,
контакт — oyutetijep68@gmail.com. Хоститься окремим репозиторієм на GitHub Pages (рішення VELDAN 07.10.2026).

`public/privacy.html` і `public/index.html` — однаковий текст (корінь сайту теж відкриває політику).

## Як викласти

1. GitHub → **New repository**, публічний (напр. `redwave-privacy`).
2. **Add file → Upload files** → завантажити `public/index.html` і `public/privacy.html` → Commit.
3. **Settings → Pages** → Source: *Deploy from a branch* → Branch: `main`, папка `/ (root)` → Save.
4. Через 1–2 хв сторінка відкривається за адресою `https://<логін>.github.io/redwave-privacy/privacy.html`.
5. Цю адресу:
   - вставити в Play Console → App content → Privacy policy;
   - додати в Remote Config `redwave_config` полем `"privacy_url": "https://…/privacy.html"`;
   - прописати в коді як `RemoteFlags.DEFAULT_PRIVACY_URL` (щоб працювало й офлайн / до першої відповіді Firebase).

Змінилась поведінка апки (дозволи, SDK, дані) — оновити текст і «Effective date», завантажити файли знову.
Текст має збігатися з анкетою Data safety у Play Console.
