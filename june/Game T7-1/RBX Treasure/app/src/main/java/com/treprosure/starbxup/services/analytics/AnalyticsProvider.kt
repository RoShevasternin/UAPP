package com.treprosure.starbxup.services.analytics

import com.treprosure.starbxup.adsmodule.UserType

interface AnalyticsProvider {
    fun openHomeScreen()
    fun userType(userType: UserType, referrer: String)
    fun hasClick_ORGtoPAID(referrer: String, irClickTime: String)
}