package com.racing.funtols.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.racing.funtols.game.actors.button.base.AButtonAnim
import com.racing.funtols.game.actors.button.base.AButtonStyles
import com.racing.funtols.game.actors.layout.autoLayout.AAutoLayout
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.controller.DailyRewardController
import com.racing.funtols.game.utils.actor.animHideAndDisable
import com.racing.funtols.game.utils.actor.animShowAndEnable
import com.racing.funtols.game.utils.actor.setOnTouchListener
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

class APanelDaily(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.PANEL_DAILY_REWARD)
    private val listItems = List(7) { AItemDailyReward(screen) }
    private val aClaimBtn = AButtonAnim(screen, AButtonStyles.Anim.CLAIM)

    private val aHorizontal = AAutoLayout(
        screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        gapMain = 8f,
    )

    private val aTimer = ATimer(screen)

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
        addTimer()

        controller.onGetReward      = { onGetReward(it) }
        controller.onShowClaimState = { showClaimState() }
        controller.onShowWaitState  = { seconds -> showWaitState(seconds) }

        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addHorizontal() {
        aHorizontal.setSize(223f, 24f)
        add(aHorizontal) { startToStart(margin = 24f); bottomToBottom(margin = 96f) }

        aHorizontal.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEachIndexed { _, item ->
            item.setSize(24f, 24f)
            add(item)
        }
    }

    private fun addClaimBtn() {
        aClaimBtn.setSize(312f, 40f)
        add(aClaimBtn) { centerX(); bottomToBottom(margin = 16f) }

        aClaimBtn.setOnTouchListener { controller.tryClaim() }
    }

    private fun addTimer() {
        aTimer.setSize(48f, 20f)
        add(aTimer) { endToEnd(margin = 32f); topToTop(margin = 20f) }

        // таймер добіг нуля → час клеймити
        aTimer.onFinish = { showClaimState() }
    }


    // ------------------------------------------------------------------------
    // Panel State
    // ------------------------------------------------------------------------
    private fun showClaimState() {
        aTimer.stop()
        aClaimBtn.animShowAndEnable(0.2f)
    }

    private fun showWaitState(seconds: Long) {
        aClaimBtn.animHideAndDisable(0.2f)
        aTimer.start(seconds)
    }
}