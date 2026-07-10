package com.rsbuxs.rcounbux.game.actors.panel.miniGame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rsbuxs.rcounbux.game.actors.label.ALabel
import com.rsbuxs.rcounbux.game.utils.actor.addActors
import com.rsbuxs.rcounbux.game.utils.actor.setOnClickListener
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGroup
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.font.FontParameter
import com.rsbuxs.rcounbux.game.utils.gdxGame

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