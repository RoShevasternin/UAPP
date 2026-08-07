package com.diam.ondbit.services.analytics

import com.diam.ondbit.adsmodule.UserType
import com.tiktok.TikTokBusinessSdk
import com.tiktok.appevents.base.EventName

class TikTokAnalyticsProvider : AnalyticsProvider {

    override fun openHomeScreen() = track(EventName.UNLOCK_ACHIEVEMENT)
    override fun userType(userType: UserType, referrer: String) {}

    override fun hasClick_ORGtoPAID(referrer: String, irClickTime: String) {}

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------
    private fun track(event: EventName) = TikTokBusinessSdk.trackTTEvent(event)
}