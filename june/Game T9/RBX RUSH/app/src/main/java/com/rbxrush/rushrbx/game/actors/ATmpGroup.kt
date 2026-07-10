package com.rbxrush.rushrbx.game.actors

import com.rbxrush.rushrbx.game.utils.advanced.AdvancedGroup
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}