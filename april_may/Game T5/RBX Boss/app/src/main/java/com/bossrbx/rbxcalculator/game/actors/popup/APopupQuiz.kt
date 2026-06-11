package com.bossrbx.rbxcalculator.game.actors.popup

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.actors.button.ABlueButton
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.actor.addActors
import com.bossrbx.rbxcalculator.game.utils.actor.setFontColor
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class APopupQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter96 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+-")
        .setSize(96)
    private val parameter36 = FontParameter()
        .setCharacters("Correct! Wrong!")
        .setSize(36)
    private val parameter16 = FontParameter()
        .setCharacters("You've earned 0 RBX | You've lost 10 RBX")
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg  = Image(gdxGame.assetsAll.POPUP_QUIZ_RESULT)
    private val aRewardLbl = Label("+0", FontFactory.create(screen, parameter96, screen.fontGenerator_FIRENIGHT, GameColor.green_55BF40))
    private val aAnswerLbl = Label("Correct!", FontFactory.create(screen, parameter36, screen.fontGenerator_FIRENIGHT, Color.WHITE))
    private val aTextLbl   = Label("You've earned 0 RBX", FontFactory.create(screen, parameter16, screen.fontGenerator_Light, GameColor.gray_808080))
    private val aClaimBtn  = ABlueButton(screen, "Next Question")

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClaim = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }

        addLbls()
        addClaimBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addLbls() {
        addActors(aRewardLbl, aAnswerLbl, aTextLbl)
        aRewardLbl.setBounds(57f, 224f, 117f, 96f)
        aAnswerLbl.setBounds(109f, 148f, 126f, 44f)
        aTextLbl.setBounds(90f, 112f, 165f, 24f)

        aRewardLbl.setAlignment(Align.right)
        aAnswerLbl.setAlignment(Align.center)
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
    fun setReward(reward: Long, isWin: Boolean) {
        if (isWin) {
            aRewardLbl.setFontColor(GameColor.green_55BF40)
            aRewardLbl.setText("+$reward")

            aAnswerLbl.setText("Correct!")
            aTextLbl.setText("You've earned $reward RBX")
        } else {
            aRewardLbl.setFontColor(Color.WHITE)
            aRewardLbl.setText("-$reward")

            aAnswerLbl.setText("Wrong!")
            aTextLbl.setText("You've lost $reward RBX")
        }

    }

}