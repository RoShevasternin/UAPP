package com.rbuxrds.counter.game.dataStore

import com.rbuxrds.counter.game.manager.AbstractDataStore
import com.rbuxrds.counter.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class DataStoreUtil<T> {
    val simpleName: String get() = this::class.java.simpleName

    abstract val coroutine: CoroutineScope
    abstract val flow     : MutableStateFlow<T>
    abstract val dataStore: AbstractDataStore.DataStoreElement<T>

    open fun initialize() {
        coroutine.launch(Dispatchers.IO) {
            dataStore.get()?.let { value -> flow.update { value } }
            log("Store $simpleName = ${flow.value}")
        }
    }

    open fun update(block: (T) -> T) {
        coroutine.launch(Dispatchers.IO) {
            flow.update { block(flow.value) }

            log("Store $simpleName update = ${flow.value}")
            dataStore.update { flow.value }
        }
    }
}
