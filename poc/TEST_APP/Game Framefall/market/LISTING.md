# Framefall — що вписати в Google Play Console

Підготовлено 13.10.2026 для першої публікації, за зразком Redwave і Driftglass. Тексти маркету англійською
(маркет США), пояснення українською. ✔ — перевірено в коді апки або в офіційній таблиці Google,
◌ — рішення чи припущення, яке варто звірити в самій консолі.

Графіка — у цій теці, опис файлів — [README.md](README.md). AAB — `framefall v1.aab` (тут же).

**Акаунт Google Play (VELDAN, 13.10.2026): CAPSLOCK LTD** — контакт `caps89temitop89@gmail.com`
(Temitope Oluwadamilola Lasade Anderson). Нові апки — на нові акаунти; Redwave — STAR ADS LLC, Driftglass — Strategic Access Ventures.

---

## 0. Що завантажити

| Поле | Файл / значення |
|---|---|
| App bundle | `framefall v1.aab` — `com.vidtools.framefall`, 1.0.0 (1), підписаний `../release/keyFramefall.jks` |
| Play App Signing | **Use Google-generated key** (рекомендовано). Наш ключ стає upload key: SHA-256 `4A:07:B2:67:F6:E7:A0:2F:2C:3B:E1:74:C9:C5:85:32:BC:56:BC:16:1F:23:F4:68:2A:F1:6C:75:82:3A:3E:1E` |
| Нова збірка | `sh release/build_aab.sh` (з теки `Game Framefall`): тести → `bundleRelease` → підпис → `market/framefall v<versionCode>.aab`. Перед нею підняти `versionCode` / `versionName` в `Framefall/app/build.gradle.kts` |

⚠️ `release/keyFramefall.jks` + пароль у `release/meta-data.txt` — **єдиний upload key**. Втратиш — лише скидання через підтримку Play.

---

## 1. Store listing (Main store listing)

### App name — 30 / 30

```
Framefall: Video Home Launcher
```

Рішення VELDAN 13.10.2026. Як у Redwave: «Home Launcher» замість «Downloader» — на «Downloader» модерація
дивиться пильніше (YouTube-завантажувачі блокують). ◌ Перевірити пошуком у Play, що назва вільна
(12.10.2026 «Framefall» не знайдено).

### Short description — 76 / 80

```
Home screen with a moving background. Save videos from links, trim and loop.
```

Запасні варіанти (76 кожен):
- `A Home screen launcher with a video background. Save, watch and edit videos.`
- `Copy a video link, press Home, save it. Video background, player and editor.`

### Full description

```
Framefall turns your Home screen into a small cinema. A scene from your own videos plays softly behind your apps, the videos you are watching wait on Home, and any video link you copy is one tap away from your phone.

A HOME SCREEN THAT MOVES
• A living video background: built-in loops or a scene you make from your own video
• Large clock and date on top of it
• Copied a link to a video? Press Home and save it in one tap
• Continue watching: your last videos right on Home
• Dock with your default Phone, Messages, Browser and Camera apps
• All apps list with every app on your phone
• Easy to switch: Framefall → Settings → Use as Home screen, or Android Settings → Apps → Default apps → Home app

SAVE VIDEOS YOU ARE ALLOWED TO KEEP
• Paste a direct link to an MP4, WebM, MOV or MKV file
• Your own files shared from Google Drive, Dropbox and OneDrive
• Internet Archive, NASA and Wikimedia Commons pages: choose the quality and save
• Share a link to Framefall from any app
• Saved to Movies/Framefall, so your Gallery sees the files too

Framefall does not support services whose terms forbid downloads, such as YouTube, Instagram, TikTok, Facebook, X or Vimeo.

OPEN MOVIES, FREE TO KEEP
• Discover: Blender Foundation films under Creative Commons and public-domain classics, including NASA footage
• Every film shows its license and its authors

A PLAYER MADE FOR YOUR LIBRARY
• Picks up where you left off
• Skip 10 seconds, change the speed, loop a video, go full screen
• Library with Videos, Audio and Studio filters

STUDIO, RIGHT ON YOUR PHONE
• Trim the part you need
• Save the sound as M4A
• Make a GIF of up to 6 seconds
• Split a video into equal parts for Status and Stories
• Save any moment as a photo
• Compress a video to send it faster
• Turn a scene into your Home screen background

PRIVACY
• No account needed
• Your videos and library stay on your phone, and all editing happens on the device
• Framefall is free and supported by ads
```

✔ Кожен пункт звірено з кодом: формати — `LinkResolver.VIDEO_EXT`, заблоковані сервіси — `LinkResolver.BLOCKED`,
Archive / NASA / Commons — `OpenArchives`, `Commons.directUrl`; Studio — `Copy.Studio.TOOLS` і `StudioEngine`;
плеєр — `PlayerScreen` (±10 с, швидкість, Loop, повний екран, продовження з місця); фільтри — `Copy.Library.FILTERS`.
«Dock … default apps» — `dockApps()`; «All apps» — `listApps()` через `<queries>`, без `QUERY_ALL_PACKAGES`.

