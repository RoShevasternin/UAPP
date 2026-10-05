package com.example.smspoc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

/**
 * What the SMS path actually buys us.
 *
 * SMS_RECEIVED is delivered to a receiver declared in the manifest even when the app has
 * never been opened in this process — it is one of the few implicit broadcasts Android 8
 * did not cut off. That is the difference from ChargingDreamPoC, which needs a live
 * foreground service and dies the moment the user swipes the app away.
 *
 * We do not start an activity here: background activity starts are blocked since API 29.
 * A notification is posted instead, exactly as in the charging test.
 */
open class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION &&
            intent.action != Telephony.Sms.Intents.SMS_DELIVER_ACTION
        ) return

        // A long message arrives split across several PDUs; this reassembles them.
        val parts = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        if (parts.isEmpty()) return

        val from = parts.first().originatingAddress
        val body = parts.joinToString(separator = "") { it.messageBody.orEmpty() }
        Log.i(TAG, "onReceive: ${parts.size} part(s) from $from")

        SmsStore.remember(ctx, from, body)
        SmsNotifier.show(ctx, SmsStore.lastOrStored(ctx) ?: return)
    }

    private companion object {
        const val TAG = "SmsPoC"
    }
}

/**
 * The same handling for the default-SMS-app path. SMS_DELIVER goes to exactly one app —
 * the default one — and whoever gets it is responsible for showing the message to the
 * user. Nothing else will.
 */
class SmsDeliverReceiver : SmsReceiver()
