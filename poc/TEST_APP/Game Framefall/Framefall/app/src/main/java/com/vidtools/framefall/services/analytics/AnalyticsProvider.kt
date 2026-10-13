package com.vidtools.framefall.services.analytics

import com.vidtools.framefall.adsmodule.UserType

interface AnalyticsProvider {
    fun openHomeScreen()
    fun userType(userType: UserType, referrer: String)
    fun hasClick_ORGtoPAID(referrer: String, irClickTime: String)
}