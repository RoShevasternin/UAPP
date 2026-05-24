package com.bossrbx.rbxcalculator.game.actors.layout

import com.bossrbx.rbxcalculator.adsmodule.AdSizeManager
import com.bossrbx.rbxcalculator.game.actors.AScrollPane
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.actors.layout.linear.AVerticalGroup
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.runGDX
import kotlinx.coroutines.launch

// Базовий клас для панелей з ScrollPane + AVerticalGroup
//
// Використання:
//
//   class APanelMain(override val screen: AdvancedScreen): AScrollLayout(screen) {
//
//       override val contentHeight = 596f  // висота контенту
//
//       override fun AVerticalGroup.addContent() {
//           // додавай акторів сюди
//           addActor(myGroup)
//       }
//   }

abstract class AScrollLayout(
    override val screen: AdvancedScreen,
    private val alignH: AlignH = AlignH.CENTER,
    private val gap   : Float  = 0f,
) : AConstraintLayout(screen) {

    // Перевизнач в підкласі — висота твого контенту
    abstract val contentHeight: Float

    protected val verticalGroup = AVerticalGroup(screen, alignH = alignH, gap = gap, wrap = true)
    private   val scrollPane    = AScrollPane(verticalGroup)

    override fun addActorsOnGroup() {
        // ScrollPane заповнює весь контейнер
        add(scrollPane) { fillParent() }

        setupVerticalGroup()
    }

    private fun setupVerticalGroup() {
        verticalGroup.setSize(width, 1f)

        // Якщо контент менший за ScrollPane — центруємо через paddingBottom
        val space = scrollPane.height - contentHeight
        if (space > 0f) verticalGroup.paddingBottom += space

        // Підписка на зміни висоти реклами
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    val adBottom = screen.adBottomUI.coerceAtLeast(0f)
                    if (adBottom > 0f) verticalGroup.paddingBottom += adBottom
                }
            }
        }

        // Додаємо контент — реалізується в підкласі
        verticalGroup.addContent()
    }

    // Перевизнач для додавання акторів у вертикальну групу
    abstract fun AVerticalGroup.addContent()
}