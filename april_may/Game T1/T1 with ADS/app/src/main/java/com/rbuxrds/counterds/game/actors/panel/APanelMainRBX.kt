package com.rbuxrds.counterds.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counterds.businesModule.economy.Wallet
import com.rbuxrds.counterds.game.actors.label.ALabel
import com.rbuxrds.counterds.game.utils.actor.addAndFillActor
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.font.FontParameter
import com.rbuxrds.counterds.game.utils.gdxGame
import com.rbuxrds.counterds.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelMainRBX(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(12)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.PANEL_MAIN_BALANCE)
    private val aTextLbl  = ALabel(screen, "0", Color.WHITE, parameter, screen.fontGenerator_InterTight_SemiBold)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aPanelImg)
        addActor(aTextLbl)
        aTextLbl.setBounds(25f, 4f, 62f, 15f)
        aTextLbl.setAlignment(Align.center)

        observeBalance()
    }

    // ------------------------------------------------------------------------
    // Balance
    // ------------------------------------------------------------------------
    private fun observeBalance() {
        coroutine?.launch {
            Wallet.balanceFlow.collect { balance ->
                runGDX { aTextLbl.setText(balance.toString()) }
            }
        }
    }

}