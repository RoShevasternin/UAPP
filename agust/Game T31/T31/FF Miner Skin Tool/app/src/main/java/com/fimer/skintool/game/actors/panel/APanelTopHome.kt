package com.fimer.skintool.game.actors.panel

import com.fimer.skintool.game.actors.button.base.AButtonAnim
import com.fimer.skintool.game.actors.button.base.AButtonStyles
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.screens.SettingsScreen
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame

class APanelTopHome(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelRBX = APanelRBX(screen)
    private val aSettBtn  = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelRBX()
        addSettBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelRBX() {
        aPanelRBX.setSize(105f, 32f)
        add(aPanelRBX) { startToStart(); centerY() }
    }

    private fun addSettBtn() {
        aSettBtn.setSize(32f, 32f)
        add(aSettBtn) { endToEnd(); centerY() }
        aSettBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) } }
    }

}