package com.sakurbx.fungambx.game.actors.panel.selector

import com.sakurbx.fungambx.game.actors.AScrollPane
import com.sakurbx.fungambx.game.actors.checkbox.ACheckBox_ItemLong
import com.sakurbx.fungambx.game.actors.layout.autoLayout.AAutoLayout
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.data.ItemData
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.font.setDoubleShadow

class APanelSelectorVertical(
    override val screen: AdvancedScreen,
    listItem: List<ItemData>,
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
    private val aVertical = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.VERTICAL,
        alignMain      = AAutoLayout.AlignMain.START,
        sizingH        = AAutoLayout.Sizing.HUG,
        gapMain        = 8f,
    )
    private val aScrollPane   = AScrollPane(aVertical)
    private val listItemBox   = List(listItem.size) { ACheckBox_ItemLong(screen, listItem[it].texture, listItem[it].name, lsDef) }

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