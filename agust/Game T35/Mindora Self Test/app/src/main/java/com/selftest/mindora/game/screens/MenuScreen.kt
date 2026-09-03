package com.selftest.mindora.game.screens

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.panel.APanelTopHome
import com.selftest.mindora.game.actors.debug.ADebugHud
import com.selftest.mindora.game.actors.debug.addDebugHud
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.panel.home.APanelHome
import com.selftest.mindora.game.actors.panel.home.main.APanelItemsMain
import com.selftest.mindora.game.actors.panel.home.more.APanelItemsMore
import com.selftest.mindora.game.actors.panel.home.test.APanelItemsTest
import com.selftest.mindora.game.actors.test.card.ACardTest
import com.selftest.mindora.game.actors.popup.APopup
import com.selftest.mindora.game.actors.popup.APopupMore
import com.selftest.mindora.game.actors.popup.APopupStart
import com.selftest.mindora.game.content.TestRepository

import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.TIME_ANIM_SCREEN
import com.selftest.mindora.game.utils.VERTICAL_BIAS
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animHideAndDisable
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.actor.animShowAndEnable
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.overlay.OverlayManager
import kotlin.getValue
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.selftest.mindora.game.utils.runGDX

class MenuScreen : AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Overlay
    // ------------------------------------------------------------------------
    private enum class Overlay { POPUP_START, POPUP_MORE, POPUP,  }

    private val overlayManager = OverlayManager(
        onShowDim = { aDimImg.animShowAndEnable(timeShow) },
        onHideDim = { aDimImg.animHideAndDisable(timeHide) },
    )

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val timeShow = 0.2f
    private val timeHide = 0.2f

    /** Один кадр при 60 fps — стільки чекаємо, поки HUG-висоти стануть на місце. */
    private val TIME_SETTLE = 1f / 60f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aDimImg      by lazy { Image(drawerUtil.getTexture(GameColor.black_0A001D_80)) }
    private val aPopupStart  by lazy { APopupStart(this) }
    private val aPopup       by lazy { APopup(this) }
    private val aPopupMore   by lazy { APopupMore(this) }

    private val aPanelTopHome by lazy { APanelTopHome(this) }
    private val aPanelHome    by lazy { APanelHome(this) }

    private val aPanelItemsMain by lazy { APanelItemsMain(this) }
    private val aPanelItemsTest by lazy { APanelItemsTest(this) }
    private val aPanelItemsMore by lazy { APanelItemsMore(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()

        // ПОЯВА ПІСЛЯ ТОГО, ЯК LAYOUT ОСІВ.
        //
        // На хабі багато вкладених HUG-груп (APanelHome → AAutoLayout →
        // картки), і їхні висоти рахуються в layout(), тобто НЕ в show(), а
        // на першому act(). Раніше fade-in стартував одночасно з цим
        // перерахунком, і було видно, як блоки стрибають на місце.
        //
        // validate() женемо примусово, а один кадр затримки лишаємо на
        // випадок, коли дитина інвалідувала батька вже під час валідації
        // (HUG змінює висоту → sizeChanged → invalidate у батька).
        stageUI.root.addAction(Actions.sequence(
            Actions.run { rootConstraintLayout.validate() },
            Actions.delay(TIME_SETTLE),
            Actions.run { animShowScreen() },
        ))

        gdxGame.analytics.openHomeScreen()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTopHome()
        addPanelHome()

        addDimImg()
        if (gdxGame.modelPlayer.getIsFirstOpen()) {
            gdxGame.modelPlayer.setIsFirstOpen(false)
            addPopupStart()
            overlayManager.show(Overlay.POPUP_START)
        }
        addPopup()
        addPopupMore()

        addDebugHud(ADebugHud(this@MenuScreen))
    }

    // ------------------------------------------------------------------------
    // Add Actors - Panel
    // ------------------------------------------------------------------------
    private fun AConstraintLayout.addPanelTopHome() {
        aPanelTopHome.setSize(344f, 48f)
        add(aPanelTopHome) { centerX(); topToTop(margin = 8f) }

        aPanelTopHome.onPanelRBX = { overlayManager.show(Overlay.POPUP_MORE) }
    }

    private fun AConstraintLayout.addPanelHome() {
        aPanelHome.width = 344f
        add(aPanelHome) { centerX(); topToBottom(aPanelTopHome, 24f); bottomToBottom(); matchHeight() }

        aPanelHome.apply {
            addPanelItemsMain()
            addPanelItemsTest()
            addPanelItemsMore()
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors - Items
    // ------------------------------------------------------------------------
    private fun APanelHome.addPanelItemsMain() {
        aPanelItemsMain.setSize(344f, 1f)
        addItem(aPanelItemsMain)

        // Портрет: прогрес і замок живуть від пройдених тестів, тож підписка,
        // а не разовий біндінг — повернувся з теста, картка вже оновлена.
        aPanelItemsMain.aPanelItemPortrait.onClick = {
            animHideScreen {
                gdxGame.navigationManager.navigate(
                    PortraitScreen::class.java.name,
                    MenuScreen::class.java.name,
                )
            }
        }

        // Два джерела: пройдені тести дають прогрес, титул портрета — замок.
        // combine, а не два collect: синтез міняє обидва майже одночасно, і
        // незалежні підписки дали б кадр з відкритим замком і старим прогресом.
        coroutine?.launch {
            combine(
                gdxGame.modelPlayer.testResultsFlow,
                gdxGame.modelPlayer.portraitTitleIdFlow,
            ) { results, titleId -> results.size to (titleId != null) }
                .collect { (done, portraitOpen) ->
                    runGDX {
                        aPanelItemsMain.aPanelItemPortrait.bind(
                            done         = done,
                            total        = TestRepository.ALL.size,
                            portraitOpen = portraitOpen,
                        )
                    }
                }
        }

        aPanelItemsMain.aPanelItemDaily.onClaim = {
            animHideScreen {
                gdxGame.navigationManager.navigate(DailyScreen::class.java.name, MenuScreen::class.java.name)
            }
        }
    }

    private fun APanelHome.addPanelItemsTest() {
        aPanelItemsTest.setSize(344f, 1f)
        addItem(aPanelItemsTest)

        aPanelItemsTest.aPanelCardsTest.onCardClick = { testId, state ->
            when (state) {
                // Не вистачає люменів: спершу кажемо ЧОМУ (інакше тап
                // виглядає як баг), і одразу даємо спосіб їх дістати.
                ACardTest.State.LOCKED -> {
                    val need = gdxGame.modelPlayer.costOf(testId) - gdxGame.modelPlayer.getLumens()
                    gdxGame.activity.showToast("Not enough lumens — you need $need more")
                    overlayManager.show(Overlay.POPUP_MORE)
                }

                // Купівля, повторний вхід і «Take Again» — одна гілка:
                // unlockTest сам вирішує, треба списувати чи вже відкрито.
                ACardTest.State.AFFORDABLE, ACardTest.State.PURCHASED, ACardTest.State.DONE -> openTest(testId)
            }
        }
    }

    private fun APanelHome.addPanelItemsMore() {
        aPanelItemsMore.setSize(344f, 1f)
        addItem(aPanelItemsMore)

        aPanelItemsMore.aPanelCardsMore.apply {
            onMemory = {}
            onWatch  = { overlayManager.show(Overlay.POPUP_MORE) }
        }
    }

    // ------------------------------------------------------------------------
    // Add Actors - Dim | Popup
    // ------------------------------------------------------------------------
    private fun AConstraintLayout.addDimImg() {
        aDimImg.animHideAndDisable()
        add(aDimImg) {
            matchConstraint()
            centerX(); bottomToBottom(); topToTop(margin = -safeStatusBarUI)
        }
        aDimImg.setOnClickListener(null) {
            if (overlayManager.isClosable) overlayManager.close()
        }
    }

    private fun AConstraintLayout.addPopupStart() {
        aPopupStart.animHideAndDisable()
        aPopupStart.setSize(362f, 443f)
        add(aPopupStart) { center(); verticalBias = VERTICAL_BIAS }

        overlayManager.register(
            Overlay.POPUP_START, OverlayManager.Config(
                showDim    = true,
                isClosable = false, // поки не забрав нагороду — не закривати кліком по фону
                onShow     = { aPopupStart.animShowAndEnable(timeShow) },
                onHide     = { aPopupStart.animHideAndDisable(timeHide) },
            ))

        aPopupStart.onStart = { overlayManager.close() }
    }

    private fun AConstraintLayout.addPopup() {
        aPopup.animHideAndDisable()
        aPopup.setSize(362f, 401f)
        add(aPopup) { center(); verticalBias = VERTICAL_BIAS }

        overlayManager.register(
            Overlay.POPUP, OverlayManager.Config(
                showDim    = true,
                isClosable = false, // поки не забрав нагороду — не закривати кліком по фону
                onShow     = { aPopup.animShowAndEnable(timeShow) },
                onHide     = { aPopup.animHideAndDisable(timeHide) },
            ))

        aPopup.onClaim = { overlayManager.close() }
    }

    private fun AConstraintLayout.addPopupMore() {
        aPopupMore.animHideAndDisable()
        aPopupMore.setSize(362f, 376f)
        add(aPopupMore) { center(); verticalBias = VERTICAL_BIAS }

        overlayManager.register(
            Overlay.POPUP_MORE, OverlayManager.Config(
                showDim    = true,
                isClosable = true,
                onShow     = { aPopupMore.animShowAndEnable(timeShow) },
                onHide     = { aPopupMore.animHideAndDisable(timeHide) },
            ))

        aPopupMore.onWatch = { reward ->
            aPopup.setReward(reward)
            overlayManager.close()
            gdxGame.activity.showInterstitial { overlayManager.show(Overlay.POPUP) }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    /**
     * Списати ціну (якщо ще не куплено) і відкрити тест.
     *
     * Списання ТУТ, а не в TestScreen: юзер бачить, як баланс падає, ще на
     * хабі — і якщо передумає та вийде з теста, повторний вхід уже
     * безкоштовний. Якби списували на старті теста, кожен вхід коштував би
     * знову.
     */
    private fun openTest(testId: String) {
        if (!gdxGame.modelPlayer.unlockTest(testId)) {
            // Люмени встигли витратитись між показом картки і тапом.
            overlayManager.show(Overlay.POPUP_MORE)
            return
        }
        animHideScreen {
            gdxGame.navigationManager.navigate(
                TestScreen::class.java.name,
                MenuScreen::class.java.name,
                key = TestRepository.ALL.indexOf(testId),
            )
        }
    }

}