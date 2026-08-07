package com.diam.ondbit.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.diam.ondbit.game.actors.button.AYellowButton
import com.diam.ondbit.game.actors.button.base.AButtonAnim
import com.diam.ondbit.game.actors.button.base.AButtonStyles
import com.diam.ondbit.game.actors.layout.autoLayout.AAutoLayout
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.controller.DailyRewardController
import com.diam.ondbit.game.utils.actor.animHideAndDisable
import com.diam.ondbit.game.utils.actor.animShowAndEnable
import com.diam.ondbit.game.utils.actor.setOnTouchListener
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame

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
        gapMain = 8f,
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
        aHorizontal.setSize(278f, 32f)
        add(aHorizontal) { startToStart(margin = 32f); bottomToBottom(margin = 106f) }

        aHorizontal.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEachIndexed { _, item ->
            item.setSize(32f, 32f)
            add(item)
        }
    }

    private fun addClaimBtn() {
        aClaimBtn.setSize(296f, 52f)
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