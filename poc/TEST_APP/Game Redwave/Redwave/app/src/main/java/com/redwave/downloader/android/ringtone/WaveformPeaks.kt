package com.redwave.downloader.android.ringtone

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.util.log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.max

// ═════════════════════════════════════════════════════════════════════════════
//  WaveformPeaks — хвиля для різака рингтонів: MediaExtractor + MediaCodec →
//  PCM → максимум амплітуди в кожному з [bins] відрізків часу. Кеш на диску
//  (filesDir/peaks/<id>_<bins>.bin): декодування 4-хвилинного mp3 — секунда-дві.
// ═════════════════════════════════════════════════════════════════════════════
object WaveformPeaks {

    fun load(ctx: Context, track: Track, bins: Int): FloatArray {
        val cache = File(File(ctx.filesDir, "peaks").apply { mkdirs() }, "${track.id}_$bins.bin")
        if (cache.exists()) runCatching { return read(cache, bins) }

        val peaks = runCatching { decode(ctx, track.localUri ?: error("no uri"), bins) }
            .onFailure { log("waveform ${track.title}: ${it.message}") }
            .getOrElse { FloatArray(bins) }
        if (peaks.any { it > 0f }) runCatching { write(cache, peaks) }
        return peaks
    }

    private fun decode(ctx: Context, uri: String, bins: Int): FloatArray {
        val ex = MediaExtractor()
        val u = Uri.parse(uri)
        if (u.scheme == "file") ex.setDataSource(u.path!!) else ex.setDataSource(ctx, u, null)

        val ti = (0 until ex.trackCount).first { ex.getTrackFormat(it).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true }
        ex.selectTrack(ti)
        val fmt = ex.getTrackFormat(ti)
        val durUs = fmt.getLong(MediaFormat.KEY_DURATION).coerceAtLeast(1)
        val codec = MediaCodec.createDecoderByType(fmt.getString(MediaFormat.KEY_MIME)!!)
        codec.configure(fmt, null, null, 0)
        codec.start()

        val peaks = FloatArray(bins)
        val info = MediaCodec.BufferInfo()
        var inputDone = false
        var outputDone = false
        try {
            while (!outputDone) {
                if (!inputDone) {
                    val ii = codec.dequeueInputBuffer(10_000)
                    if (ii >= 0) {
                        val buf = codec.getInputBuffer(ii)!!
                        val n = ex.readSampleData(buf, 0)
                        if (n < 0) {
                            codec.queueInputBuffer(ii, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            inputDone = true
                        } else {
                            codec.queueInputBuffer(ii, 0, n, ex.sampleTime, 0)
                            ex.advance()
                        }
                    }
                }
                val oi = codec.dequeueOutputBuffer(info, 10_000)
                if (oi >= 0) {
                    if (info.size > 0) {
                        val out = codec.getOutputBuffer(oi)!!.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
                        val bin = ((info.presentationTimeUs * bins) / durUs).toInt().coerceIn(0, bins - 1)
                        var peak = peaks[bin]
                        // кожен 4-й семпл — для піка досить, а вдвічі-вчетверо швидше
                        var i = 0
                        val n = out.remaining()
                        while (i < n) { peak = max(peak, abs(out.get(i) / 32768f)); i += 4 }
                        peaks[bin] = peak
                    }
                    codec.releaseOutputBuffer(oi, false)
                    if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) outputDone = true
                }
            }
        } finally {
            runCatching { codec.stop() }
            codec.release()
            ex.release()
        }
        // порожні біни (рідкі пакети) — сусіднє значення, потім нормалізація до 1
        for (i in 1 until bins) if (peaks[i] == 0f) peaks[i] = peaks[i - 1]
        val mx = peaks.maxOrNull()?.takeIf { it > 0f } ?: return peaks
        for (i in peaks.indices) peaks[i] = (peaks[i] / mx).coerceIn(0.06f, 1f)
        return peaks
    }

    private fun write(f: File, p: FloatArray) = DataOutputStream(f.outputStream().buffered()).use { o -> p.forEach { o.writeFloat(it) } }
    private fun read(f: File, bins: Int) = DataInputStream(f.inputStream().buffered()).use { i -> FloatArray(bins) { i.readFloat() } }
}
