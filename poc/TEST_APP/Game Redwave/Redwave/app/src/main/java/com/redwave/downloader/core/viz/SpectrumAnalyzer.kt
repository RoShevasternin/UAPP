package com.redwave.downloader.core.viz

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

// ═════════════════════════════════════════════════════════════════════════════
//  SpectrumAnalyzer — PCM → 48 смуг для візуалізатора плеєра.
//
//  ЧОМУ НЕ android.media.audiofx.Visualizer:
//    він вимагає дозвіл RECORD_AUDIO («Записувати аудіо»). Для завантажувача
//    музики такий діалог лякає і чіпляє ревʼю Google Play. Натомість беремо PCM,
//    який і так іде через ExoPlayer: Media3 TeeAudioProcessor → AudioBufferSink
//    → pushPcm16() тут. Нуль дозволів.
//
//  ПОТОКИ:
//    pushPcm16() кличе аудіопотік ExoPlayer; bands() читає GL-потік у render().
//    Обмін — через @Volatile посилання на готовий масив (copy-on-write), без локів.
//
//  Мапінг смуг і спад (×0.86 на кадр) — як у прототипі, щоб картинка збігалась.
// ═════════════════════════════════════════════════════════════════════════════
class SpectrumAnalyzer(
    private val fftSize: Int = 1024,
    val bandCount: Int = 48,
    /** Яку частку спектра показувати: верхні ~28 % бінів майже порожні. */
    private val usedFraction: Float = 0.72f,
    private val floorDb: Float = -70f,
) {
    init { require(fftSize >= 64 && fftSize and (fftSize - 1) == 0) { "fftSize must be a power of two" } }

    private val window = FloatArray(fftSize) { (0.5 - 0.5 * cos(2.0 * PI * it / (fftSize - 1))).toFloat() }
    private val re = FloatArray(fftSize)
    private val im = FloatArray(fftSize)
    private val mono = FloatArray(fftSize)
    private var filled = 0

    @Volatile private var latest = FloatArray(bandCount)
    private val shown = FloatArray(bandCount)

    // ------------------------------------------------------------------------
    // Вхід: аудіопотік
    // ------------------------------------------------------------------------

    /** 16-bit PCM, interleaved. Можна подавати будь-якими шматками. */
    fun pushPcm16(samples: ShortArray, channels: Int, count: Int = samples.size) {
        var i = 0
        while (i + channels <= count) {
            var sum = 0f
            for (c in 0 until channels) sum += samples[i + c] / 32768f
            mono[filled++] = sum / channels
            i += channels
            if (filled == fftSize) { analyze(); filled = 0 }
        }
    }

    /** Те саме для float PCM (якщо ExoPlayer віддає ENCODING_PCM_FLOAT). */
    fun pushPcmFloat(samples: FloatArray, channels: Int, count: Int = samples.size) {
        var i = 0
        while (i + channels <= count) {
            var sum = 0f
            for (c in 0 until channels) sum += samples[i + c]
            mono[filled++] = sum / channels
            i += channels
            if (filled == fftSize) { analyze(); filled = 0 }
        }
    }

    private fun analyze() {
        for (k in 0 until fftSize) { re[k] = mono[k] * window[k]; im[k] = 0f }
        fft(re, im)
        val half = fftSize / 2
        val used = (half * usedFraction).toInt().coerceAtLeast(bandCount)
        val out = FloatArray(bandCount)
        for (b in 0 until bandCount) {
            val lo = floor((b.toFloat() / bandCount).pow(1.7f) * used).toInt()
            val hi = max(lo + 1, floor(((b + 1f) / bandCount).pow(1.7f) * used).toInt())
            var m = 0f
            for (k in lo until minOf(hi, half)) m = max(m, hypot(re[k], im[k]) / (fftSize / 4f))
            out[b] = toLevel(m)
        }
        latest = out
    }

    /** Амплітуда → 0..1 за шкалою дБ (floorDb..0). */
    private fun toLevel(mag: Float): Float {
        if (mag <= 1e-9f) return 0f
        val db = 20f * log10(mag)
        return ((db - floorDb) / -floorDb).coerceIn(0f, 1f)
    }

    // ------------------------------------------------------------------------
    // Вихід: GL-потік, раз на кадр
    // ------------------------------------------------------------------------

    /** Згладжені рівні 0..1 для малювання. [playing] = false → плавно гаснуть. */
    fun bands(playing: Boolean): FloatArray {
        val src = latest
        for (i in 0 until bandCount) {
            val target = if (playing) src[i] else 0f
            shown[i] = max(target, shown[i] * 0.86f)
        }
        return shown
    }

    /** Енергія басу (перші 4 смуги) — для «пульсу» обкладинки. */
    fun bass(): Float = (shown[0] + shown[1] + shown[2] + shown[3]) / 4f

    fun reset() { filled = 0; latest = FloatArray(bandCount); shown.fill(0f) }

    companion object {
        /** In-place радикс-2 FFT. size — степінь двійки. */
        fun fft(re: FloatArray, im: FloatArray) {
            val n = re.size
            var j = 0
            for (i in 1 until n) {
                var bit = n shr 1
                while (j and bit != 0) { j = j xor bit; bit = bit shr 1 }
                j = j xor bit
                if (i < j) { var t = re[i]; re[i] = re[j]; re[j] = t; t = im[i]; im[i] = im[j]; im[j] = t }
            }
            var len = 2
            while (len <= n) {
                val ang = -2.0 * PI / len
                val wr = cos(ang).toFloat(); val wi = sin(ang).toFloat()
                var i = 0
                while (i < n) {
                    var cr = 1f; var ci = 0f
                    for (k in 0 until len / 2) {
                        val a = i + k; val b = a + len / 2
                        val xr = re[b] * cr - im[b] * ci
                        val xi = re[b] * ci + im[b] * cr
                        re[b] = re[a] - xr; im[b] = im[a] - xi
                        re[a] += xr;        im[a] += xi
                        val ncr = cr * wr - ci * wi
                        ci = cr * wi + ci * wr; cr = ncr
                    }
                    i += len
                }
                len = len shl 1
            }
        }
    }
}
