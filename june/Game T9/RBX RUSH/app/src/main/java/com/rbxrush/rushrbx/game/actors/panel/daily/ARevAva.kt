package com.rbxrush.rushrbx.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter
import com.rbxrush.rushrbx.game.utils.gdxGame

class ARevAva(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters("Reward available")
        .setSize(12)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Fredoka_Regular, GameColor.black_2C2C2C)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg     = Image(gdxGame.assetsAll.panel_daily)
    private val aRevAvaLbl = Label("Reward available", lsDef)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addRevAvaLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addRevAvaLbl() {
        add(aRevAvaLbl) { startToStart(margin = 12f); endToEnd(margin = 12f); centerY() }
        aRevAvaLbl.setAlignment(Align.center)
    }

}