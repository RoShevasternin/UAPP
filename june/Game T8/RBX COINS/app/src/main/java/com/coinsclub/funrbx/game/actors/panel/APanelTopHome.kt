package com.coinsclub.funrbx.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coinsclub.funrbx.game.actors.button.base.AButtonAnim
import com.coinsclub.funrbx.game.actors.button.base.AButtonStyles
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.screens.SettingsScreen
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class APanelTopHome(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg = Image(gdxGame.assetsAll.logo)
    private val aSettBtn = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addSettBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLogoImg() {
        aLogoImg.setSize(65f, 41f)
        add(aLogoImg) { startToStart(margin = 16f); centerY() }
    }

    private fun addSettBtn() {
        aSettBtn.setSize(40f, 40f)
        add(aSettBtn) { endToEnd(margin = 16f); centerY() }
        aSettBtn.setOnClickListener { screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) } }
    }

}