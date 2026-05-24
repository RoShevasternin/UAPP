package com.bossrbx.rbxcalculator.game.actors.panel.flipCard

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

class APanelCongratulations(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "You’ve won lucky RBX!")
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTextImg   = Image(gdxGame.assetsAll.TEXT_FLIP_CONGRATULATIONS)
    private val aRewardLbl = Label("You’ve won 0 lucky RBX!", FontFactory.create(screen, parameter, screen.fontGenerator_Light, GameColor.gray_808080))
    private val aBlueBtn   = ABlueButton(screen, "Good!")

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onGood = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addText()
        addRewardLbl()
        addBlueBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addText() {
        aTextImg.setSize(344f, 96f)
        add(aTextImg) { centerX(); topToTop() }
    }

    private fun AConstraintLayout.addRewardLbl() {
        aRewardLbl.setSize(194f, 24f)
        add(aRewardLbl) { centerX(); bottomToBottom(aTextImg, 16f) }

        aRewardLbl.setAlignment(Align.center)
    }

    private fun AConstraintLayout.addBlueBtn() {
        aBlueBtn.setSize(344f, 64f)
        add(aBlueBtn) { centerX(); topToBottom(aTextImg, 16f) }

        aBlueBtn.setOnClickListener { onGood() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setReward(reward: Int) {
        aRewardLbl.setText("You’ve won $reward lucky RBX!")
    }

}