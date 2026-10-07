package com.redwave.downloader.core.ringtone

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

// ═════════════════════════════════════════════════════════════════════════════
//  RingtoneCut — правила виділення фрагмента на екрані Ringtone.
//  Порт waveDrag() з прототипу: ті самі межі й та сама «найближча ручка».
//
//  Межі: фрагмент 3…40 с. Рингтон довший за ~40 с ніхто не дослухає,
//  а коротший за 3 с звучить як клік.
// ═════════════════════════════════════════════════════════════════════════════
data class CutSelection(val startMs: Long, val endMs: Long) {
    val lengthMs: Long get() = endMs - startMs
}

enum class CutHandle { START, END }

enum class SaveAs { RINGTONE, ALARM, NOTIFICATION }

object RingtoneCut {

    const val MIN_MS = 3_000L
    const val MAX_MS = 40_000L
    const val DEFAULT_LEN_MS = 25_000L
    const val DEFAULT_START_FRACTION = 0.30
    const val MAX_FADE_MS = 3_000L   // 1.5 с на дзвінку не чутно (VELDAN, 07.10.2026)

    /** Стартове виділення: з 30 % треку, 25 с (або скільки влізе). */
    fun default(durationMs: Long): CutSelection {
        if (durationMs <= MIN_MS) return CutSelection(0, durationMs.coerceAtLeast(0))
        var start = (durationMs * DEFAULT_START_FRACTION).toLong()
        var end   = min(durationMs, start + DEFAULT_LEN_MS)
        if (end - start < MIN_MS) { start = max(0, end - MIN_MS); end = start + MIN_MS }
        return CutSelection(start, end)
    }

    fun nearestHandle(sel: CutSelection, touchMs: Long): CutHandle =
        if (abs(touchMs - sel.startMs) < abs(touchMs - sel.endMs)) CutHandle.START else CutHandle.END

    /** Тягнемо ручку в позицію [toMs]; повертає нове виділення з усіма межами. */
    fun drag(sel: CutSelection, handle: CutHandle, toMs: Long, durationMs: Long): CutSelection {
        val minLen = min(MIN_MS, durationMs)
        return when (handle) {
            CutHandle.START -> {
                var v = min(toMs, sel.endMs - minLen)
                v = maxOf(v, sel.endMs - MAX_MS, 0L)
                sel.copy(startMs = v)
            }
            CutHandle.END -> {
                var v = max(toMs, sel.startMs + minLen)
                v = minOf(v, sel.startMs + MAX_MS, durationMs)
                sel.copy(endMs = v)
            }
        }
    }

    /** Fade: чверть фрагмента, але не більше 3 с. */
    fun fadeMs(lengthMs: Long): Long = min(MAX_FADE_MS, lengthMs / 4)

    /** Позиція пальця на хвилі (0..1) → мілісекунди. */
    fun fractionToMs(fraction: Float, durationMs: Long): Long =
        (fraction.coerceIn(0f, 1f) * durationMs).toLong()

    fun msToFraction(ms: Long, durationMs: Long): Float =
        if (durationMs <= 0) 0f else (ms.toFloat() / durationMs).coerceIn(0f, 1f)

    /** Назва файлу рингтону: "Night Drive (ringtone).m4a". */
    fun fileName(title: String, saveAs: SaveAs): String {
        val clean = title.replace(Regex("""[\\/:*?"<>|]"""), " ").trim().ifEmpty { "Redwave" }
        return "$clean (${saveAs.name.lowercase()}).m4a"
    }
}
