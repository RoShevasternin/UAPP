package com.redwave.downloader.game.controller

import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.core.model.TrackKind
import com.redwave.downloader.game.model.AppModel
import com.redwave.downloader.game.platform.PlatformBridge
import com.redwave.downloader.game.platform.PlaybackSnapshot

// ═════════════════════════════════════════════════════════════════════════════
//  PlayerController — GDX-обличчя PlaybackService. Знімок стану береться раз
//  на кадр (tick) з bridge.playbackState() — він дешевий (volatile-поле).
//  Черга: Library → усі пісні, Podcasts → лише подкасти (як у прототипі).
// ═════════════════════════════════════════════════════════════════════════════
class PlayerController(
    private val model : AppModel,
    private val bridge: PlatformBridge,
    private val toast : (String) -> Unit,
) {

    var snap: PlaybackSnapshot = PlaybackSnapshot(null, false, 0, 0, false, false)
        private set

    /** Що показати, поки сервіс ще не відповів (холодний старт): останній трек. */
    val now: Track? get() = model.track(snap.trackId) ?: model.track(model.state.lastPlayedId)

    val isPlaying get() = snap.isPlaying

    var playingFrom = Copy.Player.FROM_LIBRARY
        private set

    var sleepMinutes = 0
        private set

    private var lastTrackId: String? = null

    fun tick() {
        snap = bridge.playbackState()
        val id = snap.trackId
        if (id != null && id != lastTrackId) {
            lastTrackId = id
            if (model.state.lastPlayedId != id) model.update { it.copy(lastPlayedId = id) }
        }
    }

    // ------------------------------------------------------------------------
    // Commands
    // ------------------------------------------------------------------------
    fun play(track: Track) {
        val queue = if (track.kind == TrackKind.PODCAST) model.podcasts else model.songs
        playingFrom = if (track.kind == TrackKind.PODCAST) Copy.Player.FROM_PODCASTS else Copy.Player.FROM_LIBRARY
        val i = queue.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
        applyEq()
        bridge.play(queue, i)
    }

    fun shuffleAll() {
        val songs = model.songs
        if (songs.isEmpty()) return
        bridge.setShuffle(true)
        play(songs.random())
    }

    fun toggle() {
        when {
            snap.isPlaying -> bridge.pause()
            snap.trackId != null -> bridge.resume()
            else -> now?.let { play(it) }
        }
    }

    fun seekFraction(f: Float) {
        if (snap.durationMs > 0) bridge.seekTo((snap.durationMs * f.coerceIn(0f, 1f)).toLong())
    }

    fun next() = bridge.next()
    fun previous() = bridge.previous()
    fun toggleShuffle() = bridge.setShuffle(!snap.shuffle)
    fun toggleRepeat() = bridge.setRepeatOne(!snap.repeatOne)

    /** 0 → 15 → 30 → 60 → 0 хв (як у прототипі). */
    fun cycleSleep() {
        sleepMinutes = when (sleepMinutes) { 0 -> 15; 15 -> 30; 30 -> 60; else -> 0 }
        bridge.setSleepTimer(sleepMinutes)
        toast(Copy.Player.sleepToast(sleepMinutes))
    }

    fun applyEq() {
        val s = model.state
        bridge.setEqualizer(s.eqEnabled, s.eqBandsDb.toFloatArray())
    }

    /** Позиція 0..1 для прогрес-барів. */
    val progress: Float get() = if (snap.durationMs > 0) (snap.positionMs.toFloat() / snap.durationMs).coerceIn(0f, 1f) else 0f
}
