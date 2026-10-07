@file:OptIn(UnstableApi::class)

package com.redwave.downloader.android.ringtone

import android.content.ContentValues
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.core.ringtone.CutSelection
import com.redwave.downloader.core.ringtone.RingtoneCut
import com.redwave.downloader.core.ringtone.SaveAs
import com.redwave.downloader.util.log
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

// ═════════════════════════════════════════════════════════════════════════════
//  RingtoneMaker — фрагмент треку → .m4a (AAC) з fade → MediaStore
//  (Ringtones/ | Alarms/ | Notifications/) → RingtoneManager.
//
//  Transformer живе на Looper-потоці → save() кличемо з main.
//  Fade — свій AudioProcessor: лінійний гейн на перших/останніх
//  RingtoneCut.fadeMs() мілісекундах фрагмента.
//  Встановлення системним звуком — лише якщо WRITE_SETTINGS уже дано
//  (Settings.System.canWrite); інакше файл просто збережено в MediaStore.
// ═════════════════════════════════════════════════════════════════════════════
object RingtoneMaker {

    fun save(
        ctx: Context, track: Track, sel: CutSelection,
        fadeIn: Boolean, fadeOut: Boolean, saveAs: SaveAs,
        onResult: (Result<String>) -> Unit,
    ) {
        val src = track.localUri ?: return onResult(Result.failure(IllegalStateException("no file")))
        val out = File(ctx.cacheDir, "ringtone_${System.currentTimeMillis()}.m4a")

        val item = MediaItem.Builder()
            .setUri(src)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(sel.startMs)
                    .setEndPositionMs(sel.endMs)
                    .build()
            ).build()

        val fadeMs = RingtoneCut.fadeMs(sel.lengthMs)
        val fade = FadeProcessor(if (fadeIn) fadeMs else 0, if (fadeOut) fadeMs else 0, sel.lengthMs)
        val edited = EditedMediaItem.Builder(item)
            .setRemoveVideo(true)
            .setEffects(Effects(listOf<AudioProcessor>(fade), emptyList()))
            .build()

        val transformer = Transformer.Builder(ctx)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    val r = runCatching { publish(ctx, out, track.title, saveAs) }
                    out.delete()
                    onResult(r)
                }
                override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
                    log("ringtone export: ${exportException.message}")
                    out.delete()
                    onResult(Result.failure(exportException))
                }
            })
            .build()
        transformer.start(edited, out.absolutePath)
    }

    /** Копія в MediaStore + (якщо можна) системний звук. Повертає назву файлу. */
    private fun publish(ctx: Context, file: File, title: String, saveAs: SaveAs): String {
        val name = RingtoneCut.fileName(title, saveAs)
        val (dir, flag, type) = when (saveAs) {
            SaveAs.RINGTONE     -> Triple(Environment.DIRECTORY_RINGTONES, MediaStore.Audio.Media.IS_RINGTONE, RingtoneManager.TYPE_RINGTONE)
            SaveAs.ALARM        -> Triple(Environment.DIRECTORY_ALARMS, MediaStore.Audio.Media.IS_ALARM, RingtoneManager.TYPE_ALARM)
            SaveAs.NOTIFICATION -> Triple(Environment.DIRECTORY_NOTIFICATIONS, MediaStore.Audio.Media.IS_NOTIFICATION, RingtoneManager.TYPE_NOTIFICATION)
        }
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, name)
            put(MediaStore.Audio.Media.TITLE, name.substringBeforeLast('.'))
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4")
            put(flag, 1)
            if (Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.Audio.Media.RELATIVE_PATH, dir)
                put(MediaStore.Audio.Media.IS_PENDING, 1)
            } else {
                @Suppress("DEPRECATION")
                val target = File(Environment.getExternalStoragePublicDirectory(dir), name)
                target.parentFile?.mkdirs()
                file.copyTo(target, overwrite = true)
                @Suppress("DEPRECATION")
                put(MediaStore.Audio.Media.DATA, target.absolutePath)
            }
        }
        val collection = if (Build.VERSION.SDK_INT >= 29) MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                         else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val uri: Uri = ctx.contentResolver.insert(collection, values) ?: error("MediaStore insert failed")
        if (Build.VERSION.SDK_INT >= 29) {
            ctx.contentResolver.openOutputStream(uri)!!.use { o -> file.inputStream().use { it.copyTo(o) } }
            ctx.contentResolver.update(uri, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) }, null, null)
        }
        if (Build.VERSION.SDK_INT < 23 || Settings.System.canWrite(ctx)) {
            SystemSounds.rememberBeforeSet(ctx, saveAs)       // щоб Restore повернув саме це
            RingtoneManager.setActualDefaultRingtoneUri(ctx, type, uri)
            SystemSounds.markOurs(ctx, saveAs, uri)
            log("ringtone set: $uri ($saveAs)")
        } else {
            log("ringtone saved without WRITE_SETTINGS: $uri")
        }
        return name
    }

    // ------------------------------------------------------------------------
    // Fade
    // ------------------------------------------------------------------------
    private class FadeProcessor(
        private val fadeInMs: Long,
        private val fadeOutMs: Long,
        private val lengthMs: Long,
    ) : BaseAudioProcessor() {

        private var frames = 0L
        private var rate = 44100
        private var channels = 2

        override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
            if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
            rate = inputAudioFormat.sampleRate
            channels = inputAudioFormat.channelCount
            return inputAudioFormat
        }

        override fun queueInput(inputBuffer: ByteBuffer) {
            val n = inputBuffer.remaining()
            if (n == 0) return
            val out = replaceOutputBuffer(n)
            val inS = inputBuffer.order(ByteOrder.LITTLE_ENDIAN)
            val endFrame = lengthMs * rate / 1000
            while (inS.remaining() >= 2 * channels) {
                val ms = frames * 1000 / rate
                var g = 1f
                if (fadeInMs > 0 && ms < fadeInMs) g = ms.toFloat() / fadeInMs
                val fromEnd = (endFrame - frames) * 1000 / rate
                if (fadeOutMs > 0 && fromEnd < fadeOutMs) g = minOf(g, (fromEnd.toFloat() / fadeOutMs).coerceAtLeast(0f))
                for (c in 0 until channels) out.putShort((inS.short * g).toInt().toShort())
                frames++
            }
            inputBuffer.position(inputBuffer.limit())
            out.flip()
        }

        override fun onFlush(streamMetadata: AudioProcessor.StreamMetadata) { frames = 0 }
    }
}
