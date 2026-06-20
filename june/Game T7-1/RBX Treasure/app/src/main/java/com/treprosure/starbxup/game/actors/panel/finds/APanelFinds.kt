package com.treprosure.starbxup.game.actors.panel.finds

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.treprosure.starbxup.game.actors.layout.AScrollLayout
import com.treprosure.starbxup.game.actors.layout.autoLayout.AAutoLayout
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.gdxGame

class APanelFinds(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listItems = List(9) { AItemFinds(screen) }
    private val aTable    = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        wrap      = true,
        gapMain   = 26f,
        gapCross  = 23f,
        alignMain = AAutoLayout.AlignMain.CENTER,
    )

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller by lazy { FindsController(listItems) }

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
        add(Image(gdxGame.assetsAll.PANEL_FINDS)) { fillParent() }
        addTable()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addTable() {
        aTable.setSize(280f, 263f)
        add(aTable) { center() }

        aTable.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEach { item ->
            item.setSize(75f, 72f)
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