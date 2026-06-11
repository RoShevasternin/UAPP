package com.skindustry.skinly.game.actors.panel.selector

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.skindustry.skinly.game.actors.button.base.AButtonAnim
import com.skindustry.skinly.game.actors.button.base.AButtonStyles
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.MAX_SELECTOR
import com.skindustry.skinly.game.utils.actor.disable
import com.skindustry.skinly.game.utils.actor.enable
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class APanelSelector(override val screen: AdvancedScreen): AAutoLayout(
    screen     = screen,
    direction  = Direction.HORIZONTAL,
    wrap       = true,
    sizingH    = Sizing.HUG,
    gapMain    = 4f,
    gapCross   = 4f,
    alignMain  = AlignMain.CENTER,
    alignCross = AlignCross.CENTER,
) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    private val labelStyle = FontFactory.create(screen, parameter, screen.fontGenerator_Bold, Color.BLACK)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    // Динамічний список — скільки текстів передано стільки й items
    private val listItem = ArrayList<APanelSelectorItem>()

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val _selectedCountFlow = MutableStateFlow(0)
    val selectedCountFlow: StateFlow<Int> = _selectedCountFlow.asStateFlow()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------

    fun setTexts(texts: List<String>) {
        // Очищаємо старі items
        listItem.forEach { remove(it) }
        listItem.clear()

        // Створюємо нові під кожен текст
        texts.forEach { text ->
            val item = APanelSelectorItem(screen, labelStyle)
            listItem.add(item)

            item.setSize(1f, 56f)
            add(item)
            item.setText(text) // розмір виставиться автоматично

            // Підписка на зміну чекбокса
            coroutine?.launch {
                item.checkFlow.collect { isChecked ->
                    val count = listItem.toList().count { it.isChecked }
                    _selectedCountFlow.value = count

                    // Блокуємо/розблоковуємо некликнуті якщо досягли макс
                    listItem.toList().forEach { i ->
                        if (!i.isChecked) {
                            if (count >= MAX_SELECTOR) i.disable()
                            else i.enable()
                        }
                    }
                }
            }
        }
    }

}