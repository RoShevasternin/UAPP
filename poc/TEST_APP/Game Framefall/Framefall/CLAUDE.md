# Framefall: Video Downloader

Третя апка `poc/TEST_APP` (категорія **Tools**). Зберігає відео з **легальних** посилань (прямі файли,
Google Drive, Dropbox, OneDrive, сторінки Internet Archive / NASA / Wikimedia Commons) і з вбудованого
каталогу відкритих фільмів; грає їх у власному плеєрі; Studio ріже, витягує звук, робить GIF, частини для
Status, кадр, стиснення і **живий відеофон** для власного головного екрана (роль HOME).

Зроблена 13.10.2026 (рішення VELDAN) **з каркаса Redwave** (`../../Game Redwave/Redwave`): та сама бізнес-логіка
UAPP, реклама, білий / чорний режим, лаунчер, онбординг, буфер. Музичне (рингтони, EQ, подкасти, MediaSession)
прибрано, замість нього — відео.

Стек як в іграх UAPP: **Kotlin + LibGDX 1.14.2 + scene2d**, UI малюється в GL, Android-бік — тонкий шар.
Прототип: https://claude.ai/artifact/5L7TpzbpbrTptLSVq66UJw (копія `../prototype/`). Ассети й скрипти до них —
`../assets/` (README там).

Спілкування й коментарі в коді — **українською**. UI апки — **англійською** (маркет США).

## Пакет і сервер

Пакет — **`com.vidtools.framefall`**. Сервер (`/appconfig`) ключує картку апки за **останнім сегментом пакета**
(`e` в `atk`): у Framefall це `framefall`. Заміряно 13.10.2026: `com.framefall.downloader` отримав би картку Redwave
(`downloader`), а `video` / `app` — надто загальні. **Пакет після публікації не змінити.**

Поки картки `framefall` на сервері немає, приходить шаблон за замовчуванням: `custom_google` скрізь, без `tiktok`,
кампанії пушів чужі → **чорний режим для всіх**. Що віддати серверній команді — розділ «Серверній команді» нижче.

## Бізнес-логіка UAPP (як у Redwave)

`businesModule/` — копія еталона `june/Game T1-1/T1-integrated` **без змін** (лише префікс пакета): конфіг
(`/appconfig`, `atk`, гейтвей), події, `Wallet`/`Econ` (монет у UI немає), опт-ін, локальні пуші (WorkManager),
FCM-токен, повернення з веба. **Правки серверної команди (`DEV_TASKS`) бекпортувати і сюди.**

`adsmodule/` — як у Redwave: лише інтерстішал (front / back / interstitial) і App Open з поверненням, яке повідомляє
апка (`AppOpenManager.onReturnToApp`). Банера й нативки немає. TikTok — `services/tiktok` (ключі з картки).

Конфіг — `android/ServerConfig.kt`; режим — `core/config/AppFlags.kt` (+ `AppFlagsTest`):

| Провайдер `app_open` для типу юзера | Вкладка на «Додому» / «Недавні» | Роль HOME | Uninstall з лаунчера |
|---|---|---|---|
| `custom*` | так (з роллю й онлайн) | обов'язкова, без «Maybe later» | ні |
| `na` / `admob` / конфігу немає / офлайн | ні | «Maybe later» є | так |

### Плейсменти (ключ доходу `framefall-<pl>`, після релізу НЕ перейменовувати)

| pl | Коли | Де |
|---|---|---|
| `app_open` | «Додому» з іншої апки й очищення «Недавніх» (чорний режим, роль, онлайн) | `MainActivity.onResume`, `AppFlags.PL_HOME_TAB` |
| `app_open` | черга старту (опт-ін → app_open) і повернення в застосунок | `AppOpenManager` |
| `push` | тап по пушу | `LocalPush.handleOpen` |
| `front` | вкладка, відкриття плеєра / Studio із застосунку | `GDXGame.adFront`, `openVideo`, `openStudio` |
| `back` | «Назад» з плеєра / Studio в застосунок | `PlayerScreen.closePlayer`, `StudioScreen.close` |
| `interstitial` | кожні 2 збережені відео | `GDXGame.maybeInterstitial` |
| `optin_return` | повернення у вкладку після дозволу з лендінга | `MainActivity` |

