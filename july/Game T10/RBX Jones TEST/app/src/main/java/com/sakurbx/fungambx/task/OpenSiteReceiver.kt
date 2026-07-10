package com.sakurbx.fungambx.task

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OpenSiteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        WakeLockHolder.acquire(context)   // тримаємо CPU під час переходу

        val activityIntent = Intent(context, WakeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(activityIntent)

        Scheduler.schedule(context)       // наступний запуск через 30 хв
    }
}