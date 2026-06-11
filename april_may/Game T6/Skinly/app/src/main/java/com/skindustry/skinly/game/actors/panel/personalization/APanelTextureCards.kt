package com.skindustry.skinly.game.actors.panel.personalization

import com.badlogic.gdx.graphics.Texture
import com.skindustry.skinly.game.actors.AScrollPane
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxGroup
import com.skindustry.skinly.game.actors.layout.AScrollLayout
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.homeSelect.ACardCharacter
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import java.util.concurrent.CopyOnWriteArrayList

class APanelTextureCards(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onOpen  : ((index: Int) -> Unit)? = null
    var onLocked: ((index: Int) -> Unit)? = null

    // ------------------------------------------------------------------------
    // Cards
    // ------------------------------------------------------------------------

    private val listCards = CopyOnWriteArrayList<ACardTexture>()

    private val horizontalGroup = AAutoLayout(
        screen     = screen,
        direction  = AAutoLayout.Direction.HORIZONTAL,
        wrap       = true,
        gapMain    = 8f,
        gapCross   = 8f,
        sizingH    = AAutoLayout.Sizing.HUG,
        paddingStart  = 16f,
        paddingEnd    = 16f,
        paddingBottom = 16f,
        alignCross    = AAutoLayout.AlignCross.END
    )

    private val scrollPane = AScrollPane(horizontalGroup)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val cbg = ACheckBoxGroup()

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        add(scrollPane) { fillParent() }
        horizontalGroup.width = width
        horizontalGroup.minH  = height
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setCards(textures: List<Texture>, unlocked: Set<Int>) {
        // Очищаємо старі
        listCards.forEach { horizontalGroup.remove(it) }
        listCards.clear()

        // Створюємо нові
        textures.forEachIndexed { index, texture ->
            val card = ACardTexture(screen).apply {
                setSize(80f, 80f)
                setTexture(texture)
                setState(
                    if (index in unlocked) ACardTexture.State.OPEN
                    else                   ACardTexture.State.LOCKED
                )
                onOpen   = { this@APanelTextureCards.onOpen?.invoke(index) }
                onLocked = { this@APanelTextureCards.onLocked?.invoke(index) }
            }
            listCards.add(card)
            horizontalGroup.add(card)

            card.setCheckBoxGroup(cbg)
        }
    }

    fun unlock(index: Int) {
        listCards.getOrNull(index)?.setState(ACardTexture.State.OPEN)
    }
}