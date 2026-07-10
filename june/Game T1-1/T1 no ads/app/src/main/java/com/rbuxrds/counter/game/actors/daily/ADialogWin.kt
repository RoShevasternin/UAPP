package com.rbuxrds.counter.game.actors.daily

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.actors.button.base.AButtonStyles
import com.rbuxrds.counter.game.actors.button.base.AButtonTexture
import com.rbuxrds.counter.game.actors.label.ALabel
import com.rbuxrds.counter.game.utils.actor.addActors
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.actor.setOrigin
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.font.FontParameter
import com.rbuxrds.counter.game.utils.gdxGame
import com.rbuxrds.counter.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ADialogWin(override val screen: AdvancedScreen) : AdvancedGroup() {

    companion object {
        val VALUE = MutableStateFlow(0)
    }

    var onOk = {}

    val textSub = "You Won RBX!"

    private val parameterResult = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(48)
    private val parameterSub = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + textSub)
        .setSize(14)

    private val aPanelImg  = Image(gdxGame.assetsAll.POPUP_DAILY_FREE_DONE)
    private val aNumberLbl = ALabel(screen, "0", Color.WHITE, parameterResult, screen.fontGenerator_InterTight_Bold)
    private val aSubLbl    = ALabel(screen, "You Won ${VALUE.value} RBX!", Color.WHITE, parameterSub, screen.fontGenerator_InterTight_Medium)
    private val aOkBtn     = AButtonTexture(screen, AButtonStyles.OK)

    override fun addActorsOnGroup() {
        addAndFillActor(aPanelImg)
        addActor(aNumberLbl)
        addActor(aSubLbl)
        addActor(aOkBtn)

        aNumberLbl.setBounds(134f, 222f, 48f, 64f)
        aNumberLbl.setAlignment(Align.center)

        aSubLbl.setBounds(102f, 100f, 113f, 22f)
        aSubLbl.setAlignment(Align.center)

        aOkBtn.setBounds(16f, 20f, 284f, 56f)
        aOkBtn.setOnClickListener { onOk() }

        coroutine?.launch {
            VALUE.collect { value ->
                runGDX {
                    aNumberLbl.setText(value.toString())
                    aSubLbl.setText("You Won $value RBX!")
                }
            }
        }
    }


}