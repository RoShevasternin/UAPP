package com.mon.sterbx.services.analytics

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.mon.sterbx.adsmodule.UserType

class FirebaseAnalyticsProvider : AnalyticsProvider {

    private val fa = Firebase.analytics

    private object Event {
        const val UT_ORGANIC = "UT_ORGANIC"
        const val UT_PAID    = "UT_PAID"
        const val UT_GOOGLE  = "UT_GOOGLE"
        const val UT_TIKTOK  = "UT_TIKTOK"
        const val UT_META    = "UT_META"

        const val OPEN_HOME_SCREEN     = "OPEN_HOME_SCREEN"
        const val IR_HAS_CLICK_TO_PAID = "IR_HAS_CLICK_TO_PAID"
    }

    private object Param {
        const val REFERRER      = "referrer"
        const val IR_CLICK_TIME = "ir_click_time"
    }

    override fun openHomeScreen() {
        fa.logEvent(Event.OPEN_HOME_SCREEN, null)
    }

    override fun userType(userType: UserType, referrer: String) {
        val event = when (userType) {
            UserType.ORGANIC       -> Event.UT_ORGANIC
            UserType.PAID          -> Event.UT_PAID
            UserType.PAID_GOOGLE   -> Event.UT_GOOGLE
            UserType.PAID_TIKTOK   -> Event.UT_TIKTOK
            UserType.PAID_FACEBOOK -> Event.UT_META
        }
        fa.logEvent(event, bundle { putString(Param.REFERRER, referrer) })
    }

    override fun hasClick_ORGtoPAID(referrer: String, irClickTime: String) {
        fa.logEvent(Event.IR_HAS_CLICK_TO_PAID, bundle {
            putString(Param.REFERRER, referrer)
            putString(Param.IR_CLICK_TIME, irClickTime)
        })
    }

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------
    private fun bundle(block: Bundle.() -> Unit) = Bundle().apply(block)
}