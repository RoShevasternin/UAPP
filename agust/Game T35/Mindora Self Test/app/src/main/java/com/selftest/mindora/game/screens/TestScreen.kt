package com.selftest.mindora.game.screens

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.panel.APanelTop
import com.selftest.mindora.game.actors.test.AItemTestOption
import com.selftest.mindora.game.actors.test.AProgressTestBar
import com.selftest.mindora.game.actors.test.AScaleSelector
import com.selftest.mindora.game.content.TestRepository
import com.selftest.mindora.game.controller.TestController
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.TIME_ANIM_SCREEN
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
// TestScreen — проходження теста (Figma 1875:25260 / 25332).
//
// Розмітка (376×815, координати макета):
//   Header 344×48        @ y=64   — назва теста + назад
//   Progress 344×31      @ y=136  — «Question X of N» + бар 344×5
//   Question 344×58      @ y=199  — текст питання, до 2 рядків
//   Answers              @ +82    — 2..4 картки 344×64 (choice)
//                                   АБО шкала 1..N (scale)
//
// ЯКИЙ ТЕСТ ВІДКРИВАТИ: NavigationManager.key = індекс у TestRepository.ALL.
// Ключ читається один раз у show() — далі живе контролер.
//
// НАЗАД: по питаннях, а не з екрана. З першого питання — вихід. Тому
// у APanelTop перевизначено onBack.
//
// ПІДТВЕРДЖЕННЯ НЕМАЄ: у макеті на екранах питань немає кнопки Confirm —
// тап по варіанту підсвічує його на мить і сам гортає далі. Проти
// подвійних тапів — inputLocked на час анімації переходу.
// ═════════════════════════════════════════════════════════════════════════════
class TestScreen : AdvancedScreen() {

