package com.racing.funtols.game.actors.checkbox

import com.racing.funtols.game.actors.checkbox.base.ACheckBox
import com.racing.funtols.game.actors.checkbox.base.ACheckBoxStyles
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle

open class ACheckBox_ItemLong(
    screen    : AdvancedScreen,
    text      : String,
    val style : MsdfStyle,
) : ACheckBox(
    screen    = screen,
    style     = ACheckBoxStyles.ITEM_LONG,
) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    val lbl = AMsdfLabel(text, style)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        val layout = AConstraintLayout(screen)
        addAndFillActor(layout)

        lbl.setSize(130f, 20f)
        layout.add(lbl) { startToStart(margin = 16f); centerY() }
    }

}