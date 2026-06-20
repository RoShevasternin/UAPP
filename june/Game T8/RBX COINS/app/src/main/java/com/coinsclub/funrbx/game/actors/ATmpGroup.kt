package com.coinsclub.funrbx.game.actors

import com.coinsclub.funrbx.game.utils.advanced.AdvancedGroup
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}