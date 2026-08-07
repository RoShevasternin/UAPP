package com.racing.funtols.game.actors.panel.selector

import com.badlogic.gdx.math.Vector2
import com.racing.funtols.game.actors.AScrollPane
import com.racing.funtols.game.actors.checkbox.ACheckBox_Item
import com.racing.funtols.game.actors.checkbox.base.ACheckBoxGroup
import com.racing.funtols.game.actors.layout.autoLayout.AAutoLayout
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.data.ItemData
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame


class APanelSelectorGrid(
    override val screen: AdvancedScreen,
    listItemData: List<ItemData>,
    margin   : Float   = 0f,
    vSize    : Vector2
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontBarlow_Bold, 14f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTable = AAutoLayout(
        screen         = screen,
        direction      = AAutoLayout.Direction.VERTICAL,
        sizingH        = AAutoLayout.Sizing.HUG,
        gapMain        = 8f,
    )
    private val aScrollPane   = AScrollPane(aTable)
    private val listItemBox   = List(listItemData.size) {
        val item = listItemData[it]
        ACheckBox_Item(screen, item.name, styleDef, item.texture, vSize, margin)
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
            box.setSize(344f, 80f)
            add(box)

            box.checkBoxGroup = cbg
            box.setOnCheckListener { }
        }

        listItemBox.first().check()
    }

}