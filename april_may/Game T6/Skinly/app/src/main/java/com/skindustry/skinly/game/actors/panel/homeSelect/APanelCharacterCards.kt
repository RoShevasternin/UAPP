package com.skindustry.skinly.game.actors.panel.homeSelect

import com.badlogic.gdx.graphics.Texture
import com.skindustry.skinly.game.actors.layout.AScrollLayout
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import java.util.concurrent.CopyOnWriteArrayList

class APanelCharacterCards(override val screen: AdvancedScreen) : AScrollLayout(
    screen = screen,
    gap    = 0f,
) {

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onOpen  : ((index: Int) -> Unit)? = null
    var onLocked: ((index: Int) -> Unit)? = null

    // ------------------------------------------------------------------------
    // Cards
    // ------------------------------------------------------------------------
    private val listCards = CopyOnWriteArrayList<ACardCharacter>()

    private val horizontalGroup = AAutoLayout(
        screen     = screen,
        direction  = AAutoLayout.Direction.HORIZONTAL,
        wrap       = true,
        gapMain    = 8f,
        gapCross   = 8f,
        sizingH    = AAutoLayout.Sizing.HUG,
        paddingStart = 16f,
        paddingEnd   = 16f,
    )

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setCards(textures: List<Texture>, unlocked: Set<Int>) {
        // Очищаємо старі
        listCards.forEach { verticalGroup.remove(it) }
        listCards.clear()

        // Створюємо нові
        textures.forEachIndexed { index, texture ->
            val card = ACardCharacter(screen).apply {
                setSize(168f, 168f)
                setCharacter(texture)
                setState(
                    if (index in unlocked) ACardCharacter.State.OPEN
                    else                   ACardCharacter.State.LOCKED
                )
                onOpen   = { this@APanelCharacterCards.onOpen?.invoke(index) }
                onLocked = { this@APanelCharacterCards.onLocked?.invoke(index) }
            }
            listCards.add(card)
            horizontalGroup.add(card)
        }
    }

    fun unlock(index: Int) {
        listCards.getOrNull(index)?.setState(ACardCharacter.State.OPEN)
    }

    override fun AAutoLayout.addContent() {
        horizontalGroup.width = width
        add(horizontalGroup)
    }
}