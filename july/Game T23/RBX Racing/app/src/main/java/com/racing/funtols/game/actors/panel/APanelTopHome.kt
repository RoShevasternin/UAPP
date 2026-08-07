package com.racing.funtols.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.game.actors.button.base.AButtonAnim
import com.racing.funtols.game.actors.button.base.AButtonStyles
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.screens.SettingsScreen
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

class APanelTopHome(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg  = Image(gdxGame.assetsAll.logo)
    private val aPanelRBX = APanelRBX(screen)
    private val aSettBtn  = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addPanelRBX()
        addSettBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLogoImg() {
        aLogoImg.setSize(100f, 50f)
        add(aLogoImg) { startToStart(); centerY() }
    }

    private fun addPanelRBX() {
        aPanelRBX.setSize(108f, 32f)
        add(aPanelRBX) { endToEnd(margin = 39f); centerY() }
    }

    private fun addSettBtn() {
        aSettBtn.setSize(37f, 32f)
        add(aSettBtn) { endToEnd(); centerY() }
        aSettBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) } }
    }

}