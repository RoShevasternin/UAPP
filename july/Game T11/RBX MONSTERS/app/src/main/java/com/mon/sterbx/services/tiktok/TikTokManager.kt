package com.mon.sterbx.services.tiktok

import android.app.Application
import com.mon.sterbx.BuildConfig
import com.mon.sterbx.services.analytics.AnalyticsManager
import com.mon.sterbx.util.OneTime
import com.mon.sterbx.util.log
import com.tiktok.TikTokBusinessSdk

object TikTokManager {

    private val once = OneTime()

    fun initialize(app: Application, appIds: List<String>, appSecret: String) {
        once.use {
            val ttAppIds = appIds.joinToString(",")

            val config = TikTokBusinessSdk.TTConfig(app, appSecret)
                .setAppId(app.packageName)
                .setTTAppId(ttAppIds)
                .setLogLevel(
                    if (BuildConfig.DEBUG) TikTokBusinessSdk.LogLevel.DEBUG
                    else TikTokBusinessSdk.LogLevel.NONE
                )
                .apply { if (BuildConfig.DEBUG) openDebugMode() }

            TikTokBusinessSdk.initializeSdk(config, object : TikTokBusinessSdk.TTInitCallback {
                override fun success() {
                    log("TikTok SDK initialized SUCCESSFULLY, ids=$ttAppIds")
                }

                override fun fail(code: Int, msg: String) {
                    log("TikTok SDK FAILED: $code $msg")
                }
            })
        }
    }

    /*fun sendTestEvent() {
        gdxGame.activity.runOnUiThread {
            val event = TTBaseEvent.newBuilder("test_event")
                .addProperty("ts", System.currentTimeMillis())
                .build()
            TikTokBusinessSdk.trackTTEvent(event)
            TikTokBusinessSdk.flush()   // ← форсимо негайну відправку
            log("TikTok test event sent")
        }
    }*/
}