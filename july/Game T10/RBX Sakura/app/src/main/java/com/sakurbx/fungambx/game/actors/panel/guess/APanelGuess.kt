package com.sakurbx.fungambx.game.actors.panel.guess

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBox
import com.sakurbx.fungambx.game.actors.checkbox.base.ACheckBoxStyles
import com.sakurbx.fungambx.game.actors.layout.autoLayout.AAutoLayout
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.actor.disable
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter

class APanelGuess(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Kedebideri_Bold)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aCardsLbl = Label("3 PICKS LEFT", lsDef)
    private val listPick  = List(3) { ACheckBox(screen, ACheckBoxStyles.POINT) }

    private val listItems = List(9) { AItemGuess(screen) }
    private val aTable    = AAutoLayout(
        screen    = screen,
        direction = AAutoLayout.Direction.HORIZONTAL,
        wrap      = true,
        gapMain   = 8f,
        gapCross  = 8f,
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
        addCardsLbl()
        addBox()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addTable() {
        add(aTable) { fillParent() }
        aTable.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEach { item ->
            item.setSize(109f, 109f)
            add(item)
        }
    }

    private fun addCardsLbl() {
        aCardsLbl.setSize(71f, 20f)
        add(aCardsLbl) { centerX(); topToBottom(margin = 26f) }
    }

    private fun addBox() {
        var nx = 153f
        listPick.forEach { item ->
            addActor(item)
            item.setSize(10f, 10f)
            item.setPosition(nx, -26f)

            nx += 4f + 10f
            item.disable()
            item.check()
        }
    }

    private fun updatePickTabs(picks: Int) {
        val total = listPick.size
        listPick.forEachIndexed { index, pick ->
            // checked лишаються ПРАВІ picks штук → гаснуть ліві першими
            if (index >= total - picks) pick.check() else pick.uncheck()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun initialize() {
        controller.onPicksChanged = { picks ->
            aCardsLbl.setText("$picks PICKS LEFT")   // ← оновлюємо лейбл тут
            updatePickTabs(picks)
            onPicksChanged(picks)                     // ← і прокидаємо назовні (якщо екрану треба)
        }
        controller.onReward         = { onReward(it) }
        controller.onResult         = { wins, reward -> onResult(wins, reward) }
        controller.onGetFreeEnabled = { onGetFreeEnabled(it) }
        controller.initialize()
    }

    fun canGetFree(): Boolean = controller.canGetFree()
    fun hasAdsLeft(): Boolean = controller.hasAdsLeft()
    fun addFreePicks()        = controller.addFreePicks()
}