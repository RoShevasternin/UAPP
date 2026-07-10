package com.rbuxrds.counter.game.utils.screenState

class ScreenStateMachine {
    private var current: ScreenState? = null

    fun setState(new: ScreenState?) {
        current?.onExit()
        current = new
        current?.onEnter()
    }

    fun getCurrentState() = current
}