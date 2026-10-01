package com.bossrbx.rbxcalculator.services.analytics

import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.bossrbx.rbxcalculator.adsmodule.UserType
import com.bossrbx.rbxcalculator.services.meta.MetaManager

// Дзеркало TikTokAnalyticsProvider: ті самі місця в грі → стандартні події Meta.
// Поки SDK не стартував (немає блоку "meta" в конфігу) — подія мовчки пропускається.
class MetaAnalyticsProvider : AnalyticsProvider {

    // Створюється при першій події, коли SDK вже працює
    private val logger by lazy { AppEventsLogger.newLogger(FacebookSdk.getApplicationContext()) }

    override fun openHomeScreen() = track(AppEventsConstants.EVENT_NAME_UNLOCKED_ACHIEVEMENT)
    override fun userType(userType: UserType, referrer: String) {}

    override fun hasClick_ORGtoPAID(referrer: String, irClickTime: String) {}

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------
    private fun track(event: String) {
        if (MetaManager.isReady) logger.logEvent(event)
    }
}
