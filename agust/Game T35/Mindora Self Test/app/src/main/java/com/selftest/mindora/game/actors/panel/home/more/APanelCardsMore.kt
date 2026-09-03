package com.selftest.mindora.game.actors.panel.home.more

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.layout.autoLayout.AAutoLayout
import com.selftest.mindora.game.actors.test.card.ACardTest
import com.selftest.mindora.game.content.TestCatalog
import com.selftest.mindora.game.content.TestRepository
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.runGDX
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class APanelCardsMore(screen: AdvancedScreen) : AAutoLayout(
    screen     = screen,
    direction  = Direction.HORIZONTAL,
    gapMain    = 8f,
    sizingH    = Sizing.HUG,
    alignCross = AlignCross.CENTER,
) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aMemoryImg = Image(gdxGame.assetsAll.more_card_memory)
    private val aWatchImg  = Image(gdxGame.assetsAll.more_card_watch)

    private val cards = listOf(aMemoryImg, aWatchImg)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onMemory: () -> Unit = { }
    var onWatch : () -> Unit = { }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addCards()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addCards() {
        cards.forEachIndexed { _, card ->
            card.setSize(168f, 241f)
            add(card)
        }

        aMemoryImg.setOnClickListener(stopEvent = false) { onMemory() }
        aWatchImg.setOnClickListener(stopEvent = false) { onWatch() }
    }

}