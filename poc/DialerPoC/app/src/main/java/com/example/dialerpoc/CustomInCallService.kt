package com.example.dialerpoc

import android.telecom.Call
import android.telecom.InCallService
import android.util.Log

/**
 * What the dialer role actually buys us.
 *
 * Once the app holds ROLE_DIALER, Telecom binds this service and routes **every** call on
 * the device through it — incoming and outgoing, from any app. Because the manifest also
 * declares IN_CALL_SERVICE_UI, the system draws no call screen of its own: whatever we put
 * on screen here is the call screen.
 *
 * That is the whole point of the PoC, and also its weight: while this app is the default
 * dialer, a user who cannot answer a call from [CallActivity] cannot answer it at all.
 */
class CustomInCallService : InCallService() {

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        Log.i(TAG, "onCallAdded, state=${CallStore.stateOf(call)}")
        CallStore.add(call)
        // Starting an activity from a bound service is allowed here: Telecom gives the
        // default dialer the same background-start exemption a real phone app needs.
        startActivity(CallActivity.intentFor(this))
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Log.i(TAG, "onCallRemoved")
        CallStore.remove(call)
    }

    private companion object {
        const val TAG = "DialerPoC"
    }
}
