package com.rbuxdrop.cougame.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxdrop.cougame.game.actors.button.base.AButtonAnim
import com.rbuxdrop.cougame.game.actors.button.base.AButtonStyles
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.screens.SettingsScreen
import com.rbuxdrop.cougame.game.utils.NumberFormatter
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.gdxGame
import com.rbuxdrop.cougame.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelTopMain(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aSettingsBtn = AButtonAnim(screen, AButtonStyles.Anim.SETTINGS)
    private val aTitleImg    = Image(gdxGame.assetsAll.title)
    private val aPanelRBX    = APanelRBX(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addSettingsBtn()
        addTitleImg()
        addPanelRBX()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addSettingsBtn() {
        aSettingsBtn.setSize(48f, 48f)
        add(aSettingsBtn) {
            startToStart(margin = 16f)
            centerY()
        }

        aSettingsBtn.setOnClickListener {
            screen.animHideScreen { gdxGame.navigationManager.navigate(SettingsScreen::class.java.name, screen::class.java.name) }
        }
    }

    private fun addTitleImg() {
        aTitleImg.setSize(80f, 44f)
        add(aTitleImg) {
            startToEnd(aSettingsBtn, 16f)
            centerY()
        }
    }

    private fun addPanelRBX() {
        aPanelRBX.setSize(110f, 48f)
        add(aPanelRBX) {
            endToEnd(margin = 16f)
            centerY()
        }

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