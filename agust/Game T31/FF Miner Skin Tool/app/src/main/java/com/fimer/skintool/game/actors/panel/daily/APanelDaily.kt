package com.fimer.skintool.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.button.AYellowButton
import com.fimer.skintool.game.actors.button.base.AButtonAnim
import com.fimer.skintool.game.actors.button.base.AButtonStyles
import com.fimer.skintool.game.actors.layout.autoLayout.AAutoLayout
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.controller.DailyRewardController
import com.fimer.skintool.game.utils.actor.animHideAndDisable
import com.fimer.skintool.game.utils.actor.animShowAndEnable
import com.fimer.skintool.game.utils.actor.setOnTouchListener
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame

class APanelDaily(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.PANEL_DAILY_REWARD)
    private val listItems = List(7) { AItemDailyReward(screen) }
    private val aClaimBtn = AYellowButton(screen, "CLAIM")

    private val aHorizontal = AAutoLayout(
        screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        gapMain = 4f,
    )

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onGetReward: (Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------

    private val controller by lazy {
        DailyRewardController(
            scope = coroutine,
            model = gdxGame.modelPlayer,
            items = listItems,
        )
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addHorizontal()
        addClaimBtn()

        controller.onGetReward      = { onGetReward(it) }
        controller.onShowClaimState = { showClaimState() }
        controller.onShowWaitState  = { seconds -> showWaitState(seconds) }

        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addHorizontal() {
        aHorizontal.setSize(290f, 56f)
        add(aHorizontal) { centerX(); centerY() }

        aHorizontal.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEachIndexed { _, item ->
            item.setSize(38f, 56f)
            add(item)
        }
    }

    private fun addClaimBtn() {
        aClaimBtn.setSize(296f, 40f)
        add(aClaimBtn) { centerX(); bottomToBottom(margin = 24f) }

        aClaimBtn.setOnTouchListener { controller.tryClaim() }
    }

    // ------------------------------------------------------------------------
    // Panel State
    // ------------------------------------------------------------------------
    private fun showClaimState() {
        aClaimBtn.enable()
    }

    private fun showWaitState(seconds: Long) {
        aClaimBtn.disable()
    }
}