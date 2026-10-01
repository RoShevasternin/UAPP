package com.bossrbx.rbxcalculator.services.analytics

import com.bossrbx.rbxcalculator.adsmodule.UserType

object AnalyticsManager {

    private val providers: List<AnalyticsProvider> = listOf(
        FirebaseAnalyticsProvider(),
        TikTokAnalyticsProvider(),
        MetaAnalyticsProvider(),
    )

    private fun emit(block: AnalyticsProvider.() -> Unit) =
        providers.forEach { it.block() }

    fun openHomeScreen() = emit { openHomeScreen() }
    fun userType(userType: UserType, referrer: String) = emit { userType(userType, referrer) }

    fun hasClick_ORGtoPAID(referrer: String, irClickTime: String) = emit { hasClick_ORGtoPAID(referrer, irClickTime) }

}