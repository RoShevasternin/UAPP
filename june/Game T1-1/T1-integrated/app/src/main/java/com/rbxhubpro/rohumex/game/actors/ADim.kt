package com.rbxhubpro.rohumex.game.actors

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxhubpro.rohumex.game.utils.GameColor
import com.rbxhubpro.rohumex.game.utils.actor.addAndFillActor
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedGroup
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen

class ADim(override val screen: AdvancedScreen) : AdvancedGroup() {
    private val dimImg = Image(screen.drawerUtil.getTexture(GameColor.black_80))

    override fun addActorsOnGroup() {
        addAndFillActor(dimImg)
    }
}