package com.rbuxdrop.cougame.adsmodule

import android.content.Context
import android.content.Intent
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.rbuxdrop.cougame.services.analytics.AnalyticsManager
import com.rbuxdrop.cougame.util.log
import com.tiktok.TikTokBusinessSdk

// Визначає тип юзера через Install Referrer
//
// Розпізнає конкретну рекламну мережу за міткою:
//   gclid  → PAID_GOOGLE
//   ttclid → PAID_TIKTOK
//   fbclid → PAID_FACEBOOK
//   немає мітки → ORGANIC

object UserDetector {

    // ── Install Referrer ──────────────────────────────────────────────────────

    fun detectViaReferrer(context: Context, onResult: (UserType) -> Unit) {
        val client = InstallReferrerClient.newBuilder(context).build()

        client.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                val userType = when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        val referrer = runCatching { client.installReferrer.installReferrer }.getOrDefault("")
                        log("referrer = $referrer")
                        var userType = detectFromString(referrer)

                        // ------------- TEST -------------
                        val hasClick = client.installReferrer.referrerClickTimestampServerSeconds > 0 ||
                                       client.installReferrer.referrerClickTimestampSeconds       > 0

                        if (userType == UserType.ORGANIC && hasClick) {
                            AnalyticsManager.hasClickToPAID(referrer)
                            userType = UserType.PAID
                        }
                        // ------------- TEST -------------

                        AnalyticsManager.userType(userType, referrer)
                        userType
                    }
                    else -> UserType.ORGANIC
                }

                client.endConnection()
                onResult(userType)
            }

            override fun onInstallReferrerServiceDisconnected() {
                onResult(UserType.ORGANIC)
            }
        })
    }

    // ── Deep Link (резервний спосіб) ────────────────────────────────────────────

    fun detectViaIntent(intent: Intent?): UserType {
        val uri = intent?.data ?: return UserType.ORGANIC
        return detectFromString(uri.toString())
    }

    // ── Розпізнавання мережі за рядком ──────────────────────────────────────────
    // Порядок важливий — повертаємо першу знайдену мітку

    private fun detectFromString(raw: String): UserType = when {
        raw.contains("gclid", true)  -> UserType.PAID_GOOGLE
        raw.contains("ttclid", true) -> UserType.PAID_TIKTOK
        raw.contains("fbclid", true) -> UserType.PAID_FACEBOOK

        raw.isBlank() || raw.contains("organic", true) -> UserType.ORGANIC

        else -> UserType.PAID
    }
}