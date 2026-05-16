package com.rbuxdrop.cougame.game.actors.panel

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import com.rbuxdrop.cougame.game.utils.gdxGame

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
    private val aRbuxImg  = Image(gdxGame.assetsAll.logo_rbs)
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
        aRbuxImg.setSize(48f, 48f)
        add(aRbuxImg) { startToStart(); bottomToBottom() }
    }

    private fun addLbl() {
        aLbl.setSize(48f, 48f)
        add(aLbl) {
            startToEnd(aRbuxImg, margin = 7f); endToEnd(margin = 7f)
            centerY()
        }

        aLbl.setAlignment(Align.center)
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
        return 0f + aRbuxImg.width + 7f + aLbl.prefWidth + 7f
    }

    override fun getPrefHeight(): Float {
        return 0f + maxOf(aRbuxImg.height, aLbl.prefHeight) + 0f
    }

}