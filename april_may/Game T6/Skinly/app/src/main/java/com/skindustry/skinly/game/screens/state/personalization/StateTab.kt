package com.skindustry.skinly.game.screens.state.personalization

import com.skindustry.skinly.game.actors.panel.personalization.APanelFilter
import com.skindustry.skinly.game.utils.actor.animHideAndDisable
import com.skindustry.skinly.game.utils.actor.animShowAndEnable
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.screenState.ScreenContext
import com.skindustry.skinly.game.utils.screenState.ScreenState

abstract class StateTab(
    context           : ScreenContext,
    private val filter: APanelFilter,
) : ScreenState(context) {

    // Поточна видима панель карток всередині вкладки
    private var activeCards: AdvancedGroup? = null

    override fun onEnter() {
        filter.animShowAndEnable()
        // показуємо першу панель карток
        filter.checkFirst()
        showCards(firstCards())
    }

    override fun onExit() {
        filter.animHideAndDisable()
        activeCards?.animHideAndDisable()
        activeCards = null
    }

    // Перемикання панелі карток ВСЕРЕДИНІ вкладки — без зміни стану
    fun showCards(cards: AdvancedGroup) {
        if (activeCards === cards) return
        activeCards?.animHideAndDisable()
        activeCards = cards
        cards.animShowAndEnable()
    }

    // Підклас вказує першу панель
    abstract fun firstCards(): AdvancedGroup
}