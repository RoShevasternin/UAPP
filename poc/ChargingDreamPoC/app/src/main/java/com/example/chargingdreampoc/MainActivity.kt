package com.example.chargingdreampoc

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * Single-screen entry point.
 *
 * First launch: asks for the one permission the feature needs — notifications
 * (API 33+). Once that is answered, the content card is revealed and the charger
 * monitor is started, so that plugging in later (even with the app closed) posts
 * a notification. Tapping the card opens the link in a Custom Tab.
 *
 * The only runtime prompt the user ever sees is the notification one. The
 * FOREGROUND_SERVICE permissions in the manifest are install-time ("normal")
 * permissions and never show a dialog.
 */
class MainActivity : AppCompatActivity() {

    private val requestNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            onPermissionResolved()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.homeCard).setOnClickListener { openInCustomTab() }

        if (notificationsGranted()) {
            onPermissionResolved()
        } else {
            requestNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun notificationsGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** Called once the user has answered (or no prompt was needed). */
    private fun onPermissionResolved() {
        // Arm the charger monitor so a later connect shows a notification.
        ChargingMonitorService.start(this)
        revealCard()
    }

    /** Pops the content card in once the permission step is done. */
    private fun revealCard() {
        val card = findViewById<View>(R.id.homeCard)
        if (card.visibility == View.VISIBLE) return
        card.visibility = View.VISIBLE
        card.alpha = 0f
        card.scaleX = 0.9f
        card.scaleY = 0.9f
        card.translationY = resources.displayMetrics.density * 24f
        card.animate()
            .alpha(1f).scaleX(1f).scaleY(1f).translationY(0f)
            .setDuration(400L)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
    }
}
