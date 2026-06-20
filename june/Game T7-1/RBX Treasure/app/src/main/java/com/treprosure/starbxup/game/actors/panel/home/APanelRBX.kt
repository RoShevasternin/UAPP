package com.treprosure.starbxup.game.actors.panel.home

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.utils.GameColor
import com.treprosure.starbxup.game.utils.NumberFormatter
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.font.FontFactory
import com.treprosure.starbxup.game.utils.font.FontParameter
import com.treprosure.starbxup.game.utils.gdxGame
import com.treprosure.starbxup.game.utils.runGDX
import kotlinx.coroutines.launch

class APanelRBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(32)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aContentImg = Image(gdxGame.assetsAll.listHomeContent[0])
    private val aRBXLbl     = Label("0", FontFactory.create(screen, parameter, screen.fontGenerator_Anton_Regular, GameColor.yellow_DDA334))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addContentImg()
        addRBXLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addContentImg() {
        add(aContentImg) { fillParent() }
    }

    private fun addRBXLbl() {
        aRBXLbl.setSize(112f, 38f)
        add(aRBXLbl) { startToStart(margin = 16f); topToTop(margin = 40f) }

        collectRBX()
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun collectRBX() {
        coroutine?.launch {
            gdxGame.modelPlayer.rbxFlow.collect { rbx ->
                runGDX { aRBXLbl.setText(NumberFormatter.format(rbx)) }
            }
        }
    }

}