package com.example.assistrolepoc

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService

/**
 * Digital-assistant PoC.
 *
 * The app declares an ACTION_ASSIST activity, which is what makes it eligible to be
 * picked as the system "Digital assistant app". Once picked, the assist gesture
 * (long-press Home, or the bottom-corner swipe on gesture navigation) delivers
 * ACTION_ASSIST straight to this activity.
 *
 * Two modes:
 *  - onboarding, while we are not the assistant — one button to become one;
 *  - content, once we are (or when we were launched by the gesture) — the card
 *    that opens the link in a Custom Tab.
 */
class MainActivity : AppCompatActivity() {

    private var assistCount = 0
    private var roleRequestStartedAt = 0L
    /** Set when this instance was started by the gesture, so content shows even mid-setup. */
    private var launchedByAssist = false

    /**
     * Result of the system role dialog. The role state decides, not the result code.
     *
     * ASSISTANT is normally NOT a requestable role: the system returns instantly
     * without ever drawing a dialog. We detect that by how fast we came back and
     * fall back to the Settings screen, which always works.
     */
    private val requestAssistRole =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val held = isDefaultAssistant()
            val elapsed = SystemClock.elapsedRealtime() - roleRequestStartedAt
            Log.i(TAG, "Role request returned: held=$held after ${elapsed}ms")
            if (!held && elapsed < DIALOG_SUPPRESSED_MS) {
                Log.i(TAG, "No dialog was shown — falling back to assistant settings")
                openAssistSettings()
            }
            render()
        }

    /** We cannot know what the user chose in Settings, so the state is re-read in onResume. */
    private val openSettings =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { render() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        assistCount = savedInstanceState?.getInt(KEY_ASSIST_COUNT) ?: 0
        roleRequestStartedAt = savedInstanceState?.getLong(KEY_ROLE_REQUEST_AT) ?: 0L

        findViewById<Button>(R.id.btnSetAssistant).setOnClickListener { requestAssistantRole() }
        findViewById<Button>(R.id.btnAssistSettings).setOnClickListener { openAssistSettings() }

        val openLink = View.OnClickListener { openInCustomTab() }
        findViewById<View>(R.id.cardImage).setOnClickListener(openLink)
        findViewById<View>(R.id.cardText).setOnClickListener(openLink)

        handleAssist(intent, fromNewIntent = false)
        render()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAssist(intent, fromNewIntent = true)
        render()
    }

    override fun onResume() {
        super.onResume()
        // The user may have changed the default assistant in Settings while we were paused.
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_ASSIST_COUNT, assistCount)
        outState.putLong(KEY_ROLE_REQUEST_AT, roleRequestStartedAt)
    }

    /** Counts every delivery of the assist gesture so the PoC can prove it arrived. */
    private fun handleAssist(intent: Intent?, fromNewIntent: Boolean) {
        if (intent?.action != Intent.ACTION_ASSIST) return
        launchedByAssist = true
        assistCount++
        Log.i(TAG, "ACTION_ASSIST received (#$assistCount, onNewIntent=$fromNewIntent)")
        if (fromNewIntent) popCard()
    }

    /**
     * True when this app is the system assistant.
     *
     * RoleManager is authoritative on API 29+. The Settings.Secure fallback reads the
     * "assistant" key, which is what Settings writes when a plain ACTION_ASSIST app is
     * chosen; "voice_interaction_service" stays empty for apps without a
     * VoiceInteractionService, so it can only ever confirm, never rule out.
     */
    private fun isDefaultAssistant(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = getSystemService<RoleManager>()
            if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_ASSISTANT)) {
                return rm.isRoleHeld(RoleManager.ROLE_ASSISTANT)
            }
        }
        return secureComponentPackage(SETTING_ASSISTANT) == packageName ||
            secureComponentPackage(SETTING_VOICE_INTERACTION) == packageName
    }

    /** Reads a Settings.Secure key holding a flattened ComponentName and returns its package. */
    private fun secureComponentPackage(key: String): String? {
        val raw = runCatching { Settings.Secure.getString(contentResolver, key) }.getOrNull()
        if (raw.isNullOrBlank()) return null
        return raw.substringBefore('/').ifBlank { null }
    }

    private fun requestAssistantRole() {
        if (isDefaultAssistant()) { openAssistSettings(); return }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = getSystemService<RoleManager>()
            if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_ASSISTANT)) {
                // Not every build lets an app ask for this role; some throw outright.
                val intent = runCatching {
                    rm.createRequestRoleIntent(RoleManager.ROLE_ASSISTANT)
                }.getOrNull()
                if (intent != null) {
                    roleRequestStartedAt = SystemClock.elapsedRealtime()
                    val launched = runCatching { requestAssistRole.launch(intent) }.isSuccess
                    if (launched) return
                }
                Log.i(TAG, "ROLE_ASSISTANT is not requestable here — opening settings instead")
            }
        }
        openAssistSettings()
    }

    /** Settings → Apps → Default apps → Digital assistant app. */
    private fun openAssistSettings() {
        val candidates = listOf(
            Intent(Settings.ACTION_VOICE_INPUT_SETTINGS),
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
            Intent(Settings.ACTION_SETTINGS),
        )
        for (intent in candidates) {
            if (runCatching { openSettings.launch(intent) }.isSuccess) return
        }
        Toast.makeText(this, R.string.no_assist_settings, Toast.LENGTH_LONG).show()
    }

    private fun render() {
        val isAssistant = isDefaultAssistant()
        // The gesture itself proves we are the assistant, even if the state reads stale.
        val contentMode = isAssistant || launchedByAssist

        findViewById<View>(R.id.onboarding).visibility =
            if (contentMode) View.GONE else View.VISIBLE
        findViewById<View>(R.id.content).visibility =
            if (contentMode) View.VISIBLE else View.GONE

        findViewById<TextView>(R.id.statusText).setText(
            if (isAssistant) R.string.status_assistant else R.string.status_not_assistant
        )
        findViewById<TextView>(R.id.statusSub).text =
            getString(R.string.assist_triggers, assistCount)
        findViewById<Button>(R.id.btnAssistSettings).setText(
            if (isAssistant) R.string.action_change else R.string.action_open_settings
        )
    }

    /** Replays the card animation, so a repeated gesture visibly does something. */
    private fun popCard() {
        val card = findViewById<View>(R.id.card)
        card.animate().cancel()
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

    private companion object {
        const val TAG = "AssistRolePoC"
        const val KEY_ASSIST_COUNT = "assist_count"
        const val KEY_ROLE_REQUEST_AT = "role_request_at"

        /** Settings.Secure keys; both are @hide, so they are used as literals. */
        const val SETTING_ASSISTANT = "assistant"
        const val SETTING_VOICE_INTERACTION = "voice_interaction_service"

        /** Faster than any human answer: the role dialog was never actually shown. */
        const val DIALOG_SUPPRESSED_MS = 700L
    }
}
