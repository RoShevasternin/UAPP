# Redwave — Privacy Policy (Firebase Hosting)

Сторінка для Google Play і для кнопки **Settings → Privacy Policy** в апці.
Адреса після деплою: **https://redwave-original.web.app/privacy** — саме її апка відкриває за
замовчуванням (`RemoteFlags.DEFAULT_PRIVACY_URL`). Іншу адресу можна задати полем `privacy_url`
у Remote Config, без нової збірки.

## Перед деплоєм

У `public/privacy.html` замінити заглушки:
- `DEVELOPER_NAME` — назва акаунта розробника в Play Console;
- `CONTACT_EMAIL` — пошта для запитів (та сама, що в Play Console → Store listing → Contact details).

Якщо зміниться поведінка апки (нові дозволи, SDK, аналітика, реклама) — оновити відповідний розділ
і дату «Effective date». Текст має збігатися з анкетою Data safety в Play Console.

## Деплой (один раз поставити CLI)

```bash
npm install -g firebase-tools
firebase login
cd "/Users/admin/Apps/UAPP/poc/TEST_APP/Game Redwave/privacy-policy"
firebase deploy --only hosting
```

Проєкт — `redwave-original` (`.firebaserc`). Перевірити: відкрити https://redwave-original.web.app/privacy.
