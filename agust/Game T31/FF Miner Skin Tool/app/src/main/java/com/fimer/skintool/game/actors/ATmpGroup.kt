package com.fimer.skintool.game.actors

import com.fimer.skintool.game.utils.advanced.AdvancedGroup
import com.fimer.skintool.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}