package com.example.smspoc

import android.content.Context

/**
 * The last message we were handed, plus a counter.
 *
 * A received SMS is not a parcelable we can hand around, and the receiver may run in a
 * process that is torn down a second later — so the two things the screen needs are kept
 * here, and the counter is written to disk to survive the cold start the test is about.
 */
object SmsStore {

    data class Message(val from: String, val body: String, val at: Long)

    @Volatile
    var last: Message? = null
        private set

    fun remember(ctx: Context, from: String?, body: String) {
        last = Message(from ?: ctx.getString(R.string.number_unknown), body, System.currentTimeMillis())
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_COUNT, count(ctx) + 1)
            .putString(KEY_FROM, last?.from)
            .putString(KEY_BODY, body)
            .apply()
    }

    /** How many messages this app has intercepted, including the ones from a dead process. */
    fun count(ctx: Context): Int =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_COUNT, 0)

    /** Falls back to disk: after a cold start [last] is empty but the message is not lost. */
    fun lastOrStored(ctx: Context): Message? {
        last?.let { return it }
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val body = prefs.getString(KEY_BODY, null) ?: return null
        val from = prefs.getString(KEY_FROM, null) ?: ctx.getString(R.string.number_unknown)
        return Message(from, body, 0L)
    }

    private const val PREFS = "sms_poc"
    private const val KEY_COUNT = "count"
    private const val KEY_FROM = "from"
    private const val KEY_BODY = "body"
}
