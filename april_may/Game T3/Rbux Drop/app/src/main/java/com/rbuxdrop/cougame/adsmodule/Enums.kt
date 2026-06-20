package com.rbuxdrop.cougame.adsmodule

// Провайдер реклами — хто показує рекламу
enum class AdProvider(val value: String) {
    ADMOB("admob"),
    CUSTOM("custom"),
    CUSTOM_GOOGLE("custom_google"),
    CUSTOM_TIKTOK("custom_tiktok"),
    CUSTOM_FACEBOOK("custom_facebook"),
    NA("na");

    // Чи це один з кастомних провайдерів (custom / custom_google / custom_tiktok / custom_facebook)
    fun isCustomProvider() = this == CUSTOM ||
            this == CUSTOM_GOOGLE ||
            this == CUSTOM_TIKTOK ||
            this == CUSTOM_FACEBOOK

    companion object {
        fun from(value: String): AdProvider =
            entries.find { it.value == value } ?: NA
    }
}

// Тип реклами — який формат
enum class AdType(val value: String) {
    BANNER("banner"),
    NATIVE("native"),
    INTERSTITIAL("interstitial"),
    APP_OPEN("app_open");
}