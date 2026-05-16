package com.rbuxdrop.cougame.adsmodule

object AdConfig {

    var remoteConfig: RemoteConfigModel? = null
    var userType: UserType = UserType.ORGANIC

    var isFullscreenAdShowing = false

    fun getProvider(adType: AdType): AdProvider {
        val config   = remoteConfig        ?: return AdProvider.NA
        val adConfig = config.config       ?: return AdProvider.NA

        val providers = when (userType) {
            UserType.ORGANIC -> adConfig.organic ?: return AdProvider.NA
            UserType.PAID    -> adConfig.paid    ?: return AdProvider.NA
        }

        val value = when (adType) {
            AdType.BANNER       -> providers.banner
            AdType.NATIVE       -> providers.native
            AdType.INTERSTITIAL -> providers.interstitial
            AdType.APP_OPEN     -> providers.appOpen
        }

        return AdProvider.from(value)
    }

    fun admobBannerId(): String       = remoteConfig?.adUnits?.admob?.banner ?: ""
    fun admobNativeId(): String       = remoteConfig?.adUnits?.admob?.native ?: ""
    fun admobInterstitialId(): String = remoteConfig?.adUnits?.admob?.interstitial ?: ""
    fun admobAppOpenId(): String      = remoteConfig?.adUnits?.admob?.appOpen ?: ""

    fun customBannerImages()   = remoteConfig?.adUnits?.custom?.banner?.images ?: emptyList()
    fun customNativeAssets()   = remoteConfig?.adUnits?.custom?.native?.assets ?: emptyList()
    fun customInterstitial()   = remoteConfig?.adUnits?.custom?.interstitial
    fun customAppOpenUrl()     = remoteConfig?.adUnits?.custom?.appOpen?.targetUrl ?: ""
}