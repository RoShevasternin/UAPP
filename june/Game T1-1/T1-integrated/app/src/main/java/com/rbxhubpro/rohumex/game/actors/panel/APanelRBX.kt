package com.rbxhubpro.rohumex.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbxhubpro.rohumex.game.actors.label.ALabel
import com.rbxhubpro.rohumex.game.utils.actor.addAndFillActor
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedGroup
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.font.FontParameter
import com.rbxhubpro.rohumex.game.utils.gdxGame

// Панель РАЗОВОГО ВЫИГРЫША под колесом / карточкой: сколько дала эта попытка.
// Баланс тут не при чём — он живёт в шапке (APanelMainRBX, подписан на Wallet).
class APanelRBX(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(24)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.rbx_panel)
    private val aTextLbl  = ALabel(screen, "0", Color.WHITE, parameter, screen.fontGenerator_InterTight_SemiBold)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aPanelImg)
        addActor(aTextLbl)
        aTextLbl.setBounds(52f, 8f, 39f, 32f)
        aTextLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setResult(result: Int) {
        aTextLbl.setText(result.toString())
    }

}