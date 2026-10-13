package com.vidtools.framefall.android.studio

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.DefaultEncoderFactory
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.VideoEncoderSettings
import com.vidtools.framefall.android.AndroidBridge
import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.MediaKind
import com.vidtools.framefall.game.platform.StudioJob
import com.vidtools.framefall.game.platform.StudioOutput
import com.vidtools.framefall.util.log
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

// ═════════════════════════════════════════════════════════════════════════════
//  StudioEngine — інструменти Studio на телефоні, без сервера.
//
//    trim / status / compress / loop → Media3 Transformer (MediaCodec, апаратний енкодер);
//    audio                           → Transformer без відеодоріжки → AAC у .m4a;
//    frame                           → MediaMetadataRetriever.getFrameAtTime → JPEG;
//    gif                             → кадри MMR (10 fps, 320 px) → GifEncoder (свій, Android
//                                      енкодера GIF не має).
//
//  Куди: відео — Movies/Framefall, звук — Music/Framefall, кадр і GIF — Pictures/Framefall.
//  Петля фону — приватно (filesDir/homebg): це налаштування головного екрана, не файл людини.
//  Після запису — MediaScanner, щоб файл з'явився в Галереї.
//
//  Transformer вимагає потік з Looper — працюємо на main; кадри й GIF — у фоновому потоці.
//  Одна робота за раз (Studio показує прогрес і блокує кнопку).
// ═════════════════════════════════════════════════════════════════════════════
@OptIn(UnstableApi::class)
class StudioEngine(private val ctx: Context) {

    private val main = Handler(Looper.getMainLooper())
    private var transformer: Transformer? = null
    private val cancelled = AtomicBoolean(false)

    fun run(job: StudioJob, onProgress: (Float) -> Unit, onResult: (Result<List<StudioOutput>>) -> Unit) {
        cancelled.set(false)
        val src = job.video.localUri ?: return onResult(Result.failure(IllegalStateException("no file")))
        val base = safeName(job.video.title)
        // Бітрейт як у джерела (з запасом 10 %): без цього енкодер бере свій, і обрізаний шматок
        // WebM 1080p виходив більшим за весь фільм (заміряно 13.10.2026: 60 с → 65 МБ)
        val srcBitrate = if (job.video.durationMs > 0) (job.video.sizeBytes * 8_000L / job.video.durationMs * 11 / 10).toInt().coerceIn(800_000, 8_000_000) else 0
        log("studio ${job.tool}: ${job.video.title} [${job.startMs}..${job.endMs}] opt=${job.option}")
        when (job.tool) {
            "trim" -> exportVideo(src, job.startMs, job.endMs, outFile(Environment.DIRECTORY_MOVIES, "$base (trim)", "mp4"), onProgress, bitrate = srcBitrate) { r ->
                onResult(r.map { listOf(output(it, "${job.video.title} · Trim", MediaKind.VIDEO, "MP4")) })
            }
            "audio" -> exportVideo(src, job.startMs, job.endMs, outFile(Environment.DIRECTORY_MUSIC, base, "m4a"), onProgress, removeVideo = true) { r ->
                onResult(r.map { listOf(output(it, "${job.video.title} (audio)", MediaKind.AUDIO, "M4A")) })
            }
            "compress" -> exportVideo(src, -1, -1, outFile(Environment.DIRECTORY_MOVIES, "$base (${job.option}p)", "mp4"), onProgress, height = job.option) { r ->
                onResult(r.map { listOf(output(it, "${job.video.title} · ${job.option}p", MediaKind.VIDEO, "MP4")) })
            }
            "loop" -> {
                val dir = File(ctx.filesDir, "homebg").apply { mkdirs() }
                val end = minOf(job.endMs, job.startMs + LOOP_MAX_MS)
                exportVideo(src, job.startMs, end, File(dir, "${job.video.id}_${System.currentTimeMillis()}.mp4"), onProgress, removeAudio = true, height = 1280, portrait = true) { r ->
                    // Старі петлі більше не потрібні: фон — лише одна, остання
                    r.onSuccess { f -> dir.listFiles()?.filter { it != f }?.forEach { it.delete() } }
                    onResult(r.map { listOf(output(it, "${job.video.title} · loop", MediaKind.VIDEO, "MP4", scan = false)) })
                }
            }
            "status" -> splitStatus(src, job, base, srcBitrate, onProgress, onResult)
            "frame" -> thread(name = "studio-frame") { onResult(runCatching { listOf(grabFrame(src, job, base)) }) }
            "gif" -> thread(name = "studio-gif") { onResult(runCatching { listOf(makeGif(src, job, base, onProgress)) }) }
            else -> onResult(Result.failure(IllegalArgumentException(job.tool)))
        }
    }

