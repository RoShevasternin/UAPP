package com.selftest.mindora.game.state

import com.selftest.mindora.game.data.PlayerData
import com.selftest.mindora.game.data.AppJson
import com.selftest.mindora.game.data.decodeOrDefault
import com.selftest.mindora.game.data.PlayerDataMigration
import com.selftest.mindora.game.manager.DataStoreManager
import com.selftest.mindora.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

class SaveGameStateManager(
    private val gameState  : GameState,
    private val scope      : CoroutineScope
) {

    private val dataStore  = DataStoreManager.Player
    private val mutex      = Mutex()
    private var autoSaveJob: Job? = null

    // ------------------------------------------------------------------------
    // Load — при старті гри
    // ------------------------------------------------------------------------

    fun load() {
        scope.launch(Dispatchers.IO) {
            val raw = dataStore.get()
            // Безпечне декодування: несумісний/пошкоджений save → default (без краху)
            val decoded = decodeOrDefault(
                deserializer = PlayerData.serializer(),
                raw          = raw,
                default      = PlayerData(),
                tag          = "SaveGameStateManager",
            )
            // Міграція між версіями схеми (за потреби)
            val data = PlayerDataMigration.migrate(decoded)

            gameState.loadFrom(data)
            logState("GAME STATE LOADED", data)
        }
    }

    // ------------------------------------------------------------------------
    // Save — при паузі або вручну
    // ------------------------------------------------------------------------

    fun save() {
        scope.launch(Dispatchers.IO) {
            mutex.withLock {
                val data = gameState.toPlayerData()
                val json = AppJson.encodeToString(PlayerData.serializer(), data)
                dataStore.update { json }
                logState("GAME STATE SAVED", data)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Auto save — запускати в onCreate, зупиняти в onDestroy
    // ------------------------------------------------------------------------

    fun startAutoSave(intervalSec: Int = 30) {
        autoSaveJob?.cancel()
        autoSaveJob = scope.launch(Dispatchers.IO) {
            while (true) {
                delay((intervalSec * 1000L).milliseconds)
                mutex.withLock {
                    val data = gameState.toPlayerData()
                    val json = AppJson.encodeToString(PlayerData.serializer(), data)
                    dataStore.update { json }
                    logState("GAME STATE AUTO-SAVED", data)
                }
            }
        }
    }

    fun stopAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = null
    }

    // ------------------------------------------------------------------------
    // Log
    // ------------------------------------------------------------------------
    private fun logState(title: String, data: PlayerData) {
        log("""

    ╔════════════════════════════════════════════╗
    ║  $title
    ╠════════════════════════════════════════════╣
    ║  Lumens        : ${data.lumens}
    ╟────────────────────────────────────────────╢
    ║  Daily Day     : ${data.dailyRewardDay}
    ║  Daily Time    : ${data.dailyRewardTime}
    ║  Daily Streak  : ${data.dailyStreak}
    ╟────────────────────────────────────────────╢
    ║  Tests         : ${data.testResults.size}/5  ${data.testResults.keys.joinToString(", ").ifEmpty { "—" }}
    ║  Portrait      : ${data.portraitTitleId ?: "—"}
    ╟────────────────────────────────────────────╢
    ║  isFirstOpen   : ${data.isFirstOpen}
    ╚════════════════════════════════════════════╝
    """.trimIndent())
    }
}