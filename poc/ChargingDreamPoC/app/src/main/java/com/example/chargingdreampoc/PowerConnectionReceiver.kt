package com.example.chargingdreampoc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Fires when the charging cable is connected.
 *
 * NOTE: this receiver is registered at RUNTIME by [ChargingMonitorService], never
 * in the manifest — a manifest-declared receiver for [Intent.ACTION_POWER_CONNECTED]
 * has been ignored by the platform since API 26.
 */
class PowerConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_POWER_CONNECTED) return
        Log.i(TAG, "ACTION_POWER_CONNECTED received")
        ChargingMonitorService.notifyChargerConnected(context.applicationContext)
    }

    private companion object {
        const val TAG = "PowerReceiver"
    }
}
