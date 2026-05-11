package com.rsbuxs.rcounbux.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rsbuxs.rcounbux.game.actors.label.ALabel
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.font.FontParameter
import com.rsbuxs.rcounbux.game.utils.gdxGame

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
    private val aPanelImg = Image(gdxGame.assetsAll.panel_gradient)
    private val aRbuxImg  = Image(gdxGame.assetsAll.rbux)
    private val aLbl      = ALabel(screen, "0", Color.WHITE, parameter, screen.fontGenerator_Bold)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPanelImg) { fillParent() }

        addRbuxImg()
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addRbuxImg() {
        aRbuxImg.setSize(21f, 24f)
        add(aRbuxImg) {
            startToStart(margin = 16f)
            bottomToBottom(margin = 12f)
        }
    }

    private fun addLbl() {
        aLbl.setSize(10f, 22f)
        add(aLbl) {
            startToEnd(aRbuxImg, margin = 8f)
            endToEnd(margin = 16f)
            bottomToBottom(margin = 13f)
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setText(value: String) {
        aLbl.setText(value)
        aLbl.setSize(aLbl.prefWidth, aLbl.prefHeight)
        pack()
    }

    override fun getPrefWidth(): Float {
        return 16f + aRbuxImg.width + 8f + aLbl.prefWidth + 16f
    }

    override fun getPrefHeight(): Float {
        return 12f + maxOf(aRbuxImg.height, aLbl.prefHeight) + 12f
    }

}