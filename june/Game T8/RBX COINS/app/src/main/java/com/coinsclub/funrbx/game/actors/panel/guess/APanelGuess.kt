package com.coinsclub.funrbx.game.actors.panel.guess

import com.coinsclub.funrbx.game.actors.layout.autoLayout.AAutoLayout
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen

class APanelGuess(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listItems = List(9) { AItemGuess(screen) }
    private val aTable    = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        wrap      = true,
        gapMain   = 5f,
        gapCross  = 5f,
        alignMain = AAutoLayout.AlignMain.CENTER,
    )

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy { GuessController(listItems) }

    // ------------------------------------------------------------------------
    // Callbacks (виставляє екран)
    // ------------------------------------------------------------------------
    var onPicksChanged   : (Int) -> Unit                     = {}
    var onReward         : (Long) -> Unit                    = {}
    var onResult         : (wins: Int, reward: Long) -> Unit = { _, _ -> }
    var onGetFreeEnabled : (Boolean) -> Unit                 = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addTable()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addTable() {
        aTable.setSize(346f, 346f)
        add(aTable) { centerX(); topToTop() }

        aTable.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEach { item ->
            item.setSize(112f, 112f)
            add(item)
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun initialize() {
        controller.onPicksChanged   = { onPicksChanged(it) }
        controller.onReward         = { onReward(it) }
        controller.onResult         = { wins, reward -> onResult(wins, reward) }
        controller.onGetFreeEnabled = { onGetFreeEnabled(it) }
        controller.initialize()
    }

    fun canGetFree(): Boolean = controller.canGetFree()
    fun addFreePicks()        = controller.addFreePicks()

}