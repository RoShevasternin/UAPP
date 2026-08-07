package com.diam.ondbit.game.actors.panel

import com.diam.ondbit.game.actors.button.base.AButtonAnim
import com.diam.ondbit.game.actors.button.base.AButtonStyles
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.screens.SettingsScreen
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame

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
        aPanelRBX.setSize(119f, 40f)
        add(aPanelRBX) { startToStart(); centerY() }
    }

    private fun addSettBtn() {
        aSettBtn.setSize(40f, 40f)
        add(aSettBtn) { endToEnd(); centerY() }
        aSettBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) } }
    }

}