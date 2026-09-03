package com.selftest.mindora.game.screens

import com.selftest.mindora.game.actors.AScrollPane
import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.panel.APanelTop
import com.selftest.mindora.game.actors.panel.home.test.APanelCardsTest
import com.selftest.mindora.game.actors.portrait.APanelPortrait
import com.selftest.mindora.game.actors.test.card.ACardTest
import com.selftest.mindora.game.content.TestRepository
import com.selftest.mindora.game.controller.PortraitController
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.TIME_ANIM_SCREEN
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.runGDX

// ═════════════════════════════════════════════════════════════════════════════
//  PortraitScreen — «Your Portrait»: блок синтезу + той самий список тестів,
//  що на хабі.
//
//  СПИСОК ПЕРЕВИКОРИСТАНИЙ (APanelCardsTest): він сам підписаний на баланс,
//  пройдені й куплені тести, тож на цьому екрані оновлюється так само, як на
//  хабі — окремої синхронізації не треба.
//
//  ЛОГІКУ ТРИМАЄ PortraitController: він уже вміє все — стан карток, поріг
//  синтезу, списання люменів при тапі. Екран лише малює State і передає тапи.
//
//  ЧОМУ СИНТЕЗ ЗА РЕКЛАМУ: «Unlock My Portrait» — найцінніший момент у
//  застосунку, і за макетом він платний переглядом. Тому completeSynthesis
//  викликається ТІЛЬКИ в колбеку showInterstitial.
// ═════════════════════════════════════════════════════════════════════════════
class PortraitScreen : AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy {
        PortraitController(scope = coroutine, model = gdxGame.modelPlayer)
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop by lazy { APanelTop(this) }

    private val aContent by lazy {
        AAutoLayout(
            screen     = this,
            direction  = AAutoLayout.Direction.VERTICAL,
            gapMain    = 12f,
            sizingH    = AAutoLayout.Sizing.HUG,
            alignCross = AAutoLayout.AlignCross.CENTER,
        )
    }
    private val aScrollPane by lazy { AScrollPane(aContent) }

    private val aPanelPortrait by lazy { APanelPortrait(this) }
    private val aPanelCards    by lazy { APanelCardsTest(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Захист від подвійного тапу по «Unlock My Portrait» поки крутиться ролик.
    private var synthesizing = false

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

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
        addPanelTop()
        addContent()

        initController()
    }

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 48f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }
        aPanelTop.setTitle("Your Portrait")
    }

    private fun AConstraintLayout.addContent() {
        aContent.width = 344f

        // matchHeight() обов'язковий: два вертикальні якорі самі по собі
        // висоту не задають — heightMode за замовчуванням FIXED.
        aScrollPane.setSize(344f, 1f)
        add(aScrollPane) {
            matchHeight()
            centerX()
            topToBottom(aPanelTop, 16f)
            bottomToBottom(margin = 16f)
        }

        aPanelPortrait.setSize(APanelPortrait.W, APanelPortrait.H)
        aContent.add(aPanelPortrait)
        aPanelPortrait.onUnlock = { unlockPortrait() }

        aPanelCards.setSize(344f, 1f)
        aContent.add(aPanelCards)
        aPanelCards.onCardClick = { testId, state -> onCardClick(testId, state) }

        aContent.minH = aScrollPane.height
        aContent.invalidate()
    }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private fun initController() {
        controller.onRender = { state -> runGDX { aPanelPortrait.render(state) } }

        controller.onNotEnoughLumens = { _, cost ->
            gdxGame.activity.showToast("Not enough lumens — you need $cost")
        }

        controller.onOpenTest = { testId ->
            animHideScreen {
                gdxGame.navigationManager.navigate(
                    TestScreen::class.java.name,
                    PortraitScreen::class.java.name,   // назад — сюди ж
                    key = TestRepository.ALL.indexOf(testId),
                )
            }
        }

        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    /**
     * Тап по картці теста. Рішення «купити / відкрити / відмовити» ухвалює
     * контролер — екран лише не пускає туди свідомо закриті картки, щоб
     * tapTest не списав люмени там, де людина бачила сіру кнопку.
     */
    private fun onCardClick(testId: String, state: ACardTest.State) {
        if (state == ACardTest.State.LOCKED) {
            val need = gdxGame.modelPlayer.costOf(testId) - gdxGame.modelPlayer.getLumens()
            gdxGame.activity.showToast("Not enough lumens — you need $need more")
            return
        }
        controller.tapTest(testId)
    }

    private fun unlockPortrait() {
        if (synthesizing || !controller.canSynthesize()) return
        synthesizing = true

        gdxGame.activity.showInterstitial {
            // Колбек приходить з UI-потоку Android — без runGDX зміна сцени
            // з чужого потоку ламає рендер мовчки.
            runGDX {
                controller.completeSynthesis()   // тригерить collect → onRender
                synthesizing = false
            }
        }
    }
}