package com.skindustry.skinly.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.skindustry.skinly.game.actors.button.base.AButtonAnim
import com.skindustry.skinly.game.actors.button.base.AButtonStyles
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.screens.SettingsScreen
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter
import com.skindustry.skinly.game.utils.gdxGame

class APanelTopSettings(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters("Settings")
        .setSize(36)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTitleLbl = Label("Settings", FontFactory.create(screen, parameter, screen.fontGenerator_Black, Color.BLACK))
    private val aCloseBtn = AButtonAnim(screen, AButtonStyles.Anim.CLOSE)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addCloseBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLogoImg() {
        aTitleLbl.setSize(157f, 37f)
        add(aTitleLbl) { startToStart(margin = 16f); topToTop(margin = 8f) }
    }

    private fun addCloseBtn() {
        aCloseBtn.setSize(36f, 36f)
        add(aCloseBtn) { endToEnd(margin = 16f); topToTop(margin = 8f) }

        aCloseBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.back() } }
    }

}