package com.diam.ondbit.adsmodule

import com.google.gson.annotations.SerializedName

// ------------------------------------------------------------------------
// RemoteConfigModel
// ------------------------------------------------------------------------
data class RemoteConfigModel(
    val config: Config?,
    @SerializedName("ad_units") val adUnits: AdUnits?,
    @SerializedName("tiktok")   val tiktok : TikTokConfig? = null,
)

// ------------------------------------------------------------------------
// Config — який провайдер для кожного типу юзера
// ------------------------------------------------------------------------
data class Config(
    val organic: AdProviders?,
    val paid   : AdProviders?,
    val gclid  : AdProviders?,   // Google Ads юзери
    val ttclid : AdProviders?,   // TikTok Ads юзери
    val fbclid : AdProviders?,   // Facebook Ads юзери
)

data class AdProviders(
    val banner      : String = "na",
    val native      : String = "na",
    val interstitial: String = "na",
    @SerializedName("app_open") val appOpen: String = "na"
)

// ------------------------------------------------------------------------
// AdUnits — конкретні рекламні юніти
// ------------------------------------------------------------------------
data class AdUnits(
    val admob          : AdmobUnits?,
    val custom         : CustomUnits?,
    @SerializedName("custom_google")   val customGoogle  : CustomUnits?,
    @SerializedName("custom_tiktok")   val customTiktok  : CustomUnits?,
    @SerializedName("custom_facebook") val customFacebook: CustomUnits?,
)

data class AdmobUnits(
    val banner      : String = "",
    val native      : String = "",
    val interstitial: String = "",
    @SerializedName("app_open") val appOpen: String = ""
)

data class CustomUnits(
    val banner      : CustomBanner?,
    val native      : CustomNative?,
    val interstitial: CustomInterstitial?,
    @SerializedName("app_open") val appOpen: CustomAppOpen?
)

data class CustomBanner(
    val images: List<BannerImage> = emptyList()
)

data class BannerImage(
    val url: String,
    @SerializedName("target_url") val targetUrl: String
)

data class CustomNative(
    val assets: List<NativeAsset> = emptyList()
)

data class NativeAsset(
    val icon: String,
    val image: String,
    val headline: String,
    val description: String,
    val cta: String,
    @SerializedName("target_url") val targetUrl: String
)

data class CustomInterstitial(
    @SerializedName("target_url")       val targetUrl: String = "",
    @SerializedName("front_navigation") val frontNavigation: NavConfig = NavConfig(),
    @SerializedName("back_navigation")  val backNavigation: NavConfig = NavConfig()
)

data class NavConfig(
    val enabled: Boolean = false,
    val frequency: Int = 1,
    @SerializedName("reset_on_app_restart") val resetOnAppRestart: Boolean = false
)

data class CustomAppOpen(
    @SerializedName("target_url") val targetUrl: String = ""
)

// ------------------------------------------------------------------------
// TikTok
// ------------------------------------------------------------------------
data class TikTokConfig(
    @SerializedName("app_id") val appIdRaw: String? = null,
    @SerializedName("secret") val secret  : String? = null,
) {
    val appIds: List<String>
        get() = appIdRaw
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

    val isValid: Boolean
        get() = appIds.isNotEmpty() && !secret.isNullOrBlank()
}