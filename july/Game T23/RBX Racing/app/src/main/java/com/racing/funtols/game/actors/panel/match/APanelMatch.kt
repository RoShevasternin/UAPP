package com.racing.funtols.game.actors.panel.match

import com.racing.funtols.game.actors.layout.autoLayout.AAutoLayout
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.controller.MatchController
import com.racing.funtols.game.utils.actor.animDelay
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

// ------------------------------------------------------------------------
// APanelMatch — сітка 3×4 з 12 карток
// ------------------------------------------------------------------------
// Панель нічого не вирішує сама: усі правила в MatchController,
// а тут тільки анімації та зв'язок "клік → контролер → анімація".
class APanelMatch(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onWin: () -> Unit = {}

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller = MatchController(pairCount = 6)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val cardsRegion = gdxGame.assetsAll.listMatch

    private val timeShowPair  = 0.25f // пауза після вгаданої пари
    private val timeShowWrong = 0.7f  // скільки видно невірну пару перед закриттям

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCardList = List(controller.cardCount) { ACard(screen) }

    private val aTable = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        wrap      = true,
        gapMain   = 8f,
        gapCross  = 8f
    )

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addTable()
        initController()
        newGame()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addTable() {
        add(aTable) { fillParent() }

        aCardList.forEachIndexed { index, card ->
            card.setSize(109f, 109f)
            card.onClick = { controller.onCardClick(index) }
            aTable.add(card)
        }
    }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private fun initController() {

        // Відкрити картку
        controller.onOpen = { index ->
            aCardList[index].open()
        }

        // Пара збіглась — картки лишаються відкритими
        controller.onMatch = { first, second ->
            aCardList[first].setMatched()
            aCardList[second].setMatched()

            // Невелика пауза, щоб гравець побачив результат, і знімаємо блок
            animDelay(timeShowPair) { controller.onCompareFinished() }
        }

        // Пара не збіглась — тримаємо відкритими, потім закриваємо обидві
        controller.onMismatch = { first, second ->
            animDelay(timeShowWrong) {
                aCardList[first].close()
                aCardList[second].close { controller.onCompareFinished() }
            }
        }

        // Усі 6 пар знайдено
        controller.onWin = {
            onWin()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Нова роздача: перемішати колоду і закрити всі картки */
    fun newGame() {
        clearActions()
        controller.newGame()

        aCardList.forEachIndexed { index, card ->
            card.reset()
            card.setFace(cardsRegion[controller.faceOf(index)])
        }
    }
}