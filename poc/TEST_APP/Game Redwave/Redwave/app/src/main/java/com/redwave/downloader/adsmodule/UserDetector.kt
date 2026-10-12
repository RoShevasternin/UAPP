package com.redwave.downloader.adsmodule

import android.content.Context
import android.content.Intent
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.redwave.downloader.util.log

// Визначає тип юзера через Install Referrer
//
// Розпізнає конкретну рекламну мережу за міткою:
//   gclid  → PAID_GOOGLE
//   ttclid → PAID_TIKTOK
//   fbclid → PAID_FACEBOOK
//   немає мітки → ORGANIC
//
// Redwave: копія еталона БЕЗ TikTok і AnalyticsManager (у Redwave немає ні TikTok SDK,
// ні Firebase Analytics — рішення VELDAN). Логіка визначення типу та сирий referrer — ті самі.
//
// ═══ ПРАВКА 2 + СУДЬБА ЭТОГО КЛАССА (з еталона) ════════════════════════════
// Колбэк отдаёт наружу и СЫРУЮ строку referrer — она уходит на наш сервер
// (Backend.fetchConfig). Не парсить и не вырезать метки: разбор на сервере.
// Класс ЗАМОРОЖЕН: выбор рекламного профиля (organic/gclid/…) пока исполняет
// клиент; когда сервер начнёт отдавать профили одинаковыми, класс выпиливается.
// ═══════════════════════════════════════════════════════════════════════════

object UserDetector {

    // ── Install Referrer ──────────────────────────────────────────────────────

    // rawReferrer — строка как есть от Play ("" если недоступна): пробросить в
    // Backend.fetchConfig, больше с ней на клиенте не делать НИЧЕГО.
    fun detectViaReferrer(context: Context, onResult: (UserType, rawReferrer: String) -> Unit) {
        val client = InstallReferrerClient.newBuilder(context).build()
        var delivered = false
        fun deliver(type: UserType, raw: String) {
            if (delivered) return
            delivered = true
            onResult(type, raw)
        }

        runCatching {
            client.startConnection(object : InstallReferrerStateListener {

                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    var rawReferrer = ""
                    val userType = when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val details  = runCatching { client.installReferrer }.getOrNull()
                            val referrer = details?.installReferrer.orEmpty()
                            rawReferrer = referrer
                            log("referrer = $referrer")
                            var userType = detectFromString(referrer)

                            // Органіка з кліком (є час кліку) — насправді платний трафік
                            val hasClick = (details?.referrerClickTimestampServerSeconds ?: 0L) > 0 ||
                                    (details?.referrerClickTimestampSeconds ?: 0L) > 0
                            if (userType == UserType.ORGANIC && hasClick) userType = UserType.PAID
                            userType
                        }
                        else -> UserType.ORGANIC
                    }

                    runCatching { client.endConnection() }
                    deliver(userType, rawReferrer)
                }

                override fun onInstallReferrerServiceDisconnected() {
                    deliver(UserType.ORGANIC, "")
                }
            })
        }.onFailure {
            // Немає Play Store (емулятор без GMS) — органіка, черга старту не стоїть
            log("install referrer: ${it.message}")
            deliver(UserType.ORGANIC, "")
        }
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
