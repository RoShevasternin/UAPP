package com.redwave.downloader.game.actors.layout

import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen

// ─────────────────────────────────────────────────────────────────────────────
// ADyn — секція, що перебудовується, коли змінився її ключ (model.version,
// вибраний настрій…). build() створює дітей і повертає висоту; 0 → секція
// ховається (AColumn не рахує для неї gap).
// ─────────────────────────────────────────────────────────────────────────────
class ADyn(
    override val screen: AdvancedScreen,
    w: Float,
    private val key: () -> Any?,
    private val build: ADyn.(w: Float) -> Float,
) : AdvancedGroup() {

    private var lastKey: Any? = NONE

    init { setSize(w, 1f) }

    override fun addActorsOnGroup() { rebuild() }

    override fun act(delta: Float) {
        if (key() != lastKey) rebuild()
        super.act(delta)
    }

    fun rebuild() {
        lastKey = key()
        disposeAndClearChildren()
        val h = build(width)
        isVisible = h > 0f
        height = if (h > 0f) h else 0.01f
    }

    private companion object { val NONE = Any() }
}
