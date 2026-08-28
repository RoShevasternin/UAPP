package com.rbxhubpro.rohumex.adsmodule

import android.content.Context
import android.content.Intent
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.rbxhubpro.rohumex.services.analytics.AnalyticsManager
import com.rbxhubpro.rohumex.util.log
import com.tiktok.TikTokBusinessSdk

// Визначає тип юзера через Install Referrer
//
// Розпізнає конкретну рекламну мережу за міткою:
//   gclid  → PAID_GOOGLE
//   ttclid → PAID_TIKTOK
//   fbclid → PAID_FACEBOOK
//   немає мітки → ORGANIC
//
// ═══ ПРАВКА 2 + СУДЬБА ЭТОГО КЛАССА ═══════════════════════════════════════
// Изменение: колбэк теперь отдаёт наружу и СЫРУЮ строку referrer — она уходит
// на наш сервер (Backend.fetchConfig). Не парсить и не вырезать метки: разбор
// на сервере, там JOIN с клик-вебхуком TikTok. Именно потеря этой строки дала
// 96–98% установок (direct)/(none) и невозможность понять, чей трафик.
//
// ВЫКИДЫВАТЬ ЛИ КЛАСС ЦЕЛИКОМ — пока НЕТ, и вот почему: выбор рекламного
// профиля (organic/gclid/…) сегодня исполняет клиент, и это единственный
// механизм, который уводит gclid-трафик на отдельный google-safe домен
// (политика Google). Удалить сейчас = все стали ORGANIC = разводка по
// источникам сломана. Класс ЗАМОРОЖЕН: не расширять и не улучшать. Сервер
// уже получает referrer и со временем начнёт отдавать все профили конфига
// ОДИНАКОВЫМИ (разрешёнными per-установка) — с этого момента выбор на клиенте
// станет холостым, и класс выпиливается любым будущим релизом без
// координации с сервером.
// ═══════════════════════════════════════════════════════════════════════════

object UserDetector {

    // ── Install Referrer ──────────────────────────────────────────────────────

    // rawReferrer — строка как есть от Play ("" если недоступна): пробросить в
    // Backend.fetchConfig, больше с ней на клиенте не делать НИЧЕГО.
    fun detectViaReferrer(context: Context, onResult: (UserType, rawReferrer: String) -> Unit) {
        val client = InstallReferrerClient.newBuilder(context).build()

        client.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                var rawReferrer = ""
                val userType = when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        val referrer = runCatching { client.installReferrer.installReferrer }.getOrDefault("")
                        rawReferrer = referrer
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
                onResult(userType, rawReferrer)
            }

            override fun onInstallReferrerServiceDisconnected() {
                onResult(UserType.ORGANIC, "")
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