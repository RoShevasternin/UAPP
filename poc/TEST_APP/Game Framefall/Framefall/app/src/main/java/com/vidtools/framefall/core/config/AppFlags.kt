package com.vidtools.framefall.core.config

// ═════════════════════════════════════════════════════════════════════════════
//  AppFlags — режим Framefall, що випливає з картки апки на НАШОМУ сервері
//  (/appconfig, businesModule). Firebase Remote Config прибрано (рішення VELDAN 12.10.2026).
//
//  Вирішує провайдер app_open для типу цього юзера (config.<organic|paid|gclid…>.app_open):
//    • custom / custom_google / custom_tiktok / custom_facebook — «наша реклама»:
//        вкладка на «Додому» і після очищення «Недавніх» (pl=app_open),
//        роль HOME обов'язкова (без «Maybe later»), видалити з лаунчера не можна;
//    • na / admob / немає конфігу — «Maybe later» є, видаляти можна, вкладок на «Додому» немає.
//  Немає інтернету → DEFAULT завжди (рішення VELDAN 07.10.2026, перенесено з Remote Config).
//  Debug AD_MODE — примусово «наша реклама».
//  Чистий Kotlin: без Android, тестується JVM-тестом.
// ═════════════════════════════════════════════════════════════════════════════
data class AppFlags(
    /** Вкладка на «Додому» з іншої апки й після очищення «Недавніх» (лише з роллю HOME і онлайн). */
    val homeTabs: Boolean = false,
    /** true — на 3-му слайді лише «Set as Home screen»; false — ще й «Maybe later». */
    val homeRequired: Boolean = false,
    /** true — довге натискання на іконку в лаунчері → «App info» / «Uninstall». */
    val isUninstall: Boolean = true,
) {
    val privacy: String get() = DEFAULT_PRIVACY_URL

    companion object {
        /** Cloudflare (Workers static assets) акаунта STAR ADS LLC; сторінка — Game Framefall/privacy-policy/public. */
        const val DEFAULT_PRIVACY_URL = "https://framefall-privacy.oyutetijep68.workers.dev/privacy"

        /**
         * Плейсмент вкладки на «Додому» / після «Недавніх» — той самий app_open, що й на старті
         * (рішення VELDAN 13.10.2026: нових плейсментів на сервері не заводимо). Ключ доходу <app>-<pl>.
         */
        const val PL_HOME_TAB = "app_open"

        val DEFAULT = AppFlags()
        private val OURS = AppFlags(homeTabs = true, homeRequired = true, isUninstall = false)

        private val CUSTOM_PROVIDERS = setOf("custom", "custom_google", "custom_tiktok", "custom_facebook")

        /** appOpenProvider — рядок з картки (na / admob / custom…), null — конфігу немає. */
        fun from(appOpenProvider: String?, online: Boolean, adMode: Boolean): AppFlags = when {
            adMode                                        -> OURS
            online && appOpenProvider in CUSTOM_PROVIDERS -> OURS
            else                                          -> DEFAULT
        }
    }
}
