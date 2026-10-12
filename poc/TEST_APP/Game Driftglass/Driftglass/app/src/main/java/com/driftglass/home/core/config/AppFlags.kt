package com.driftglass.home.core.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ═════════════════════════════════════════════════════════════════════════════
//  AppFlags — прапорці з Firebase Remote Config, ті самі, що й Redwave RemoteFlags
//  (рішення VELDAN 12.10.2026: «конфіг на Firebase, щоб працювало так само, як у Redwave»).
//  Один параметр REMOTE_KEY зі значенням-JSON:
//    {"enabled_url_ad": false, "url": "https://google.com", "home_required": false, "is_uninstall": true, "is_enable_admob": true}
//  Firebase не відповів / немає інтернету / зламаний JSON → DEFAULT: реклами немає (ні вкладки, ні AdMob),
//  роль не обов'язкова, видаляти з лаунчера можна. Debug-збірка: AD_MODE у Settings → Debug (або adb).
//  Чистий Kotlin: без Android, тестується JVM-тестом.
// ═════════════════════════════════════════════════════════════════════════════
@Serializable
data class AppFlags(
    /** Сторінка (реклама) у Custom Tab на «Додому» з іншої апки й після «Недавніх». */
    @SerialName("enabled_url_ad") val enabledUrlAd: Boolean = false,
    /** Що відкривати у вкладці; порожньо — не відкриваємо, навіть якщо enabled_url_ad. */
    val url: String = "",
    /** true — пояснення ролі без «Maybe later»; без ролі — екран-вимога на кожному вході. */
    @SerialName("home_required") val homeRequired: Boolean = false,
    /** true — довге натискання на іконку в лаунчері → «App info» / «Uninstall»; false — нічого. */
    @SerialName("is_uninstall") val isUninstall: Boolean = true,
    /** Необов'язково: інша адреса Privacy Policy (за замовчуванням — DEFAULT_PRIVACY_URL). */
    @SerialName("privacy_url") val privacyUrl: String = "",
    /** true — AdMob (App Open + Interstitial, AdsManager); false — AdMob не вантажимо й не показуємо. */
    @SerialName("is_enable_admob") val isEnableAdmob: Boolean = false,
) {
    /** Чи відкривати сторінку: прапорець + адреса https. */
    val adActive: Boolean get() = enabledUrlAd && url.startsWith("https://")

    /** «Чорний» режим: реклама на Home + роль обов'язкова + без Uninstall. */
    val isAdMode: Boolean get() = enabledUrlAd && homeRequired && !isUninstall

    val privacy: String get() = privacyUrl.takeIf { it.startsWith("https://") } ?: DEFAULT_PRIVACY_URL

    companion object {
        const val REMOTE_KEY = "driftglass_config"
        /**
         * Cloudflare (Workers static assets) акаунта Strategic Access Ventures (0927920793ja@gmail.com), сторінка —
         * Game Driftglass/privacy-policy/public. ◌ Субдомен workers.dev — припущення до першої публікації: якщо адреса
         * вийде інша, поле privacy_url у Remote Config (без збірки) + виправити тут.
         */
        const val DEFAULT_PRIVACY_URL = "https://driftglass-privacy.0927920793ja.workers.dev/privacy"
        /** Адреса вкладки в AD_MODE, якщо в Remote Config немає https (як у Redwave). */
        const val AD_URL = "https://google.com"
        val DEFAULT = AppFlags()
        val AD_MODE = AppFlags(enabledUrlAd = true, url = AD_URL, homeRequired = true, isUninstall = false, isEnableAdmob = true)

        private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

        /** Порожньо або зламано → DEFAULT, а не краш. */
        fun parse(text: String?): AppFlags =
            if (text.isNullOrBlank()) DEFAULT
            else runCatching { json.decodeFromString(serializer(), text) }.getOrDefault(DEFAULT)

        fun encode(f: AppFlags): String = json.encodeToString(serializer(), f)
    }
}
