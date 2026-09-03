# Mindora — Self Test

Комерційна Android-апка (не пет-проєкт): набір психологічних self-тестів.
Весь UI намальований у **LibGDX** — нативних View майже нема, лише контейнери
під рекламу в `activity_main.xml`.

Стек: Kotlin, LibGDX 1.14.2, scene2d, OpenGL ES 2.0, minSdk 24, JVM 11.
Хост: Mac M1 Pro, тест — на реальному девайсі через USB.
Спілкування і коментарі в коді — **українською**.

## Збірка і запуск

`adb` не в PATH, повний шлях:
```bash
ADB=~/Library/Android/sdk/platform-tools/adb

./gradlew installDebug                       # зібрати + залити на девайс

# запуск: MainActivity НЕ exported, тому `am start` падає з SecurityException.
# єдиний робочий спосіб з adb:
$ADB shell monkey -p com.selftest.mindora -c android.intent.category.LAUNCHER 1

$ADB logcat -s self_mind:V AndroidRuntime:E  # логи (тег із util/util.kt)
$ADB exec-out screencap -p > /tmp/shot.png   # скріншот — можна відкрити й подивитись
$ADB shell input tap 360 1050                # тик по екрану, щоб пройти флоу
```
Девайс зазвичай підключений (Redmi, Android 13, 720×1650 px, Adreno 610).
Юзер запускає апку кнопкою Run у Android Studio; adb потрібен, щоб я сам
подивився результат.

**Якщо міняєш UI, анімацію чи шейдер — зроби скріншот і подивись на нього.**
Не описувати зміну словами, не здогадуватись — перевіряти очима.

У логах уже є трасування життєвого циклу (`show/dispose AdvancedScreen: X`)
і дамп збереженого стану при старті — цього зазвичай досить, щоб зрозуміти,
де застряг перехід, без додаткових логів.

## Архітектура

```
MainActivity (Android)     → реклама, Firebase RC, permissions, share/rate
  └ GDXFragment → GDXGame  → AdvancedGame, тримає менеджери
      └ AdvancedScreen     → база всіх екранів: stageUI, viewport, safe area
```

- **Навігація тільки через `NavigationManager`** — `navigate()` / `back()`.
  Він веде backStack, знає `fromScreenName` і тригерить interstitial-рекламу.
  Прямий `game.setScreen()` ламає і бекстек, і рекламу.
- Екрани — `game/screens/*Screen.kt`, логіка контенту — `game/controller/*Controller.kt`,
  ресурси — `game/manager/*Manager.kt`.
- Тексти/питання тестів — `assets/tests/*.json` (kotlinx.serialization),
  індекс і метадані — `game/content/TestCatalog.kt`. Це джерело правди для контенту.

## Конвенції

- **Актори — префікс `A`**: `AButton`, `APanelHome`, `AMsdfLabel`. Файл = клас.
  Базові класи в `utils/advanced/` — префікс `Advanced`.
- Координати UI — **world-юніти**, не пікселі. Дизайн-база `376 × 815`
  (`Constants.kt`), `ExtendViewport`. Системні бари й банер — через
  `safeStatusBarUI` / `safeNavBarUI` / `adBannerUI` в `AdvancedScreen`.
- Кольори — `utils/GameColor.kt`, не хардкодити hex по місцях.
- Текст — MSDF-шрифти (`AMsdfLabel` + `utils/font/msdf/`), не `BitmapFont`.
- Все `Disposable` — в `disposableSet` екрана, інакше тече пам'ять.

## Коментарі

Секції всередині класу — сепаратор із рядків дефісів:
```kotlin
// ------------------------------------------------------------------------
// Ad система
// ------------------------------------------------------------------------
```
Підсекції — `// ── Banner ────────`. Пояснення «чому саме так» — KDoc `/** */`.
Звичайні `//` теж норм. Не прибирати наявні сепаратори при рефакторингу.

## Шейдери

`assets/shader/**/*.glsl`, таргет — **OpenGL ES 2.0**: `varying`/`attribute`,
без `in`/`out`, без `#version`. Кожен FS починається з:
```glsl
#ifdef GL_ES
precision mediump float;
#endif
```
Спільний вершинний — `shader/defaultVS.glsl`. Пост-ефекти (blur, mask,
roundRect) — через `utils/vfx/` (FBO-стек, ping-pong, кеш шейдерів).
Розміри в шейдери передавати у world-юнітах, щоб радіуси не «пливли».

## Графіка: атлас vs окремі текстури

Два шляхи, залежно від розміру:

- **Малі й середні** → в атлас. Вихідні PNG лежать у `../assets/all/`,
  `../assets/_9_patch/`, `../assets/loader/`, пакуються **TexturePacker'ом**
  через `assets.tps` у кожній папці. Реєстрація — `SpriteManager.EnumAtlas`.
- **Великі** (фони, попапи, ілюстрації) → кладуться в `assets/textures/**`
  окремими файлами й реєструються в `SpriteManager.EnumTexture`.
  Пронумеровані серії (`onboarding_1..3`) — через `EnumTextureGroup`,
  який сам збирає шляхи за патерном `folder/prefix_N.png`.

Додаєш картинку — додай запис у відповідний enum, інакше вона не завантажиться.
`assets/atlas/*.atlas` і `*.png` — **згенеровані**, руками не правити.

## Не чіпати руками

- `app/libs/**/*.so` — розпаковуються таском `copyAndroidNatives`.
- `local.properties`, `build/`, `.gradle/`.

## Контекст репозиторію

Це один проєкт всередині великого репо UAPP, де лежать не пов'язані між собою
ігри різних замовників. Правки роблю **тільки в межах цієї папки**.

## Git

**Не роби git-операцій.** Ніяких `commit`, `add`, `push`, `checkout`, `stash`.
Комміти йдуть вручну через GitHub Desktop — я лише міняю файли в робочій копії.
Підсвітку змінених файлів в Android Studio вимкнено навмисно (заважала), тому
не покладайся на неї і не пропонуй вмикати назад.
