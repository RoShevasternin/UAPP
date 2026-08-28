package com.rbxhubpro.rohumex.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbxhubpro.rohumex.businesModule.economy.Wallet
import com.rbxhubpro.rohumex.game.actors.label.ALabel
import com.rbxhubpro.rohumex.game.utils.actor.addAndFillActor
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedGroup
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.font.FontParameter
import com.rbxhubpro.rohumex.game.utils.gdxGame
import com.rbxhubpro.rohumex.game.utils.runGDX
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
    private val aPanelImg = Image(gdxGame.assetsAll.panel_main_balance)
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