package com.rsbuxs.rcounbux.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.utils.NumberFormatter
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.gdxGame
import com.rsbuxs.rcounbux.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelTopLogo(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aLogoImg  = Image(gdxGame.assetsAll.logo_mini)
    private val aPanelRBX = APanelRBX(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addRbuxImg()
        addPanelRBX()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addRbuxImg() {
        aLogoImg.setSize(89f, 49f)
        add(aLogoImg) {
            startToStart(margin = 16f)
            centerY()
        }
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(91f, 48f)
        add(aPanelRBX) {
            endToEnd(margin = 16f)
            centerY()
        }

        coroutine?.launch {
            gdxGame.modelPlayer.rbxFlow.collect { rbx ->
                runGDX {
                    val rbxFormat = NumberFormatter.format(rbx)
                    aPanelRBX.setText(rbxFormat)
                }
            }
        }
    }

}