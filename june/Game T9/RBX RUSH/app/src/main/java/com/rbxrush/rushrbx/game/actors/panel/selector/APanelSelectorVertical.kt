package com.rbxrush.rushrbx.game.actors.panel.selector

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxrush.rushrbx.game.actors.AScrollPane
import com.rbxrush.rushrbx.game.actors.checkbox.ACheckBox_ItemLong
import com.rbxrush.rushrbx.game.actors.layout.autoLayout.AAutoLayout
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter
import com.rbxrush.rushrbx.game.utils.gdxGame
import com.rbxrush.rushrbx.util.log

class APanelSelectorVertical(
    override val screen: AdvancedScreen,
    listName: List<String>,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(18)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Fredoka_Medium)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVertical = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.VERTICAL,
        alignMain      = AAutoLayout.AlignMain.START,
        sizingH        = AAutoLayout.Sizing.HUG,
        paddingBottom  = 40f,
        gapMain        = 8f,
    )
    private val aScrollPane   = AScrollPane(aVertical)
    private val listItemBox   = List(listName.size) { ACheckBox_ItemLong(screen, listName[it], lsDef) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        aVertical.setUpTable()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aVertical.minH = height
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AAutoLayout.setUpTable() {
        aVertical.width = width
        addListBox()
    }

    private fun AAutoLayout.addListBox() {
        listItemBox.forEach { box ->
            box.setSize(344f, 60f)
            add(box)

            box.setOnCheckListener { }
        }

        listItemBox.first().check()
    }

}