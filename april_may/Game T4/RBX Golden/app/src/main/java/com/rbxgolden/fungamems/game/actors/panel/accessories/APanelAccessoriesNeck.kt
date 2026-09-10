package com.rbxgolden.fungamems.game.actors.panel.accessories

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.adsmodule.AdSizeManager
import com.rbxgolden.fungamems.game.actors.AScrollPane
import com.rbxgolden.fungamems.game.actors.ATmpGroup
import com.rbxgolden.fungamems.game.actors.layout.AlignH
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.layout.linear.AVerticalGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.launch

class APanelAccessoriesNeck(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // Рахується один раз; підписка на рекламу бере maxOf з ним, а не додає
    private var basePaddingBottom = 0f


    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, alignH = AlignH.CENTER, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val listContentImg = List(1) { Image(gdxGame.assetsAll.listAccessoriesPanel[2]) }
    private val aScrollPane    = AScrollPane(aVerticalGroup)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addScrollPane()
        setUpContentGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addScrollPane() {
        add(aScrollPane) { fillParent() }
    }

    // Content Group ------------------------------------------------------------------------
    private fun setUpContentGroup() {
        val contentH = 1037f

        aVerticalGroup.setSize(width, 1f)
        aContentGroup.setSize(width, contentH)

        aVerticalGroup.addActor(aContentGroup)

        // Скільки треба добити знизу, щоб короткий контент заповнив ScrollPane
        basePaddingBottom = (aScrollPane.height - contentH).coerceAtLeast(0f)
        aVerticalGroup.paddingBottom = basePaddingBottom

        // ⚠️ Два «не так», на які легко наступити:
        // 1. Саме «=», а не «+=»: adBottomFlow це StateFlow — віддає поточне
        //    значення одразу і далі КОЖНУ зміну; з «+=» відступ накопичувався б
        //    (порожнеча між останнім елементом списку і банером росла).
        // 2. maxOf, а не сума: base добиває короткий контент до висоти pane,
        //    adBottom ховає його за рекламою — це ОДНА й та сама дірка знизу.
        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    aVerticalGroup.paddingBottom = maxOf(basePaddingBottom, screen.adBottomUI.coerceAtLeast(0f))
                    log("APanelMain adBottomUI = ${screen.adBottomUI} | banner = ${screen.safeBannerUI}")
                }
            }
        }

        aContentGroup.addAndFillActor(listContentImg.first())
    }

}