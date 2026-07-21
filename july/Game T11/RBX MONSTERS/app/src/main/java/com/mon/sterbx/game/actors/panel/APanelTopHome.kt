package com.mon.sterbx.game.actors.panel

import com.mon.sterbx.game.actors.button.base.AButtonAnim
import com.mon.sterbx.game.actors.button.base.AButtonStyles
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.screens.SettingsScreen
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame

class APanelTopHome(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg = APanelRBX(screen)
    private val aSettBtn = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)

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
        aLogoImg.setSize(186f, 47f)
        add(aLogoImg) { startToStart(); centerY() }
    }

    private fun addSettBtn() {
        aSettBtn.setSize(55f, 41f)
        add(aSettBtn) { endToEnd(); centerY() }
        aSettBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) } }
    }

}