package com.racing.funtols.game.actors.panel.pick

import com.racing.funtols.game.actors.layout.autoLayout.AAutoLayout
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.controller.PickController
import com.racing.funtols.game.utils.actor.animDelay
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

// ------------------------------------------------------------------------
// APanelPick — 5 каністр (3 + 2, другий ряд по центру)
// ------------------------------------------------------------------------
// Правила в PickController, тут тільки розкладка, анімації і звук.
class APanelPick(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    // Спроби скінчились — передаємо екрану підсумок (0 / 25 / 50)
    var onFinish: (totalReward: Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller = PickController(
        cardCount    = 5,
        winCount     = 2,
        picksAllowed = 2,
        rewardPerWin = 25L,
    )

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Пауза після останньої каністри — щоб гравець побачив результат
    // до появи попапа
    private val timeShowResult = 0.6f

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCanList = List(controller.cardCount) { ACan(screen) }

    private val aTable = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        wrap      = true,
        gapMain   = 8f,
        gapCross  = 8f,
        alignMain = AAutoLayout.AlignMain.CENTER,
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

        aCanList.forEachIndexed { index, can ->
            can.setSize(109f, 148f)
            can.onClick = { controller.onCardClick(index) }
            aTable.add(can)
        }
    }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private fun initController() {

        // Відкриваємо каністру і показуємо, що там
        controller.onReveal = { index, isWin ->
            //if (isWin) gdxGame.soundUtil.apply { play(REWARD) }

            aCanList[index].reveal(isWin) {
                controller.onRevealFinished()
            }
        }

        // Обидві спроби використані
        controller.onFinish = { totalReward ->
            animDelay(timeShowResult) { onFinish(totalReward) }
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Нова гра: перемішати призи і закрити всі каністри */
    fun newGame() {
        clearActions()
        controller.newGame()
        aCanList.forEach { it.reset() }
    }
}