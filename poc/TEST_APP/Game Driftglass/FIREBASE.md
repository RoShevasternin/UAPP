# Driftglass — Firebase (лише FCM)

Проєкт **`driftglass-original`** (VELDAN, 12.10.2026), файл `Driftglass/app/google-services.json` (апка `com.driftglass.home`).

З **15.10.2026** (рішення VELDAN: «бізнес-логіка як у Redwave та Framefall») Firebase потрібен **лише для FCM** —
токен пушів і серверні пуші (`businesModule/push/PushService`). Режим апки й реклама приходять з **картки `home`**
нашого сервера (`/appconfig`, `android/ServerConfig.kt`), а не з Remote Config.

Параметр Remote Config `driftglass_config`, створений 12.10.2026, апка **більше не читає** — його можна лишити або
видалити в консолі, на роботу це не впливає. Файли `firebase/*.json` — історія того конфігу.

Як працює режим, плейсменти, пуші й диплінк — `Driftglass/CLAUDE.md`, розділ «Бізнес-логіка UAPP».
