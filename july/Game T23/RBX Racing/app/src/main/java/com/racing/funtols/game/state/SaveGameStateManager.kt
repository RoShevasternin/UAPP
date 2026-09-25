package com.racing.funtols.game.state

import com.racing.funtols.game.data.PlayerData
import com.racing.funtols.game.manager.DataStoreManager
import com.racing.funtols.util.log
import com.racing.funtols.game.state.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

// ⚠️ ignoreUnknownKeys обов'язковий: PlayerData.rbx прибрано (баланс у Wallet),
//    але поле лишається в збереженні живих юзерів. Строгий Json кидає виняток у
//    корутині → краш на КОЖНОМУ старті після оновлення.
private val playerJson = Json { ignoreUnknownKeys = true }

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
            // Зіпсоване збереження не має вбивати апку — беремо дефолт
            val data = raw
                ?.let { runCatching { playerJson.decodeFromString(PlayerData.serializer(), it) }
                    .onFailure { e -> log("SaveGameStateManager: decode failed, using default: $e") }
                    .getOrNull() }
                ?: PlayerData() // ← дефолтні значення з PlayerData
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
                val json = playerJson.encodeToString(PlayerData.serializer(), data)
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
                    val json = playerJson.encodeToString(PlayerData.serializer(), data)
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
        ║  Daily Day  : ${data.dailyRewardDay}
        ║  Daily Time : ${data.dailyRewardTime}
        ╚══════════════════════════════╝
    """.trimIndent())
    }
}