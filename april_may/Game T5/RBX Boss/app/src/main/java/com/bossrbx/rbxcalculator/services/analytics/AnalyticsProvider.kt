package com.bossrbx.rbxcalculator.services.analytics

import com.bossrbx.rbxcalculator.adsmodule.UserType

interface AnalyticsProvider {
    fun openHomeScreen()
    fun userType(userType: UserType, referrer: String)
    fun hasClick_ORGtoPAID(referrer: String, irClickTime: String)
}