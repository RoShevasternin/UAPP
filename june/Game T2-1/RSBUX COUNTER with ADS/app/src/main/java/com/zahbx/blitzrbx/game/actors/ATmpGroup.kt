package com.zahbx.blitzrbx.game.actors

import com.zahbx.blitzrbx.game.utils.advanced.AdvancedGroup
import com.zahbx.blitzrbx.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}