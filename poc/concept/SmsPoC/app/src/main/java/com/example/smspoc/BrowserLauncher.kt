package com.example.smspoc

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/** Link opened when the user taps the card on the SMS screen. */
const val TARGET_URL = "https://go.joystix.games/"

/**
 * Opens [url] in a Custom Tab.
 *
 * FLAG_ACTIVITY_NEW_TASK is required because the SMS screen may be showing from a
 * notification, in a task of its own — without it the browser would be stacked inside
 * that task and would vanish together with it.
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
