package com.example.chargingdreampoc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

/**
 * Foreground service whose only job is to keep a live process around so that
 * [PowerConnectionReceiver] can be registered at RUNTIME.
 *
 * Why runtime registration: since API 26 a manifest-declared receiver for
 * [Intent.ACTION_POWER_CONNECTED] is never called, so the broadcast can only be
 * observed from a running process. A foreground service is the only process type
 * the OS keeps alive reliably in the background.
 *
 * When the charger is connected we do NOT call startActivity() directly (that is
 * blocked from the background since API 29). Instead we post a high-importance
 * notification carrying a full-screen intent, which the system is allowed to turn
 * into an activity launch even while the screen is off or locked.
 */
class ChargingMonitorService : Service() {

    private val receiver = PowerConnectionReceiver()

    override fun onCreate() {
        super.onCreate()
        createChannels()

        // ACTION_POWER_CONNECTED must be registered at runtime (see class doc).
        val filter = IntentFilter(Intent.ACTION_POWER_CONNECTED)
        ContextCompat.registerReceiver(
            this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        Log.i(TAG, "Monitor service created, power receiver registered")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildOngoingNotification()
        // API 34+ requires the running type to be passed explicitly and to match
        // the <service android:foregroundServiceType> declared in the manifest.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                FGS_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(FGS_NOTIFICATION_ID, notification)
        }
        // Restart if the OEM kills us; START_STICKY redelivers a null intent.
        return START_STICKY
    }

    override fun onDestroy() {
        runCatching { unregisterReceiver(receiver) }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /** The persistent, low-key notification that keeps the service in the foreground. */
    private fun buildOngoingNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ONGOING)
            .setContentTitle(getString(R.string.monitor_running_title))
            .setContentText(getString(R.string.monitor_running_text))
            .setSmallIcon(R.drawable.ic_charging)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(mainActivityPendingIntent())
            .build()

    private fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = getSystemService(NotificationManager::class.java)

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ONGOING,
                getString(R.string.channel_ongoing),
                NotificationManager.IMPORTANCE_MIN,
            ),
        )
        nm.createNotificationChannel(
            // HIGH importance is required for a full-screen intent to be honoured.
            NotificationChannel(
                CHANNEL_LAUNCH,
                getString(R.string.channel_launch),
                NotificationManager.IMPORTANCE_HIGH,
            ).apply { description = getString(R.string.channel_launch_desc) },
        )
    }

    private fun mainActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                Intent.FLAG_ACTIVITY_SINGLE_TOP,
        )
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    companion object {
        private const val TAG = "ChargingMonitor"

        private const val CHANNEL_ONGOING = "charging_monitor_ongoing"
        private const val CHANNEL_LAUNCH = "charging_monitor_launch"
        private const val FGS_NOTIFICATION_ID = 1001
        private const val LAUNCH_NOTIFICATION_ID = 1002

        /** Starts the monitor as a foreground service (safe to call repeatedly). */
        fun start(context: Context) {
            val intent = Intent(context, ChargingMonitorService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        /**
         * Called from [PowerConnectionReceiver] the moment the charger is plugged
         * in. Posts a normal, tap-to-open notification (no forced launch, no
         * lock-screen takeover) — tapping it opens [MainActivity].
         */
        fun notifyChargerConnected(context: Context) {
            val openIntent = Intent(context, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP,
            )
            val contentPi = PendingIntent.getActivity(
                context, 1, openIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

            val banner = BitmapFactory.decodeResource(context.resources, R.drawable.home_banner)

            val notification = NotificationCompat.Builder(context, CHANNEL_LAUNCH)
                .setContentTitle(context.getString(R.string.launch_title))
                .setContentText(context.getString(R.string.launch_text))
                .setSmallIcon(R.drawable.ic_charging)
                .setLargeIcon(banner)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(banner)
                        .bigLargeIcon(null as android.graphics.Bitmap?),
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(contentPi)
                .build()

            context.getSystemService(NotificationManager::class.java)
                .notify(LAUNCH_NOTIFICATION_ID, notification)
            Log.i(TAG, "Charger connected -> notification posted")
        }
    }
}
