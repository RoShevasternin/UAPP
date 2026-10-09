package com.driftglass.home.game.actors.ui

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.driftglass.home.game.actors.button.base.AButtonBase
import com.driftglass.home.game.utils.advanced.AdvancedScreen

// ─────────────────────────────────────────────────────────────────────────────
// ATap — натискна група: дитячі актори = вигляд, сама група = зона тапу.
// Натиск — scale 0.97 за 0.08 с, відпускання — назад (SCREENS.md, «Анімації»).
// Вміст додається в addContent() (викликається рівно один раз, коли є розмір).
// ─────────────────────────────────────────────────────────────────────────────
open class ATap(
    override val screen: AdvancedScreen,
    var pressScale: Float = 0.97f,
) : AButtonBase(screen) {

    private var scaleAction: Action? = null

    open fun addContent() {}

    override fun addActorsOnGroup() {
        addContent()
        super.addActorsOnGroup()
    }

    override fun press() = scale(pressScale, 0.08f)
    override fun unpress() = scale(1f, 0.14f)

    private fun scale(to: Float, time: Float) {
        scaleAction?.let { removeAction(it) }
        scaleAction = Actions.scaleTo(to, to, time, Interpolation.pow2Out).also { addAction(it) }
    }

    override fun disable() { touchable = Touchable.disabled; color.a = 0.45f }
    override fun enable()  { touchable = Touchable.enabled;  color.a = 1f }

    fun onClick(block: () -> Unit): ATap { setOnClickListener { block() }; return this }
}
