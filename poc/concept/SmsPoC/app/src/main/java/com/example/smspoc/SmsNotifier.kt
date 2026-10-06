package com.example.smspoc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

/** The heads-up card the user sees a moment after the message lands. */
object SmsNotifier {

    fun show(ctx: Context, msg: SmsStore.Message) {
        val nm = ctx.getSystemService<NotificationManager>() ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // IMPORTANCE_HIGH is what makes it slide in over whatever is on screen.
            val channel = NotificationChannel(CHANNEL, ctx.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH)
            channel.description = ctx.getString(R.string.channel_desc)
            nm.createNotificationChannel(channel)
        }

        val open = PendingIntent.getActivity(
            ctx, 0, SmsActivity.intentFor(ctx),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(R.drawable.ic_sms)
            .setContentTitle(ctx.getString(R.string.notif_title))
            .setContentText(ctx.getString(R.string.card_text))
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg.body))
            .setContentIntent(open)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .build()

        // No POST_NOTIFICATIONS (API 33+) means notify() is a silent no-op, which is
        // the one way this path can fail quietly — the event itself still arrives.
        nm.notify(NOTIFICATION_ID, n)
    }

    private const val CHANNEL = "sms_poc_incoming"
    private const val NOTIFICATION_ID = 1006
}
