package com.coinsclub.funrbx

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.gson.Gson
import com.coinsclub.funrbx.adsmodule.AdConfig
import com.coinsclub.funrbx.adsmodule.AdManager
import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.adsmodule.AppOpenManager
import com.coinsclub.funrbx.adsmodule.RemoteConfigModel
import com.coinsclub.funrbx.adsmodule.UserDetector
import com.coinsclub.funrbx.databinding.ActivityMainBinding
import com.coinsclub.funrbx.game.utils.LINK_JSON
import com.coinsclub.funrbx.game.utils.runGDX
import com.coinsclub.funrbx.services.tiktok.TikTokManager
import com.coinsclub.funrbx.util.OneTime
import com.coinsclub.funrbx.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity {

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize()
    }

    private fun initialize() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // ------------------------------------------------------------------------
    // Business Logic
    // ------------------------------------------------------------------------

    private fun fetchRemoteConfig(onComplete: (success: Boolean) -> Unit) {
        // Простий HTTP запит замість Firebase
        Thread {
            runCatching {
                val url = java.net.URL(LINK_JSON)
                val connection = url.openConnection() as java.net.HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout    = 5000
                connection.requestMethod  = "GET"

                val json = connection.inputStream.bufferedReader().readText()
                connection.disconnect()

                val model = Gson().fromJson(json, RemoteConfigModel::class.java)
                AdConfig.remoteConfig = model
                App.adPref.saveConfig(model)

                //log("RAW JSON = $json")
                log("MODEL = $model")
                log("Config applied. UserType=${AdConfig.userType}")

                runOnUiThread {
                    initTikTok(model) // TikTok  TikTok  TikTok  TikTok  TikTok
                    onComplete(true)
                }
            }.onFailure {
                log("Failed to fetch config: $it")
                runOnUiThread { onComplete(false) }
            }
        }.start()
    }

    // ------------------------------------------------------------------------
    // Services
    // ------------------------------------------------------------------------

    private fun initTikTok(model: RemoteConfigModel) {
        val tiktok = model.tiktok
        if (tiktok == null || !tiktok.isValid) {
            log("TikTok config missing/invalid — skip init")
            return
        }
        TikTokManager.initialize(application, tiktok.appIds, tiktok.secret!!)
    }

}