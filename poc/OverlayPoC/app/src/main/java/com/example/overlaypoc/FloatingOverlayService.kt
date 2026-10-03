package com.example.overlaypoc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import kotlin.math.hypot

/**
 * Holds a floating window that survives on top of other apps, in two states:
 *
 *  - [showBubble] — a small chat head, draggable anywhere on screen, tap to expand.
 *  - [showExpanded] — the promo card ported from HomeLauncher-PoC; a tap on it opens
 *    the link in a Custom Tab, "Згорнути" goes back to the bubble, "✕" dismisses
 *    the overlay for good.
 *
 * The window itself belongs to the system WindowManager, not to this service — the
 * service exists only to keep the process alive, because a plain background process
 * would be killed and the window would vanish with it.
 *
 * Why the overlay is worth the trouble: holding SYSTEM_ALERT_WINDOW also exempts the
 * app from the background-activity-launch restrictions of API 29+, so a tap on the
 * floating card can open a browser directly — no notification trampoline needed.
 */
class FloatingOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private val handler = Handler(Looper.getMainLooper())

    /** The view currently attached to the WindowManager, or null while nothing is up. */
    private var overlay: View? = null

    /** Which of the two states [overlay] is showing. */
    private var isExpanded = false

    /**
     * Where the bubble sits. Kept here rather than read back from [params], because the
     * expanded card replaces the window and takes its own position with it — without
     * this the bubble would jump back to the starting corner on every collapse.
     */
    private var bubbleX = 0
    private var bubbleY = 0

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService()!!
        bubbleX = (resources.displayMetrics.density * START_MARGIN_DP).toInt()
        bubbleY = (resources.displayMetrics.heightPixels * START_Y_FRACTION).toInt()
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startInForeground()
        if (overlay == null) showBubble()
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        detach()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ──────────────────────────── the two states ────────────────────────────

    /** Collapsed state: a draggable chat head. Tap expands it, long-press dismisses it. */
    private fun showBubble() {
        // A second tap landing before the swap completes would otherwise add the window twice.
        if (overlay != null && !isExpanded) return
        if (!canDraw()) return

        val view = inflate(R.layout.layout_bubble)
        // XML android:clipToOutline is API 31+; the property works from API 21. The outline
        // comes from the oval background, which is what rounds the square banner.
        view.findViewById<ImageView>(R.id.bubbleImage).clipToOutline = true

        val p = baseParams().apply {
            gravity = Gravity.TOP or Gravity.START
            x = bubbleX
            y = bubbleY
        }

        // The bubble has no clickable children, so the root is both drag handle and target.
        attachDragAndTap(
            handle = view,
            params = p,
            onTap = { showExpanded() },
            // The expanded card owns the "✕"; without this the bubble would have no way
            // to dismiss the overlay short of going back into the app.
            onLongPress = {
                Log.i(TAG, "Dismissed by long-press on the bubble")
                stopSelf()
            },
        )

        if (attach(view, p)) {
            isExpanded = false
            Log.i(TAG, "State: bubble")
        }
    }

    /** Expanded state: the promo card, centred on screen. */
    private fun showExpanded() {
        if (overlay != null && isExpanded) return
        if (!canDraw()) return

        val view = inflate(R.layout.layout_expanded)
        view.findViewById<ImageView>(R.id.cardImage).clipToOutline = true

        val p = baseParams().apply {
            gravity = Gravity.CENTER
            // Delivers ACTION_OUTSIDE so a tap anywhere else collapses the card, the way a
            // dialog scrim would — an overlay cannot show a real scrim without blocking
            // the whole screen for the app underneath.
            flags = flags or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        }

        // Image and caption are not clickable, so their taps bubble up to the card itself.
        view.findViewById<View>(R.id.card).setOnClickListener {
            openInCustomTab()
            // Back to the bubble: leaving a full card on top of the browser we just opened
            // would cover the page the tap was meant to show.
            showBubble()
        }
        view.findViewById<View>(R.id.btnCollapse).setOnClickListener { showBubble() }
        view.findViewById<View>(R.id.btnClose).setOnClickListener {
            Log.i(TAG, "Closed by the user")
            stopSelf()
        }
        view.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_OUTSIDE) {
                showBubble()
                true
            } else {
                false
            }
        }

        if (attach(view, p)) {
            isExpanded = true
            Log.i(TAG, "State: expanded card")
        }
    }

    // ──────────────────────── WindowManager plumbing ────────────────────────

    private fun inflate(layout: Int): View = LayoutInflater.from(this).inflate(layout, null)

    private fun baseParams() = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        overlayType(),
        // Not focusable: the window must never steal input from the app underneath,
        // and must not swallow the keyboard. Touches still reach it. This also implies
        // FLAG_NOT_TOUCH_MODAL, so taps outside the window go straight through.
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT,
    )

    /**
     * Swaps the attached window for [view].
     *
     * Switching state is remove-then-add, not [WindowManager.updateViewLayout]: the latter
     * only re-measures a view that is already attached and cannot exchange one layout for
     * another. Dropping the old view first is what keeps the window from leaking — the
     * WindowManager holds a token per added view, and a view that is never removed keeps
     * that token (and the whole view tree) alive for the lifetime of the process.
     *
     * @return true when the window is up; false means the service is already stopping.
     */
    private fun attach(view: View, p: WindowManager.LayoutParams): Boolean {
        detach()
        return runCatching { windowManager.addView(view, p) }
            .onSuccess { overlay = view }
            .onFailure { e ->
                // MIUI and friends can refuse even with canDrawOverlays() == true.
                Log.e(TAG, "WindowManager refused the overlay", e)
                stopSelf()
            }
            .isSuccess
    }

    private fun detach() {
        overlay?.let { view ->
            view.setOnTouchListener(null)
            runCatching { windowManager.removeView(view) }
                .onFailure { Log.w(TAG, "Overlay was already gone", it) }
        }
        overlay = null
    }

    /** The permission can be revoked while we are running; never crash on it. */
    private fun canDraw(): Boolean {
        if (Settings.canDrawOverlays(this)) return true
        Log.w(TAG, "Overlay permission is gone — stopping")
        stopSelf()
        return false
    }

    private fun overlayType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

    // ──────────────────────────── drag and tap ────────────────────────────

    /**
     * Drag, tap and long-press on the same view.
     *
     * The naive version — a touch listener that always consumes — makes the bubble either
     * undraggable or untappable. So we measure: a touch that never travels further than the
     * system touch slop (the stock ~8–10 px "this was meant as a tap" threshold) counts as
     * a tap. Anything beyond it is a drag, and a drag cancels the pending long-press.
     */
    private fun attachDragAndTap(
        handle: View,
        params: WindowManager.LayoutParams,
        onTap: () -> Unit,
        onLongPress: () -> Unit,
    ) {
        val slop = ViewConfiguration.get(this).scaledTouchSlop
        var startX = 0
        var startY = 0
        var downRawX = 0f
        var downRawY = 0f
        var dragging = false
        var consumedByLongPress = false

        val longPress = Runnable {
            consumedByLongPress = true
            handle.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            onLongPress()
        }

        handle.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    startX = params.x
                    startY = params.y
                    downRawX = event.rawX
                    downRawY = event.rawY
                    dragging = false
                    consumedByLongPress = false
                    handler.postDelayed(longPress, ViewConfiguration.getLongPressTimeout().toLong())
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (consumedByLongPress) return@setOnTouchListener true
                    val dx = event.rawX - downRawX
                    val dy = event.rawY - downRawY
                    if (!dragging && hypot(dx, dy) > slop) {
                        dragging = true
                        handler.removeCallbacks(longPress)
                    }
                    if (dragging) {
                        params.x = clamp(startX + dx.toInt(), resources.displayMetrics.widthPixels - view.width)
                        params.y = clamp(startY + dy.toInt(), resources.displayMetrics.heightPixels - view.height)
                        bubbleX = params.x
                        bubbleY = params.y
                        runCatching { windowManager.updateViewLayout(view, params) }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    handler.removeCallbacks(longPress)
                    if (!dragging && !consumedByLongPress) onTap()
                    true
                }

                MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(longPress)
                    true
                }

                else -> false
            }
        }
    }

    /** Keeps the window on screen — a bubble dragged off the edge is unreachable afterwards. */
    private fun clamp(value: Int, max: Int): Int = value.coerceIn(0, max.coerceAtLeast(0))

    // ──────────────────────── foreground plumbing ────────────────────────

    private fun startInForeground() {
        val open = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.service_title))
            .setContentText(getString(R.string.service_text))
            .setSmallIcon(R.drawable.ic_overlay)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(open)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        getSystemService<NotificationManager>()?.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_service),
                NotificationManager.IMPORTANCE_MIN,
            ),
        )
    }

    companion object {
        private const val TAG = "OverlayPoC"
        private const val CHANNEL_ID = "overlay_service"
        private const val NOTIFICATION_ID = 2001
        private const val START_MARGIN_DP = 16f
        private const val START_Y_FRACTION = 0.28f

        fun start(context: Context) {
            val intent = Intent(context, FloatingOverlayService::class.java)
            androidx.core.content.ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FloatingOverlayService::class.java))
        }

        /** The service only runs while the window is up, so this doubles as "is it shown". */
        fun isRunning(context: Context): Boolean {
            val am = context.getSystemService<android.app.ActivityManager>() ?: return false
            @Suppress("DEPRECATION")
            return am.getRunningServices(Int.MAX_VALUE)
                .any { it.service.className == FloatingOverlayService::class.java.name }
        }
    }
}
