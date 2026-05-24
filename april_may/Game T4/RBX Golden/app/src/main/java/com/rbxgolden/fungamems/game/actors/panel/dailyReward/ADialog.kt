package com.rbxgolden.fungamems.game.actors.panel.dailyReward

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import com.rbxgolden.fungamems.game.utils.gdxGame

class ADialog(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setBorder(2f, GameColor.orange_FE)
        .setSize(64)
    private val parameter14 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "You have received 200 RBX!")
        .setSize(14)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDialogImg = Image(gdxGame.assetsAll.DAILY_RESULT)
    private val aRewardLbl = Label("200", FontFactory.create(screen, parameter, screen.fontGenerator_Bold, GameColor.yellow_FF))
    private val aTextLbl   = Label("You have received 200 RBX!", FontFactory.create(screen, parameter14, screen.fontGenerator_Medium, Color.WHITE))
    private val aClaimBtn  = Actor()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClaim = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aDialogImg) { fillParent() }

        addRewardLbl()
        addTextLbl()
        addClaimBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addRewardLbl() {
        aRewardLbl.setSize(129f, 72f)
        add(aRewardLbl) { startToStart(margin = 135f); topToTop(margin = 72f) }
    }

    private fun addTextLbl() {
        aTextLbl.setSize(312f, 24f)
        add(aTextLbl) { centerX(); topToTop(margin = 164f) }
        aTextLbl.setAlignment(Align.center)
    }

    private fun addClaimBtn() {
        addActor(aClaimBtn)
        aClaimBtn.setBounds(16f, 20f, 284f, 56f)

        aClaimBtn.setOnClickListener { onClaim() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(reward: Long) {
        aRewardLbl.setText(reward.toString())
        aTextLbl.setText("You have received $reward RBX!")
    }

}