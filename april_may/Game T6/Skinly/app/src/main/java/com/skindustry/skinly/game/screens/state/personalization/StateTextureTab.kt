package com.skindustry.skinly.game.screens.state.personalization

import com.skindustry.skinly.game.actors.panel.personalization.APanelFilter
import com.skindustry.skinly.game.utils.actor.animHideAndDisable
import com.skindustry.skinly.game.utils.actor.animShowAndEnable
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.screenState.ScreenContext
import com.skindustry.skinly.game.utils.screenState.ScreenState

class StateTextureTab(
    context               : ScreenContext,
    filter                : APanelFilter,
    private val firstPanel: AdvancedGroup,
) : StateTab(context, filter) {

    override fun firstCards() = firstPanel
}