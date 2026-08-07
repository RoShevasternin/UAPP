package com.racing.funtols.game.actors

import com.racing.funtols.game.utils.advanced.AdvancedGroup
import com.racing.funtols.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}