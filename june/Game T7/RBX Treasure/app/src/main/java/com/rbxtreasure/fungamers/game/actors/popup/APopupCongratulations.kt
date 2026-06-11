package com.rbxtreasure.fungamers.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.button.AYellowButton
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontFactory
import com.rbxtreasure.fungamers.game.utils.font.FontParameter
import com.rbxtreasure.fungamers.game.utils.gdxGame

class APopupCongratulations(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX CARD PICKS")
        .setSize(48)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP)
    private val aResultLbl   = Label("Day 0", FontFactory.create(screen, parameter, screen.fontGenerator_Anton_Regular, GameColor.yellow_DDA334))
    private val aContinueBtn = AYellowButton(screen, "CONTINUE")

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onContinue = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }
        addRewardLbl()
        addContinue()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addRewardLbl() {
        aResultLbl.setSize(152f, 48f)
        add(aResultLbl) { centerX(); topToTop(margin = 86f) }
        aResultLbl.setAlignment(Align.center)
    }

    private fun addContinue() {
        aContinueBtn.setSize(280f, 51f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 16f) }

        aContinueBtn.setOnClickListener { onContinue() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(reward: Long) {
        aResultLbl.setText("$reward RBX")
    }

    fun setCards(cards: Int) {
        aResultLbl.setText("$cards CARD PICKS")
    }

}