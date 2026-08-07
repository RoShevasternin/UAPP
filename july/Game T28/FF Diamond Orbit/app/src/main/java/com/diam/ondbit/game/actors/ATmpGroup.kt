package com.diam.ondbit.game.actors

import com.diam.ondbit.game.utils.advanced.AdvancedGroup
import com.diam.ondbit.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}