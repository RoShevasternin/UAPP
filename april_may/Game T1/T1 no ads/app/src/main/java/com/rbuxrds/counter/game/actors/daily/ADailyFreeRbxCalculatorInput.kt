package com.rbuxrds.counter.game.actors.daily

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.actors.button.ABlueButton
import com.rbuxrds.counter.game.actors.label.ALabel
import com.rbuxrds.counter.game.utils.GameColor
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.font.FontParameter
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.runGDX

class ADailyFreeRbxCalculatorInput(
    override val screen: AdvancedScreen,
): AdvancedGroup() {

    private val text = "Enter Number Of Days"

    private val parameter = FontParameter()
        .setCharacters(text)
        .setSize(12)
    private val parameterBig = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(96)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCosmo       = ACosmo(screen)
    private val aInputLbl    = ALabel(screen, "0", GameColor.gray_23252A, parameterBig, screen.fontGenerator_InterTight_Bold)
    private val aTextLbl     = ALabel(screen, text, GameColor.gray_5C6070, parameter, screen.fontGenerator_InterTight_Medium)
    private val aCountNowBtn = ABlueButton(screen, "Count Now")

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onInput        : (Int) -> Unit = {}
    var onCountNowClick: () -> Unit = {}

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    var resultInput = 0
        private set

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addCosmo()
        addInputLbl()
        addTextLbl()
        addCountNowBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun Group.addCosmo() {
        addActor(aCosmo)
        aCosmo.setBounds(66f, 416f, 212f, 225f)
    }

    private fun Group.addInputLbl() {
        addActor(aInputLbl)
        aInputLbl.setBounds(108f, 234f, 128f, 116f)
        aInputLbl.setAlignment(Align.center)

        aInputLbl.setOnClickListener {
            showKeyboard()
        }
    }

    private fun Group.addTextLbl() {
        addActor(aTextLbl)
        aTextLbl.setBounds(108f, 201f, 128f, 20f)
        aTextLbl.setAlignment(Align.center)
    }

    private fun Group.addCountNowBtn() {
        addActor(aCountNowBtn)
        aCountNowBtn.setBounds(0f, 0f, 344f, 56f)

        aCountNowBtn.onClick = { onCountNowClick() }
    }

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------

    private fun showKeyboard() {
        val activity = gdxGame.activity
        activity.showInput(hint = text) { result ->
            resultInput = result.toIntOrNull()?.coerceIn(0, 100) ?: 0

            runGDX {
                // Якщо вже був результат — завжди 0 (lose)
                onInput(resultInput)
                aInputLbl.setText(resultInput.toString())
                aInputLbl.getLabelOrNull()?.style?.fontColor = Color.WHITE
            }
        }
    }

}