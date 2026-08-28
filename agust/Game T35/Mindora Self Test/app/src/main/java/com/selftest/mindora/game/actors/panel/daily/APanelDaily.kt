package com.selftest.mindora.game.actors.panel.daily

import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.advanced.AdvancedScreen

// ----------------------------------------------------------------------------
// APanelDaily — сітка з 7 клітинок нагород.
//
//   ТУПА В'ЮХА: не знає про PlayerModel, не має контролера, не приймає рішень.
//   Тільки render(day, canClaim) ззовні. Контролер живе на DailyScreen, бо
//   кнопки Claim і x2 лежать саме там і мусять ділити з панеллю один стан.
// ----------------------------------------------------------------------------

class APanelDaily(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listItems = List(7) { AItemDailyReward(screen) }

    private val aHorizontal = AAutoLayout(
        screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        wrap      = true,
        gapMain   = 8f,
        gapCross  = 8f,
        alignMain = AAutoLayout.AlignMain.CENTER
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addHorizontal()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addHorizontal() {
        add(aHorizontal) { fillParent() }
        aHorizontal.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEach { item ->
            item.setSize(80f, 105f)
            add(item)
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /**
     * @param currentDay день циклу 1..7
     * @param canClaim   чи доступна нагорода прямо зараз
     *
     * Дні до поточного — CLAIMED, поточний — CLAIM або LOCKED (залежно від
     * таймера), майбутні — LOCKED.
     */
    fun render(currentDay: Int, canClaim: Boolean) {
        listItems.forEachIndexed { index, item ->
            val day = index + 1
            item.setReward(day)

            val state = when {
                day <  currentDay -> AItemDailyReward.DailyRewardState.CLAIMED
                day == currentDay -> if (canClaim) AItemDailyReward.DailyRewardState.CLAIM else AItemDailyReward.DailyRewardState.LOCKED
                else              -> AItemDailyReward.DailyRewardState.LOCKED
            }
            item.setState(state)
        }
    }
}