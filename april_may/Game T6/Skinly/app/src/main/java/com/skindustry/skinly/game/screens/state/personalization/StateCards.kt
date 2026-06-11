package com.skindustry.skinly.game.screens.state.personalization

import com.skindustry.skinly.game.utils.actor.animHideAndDisable
import com.skindustry.skinly.game.utils.actor.animShowAndEnable
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.screenState.ScreenContext
import com.skindustry.skinly.game.utils.screenState.ScreenState

class StateCards(
    context   : ScreenContext,
    var cards : AdvancedGroup,
) : ScreenState(context) {

    override fun onEnter() {
        cards.animShowAndEnable()
    }

    override fun onExit() {
        cards.animHideAndDisable()
    }
}