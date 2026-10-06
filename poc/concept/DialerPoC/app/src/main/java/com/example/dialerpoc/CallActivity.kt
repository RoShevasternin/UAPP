package com.example.dialerpoc

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.telecom.VideoProfile
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * The call screen itself — the layout ported from HomeLauncher-PoC, shown while a call
 * is up instead of the system's own in-call UI.
 *
 * Answer and reject are here even though the brief only asked for "end call": while this
 * app holds the dialer role there is no other UI for an incoming call, so a screen without
 * an answer button would mean the phone can no longer take calls.
 */
class CallActivity : AppCompatActivity() {

    /** Whether a call was ever present. Lets a manual launch preview the screen. */
    private var hadCall = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreen()
        setContentView(R.layout.activity_call)

        // Image and caption both open the link, like the card in HomeLauncher-PoC.
        val openLink = View.OnClickListener { openInCustomTab() }
        findViewById<ImageView>(R.id.cardImage).setOnClickListener(openLink)
        findViewById<TextView>(R.id.cardText).setOnClickListener(openLink)

        findViewById<View>(R.id.btnAnswer).setOnClickListener { answer() }
        findViewById<View>(R.id.btnHangup).setOnClickListener { hangUp() }

        // XML android:clipToOutline is API 31+; the property works from API 21.
        findViewById<ImageView>(R.id.cardImage).clipToOutline = true
    }

    override fun onStart() {
        super.onStart()
        CallStore.observe(::render)
        render()
    }

    override fun onStop() {
        CallStore.observe(null)
        super.onStop()
    }

    // ──────────────────────────── the call ────────────────────────────

    private fun render() {
        val call = CallStore.current
        if (call != null) hadCall = true

        // The call ended — nothing left to show. A screen opened by hand (never had a
        // call) stays up, so the layout can be inspected without ringing the phone.
        if (call == null) {
            if (hadCall) finishAndRemoveTask()
            else renderIdle()
            return
        }

        val state = CallStore.stateOf(call)
        val ringing = state == Call.STATE_RINGING

        findViewById<TextView>(R.id.callState).setText(labelFor(state))
        findViewById<TextView>(R.id.callNumber).text =
            CallStore.numberOf(call) ?: getString(R.string.number_unknown)

        // Answer only exists while the phone is ringing; otherwise a single hang-up.
        findViewById<View>(R.id.btnAnswer).visibility = if (ringing) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.btnHangup).setText(
            if (ringing) R.string.action_reject else R.string.action_hangup
        )
    }

    private fun renderIdle() {
        findViewById<TextView>(R.id.callState).setText(R.string.state_preview)
        findViewById<TextView>(R.id.callNumber).setText(R.string.preview_hint)
        findViewById<View>(R.id.btnAnswer).visibility = View.GONE
        findViewById<TextView>(R.id.btnHangup).setText(R.string.action_close)
    }

    private fun answer() {
        val call = CallStore.current ?: return
        if (CallStore.stateOf(call) == Call.STATE_RINGING) {
            call.answer(VideoProfile.STATE_AUDIO_ONLY)
        }
    }

    /** Reject while ringing, disconnect once connected — Telecom treats them differently. */
    private fun hangUp() {
        val call = CallStore.current
        if (call == null) { finishAndRemoveTask(); return }
        if (CallStore.stateOf(call) == Call.STATE_RINGING) {
            call.reject(false, null)
        } else {
            call.disconnect()
        }
    }

    private fun labelFor(state: Int): Int = when (state) {
        Call.STATE_RINGING -> R.string.state_ringing
        Call.STATE_DIALING, Call.STATE_CONNECTING -> R.string.state_dialing
        Call.STATE_ACTIVE -> R.string.state_active
        Call.STATE_HOLDING -> R.string.state_holding
        Call.STATE_DISCONNECTING, Call.STATE_DISCONNECTED -> R.string.state_ended
        else -> R.string.state_other
    }

    // ──────────────────────── lock screen plumbing ────────────────────────

    /**
     * A ringing phone is usually locked with the screen off, so the call screen has to
     * wake it and draw over the keyguard. The manifest attributes cover API 27+; the
     * window flags below are the only way to do it on 24–26.
     */
    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }

    companion object {
        /**
         * NEW_TASK because the caller is a bound service with no task of its own.
         * The activity is singleInstance, so a second call reuses this screen.
         */
        fun intentFor(context: Context): Intent =
            Intent(context, CallActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
