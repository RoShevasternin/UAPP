package com.skindustry.skinly.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.skindustry.skinly.game.actors.button.base.AButtonAnim
import com.skindustry.skinly.game.actors.button.base.AButtonStyles
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter

class APanelTopHomeSelect(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(20)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackBtn  = AButtonAnim(screen, AButtonStyles.Anim.BACK)
    private val aTitleLbl = Label("", FontFactory.create(screen, parameter, screen.fontGenerator_SemiBold, Color.BLACK))

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onBack = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBackBtn()
        addTitleLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBackBtn() {
        aBackBtn.setSize(50f, 50f)
        add(aBackBtn) { startToStart(margin = 3f); topToTop(margin = -3f) }
        aBackBtn.setOnClickListener { onBack() }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(61f, 28f)
        add(aTitleLbl) { startToEnd(aBackBtn, 3f); topToTop(margin = 8f) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

}