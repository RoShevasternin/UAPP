package com.mon.sterbx.game.actors.popup.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.mon.sterbx.game.actors.button.base.AButtonAnim
import com.mon.sterbx.game.actors.button.base.AButtonStyles
import com.mon.sterbx.game.actors.layout.autoLayout.AAutoLayout
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.controller.DailyRewardController
import com.mon.sterbx.game.utils.actor.setOnTouchListener
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.gdxGame

class APopupDaily(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.PANEL_DAILY_REWARD)
    private val listItems = List(6) { AItemDailyReward(screen) }
    private val aItemDay7 = AItemDailyReward(screen, true)
    private val aClaimBtn = AButtonAnim(screen, AButtonStyles.Anim.CLAIM)
    private val aCloseBtn = AButtonAnim(screen, AButtonStyles.Anim.CLOSE)

    private val aHorizontal = AAutoLayout(screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        gapMain   = 8f,
        gapCross  = 8f,
        wrap      = true,
    )

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClose = {}
    var onGetReward: (Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    // усі 7 айтемів разом — для контролера
    private val allItems by lazy { listItems + aItemDay7 }

    private val controller by lazy {
        DailyRewardController(
            scope = coroutine,
            model = gdxGame.modelPlayer,
            items = allItems,
        )
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addHorizontal()
        addItemDay7()
        addClaimBtn()
        addCloseBtn()

        controller.onGetReward      = { onGetReward(it) }
        controller.onShowClaimState = { showClaimState() }
        controller.onShowWaitState  = { showWaitState() }

        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addHorizontal() {
        aHorizontal.setSize(232f, 201f)
        add(aHorizontal) { startToStart(margin = 20f); bottomToBottom(margin = 20f) }

        aHorizontal.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEachIndexed { _, item ->
            item.setSize(72f, 96f)
            add(item)
        }
    }

    private fun addItemDay7() {
        aItemDay7.setSize(72f, 201f)   // висота як уся сітка, ширина під праву колонку
        add(aItemDay7) { endToEnd(margin = 18f); bottomToBottom(margin = 20f) }
    }

    private fun addClaimBtn() {
        aClaimBtn.setSize(312f, 44f)
        add(aClaimBtn) { centerX(); topToTop(margin = 67f) }

        aClaimBtn.setOnTouchListener { controller.tryClaim() }
    }

    private fun addCloseBtn() {
        aCloseBtn.setSize(24f, 24f)
        add(aCloseBtn) { endToEnd(margin = 18f); topToTop(margin = 20f) }

        aCloseBtn.setOnTouchListener { onClose() }
    }

    // ------------------------------------------------------------------------
    // Panel State
    // ------------------------------------------------------------------------
    private fun showClaimState() {
        aClaimBtn.enable()
    }

    private fun showWaitState() {
        aClaimBtn.disable()
    }
}