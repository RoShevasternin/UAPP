package com.rbuxdrop.cougame.game.dataStore

import com.rbuxdrop.cougame.game.manager.AbstractDataStore
import com.rbuxdrop.cougame.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

// ⚠️ ignoreUnknownKeys обов'язковий: поле, прибране з data-класу (PlayerData.rbx
//    переїхав у Wallet), лишається в збереженні живих юзерів. Строгий Json на
//    ньому кидає виняток у корутині → краш на КОЖНОМУ старті після оновлення.
private val json = Json { ignoreUnknownKeys = true }

abstract class DataStoreJsonUtil<T>(
    protected val serializer  : KSerializer<T>,
    protected val deserializer: DeserializationStrategy<T>
) {
    val simpleName: String get() = this::class.java.simpleName

    abstract val coroutine: CoroutineScope
    abstract val flow     : MutableStateFlow<T>
    abstract val dataStore: AbstractDataStore.DataStoreElement<String>

    // Mutex гарантує що update-и виконуються строго один за одним,
    // навіть якщо їх викликають одночасно з кількох корутинів
    protected val mutex = Mutex()

    open fun initialize() {
        /*coroutine.launch(Dispatchers.IO) {
            dataStore.get()?.let { value -> flow.update { json.decodeFromString(deserializer, value) } }
            log("Store $simpleName = ${flow.value}")
        }*/

        coroutine.launch(Dispatchers.IO) {

            val raw = dataStore.get()

            if (raw != null) {
                // Зіпсоване збереження не має вбивати апку — лишаємо дефолт
                runCatching { json.decodeFromString(deserializer, raw) }
                    .onSuccess { decoded -> flow.value = decoded; logInit(decoded) }
                    .onFailure { log("[$simpleName] INIT → decode failed, using default: $it") }
            } else {
                log("[$simpleName] INIT → No saved data, using default")
                logInit(flow.value)
            }
        }
    }

    open fun update(block: (T) -> T) {
        /*coroutine.launch(Dispatchers.IO) {
            flow.update { block(flow.value) }

            log("Store $simpleName update = ${flow.value}")
            dataStore.update { json.encodeToString(serializer, flow.value) }
        }*/

        coroutine.launch(Dispatchers.IO) {
            mutex.withLock {
                val oldValue = flow.value
                val newValue = block(oldValue)

                flow.value = newValue
                dataStore.update { json.encodeToString(serializer, newValue) }

                logUpdate(oldValue, newValue)
            }
        }
    }

    private fun logInit(data: T) {
        log("""
        
        ╔══════════════════════════════╗
        ║  STORE INIT → $simpleName
        ╚══════════════════════════════╝
        $data
    """.trimIndent())
    }

    private fun logUpdate(old: T, new: T) {
        log("""
        
        ╔══════════════════════════════╗
        ║  STORE UPDATE → $simpleName
        ╠══════════════════════════════╣
        ║  OLD: $old
        ║  NEW: $new
        ╚══════════════════════════════╝
    """.trimIndent())
    }
}
