package com.redwave.downloader.android

import android.media.audiofx.Equalizer
import com.redwave.downloader.core.eq.EqCurve
import com.redwave.downloader.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  AudioFx — android.media.audiofx.Equalizer на audioSessionId плеєра.
//  PlaybackService повідомляє сесію (attachSession), GDX — криву (set).
//  Кількість смуг і частоти — від прошивки: UI завжди 5 повзунків,
//  на пристрій — EqCurve.levelsForDevice() (центри в мГц, рівні в мБ).
// ═════════════════════════════════════════════════════════════════════════════
object AudioFx {

    @Volatile private var enabled = true
    @Volatile private var curve   = FloatArray(5)
    private var eq: Equalizer? = null
    private var sessionId = 0

    @Synchronized
    fun attachSession(id: Int) {
        if (id == sessionId && eq != null) return
        release()
        sessionId = id
        if (id == 0) return
        eq = runCatching { Equalizer(0, id) }.onFailure { log("Equalizer: ${it.message}") }.getOrNull()
        apply()
    }

    @Synchronized
    fun set(enabled: Boolean, bandsDb: FloatArray) {
        this.enabled = enabled
        this.curve   = bandsDb.copyOf()
        apply()
    }

    @Synchronized
    fun release() {
        runCatching { eq?.release() }
        eq = null
    }

    private fun apply() {
        val e = eq ?: return
        runCatching {
            val n = e.numberOfBands.toInt()
            val centers = IntArray(n) { e.getCenterFreq(it.toShort()) }
            val range = e.bandLevelRange
            val levels = EqCurve.levelsForDevice(curve, centers, range[0], range[1])
            for (i in 0 until n) e.setBandLevel(i.toShort(), levels[i])
            e.enabled = enabled
            log("Equalizer.setBandLevel ${levels.joinToString()} mB (enabled=$enabled, bands=$n)")
        }.onFailure { log("Equalizer apply: ${it.message}") }
    }
}
