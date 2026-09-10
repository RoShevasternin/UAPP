package com.rbxgolden.fungamems.game.actors.panel.select

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.adsmodule.AdSizeManager
import com.rbxgolden.fungamems.game.actors.AScrollPane
import com.rbxgolden.fungamems.game.actors.ATmpGroup
import com.rbxgolden.fungamems.game.actors.button.AGoldenButton
import com.rbxgolden.fungamems.game.actors.checkbox.base.ACheckBox
import com.rbxgolden.fungamems.game.actors.checkbox.base.ACheckBoxGroup
import com.rbxgolden.fungamems.game.actors.checkbox.base.ACheckBoxStyles
import com.rbxgolden.fungamems.game.actors.layout.AlignH
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.layout.linear.AVerticalGroup
import com.rbxgolden.fungamems.game.screens.select.Select_2_Screen
import com.rbxgolden.fungamems.game.screens.select.Select_3_Screen
import com.rbxgolden.fungamems.game.utils.actor.setBounds
import com.rbxgolden.fungamems.game.utils.actor.setSize
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.launch

class APanelSelect2(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // Рахується один раз; підписка на рекламу бере maxOf з ним, а не додає
    private var basePaddingBottom = 0f


    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, alignH = AlignH.CENTER, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val listContentImg = List(3) { Image(gdxGame.assetsAll.listS2[it]) }
    private val listContentBox = List(42) { ACheckBox(screen, ACheckBoxStyles.YELLOW) }
    private val aScrollPane    = AScrollPane(aVerticalGroup)

    private val aGoldenBtn = AGoldenButton(screen, "Next")

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
        val contentH = 2134f

        aVerticalGroup.setSize(width, 1f)
        aContentGroup.setSize(width, contentH)

        aGoldenBtn.setSize(344f, 56f)
        aGoldenBtn.disable()

        aVerticalGroup.addActor(aContentGroup)
        aVerticalGroup.addActor(aGoldenBtn)

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

        aContentGroup.also {
            it.addListContentImg()
            it.addListContentBox()
        }

        aGoldenBtn.setOnClickListener {
            screen.animHideScreen {
                gdxGame.navigationManager.navigate(Select_3_Screen::class.java.name, screen::class.java.name)
            }
        }

    }

    private fun AdvancedGroup.addListContentImg() {
        val listBounds = listOf(
            Rectangle(16f, 1224f, 343f, 894f),
            Rectangle(16f, 318f, 343f, 894f),
            Rectangle(16f, 16f, 343f, 290f),
        )

        listContentImg.forEachIndexed { index, img ->
            addActor(img)
            img.setBounds(listBounds[index])
        }

    }

    private fun AdvancedGroup.addListContentBox() {
        var nx = 16f
        var ny = 2009f
        val cbg = ACheckBoxGroup()
        listContentBox.forEachIndexed { index, box ->
            addActor(box)
            box.setBounds(nx, ny, 109f, 109f)
            box.checkBoxGroup = cbg

            nx += 8f + 109f
            if (index.inc() % 3 == 0) {
                nx = 16f
                ny -= 42f + 109f
            }

            box.setOnCheckListener { if (it) aGoldenBtn.enable() }
        }

    }

}