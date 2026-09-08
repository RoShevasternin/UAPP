package com.selftest.mindora.game.screens

import com.selftest.mindora.game.actors.AScrollPane
import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.panel.APanelTop
import com.selftest.mindora.game.actors.portrait.APanelPersonalPortrait
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.selftest.mindora.game.content.PortraitSynthesis
import com.selftest.mindora.game.content.SynthesisTitle
import com.selftest.mindora.game.controller.PortraitController
import com.selftest.mindora.game.utils.ShareCardRenderer
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.TIME_ANIM_SCREEN
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.runGDX

// ═════════════════════════════════════════════════════════════════════════════
//  PersonalPortraitScreen — зібраний портрет: «YOUR PERSONAL PORTRAIT».
//
//  Куди веде: PortraitScreen → «Unlock My Portrait» → ролик → синтез → сюди.
//
//  ДАНІ З МОДЕЛІ, а не з навігації: титул уже збережений у PlayerData
//  (completeSynthesis), тож екран переживає перезапуск процесу і його можна
//  відкрити повторно, не переграваючи рекламу.
//
//  ВИХІД ПО ЗНИКНЕННЮ ТИТУЛА. «Restart the test» чистить портрет, і замість
//  окремої гілки навігації екран просто слухає той самий State: прийшов
//  synthesis == null — показувати більше нема чого, йдемо назад. Одна
//  причина виходу замість двох шляхів, які треба тримати в синхроні.
// ═════════════════════════════════════════════════════════════════════════════
class PersonalPortraitScreen : AdvancedScreen() {

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

    private val aPanelPortrait by lazy { APanelPersonalPortrait(this) }

    /** Створюється на першому шері й живе до кінця екрана — FBO і сцена
     *  всередині надто дорогі, щоб піднімати їх на кожен тап. */
    private val shareRenderer by lazy {
        ShareCardRenderer(this, "portrait.png").also { disposableSet.add(it) }
    }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    /**
     * Титул, яким панель уже заповнена. Потрібен, бо контролер підписаний на
     * ТРИ потоки (люмени, результати, титул) і на старті віддає State кілька
     * разів — без цього поля кожен зайвий render перебудовував би рядки і
     * згортав уже розкриті.
     */
    private var boundTitleId: String? = null

    /** Титул, показаний зараз. Потрібен шеру — контролер State не зберігає. */
    private var boundTitle: SynthesisTitle? = null

    /** Захист від подвійного back, поки грає анімація виходу. */
    private var leaving = false

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

        aPanelPortrait.setSize(APanelPersonalPortrait.W, 1f)
        aContent.add(aPanelPortrait)

        aPanelPortrait.onShare   = { shareResult() }
        aPanelPortrait.onRestart = { restartPortrait() }

        // Панель міряє себе в bind() ПІСЛЯ add(), а розкриття рядків міняє її
        // висоту й пізніше. AAutoLayout цього не бачить сам — він перераховує
        // на власний invalidate, а не на sizeChanged дитини.
        aPanelPortrait.onHeightChanged = { aContent.invalidate() }

        aContent.minH = aScrollPane.height

        // Панель міряє себе аж у bind(), коли контролер віддасть State, але
        // HUG-висота контенту рахується тут і зараз — без invalidate вона
        // лишилась би одиницею, як уже було в ResultScreen.
        aContent.invalidate()
    }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private fun initController() {
        controller.onRender = { state -> runGDX { render(state) } }
        controller.initialize()
    }

    private fun render(state: PortraitController.State) {
        val title = state.synthesis

        if (title == null) {
            // Портрет щойно скинули — виходимо (див. шапку файлу).
            if (!leaving) {
                leaving = true
                animHideScreen { gdxGame.navigationManager.back() }
            }
            return
        }

        if (title.id == boundTitleId) return
        boundTitleId = title.id
        boundTitle   = title

        aPanelPortrait.bind(title, PortraitSynthesis.content.header, state)
        aContent.invalidate()
    }

    // ------------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------------
    /**
     * Шер зібраного портрета.
     *
     * Арт той самий, що на панелі: мапінга «титул → картинка» немає, за
     * макетом усі титули діляться одним круглим артом.
     */
    private fun shareResult() {
        val title = boundTitle ?: return

        shareRenderer.card.bind(
            kicker   = PortraitSynthesis.content.header,
            name     = title.name,
            tagline  = title.tagline,
            art      = TextureRegionDrawable(gdxGame.assetsAll.ICON_PORTRAIT_RESULT),
            artRatio = 1f,
        )

        gdxGame.activity.shareImage(shareRenderer.render() ?: return)
    }

    /**
     * «Restart the test» — знімає ТІЛЬКИ титул портрета.
     *
     * Результати тестів лишаються: частина з них куплена за люмени, і стирати
     * їх з кнопки без попередження означало б забрати оплачене. Людина
     * повертається на PortraitScreen і може зібрати портрет наново.
     */
    private fun restartPortrait() {
        controller.resetPortrait(alsoClearResults = false)
    }
}
