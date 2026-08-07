package com.diam.ondbit.services.tiktok

import android.app.Application
import com.diam.ondbit.BuildConfig
import com.diam.ondbit.util.log
import com.tiktok.TikTokBusinessSdk
import java.util.concurrent.atomic.AtomicBoolean

object TikTokManager {

    private val once = AtomicBoolean(true)

    fun initialize(app: Application, appIds: List<String>, appSecret: String) {
        if (once.getAndSet(false)) {
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