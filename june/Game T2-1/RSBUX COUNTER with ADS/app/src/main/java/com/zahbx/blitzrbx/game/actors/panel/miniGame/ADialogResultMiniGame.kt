package com.zahbx.blitzrbx.game.actors.panel.miniGame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.zahbx.blitzrbx.game.actors.label.ALabel
import com.zahbx.blitzrbx.game.utils.actor.addActors
import com.zahbx.blitzrbx.game.utils.actor.setOnClickListener
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedGroup
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.font.FontParameter
import com.zahbx.blitzrbx.game.utils.gdxGame

class ADialogResultMiniGame(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aResultImg      = Image(gdxGame.assetsAll.RESULT_MINI_GAME)
    private val aCollectLbl     = ALabel(screen, "You collect 0 RBX!", Color.WHITE, parameter, screen.fontGenerator_Medium)
    private val aClaimRewardBtn = Actor()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClaimReward = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aResultImg)
        addActors(aCollectLbl, aClaimRewardBtn)
        aCollectLbl.setBounds(12f, 100f, 320f, 22f)
        aClaimRewardBtn.setBounds(12f, 24f, 320f, 60f)

        aCollectLbl.setAlignment(Align.center)
        aClaimRewardBtn.setOnClickListener { onClaimReward() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setResult(value: Int) {
        aCollectLbl.setText("You collect $value RBX!")
    }

}