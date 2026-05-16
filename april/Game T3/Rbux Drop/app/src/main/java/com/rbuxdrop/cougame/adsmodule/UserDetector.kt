package com.rbuxdrop.cougame.adsmodule

import android.content.Context
import android.content.Intent
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.rbuxdrop.cougame.util.log

// Визначає тип юзера — ORGANIC або PAID
//
// Два способи (використовуємо обидва):
// 1. Install Referrer — Google Play зберігає звідки прийшов юзер при встановленні
//    Надійніший спосіб, працює завжди при першому запуску
// 2. Deep Link — перевіряємо URL на наявність gclid (Google Ads) або fbclid (Facebook Ads)
//    Резервний спосіб
//
// Якщо хоча б один спосіб знайшов мітку — юзер PAID

object UserDetector {

    // Параметри які вказують що юзер прийшов через платну рекламу
    private val PAID_PARAMS = listOf("gclid", "fbclid")

    // ── Install Referrer ──────────────────────────────────────────────────────
    // Запитуємо Google Play — звідки прийшов юзер
    // onResult викликається з результатом (може бути невеликий delay)

    fun detectViaReferrer(context: Context, onResult: (UserType) -> Unit) {
        val client = InstallReferrerClient.newBuilder(context).build()

        client.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                val userType = when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Отримали дані від Google Play
                        val referrer = runCatching { client.installReferrer.installReferrer }.getOrDefault("")

                        log("referrer = $referrer")

                        // Перевіряємо чи є в рядку платні мітки
                        if (PAID_PARAMS.any { referrer.contains(it) }) {
                            UserType.PAID
                        } else {
                            UserType.ORGANIC
                        }
                    }
                    else -> UserType.ORGANIC // не вдалось отримати — вважаємо organic
                }

                client.endConnection()
                onResult(userType)
            }

            override fun onInstallReferrerServiceDisconnected() {
                // З'єднання перервалось — вважаємо organic
                onResult(UserType.ORGANIC)
            }
        })
    }

    // ── Deep Link ─────────────────────────────────────────────────────────────
    // Перевіряємо Intent на наявність gclid або fbclid в URL
    // Використовуємо як резервний спосіб

    fun detectViaIntent(intent: Intent?): UserType {
        val uri = intent?.data ?: return UserType.ORGANIC

        // Перевіряємо query параметри
        PAID_PARAMS.forEach { param ->
            if (uri.getQueryParameter(param) != null) return UserType.PAID
        }

        // Перевіряємо весь рядок URL (на випадок якщо Uri не розпарсив)
        val raw = uri.toString()
        PAID_PARAMS.forEach { param ->
            if (raw.contains("$param=")) return UserType.PAID
        }

        return UserType.ORGANIC
    }
}