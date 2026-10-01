package com.bossrbx.rbxcalculator.services.meta

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import com.bossrbx.rbxcalculator.BuildConfig
import com.bossrbx.rbxcalculator.adsmodule.MetaConfig
import com.bossrbx.rbxcalculator.util.OneTime
import com.bossrbx.rbxcalculator.util.log

// Meta SDK — лише App Events. App ID і Client Token приходять із нашого сервера
// (блок "meta" конфігу), тому в маніфесті їх немає: автостарт Meta на початку процесу
// тихо пропускається, а справжній старт — тут, щойно прийшов конфіг.
object MetaManager {

    private val once = OneTime()

    // true після успішного старту. До нього AppEventsLogger створювати не можна — кине виняток
    @Volatile
    var isReady = false
        private set

    // Ключі з сервера, а якщо їх немає — тестові з local.properties (лише debug-збірка)
    fun resolve(server: MetaConfig?): MetaConfig? =
        server?.takeIf { it.isValid }
            ?: MetaConfig(BuildConfig.META_TEST_APP_ID, BuildConfig.META_TEST_CLIENT_TOKEN)
                .takeIf { it.isValid }
                ?.also { log("Meta: тестові ключі з local.properties") }

    fun initialize(app: Application, appId: String, clientToken: String) {
        once.use {
            runCatching {
                FacebookSdk.setApplicationId(appId)
                FacebookSdk.setClientToken(clientToken)
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
                }
                @Suppress("DEPRECATION")
                FacebookSdk.sdkInitialize(app)
                FacebookSdk.setAutoInitEnabled(true)
                FacebookSdk.fullyInitialize()

                // встановлення й запуски — на них тримається атрибуція реклами Meta
                AppEventsLogger.activateApp(app)

                isReady = true
                log("Meta SDK initialized SUCCESSFULLY, appId=$appId")
            }.onFailure {
                log("Meta SDK FAILED: $it")
            }
        }
    }
}
