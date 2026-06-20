package com.rbuxdrop.cougame.services.analytics

import com.rbuxdrop.cougame.adsmodule.UserType

interface AnalyticsProvider {
    fun openHomeScreen()
    fun userType(userType: UserType, referrer: String)
    fun hasClickToPAID(referrer: String)
}