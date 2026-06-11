package com.skindustry.skinly.game.actors.panel.skinBook

import com.badlogic.gdx.graphics.Texture
import com.skindustry.skinly.game.actors.AScrollPane
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.blokcy.APanelPoints
import com.skindustry.skinly.game.actors.panel.personalization.ACardTexture
import com.skindustry.skinly.game.screens.HomeSelectScreen
import com.skindustry.skinly.game.utils.GLOBAL_selectedHomeType
import com.skindustry.skinly.game.utils.SelectedHomeType
import com.skindustry.skinly.game.utils.actor.setOnTouchListener
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class APanelSkinBookHorizontal(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aHorizontal = AAutoLayout(
        screen       = screen,
        direction    = AAutoLayout.Direction.HORIZONTAL,
        sizingW      = AAutoLayout.Sizing.HUG,
        paddingStart = 16f,
        paddingEnd   = 16f,
        gapMain      = 10f,
    )
    private val aScrollPane = AScrollPane(aHorizontal, scrollX = true, scrollY = false)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addScrollPane()
        setUpHorizontal()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addScrollPane() {
        add(aScrollPane) { fillParent() }
    }

    private fun setUpHorizontal() {
        aHorizontal.setSize(width, aScrollPane.height)
        aHorizontal.minW = width
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setCards(textures: List<Texture>, unlocked: Set<Int> = setOf(0)) {
        // Створюємо нові
        textures.forEachIndexed { index, texture ->
            val card = ACard(screen).apply {
                setSize(144f, 144f)
                setTexture(texture)
                setState(
                    if (index in unlocked) ACard.State.OPEN
                    else                   ACard.State.LOCKED
                )
                //onOpen   = { this@APanelTextureCards.onOpen?.invoke(index) }
                //onLocked = { this@APanelTextureCards.onLocked?.invoke(index) }
            }
            aHorizontal.add(card)
        }
    }

}