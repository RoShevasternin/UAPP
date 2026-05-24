package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import com.rbxgolden.fungamems.game.utils.gdxGame

class APanelRBX(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.panel_rbx)
    private val aCoinImg  = Image(gdxGame.assetsAll.coin)
    private val aLbl      = Label("0", FontFactory.create(screen, parameter, screen.fontGenerator_Bold, Color.WHITE))

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPanelImg) { fillParent() }

        addCoinImg()
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addCoinImg() {
        aCoinImg.setSize(24f, 24f)
        add(aCoinImg) { startToStart(margin = 12f); centerY() }
    }

    private fun addLbl() {
        aLbl.setSize(1f, 22f)
        add(aLbl) {
            startToEnd(aCoinImg, margin = 8f); endToEnd(margin = 12f)
            centerY()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setText(value: String) {
        aLbl.setText(value)
        aLbl.pack()

        val newW = (12f + aCoinImg.width + 8f + aLbl.width + 12f)
        width = newW
    }

}