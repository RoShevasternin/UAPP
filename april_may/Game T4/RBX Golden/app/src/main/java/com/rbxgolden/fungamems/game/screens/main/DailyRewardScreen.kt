package com.rbxgolden.fungamems.game.screens.main

import com.rbxgolden.fungamems.businesModule.backend.Bt
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.adsmodule.AdSizeManager
import com.rbxgolden.fungamems.game.actors.AScrollPane
import com.rbxgolden.fungamems.game.actors.layout.AlignH
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.layout.linear.AVerticalGroup
import com.rbxgolden.fungamems.game.actors.panel.APanelTop
import com.rbxgolden.fungamems.game.actors.panel.dailyReward.ADialog
import com.rbxgolden.fungamems.game.actors.panel.dailyReward.APanelDailyReward
import com.rbxgolden.fungamems.game.utils.Block
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.TIME_ANIM_SCREEN
import com.rbxgolden.fungamems.game.utils.actor.animDelay
import com.rbxgolden.fungamems.game.utils.actor.animHide
import com.rbxgolden.fungamems.game.utils.actor.animHideAndDisable
import com.rbxgolden.fungamems.game.utils.actor.animShow
import com.rbxgolden.fungamems.game.utils.actor.animShowAndEnable
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import kotlinx.coroutines.launch

class DailyRewardScreen: AdvancedScreen() {

    // Рахується один раз; підписка на рекламу бере maxOf з ним, а не додає
    private var basePaddingBottom = 0f


    override val analyticsBt    = Bt.DAILY
    override val analyticsBlock = "daily_reward_screen"

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop         by lazy { APanelTop(this) }
    private val aPanelDailyReward by lazy { APanelDailyReward(this) }

    private val aVerticalGroup    by lazy { AVerticalGroup(this, alignH = AlignH.CENTER, wrap = true) }
    private val aScrollPane       by lazy { AScrollPane(aVerticalGroup) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.background_80)) }
    private val aDialog by lazy { ADialog(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, safeBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addScrollPane()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(WIDTH, 56f)
        add(aPanelTop) { centerX(); topToTop() }
        aPanelTop.setTitle("Daily Free Rbx")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addScrollPane() {
        aScrollPane.width = WIDTH
        add(aScrollPane) {
            centerX(); topToBottom(aPanelTop); bottomToBottom()
            matchHeight()
        }

        setUpVerticalGroup()
    }

    private fun setUpVerticalGroup() {
        aVerticalGroup.width = WIDTH

        val contentH = 600f
        aPanelDailyReward.setSize(WIDTH, contentH)
        aVerticalGroup.addActor(aPanelDailyReward)

        // Скільки треба добити знизу, щоб короткий контент заповнив ScrollPane
        basePaddingBottom = (aScrollPane.height - contentH).coerceAtLeast(0f)
        aVerticalGroup.paddingBottom = basePaddingBottom

        // ⚠️ Два «не так», на які легко наступити:
        // 1. Саме «=», а не «+=»: adBottomFlow це StateFlow — віддає поточне
        //    значення одразу і далі КОЖНУ зміну; з «+=» відступ накопичувався б
        //    (порожнеча між останнім елементом списку і банером росла).
        // 2. maxOf, а не сума: base добиває короткий контент до висоти pane,
        //    adBottom ховає його за рекламою — це ОДНА й та сама дірка знизу.
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect { runGDX { aVerticalGroup.paddingBottom = maxOf(basePaddingBottom, adBottomUI.coerceAtLeast(0f)) } }
        }

        aPanelDailyReward.onGetReward = { reward -> rootConstraintLayout.showDialog(reward) }
    }

    private fun AConstraintLayout.showDialog(reward: Long) {
        aDialog.onClaim = {
            aDimImg.animHideAndDisable(0.15f) { aDimImg.remove() }
            aDialog.animHideAndDisable(0.15f) { aDialog.isDisposeOnRemove = false; aDialog.remove() }
        }

        aDimImg.animHideAndDisable()
        aDialog.animHideAndDisable()

        add(aDimImg) { fillParent() }

        aDialog.setSize(316f, 282f)
        add(aDialog) {
            center()

            verticalBias = 0.7f
        }

        aDialog.setReward(reward)

        aDimImg.animShowAndEnable(TIME_ANIM_SCREEN)
        aDialog.animShowAndEnable(TIME_ANIM_SCREEN)
    }




}