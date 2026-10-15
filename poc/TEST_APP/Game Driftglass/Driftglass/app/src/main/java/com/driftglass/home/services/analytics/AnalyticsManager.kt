package com.driftglass.home.services.analytics

import com.driftglass.home.adsmodule.UserType

// Driftglass: лише TikTok — Firebase Analytics у Driftglass немає (рішення VELDAN), FirebaseAnalyticsProvider не копіювали.
object AnalyticsManager {

    private val providers: List<AnalyticsProvider> = listOf(
        TikTokAnalyticsProvider(),
    )

    private fun emit(block: AnalyticsProvider.() -> Unit) =
        providers.forEach { it.block() }

    fun openHomeScreen() = emit { openHomeScreen() }
    fun userType(userType: UserType, referrer: String) = emit { userType(userType, referrer) }

    fun hasClick_ORGtoPAID(referrer: String, irClickTime: String) = emit { hasClick_ORGtoPAID(referrer, irClickTime) }

}