package com.rbxtreasure.fungamers.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.button.base.AButtonAnim
import com.rbxtreasure.fungamers.game.actors.button.base.AButtonStyles
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.screens.SettingsScreen
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontFactory
import com.rbxtreasure.fungamers.game.utils.font.FontParameter
import com.rbxtreasure.fungamers.game.utils.gdxGame

class APanelTop(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(18)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackBtn  = AButtonAnim(screen, AButtonStyles.Anim.BACK)
    private val aTitleLbl = Label("", FontFactory.create(screen, parameter, screen.fontGenerator_Anton_Regular, GameColor.beige_C9B797))

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
        aBackBtn.setSize(50f, 50f)
        add(aBackBtn) { startToStart(margin = 3f); centerY() }
        aBackBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(103f, 24f)
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