package com.rbuxrds.counterds.game.actors

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counterds.game.utils.GameColor
import com.rbuxrds.counterds.game.utils.actor.addAndFillActor
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen

class ADim(override val screen: AdvancedScreen) : AdvancedGroup() {
    private val dimImg = Image(screen.drawerUtil.getTexture(GameColor.black_80))

    override fun addActorsOnGroup() {
        addAndFillActor(dimImg)
    }
}