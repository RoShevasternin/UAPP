package com.rbuxdrop.cougame.adsmodule

object AdConfig {

    var remoteConfig: RemoteConfigModel? = null
    var userType: UserType = UserType.ORGANIC

    var isFullscreenAdShowing = false

    // ── Provider resolution ─────────────────────────────────────────────────
    // Який провайдер показувати для конкретного типу реклами (на основі типу юзера)

    fun getProvider(adType: AdType): AdProvider {
        val config    = remoteConfig?.config ?: return AdProvider.NA
        val providers = providersForUser(config) ?: return AdProvider.NA

        val value = when (adType) {
            AdType.BANNER       -> providers.banner
            AdType.NATIVE       -> providers.native
            AdType.INTERSTITIAL -> providers.interstitial
            AdType.APP_OPEN     -> providers.appOpen
        }
        return AdProvider.Companion.from(value)
    }

    // Яка секція config відповідає типу юзера
    // Платні мережі fallback-аються на paid якщо їх секції немає в конфізі
    private fun providersForUser(config: Config): AdProviders? = when (userType) {
        UserType.ORGANIC       -> config.organic
        UserType.PAID          -> config.paid
        UserType.PAID_GOOGLE   -> config.gclid  ?: config.paid
        UserType.PAID_TIKTOK   -> config.ttclid ?: config.paid
        UserType.PAID_FACEBOOK -> config.fbclid ?: config.paid
    }

    // ── AdMob ids ─────────────────────────────────────────────────────────────

    fun admobBannerId(): String       = remoteConfig?.adUnits?.admob?.banner ?: ""
    fun admobNativeId(): String       = remoteConfig?.adUnits?.admob?.native ?: ""
    fun admobInterstitialId(): String = remoteConfig?.adUnits?.admob?.interstitial ?: ""
    fun admobAppOpenId(): String      = remoteConfig?.adUnits?.admob?.appOpen ?: ""

    // ── Custom units ──────────────────────────────────────────────────────────
    // Секція юнітів залежить від ПРОВАЙДЕРА (не від типу юзера)
    // бо getProvider() вже повернув custom / custom_google / custom_tiktok / custom_facebook

    private fun customUnitsFor(provider: AdProvider): CustomUnits? {
        val units = remoteConfig?.adUnits ?: return null
        return when (provider) {
            AdProvider.CUSTOM          -> units.custom
            AdProvider.CUSTOM_GOOGLE   -> units.customGoogle
            AdProvider.CUSTOM_TIKTOK   -> units.customTiktok
            AdProvider.CUSTOM_FACEBOOK -> units.customFacebook
            else                       -> null
        }
    }

    fun customBannerImages(provider: AdProvider) = customUnitsFor(provider)?.banner?.images ?: emptyList()
    fun customNativeAssets(provider: AdProvider) = customUnitsFor(provider)?.native?.assets ?: emptyList()
    fun customInterstitial(provider: AdProvider) = customUnitsFor(provider)?.interstitial
    fun customAppOpenUrl(provider: AdProvider) = customUnitsFor(provider)?.appOpen?.targetUrl ?: ""

}