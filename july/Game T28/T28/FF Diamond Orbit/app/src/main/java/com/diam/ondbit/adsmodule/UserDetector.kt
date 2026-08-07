package com.diam.ondbit.adsmodule

import android.content.Context
import android.content.Intent
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.diam.ondbit.services.analytics.AnalyticsManager
import com.diam.ondbit.util.log
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
                        val timeClickServer = client.installReferrer.referrerClickTimestampServerSeconds
                        val timeClickUser   = client.installReferrer.referrerClickTimestampSeconds

                        val hasClick: Boolean = (timeClickServer > 0 || timeClickUser > 0)

                        if (userType == UserType.ORGANIC && hasClick) {
                            val irClickTime = "server: $timeClickServer | user: $timeClickUser"
                            AnalyticsManager.hasClick_ORGtoPAID(referrer, irClickTime)
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