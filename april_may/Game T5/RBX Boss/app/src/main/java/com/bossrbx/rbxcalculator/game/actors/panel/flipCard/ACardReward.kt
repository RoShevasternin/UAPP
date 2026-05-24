package com.bossrbx.rbxcalculator.game.actors.panel.flipCard

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class ACardReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+")
        .setSize(48)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCardImg    = Image(gdxGame.assetsAll.CARD_REWARD)
    private val aRewardLbl1 = Label("+0", FontFactory.create(screen, parameter, screen.fontGenerator_FIRENIGHT, Color.WHITE))
    private val aRewardLbl2 = Label("+0", FontFactory.create(screen, parameter, screen.fontGenerator_FIRENIGHT, Color.WHITE))


    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addCardImg()
        addRewardLbls()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addCardImg() {
        aCardImg.setSize(368f, 463f)
        add(aCardImg) { center() }
    }

    private fun AConstraintLayout.addRewardLbls() {
        aRewardLbl1.setSize(65f, 48f)
        add(aRewardLbl1) { startToStart(margin = 24f); topToTop(margin = 24f) }

        aRewardLbl2.setSize(65f, 48f)
        add(aRewardLbl2) { endToEnd(margin = 24f); bottomToBottom(margin = 24f) }

        aRewardLbl1.setAlignment(Align.left)
        aRewardLbl2.setAlignment(Align.right)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setReward(reward: Int) {
        aRewardLbl1.setText("+$reward")
        aRewardLbl2.setText("+$reward")
    }


}