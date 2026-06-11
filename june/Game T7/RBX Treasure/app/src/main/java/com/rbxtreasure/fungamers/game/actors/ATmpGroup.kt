package com.rbxtreasure.fungamers.game.actors

import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedGroup
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}