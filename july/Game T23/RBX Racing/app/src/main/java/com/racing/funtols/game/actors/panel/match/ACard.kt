package com.racing.funtols.game.actors.panel.match

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.actor.disable
import com.racing.funtols.game.utils.actor.enable
import com.racing.funtols.game.utils.actor.setOnClickListener
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

// ------------------------------------------------------------------------
// ACard — одна картка
// ------------------------------------------------------------------------
// Складається з двох картинок, які лежать одна на одній:
//   aBackImg  — сорочка (RBX RACING), видно коли картка закрита
//   aFrontImg — лице (машинка / колесо / трофей...), видно коли відкрита
//
// Переворот — це стиснення по X до нуля, підміна картинки в найвужчій точці
// і розтягнення назад. Виглядає як справжній flip.
class ACard(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClick: () -> Unit = {}

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackImg  = Image(gdxGame.assetsAll.card) // сорочка
    private val aFrontImg = Image()                       // лице

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeFlip = 0.12f // половина перевороту

    var isOpened = false
        private set

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        // Потрібно для scaleX-анімації: без transform група не масштабується
        isTransform = true

        add(aBackImg)  { fillParent() }
        add(aFrontImg) { fillParent() }

        aFrontImg.isVisible = false

        setOnClickListener { onClick() }
    }

    override fun sizeChanged() {
        super.sizeChanged()
        setOrigin(Align.center) // переворот навколо центру, а не кута
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Яку картинку показувати на лиці картки */
    fun setFace(region: TextureRegion) {
        aFrontImg.drawable = TextureRegionDrawable(region)
    }

    fun open(block: () -> Unit = {}) {
        if (isOpened) { block(); return }
        isOpened = true
        flip(showFront = true, block = block)
    }

    fun close(block: () -> Unit = {}) {
        if (!isOpened) { block(); return }
        isOpened = false
        flip(showFront = false, block = block)
    }

    /** Пара вгадана — картку більше не можна натиснути */
    fun setMatched() {
        disable()
    }

    /** Повернути картку в початковий стан (нова роздача) */
    fun reset() {
        clearActions()
        isOpened            = false
        scaleX              = 1f
        aFrontImg.isVisible = false
        aBackImg.isVisible  = true
        enable()
    }

    // ------------------------------------------------------------------------
    // Animation
    // ------------------------------------------------------------------------
    private fun flip(showFront: Boolean, block: () -> Unit) {
        clearActions()
        addAction(Actions.sequence(
            Actions.scaleTo(0f, 1f, timeFlip, Interpolation.fade),
            Actions.run {
                aFrontImg.isVisible = showFront
                aBackImg.isVisible  = !showFront
            },
            Actions.scaleTo(1f, 1f, timeFlip, Interpolation.fade),
            Actions.run(block)
        ))
    }
}