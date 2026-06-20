package com.coinsclub.funrbx.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.coinsclub.funrbx.game.actors.layout.autoLayout.AAutoLayout
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.controller.DailyRewardController
import com.coinsclub.funrbx.game.utils.actor.animHideAndDisable
import com.coinsclub.funrbx.game.utils.actor.animShowAndEnable
import com.coinsclub.funrbx.game.utils.actor.disable
import com.coinsclub.funrbx.game.utils.actor.setOnTouchListener
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class APanelDaily(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.CONTENT_3_CLAIM)
    private val listItems = List(7) { AItemDailyReward(screen) }
    private val aTimer    = ATimer(screen)

    private val aHorizontal = AAutoLayout(screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        gapMain   = 14f,
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
        addTimer()

        controller.onGetReward      = { onGetReward(it) }
        controller.onShowClaimState = { showClaimState() }
        controller.onShowWaitState  = { showWaitState(it) }

        aTimer.onFinish = { controller.refresh() }

        controller.initialize()

        setOnTouchListener { controller.tryClaim() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addHorizontal() {
        aHorizontal.setSize(308f, 32f)
        add(aHorizontal) { startToStart(margin = 21f); topToTop(margin = 56f) }

        // дні не перехоплюють тач → клік по будь-якій частині панелі = claim
        aHorizontal.touchable = Touchable.disabled

        aHorizontal.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEachIndexed { index, item ->
            item.setSize(32f, 32f)
            add(item)

            if (index.inc() == 7) item.is7 = true
        }
    }

    private fun addTimer() {
        aTimer.setSize(100f, 18f)
        add(aTimer) { centerX(); bottomToBottom(margin = 17f) }

        aTimer.disable()
        aTimer.color.a = 0f
    }

    // ------------------------------------------------------------------------
    // Panel State
    // ------------------------------------------------------------------------
    private fun showClaimState() {
        aBgImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.CONTENT_3_CLAIM)
        aTimer.stop()
        aTimer.color.a = 0f
    }

    private fun showWaitState(seconds: Long) {
        aBgImg.drawable = TextureRegionDrawable(gdxGame.assetsAll.CONTENT_3_WAIT)
        aTimer.color.a  = 1f
        aTimer.start(seconds)
    }
}