    fun cancel() {
        cancelled.set(true)
        main.post { transformer?.cancel(); transformer = null }
    }

    // ------------------------------------------------------------------------
    // Transformer
    // ------------------------------------------------------------------------
    private fun exportVideo(
        src: String, startMs: Long, endMs: Long, out: File,
        onProgress: (Float) -> Unit,
        removeVideo: Boolean = false, removeAudio: Boolean = false, height: Int = 0,
        /** Фон Home: кроп під пропорцію телефона (9 : 19.5), інакше ландшафт 1080p роздувався б до 2275×1280. */
        portrait: Boolean = false,
        /** Бітрейт відео; 0 — за висотою кадру (стиснення, петля) або як вирішить енкодер. */
        bitrate: Int = 0,
        onDone: (Result<File>) -> Unit,
    ) {
        main.post {
            val item = MediaItem.Builder().setUri(Uri.parse(src)).apply {
                if (startMs >= 0 && endMs > startMs) setClippingConfiguration(
                    MediaItem.ClippingConfiguration.Builder().setStartPositionMs(startMs).setEndPositionMs(endMs).build()
                )
            }.build()
            val video = buildList {
                if (portrait) add(Presentation.createForAspectRatio(9f / 19.5f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP))
                if (height > 0) add(Presentation.createForHeight(height))
            }
            val effects = if (video.isNotEmpty()) Effects(emptyList(), video) else Effects.EMPTY
            val edited = EditedMediaItem.Builder(item)
                .setRemoveVideo(removeVideo)
                .setRemoveAudio(removeAudio)
                .setEffects(effects)
                .build()
            val builder = Transformer.Builder(ctx).setAudioMimeType(MimeTypes.AUDIO_AAC)
            if (!removeVideo) builder.setVideoMimeType(MimeTypes.VIDEO_H264)
            val target = when {
                bitrate > 0 -> bitrate
                // Стиснення і петля фону: бітрейт під висоту кадру, а не «як у джерела»
                portrait -> 3_000_000
                height >= 720 -> 2_500_000
                height >= 480 -> 1_200_000
                height > 0 -> 700_000
                else -> 0
            }
            if (target > 0 && !removeVideo) {
                builder.setEncoderFactory(
                    DefaultEncoderFactory.Builder(ctx)
                        .setRequestedVideoEncoderSettings(VideoEncoderSettings.Builder().setBitrate(target).build())
                        .build()
                )
            }
            out.parentFile?.mkdirs()
            out.delete()
            val t = builder.addListener(object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    transformer = null
                    log("studio export ok → ${out.name} ${out.length() / 1024} KB")
                    onDone(Result.success(out))
                }
                override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
                    transformer = null
                    log("studio export failed: ${exportException.errorCodeName} ${exportException.message}")
                    out.delete()
                    onDone(Result.failure(exportException))
                }
            }).build()
            transformer = t
            t.start(edited, out.absolutePath)
            val holder = ProgressHolder()
            val tick = object : Runnable {
                override fun run() {
                    val cur = transformer ?: return
                    if (cur !== t) return
                    if (cur.getProgress(holder) == Transformer.PROGRESS_STATE_AVAILABLE) onProgress(holder.progress / 100f)
                    main.postDelayed(this, 150L)
                }
            }
            main.postDelayed(tick, 150L)
        }
    }

    /** Рівні частини по job.option секунд (за замовчуванням 30) — послідовно, прогрес загальний. */
    private fun splitStatus(src: String, job: StudioJob, base: String, bitrate: Int, onProgress: (Float) -> Unit, onResult: (Result<List<StudioOutput>>) -> Unit) {
        val partMs = (job.option.takeIf { it > 0 } ?: 30) * 1000L
        val start = job.startMs.coerceAtLeast(0L)
        val end = if (job.endMs > start) job.endMs else job.video.durationMs
        val n = ((end - start + partMs - 1) / partMs).toInt().coerceIn(1, MAX_PARTS)
        val outs = ArrayList<StudioOutput>()
        fun part(i: Int) {
            if (cancelled.get()) { onResult(Result.failure(InterruptedException("cancelled"))); return }
            if (i >= n) { onResult(Result.success(outs)); return }
            val s = start + i * partMs
            val e = minOf(end, s + partMs)
            val file = outFile(Environment.DIRECTORY_MOVIES, "$base (part ${i + 1} of $n)", "mp4")
            exportVideo(src, s, e, file, { p -> onProgress((i + p) / n) }, bitrate = bitrate) { r ->
                r.onSuccess { f -> outs += output(f, "${job.video.title} · ${Copy.Studio.partOf(i + 1, n)}", MediaKind.VIDEO, "MP4"); part(i + 1) }
                    .onFailure { onResult(Result.failure(it)) }
            }
        }
        part(0)
    }

    // ------------------------------------------------------------------------
    // Кадр і GIF
    // ------------------------------------------------------------------------
    private fun grabFrame(src: String, job: StudioJob, base: String): StudioOutput {
        val mmr = retriever(src)
        try {
            val bmp = mmr.getFrameAtTime(job.startMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST) ?: error("no frame")
            val out = outFile(Environment.DIRECTORY_PICTURES, "$base ${fmtTime(job.startMs)}", "jpg")
            out.parentFile?.mkdirs()
            FileOutputStream(out).use { bmp.compress(Bitmap.CompressFormat.JPEG, 92, it) }
            val w = bmp.width; val h = bmp.height
            val thumb = AndroidBridge.saveThumb(ctx, "frame_${out.nameWithoutExtension.hashCode()}", bmp)
            bmp.recycle()
            scan(out, "image/jpeg")
            return StudioOutput(Uri.fromFile(out).toString(), "${job.video.title} · ${fmtTime(job.startMs)}", MediaKind.IMAGE, "JPG", 0, w, h, out.length(), thumb)
        } finally { mmr.release() }
    }

    private fun makeGif(src: String, job: StudioJob, base: String, onProgress: (Float) -> Unit): StudioOutput {
        val start = job.startMs.coerceAtLeast(0L)
        val end = minOf(if (job.endMs > start) job.endMs else start + GIF_MAX_MS, start + GIF_MAX_MS)
        val frames = ((end - start) / GIF_FRAME_MS).toInt().coerceAtLeast(2)
        val out = outFile(Environment.DIRECTORY_PICTURES, base, "gif")
        out.parentFile?.mkdirs()
        val mmr = retriever(src)
        var thumb: String? = null
        var w = 0; var h = 0
        try {
            FileOutputStream(out).use { fos ->
                val enc = GifEncoder(fos, delayCs = (GIF_FRAME_MS / 10).toInt())
                for (i in 0 until frames) {
                    if (cancelled.get()) throw InterruptedException("cancelled")
                    val us = (start + i * GIF_FRAME_MS) * 1000
                    val raw = (if (Build.VERSION.SDK_INT >= 27) mmr.getScaledFrameAtTime(us, MediaMetadataRetriever.OPTION_CLOSEST, GIF_W, GIF_W)
                               else mmr.getFrameAtTime(us, MediaMetadataRetriever.OPTION_CLOSEST)) ?: continue
                    val bmp = if (raw.width > GIF_W) Bitmap.createScaledBitmap(raw, GIF_W, (raw.height * GIF_W.toFloat() / raw.width).toInt().coerceAtLeast(2), true).also { if (it !== raw) raw.recycle() } else raw
                    if (thumb == null) { thumb = AndroidBridge.saveThumb(ctx, "gif_${out.nameWithoutExtension.hashCode()}", bmp); w = bmp.width; h = bmp.height }
                    enc.addFrame(bmp)
                    bmp.recycle()
                    onProgress((i + 1f) / frames)
                }
                enc.finish()
            }
        } catch (e: Throwable) { out.delete(); throw e } finally { mmr.release() }
        scan(out, "image/gif")
        return StudioOutput(Uri.fromFile(out).toString(), "${job.video.title} · GIF", MediaKind.IMAGE, "GIF", end - start, w, h, out.length(), thumb)
    }

    // ------------------------------------------------------------------------
    // Хелпери
    // ------------------------------------------------------------------------
    private fun output(f: File, title: String, kind: MediaKind, format: String, scan: Boolean = true): StudioOutput {
        if (scan) scan(f, if (kind == MediaKind.AUDIO) "audio/mp4" else "video/mp4")
        val mmr = MediaMetadataRetriever()
        return try {
            mmr.setDataSource(f.absolutePath)
            val dur = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            var w = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            var h = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            val rot = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
            if (rot == 90 || rot == 270) { val t = w; w = h; h = t }
            // Кадр з 10 % (не далі 3 с): перший кадр обрізки часто чорний
            val thumb = if (kind == MediaKind.VIDEO) mmr.getFrameAtTime((dur * 100L).coerceAtMost(3_000_000L))?.let { AndroidBridge.saveThumb(ctx, "s_${f.nameWithoutExtension.hashCode()}", it) } else null
            StudioOutput(Uri.fromFile(f).toString(), title, kind, format, dur, w, h, f.length(), thumb)
        } catch (e: Exception) {
            StudioOutput(Uri.fromFile(f).toString(), title, kind, format, 0, 0, 0, f.length(), null)
        } finally { mmr.release() }
    }

    private fun retriever(src: String) = MediaMetadataRetriever().apply {
        val u = Uri.parse(src)
        if (u.scheme == "file" || u.scheme == null) setDataSource(u.path ?: src) else setDataSource(ctx, u)
    }

    private fun scan(f: File, mime: String) {
        MediaScannerConnection.scanFile(ctx, arrayOf(f.absolutePath), arrayOf(mime), null)
    }

    /** Movies|Music|Pictures/Framefall/<name>.<ext>, з «(2)» якщо зайнято. */
    private fun outFile(publicDir: String, name: String, ext: String): File {
        val dir = File(Environment.getExternalStoragePublicDirectory(publicDir), AndroidBridge.FOLDER)
        var f = File(dir, "$name.$ext"); var n = 2
        while (f.exists()) { f = File(dir, "$name ($n).$ext"); n++ }
        return f
    }

    private fun safeName(s: String) = s.replace(Regex("""[\\/:*?"<>|·]"""), " ").replace(Regex("""\s+"""), " ").trim().take(60).ifEmpty { "Framefall" }

    private fun fmtTime(ms: Long): String {
        val s = ms / 1000
        return "${s / 60}m${(s % 60).toString().padStart(2, '0')}s"
    }

    companion object {
        const val LOOP_MAX_MS = 10_000L
        const val GIF_MAX_MS = 6_000L
        const val GIF_FRAME_MS = 100L
        const val GIF_W = 320
        const val MAX_PARTS = 20
    }
}
