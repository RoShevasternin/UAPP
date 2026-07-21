package com.mon.sterbx.game.actors

import com.mon.sterbx.game.utils.advanced.AdvancedGroup
import com.mon.sterbx.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}