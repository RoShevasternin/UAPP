package com.rbuxrds.counter.game.actors.label

import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.actors.vfx.AMask
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

class ALabelSpinning(
    override val screen: AdvancedScreen,
    var _text: String,
    private val labelStyle: Label.LabelStyle,
    private val alignment : Int   = Align.center,
    private var timeDelay : Float = TIME_DELAY,
    private var timeRoll  : Float = TIME_ROLL_CURRENT,
): AdvancedGroup() {

    companion object {
        const val SPACE_PERCENT = 10

        // seconds
        const val TIME_ROLL_CURRENT = 5f
        const val TIME_DELAY        = 2f
    }

    // Actors
    private val mask       = AMask(screen)
    private var lblCurrent = Label(_text, labelStyle)
    private var lblNext    = Label(_text, labelStyle)
    private val tmpActor   = Actor()


    // Field
    private val glyphLayout  = GlyphLayout()
    private val space get()  = (getTextWidth() / 100) * SPACE_PERCENT
    private val timeRollNext = timeRoll + ((timeRoll / 100) * SPACE_PERCENT)

    override fun addActorsOnGroup() {
        addActor(tmpActor)

        addMask()
        addLblNext()
        addLblCurrent()
    }

    private fun addMask() {
        addAndFillActor(mask)
    }

    private fun addLblNext() {
        mask.addAndFillActor(lblNext)

        lblNext.apply {
            setAlignment(Align.left)
            x = (getTextWidth() + space)
        }
    }

    private fun addLblCurrent() {
        mask.addAndFillActor(lblCurrent)
        checkIsLaunchSpin()
    }

    private fun checkIsLaunchSpin() {
        if (getTextWidth() > lblCurrent.width) {
            lblCurrent.setAlignment(Align.left)
            lblCurrent.x = 0f
            lblNext.x    = (getTextWidth() + space)

            spin()
        } else {
            lblCurrent.setAlignment(alignment)
            lblCurrent.x = 0f
            lblNext.x    = (width + space)
        }
    }

    private fun getTextWidth() = glyphLayout.run {
        setText(labelStyle.font, _text)
        width
    }


    private fun swapLabel() {
        lblCurrent = lblNext.apply { lblNext = lblCurrent }
        lblNext.x  = (getTextWidth() + space)
    }

    private fun spin() {
        tmpActor.addAction(
            Actions.forever(Actions.sequence(
                Actions.delay(timeDelay),
                Actions.run {
                    lblCurrent.addAction(Actions.moveBy(-getTextWidth(), 0f, timeRoll))
                    lblNext.addAction(Actions.moveBy(-(getTextWidth() + space), 0f, timeRollNext))
                },
                Actions.delay(timeRollNext),
                Actions.run { swapLabel() }
            ))
        )
    }

    fun setText(text: String) {
        _text = text
        lblCurrent.setText(text)
        lblNext.setText(text)

        tmpActor.clearActions()
        lblCurrent.clearActions()
        lblNext.clearActions()

        checkIsLaunchSpin()

    }

}