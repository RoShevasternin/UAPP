package com.rbuxrds.counter.game.dataStore

import com.rbuxrds.counter.game.manager.AbstractDataStore
import com.rbuxrds.counter.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

abstract class DataStoreJsonUtil<T>(
    protected val serializer  : KSerializer<T>,
    protected val deserializer: DeserializationStrategy<T>
) {
    val simpleName: String get() = this::class.java.simpleName

    abstract val coroutine: CoroutineScope
    abstract val flow     : MutableStateFlow<T>
    abstract val dataStore: AbstractDataStore.DataStoreElement<String>

    open fun initialize() {
        /*coroutine.launch(Dispatchers.IO) {
            dataStore.get()?.let { value -> flow.update { Json.decodeFromString(deserializer, value) } }
            log("Store $simpleName = ${flow.value}")
        }*/

        coroutine.launch(Dispatchers.IO) {

            val raw = dataStore.get()

            if (raw != null) {
                val decoded = Json.decodeFromString(deserializer, raw)
                flow.value = decoded
                logInit(decoded)
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
            dataStore.update { Json.encodeToString(serializer, flow.value) }
        }*/

        coroutine.launch(Dispatchers.IO) {

            val oldValue = flow.value
            val newValue = block(oldValue)

            flow.value = newValue
            dataStore.update { Json.encodeToString(serializer, newValue) }

            logUpdate(oldValue, newValue)
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
