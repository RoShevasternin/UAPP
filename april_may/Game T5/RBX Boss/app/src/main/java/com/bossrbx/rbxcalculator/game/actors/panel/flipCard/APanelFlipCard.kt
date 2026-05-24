package com.bossrbx.rbxcalculator.game.actors.panel.flipCard

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.panel.APanelRS
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.actor.animShow
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class APanelFlipCard(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTextImg        = Image(gdxGame.assetsAll.TEXT_FLIP_CARD)
    private val aPanelRSBackImg = Image(screen.drawerUtil.getTexture(GameColor.gray_171717))
    private val aPanelRS        = APanelRS(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addText()
        addPanelRS()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addText() {
        aTextImg.setSize(344f, 116f)
        add(aTextImg) { centerX(); topToTop() }
    }

    private fun AConstraintLayout.addPanelRS() {
        aPanelRSBackImg.setSize(344f, 56f)
        add(aPanelRSBackImg) { centerX(); topToBottom(aTextImg, 24f) }

        aPanelRS.setSize(64f, 32f)
        add(aPanelRS) { center(aPanelRSBackImg) }
    }


}