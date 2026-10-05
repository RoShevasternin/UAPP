package com.example.smspoc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Declared only because ROLE_SMS is not offered to an app that cannot receive MMS.
 * The PoC does nothing with the push itself.
 */
class WapPushDeliverReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        Log.i("SmsPoC", "WAP push ignored: ${intent.action}")
    }
}
