package com.diam.ondbit.game.utils.global

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object GlobalEvents {

    enum class EventType {

    }

    private val _events = MutableSharedFlow<EventType>(extraBufferCapacity = 16)
    val events = _events.asSharedFlow()

    fun emit(type: EventType) {
        _events.tryEmit(type)
    }
}