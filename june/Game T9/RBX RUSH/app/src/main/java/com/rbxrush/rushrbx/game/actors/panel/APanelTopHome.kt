package com.rbxrush.rushrbx.game.actors.panel

import com.rbxrush.rushrbx.game.actors.button.base.AButtonAnim
import com.rbxrush.rushrbx.game.actors.button.base.AButtonStyles
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.screens.SettingsScreen
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame

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
        aLogoImg.setSize(93f, 40f)
        add(aLogoImg) { startToStart(margin = 16f); centerY() }
    }

    private fun addSettBtn() {
        aSettBtn.setSize(40f, 40f)
        add(aSettBtn) { endToEnd(margin = 16f); centerY() }
        aSettBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) } }
    }

}