### Графіка

| Поле | Файл | Вимога Play |
|---|---|---|
| App icon | `icon_512.png` | 512×512, PNG, без альфи ✔ |
| Feature graphic | `feature_1024x500.png` | 1024×500, без альфи ✔ |
| Phone screenshots | `screenshot_01.png` … `_06.png` | 2–8 шт., 9:16, 1080×1920, без альфи ✔ |
| Tablet screenshots | — | необов'язково; апка лише портретна для телефона — пропускаємо |
| Video | — | необов'язково |

Порядок кадрів: 01 Home ловить лінк → 02 Save → 03 Studio → 04 плеєр → 05 Discover → 06 Library.
Усі кадри — справжні екрани апки з Redmi 13.10.2026; фільми — відкриті (Blender CC BY 3.0, NASA, суспільне надбання).

---

## 2. Store settings

| Поле | Значення |
|---|---|
| App or game | **App** |
| Category | **Video Players & Editors** ◌ (точніше за Tools: плеєр + Studio). Альтернатива — Tools (як Redwave) або Personalization (лаунчер) |
| Tags | ◌ до 5: **Video player, Video editor, Launcher, Personalization, Video downloader** (якщо такого тегу немає — Movies & TV). Остаточно — що запропонує консоль |
| Contact email | `caps89temitop89@gmail.com` |
| Website | необов'язково |
| Phone | необов'язково |
| External marketing | лишити увімкненим (за замовчуванням) |

---

## 3. App content (Policy → App content)

| Розділ | Відповідь |
|---|---|
| Privacy policy | `https://framefall-privacy.caps89temitop89.workers.dev/` ✔ опубліковано 13.10.2026 (`/privacy` — та сама сторінка, вона вшита в апку) |
| Ads | **Yes, my app contains ads** |
| App access | **All functionality is available without special access** ◌ — логіна немає; див. «Тонкощі» про роль HOME |
| Content rating | анкета IARC, §3.1 |
| Target audience | **18 and over**, §3.2 |
| News apps | No |
| Health apps | My app does not have any health features |
| Financial features | My app doesn't provide any financial features |
| Government apps | No |
| Data safety | §4 |
| Advertising ID | **Yes** — Advertising or marketing, Analytics, Fraud prevention, security and compliance ✔ (`AD_ID` у маніфесті; AdMob і TikTok SDK) |
| Foreground service permissions | ◌ **Не потрібно**: у фінальному маніфесті немає служб з `foregroundServiceType` (заміряно в merged manifest 13.10.2026; `FOREGROUND_SERVICE` додає WorkManager без типу). Якщо консоль усе ж спитає — відповісти, що апка не використовує типізованих foreground services |

### 3.1 Content rating (IARC)

- Категорія — **All Other App Types** (утиліта, не гра й не соцмережа).
- Violence / Sexuality / Language / Controlled substances / Gambling — **No**.
- Users interact or exchange content — **No**. Shares location — **No**. Digital purchases — **No**.
- Unrestricted internet access (web browser / search engine) — **No** ◌: Framefall не браузер, завантажує лише файли
  за лінком, який вставила людина, і з вибраного каталогу.

Очікуваний результат: **Everyone / PEGI 3 / USK 0** ◌ (каталог: класичні хорор-фільми Nosferatu і Night of the Living
Dead у Discover — це вбудований контент; якщо IARC спитає про лякливий контент у каталозі — чесно «Yes, mild», рейтинг
стане Teen / PEGI 12; або прибрати ці два фільми з `catalog.json` до релізу).

### 3.2 Target audience

**18 and over**: у апці реклама й лаунчер; для молодших груп Play висуває додаткові вимоги до реклами (Families,
teen ads). «Appeals to children» — **No**.

---

## 4. Data safety

