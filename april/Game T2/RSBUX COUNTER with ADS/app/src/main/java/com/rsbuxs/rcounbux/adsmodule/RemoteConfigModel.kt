package com.rsbuxs.rcounbux.adsmodule

import com.google.gson.annotations.SerializedName

data class RemoteConfigModel(
    val config: Config?,
    @SerializedName("ad_units") val adUnits: AdUnits?
)

data class Config(
    val organic: AdProviders?,
    val paid: AdProviders?
)

data class AdProviders(
    val banner: String = "na",
    val native: String = "na",
    val interstitial: String = "na",
    @SerializedName("app_open") val appOpen: String = "na"
)

data class AdUnits(
    val admob: AdmobUnits?,
    val custom: CustomUnits?
)

data class AdmobUnits(
    val banner: String = "",
    val native: String = "",
    val interstitial: String = "",
    @SerializedName("app_open") val appOpen: String = ""
)

data class CustomUnits(
    val banner: CustomBanner?,
    val native: CustomNative?,
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