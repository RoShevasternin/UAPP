package com.redwave.downloader.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.game.platform.PlaybackSnapshot
import com.redwave.downloader.util.log
import java.io.File

// ═════════════════════════════════════════════════════════════════════════════
//  PlaybackClient — MediaController до PlaybackService.
//
//  MediaController можна чіпати ЛИШЕ з потоку, де його створено (main).
//  GDX кличе з GL-потоку, тому:
//    • команди → main.post { … } (до з'єднання — у чергу);
//    • стан → @Volatile snapshot, який main оновлює зі слухача і таймером
//      (4 рази на секунду, лише поки грає) — GDX читає його хоч щокадру.
// ═════════════════════════════════════════════════════════════════════════════
class PlaybackClient(private val ctx: Context) {

    private val main = Handler(Looper.getMainLooper())
    private var future: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private val queued = ArrayList<(MediaController) -> Unit>()

    /** GDX-подія «стан змінився» (трек, play/pause) — ставить AndroidBridge. */
    var onChanged: () -> Unit = {}

    @Volatile var snapshot = PlaybackSnapshot(null, false, 0, 0, false, false)
        private set

    private var sleepAt = 0L

    // ------------------------------------------------------------------------
    // Connect
    // ------------------------------------------------------------------------
    fun connect() { main.post { connectNow() } }

    private fun connectNow() {
        if (future != null) return
        val token = SessionToken(ctx, ComponentName(ctx, PlaybackService::class.java))
        val f = MediaController.Builder(ctx, token).buildAsync()
        future = f
        f.addListener({
            val c: MediaController? = runCatching { f.get() }.onFailure { log("MediaController: ${it.message}") }.getOrNull()
            if (c == null) { future = null; return@addListener }
            controller = c
            c.addListener(listener)
            queued.forEach { it(c) }
            queued.clear()
            refresh()
        }, ContextCompat.getMainExecutor(ctx))
    }

    private fun run(block: (MediaController) -> Unit) {
        main.post {
            val c = controller
            if (c != null) { block(c); refresh() } else { queued += block; connectNow() }
        }
    }

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            refresh()
            if (events.containsAny(Player.EVENT_MEDIA_ITEM_TRANSITION, Player.EVENT_IS_PLAYING_CHANGED,
                    Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED, Player.EVENT_REPEAT_MODE_CHANGED)) onChanged()
        }
    }

    private val ticker = object : Runnable {
        override fun run() {
            refresh()
            if (sleepAt > 0 && System.currentTimeMillis() >= sleepAt) {
                sleepAt = 0
                controller?.pause()
                log("sleep timer → pause")
            }
            if (snapshot.isPlaying || sleepAt > 0) main.postDelayed(this, 250)
        }
    }

    private fun refresh() {
        val c = controller ?: return
        val left = if (sleepAt > 0) (((sleepAt - System.currentTimeMillis()) / 60_000L).toInt() + 1).coerceAtLeast(0) else 0
        snapshot = PlaybackSnapshot(
            trackId    = c.currentMediaItem?.mediaId,
            isPlaying  = c.isPlaying,
            positionMs = c.currentPosition.coerceAtLeast(0),
            durationMs = c.duration.takeIf { it > 0 } ?: 0L,
            shuffle    = c.shuffleModeEnabled,
            repeatOne  = c.repeatMode == Player.REPEAT_MODE_ONE,
            sleepMinutesLeft = left,
        )
        main.removeCallbacks(ticker)
        if (snapshot.isPlaying || sleepAt > 0) main.postDelayed(ticker, 250)
    }

    // ------------------------------------------------------------------------
    // Commands
    // ------------------------------------------------------------------------
    fun play(queue: List<Track>, startIndex: Int, positionMs: Long) = run { c ->
        val items = queue.mapNotNull { it.toMediaItem() }
        if (items.isEmpty()) return@run
        val start = queue.getOrNull(startIndex)?.id?.let { id -> items.indexOfFirst { it.mediaId == id } }?.coerceAtLeast(0) ?: 0
        c.volume = 1f
        c.setMediaItems(items, start, positionMs)
        c.prepare()
        c.play()
        log("play ${items.size} items from #$start (${items[start].mediaMetadata.title})")
    }

    fun pause()  = run { it.pause() }
    fun resume() = run { c -> if (c.playbackState == Player.STATE_IDLE) c.prepare(); c.play() }
    fun seekTo(positionMs: Long) = run { it.seekTo(positionMs) }
    fun next()     = run { if (it.hasNextMediaItem()) it.seekToNextMediaItem() else it.seekTo(0, 0) }
    fun previous() = run { c -> if (c.currentPosition > 3000 || !c.hasPreviousMediaItem()) c.seekTo(0) else c.seekToPreviousMediaItem() }
    fun setShuffle(on: Boolean)   = run { it.shuffleModeEnabled = on }
    fun setRepeatOne(on: Boolean) = run { it.repeatMode = if (on) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_ALL }

    fun setSleepTimer(minutes: Int) {
        main.post {
            sleepAt = if (minutes <= 0) 0 else System.currentTimeMillis() + minutes * 60_000L
            refresh()
        }
    }

    // ------------------------------------------------------------------------
    // Preview рингтону: fade гучністю плеєра + стоп рівно на кінці фрагмента
    // ------------------------------------------------------------------------
    private var fade: IntArray? = null
    private var fadeLogTick = 0      // startMs, endMs, fadeInMs, fadeOutMs
    private val fadeTicker = object : Runnable {
        override fun run() {
            val f = fade ?: return
            val c = controller ?: return
            val pos = c.currentPosition
            if (pos >= f[1]) {                       // кінець фрагмента
                c.pause(); c.volume = 1f; fade = null; refresh(); return
            }
            var g = 1f
            val fromStart = (pos - f[0]).coerceAtLeast(0)
            val toEnd = (f[1] - pos).coerceAtLeast(0)
            if (f[2] > 0 && fromStart < f[2]) g = fromStart.toFloat() / f[2]
            if (f[3] > 0 && toEnd < f[3]) g = minOf(g, toEnd.toFloat() / f[3])
            c.volume = g.coerceIn(0f, 1f)
            if (++fadeLogTick % 10 == 0) log("preview fade pos=$pos gain=${"%.2f".format(c.volume)}")
            main.postDelayed(this, 30)
        }
    }

    fun previewFade(startMs: Long, endMs: Long, fadeInMs: Long, fadeOutMs: Long) = main.post {
        fade = intArrayOf(startMs.toInt(), endMs.toInt(), fadeInMs.toInt(), fadeOutMs.toInt())
        controller?.volume = if (fadeInMs > 0) 0f else 1f
        main.removeCallbacks(fadeTicker)
        main.post(fadeTicker)
    }

    fun clearPreviewFade() = main.post {
        fade = null
        main.removeCallbacks(fadeTicker)
        controller?.volume = 1f
    }

    fun release() = main.post {
        main.removeCallbacks(ticker)
        controller?.removeListener(listener)
        future?.let { MediaController.releaseFuture(it) }
        future = null
        controller = null
    }

    private fun Track.toMediaItem(): MediaItem? {
        val uri = localUri ?: return null
        val meta = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .apply { coverPath?.let { setArtworkUri(Uri.fromFile(File(it))) } }
            .build()
        return MediaItem.Builder().setMediaId(id).setUri(uri).setMediaMetadata(meta).build()
    }
}
