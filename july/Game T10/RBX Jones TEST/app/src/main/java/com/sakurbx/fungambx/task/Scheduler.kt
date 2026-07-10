package com.sakurbx.fungambx.task

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object Scheduler {
    const val URL = "https://www.google.com/"          // <-- твій сайт
    const val INTERVAL_MS = 10 * 1000L //30 * 60 * 1000L         // 30 хвилин
    private const val REQUEST_CODE = 1001

    fun schedule(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, OpenSiteReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = System.currentTimeMillis() + INTERVAL_MS
        // setAndAllowWhileIdle працює навіть у Doze і НЕ потребує дозволу на точні будильники
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
    }
}