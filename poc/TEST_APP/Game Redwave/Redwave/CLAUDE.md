# Redwave: Music Downloader

Нова апка категорії **Tools** (не гра парку RBX). Завантажувач музики з **легальних**
посилань + безкоштовний CC-каталог, плеєр з еквалайзером, різак рингтонів і **власний
головний екран** (роль HOME), який ловить скопійовані аудіолінки.

Стек як в іграх UAPP: **Kotlin + LibGDX 1.14.2 + scene2d**, UI малюється в GL, Android-бік —
тонкий шар сервісів. **Еталона немає** (правила теки — `poc/TEST_APP/CLAUDE.md`): ні `businesModule`,
ні `adsmodule`, ні Firebase/TikTok/білінгу, доки VELDAN окремо не скаже.

Спілкування й коментарі в коді — **українською**. UI апки — **англійською** (маркет США).

## Перед роботою — прочитати

Ця тека (`Game Redwave/Redwave/`) — **сама апка** (Android-проєкт). Документи й матеріали до
неї лежать рівнем вище, у `Game Redwave/` (як `Game T35/` → `Mindora Self Test/` + `market/`):

1. `../PORTING.md` — архітектура, Android-бік, маніфест, **фази з чекпоінтами на девайсі**, пастки.
2. `../SCREENS.md` — кожен екран і компонент: розкладка, актори, стани, анімації.
3. Прототип — еталон вигляду й поведінки: https://claude.ai/artifact/1WQgAQ5KYh7M3EHovXgLMC
   (копія `../prototype/index.html`, відкривається в браузері). Розбіжність прототип ↔ апка —
   баг апки, якщо в `PORTING.md` не записано інше.
4. На старті першої сесії поставити VELDAN питання з розділу **«❓ Відкриті рішення»**
   у `../PORTING.md` (пакет, джерело каталогу тощо). Пакет після публікації не змінити.

## Що вже готово поруч (`Game Redwave/`)

| Тека | Що | Куди в апці |
|---|---|---|
| `../prototype/` | HTML-прототип v2 + фото CC0 (`img/SOURCES.md`) | — (довідка) |
| `../port-kit/` | Чиста Kotlin-логіка + 31 JVM-тест: розбір лінків, перевірка «чи це аудіо», буфер, RSS, каталог, стан, рингтон, EQ, спектр, тексти UI, кольори, розміри | `src/main/kotlin/**` → `app/src/main/java/<пакет>/…` (поміняти `com.redwave.downloader`, якщо пакет інший) |
| `../assets/fonts/` | MSDF-шрифти у форматі T35 + TTF + скрипти | `msdf/*` → `app/src/main/assets/font/msdf/` |
| `../assets/all/icons/` | Іконки 96 px, білі (тонуються кольором актора) | джерело атласу `all` |
| `../assets/brand/` | Лого, адаптивна іконка по mipmap-папках, SVG | `android-res/*` → `app/src/main/res/` |
| `../assets/textures/` | Великі текстури: фото онбордингу, банери Discover, фон лаунчера, світіння | `app/src/main/assets/textures/` |
| `../market/` | Google Play: 6 скрінів 1080×1920, іконка 512, feature 1024×500 | Play Console |
| `../test-media/` | Наші згенеровані треки (CC0), RSS, `catalog.json`, `links.md` — **прямі лінки для тестів на девайсі** | хоститься з GitHub raw |

Android-проєкт створюється **тут, у `Game Redwave/Redwave/`** (поруч із цим `CLAUDE.md`), як
`Game T35/Mindora Self Test/`. У `Game Redwave/` — лише матеріали до апки (маркет, ассети,
прототип, port-kit, тест-треки, документи), коду апки там немає.

## Збірка і запуск (коли проєкт буде)

```bash
export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"   # adb не в PATH
cd "/Users/admin/Apps/UAPP/poc/TEST_APP/Game Redwave/Redwave"
sh ./gradlew assembleDebug                                       # gradlew часто без біта виконання
adb install -r -t app/build/outputs/apk/debug/app-debug.apk      # MIUI: INSTALL_FAILED_USER_RESTRICTED → просто повторити
adb shell monkey -p <applicationId> -c android.intent.category.LAUNCHER 1
adb logcat -s redwave:V AndroidRuntime:E
adb exec-out screencap -p > /tmp/shot.png                        # скріншот — відкрити й подивитись
```

Тести чистої логіки: `cd ../port-kit && gradle test` (або в апці — `testDebugUnitTest`, якщо
перенести тести в `app/src/test`).

**Змінив UI, анімацію чи шейдер — зроби скріншот з девайса і подивись на нього.** Порівняй
з тим самим екраном прототипу. Не описувати зміну словами, не здогадуватись.

## Конвенції (як у T35)

- Навігація **тільки** через `NavigationManager` (`navigate()` / `back()`), прямий `setScreen()` заборонено.
- Актори з префіксом `A`, файл = клас. Базові класи — `utils/advanced/` (`Advanced*`).
- Координати — **world-юніти**, база 376 × 815. Розміри з прототипу: `px(16f)` (1 px = 1.074 wu),
  готові — в `Dimens` (`../port-kit/.../game/utils/Constants.kt`).
- Кольори — лише `GameColor`, тексти — лише `Copy`.
- Текст — MSDF (`AMsdfLabel`). **Archivo — тільки фіксовані англійські заголовки й цифри**
  (кирилиці немає). Усе, що прийшло від користувача (ID3-теги, імена файлів, RSS) — **Onest**.
- Усе `Disposable` — у `disposableSet` екрана. Текстури обкладинок (із файлів) — теж.
- Шейдери GLSL ES 2.0: `varying`/`attribute`, без `#version`, `precision mediump float`.
- Android ↔ GDX: виклики з GL-потоку в Android — через `PlatformBridge`; колбеки назад — `runGDX { }`.
- Секції класу — сепаратор `// ----…`, підсекції `// ── Назва ───`, «чому так» — KDoc.

## Git

Локально Claude **комітить, але не пушить**: у локальному git немає логіна до GitHub.
Пушить VELDAN через GitHub Desktop; після коміту — нагадати йому. Хмарна сесія (claude.ai/code)
може сама відкрити PR і змержити в `main` (пам'ять `uapp-cloud-merge-permission`).
Перед роботою — `git pull`, щоб мати свіжі `../test-media/`, `../assets/`, документи.

## Не чіпати руками

- `../assets/fonts/msdf/*` — генерується `../assets/fonts/tools/build_all.sh`.
- Атласи (`app/src/main/assets/atlas/*`) — пакує VELDAN у TexturePacker GUI. CLI TexturePacker
  у компанії без ліцензії й ставить водяні знаки (пам'ять `mindora-texturepacker-cli`).
  Альтернатива без ліцензії — `gdx-tools` TexturePacker (див. `../PORTING.md`).
- `app/libs/**/*.so` — розпаковує таск `copyAndroidNatives`.
