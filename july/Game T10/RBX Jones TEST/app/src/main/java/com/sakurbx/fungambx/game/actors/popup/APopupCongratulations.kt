package com.sakurbx.fungambx.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.button.APinkButton
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame

class APopupCongratulations(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(60)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Laila_Bold, GameColor.yellow_FACA4F)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP)
    private val aResultLbl   = Label("0 RBX", lsDef)
    private val aContinueBtn = APinkButton(screen, "CONTINUE")

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
        aResultLbl.setSize(280f, 60f)
        add(aResultLbl) { centerX(); topToTop(margin = 100f) }
        aResultLbl.setAlignment(Align.center)
        aResultLbl.setEllipsis(true)
    }

    private fun addContinue() {
        aContinueBtn.setSize(280f, 56f)
        add(aContinueBtn) { centerX(); bottomToBottom(margin = 16f) }

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

}