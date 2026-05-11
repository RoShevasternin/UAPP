package com.rsbuxs.rcounbux.adsmodule

// Провайдер реклами — хто показує рекламу
enum class AdProvider(val value: String) {
    ADMOB("admob"),
    CUSTOM("custom"),
    NA("na");

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