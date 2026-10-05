package com.example.smspoc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * "Reply with a message" from the incoming-call screen. Like the WAP push receiver, this
 * exists so the app is eligible for ROLE_SMS at all — the role is withheld from an app
 * that declares only some of the four messaging entry points.
 */
class RespondViaMessageService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("SmsPoC", "respond-via-message ignored: ${intent?.action}")
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
