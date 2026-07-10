package com.rbxrush.rushrbx.game.actors.panel.selector

import com.badlogic.gdx.math.Vector2
import com.rbxrush.rushrbx.game.actors.AScrollPane
import com.rbxrush.rushrbx.game.actors.checkbox.base.ACheckBoxGroup
import com.rbxrush.rushrbx.game.actors.checkbox.ACheckBox_Item
import com.rbxrush.rushrbx.game.actors.layout.autoLayout.AAutoLayout
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.data.ItemData
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter

class APanelSelectorGrid(
    override val screen: AdvancedScreen,
    listItemData: List<ItemData>,
    marginTop: Float   = 0f,
    vSize    : Vector2 = Vector2(109f, 109f)

): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Fredoka_Regular)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTable = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.HORIZONTAL,
        wrap           = true,
        sizingH        = AAutoLayout.Sizing.HUG,
        paddingBottom  = 40f,
        gapMain        = 8f,
        gapCross       = 8f,
    )
    private val aScrollPane   = AScrollPane(aTable)
    private val listItemBox   = List(listItemData.size) {
        val item = listItemData[it]
        ACheckBox_Item(screen, item.name, lsDef, item.texture, vSize, marginTop)
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        aTable.setUpTable()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aTable.minH = height
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AAutoLayout.setUpTable() {
        aTable.width = width
        addListBox()
    }

    private fun AAutoLayout.addListBox() {
        val cbg = ACheckBoxGroup()

        listItemBox.forEach { box ->
            box.setSize(109f, 137f)
            add(box)

            box.checkBoxGroup = cbg
            box.setOnCheckListener { }
        }

        listItemBox.first().check()
    }

}