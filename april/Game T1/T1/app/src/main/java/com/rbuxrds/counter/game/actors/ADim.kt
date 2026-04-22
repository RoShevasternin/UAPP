package com.rbuxrds.counter.game.actors

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counter.game.utils.GameColor
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

class ADim(override val screen: AdvancedScreen) : AdvancedGroup() {
    private val dimImg = Image(screen.drawerUtil.getTexture(GameColor.black_80))

    override fun addActorsOnGroup() {
        addAndFillActor(dimImg)
    }
}