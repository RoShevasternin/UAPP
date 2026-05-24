package com.rbuxdrop.cougame.game.actors.panel.scratch

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.NumberFormatter
import com.rbuxdrop.cougame.game.utils.actor.setOnClickListener
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.util.log
import java.text.NumberFormat

class ADialog(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "You’ve won lucky RBX!")
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDialogImg = Image(gdxGame.assetsAll.SCRATCH_RESULT)
    private val aTextLbl   = ALabel(screen, "You’ve won 0 lucky RBX!", GameColor.gary_7F, parameter, screen.fontGenerator_Medium)
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

        addTextLbl()
        addClaimBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addTextLbl() {
        aTextLbl.setSize(312f, 24f)
        add(aTextLbl) { centerX(); topToTop(margin = 168f) }
        aTextLbl.setAlignment(Align.center)
    }

    private fun addClaimBtn() {
        addActor(aClaimBtn)
        aClaimBtn.setBounds(16f, 16f, 312f, 60f)

        aClaimBtn.setOnClickListener { onClaim() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(reward: Long) {
        aTextLbl.setText("You’ve won $reward lucky RBX!")
    }

}