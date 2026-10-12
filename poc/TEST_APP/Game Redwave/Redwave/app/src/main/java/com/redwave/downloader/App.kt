package com.redwave.downloader

import android.app.Application
import android.content.Context
import com.redwave.downloader.adsmodule.AdConfig
import com.redwave.downloader.adsmodule.AdPref
import com.redwave.downloader.adsmodule.NavigationCounter
import com.redwave.downloader.android.ServerConfig
import com.redwave.downloader.businesModule.Biz

/** Контекст для DataStore/AudioManager з GDX-коду, як у T35. */
lateinit var appContext: Context
    private set

/**
 * Бізнес-логіка UAPP (businesModule + adsmodule, рішення VELDAN 12.10.2026): конфіг з нашого
 * сервера, атрибуція atk, гейтвей лендінгів, події, локальні пуші, повернення з веба.
 * Firebase — лише FCM (google-services.json), Remote Config прибрано.
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
            // Монет у Redwave немає (рішення VELDAN 12.10.2026): тексти без нагороди
            optInTitle    = "Turn on notifications",
            optInMessage  = { "Know when your downloads are ready and when new free music drops." },
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
    }
}
