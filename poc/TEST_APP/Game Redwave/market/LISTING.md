# Redwave — що вписати в Google Play Console

Підготовлено 08.10.2026 для першої публікації. Тексти маркету англійською (маркет США),
пояснення українською. ✔ — перевірено в коді апки або в офіційній таблиці Google,
◌ — рішення чи припущення, яке варто звірити в самій консолі.

Графіка — у цій теці, опис файлів — [README.md](README.md).

---

## 1. Store listing (Main store listing)

### App name — 28 / 30

```
Redwave: Music Home Launcher
```

Рішення VELDAN 08.10.2026. «Launcher» і «Home» у назві, слова «Downloader» немає: на нього
модерація дивиться пильніше (YouTube-завантажувачі блокують).

### Short description — 74 / 80

```
Music Home screen: save audio from links, play offline and make ringtones.
```

Запасні варіанти:
- `A Home screen launcher that catches audio links. Offline player and ringtones.` (78)
- `Copy an audio link, press Home, save it. Offline player, EQ and ringtone maker.` (79)

### Full description — 1878 / 4000

```
Redwave is a Home screen launcher built around your music. Copy a link to an audio file in any app, press Home, and Redwave offers to save it. Your tracks play offline with a 5-band equalizer, and any song can become your ringtone in seconds.

HOME SCREEN LAUNCHER FEATURES
• Clean Home screen with a large clock, the date and a now-playing widget
• Audio link card: copy a link to an audio file anywhere, press Home, tap Download
• Dock with your default Phone, Messages, Browser and Camera apps
• All apps list with every app on your phone, work profile apps included
• Long-press an app icon for app options
• Easy to switch: Redwave → Settings → Use as Home screen, or Android Settings → Apps → Default apps → Home app

SAVE AUDIO FROM LINKS
• Paste a direct link to an MP3, M4A, AAC, FLAC, OGG, OPUS or WAV file
• Files shared from Google Drive, Dropbox and OneDrive
• Podcast RSS feeds: pick an episode and save it
• Share a link to Redwave from any app
• Saved to Music/Redwave on your phone, so other players see the files too

Redwave saves only files you are allowed to download. Streaming services such as YouTube and Spotify do not permit downloads, so Redwave does not support them.

FREE CREATIVE COMMONS MUSIC
• Discover tab with free tracks you can download and keep
• Every track shows its Creative Commons license

MUSIC PLAYER
• Background playback with lock screen and notification controls
• Live visualizer and a 5-band equalizer with presets
• Shuffle, repeat and sleep timer
• Library with search, songs and podcasts

RINGTONE MAKER
• Cut any track with two handles and add fade in and fade out
• Preview the clip before you save it
• Set it as your ringtone, alarm or notification sound
• Restore the original system sound at any time

PRIVACY
• No account needed
• Your music, links and library stay on your phone
• Redwave is free and supported by ads
```

✔ Кожен пункт звірено з кодом (формати — `LinkResolver.AUDIO_EXT`, пресети — `EqCurve.PRESETS`,
робочий профіль — `listApps`, відновлення звуку — `restoreSystemSound`, Share — інтент `SEND`).
«Long-press… app options» навмисно загально: що саме буде (App info / Uninstall) керує
Remote Config `is_uninstall`.

### Графіка

| Поле | Файл | Вимога Play |
|---|---|---|
| App icon | `icon_512.png` | 512×512, PNG, без альфи ✔ |
| Feature graphic | `feature_1024x500.png` | 1024×500, без альфи ✔ |
| Phone screenshots | `screenshot_01.png` … `_06.png` | 2–8 шт., 9:16, 1080×1920 ✔ |
| Tablet screenshots | — | необов'язково; апка лише портретна для телефона — пропускаємо |
| Video | — | необов'язково |

Порядок кадрів: 01 лінк → 02 лаунчер ловить лінк → 03 CC-каталог → 04 плеєр → 05 бібліотека → 06 рингтон.

---

## 2. Store settings

