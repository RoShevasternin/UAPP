package com.rbxtreasure.fungamers.game.actors.layout

import com.rbxtreasure.fungamers.adsmodule.AdSizeManager
import com.rbxtreasure.fungamers.game.actors.AScrollPane
import com.rbxtreasure.fungamers.game.actors.layout.autoLayout.AAutoLayout
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.runGDX
import kotlinx.coroutines.launch

abstract class AScrollLayout(
    screen: AdvancedScreen,
    gap          : Float = 0f,
    paddingBottom: Float = 0f
) : AConstraintLayout(screen) {

    protected val verticalGroup = AAutoLayout(screen,
        direction     = AAutoLayout.Direction.VERTICAL,
        gapMain       = gap,
        sizingH       = AAutoLayout.Sizing.HUG,
        alignCross    = AAutoLayout.AlignCross.CENTER,
        paddingBottom = paddingBottom,
    )
    private val scrollPane = AScrollPane(verticalGroup)

    override fun addActorsOnGroup() {
        add(scrollPane) { fillParent() }
        setupVerticalGroup()
    }

    private fun setupVerticalGroup() {
        verticalGroup.setSize(width, height)
        verticalGroup.minH = height

        // Підписка на зміни висоти реклами.
        // ⚠️ «=» і maxOf, не «+=»: adBottomFlow — StateFlow (шле на КОЖНУ зміну,
        //    з «+=» відступ накопичувався), а базовий відступ і реклама закривають
        //    одну й ту саму дірку знизу. База — те, що задав addContent().
        var basePaddingBottom: Float? = null
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    val base = basePaddingBottom ?: verticalGroup.paddingBottom.also { basePaddingBottom = it }
                    verticalGroup.paddingBottom = maxOf(base, screen.adBottomUI.coerceAtLeast(0f))
                }
            }
        }

        // Додаємо контент — реалізується в підкласі
        verticalGroup.addContent()
    }

    // Перевизнач для додавання акторів у вертикальну групу
    abstract fun AAutoLayout.addContent()
}