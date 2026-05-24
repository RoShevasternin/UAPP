package com.bossrbx.rbxcalculator.game.actors.panel.flipCard

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedGroup
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import kotlin.math.abs

class ACircleCard(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCircleCardImg = Image(gdxGame.assetsAll.CIRCLE_CARD)

    // ------------------------------------------------------------------------
    // Inertia
    // ------------------------------------------------------------------------
    private var velocity    = 0f
    private val friction    = 0.92f  // 0..1 — чим менше тим швидше зупиняється
    private val minVelocity = 0.1f

    private var prevAngle  = 0f
    private var isDragging = false

    // Колбек при зміні кута
    var onRotationChanged: ((rotation: Float) -> Unit)? = null

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addCircleCardImg()
        addRotationListener()
    }

    override fun act(delta: Float) {
        super.act(delta)

        // Інерція після відпускання
        if (!isDragging && abs(velocity) > minVelocity) {
            aCircleCardImg.rotation += velocity
            velocity *= friction
            onRotationChanged?.invoke(aCircleCardImg.rotation)
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addCircleCardImg() {
        addAndFillActor(aCircleCardImg)
        aCircleCardImg.setOrigin(Align.center)
        // Вимикаємо touch на image — слухаємо на групі
        aCircleCardImg.touchable = Touchable.disabled
    }

    // ------------------------------------------------------------------------
    // Rotation listener — на групі
    // ------------------------------------------------------------------------
    private fun addRotationListener() {
        addListener(object : InputListener() {

            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                isDragging = true
                velocity   = 0f
                prevAngle  = angleToCenterDeg(x, y)
                return true
            }

            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
                val currentAngle = angleToCenterDeg(x, y)

                var delta = currentAngle - prevAngle
                if (delta >  180f) delta -= 360f
                if (delta < -180f) delta += 360f

                aCircleCardImg.rotation += delta
                velocity  = delta
                prevAngle = currentAngle

                onRotationChanged?.invoke(aCircleCardImg.rotation)
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                isDragging = false
            }
        })
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    fun animRotateToStart() {
        aCircleCardImg.addAction(Actions.rotateTo(450f, 1.3f, Interpolation.swing))
    }

    // ------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------
    private fun angleToCenterDeg(x: Float, y: Float): Float {
        val cx = width  / 2f
        val cy = height / 2f
        return MathUtils.atan2(y - cy, x - cx) * MathUtils.radiansToDegrees
    }

    val currentRotation get() = aCircleCardImg.rotation
}