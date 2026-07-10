package com.sakurbx.fungambx.task

import android.content.Context
import android.os.PowerManager

object WakeLockHolder {
    private var wakeLock: PowerManager.WakeLock? = null

    fun acquire(context: Context, timeoutMs: Long = Scheduler.INTERVAL_MS) {
        if (wakeLock?.isHeld == true) return
        val pm = context.applicationContext
            .getSystemService(Context.POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "AutoOpener::WakeLock"
        ).apply { acquire(timeoutMs) }   // таймаут = страховка від витоку
    }

    fun release() {
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
    }
}