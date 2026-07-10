package com.rbxrush.rushrbx.services.analytics

import com.rbxrush.rushrbx.adsmodule.UserType

interface AnalyticsProvider {
    fun openHomeScreen()
    fun userType(userType: UserType, referrer: String)
    fun hasClick_ORGtoPAID(referrer: String, irClickTime: String)
}