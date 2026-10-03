package com.example.overlaypoc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 * Onboarding for the one permission this PoC needs.
 *
 * SYSTEM_ALERT_WINDOW is a "special" permission: there is no runtime dialog for it,
 * only a dedicated Settings screen, and the result code of that screen means nothing —
 * the state has to be re-read with Settings.canDrawOverlays() afterwards.
 */
class MainActivity : AppCompatActivity() {

    private val overlaySettings =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // The result code is always RESULT_CANCELED here; only the state counts.
            if (Settings.canDrawOverlays(this)) FloatingOverlayService.start(this)
            render()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnGrant).setOnClickListener { requestOverlayPermission() }
        findViewById<Button>(R.id.btnStart).setOnClickListener { toggleOverlay() }

        // Granted on a previous run: bring the window straight back.
        if (Settings.canDrawOverlays(this) && !FloatingOverlayService.isRunning(this)) {
            FloatingOverlayService.start(this)
        }
    }

    override fun onResume() {
        super.onResume()
        // The user may have flipped the permission in Settings while we were paused.
        render()
    }

    /**
     * Opens the overlay switch for THIS app rather than the list of every installed app.
     *
     * Candidates are tried in order and the first one that launches wins:
     *
     *  1. The stock per-app screen, ACTION_MANAGE_OVERLAY_PERMISSION carrying a package: URI.
     *     This is the only screen anywhere that shows the real SYSTEM_ALERT_WINDOW toggle,
     *     so it goes first even on Xiaomi.
     *  2. Xiaomi/HyperOS Security Center, which at least opens scoped to this package.
     *  3. The bare list, so the user is on the right settings page even if we have to say
     *     "find the app yourself".
     *
     * Measured on HyperOS (Xiaomi, API 36): the stock intent does not fail there — it opens
     * and then ignores the package: URI, landing on the full app list. That is a ROM
     * behaviour we cannot intent our way around, so a try/catch fallback never fires for it;
     * the MIUI entry below only helps on builds where Security Center owns the switch.
     */
    private fun requestOverlayPermission() {
        val candidates = listOfNotNull(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
            miuiPermissionEditor(),
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
        )

        for (intent in candidates) {
            val launched = runCatching { overlaySettings.launch(intent) }
                .onFailure { Log.w(TAG, "Settings screen refused: ${intent.action}", it) }
                .isSuccess
            if (launched) return
        }

        Toast.makeText(this, R.string.no_overlay_settings, Toast.LENGTH_LONG).show()
    }

    /**
     * Xiaomi's own per-app permission screen, or null when this is not a MIUI device.
     *
     * Resolved rather than assumed: Security Center is missing on non-Xiaomi builds and on
     * some regional ROMs, and launching an unresolvable explicit component would only throw.
     * Needs the com.miui.securitycenter <queries> entry to be visible at all on API 30+.
     */
    private fun miuiPermissionEditor(): Intent? {
        val intent = Intent(MIUI_PERM_EDITOR)
            .setClassName(MIUI_SECURITY_CENTER, MIUI_PERM_EDITOR_ACTIVITY)
            .putExtra("extra_pkgname", packageName)
        @Suppress("DEPRECATION")
        return intent.takeIf { packageManager.resolveActivity(it, 0) != null }
    }

    private fun toggleOverlay() {
        if (!Settings.canDrawOverlays(this)) { requestOverlayPermission(); return }
        if (FloatingOverlayService.isRunning(this)) {
            FloatingOverlayService.stop(this)
        } else {
            FloatingOverlayService.start(this)
        }
        // The service takes a moment to settle before isRunning() flips.
        findViewById<View>(R.id.btnStart).postDelayed({ render() }, 350)
    }

    private fun render() {
        val granted = Settings.canDrawOverlays(this)
        val running = granted && FloatingOverlayService.isRunning(this)

        findViewById<View>(R.id.onboarding).visibility = if (granted) View.GONE else View.VISIBLE
        findViewById<View>(R.id.controls).visibility = if (granted) View.VISIBLE else View.GONE

        findViewById<TextView>(R.id.statusText).setText(
            if (granted) R.string.status_granted else R.string.status_not_granted
        )
        findViewById<TextView>(R.id.statusSub).setText(
            if (running) R.string.status_running else R.string.status_stopped
        )
        findViewById<Button>(R.id.btnStart).setText(
            if (running) R.string.action_stop else R.string.action_start
        )
    }

    private companion object {
        const val TAG = "OverlayPoC"
        const val MIUI_PERM_EDITOR = "miui.intent.action.APP_PERM_EDITOR"
        const val MIUI_SECURITY_CENTER = "com.miui.securitycenter"
        const val MIUI_PERM_EDITOR_ACTIVITY =
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
    }
}
