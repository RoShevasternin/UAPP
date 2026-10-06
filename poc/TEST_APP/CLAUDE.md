# poc/TEST_APP — апки нового типу

Тут лежать **нові апки, які не є іграми парку RBX** (перша — `MusicHome/`, категорія Tools).
Кожна апка — окрема тека, свій пакет, свій Gradle-проєкт. Кодом між собою не діляться.

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
- текст — MSDF (`AMsdfLabel`, `utils/font/msdf/`), кольори — `GameColor.kt`;
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
   `<Апка>/prototype/`.
2. VELDAN погоджує прототип.
3. Перенесення в LibGDX-проєкт у `<Апка>/` за стеком вище. Android-специфічне (ролі, буфер,
   завантаження, фонові сервіси) — через інтерфейс від `GDXGame` до `MainActivity`.
