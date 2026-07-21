package com.mon.sterbx.game.actors.panel.selector

import com.mon.sterbx.game.actors.AScrollPane
import com.mon.sterbx.game.actors.checkbox.ACheckBox_ItemLong
import com.mon.sterbx.game.actors.layout.autoLayout.AAutoLayout
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.data.ItemData
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter


class APanelSelectorVertical(
    override val screen: AdvancedScreen,
    listItem: List<String>,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)
        

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_BeVietnamPro_Bold, GameColor.black_060606)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVertical = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.VERTICAL,
        alignMain      = AAutoLayout.AlignMain.START,
        sizingH        = AAutoLayout.Sizing.HUG,
        gapMain        = 8f,
    )
    private val aScrollPane   = AScrollPane(aVertical)
    private val listItemBox   = List(listItem.size) { ACheckBox_ItemLong(screen, listItem[it], lsDef) }

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
            box.setSize(344f, 42f)
            add(box)

            box.setOnCheckListener { }
        }

        listItemBox.first().check()
    }

}