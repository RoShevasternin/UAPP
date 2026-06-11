package com.skindustry.skinly.game.actors

import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}