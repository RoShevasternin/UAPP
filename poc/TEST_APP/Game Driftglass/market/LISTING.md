# Driftglass — що вписати в Google Play Console

Підготовлено 12.10.2026 для першої публікації, за зразком Redwave. Тексти маркету англійською (маркет США),
пояснення українською. ✔ — перевірено в коді апки або в офіційній таблиці Google,
◌ — рішення чи припущення, яке варто звірити в самій консолі.

Графіка — у цій теці, опис файлів — [README.md](README.md).

**Акаунт Google Play (VELDAN, 12.10.2026): Strategic Access Ventures** — Organization account, Account ID `5142898478012444545`,
контакт `0927920793ja@gmail.com`. Нові апки викладаємо на нові акаунти; Redwave — на STAR ADS LLC.

---

## 1. Store listing (Main store listing)

### App name — 28 / 30

```
Driftglass: Live Home Screen
```

«Home Screen» у назві навмисно: людина з першого рядка знає, що апка пропонує стати головним екраном
(чесний варіант з аналізу конкурентів, пам'ять `wallpaper-home-analysis`).

### Short description — 76 / 80

```
Live wallpapers drawn in real time and a Home screen with smart app folders.
```

Запасні варіанти:
- `Living wallpapers that never repeat, a wallpaper studio and a clean Home screen.` (80)
- `Animated wallpapers, made on your phone. Create your own and set your Home screen.` (80)

### Full description

```
Driftglass turns your Home screen into living glass. Every wallpaper is drawn in real time on your phone, so it moves softly behind your apps and never looks the same twice.

LIVING WALLPAPERS
• 24 original wallpapers in five styles: aurora, liquid, waves, glass and gradient
• Eight hand-picked palettes, from sea glass and ember to orchid and night
• Gentle parallax: the picture follows the tilt of your phone
• Battery saver slows the animation down when the battery is low

STUDIO: MAKE ONE THAT'S YOURS
• Pick a style and a palette, then tune motion, scale and grain
• Tap the dice for a new variation
• Save your creations to My Glass and apply them in one tap

SHIFTS WITH YOUR DAY
• Day cycle: a different wallpaper for dawn, day, dusk and night
• Auto-shuffle on every unlock, every hour or every day
• Double-tap the Home screen for a new wallpaper

A CLEAN HOME SCREEN
• Large clock and date over your live wallpaper
• Apps sorted into folders on their own: Google, Tools, Games, Social and Media
• Swipe up for all your apps, with search
• Dock with your default Phone, Messages, Browser and Camera apps
• Long-press an app icon for app options
• Easy to switch back: Driftglass → Settings → Use as Home screen, or Android Settings → Apps → Default apps → Home app

NOT READY TO SWITCH?
• Set a still frame of any wallpaper on your current Home screen and lock screen
• Studio, favorites and the gallery work without changing your launcher

PRIVACY
• No account needed
• Wallpapers are generated on your phone
• Your settings and creations stay on your device
• Driftglass is free and supported by ads
```

✔ Звірено з кодом: 24 шпалери й 5 стилів — `Catalog`, `Style`; 8 палітр — `Palettes`; паралакс — `GDXGame.updateParallax`;
економія — `PowerPolicy` (авто нижче 20 %); Studio (стиль, палітра, Motion / Scale / Grain, кубик, Save) — `StudioScreen`;
цикл дня 4 слоти й Auto-shuffle unlock / hourly / daily — `MyGlassTab`, `ShufflePolicy`; подвійний тап — `LauncherScreen`;
папки — `HomeLayout`; док — `dockApps`; кадр на системні шпалери й екран блокування — `setStillWallpaper`.
«Long-press… app options» навмисно загально: що саме буде (App info / Uninstall), керує Remote Config `is_uninstall`.
«Google» у списку папок — назва папки, як у самого Android; логотипів Google ні в кадрах, ні в описі немає.

### Графіка

| Поле | Файл | Вимога Play |
|---|---|---|
| App icon | `icon_512.png` | 512×512, PNG, без альфи ✔ |
| Feature graphic | `feature_1024x500.png` | 1024×500, без альфи ✔ |
| Phone screenshots | `screenshot_01.png` … `_06.png` | 2–8 шт., 9:16, 1080×1920, без альфи ✔ |
| Tablet screenshots | — | необов'язково; апка лише портретна для телефона — пропускаємо |
| Video | — | необов'язково |

Порядок кадрів: 01 головний екран → 02 галерея → 03 перегляд → 04 Studio → 05 цикл дня → 06 папка.

---

## 2. Store settings

| Поле | Значення |
|---|---|
| App or game | **App** |
| Category | **Personalization** ◌ (шпалери + лаунчер; так у маніфесті `appCategory="image"` не заважає) |
| Tags | ◌ обрати в консолі: **Launcher, Wallpapers, Live wallpapers, Personalization** (що з цього є в списку тегів Google — звірити) |
| Contact email | `0927920793ja@gmail.com` |
| Website | необов'язково |
| Phone | необов'язково |
| External marketing | лишити увімкненим (за замовчуванням) |

---

## 3. App content (Policy → App content)

| Розділ | Відповідь |
|---|---|
| Privacy policy | `https://driftglass-privacy.0927920793ja.workers.dev/privacy` ◌ (спершу викласти — `../privacy-policy/README.md`; адресу звірити після публікації) |
| Ads | **Yes, my app contains ads** |
| App access | **All functionality is available without special access** — логіна немає |
| Content rating | анкета IARC, див. §3.1 |
| Target audience | **18 and over** ◌, див. §3.2 |
| News apps | No |
| Health apps | My app does not have any health features |
| Financial features | My app doesn't provide any financial features |
| Government apps | No |
| Data safety | див. §4 |
| Advertising ID | **Yes** — Advertising or marketing, Analytics, Fraud prevention, security and compliance ✔ (`AD_ID` у маніфесті) |
| Foreground service permissions | **Не потрібно** ✔ — foreground service у Driftglass немає |

### 3.1 Content rating (IARC)

- Email — той самий контакт; категорія — **All Other App Types**.
- Violence / Sexuality / Language / Controlled substances / Gambling — **No** на все.
- Users interact or exchange content — **No**.
- Shares user's current physical location with others — **No**.
- Digital purchases — **No**.
- Unrestricted internet access (web browser / search engine) — **No** ◌ (Custom Tab з Remote Config — не браузер у самій апці).

Очікуваний результат: **Everyone / PEGI 3 / USK 0**.

### 3.2 Target audience

Як у Redwave: лише **18 and over** — у апці є реклама й лаунчер, для вікових груп до 18 Play висуває додаткові
вимоги до реклами (Families policy). «Appeals to children» — **No**.

---

## 4. Data safety

Основа — офіційні таблиці Google: [Google Mobile Ads SDK](https://developers.google.com/admob/android/privacy/play-data-disclosure)
і [Firebase](https://firebase.google.com/docs/android/play-data-disclosure) (Remote Config + Installations). Ті самі SDK і версії, що в Redwave ✔.

### Загальні питання

| Питання | Відповідь |
|---|---|
| Does your app collect or share any of the required user data types? | **Yes** |
| Is all of the user data collected by your app encrypted in transit? | **Yes** (AdMob — TLS, Firebase — HTTPS) ✔ |
| Account creation | **My app does not allow users to create an account** |
| Do you provide a way for users to request that their data is deleted? | **No** ◌ — даних у нас немає; рекламний ID людина скидає в налаштуваннях Android |

### Типи даних

| Категорія → тип | Collected | Shared | Ephemeral | Required | Purposes |
|---|---|---|---|---|---|
| Location → **Approximate location** | ✔ | ✔ | No | Required | Advertising or marketing; Analytics; Fraud prevention, security and compliance |
| App activity → **App interactions** | ✔ | ✔ | No | Required | Advertising or marketing; Analytics; Fraud prevention, security and compliance |
| App info and performance → **Diagnostics** | ✔ | ✔ | No | Required | Advertising or marketing; Analytics; Fraud prevention, security and compliance |
| Device or other IDs → **Device or other IDs** | ✔ | ✔ | No | Required | Advertising or marketing; Analytics; Fraud prevention, security and compliance; **App functionality** (Firebase installation ID для Remote Config) |

**Не позначаємо** (обробляється лише на телефоні, нікуди не відправляється — так і в Privacy Policy):
список встановлених апок і їхні категорії, акселерометр (паралакс), рівень батареї, створені шпалери й налаштування.
✔ Перевірено в коді: `listApps`, `updateParallax`, `battery()`, `DataStoreManager` нічого не шлють у мережу.

---

## 5. Release

| Поле | Значення |
|---|---|
| Countries | United States ◌ (маркет США; інші — за рішенням VELDAN) |
| Price | Free |
| Track | Production одразу (акаунт організації Strategic Access Ventures) ◌ |
| Release name | `1.0.0 (1)` |

Release notes (en-US) — 3 абстрактні пункти, як у парку:

```
• Living wallpapers drawn in real time
• Create your own wallpapers in Studio
• A clean Home screen with smart folders
```

---

## 6. Що ще не зроблено перед кнопкою «Publish»

- [x] Firebase: проєкт `driftglass-original`, `google-services.json`, параметр `driftglass_config` (VELDAN, 12.10.2026; апка отримує `[remote]`).
- [ ] Cloudflare → `driftglass-privacy` → викласти `privacy-policy/public` — покроково в `../privacy-policy/README.md`.
- [ ] Свій AdMob App ID у маніфесті й свої блоки в `AdsManager` (зараз тестові Google).
- [ ] Вікно згоди UMP для ЄЕЗ/UK (якщо країни не лише США — обов'язково).
- [ ] Release-збірка `.aab`, підписана ключем апки (ключа Driftglass ще немає — створити й покласти в git, як в іграх парку).
- [ ] Remote Config на момент рев'ю — ті самі значення, що й після публікації (див. «Тонкощі»).

## Тонкощі

- ✔ **Скріни чисті.** Знято на Redmi в debug-режимі `StoreDemo`: замість апок телефона — вигадані (Calculator, Music,
  Shop…) з нашими іконками: гліфи Material Symbols (Google, Apache 2.0) на нашому градієнті. Чужих логотипів і назв
  брендів у кадрах немає. Статус-бар телефона (сповіщення) зрізано й замінено чистим «9:41».
- ◌ **Демо-апки замість справжніх.** На справжньому телефоні в сітці будуть апки людини. Кадри показують ту саму
  розкладку (папки, док, свайп угору) — це нормальна практика для лаунчерів, але без обіцянки конкретних апок.
- ◌ **Реклама на «Додому» (`enabled_url_ad`).** Сторінка в Custom Tab при поверненні на головний екран — той самий
  механізм, що й у Redwave; Google Play може вважати його disruptive ads. Вимикати лише на час рев'ю — обман
  модерації, ризик бану акаунта. У конфігу VELDAN 12.10.2026 `enabled_url_ad` = **false**.
- ◌ **`home_required = true`** — рев'юер може оцінити як примус змінити лаунчер. У конфігу 12.10.2026 — **false**.
- ✔ AdMob не показується на самому лаунчері, онбордингу й екрані-вимозі — лише у вкладках, Preview і Studio.
- ◌ Назву перевірити на зайнятість пошуком у Google Play перед публікацією.
