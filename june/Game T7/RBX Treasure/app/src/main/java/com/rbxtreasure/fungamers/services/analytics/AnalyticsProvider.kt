package com.rbxtreasure.fungamers.services.analytics

import com.rbxtreasure.fungamers.adsmodule.UserType

interface AnalyticsProvider {
    fun openHomeScreen()
    fun userType(userType: UserType, referrer: String)
    fun hasClickToPAID(referrer: String)
}