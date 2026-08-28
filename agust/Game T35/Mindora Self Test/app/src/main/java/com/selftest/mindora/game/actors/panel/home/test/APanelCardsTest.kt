package com.selftest.mindora.game.actors.panel.home.test

import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.actors.test.card.ACardTest
import com.selftest.mindora.game.content.TestCatalog
import com.selftest.mindora.game.content.TestRepository
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.runGDX
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// ═════════════════════════════════════════════════════════════════════════════
//  APanelCardsTest — 5 карток тестів.
//
//  САМОСИНХРОНІЗУЄТЬСЯ. Підписана на баланс, пройдені й куплені тести, тож
//  стани перераховуються самі: купив — картка стала «Open», пройшов —
//  «Take Again», забрав дейлі й перетнув ціну — сіра стала кольоровою.
//  Екрану не треба нічого «оновлювати» після повернення з теста.
//
//  ЧОМУ combine, А НЕ ТРИ ОКРЕМІ collect: стан картки залежить від УСІХ
//  трьох джерел одночасно, і купівля міняє два з них в одну мить (люмени
//  вниз, purchased вгору). Три незалежні collect дали б кадр, де ціну вже
//  списано, а тест ще не куплений — тобто картка мигне як LOCKED.
// ═════════════════════════════════════════════════════════════════════════════
class APanelCardsTest(screen: AdvancedScreen) : AAutoLayout(
    screen     = screen,
    direction  = Direction.VERTICAL,
    gapMain    = 8f,
    sizingH    = Sizing.HUG,
    alignCross = AlignCross.CENTER,
) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val cards = TestCatalog.ALL.map { ACardTest(screen) }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * Тап по картці. Панель НЕ купує і НЕ навігує сама — вона лише
     * повідомляє, ЩО натиснули і в якому стані картка була. Рішення
     * (списати люмени / відкрити тест / показати тост і попап) приймає
     * екран: тільки він володіє оверлеями й навігацією.
     */
    var onCardClick: (testId: String, state: ACardTest.State) -> Unit = { _, _ -> }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addCards()

        // ПЕРШИЙ БІНДІНГ — СИНХРОННО, до першого кадру.
        //
        // observeState() збирає флоу на Dispatchers.Default і повертається в
        // GDX-потік через runGDX, тобто найраніше на НАСТУПНОМУ кадрі. Кадр
        // між цим показував порожні картки з нульовими розмірами, HUG-батько
        // перераховувався — і весь екран смикався на появі.
        refreshNow()
        observeState()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addCards() {
        cards.forEachIndexed { i, card ->
            card.setSize(ACardTest.W, ACardTest.H)
            add(card)

            val entry = TestCatalog.ALL[i]
            // card.state читаємо в момент тапу, а не при підписці: до кліку
            // стан міг помінятись (забрали дейлі, купили в іншому місці).
            card.onClick = { onCardClick(entry.id, card.state) }
        }
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    /** Поточний стан без флоу — для першого кадру. */
    private fun refreshNow() {
        val model = gdxGame.modelPlayer
        refresh(model.getLumens(), model.testResults().keys, model.purchasedTests())
    }

    private fun observeState() {
        val model = gdxGame.modelPlayer

        coroutine?.launch {
            combine(
                model.lumensFlow,
                model.testResultsFlow,
                model.purchasedTestsFlow,
            ) { lumens, results, purchased ->
                Triple(lumens, results.keys, purchased)
            }.collect { (lumens, doneIds, purchased) ->
                runGDX { refresh(lumens, doneIds, purchased) }
            }
        }
    }

    private fun refresh(balance: Long, doneIds: Set<String>, purchasedIds: Set<String>) {
        val costs = gdxGame.activity.appConfig.costs

        TestCatalog.ALL.forEachIndexed { i, entry ->
            val card  = cards[i]
            val price = costs.of(entry.testId)

            val state = card.resolveState(
                isDone      = entry.id in doneIds,
                isPurchased = entry.id in purchasedIds,
                balance     = balance,
                price       = price,
            )

            card.bind(
                title     = TestRepository.get(entry.id).title,
                desc      = entry.subtitle,
                iconIndex = entry.index,
                price     = price,
                state     = state,
                animated  = true,
            )
        }
    }
}