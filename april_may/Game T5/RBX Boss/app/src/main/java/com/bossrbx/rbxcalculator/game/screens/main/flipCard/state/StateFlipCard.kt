package com.bossrbx.rbxcalculator.game.screens.main.flipCard.state

import com.bossrbx.rbxcalculator.game.actors.panel.flipCard.APanelFlipCard
import com.bossrbx.rbxcalculator.game.utils.TIME_SCREEN_STATE
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShowAndEnable
import com.bossrbx.rbxcalculator.game.utils.screenState.ScreenContext
import com.bossrbx.rbxcalculator.game.utils.screenState.ScreenState

class StateFlipCard(
    context            : ScreenContext,
    private val panel  : APanelFlipCard,
) : ScreenState(context) {

    override fun onEnter() {
        panel.animShowAndEnable(TIME_SCREEN_STATE)
    }

    override fun onExit() {
        //panel.animHideAndDisable(TIME_SCREEN_STATE)
    }
}