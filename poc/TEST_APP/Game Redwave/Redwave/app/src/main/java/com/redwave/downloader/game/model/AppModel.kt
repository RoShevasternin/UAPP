package com.redwave.downloader.game.model

import com.redwave.downloader.core.model.AppState
import com.redwave.downloader.core.model.AppStateCodec
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.core.model.TrackKind
import com.redwave.downloader.game.manager.DataStoreManager
import com.redwave.downloader.game.utils.runGDX
import com.redwave.downloader.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

// ═════════════════════════════════════════════════════════════════════════════
//  AppModel — єдине джерело стану (бібліотека, черга, EQ, онбординг).
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
                log("state loaded: onboarded=${loaded.onboarded}, library=${loaded.library.size}, downloads=${loaded.downloads.size}")
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
    val songs: List<Track>    get() = state.library.filter { it.kind == TrackKind.SONG }.sortedByDescending { it.addedAt }
    val podcasts: List<Track> get() = state.library.filter { it.kind == TrackKind.PODCAST }.sortedByDescending { it.addedAt }

    fun track(id: String?): Track? = id?.let { i -> state.library.firstOrNull { it.id == i } }

    fun isFavorite(id: String) = id in state.favorites

    fun toggleFavorite(id: String) = update { s ->
        s.copy(favorites = if (id in s.favorites) s.favorites - id else s.favorites + id)
    }

    val totalBytes: Long get() = state.library.sumOf { it.sizeBytes }

    /** Абсолютний шлях файлу треку (file:// → /storage/…), для порівняння з вмістом теки. */
    fun pathOf(t: Track): String? = t.localUri?.let { runCatching { java.net.URI(it).path }.getOrNull() }
}
