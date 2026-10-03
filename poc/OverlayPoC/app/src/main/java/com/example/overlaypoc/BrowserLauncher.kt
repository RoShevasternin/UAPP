package com.example.overlaypoc

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/** Link opened when the user taps the floating card. */
const val TARGET_URL = "https://go.joystix.games/"

/**
 * Opens [url] in a Custom Tab.
 *
 * FLAG_ACTIVITY_NEW_TASK is required because the caller is a Service with no task of
 * its own. Starting an activity from the background is normally blocked since API 29,
 * but holding SYSTEM_ALERT_WINDOW exempts the app from that restriction — which is
 * precisely what makes a tap on the overlay work from anywhere.
 */
fun Context.openInCustomTab(url: String = TARGET_URL) {
    val uri = url.toUri()
    val provider = CustomTabsClient.getPackageName(this, null)
    try {
        if (provider != null) {
            val tab = CustomTabsIntent.Builder().setShowTitle(true).build()
            tab.intent.setPackage(provider)
            tab.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            tab.launchUrl(this, uri)
        } else {
            Log.i(TAG, "No Custom Tabs browser, falling back to ACTION_VIEW")
            startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    } catch (e: ActivityNotFoundException) {
        Log.w(TAG, "No app can open $url", e)
        Toast.makeText(this, R.string.no_browser, Toast.LENGTH_SHORT).show()
    }
}

private const val TAG = "BrowserLauncher"
