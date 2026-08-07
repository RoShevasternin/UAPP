package com.racing.funtols.game.actors.panel.plate

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.advanced.AdvancedScreen

// ------------------------------------------------------------------------
// APlatePiece — одна половинка номерної таблички
// ------------------------------------------------------------------------
// Уміє тільки одне: їздити за пальцем. Куди її можна кинути і що з того
// вийде — вирішує APanelPlate через onDrop.
//
//   touchDown    → запам'ятали точку хвата, підняли половинку над іншими
//   touchDragged → їдемо за пальцем
//   touchUp      → віддали рішення панелі (onDrop)
class APlatePiece(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var canDrag: () -> Boolean = { true } // панель забороняє драг під час анімації
    var onDrop : () -> Unit    = {}

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPlateImg = Image()

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    val centerX get() = x + width  / 2f
    val centerY get() = y + height / 2f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPlateImg) { fillParent() }
        addDragListener()
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Яку половинку малювати (регіон з listPlate) */
    fun setRegion(region: TextureRegion) {
        aPlateImg.drawable = TextureRegionDrawable(region)
    }

    // ------------------------------------------------------------------------
    // Drag
    // ------------------------------------------------------------------------
    private fun addDragListener() {
        addListener(object : InputListener() {

            private var isDragging = false
            private var grabX = 0f
            private var grabY = 0f

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (pointer != 0)  return false // тільки перший палець
                if (!canDrag())    return false // йде анімація — не чіпаємо

                isDragging = true
                grabX = x
                grabY = y
                toFront() // половинка їде ПОВЕРХ сусідів, а не під ними
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                if (!isDragging) return
                // x/y — локальні координати відносно половинки,
                // тому зсув = поточна точка мінус точка хвата
                moveBy(x - grabX, y - grabY)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (!isDragging) return
                isDragging = false
                onDrop()
            }
        })
    }
}