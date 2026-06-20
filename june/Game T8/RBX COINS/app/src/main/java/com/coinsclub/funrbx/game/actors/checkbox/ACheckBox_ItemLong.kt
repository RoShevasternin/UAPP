package com.coinsclub.funrbx.game.actors.checkbox

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.coinsclub.funrbx.game.actors.checkbox.base.ACheckBox
import com.coinsclub.funrbx.game.actors.checkbox.base.ACheckBoxStyles
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.actor.setFontColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen

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

        lbl.setSize(270f, 18f)
        layout.add(lbl) { startToStart(margin = 19f); centerY() }
        lbl.setEllipsis(true)
    }

    override fun onChecked() {
        super.onChecked()
        lbl.setFontColor(GameColor.yellow_DFA008)
    }

    override fun onUnchecked() {
        super.onUnchecked()
        lbl.setFontColor(GameColor.white_FFF5E3)
    }

}