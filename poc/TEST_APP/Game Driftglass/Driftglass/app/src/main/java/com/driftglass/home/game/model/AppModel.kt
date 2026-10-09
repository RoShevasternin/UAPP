package com.driftglass.home.game.model

import com.driftglass.home.core.logic.HomeWallpaper
import com.driftglass.home.core.model.AppState
import com.driftglass.home.core.model.AppStateCodec
import com.driftglass.home.core.model.Wallpaper
import com.driftglass.home.game.manager.DataStoreManager
import com.driftglass.home.game.utils.runGDX
import com.driftglass.home.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.Calendar

// ═════════════════════════════════════════════════════════════════════════════
//  AppModel — єдине джерело стану (як у Redwave). Живе в GL-потоці.
//  Кожна зміна: version++ (актори порівнюють у act()) + JSON у DataStore на IO (конфлейт).
// ═════════════════════════════════════════════════════════════════════════════
class AppModel(private val coroutine: CoroutineScope) {

    var state = AppState()
        private set

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
                log("state loaded: onboarded=${loaded.onboarded}, applied=${loaded.applied}, created=${loaded.created.size}")
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
    /** Поточна година 0..24 з дробом — для циклу дня. */
    fun hourNow(): Float {
        val c = Calendar.getInstance()
        return c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) / 60f
    }

    /** Що зараз має стояти на Home. */
    fun homeWallpaper(): Wallpaper = HomeWallpaper.current(state, hourNow())

    fun wallpaper(id: String?): Wallpaper? = id?.let { state.wallpaper(it) }

    fun toggleFavorite(id: String): Boolean {
        val was = state.isFavorite(id)
        update { s -> s.copy(favorites = if (was) s.favorites - id else s.favorites + id) }
        return !was
    }
}
