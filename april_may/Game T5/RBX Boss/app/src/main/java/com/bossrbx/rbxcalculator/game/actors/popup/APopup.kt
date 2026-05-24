package com.bossrbx.rbxcalculator.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.actors.button.ABlueButton
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class APopup(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "You collect RBX!")
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg  = Image(gdxGame.assetsAll.POPUP)
    private val aTextLbl   = Label("You collect 0 RBX!", FontFactory.create(screen, parameter, screen.fontGenerator_Light, GameColor.gray_808080))
    private val aClaimBtn  = ABlueButton(screen, "Good!")

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClaim = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }

        addTextLbl()
        addClaimBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addTextLbl() {
        aTextLbl.setSize(147f, 24f)
        add(aTextLbl) { centerX(); topToTop(margin = 204f) }
        aTextLbl.setAlignment(Align.center)
    }

    private fun addClaimBtn() {
        addActor(aClaimBtn)
        aClaimBtn.setBounds(16f, 16f, 312f, 64f)

        aClaimBtn.setOnClickListener { onClaim() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(reward: Long) {
        aTextLbl.setText("You collect $reward RBX!")
    }

}