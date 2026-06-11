package com.skindustry.skinly.game.actors.layout

import com.skindustry.skinly.adsmodule.AdSizeManager
import com.skindustry.skinly.game.actors.AScrollPane
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.runGDX
import kotlinx.coroutines.launch

abstract class AScrollLayout(
    screen: AdvancedScreen,
    gap: Float  = 0f,
) : AConstraintLayout(screen) {

    protected val verticalGroup = AAutoLayout(screen,
        direction  = AAutoLayout.Direction.VERTICAL,
        gapMain    = gap,
        sizingH    = AAutoLayout.Sizing.HUG,
        alignCross = AAutoLayout.AlignCross.CENTER
    )
    private val scrollPane = AScrollPane(verticalGroup)

    override fun addActorsOnGroup() {
        add(scrollPane) { fillParent() }
        setupVerticalGroup()
    }

    private fun setupVerticalGroup() {
        verticalGroup.setSize(width, height)
        verticalGroup.minH = height

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
    abstract fun AAutoLayout.addContent()
}