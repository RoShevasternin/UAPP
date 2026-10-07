# poc/TEST_APP — апки нового типу

Тут лежать **нові апки, які не є іграми парку RBX** (перша — `Game Redwave/`, категорія Tools;
свої правила й план переносу — `Game Redwave/Redwave/CLAUDE.md`, `Game Redwave/PORTING.md`).
Кожна апка — окрема тека, свій пакет, свій Gradle-проєкт. Кодом між собою не діляться.

## Структура теки апки — як в іграх парку

Як `agust/Game T35/` → `Mindora Self Test/` + `market/` + `assets/`: зовнішня тека
`Game <Назва>/`, а **сама апка — лише у вкладеній `Game <Назва>/<Назва>/`**. Усе дотичне лежить
поруч і в Android-проєкт не змішується:

```
Game <Назва>/
├── <Назва>/        ← апка: Android-проєкт LibGDX + її CLAUDE.md
├── PORTING.md, SCREENS.md, README.md
├── prototype/      ← прототип з claude.ai
├── port-kit/       ← чиста Kotlin-логіка з тестами (копіюється в апку)
├── assets/         ← джерела графіки й шрифтів (копіюються в апку)
├── market/         ← Google Play
└── test-media/ …   ← інші матеріали для тестів
```

## Еталона тут немає

Правила кореневого `CLAUDE.md` про еталон до цієї теки **не застосовуються**:

- **не** підключаємо `businesModule` і `adsmodule`;
- **не** йдемо за `docs/procedures/ETALON_MIGRATION.md` і не повторюємо логіку еталона
  (конфіг-сервер, атрибуція, лендінги, `Wallet`/`Econ`, пуші);
- рекламу, аналітику чи сервер додаємо лише тоді, коли VELDAN окремо скаже, і під потреби
  конкретної апки.

## Стек — як у LibGDX-апках UAPP

Апку робимо на LibGDX тим самим інструментарієм, що й ігри парку, але з нуля під нову задачу:

- каркас `AdvancedGame` / `AdvancedScreen` / `AdvancedStage` / `AdvancedGroup`, навігація
  тільки через `NavigationManager`;
- екрани — `game/screens/*Screen.kt`, актори з префіксом `A` (`AButton`, `APanel…`),
  розкладка — `AConstraintLayout` / `AAutoLayout`;
- текст — MSDF (`AMsdfLabel`, `utils/font/msdf/`), кольори — `GameColor.kt`; перевіряти, чи є
  в шрифті кирилиця, якщо апка показує текст користувача;
- шейдери — `assets/shader/**/*.glsl`, OpenGL ES 2.0; пост-ефекти (blur, mask, roundRect) —
  через `utils/vfx/` (FBO-стек, ping-pong, кеш шейдерів);
- звук і музика — `SoundManager` / `MusicManager` / `AudioManager`, вібро — `VibroUtil`;
- графіка — `SpriteManager` (атласи + окремі текстури), частинки — `ParticleEffectManager`;
- збереження — `DataStoreManager` + kotlinx.serialization.

**Донор інструментів — `agust/Game T35/Mindora Self Test`**: найсвіжіший набір
(MSDF, `FboStack`, 10 шейдерів, `stateMachine`, `overlay`). Звідти копіюємо `utils/`,
`manager/` і базові актори (кнопки, лейбли, layout, vfx, popup, scroll). Контент, екрани,
рекламу, Firebase, TikTok, білінг і install referrer Mindora **не** беремо.

## Порядок роботи

1. Прототип — артефакт на claude.ai (будь-яка технологія, зараз HTML/JS), копія в
   `Game <Назва>/prototype/`. Чиста логіка — одразу Kotlin з тестами в `Game <Назва>/port-kit/`.
2. VELDAN погоджує прототип.
3. Перенесення в LibGDX-проєкт у `Game <Назва>/<Назва>/` за стеком вище. Android-специфічне (ролі, буфер,
   завантаження, фонові сервіси) — через інтерфейс від `GDXGame` до `MainActivity`.
