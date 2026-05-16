package com.rbuxdrop.cougame.game.actors.panel.flip

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import com.rbuxdrop.cougame.game.utils.gdxGame

class APanelFlipResult(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(36)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCardImg = Image(gdxGame.assetsAll.FLIP_CARD_RESULT)
    private val aTextLbl = ALabel(screen, "", GameColor.purple_3D, parameter, screen.fontGenerator_Bold)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aCardImg) { fillParent() }
        addTextLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addTextLbl() {
        aTextLbl.setSize(154f, 44f)
        add(aTextLbl) { centerX(); topToTop(margin = 259f) }
        aTextLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(reward: Long) {
        aTextLbl.setText("$reward RBX")
    }



}