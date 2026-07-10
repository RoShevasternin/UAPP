package com.sakurbx.fungambx.game.actors

import com.sakurbx.fungambx.game.utils.advanced.AdvancedGroup
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}