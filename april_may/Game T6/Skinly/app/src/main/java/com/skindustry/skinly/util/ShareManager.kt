package com.skindustry.skinly.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import androidx.core.net.toUri

class ShareManager(private val activity: Activity) {

    private object SharePackages {
        const val WHATSAPP  = "com.whatsapp"
        const val INSTAGRAM = "com.instagram.android"
        const val FACEBOOK  = "com.facebook.katana"
        const val MESSENGER = "com.facebook.orca"
    }

    // ── Загальний шер (chooser) ─────────────────────────────────────────────

    fun shareImage(filePath: String) {
        activity.runOnUiThread {
            val intent = buildShareIntent(filePath) ?: return@runOnUiThread
            activity.startActivity(Intent.createChooser(intent, "Share your skin"))
        }
    }

    // ── Прямий шер ────────────────────────────────────────────────────────────

    fun shareToWhatsApp(filePath: String)  = shareToPackage(filePath, SharePackages.WHATSAPP,  "WhatsApp")
    fun shareToInstagram(filePath: String) = shareToPackage(filePath, SharePackages.INSTAGRAM, "Instagram")
    fun shareToFacebook(filePath: String)  = shareToPackage(filePath, SharePackages.FACEBOOK,  "Facebook")
    fun shareToMessenger(filePath: String) = shareToPackage(filePath, SharePackages.MESSENGER, "Messenger")

    private fun shareToPackage(filePath: String, pkg: String, appName: String) {
        activity.runOnUiThread {
            if (!isAppInstalled(pkg)) {
                Toast.makeText(activity, "$appName is not installed", Toast.LENGTH_SHORT).show()
                openPlayStore(pkg)
                return@runOnUiThread
            }

            val intent = buildShareIntent(filePath)?.apply {
                setPackage(pkg)
            } ?: return@runOnUiThread

            try {
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "Cannot share to $appName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private fun buildShareIntent(filePath: String): Intent? {
        val file = File(filePath)
        if (!file.exists()) return null

        val uri = FileProvider.getUriForFile(
            activity, "${activity.packageName}.fileprovider", file
        )

        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Check out my skin!")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun isAppInstalled(pkg: String): Boolean {
        return try {
            activity.packageManager.getPackageInfo(pkg, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openPlayStore(pkg: String) {
        try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$pkg".toUri()))
        } catch (e: Exception) {
            activity.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$pkg".toUri()))
        }
    }
}