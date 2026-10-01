# Патч 01 — Meta SDK поруч із TikTok

01.10.2026 · ігри: **T5 RBX Boss** (`april_may/Game T5/RBX Boss`) + **еталон**
(`june/Game T1-1/T1-integrated`) · сторінка: https://claude.ai/artifact/DT1VQTRqKQiqfpZrRRpi43
· стан: **чекає вибору**.

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

## Далі

1. Вибір на сторінці → вставка → збірка → девайс.
2. Серверна команда додає блок `meta`. Перевірка в Events Manager → Test events.
3. Play Console → Data safety: дописати дані, які збирає Meta SDK.
4. Бекпорт у решту мігрованих апок — окремими патчами.
