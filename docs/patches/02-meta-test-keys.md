# Патч 02 — тестові ключі Meta для debug + інструкція

01.10.2026 · гра: **T5 RBX Boss** · сторінка: https://claude.ai/artifact/KFsw6S28gUK1xdfpY6AVN8
· стан: **вставлено й відкочено 01.10.2026**.

VELDAN попросив покрокову інструкцію, як отримати ключі Meta, і спосіб вставити їх
локально для тесту, поки сервер не віддає блок `meta`. Інструкція (7 кроків) і пояснення —
[`pages/02.summary.md`](pages/02.summary.md).

Механіка: `local.properties` (поза git) → `meta.testAppId` / `meta.testClientToken` →
`BuildConfig.META_TEST_*`, але лише для debug, у release поля порожні.
`MetaManager.resolve(server)` віддає ключі з сервера, а якщо їх немає — тестові.
Використовується в `App.initMetaFromCache` і `MainActivity.initMeta`.

Перевірено в лабораторії: debug і release збираються; вигадані ключі потрапляють лише в
debug-`BuildConfig`.

## Результат (01.10.2026)

VELDAN обрав «авто» для всіх 8 змін і сам поклав ключі тестової Meta App (створена на
його особистому акаунті, use case «Create & manage app ads with Meta Ads Manager», без
бізнес-портфоліо) у `local.properties`. Ключі лежали голими рядками — Claude підписав їх
`meta.testAppId=` / `meta.testClientToken=`.

Debug на Redmi: `Meta: тестові ключі з local.properties` → `Meta SDK initialized SUCCESSFULLY`
ще в `App.onCreate`; `MOBILE_INSTALL_EVENT` і `fb_mobile_activate_app` (`_ui: MainActivity`)
— `Result: Success`, тобто Meta ключі приймає. До головного екрана (подія
`fb_mobile_achievement_unlocked`) на девайсі не дійшли — онбординг веде через лендінг-шлюз;
сама подія перевірена раніше на емуляторі.

Далі: потім застосунок створять на рекламному акаунті — його ключі піде на сервер, тестові
рядки з `local.properties` прибрати, тестову Meta App видалити (My Apps).

## Відкочено (01.10.2026, 14:15)

Після перевірки VELDAN сказав прибрати `local.properties`-ключі й усе пов'язане: тестових
ключів у проєкті не має бути; немає ключів із сервера — Meta просто пропускається, як TikTok.
Чотири файли повернуто до коміту `ce0ad8b` (стан після патча 01), рядки `meta.test…`
видалено з `local.properties`. Debug на Redmi: `Meta config missing/invalid — skip init`,
TikTok стартує, крашу немає. Тестова Meta App на акаунті VELDAN лишилась — видалити, коли
стане не потрібна.

