package com.redwave.downloader.game.utils

import java.util.Locale

/** Формати як у прототипі: fmtT(), «4.1 of 5.2 MB», «486 MB», «2.1 h». */
object Fmt {
    fun time(ms: Long): String {
        val s = (ms / 1000).coerceAtLeast(0)
        return "${s / 60}:${(s % 60).toString().padStart(2, '0')}"
    }

    fun mb(bytes: Long): String = String.format(Locale.US, "%.1f", bytes / 1_048_576f)

    fun size(bytes: Long): String = when {
        bytes >= 1_073_741_824L -> String.format(Locale.US, "%.1f GB", bytes / 1_073_741_824f)
        bytes >= 1_048_576L     -> "${(bytes / 1_048_576f).toInt()} MB"
        bytes > 0               -> "${(bytes / 1024f).toInt()} KB"
        else                    -> "0 MB"
    }

    /** «just now», «5 min ago», «3 h ago», «2 d ago» — для центру подій. */
    fun ago(atMs: Long, nowMs: Long = System.currentTimeMillis()): String {
        val m = (nowMs - atMs).coerceAtLeast(0) / 60_000
        return when {
            m < 1      -> "just now"
            m < 60     -> "$m min ago"
            m < 60 * 24 -> "${m / 60} h ago"
            else       -> "${m / 1440} d ago"
        }
    }

    fun hours(ms: Long): String = String.format(Locale.US, "%.1f h", ms / 3_600_000f)

    fun speed(bytesPerSec: Float): String = String.format(Locale.US, "%.1f MB/s", bytesPerSec / 1_048_576f)
}
