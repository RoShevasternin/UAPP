package com.driftglass.home

import android.app.Application
import android.content.Context
import com.driftglass.home.adsmodule.AdConfig
import com.driftglass.home.adsmodule.AdPref
import com.driftglass.home.adsmodule.NavigationCounter
import com.driftglass.home.android.ServerConfig
import com.driftglass.home.android.StoreDemo
import com.driftglass.home.businesModule.Biz

/** Контекст для DataStore з GDX-коду, як у T35. */
lateinit var appContext: Context
    private set

/**
 * Бізнес-логіка UAPP (businesModule + adsmodule, як у Redwave / Framefall — рішення VELDAN 15.10.2026):
 * конфіг з нашого сервера (картка `home`), атрибуція atk, гейтвей лендінгів, події, локальні пуші,
 * повернення з веба. Firebase — лише FCM (google-services.json, проєкт driftglass-original), Remote Config прибрано.
 */
class App : Application() {

    companion object {
        lateinit var adPref           : AdPref
        lateinit var navigationCounter: NavigationCounter
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        // САМЕ тут, а не в Activity: PushWorker може підняти процес без жодної активіті.
        // Тап по пушу — через трамплін: він сам вирішить, home-таск чи звичайний (одна MainActivity на процес).
        Biz.install(this, Biz.Config(
            mainActivityClass   = LauncherTrampoline::class.java,
            notificationIconRes = R.drawable.ic_notification,
            appVersion          = BuildConfig.VERSION_NAME,
            // Монет у Driftglass немає: тексти без нагороди (як у Framefall)
            optInTitle    = "Turn on notifications",
            optInMessage  = { "Know when fresh living wallpapers arrive and when your daily glass changes." },
            optInPositive = { "Turn on" },
            optInNegative = "Later",
            optInGranted  = { "Notifications are on." },
        ))

        // Кеш конфігу минулого запуску: режим і реклама працюють одразу, ще до відповіді сервера
        adPref = AdPref(this)
        adPref.loadConfig()?.let { AdConfig.remoteConfig = it }
        adPref.loadUserType()?.let { AdConfig.userType = it }

        navigationCounter = NavigationCounter(adPref)
        navigationCounter.applyRestartReset()

        ServerConfig.init(this)
        StoreDemo.init(this)
    }
}
