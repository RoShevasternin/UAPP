package com.sakurbx.fungambx.game.actors.panel.guess

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.sakurbx.fungambx.game.actors.layout.autoLayout.AAutoLayout
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame

class APanelGuess(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Laila_Bold)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.PANEL_QUESS)
    private val aCardsLbl = Label("3 tries left", lsDef)

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
        add(aBgImg) { fillParent() }
        addTable()
        addCardsLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addTable() {
        aTable.setSize(316f, 312f)
        add(aTable) { centerX(); topToTop(margin = 64f) }
        aTable.addItems()
    }

    private fun AAutoLayout.addItems() {
        listItems.forEach { item ->
            item.setSize(99f, 99f)
            add(item)
        }
    }

    private fun addCardsLbl() {
        aCardsLbl.setSize(67f, 20f)
        add(aCardsLbl) { centerX(); bottomToBottom(margin = 6f) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun initialize() {
        controller.onPicksChanged = { picks ->
            aCardsLbl.setText("$picks tries left")   // ← оновлюємо лейбл тут
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