    companion object {
        /** Пауза «побачити свій вибір» перед перегортанням. */
        private const val TIME_PICKED = 0.22f
        private const val TIME_SWAP   = 0.15f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleProgress = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, GameColor.white_70)
    private val styleQuestion = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 20f)

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    // key приїжджає з navigate(...) і живе лише до наступного переходу,
    // тому контролер створюється лінива-разово і далі тримає testId сам.
    private val controller by lazy {
        val id = TestRepository.ALL.getOrNull(gdxGame.navigationManager.key ?: -1)
            ?: TestRepository.ALL.first()
        TestController(id, gdxGame.modelPlayer)
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop    by lazy { APanelTop(this) }
    private val aProgressLbl by lazy { AMsdfLabel("", styleProgress) }
    private val aProgressBar by lazy { AProgressTestBar(this) }
    private val aQuestionLbl by lazy { AMsdfLabel("", styleQuestion) }

    private val aOptionsLayout by lazy {
        AAutoLayout(
            screen    = this,
            direction = AAutoLayout.Direction.VERTICAL,
            gapMain   = 8f,
            sizingH   = AAutoLayout.Sizing.HUG,
        )
    }
    private val aOptions = mutableListOf<AItemTestOption>()

    private val aScale by lazy { AScaleSelector(this) }

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Тап уже прийнято, йде анімація переходу — решту тапів ігноруємо.
    private var inputLocked = false

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
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
        addPanelTop()
        addProgress()
        addQuestion()
        addAnswers()

        bindQuestion(animated = false)
    }

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 48f)
        add(aPanelTop) { centerX(); topToTop(margin = 8f) }

        aPanelTop.setTitle(controller.test.title)

        // Назад = попереднє питання; з першого — вихід з теста.
        aPanelTop.onBack = {
            if (!inputLocked) {
                if (controller.back()) bindQuestion(animated = true, backwards = true)
                else animHideScreen { gdxGame.navigationManager.back() }
            }
        }
    }

    private fun AConstraintLayout.addProgress() {
        aProgressLbl.setSize(344f, 14f)
        add(aProgressLbl) { centerX(); topToBottom(aPanelTop, 24f) }
        aProgressLbl.setAlignment(Align.left)

        aProgressBar.setSize(344f, 5f)
        add(aProgressBar) { centerX(); topToBottom(aProgressLbl, 12f) }
    }

    private fun AConstraintLayout.addQuestion() {
        aQuestionLbl.setSize(344f, 58f)
        add(aQuestionLbl) { centerX(); topToBottom(aProgressBar, 32f) }
        aQuestionLbl.setAlignment(Align.topLeft)
        aQuestionLbl.setWrap(true)
    }

    private fun AConstraintLayout.addAnswers() {
        if (controller.test.isScale) {
            aScale.setSize(344f, aScale.fullHeight)
            add(aScale) { centerX(); topToBottom(aQuestionLbl, 24f) }
            aScale.onPick = { value -> onAnswered(value) }
        } else {
            // Пул на максимум (4): біндінг лише перемикає видимість — без
            // перезбирання ієрархії на кожне питання. AAutoLayout НЕ фільтрує
            // невидимих — приховані слоти лишаються в розкладці, але вони
            // хвостові й під ними нічого немає, тож візуально це непомітно,
            // а невидимий актор у scene2d не ловить тапи.
            aOptionsLayout.setSize(344f, 1f)
            add(aOptionsLayout) { centerX(); topToBottom(aQuestionLbl, 24f) }

            repeat(4) { i ->
                val item = AItemTestOption(this@TestScreen)
                item.setSize(344f, 64f)
                aOptionsLayout.add(item)
                item.onPick = { onOptionPicked(i) }
                aOptions += item
            }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun onOptionPicked(optionIndex: Int) {
        if (inputLocked) return
        if (optionIndex >= controller.question.options.size) return

        aOptions.forEachIndexed { i, item -> item.setSelected(i == optionIndex) }
        onAnswered(optionIndex)
    }

    private fun onAnswered(value: Int) {
        if (inputLocked) return
        inputLocked = true

        // Дати оку зафіксувати вибір, потім гортати.
        stageUI.root.addAction(Actions.sequence(
            Actions.delay(TIME_PICKED),
            Actions.run {
                if (controller.answer(value)) bindQuestion(animated = true)
                else finishTest()
            }
        ))
    }

    /**
     * Показати поточне питання контролера.
     * animated: перегортання (fade контенту); backwards — тільки семантика
     * для майбутніх напрямних анімацій, зараз перехід однаковий.
     */
    private fun bindQuestion(animated: Boolean, backwards: Boolean = false) {
        val apply = {
            aProgressLbl.setText(controller.progressText)
            aProgressBar.setProgress(controller.progressFraction, animated)
            aQuestionLbl.setText(controller.question.text)

            if (controller.test.isScale) {
                aScale.bind(controller.test.scaleSize, controller.currentAnswer)
            } else {
                val opts = controller.question.options
                aOptions.forEachIndexed { i, item ->
                    val visible = i < opts.size
                    item.isVisible = visible
                    if (visible) {
                        item.setText(opts[i].text)
                        item.setSelected(selected = (controller.currentAnswer == i), animated = false)
                    }
                }
                aOptionsLayout.invalidate()
            }
            inputLocked = false
        }

        if (!animated) { apply(); return }

        // Гортаємо тільки контент питання — хедер і прогрес лишаються.
        val content = listOf(aQuestionLbl, if (controller.test.isScale) aScale else aOptionsLayout)
        inputLocked = true
        content.forEach { it.animHide(TIME_SWAP) }
        stageUI.root.addAction(Actions.sequence(
            Actions.delay(TIME_SWAP),
            Actions.run {
                apply()
                content.forEach { it.animShow(TIME_SWAP) }
            }
        ))
    }

    private fun finishTest() {
        val outcome = controller.finish()

        // TODO(result): за макетом далі екран «рахуємо результат» (лоадер) і
        //  екран результату. Поки їх немає — повертаємось на хаб: результат
        //  уже збережений, картка портрета підхопить +1 вимір через flow.
        animHideScreen { gdxGame.navigationManager.back() }
    }
}