З лаунчера (Continue watching на Home) плеєр відкривається **без** реклами — лише в застосунку.
Відео в плеєрі й прев'ю Studio **продовжує саме** після реклами поверх (GL-пауза зупиняє його) — якщо паузу не
ставила людина (`userPaused`).

### Скріни й тексти

Screen_view (`Bt` + block): `save_tab`, `discover_tab`, `library_tab`, `studio_tab` (вкладки), `player_screen`,
`studio_<tool>` (`studio_trim`, `studio_loop`…), `launcher_screen`. Опт-ін без монет — тексти в `App.kt`.
Диплінк: схема **`framefall`** (`framefall://reward`, `framefall://optin`) через `LauncherTrampoline`.

## Відео: як влаштовано

- **ExoPlayer → OES-текстура в GDX** (`android/video/ExoVideoPlayer.kt`): ExoPlayer на main декодує в
  `Surface(SurfaceTexture(texId))`, GL-потік робить `updateTexImage()`, актор `AVideo` малює кадр шейдером
  `assets/shader/video/oes*.glsl` (`samplerExternalOES`, матриця SurfaceTexture, заокруглення, cover/contain,
  `focusY`). Тож плеєр, прев'ю Studio і живий фон Home — частина сцени, поверх лягають наші градієнти й кнопки.
- Кожен екран тримає **свій** `VideoPlayer` (`bridge.createVideoPlayer()`) і звільняє в `hide()`/`dispose()`.
- **GL-пауза** (Activity у фоні, реклама поверх): `GDXGame.pause()` → `AndroidBridge.onGlPause()` — плеєри
  звільняють текстуру/Surface і стають на паузу; на наступному кадрі все створюється наново (контекст міг змінитись).
- Повний екран у плеєрі — той самий кадр, повернутий на 90° (Activity портретна: лаунчер не повертаємо).
- Фон Home: вбудовані петлі `assets/video/bg_orion.mp4`, `bg_sintel.mp4` (540×1170, без звуку, бумеранг) або своя
  петля зі Studio (`filesDir/homebg/*.mp4`, кроп 9:19.5, ≤ 10 с, стара видаляється). Постери — `textures/launcher/`.

## Збереження й Studio

- `DownloadController`: `LinkResolver` → прямий файл / хмара одразу в DownloadManager; сторінка Archive / NASA →
  API (`core/link/OpenArchives.kt`) → шторка якості (`QualitySheet`); невідоме → `probe` (Range 0-63) →
  `VideoSniffer` (Content-Type + магія: mp4/mov/webm/mkv/3gp; Theora `.ogv`, AVI, HLS — чесна відмова).
- Заблоковані: YouTube, Instagram, Facebook, TikTok, X, Vimeo, Twitch, Dailymotion, стрімінги, Reddit, VK… —
  **перевірка першою**, навіть якщо в URL є `.mp4`. ⚠️ `x.com` — це X: не брати його тестовим доменом.
- Файли: `Movies/Framefall` (відео), `Music/Framefall` (звук), `Pictures/Framefall` (кадр, GIF). Мініатюри —
  `filesDir/thumbs`. Бібліотека — наш індекс у DataStore (`AppState.library`), не MediaStore.
- Discover — `assets/catalog.json` (9 фільмів: Blender CC BY 3.0, NASA і класика в суспільному надбанні; прямі лінки
  archive.org / images-assets.nasa.gov; розміри з їхніх API 13.10.2026). Мініатюри — `textures/thumbs/`.
- Studio (`android/studio/StudioEngine.kt`): trim / status / compress / loop — Media3 Transformer (H.264, бітрейт як у
  джерела або під висоту кадру); audio — без відеодоріжки → AAC `.m4a` (немає звуку → тост, не помилка); frame — MMR;
  GIF — свій `GifEncoder` (палітра 6×7×6 + Байєр, LZW; Android енкодера GIF не має). Кадр / GIF відкриваються в Галереї.

## Збірка і запуск

```bash
export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"
cd "/Users/admin/Apps/UAPP/poc/TEST_APP/Game Framefall/Framefall"
sh ./gradlew assembleDebug
adb install -r -t app/build/outputs/apk/debug/app-debug.apk      # MIUI: INSTALL_FAILED_USER_RESTRICTED → повторити
adb shell monkey -p com.vidtools.framefall -c android.intent.category.LAUNCHER 1
adb logcat -s FRAMEFALL:V AndroidRuntime:E
sh ./gradlew :app:testDebugUnitTest                              # 28 тестів чистої логіки
sh ./gradlew :app:packAtlas                                      # атлас іконок з ../assets/all/icons
```

