package com.rbxrush.rushrbx.game.actors.checkbox

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.rbxrush.rushrbx.game.actors.checkbox.base.ACheckBox
import com.rbxrush.rushrbx.game.actors.checkbox.base.ACheckBoxStyles
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.actor.setFontColor
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen

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

        lbl.setSize(280f, 20f)
        layout.add(lbl) { startToStart(margin = 16f); centerY() }
        lbl.setEllipsis(true)
    }

    override fun onChecked() {
        super.onChecked()
        lbl.setFontColor(GameColor.black_2C2C2C)
    }

    override fun onUnchecked() {
        super.onUnchecked()
        lbl.setFontColor(Color.WHITE)
    }

}