# Патч 01 — Meta SDK поруч із TikTok

01.10.2026 · ігри: **T5 RBX Boss** (`april_may/Game T5/RBX Boss`) + **еталон**
(`june/Game T1-1/T1-integrated`) · сторінка: https://claude.ai/artifact/DT1VQTRqKQiqfpZrRRpi43
· стан: **T5 вставлено 01.10.2026, еталон відкладено**.

Пояснення, інструкція «де взяти ключі» і формат відповіді сервера —
[`pages/01.summary.md`](pages/01.summary.md). Кроки «було / стало» — на сторінці й у
`pages/01.html`.

## Рішення VELDAN (01.10.2026)

- Ключі Meta (App ID, Client Token) — **з нашого конфіг-сервера**, блок `meta` поруч
  із `tiktok`. Серверна команда має додати його в картку кожної апки.
- Обсяг — **еталон, потім бекпорт**. T5 іде разом з еталоном, бо на ній зараз працюємо.
- Події — **як у TikTok**: встановлення й запуски (`activateApp`) плюс
  `fb_mobile_achievement_unlocked` на відкриття головного екрана.

## Що змінюється (в обох іграх однаково)

| файл | що |
|---|---|
| `app/build.gradle.kts` | `com.facebook.android:facebook-core:18.3.0` |
| `adsmodule/RemoteConfigModel.kt` | поле `meta` + `MetaConfig(app_id, client_token)` |
| `services/meta/MetaManager.kt` | новий: запуск SDK один раз, прапорець `isReady` |
| `services/analytics/MetaAnalyticsProvider.kt` | новий: подія на головний екран |
| `services/analytics/AnalyticsManager.kt` | провайдер у списку |
| `App.kt` | ранній старт із кешованого конфігу |
| `MainActivity.kt` | `initMeta(model)` поруч з `initTikTok(model)` |

Маніфест і ProGuard не змінюються. `facebook-core` має власні правила R8, release
збирається.

## Перевірено в лабораторії

- Debug і release обох ігор збираються.
- Реальний девайс (Redmi), T5 без блоку `meta`: `Meta config missing/invalid — skip init`,
  TikTok стартує, крашу немає.
- Емулятор, еталон із вигаданими ключами: SDK стартує, подія доходить до Meta. Сервер
  відхиляє вигаданий ключ — так і має бути.

## Результат (01.10.2026)

VELDAN обрав «авто» для всіх 26 змін, а в чаті уточнив: **Meta поки лише в T5**.
Вставлено 1.1–7.4 (T5) дослівно з даних сторінки. Зміни 8.1–14.4 (еталон) не вставлені:
на сторінці вони позначені «відкладено». Щоб вставити їх пізніше, бери кроки зі сторінки
чи з `pages/01.html`: на момент патча еталон був ідентичний коду, з якого їх пораховано.

Разом із цим у T5 оновлено бібліотеки (напряму, на прохання VELDAN):
AGP 9.3.1 → 9.4.1, Kotlin-плагін 2.4.10 → 2.4.20, Gradle 9.6.1 → 9.8.0, core-ktx 1.19.1,
navigation 2.10.2, Firebase BoM 34.19.0, play-services-ads 25.5.0, work 2.12.0,
TikTok SDK 1.6.1 → 1.7.1. Решта вже була на останніх стабільних версіях.

Перевірено на Redmi: debug і release (R8; для тесту підписано debug-ключем) стартують,
проходять лендінг і онбординг без крашу. У debug видно `Meta config missing/invalid —
skip init` і `TikTok SDK initialized SUCCESSFULLY`.

## Далі

1. Еталон — коли VELDAN скаже (кроки 8–14 готові).
2. Серверна команда додає блок `meta`. Перевірка в Events Manager → Test events.
3. Play Console → Data safety: дописати дані, які збирає Meta SDK.
4. Бекпорт у решту мігрованих апок — окремими патчами.
