package com.racing.funtols.game.actors.panel.selector

import com.racing.funtols.game.actors.AScrollPane
import com.racing.funtols.game.actors.checkbox.ACheckBox_ItemLong
import com.racing.funtols.game.actors.layout.autoLayout.AAutoLayout
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.GameColor
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame


class APanelSelectorVertical(
    override val screen: AdvancedScreen,
    listItem: List<String>,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontBarlow_Bold, 14f)

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
    private val aScrollPane = AScrollPane(aVertical)
    private val listItemBox = List(listItem.size) { ACheckBox_ItemLong(screen, listItem[it], styleDef) }

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
            box.setSize(344f, 58f)
            add(box)

            box.setOnCheckListener { }
        }

        listItemBox.first().check()
    }

}