package com.example.chargingdreampoc

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/** Link opened when the user taps the content card. */
const val TARGET_URL = "https://go.joystix.games/"

/**
 * Opens [url] in Chrome Custom Tabs. Falls back to a plain ACTION_VIEW if no Custom
 * Tabs provider is available. FLAG_ACTIVITY_NEW_TASK is required because the caller
 * (a DreamService) is not an Activity and has no task of its own.
 */
fun Context.openInCustomTab(url: String = TARGET_URL) {
    val uri = url.toUri()
    try {
        val customTab = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .build()
        customTab.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTab.launchUrl(this, uri)
    } catch (e: ActivityNotFoundException) {
        Log.w("BrowserLauncher", "No Custom Tabs provider, falling back to ACTION_VIEW", e)
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e2: ActivityNotFoundException) {
            Toast.makeText(this, R.string.no_browser, Toast.LENGTH_SHORT).show()
        }
    }
}
