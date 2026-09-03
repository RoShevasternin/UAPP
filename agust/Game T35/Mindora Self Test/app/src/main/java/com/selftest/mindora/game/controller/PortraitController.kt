package com.selftest.mindora.game.controller

import com.selftest.mindora.game.content.PortraitSynthesis
import com.selftest.mindora.game.content.SynthesisTitle
import com.selftest.mindora.game.content.TestCatalog
import com.selftest.mindora.game.content.TestRepository
import com.selftest.mindora.game.model.PlayerModel
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.runGDX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// ----------------------------------------------------------------------------
// PortraitController — єдине джерело правди для портрета.
//
//   Обслуговує ОБИДВА місця: панель прогресу на хабі («X of 5 dimensions
//   unlocked») і повний екран Your Portrait (картки тестів + синтез).
//
//   Портрет за макетом: 5 вимірів = 5 тестів, кожен пройдений тест = +1.
//   Синтез доступний з portrait_synthesis_threshold (4 of 5) пройдених
//   і відкривається через rewarded: екран показує ролик → completeSynthesis().
//
//   Титул синтезу фіксується назавжди при першому резолві. «Restart the
//   test» з макета = resetPortrait() — чистить титул (і, за бажанням,
//   результати) для повторного проходження.
// ----------------------------------------------------------------------------

class PortraitController(
    private val scope: CoroutineScope?,
    private val model: PlayerModel,
) {

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    data class DimensionCard(
        val testId        : String,
        val title         : String,   // назва теста ("Attachment Style")
        val done          : Boolean,
        val cost          : Long,     // 0 = Free
        val affordable    : Boolean,  // done=false і балансу вистачає
        val resultName    : String?,  // "Secure" — якщо done
        val resultTagline : String?,  // "Steady in love" — якщо done
    )

    data class State(
        val cards          : List<DimensionCard>,
        val unlockedCount  : Int,
        val totalCount     : Int,
        /** Скільки вимірів треба для синтезу. З конфігу, а не константа. */
        val threshold      : Int,
        val canSynthesize  : Boolean,          // поріг взято, синтез ще не зроблено
        val synthesis      : SynthesisTitle?,  // готовий портрет (якщо вже відкрито)
    )

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onRender: (State) -> Unit = {}

    /** Балансу не вистачає на цей тест — показати тост з макета. */
    var onNotEnoughLumens: (testId: String, cost: Long) -> Unit = { _, _ -> }

    /** Тест можна відкривати — екран веде юзера у флоу теста. */
    var onOpenTest: (testId: String) -> Unit = {}

    // ------------------------------------------------------------------------
    // Config
    // ------------------------------------------------------------------------
    private val config get() = gdxGame.activity.appConfig

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    fun initialize() {
        scope?.launch { model.testResultsFlow.collect { render() } }
        scope?.launch { model.lumensFlow.collect { render() } }
        scope?.launch { model.portraitTitleIdFlow.collect { render() } }
        render()
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /**
     * Тап по картці теста.
     *
     * ⚠️ БУВ БАГ: перевірявся тільки `resultOf != null`, тобто «пройдений».
     * Куплений, але ще не пройдений тест у цю умову не потрапляв — і
     * spendLumens намагався списати ціну ВДРУГЕ. Людина бачила картку
     * «Open», тиснула, і отримувала «не вистачає люменів» за те, що вже
     * оплатила.
     *
     * Тепер рішення ухвалює model.unlockTest — та сама точка, що на хабі.
     * Вона ідемпотентна: відкритий тест (куплений АБО пройдений) повертає
     * true без списання, а неоплачений списує рівно раз.
     */
    fun tapTest(testId: String) {
        if (model.unlockTest(testId)) onOpenTest(testId)
        else onNotEnoughLumens(testId, costOf(testId))
    }

    /** Чи активна кнопка «Unlock My Portrait». */
    fun canSynthesize(): Boolean =
        model.portraitTitleId() == null &&
                model.unlockedDimensions() >= config.portrait.synthesisThreshold

    /** Викликати ПІСЛЯ успішного rewarded на кнопці «Unlock My Portrait». */
    fun completeSynthesis() {
        if (!canSynthesize()) return

        val outcomes = model.testResults().mapValues { it.value.resultIds }
        val title    = PortraitSynthesis.resolve(outcomes)

        model.savePortraitTitle(title.id)   // тригерить collect → render
    }

    /** «Restart the test» на екрані результату: скинути синтез. */
    fun resetPortrait(alsoClearResults: Boolean = false) {
        model.clearPortraitTitle()
        if (alsoClearResults) model.clearTestResults()
    }

    // ------------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------------
    private fun render() = runGDX { onRender(buildState()) }

    private fun buildState(): State {
        val lumens     = model.getLumens()
        val results = model.testResults()

        val cards = TestRepository.ALL.map { id ->
            val content = TestRepository.get(id)
            val saved   = results[id]
            val cost    = costOf(id)
            val primary = saved?.resultIds?.firstOrNull()?.let(content::resultById)

            DimensionCard(
                testId        = id,
                title         = content.title,
                done          = saved != null,
                cost          = cost,
                affordable    = saved == null && (cost <= 0L || lumens >= cost),
                resultName    = primary?.name,
                resultTagline = primary?.tagline,
            )
        }

        val titleId   = model.portraitTitleId()
        val synthesis = titleId?.let { PortraitSynthesis.content.titleById(it) }

        return State(
            cards         = cards,
            unlockedCount = results.size,
            totalCount    = TestRepository.ALL.size,
            threshold     = config.portrait.synthesisThreshold,
            canSynthesize = canSynthesize(),
            synthesis     = synthesis,
        )
    }

    // Місток «рядковий id → ключ ціни» живе в TestCatalog і тільки там:
    // раніше цей when був другою копією тієї ж мапи, і при додаванні тесту
    // її треба було памʼятати оновити в двох місцях.
    private fun costOf(testId: String): Long =
        config.costs.of(TestCatalog.byId(testId).testId)
}