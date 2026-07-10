package com.sakurbx.fungambx.game.actors.panel.selector

import com.badlogic.gdx.math.Vector2
import com.sakurbx.fungambx.game.actors.AScrollPane
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBoxGroup
import com.sakurbx.fungambx.game.actors.checkbox.ACheckBox_Item
import com.sakurbx.fungambx.game.actors.layout.autoLayout.AAutoLayout
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.data.ItemData
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.font.setDoubleShadow

class APanelSelectorGrid(
    override val screen: AdvancedScreen,
    listItemData: List<ItemData>,
    margin   : Float   = 0f,
    vSize    : Vector2
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)
        .setDoubleShadow()

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Laila_Bold)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTable = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.HORIZONTAL,
        wrap           = true,
        sizingH        = AAutoLayout.Sizing.HUG,
        gapMain        = 8f,
        gapCross       = 8f,
    )
    private val aScrollPane   = AScrollPane(aTable)
    private val listItemBox   = List(listItemData.size) {
        val item = listItemData[it]
        ACheckBox_Item(screen, item.name, lsDef, item.texture, vSize, margin)
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
            box.setSize(168f, 168f)
            add(box)

            box.checkBoxGroup = cbg
            box.setOnCheckListener { }
        }

        listItemBox.first().check()
    }

}