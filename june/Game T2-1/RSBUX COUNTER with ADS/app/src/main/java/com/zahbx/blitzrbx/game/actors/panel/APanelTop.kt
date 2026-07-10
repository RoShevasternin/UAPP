package com.zahbx.blitzrbx.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.zahbx.blitzrbx.game.actors.button.base.AButtonAnim
import com.zahbx.blitzrbx.game.actors.button.base.AButtonStyles
import com.zahbx.blitzrbx.game.actors.label.ALabel
import com.zahbx.blitzrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen
import com.zahbx.blitzrbx.game.utils.font.FontParameter

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
        aBackBtn.setSize(39f, 39f)
        add(aBackBtn) {
            startToStart(margin = 9f)
            bottomToBottom(margin = 9f)
        }

        aBackBtn.setOnClickListener { onBack() }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(130f, 24f)
        add(aTitleLbl) { center() }
        aTitleLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}