package com.diam.ondbit.services.analytics

import com.diam.ondbit.adsmodule.UserType

interface AnalyticsProvider {
    fun openHomeScreen()
    fun userType(userType: UserType, referrer: String)
    fun hasClick_ORGtoPAID(referrer: String, irClickTime: String)
}