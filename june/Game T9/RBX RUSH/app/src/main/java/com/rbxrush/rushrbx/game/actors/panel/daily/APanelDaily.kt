package com.rbxrush.rushrbx.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbxrush.rushrbx.game.actors.button.ABlueButton
import com.rbxrush.rushrbx.game.actors.layout.autoLayout.AAutoLayout
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.controller.DailyRewardController
import com.rbxrush.rushrbx.game.utils.actor.animHideAndDisable
import com.rbxrush.rushrbx.game.utils.actor.animShowAndEnable
import com.rbxrush.rushrbx.game.utils.actor.disable
import com.rbxrush.rushrbx.game.utils.actor.setOnTouchListener
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.gdxGame

class APanelDaily(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.PANEL_DAILY_REWARD)
    private val listItems = List(7) { AItemDailyReward(screen) }
    private val aRevAva   = ARevAva(screen)
    private val aTimer    = ATimer(screen)
    private val aBlueBtn  = ABlueButton(screen, "CLAIM")

    private val aHorizontal = AAutoLayout(screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        gapMain   = 15f,
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
        addRevAva()
        addTimer()
        addBlueBtn()

        controller.onGetReward      = { onGetReward(it) }
        controller.onShowClaimState = { showClaimState() }
        controller.onShowWaitState  = { showWaitState(it) }

        aTimer.onFinish = { controller.refresh() }

        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addHorizontal() {
        aHorizontal.setSize(312f, 32f)
        add(aHorizontal) { startToStart(margin = 16f); topToTop(margin = 89f) }

        // дні не перехоплюють тач → клік по будь-якій частині панелі = claim
        aHorizontal.touchable = Touchable.disabled

        aHorizontal.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEachIndexed { _, item ->
            item.setSize(32f, 32f)
            add(item)
        }
    }

    private fun addRevAva() {
        aRevAva.setSize(117f, 29f)
        add(aRevAva) { endToEnd(margin = 16f); topToTop(margin = 16f) }

        aRevAva.disable()
        aRevAva.color.a = 0f
    }

    private fun addTimer() {
        aTimer.setSize(70f, 29f)
        add(aTimer) { endToEnd(margin = 16f); topToTop(margin = 16f) }

        aTimer.disable()
        aTimer.color.a = 0f
    }

    private fun addBlueBtn() {
        aBlueBtn.setSize(312f, 44f)
        add(aBlueBtn) { centerX(); bottomToBottom(margin = 16f) }

        aBlueBtn.setOnTouchListener { controller.tryClaim() }
    }

    // ------------------------------------------------------------------------
    // Panel State
    // ------------------------------------------------------------------------
    private fun showClaimState() {
        aRevAva.color.a = 1f
        aTimer.stop()
        aTimer.color.a = 0f

        aBlueBtn.enable()
    }

    private fun showWaitState(seconds: Long) {
        aRevAva.color.a = 0f
        aTimer.color.a  = 1f
        aTimer.start(seconds)

        aBlueBtn.disable()
    }
}