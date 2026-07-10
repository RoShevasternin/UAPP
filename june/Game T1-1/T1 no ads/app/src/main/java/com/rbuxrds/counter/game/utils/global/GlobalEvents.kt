package com.rbuxrds.counter.game.utils.global

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object GlobalEvents {

    enum class EventType {
        END_FLY_COIN, END_FLY_XP,
        TUTORIAL_STEP_CHANGED, TUTORIAL_CUBE_POSITION_CHANGED,
    }

    private val _events = MutableSharedFlow<EventType>(extraBufferCapacity = 16)
    val events = _events.asSharedFlow()

    fun emit(type: EventType) {
        _events.tryEmit(type)
    }
}