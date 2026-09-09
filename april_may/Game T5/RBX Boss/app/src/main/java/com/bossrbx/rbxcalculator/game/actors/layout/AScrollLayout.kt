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

    // Скільки треба добити знизу, щоб короткий контент заповнив ScrollPane.
    // Рахується один раз; підписка на рекламу додає свій відступ ПОВЕРХ нього.
    private var basePaddingBottom = 0f

    override fun addActorsOnGroup() {
        // ScrollPane заповнює весь контейнер
        add(scrollPane) { fillParent() }

        setupVerticalGroup()
    }

    private fun setupVerticalGroup() {
        verticalGroup.setSize(width, 1f)

        // Якщо контент менший за ScrollPane — добиваємо висоту через paddingBottom
        basePaddingBottom = (scrollPane.height - contentHeight).coerceAtLeast(0f)
        verticalGroup.paddingBottom = basePaddingBottom

        // Підписка на зміни висоти реклами: відступ знизу, щоб останній
        // елемент списку можна було доскролити над банером/нативкою.
        //
        // ⚠️ Два "не так", на які легко наступити:
        //
        // 1. Саме "=", а не "+=". adBottomFlow — це StateFlow: він віддає
        //    поточне значення одразу на підписку і далі КОЖНУ зміну (нативка
        //    з'явилась, банер сховався, висота перерахувалась). З "+=" відступ
        //    накопичувався б з кожною емісією.
        //
        // 2. maxOf, а не сума. Обидва доданки закривають ОДНУ й ту саму дірку
        //    знизу: base добиває короткий контент до висоти pane, adBottom
        //    ховає його за рекламою. Складені разом вони дають порожнечу
        //    висотою в base між останнім елементом і банером.
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    val adBottom = screen.adBottomUI.coerceAtLeast(0f)
                    verticalGroup.paddingBottom = maxOf(basePaddingBottom, adBottom)
                }
            }
        }

        // Додаємо контент — реалізується в підкласі
        verticalGroup.addContent()
    }

    // Перевизнач для додавання акторів у вертикальну групу
    abstract fun AVerticalGroup.addContent()
}