Debug-хуки (лише debug-збірка):
```bash
# картка буфера на Home без буфера (adb на Android 13 не пише в буфер)
adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME -n com.vidtools.framefall/.MainActivity --es framefall.debug_clip "https://archive.org/details/CaminandesLlamigos"
# режим як при іншому провайдері app_open (na / admob / custom_google); прибрати — порожній рядок У ЛАПКАХ всередині shell
adb shell am start -n com.vidtools.framefall/.MainActivity --es framefall.debug_app_open na
adb shell "am start -n com.vidtools.framefall/.MainActivity --es framefall.debug_app_open ''"
# відповідь /appconfig коротко
adb logcat -d -v raw -s FRAMEFALL:V | grep -A10 "^──── /appconfig"
# маленький тестовий MP4 (10 с, 0.9 МБ) для поля Save
https://archive.org/download/night_of_the_living_dead_dvd/VIDEO_TS.mp4
```
Settings → Debug: AD_MODE, скинути онбординг, джерело конфігу, atk, лічильники реклами.

**Release (R8) перевіряти на девайсі:** `assembleRelease` → `zipalign` + `apksigner` debug-ключем → `adb install -r`.
Перевірено 13.10.2026: старт як HOME → `app_open`, локальні пуші (WorkManager), відеофон, плеєр, Studio (Transformer).

**Одна MainActivity на процес** — іконка йде через `LauncherTrampoline` (як у Redwave). Не повертати LAUNCHER на MainActivity.

## Firebase / FCM

`app/google-services.json` **немає**: Firebase-проєкт апки має створити VELDAN в акаунті Framefall (gcloud на цьому Маці —
інший акаунт). Плагін `google-services` підключається, лише коли файл є (`app/build.gradle.kts`); без нього апка
працює повністю, крім серверних пушів (`FCM token sync failed` у лозі — очікувано). Поклали файл — пересібрати.

AdMob App ID у маніфесті — **тестовий** Google; перед релізом — свій.

## Публікація

Акаунт Play — **CAPSLOCK LTD** (`caps89temitop89@gmail.com`, VELDAN 13.10.2026). Усе для консолі — `../market/LISTING.md`,
графіка — `../market/` (`render.sh`), AAB — `sh ../release/build_aab.sh` (ключ `../release/keyFramefall.jks`, пароль у
`meta-data.txt`), Privacy Policy — `../privacy-policy/` (Cloudflare `framefall-privacy`, адреса вшита в `AppFlags.DEFAULT_PRIVACY_URL`).

## Серверній команді (картка `framefall`)

- `organic` → `na` або `admob` (модератор Google не має потрапити в чорний режим), рекламні типи (`ttclid`…) → `custom*`;
- блок `tiktok` (app_id / secret TikTok-застосунку Framefall);
- `deeplink_scheme = framefall`;
- свої тексти пушів (без монет, колеса, квізів);
- домен лендінга під відео-тематику в гейтвеї;
- доступ до FCM-проєкту Framefall, коли його створять.

## Конвенції (як у Redwave / T35)

- Навігація **тільки** через `NavigationManager`; що відкрити в плеєрі / Studio — `gdxGame.playerVideoId` / `studioTool`
  (ключ навігації — лише Int). Відкривати — `gdxGame.openVideo(v)` / `openStudio(tool, v)` (там реклама й перевірки).
- Кольори — лише `GameColor` (палітра бурштин #FFC94D → помаранч #FF8A2A → полум'я #F2511B на #0A0705), тексти — лише `Copy`.
- Текст — MSDF: Archivo Expanded (заголовки, цифри), Onest (усе від користувача), JetBrains Mono (таймкоди, розміри).
  ⚠️ В Archivo немає «≈» — тому «~».
- Розміри — десяткові (61.9 MB = 61 900 000 байт), як у Файлах Android і на archive.org (`Fmt.size`).
- Android ↔ GDX — лише `PlatformBridge`; колбеки назад — `runGDX { }`.

## Git

Локально Claude **комітить, але не пушить** — пушить VELDAN (GitHub Desktop).
