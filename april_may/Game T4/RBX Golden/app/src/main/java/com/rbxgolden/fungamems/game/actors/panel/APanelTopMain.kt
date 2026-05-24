package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.game.actors.button.base.AButtonAnim
import com.rbxgolden.fungamems.game.actors.button.base.AButtonStyles
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.screens.SettingsScreen
import com.rbxgolden.fungamems.game.utils.NumberFormatter
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelTopMain(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg     = Image(gdxGame.assetsAll.logo)
    private val aSettingsBtn = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)
    private val aPanelRBX    = APanelRBX(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addLogoImg()
        addSettingsBtn()
        addPanelRBX()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLogoImg() {
        aLogoImg.setSize(133f, 60f)
        add(aLogoImg) { startToStart(margin = 16f); centerY() }
    }

    private fun addSettingsBtn() {
        aSettingsBtn.setSize(24f, 24f)
        add(aSettingsBtn) { endToEnd(margin = 16f); centerY() }

        aSettingsBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) }
        }
    }

    private fun addPanelRBX() {
        aPanelRBX.setSize(86f, 40f)
        add(aPanelRBX) { endToStart(aSettingsBtn, margin = 16f); centerY() }

        collectRBX()
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun collectRBX() {
        coroutine?.launch {
            gdxGame.modelPlayer.rbxFlow.collect { rbx ->
                runGDX { aPanelRBX.setText(NumberFormatter.format(rbx)) }
            }
        }
    }

}