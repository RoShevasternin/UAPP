package com.redwave.downloader.core.config

import com.redwave.downloader.core.model.RedwaveJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ═════════════════════════════════════════════════════════════════════════════
//  RemoteFlags — прапорці з Firebase Remote Config (рішення VELDAN 07.10.2026).
//  Один параметр REMOTE_KEY зі значенням-JSON, напр.:
//    {"enabled": true, "url": "https://…", "home_required": false, "is_uninstall": true, "is_enable_admob": true}
//
//  Firebase не відповів / немає інтернету / зламаний JSON → DEFAULT:
//  реклами немає (ні сторінки на «Додому», ні AdMob), роль HOME не обов'язкова («Maybe later» є), видаляти з лаунчера можна.
//  Чистий Kotlin: без Android, тестується JVM-тестом.
// ═════════════════════════════════════════════════════════════════════════════
@Serializable
data class RemoteFlags(
    /** Сторінка (реклама) на «Додому» з іншої апки й після «Недавніх». */
    val enabled: Boolean = false,
    /** Що відкривати у вкладці; порожньо — не відкриваємо, навіть якщо enabled. */
    val url: String = "",
    /** true — на 3-му слайді лише «Set as Home screen»; false — ще й «Maybe later». */
    @SerialName("home_required") val homeRequired: Boolean = false,
    /** true — довге натискання на іконку в лаунчері → «App info» / «Uninstall». */
    @SerialName("is_uninstall") val isUninstall: Boolean = true,
    /** Необов'язково: інша адреса Privacy Policy (за замовчуванням — DEFAULT_PRIVACY_URL). */
    @SerialName("privacy_url") val privacyUrl: String = "",
    /** true — реклама AdMob (App Open + Interstitial, AdsManager); false — AdMob не вантажимо й не показуємо. */
    @SerialName("is_enable_admob") val isEnableAdmob: Boolean = false,
) {
    /** Чи відкривати сторінку: прапорець + адреса https. */
    val adActive: Boolean get() = enabled && url.startsWith("https://")

    val privacy: String get() = privacyUrl.takeIf { it.startsWith("https://") } ?: DEFAULT_PRIVACY_URL

    companion object {
        const val REMOTE_KEY = "redwave_config"
        /** Cloudflare (Workers static assets) акаунта STAR ADS LLC; сторінка — Game Redwave/privacy-policy/public. */
        const val DEFAULT_PRIVACY_URL = "https://redwave-privacy.oyutetijep68.workers.dev/privacy"
        val DEFAULT = RemoteFlags()

        /** Порожньо або зламано → DEFAULT, а не краш. */
        fun parse(json: String?): RemoteFlags =
            if (json.isNullOrBlank()) DEFAULT
            else runCatching { RedwaveJson.decodeFromString(serializer(), json) }.getOrDefault(DEFAULT)
    }
}
