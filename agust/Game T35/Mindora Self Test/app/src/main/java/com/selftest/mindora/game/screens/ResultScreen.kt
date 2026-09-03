package com.selftest.mindora.game.screens

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.AScrollPane
import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.panel.APanelTop
import com.selftest.mindora.game.actors.popup.APopupUnlockResult
import com.selftest.mindora.game.actors.result.ACardResultSingle
import com.selftest.mindora.game.actors.result.ACardResultTrait
import com.selftest.mindora.game.actors.vfx.ABlurBack
import com.selftest.mindora.game.content.TestCatalog
import com.selftest.mindora.game.content.TestRepository
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.TIME_ANIM_SCREEN
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animHideAndDisable
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.actor.animShowAndEnable
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.runGDX

// ═════════════════════════════════════════════════════════════════════════════
//  ResultScreen — результат пройденого теста.
//
//  ДВА РЕЖИМИ, один екран:
//    per_axis (big_five) → 5 карток з розкриттям  → ACardResultTrait
//    решта 4 тести       → одна велика картка      → ACardResultSingle
//
//  ЛОГІКА ВІДКРИТТЯ:
//    показ → результат намальований, але розмитий (ABlurBack) → dim + попап
//    → «Unlock» → interstitial → blur знімається, попап зникає.
//
//  ДВІ ПАСТКИ, які вже стріляли:
//
//  1. ABlurBack з isStaticEffect робить знімок ОДИН раз — на першому draw.
//     А перший draw іде при alpha=0 (екран ще фейдиться) → знімок чорний, і
//     він застигає назавжди. Тому static вмикається ТІЛЬКИ ПІСЛЯ animShowScreen.
//
//  2. ABlurBack тримає Image на весь екран, а Image за замовчуванням ловить
//     тапи. Після зняття блюру він невидимий, але ВСЕ ЩЕ перехоплював клік по
//     кнопках під собою. Тому touchable = disabled одразу при створенні.
//
//  ДАНІ З МОДЕЛІ, не з навігації: TestScreen уже зберіг outcome, тож екран
//  переживає перезапуск процесу і відкривається повторно з хаба.
// ═════════════════════════════════════════════════════════════════════════════
class ResultScreen : AdvancedScreen() {

    companion object {
        private const val BLUR_RADIUS = 8f
        private const val TIME_FADE   = 0.25f
    }

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------
    private val testId by lazy {
        TestRepository.ALL.getOrNull(gdxGame.navigationManager.key ?: -1)
            ?: TestRepository.ALL.first()
    }

    private val test  by lazy { TestRepository.get(testId) }
    private val entry by lazy { TestCatalog.byId(testId) }

    /** id результатів у порядку, який дав скоринг. Порожньо = тест не пройдено. */
    private val resultIds by lazy {
        gdxGame.modelPlayer.testResults()[testId]?.resultIds ?: emptyList()
    }

    private val isMulti by lazy { test.resultRule == "per_axis" }

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

    private val aSingleCard by lazy { ACardResultSingle(this) }
    private val aTraitCards = mutableListOf<ACardResultTrait>()

