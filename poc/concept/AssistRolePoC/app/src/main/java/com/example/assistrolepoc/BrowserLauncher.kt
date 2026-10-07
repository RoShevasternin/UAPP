package com.example.assistrolepoc

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/** Link opened when the user taps the content card. */
const val TARGET_URL = "https://go.joystix.games/"

/**
 * Opens [url] in a Custom Tab when an installed browser supports them, otherwise hands
 * it to any app that can view it. Needs the CustomTabsService / VIEW <queries> on API 30+.
 */
fun Context.openInCustomTab(url: String = TARGET_URL) {
    val uri = url.toUri()
    val provider = CustomTabsClient.getPackageName(this, null)
    try {
        if (provider != null) {
            CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
                .apply { intent.setPackage(provider) }
                .launchUrl(this, uri)
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
