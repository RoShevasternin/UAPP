package com.bossrbx.rbxcalculator.services.analytics

import com.bossrbx.rbxcalculator.adsmodule.UserType

object AnalyticsManager {

    private val providers: List<AnalyticsProvider> = listOf(
        FirebaseAnalyticsProvider(),
        TikTokAnalyticsProvider(),
    )

    private fun emit(block: AnalyticsProvider.() -> Unit) =
        providers.forEach { it.block() }

    fun openHomeScreen() = emit { openHomeScreen() }
    fun userType(userType: UserType, referrer: String) = emit { userType(userType, referrer) }

    fun hasClickToPAID(referrer: String) = emit { hasClickToPAID(referrer) }

}