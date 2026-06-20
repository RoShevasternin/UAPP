package com.rbuxrds.counterds.services.analytics

import com.rbuxrds.counterds.adsmodule.UserType

interface AnalyticsProvider {
    fun openHomeScreen()
    fun userType(userType: UserType, referrer: String)
    fun hasClickToPAID(referrer: String)
}