Основа — офіційні таблиці Google ✔: [Google Mobile Ads SDK](https://developers.google.com/admob/android/privacy/play-data-disclosure),
[Firebase (Cloud Messaging, Installations)](https://firebase.google.com/docs/android/play-data-disclosure); TikTok Business SDK — з
[документації TikTok](https://business-api.tiktok.com/portal/docs?id=1739585600931842) ◌; наш сервіс — з коду `businesModule/backend`
(`Backend.kt`, `Events.kt`): app instance ID (`iid`), install referrer, версія, події (app_open, screen_view, push_open…), токен пушів.

### Загальні питання

| Питання | Відповідь |
|---|---|
| Does your app collect or share any of the required user data types? | **Yes** |
| Is all of the user data collected by your app encrypted in transit? | **Yes** (усе — HTTPS) ✔ |
| Account creation | **My app does not allow users to create an account** |
| Do you provide a way for users to request that their data is deleted? | **Yes** ◌ — листом на контакт (так написано в політиці, розділ 10) |

### Типи даних

| Категорія → тип | Collected | Shared | Ephemeral | Required | Purposes |
|---|---|---|---|---|---|
| Location → **Approximate location** | ✔ | ✔ | No | Required | Advertising or marketing; Analytics; Fraud prevention, security and compliance |
| App activity → **App interactions** | ✔ | ✔ | No | Required | App functionality; Analytics; Advertising or marketing; Fraud prevention, security and compliance |
| App info and performance → **Diagnostics** | ✔ | ✔ | No | Required | Advertising or marketing; Analytics; Fraud prevention, security and compliance |
| Device or other IDs → **Device or other IDs** | ✔ | ✔ | No | Required | App functionality; Analytics; Advertising or marketing; Fraud prevention, security and compliance |

Звідки: AdMob і TikTok збирають IP (→ приблизне місце), взаємодію з рекламою / події апки, діагностику, рекламний ID
(Shared: Yes). Наш сервіс — app instance ID, події, токен пушів (App functionality, Analytics; іншим не передає).
Firebase — installation ID і токен FCM (App functionality).

**Не позначаємо** (обробляється лише на телефоні, нікуди не відправляється — так і в політиці, розділ 3):
відео й бібліотека, буфер обміну, список встановлених апок, налаштування. ✔ Перевірено в коді: `ClipWatcher`, `listApps`,
`DataStoreManager`, `StudioEngine` нічого не шлють у мережу. Лінк, який людина вставила, йде лише на сайт цього лінка
(саме завантаження), не до нас.

---

## 5. Release

| Поле | Значення |
|---|---|
| Countries | United States ◌ (маркет США; інші — за рішенням VELDAN; для ЄЕЗ/UK потрібне вікно згоди UMP) |
| Price | Free |
| Track | ◌ Новий **особистий** акаунт після 13.11.2023 — спершу Closed testing 12 тестувальників × 14 днів; **організація** (LTD) — можна одразу Production. Перевірити тип акаунта CAPSLOCK LTD у консолі |
| Release name | `1.0.0 (1)` |

Release notes (en-US) — 3 абстрактні пункти, як у парку:

```
• Turn your Home screen into a living video background
• Save videos from links and watch them offline
• Trim, loop and pull the sound in Studio
```

---

## 6. Перед кнопкою «Publish» — чекліст

- [ ] **Серверна команда: картка `framefall`** з `organic` → `na` або `admob` (зараз шаблон `custom_google` = чорний режим
      для всіх, і модератор теж потрапить у нього), `tiktok` app_id / secret, `deeplink_scheme = framefall`, свої тексти
      пушів без монет. Перевірити: `adb logcat -d -v raw -s FRAMEFALL:V | grep -A10 "^──── /appconfig"`.
- [x] Cloudflare `framefall-privacy` — опубліковано, `/` і `/privacy` віддають політику CAPSLOCK LTD.
- [x] Firebase-проєкт `framefall` → `Framefall/app/google-services.json` (13.10.2026), FCM-токен на девайсі є.
- [ ] Зібрати AAB (`sh release/build_aab.sh`) — уже з `google-services.json`.
- [ ] ◌ Свій AdMob App ID у `AndroidManifest.xml` (зараз тестовий Google): потрібен, лише якщо в картці буде `admob`.
- [ ] ◌ UMP-згода, якщо країни не лише США.
- [ ] Назва вільна (пошук у Play), App content заповнено, Data safety збігається з політикою.

## Тонкощі

- ◌ **Вкладка на «Додому» (чорний режим).** Механізм той самий, що в Redwave; Google Play прямо забороняє рекламу на
  кнопку Home (Disruptive Ads, дослідження `../research/`). Вмикати чорний режим лише на час після рев'ю й ховати його від
  модератора — обман модерації, ризик бану акаунта. Рішення про картку — за VELDAN і серверною командою.
- ◌ **Роль HOME обов'язкова в чорному режимі** (без «Maybe later») — рев'юер може оцінити це як примус змінити лаунчер.
  У білому режимі є «Maybe later», ризик нижчий.
- ✔ Реклама (`front` / `back` / `interstitial` / `app_open`) — лише всередині застосунку; на самому лаунчері й з віджета
  «Continue watching» плеєр відкривається без реклами.
- ✔ Discover — лише твори з дозволом на поширення (Blender CC BY 3.0, NASA, суспільне надбання), з авторами й ліцензією
  на кожному файлі — це і вимога CC BY, і аргумент для модерації.
- ✔ Заблоковані сервіси (YouTube, Instagram, TikTok…) перевіряються першими — навіть якщо в URL є `.mp4`.