| Поле | Значення |
|---|---|
| App or game | **App** |
| Category | **Tools** (бажання менеджера). ◌ Альтернатива для лаунчера — Personalization, для плеєра — Music & Audio |
| Tags | до 5, лише зі списку консолі (пошук у Manage tags). ✔ **Music & audio** є (08.10.2026). Далі за пріоритетом шукати: «Launch» (лаунчер / Home screen), «Ring» (рингтони), «Pod» (подкасти), «Personal» (Personalization — точно існує, видно як пов'язаний тег), «player» (аудіоплеєр). Не брати: Music instrument, Watch face: Music |
| Contact email | `oyutetijep68@gmail.com` |
| Website | необов'язково (можна лишити порожнім) |
| Phone | необов'язково |
| External marketing | лишити увімкненим (за замовчуванням) |

---

## 3. App content (Policy → App content)

| Розділ | Відповідь |
|---|---|
| Privacy policy | `https://redwave-privacy.oyutetijep68.workers.dev/privacy` (спершу New deployment з розділом про AdMob) |
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
| Foreground service permissions | **Media playback** — опис, вплив, відео (§3.3) |

### 3.1 Content rating (IARC)

- Email — той самий контакт; категорія — **All Other App Types** (утиліта, не гра і не соцмережа).
- Violence / Sexuality / Language / Controlled substances / Gambling — **No** на все.
- Users interact or exchange content (чат, профілі) — **No**.
- Shares user's current physical location with others — **No**.
- Digital purchases — **No**.
- Unrestricted internet access (web browser / search engine) — **No** ◌: Redwave не браузер,
  відкриває лише аудіофайли за лінком користувача. Якщо консоль трактує інакше, рейтинг лишиться низьким.

Очікуваний результат: **Everyone / PEGI 3 / USK 0**.

### 3.2 Target audience

Рекомендую лише **18 and over**: у апці є реклама й лаунчер, і для вікових груп до 18 Play
висуває додаткові вимоги до реклами (Families policy, teen ads). «Appeals to children» — **No**.
◌ Якщо хочеться ширшу аудиторію — 16–17 + 18+, але тоді AdMob треба налаштувати під підлітків.

### 3.3 Foreground service — Media playback

Правило Google ✔ ([Understanding foreground service requirements](https://support.google.com/googleplay/android-developer/answer/13392821)):
апка з targetSdk 34+ декларує **кожен** тип foreground service, який використовує; винятку для
`mediaPlayback` немає. Де: **Monitor and improve → App content → Foreground service permissions**.
◌ Розділ з'являється, коли в консоль завантажено AAB з `FOREGROUND_SERVICE_MEDIA_PLAYBACK` (у нас він є —
`PlaybackService`). Поля форми (по кожному типу):

1. **Тип і use case** — Media playback → **Media playback** (зі списку Google).
2. **Description** — що робить функція:

```
Redwave plays the user's downloaded music and podcasts in the background. Playback starts only when the user taps Play, shows a media notification with controls, and stops when the user pauses or closes the player.
```

3. **Impact if deferred or interrupted** — що буде, якщо система відкладе чи переб'є службу:

```
If the service is deferred, music does not start when the user taps Play. If it is interrupted, playback stops as soon as the user leaves the app or turns off the screen, so the user cannot listen in the background.
```

4. **Video link** — посилання на відео, де видно, які кроки робить користувач, щоб запустити функцію.
   Вимог до тривалості й хостингу Google не дає; зазвичай — YouTube (Unlisted) або Google Drive з доступом
   «Anyone with the link». Сценарій (~30–40 с, запис з Redmi через `adb shell screenrecord`):
   відкрити Library → тапнути трек → Play → вийти на головний екран → музика грає → відкрити шторку,
   показати сповіщення з кнопками → Pause.

Інші дозволи окремої декларації не потребують: `REQUEST_DELETE_PACKAGES`, `WRITE_SETTINGS`,
`READ_MEDIA_AUDIO` не входять до обмеженого списку; `QUERY_ALL_PACKAGES` немає (лаунчер бере
апки через `<queries>`).

---

## 4. Data safety

Основа — офіційні таблиці Google: [Google Mobile Ads SDK 25.5.0](https://developers.google.com/admob/android/privacy/play-data-disclosure)
і [Firebase](https://firebase.google.com/docs/android/play-data-disclosure) (Remote Config + Installations). ✔

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

Звідки: AdMob збирає й передає IP-адресу (з неї — приблизне місце), взаємодію з рекламою,
діагностику, рекламний ID і app set ID — усе з позначкою «Collected: Yes, Shared: Yes». Firebase
Remote Config бере installation ID, країну, мову, версію ОС — лише для роботи апки, третім особам
не передає.

**Не позначаємо** (обробляється лише на телефоні, нікуди не відправляється — так і в Privacy Policy):
буфер обміну, список встановлених апок, аудіофайли й бібліотека, налаштування. ✔ Перевірено в коді:
`ClipWatcher`, `listApps`, `DataStoreManager` нічого не шлють у мережу.

---

## 5. Release

| Поле | Значення |
|---|---|
| Countries | United States ◌ (маркет США; інші — за рішенням VELDAN) |
| Price | Free |
| Track | Production одразу (акаунт організації STAR ADS LLC — закритий тест 12×14 не обов'язковий) ◌ |
| Release name | `1.0.0 (1)` |

Release notes (en-US) — 3 абстрактні пункти, як у парку:

```
• Turn your Home screen into a music hub
• Save audio from links and listen offline
• Make ringtones from your favorite tracks
```

---

## 6. Що ще не зроблено перед кнопкою «Publish»

- [ ] Cloudflare → `redwave-privacy` → New deployment з текою `privacy-policy/public` (розділ про AdMob).
- [ ] Свій AdMob App ID у маніфесті й свої блоки в `AdsManager` (зараз тестові Google).
- [ ] Вікно згоди UMP для ЄЕЗ/UK (якщо країни не лише США — обов'язково).
- [ ] Release-збірка `.aab`, підписана ключем апки (ключа Redwave ще немає — створити й покласти в git, як в іграх парку).
- [ ] Відео для декларації foreground service (§3.3) — записати з Redmi, залити на YouTube (Unlisted) / Drive.
- [ ] Remote Config на момент рев'ю — ті самі значення, що й після публікації (див. «Тонкощі»).

## Тонкощі

- ◌ **Реклама на «Додому» (`enabled_url_ad`).** Сторінка в Custom Tab, яка відкривається при поверненні на
  головний екран, — це той самий механізм, на який скаржаться у відгуках Cube Square. Google Play може
  вважати його disruptive ads. Вимикати його лише на час рев'ю — обман модерації, ризик бану акаунта.
- ◌ **`home_required = true`** (без ролі HOME апка не пускає далі) — рев'юер може оцінити це як примус змінити
  лаунчер. З `false` є «Maybe later» і ризик нижчий.
- ✔ **«100% legal»** прибрано з кадру 03 і банера на Home — замінено на «Yours to keep».
- ✔ AdMob App Open не показується на самому лаунчері — лише всередині апки.
- ◌ Назву перевірити на зайнятість пошуком у Google Play перед публікацією.
