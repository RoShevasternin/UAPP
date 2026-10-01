# Патч 02 — тестові ключі Meta для debug + інструкція

01.10.2026 · гра: **T5 RBX Boss** · сторінка: https://claude.ai/artifact/KFsw6S28gUK1xdfpY6AVN8
· стан: **чекає вибору**.

VELDAN попросив покрокову інструкцію, як отримати ключі Meta, і спосіб вставити їх
локально для тесту, поки сервер не віддає блок `meta`. Інструкція (7 кроків) і пояснення —
[`pages/02.summary.md`](pages/02.summary.md).

Механіка: `local.properties` (поза git) → `meta.testAppId` / `meta.testClientToken` →
`BuildConfig.META_TEST_*`, але лише для debug, у release поля порожні.
`MetaManager.resolve(server)` віддає ключі з сервера, а якщо їх немає — тестові.
Використовується в `App.initMetaFromCache` і `MainActivity.initMeta`.

Перевірено в лабораторії: debug і release збираються; вигадані ключі потрапляють лише в
debug-`BuildConfig`.
