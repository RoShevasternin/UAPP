package com.skindustry.skinly.services.analytics

import com.skindustry.skinly.adsmodule.UserType
import com.tiktok.TikTokBusinessSdk
import com.tiktok.appevents.base.EventName

class TikTokAnalyticsProvider : AnalyticsProvider {

    override fun openHomeScreen() = track(EventName.UNLOCK_ACHIEVEMENT)
    override fun userType(userType: UserType, referrer: String) {}

    override fun hasClickToPAID(referrer: String) {}

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------
    private fun track(event: EventName) = TikTokBusinessSdk.trackTTEvent(event)
}