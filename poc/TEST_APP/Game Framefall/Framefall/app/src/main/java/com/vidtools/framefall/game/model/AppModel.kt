package com.vidtools.framefall.game.model

import com.vidtools.framefall.core.model.AppState
import com.vidtools.framefall.core.model.AppStateCodec
import com.vidtools.framefall.core.model.MediaKind
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.core.model.VideoSource
import com.vidtools.framefall.game.manager.DataStoreManager
import com.vidtools.framefall.game.utils.runGDX
import com.vidtools.framefall.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

// ═════════════════════════════════════════════════════════════════════════════
//  AppModel — єдине джерело стану (бібліотека, черга, онбординг, фон Home).
//
//  Живе в GL-потоці: читати й міняти лише звідти. Кожна зміна:
//    • version++ — актори в act() порівнюють зі своїм lastVersion і
//      перемальовуються (дешевше й простіше за підписки);
//    • JSON у DataStore на IO, конфлейтом (зберігається лише останній знімок).
// ═════════════════════════════════════════════════════════════════════════════
class AppModel(private val coroutine: CoroutineScope) {

    var state = AppState()
        private set

    /** Зростає на кожну зміну стану. */
    var version = 0
        private set

    var isLoaded = false
        private set

    private val saves = Channel<String>(Channel.CONFLATED)

    init {
        coroutine.launch(Dispatchers.IO) {
            for (json in saves) runCatching { DataStoreManager.State.update { json } }
                .onFailure { log("save state: ${it.message}") }
        }
    }

    fun load(onLoaded: () -> Unit) {
        coroutine.launch(Dispatchers.IO) {
            val loaded = AppStateCodec.decodeOrDefault(runCatching { DataStoreManager.State.get() }.getOrNull())
            runGDX {
                state = loaded
                isLoaded = true
                version++
                log("state loaded: onboarded=${loaded.onboarded}, library=${loaded.library.size}, downloads=${loaded.downloads.size}, homeBg=${loaded.homeBg}")
                onLoaded()
            }
        }
    }

    fun update(block: (AppState) -> AppState) {
        state = block(state)
        version++
        saves.trySend(AppStateCodec.encode(state))
    }

    // ------------------------------------------------------------------------
    // Похідне
    // ------------------------------------------------------------------------
    /** Уся бібліотека, найновіші зверху. */
    val all: List<Video> get() = state.library.sortedByDescending { it.addedAt }
    val videos: List<Video> get() = all.filter { it.kind == MediaKind.VIDEO }
    val audio: List<Video> get() = all.filter { it.kind == MediaKind.AUDIO }
    val studio: List<Video> get() = all.filter { it.source == VideoSource.STUDIO }

    /** «Continue watching»: почали, не додивились; останні переглянуті — першими. */
    val continueWatching: List<Video> get() = state.library.filter { it.inProgress }.sortedByDescending { it.lastWatchedAt }

    fun video(id: String?): Video? = id?.let { i -> state.library.firstOrNull { it.id == i } }

    /** Відео для Studio: вибране востаннє, інакше найновіше. */
    val studioVideo: Video? get() = video(state.studioVideoId) ?: videos.firstOrNull()

    val totalBytes: Long get() = state.library.sumOf { it.sizeBytes }
    /** Скільки ще дивитись (недодивлене) — плитка «h to watch». */
    val leftToWatchMs: Long get() = videos.sumOf { (it.durationMs - it.watchedMs).coerceAtLeast(0L) }

    fun saveWatch(id: String, positionMs: Long) {
        val v = video(id) ?: return
        if (kotlin.math.abs(v.watchedMs - positionMs) < 1500 && v.lastWatchedAt > 0) return
        update { s -> s.copy(library = s.library.map { if (it.id == id) it.copy(watchedMs = positionMs, lastWatchedAt = System.currentTimeMillis()) else it }) }
    }
}
