package com.redwave.downloader.core.eq

import kotlin.math.log10
import kotlin.math.roundToInt

// ═════════════════════════════════════════════════════════════════════════════
//  EqCurve — 5 смуг з прототипу → смуги КОНКРЕТНОГО телефона.
//
//  android.media.audiofx.Equalizer не гарантує 5 смуг і наші частоти:
//  кількість (getNumberOfBands), центри (getCenterFreq, у МІЛІгерцах!) і
//  діапазон (getBandLevelRange, у мілібелах) залежать від прошивки.
//  Тому UI завжди показує наші 5 повзунків, а на пристрій ідуть значення,
//  інтерпольовані по log-частоті.
// ═════════════════════════════════════════════════════════════════════════════
object EqCurve {

    /** Центри смуг UI, Гц — як у прототипі: 60 / 230 / 910 / 3.6k / 14k. */
    val UI_CENTERS_HZ = floatArrayOf(60f, 230f, 910f, 3600f, 14000f)
    val UI_LABELS     = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
    const val UI_MIN_DB = -12f
    const val UI_MAX_DB = 12f

    /** Пресети в дБ, порядок = UI_CENTERS_HZ. "Custom" з'являється, коли рухають повзунок. */
    val PRESETS: Map<String, FloatArray> = linkedMapOf(
        "Flat"       to floatArrayOf( 0f, 0f,  0f,  0f,  0f),
        "Bass Boost" to floatArrayOf( 8f, 5f,  0f,  0f,  1f),
        "Vocal"      to floatArrayOf(-2f, 0f,  4f,  4f,  1f),
        "Lo-fi"      to floatArrayOf( 3f, 2f, -1f, -4f, -8f),
        "Rock"       to floatArrayOf( 5f, 2f, -2f,  3f,  5f),
    )

    /** Значення кривої (дБ) на довільній частоті: лінійно між сусідніми смугами в log10. */
    fun dbAt(curveDb: FloatArray, hz: Float): Float {
        require(curveDb.size == UI_CENTERS_HZ.size)
        if (hz <= UI_CENTERS_HZ.first()) return curveDb.first()
        if (hz >= UI_CENTERS_HZ.last())  return curveDb.last()
        val x = log10(hz)
        for (i in 0 until UI_CENTERS_HZ.size - 1) {
            val x0 = log10(UI_CENTERS_HZ[i]); val x1 = log10(UI_CENTERS_HZ[i + 1])
            if (x in x0..x1) {
                val t = (x - x0) / (x1 - x0)
                return curveDb[i] + (curveDb[i + 1] - curveDb[i]) * t
            }
        }
        return 0f
    }

    /**
     * Рівні для Equalizer.setBandLevel(band, level).
     * @param deviceCentersMilliHz equalizer.getCenterFreq(band) для кожної смуги
     * @param minMb, maxMb         equalizer.getBandLevelRange()[0], [1]
     * @return мілібели (1 дБ = 100 мБ), вже обрізані в діапазон пристрою
     */
    fun levelsForDevice(curveDb: FloatArray, deviceCentersMilliHz: IntArray, minMb: Short, maxMb: Short): ShortArray =
        ShortArray(deviceCentersMilliHz.size) { i ->
            val mb = (dbAt(curveDb, deviceCentersMilliHz[i] / 1000f) * 100f).roundToInt()
            mb.coerceIn(minMb.toInt(), maxMb.toInt()).toShort()
        }
}
