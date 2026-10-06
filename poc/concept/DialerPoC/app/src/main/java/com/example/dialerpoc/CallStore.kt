package com.example.dialerpoc

import android.os.Build
import android.telecom.Call

/**
 * The bridge between the service Telecom talks to and the screen the user sees.
 *
 * Telecom hands [CustomInCallService] live [Call] objects, and [CallActivity] needs those
 * very same instances to answer or hang up. They are held in a process-wide object rather
 * than passed through an Intent on purpose: a Call is not parcelable, and the service and
 * the activity live in the same process anyway.
 */
object CallStore {

    private val calls = mutableListOf<Call>()
    private var listener: (() -> Unit)? = null

    /** Telecom moves a call between states on its own; the screen has to follow. */
    private val callback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) = notifyChanged()
        override fun onDetailsChanged(call: Call, details: Call.Details) = notifyChanged()
    }

    /** A ringing call wins: it is the one the user is being asked about right now. */
    val current: Call?
        get() = calls.firstOrNull { stateOf(it) == Call.STATE_RINGING } ?: calls.firstOrNull()

    val isEmpty: Boolean get() = calls.isEmpty()

    fun add(call: Call) {
        if (calls.none { it === call }) {
            calls += call
            call.registerCallback(callback)
        }
        notifyChanged()
    }

    fun remove(call: Call) {
        call.unregisterCallback(callback)
        calls.removeAll { it === call }
        notifyChanged()
    }

    /** Only the visible screen observes; passing null on the way out avoids leaking it. */
    fun observe(block: (() -> Unit)?) {
        listener = block
    }

    private fun notifyChanged() {
        listener?.invoke()
    }

    /** Call.getState() is deprecated from API 31, where the state moved onto Details. */
    fun stateOf(call: Call): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            call.details.state
        } else {
            @Suppress("DEPRECATION")
            call.state
        }

    /** The other party, as Telecom knows it — may be absent on a withheld number. */
    fun numberOf(call: Call): String? =
        call.details.handle?.schemeSpecificPart?.takeIf { it.isNotBlank() }
}