    private val aBlurBack by lazy { ABlurBack(this) }
    private val aDimImg   by lazy { Image(drawerUtil.getTexture(GameColor.black_0A001D_80)) }
    private val aPopup    by lazy { APopupUnlockResult(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Захист від подвійного тапу по «Unlock» і від другого колбека реклами.
    private var unlocked = false

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()

        // Static лише коли екран ПОВНІСТЮ видимий — інакше знімок чорний
        // (пастка 1 у шапці). До того ABlurBack перезнімає щокадру: дорого,
        // але це 0.2 секунди.
        animShowScreen {
            aBlurBack.isStaticEffect = true
        }
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
        addBlur()
        addDimImg()
        addPopup()

        lockResult()
    }

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 48f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }
        aPanelTop.setTitle(test.title)
    }

    private fun AConstraintLayout.addContent() {
        aContent.width = 344f

        // ⚠️ matchHeight() ОБОВ'ЯЗКОВИЙ. Два вертикальні якорі САМІ по собі
        // висоту не задають: за замовчуванням heightMode = FIXED, і вони лише
        // позиціонують актор. Без цього рядка ScrollPane лишався заввишки 1px,
        // тобто на екрані не було видно НІЧОГО — ні карток, ні через блюр.
        aScrollPane.setSize(344f, 1f)
        add(aScrollPane) {
            matchHeight()
            centerX()
            topToBottom(aPanelTop, 16f)
            bottomToBottom(margin = 16f)
        }

        if (isMulti) addTraitCards() else addSingleCard()

        // Той самий прийом, що в APanelHome: контент не може бути нижчим за
        // вьюпорт, інакше AlignCross.CENTER притискає єдину картку до низу.
        aContent.minH = aScrollPane.height

        // Картки виміряли себе в bind() ПІСЛЯ add(): AAutoLayout цього не
        // бачить — він перераховує на власний invalidate, а не на sizeChanged
        // дитини. Без цього HUG-висота контенту лишилась би 1.
        aContent.invalidate()
    }

    private fun addSingleCard() {
        val result = resultIds.firstOrNull()?.let { test.resultById(it) } ?: return

        aSingleCard.setSize(ACardResultSingle.W, 1f)
        aContent.add(aSingleCard)
        aSingleCard.bind(entry.resultKicker, entry.index, result)

        aSingleCard.onShare       = { shareResult() }
        aSingleCard.onAddPortrait = { addToPortrait() }
        aSingleCard.onNextTest    = { openNextTest() }
    }

    private fun addTraitCards() {
        resultIds.forEachIndexed { i, id ->
            val result = test.resultById(id) ?: return@forEachIndexed

            val card = ACardResultTrait(this)
            card.setSize(ACardResultTrait.W, 1f)
            aContent.add(card)
            card.bind(i, result)

            card.onHeightChanged = { aContent.invalidate() }
            aTraitCards += card
        }
    }

    private fun AConstraintLayout.addBlur() {
        // fillParent задає розмір сам — зашивати 376×815 не треба, і на
        // екрані іншої пропорції зашите число дало б обрізаний знімок.
        add(aBlurBack) { fillParent() }

        // Пастка 2: Image усередині ловив би тапи навіть після зняття блюру.
        aBlurBack.touchable = Touchable.disabled
    }

    private fun AConstraintLayout.addDimImg() {
        add(aDimImg) { fillParent() }
        aDimImg.color.a = 0f
        aDimImg.touchable = Touchable.disabled
    }

    private fun AConstraintLayout.addPopup() {
        aPopup.setSize(328f, 182f)
        add(aPopup) { centerX(); centerY() }
        aPopup.color.a = 0f

        aPopup.onUnlock = { watchAdAndUnlock() }
    }

    // ------------------------------------------------------------------------
    // Lock / unlock
    // ------------------------------------------------------------------------
    private fun lockResult() {
        aBlurBack.radiusBlur = BLUR_RADIUS
        // isStaticEffect ставиться в show() після фейду — не тут.

        aDimImg.animShowAndEnable(TIME_FADE)
        aPopup.animShowAndEnable(TIME_FADE)
    }

    private fun watchAdAndUnlock() {
        if (unlocked) return
        unlocked = true

        // Колбек реклами приходить з UI-потоку Android → runGDX обов'язковий,
        // інакше зміна сцени з чужого потоку ламає рендер мовчки.
        gdxGame.activity.showInterstitial {
            runGDX { unlockResult() }
        }
    }

    private fun unlockResult() {
        aPopup.animHideAndDisable(TIME_FADE)
        aDimImg.animHideAndDisable(TIME_FADE)

        // radiusBlur = 0 → ABlurBack.draw виходить одразу, нічого не малює і
        // не перезнімає — контент під ним видно як є.
        aBlurBack.radiusBlur = 0f
    }

    // ------------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------------
    private fun shareResult() {
        // TODO(share): системний share-sheet зі скріншотом картки (AScreenShot).
    }

    private fun addToPortrait() {
        // Результат уже збережений у TestScreen.finish() — портрет його бачить.
        animHideScreen { gdxGame.navigationManager.back() }
    }

    /**
     * «Open next test» — НАСТУПНИЙ ПО ПОРЯДКУ від поточного, по колу.
     *
     * Порядок — TestRepository.ALL, той самий, що на хабі. Обхід кільцевий:
     * з останнього теста ведемо на перший, інакше на big_five кнопка
     * перетворювалась би на «вихід».
     *
     * ДВА ПРОХОДИ, і саме в такому порядку:
     *   1. відкритий і ще НЕ пройдений — найкорисніше, це нове для людини;
     *   2. якщо таких немає — просто відкритий, хай і пройдений: перепройти
     *      можна безкоштовно, і це краще за глухий вихід на хаб.
     *
     * ⚠️ Некуплені пропускаємо. Не тому, що «не можна показувати», а тому що
     * тап по кнопці не має мовчки списувати люмени — покупка лишається одним
     * явним жестом на картці хаба.
     */
    private fun openNextTest() {
        val model = gdxGame.modelPlayer
        val order = TestRepository.ALL
        val from  = order.indexOf(testId)

        // Кільце: наступний, наступний-за-ним … і назад до поточного (без нього).
        val ring = (1 until order.size).map { order[(from + it) % order.size] }

        val next = ring.firstOrNull { model.isTestUnlocked(it) && it !in model.testResults() }
            ?: ring.firstOrNull { model.isTestUnlocked(it) }

        animHideScreen {
            if (next != null) {
                gdxGame.navigationManager.navigate(
                    TestScreen::class.java.name,
                    MenuScreen::class.java.name,
                    key = order.indexOf(next),
                )
            } else {
                gdxGame.navigationManager.back()
            }
        }
    }
}