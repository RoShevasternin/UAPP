package com.selftest.mindora.game.actors

import com.selftest.mindora.game.utils.advanced.AdvancedGroup
import com.selftest.mindora.game.utils.advanced.AdvancedScreen

class ATmpGroup(override val screen: AdvancedScreen): AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    override fun addActorsOnGroup() { }

}