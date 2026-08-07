package com.diam.ondbit.game.state

import com.diam.ondbit.game.data.PlayerData
import com.diam.ondbit.game.manager.DataStoreManager
import com.diam.ondbit.util.log
import com.diam.ondbit.game.state.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

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
            val raw  = dataStore.get()
            val data = if (raw != null) {
                Json.decodeFromString(PlayerData.serializer(), raw)
            } else {
                PlayerData() // ← дефолтні значення з PlayerData
            }
            gameState.loadFrom(data)
            logLoad(data)
        }
    }

    // ------------------------------------------------------------------------
    // Save — при паузі або вручну
    // ------------------------------------------------------------------------

    fun save() {
        scope.launch(Dispatchers.IO) {
            mutex.withLock {
                val data = gameState.toPlayerData()
                val json = Json.encodeToString(PlayerData.serializer(), data)
                dataStore.update { json }
                logSave(data)
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
                delay(intervalSec * 1000L)
                mutex.withLock {
                    val data = gameState.toPlayerData()
                    val json = Json.encodeToString(PlayerData.serializer(), data)
                    dataStore.update { json }
                    log("SaveGameStateManager: auto-save ✓")
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

    private fun logLoad(data: PlayerData) {
        log("""
        ╔══════════════════════════════╗
        ║  GAME STATE LOADED
        ╠══════════════════════════════╣
        ║  RBX        : ${data.rbx}
        ║  Daily Day  : ${data.dailyRewardDay}
        ║  Daily Time : ${data.dailyRewardTime}
        ╚══════════════════════════════╝
    """.trimIndent())
    }

    private fun logSave(data: PlayerData) {
        log("""
        ╔══════════════════════════════╗
        ║  GAME STATE SAVED
        ╠══════════════════════════════╣
        ║  RBX        : ${data.rbx}
        ║  Daily Day  : ${data.dailyRewardDay}
        ║  Daily Time : ${data.dailyRewardTime}
        ╚══════════════════════════════╝
    """.trimIndent())
    }
}