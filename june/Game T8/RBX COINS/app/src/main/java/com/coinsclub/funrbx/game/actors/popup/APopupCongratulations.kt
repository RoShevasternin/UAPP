package com.coinsclub.funrbx.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.button.AYellowButton
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame

class APopupCongratulations(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX") // CARD PICKS
        .setSize(50)
        .setBorderAndShadow(border = 5f, shadowX = 7, shadowY = 4)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_LuckiestGuy_Regular, GameColor.yellow_DFA008)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP)
    private val aResultLbl   = Label("0 RBX", lsDef)
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
        aResultLbl.setSize(188f, 50f)
        add(aResultLbl) { centerX(); topToTop(margin = 86f) }
        aResultLbl.setAlignment(Align.center)
    }

    private fun addContinue() {
        aContinueBtn.setSize(307f, 57f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 18f) }

        aContinueBtn.setOnClickListener {
            gdxGame.soundUtil.apply { play(REWARD) }
            onContinue()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(reward: Long) {
        aResultLbl.setText("$reward RBX")
    }

    fun setCards(cards: Int) {
        //aResultLbl.setText("$cards CARD PICKS")
    }

}