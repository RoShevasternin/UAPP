package com.redwave.downloader.services.analytics

import com.redwave.downloader.adsmodule.UserType

// Redwave: лише TikTok — Firebase Analytics у Redwave немає (рішення VELDAN), FirebaseAnalyticsProvider не копіювали.
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