package com.bossrbx.rbxcalculator.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.game.actors.ATmpGroup
import com.bossrbx.rbxcalculator.game.actors.button.base.AButtonAnim
import com.bossrbx.rbxcalculator.game.actors.button.base.AButtonStyles
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.screens.SettingsScreen
import com.bossrbx.rbxcalculator.game.utils.NumberFormatter
import com.bossrbx.rbxcalculator.game.utils.actor.setOnClickListener
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelTopMain(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg     = Image(gdxGame.assetsAll.logo)
    private val aSettingsBtn = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addSettingsBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLogoImg() {
        aLogoImg.setSize(150f, 32f)
        add(aLogoImg) { startToStart(margin = 16f); centerY() }
    }

    private fun addSettingsBtn() {
        aSettingsBtn.setSize(32f, 32f)
        add(aSettingsBtn) { endToEnd(margin = 16f); centerY() }

        aSettingsBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) }
        }
    }

}