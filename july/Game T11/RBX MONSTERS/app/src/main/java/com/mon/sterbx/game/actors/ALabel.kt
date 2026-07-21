package com.mon.sterbx.game.actors

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.mon.sterbx.game.utils.advanced.AdvancedGroup
import com.mon.sterbx.game.utils.advanced.AdvancedScreen

class ALabel(
    override val screen: AdvancedScreen,
    val text: String,
    val labelStyle: Label.LabelStyle,
): AdvancedGroup() {

    val label = Label("", labelStyle)

    override fun addActorsOnGroup() {
        addAndFillActor(label)
    }

}