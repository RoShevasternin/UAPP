package com.rbxhubpro.rohumex.game.actors

import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedGroup
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}