package com.mon.sterbx.game.actors.panel.selector

import com.badlogic.gdx.math.Vector2
import com.mon.sterbx.game.actors.AScrollPane
import com.mon.sterbx.game.actors.checkbox.base.ACheckBoxGroup
import com.mon.sterbx.game.actors.checkbox.ACheckBox_Item
import com.mon.sterbx.game.actors.layout.autoLayout.AAutoLayout
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.data.ItemData
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter


class APanelSelectorGrid(
    override val screen: AdvancedScreen,
    listItemData: List<ItemData>,
    margin   : Float   = 0f,
    vSize    : Vector2
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterTitle = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)
    private val parameterDesc = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val lsTitle = FontFactory.create(screen, parameterTitle, screen.fontGenerator_BeVietnamPro_Bold, GameColor.black_060606)
    private val lsDesc  = FontFactory.create(screen, parameterDesc, screen.fontGenerator_BeVietnamPro_Regular, GameColor.black_373737)

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
        ACheckBox_Item(screen, item.name, item.desc, lsTitle, lsDesc, item.texture, vSize, margin)
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
            box.setSize(344f, 111f)
            add(box)

            box.checkBoxGroup = cbg
            box.setOnCheckListener { }
        }

        listItemBox.first().check()
    }

}