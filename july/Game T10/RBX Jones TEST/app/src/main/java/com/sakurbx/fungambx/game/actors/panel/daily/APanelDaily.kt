package com.sakurbx.fungambx.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.sakurbx.fungambx.game.actors.button.base.AButtonAnim
import com.sakurbx.fungambx.game.actors.button.base.AButtonStyles
import com.sakurbx.fungambx.game.actors.layout.autoLayout.AAutoLayout
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.controller.DailyRewardController
import com.sakurbx.fungambx.game.utils.actor.setOnTouchListener
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame

class APanelDaily(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.PANEL_DAILY_REWARD)
    private val listItems = List(7) { AItemDailyReward(screen) }
    private val aClaimBtn = AButtonAnim(screen, AButtonStyles.Anim.CLAIM)

    private val aHorizontal = AAutoLayout(screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        gapMain   = 4f,
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
        addBlueBtn()

        controller.onGetReward      = { onGetReward(it) }
        controller.onShowClaimState = { showClaimState() }
        controller.onShowWaitState  = { showWaitState(it) }

        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addHorizontal() {
        aHorizontal.setSize(318f, 65f)
        add(aHorizontal) { startToStart(margin = 13f); topToTop(margin = 40f) }

        // дні не перехоплюють тач → клік по будь-якій частині панелі = claim
        aHorizontal.touchable = Touchable.disabled

        aHorizontal.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEachIndexed { _, item ->
            item.setSize(42f, 65f)
            add(item)
        }
    }

    private fun addBlueBtn() {
        aClaimBtn.setSize(318f, 38f)
        add(aClaimBtn) { centerX(); bottomToBottom(margin = 16f) }

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