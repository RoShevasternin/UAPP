package com.mon.sterbx.game.actors.checkbox

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.mon.sterbx.game.actors.checkbox.base.ACheckBox
import com.mon.sterbx.game.actors.checkbox.base.ACheckBoxStyles
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.utils.advanced.AdvancedScreen

open class ACheckBox_ItemLong(
    screen : AdvancedScreen,
    val text      : String,
    val labelStyle: Label.LabelStyle,
) : ACheckBox(
    screen    = screen,
    style     = ACheckBoxStyles.ITEM_LONG,
) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    val lbl = Label(text, labelStyle)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        val layout = AConstraintLayout(screen)
        addAndFillActor(layout)

        lbl.setSize(175f, 15f)
        layout.add(lbl) { startToStart(margin = 12f); topToTop(margin = 12f) }
        lbl.setEllipsis(true)
    }

}