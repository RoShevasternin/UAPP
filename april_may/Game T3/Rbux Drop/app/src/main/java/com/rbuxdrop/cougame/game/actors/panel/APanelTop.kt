package com.rbuxdrop.cougame.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.button.base.AButtonAnim
import com.rbuxdrop.cougame.game.actors.button.base.AButtonStyles
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter

class APanelTop(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackBtn  = AButtonAnim(screen, AButtonStyles.Anim.BACK)
    private val aTitleLbl = ALabel(screen, "", Color.WHITE, parameter, screen.fontGenerator_Medium)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onBack = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addTitleLbl()
        addBackBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBackBtn() {
        aBackBtn.setSize(42f, 42f)
        add(aBackBtn) { startToStart(margin = 7f); bottomToBottom(margin = 7f) }
        aBackBtn.setOnClickListener { onBack() }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(130f, 24f)
        add(aTitleLbl) { startToEnd(aBackBtn, 7f); centerY() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}