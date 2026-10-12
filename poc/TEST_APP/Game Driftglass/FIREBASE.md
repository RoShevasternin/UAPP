# Driftglass — Firebase Remote Config (як у Redwave)

✔ **Зроблено 12.10.2026 (VELDAN):** проєкт `driftglass-original`, `Driftglass/app/google-services.json`, параметр `driftglass_config`.
Перевірено на Redmi: `remote flags (fetch) [remote]: … isEnableAdmob=true` → AdMob стартує, App Open показується.

Рішення VELDAN 12.10.2026: конфіг на Firebase, щоб працювало так само, як у Redwave. Код уже готовий
(`Driftglass/app/.../android/RemoteFlagsSource.kt`, `core/config/AppFlags.kt`, `android/AdsManager.kt`) — бракує лише
проєкту у Firebase і файлу `google-services.json`. Без файлу апка збирається й працює з `AppFlags.DEFAULT`
(реклами немає, «Maybe later» є, Uninstall є).

## Значення (VELDAN, 12.10.2026)

Параметр **`driftglass_config`**, тип **JSON** (файл — `firebase/driftglass_config.json`):

```json
{
  "enabled_url_ad": false,
  "url": "https://google.com",
  "home_required": false,
  "is_uninstall": true,
  "is_enable_admob": true
}
```

| Поле | Що робить у Driftglass |
|---|---|
| `enabled_url_ad` + `url` | Custom Tab з `url` на «Додому» з іншої апки, після «Недавніх» / «Очистити все», після вимкненого екрана (лише з роллю HOME і онлайн) |
| `home_required` | `true` — без «Maybe later»; без ролі — екран-вимога на кожному вході |
| `is_uninstall` | `true` — довге натискання на іконку в лаунчері → App info / Uninstall; `false` — нічого |
| `is_enable_admob` | `true` — AdMob App Open + Interstitial у вкладках, Preview і Studio (зараз тестові блоки Google) |
| `privacy_url` (необов.) | інша адреса Privacy Policy |

Офлайн або без відповіді Firebase — усе як `false` / `is_uninstall: true` (рішення VELDAN 07.10.2026 для Redwave, діє й тут).

## Як створити (Firebase Console, акаунт, де живе `redwave-original`)

1. https://console.firebase.google.com → **Add project** → назва `driftglass-original` (як `redwave-original`) →
   Google Analytics **вимкнути** (Analytics у апці немає) → Create.
2. У проєкті: **Add app → Android** → package name **`com.driftglass.home`**, nickname `Driftglass` →
   SHA-1 не потрібен (Remote Config без нього працює) → **Register app**.
3. **Download google-services.json** → покласти в `Game Driftglass/Driftglass/app/google-services.json`.
   Решту кроків майстра (gradle) пропустити — у проєкті вже є.
4. **Run → Remote Config → Create configuration** → параметр:
   - Parameter name: `driftglass_config`
   - Data type: **JSON**
   - Default value: вміст `firebase/driftglass_config.json`
   - **Save** → **Publish changes**.

   Або одним кроком: меню ⋮ → **Publish from a file** → `firebase/remote_config_template.json` ◌ (формат шаблону Remote Config;
   якщо консоль не прийме — створити вручну, як вище).
5. Зібрати й перевірити: `sh ./gradlew assembleDebug` → встановити → Settings → Debug: рядок має бути
   `[remote] driftglass_config = enabled_url_ad=false home_required=false is_uninstall=true is_enable_admob=true url=https://google.com`
   і `ads(test) · act 0/2 · …` (AdMob увімкнено).

`google-services.json` комітимо в git, як у Redwave (репо — повний бекап, пам'ять `uapp-git-backup-public-repo`).

## Як працює (1:1 з Redwave)

- Перший запуск онлайн: Splash чекає Firebase до 3 с; далі — кеш одразу, `fetchAndActivate` на кожному `onResume`
  (release — не частіше 5 хв) + real-time listener.
- Послаблення прапорців діє одразу, посилення (`home_required` → true) — на наступному вході.
- Debug: Settings → Debug → AD_MODE — підміна поверх Remote Config (вкладка + AdMob + роль обов'язкова + без Uninstall),
  або JSON з adb: `--es driftglass.debug_flags '{…}'` (`""` — прибрати).
