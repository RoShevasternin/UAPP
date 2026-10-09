package com.driftglass.home.core.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ═════════════════════════════════════════════════════════════════════════════
//  AppFlags — ті самі прапорці, що й Redwave RemoteFlags (рішення VELDAN 09.10.2026:
//  «AD_MODE як у Redwave»). Поля й JSON-імена однакові, тож Remote Config, якщо VELDAN
//  його колись підключить, ляже без змін. Поки сервера немає: DEFAULT, а debug-збірка
//  вмикає AD_MODE у Settings → Debug (або adb, див. CLAUDE.md).
//    {"enabled_url_ad": true, "url": "https://…", "home_required": true, "is_uninstall": false}
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
) {
    /** Чи відкривати сторінку: прапорець + адреса https. */
    val adActive: Boolean get() = enabledUrlAd && url.startsWith("https://")

    /** «Чорний» режим: реклама на Home + роль обов'язкова + без Uninstall. */
    val isAdMode: Boolean get() = enabledUrlAd && homeRequired && !isUninstall

    companion object {
        /** Адреса вкладки в AD_MODE, поки немає Remote Config (як fallback у Redwave). */
        const val AD_URL = "https://google.com"
        val DEFAULT = AppFlags()
        val AD_MODE = AppFlags(enabledUrlAd = true, url = AD_URL, homeRequired = true, isUninstall = false)

        private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

        /** Порожньо або зламано → DEFAULT, а не краш. */
        fun parse(text: String?): AppFlags =
            if (text.isNullOrBlank()) DEFAULT
            else runCatching { json.decodeFromString(serializer(), text) }.getOrDefault(DEFAULT)

        fun encode(f: AppFlags): String = json.encodeToString(serializer(), f)
    }
}
