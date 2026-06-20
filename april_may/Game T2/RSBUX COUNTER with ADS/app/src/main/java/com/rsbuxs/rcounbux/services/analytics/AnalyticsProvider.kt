package com.rsbuxs.rcounbux.services.analytics

import com.rsbuxs.rcounbux.adsmodule.UserType

interface AnalyticsProvider {
    fun openHomeScreen()
    fun userType(userType: UserType, referrer: String)
    fun hasClickToPAID(referrer: String)
}