package com.rbxrush.rushrbx.game.utils.screenState

// ─── Context ─────────────────────────────────────────────────────────────────

interface ScreenContext {
    fun setState(state: ScreenState)
    fun pushState(state: ScreenState)
    fun popState()
    fun dismiss()
    fun getCurrentState(): ScreenState?
}

// ─── ScreenState ─────────────────────────────────────────────────────────────

abstract class ScreenState(protected val context: ScreenContext) {
    open fun onEnter() {}
    open fun onExit()  {}
}

// ─── ScreenStateMachine ──────────────────────────────────────────────────────

class ScreenStateMachine : ScreenContext {

    private var current: ScreenState? = null
    private val stack = ArrayDeque<ScreenState>()

    override fun setState(state: ScreenState) {
        exit()
        stack.clear()
        enter(state)
    }

    override fun pushState(state: ScreenState) {
        current?.let { stack.addLast(it) }
        enter(state)
    }

    override fun popState() {
        exit()
        val prev = stack.removeLastOrNull()
        if (prev != null) enter(prev) else current = null
    }

    override fun dismiss() {
        current?.onExit()
        current = stack.removeLastOrNull()
    }

    override fun getCurrentState() = current

    private fun enter(state: ScreenState) {
        current = state
        state.onEnter()
    }

    private fun exit() {
        current?.onExit()
    }
}