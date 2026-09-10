package com.rbxgolden.fungamems.game.actors.panel

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
import com.rbxgolden.fungamems.game.screens.MainScreen
import com.rbxgolden.fungamems.game.screens.select.Select_2_Screen
import com.rbxgolden.fungamems.game.utils.actor.setBounds
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.launch

class APanelSelectAnimationPack(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // Рахується один раз; підписка на рекламу бере maxOf з ним, а не додає
    private var basePaddingBottom = 0f


    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, gap = 12f, alignH = AlignH.CENTER, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val aContentImg    = Image(gdxGame.assetsAll.SELECT_ANIMATION_PACK)
    private val listContentBox = List(8) { ACheckBox(screen, ACheckBoxStyles.YELLOW_LONG) }
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
        val contentW = 344f
        val contentH = 532f

        aVerticalGroup.setSize(width, 1f)
        aContentGroup.setSize(contentW, contentH)

        aGoldenBtn.setSize(344f, 56f)
        aGoldenBtn.disable()

        aVerticalGroup.addActor(aContentGroup)
        aVerticalGroup.addActor(aGoldenBtn)

        aVerticalGroup.paddingTop = 16f

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
            it.addContentImg()
            it.addListContentBox()
        }

        aGoldenBtn.setOnClickListener {
            screen.animHideScreen {
                gdxGame.navigationManager.navigate(MainScreen::class.java.name, screen::class.java.name)
            }
        }

    }

    private fun AdvancedGroup.addContentImg() {
        addAndFillActor(aContentImg)
    }

    private fun AdvancedGroup.addListContentBox() {
        val nx = 0f
        var ny = 476f
        val cbg = ACheckBoxGroup()
        listContentBox.forEachIndexed { _, box ->
            addActor(box)
            box.setBounds(nx, ny, 344f, 56f)
            box.checkBoxGroup = cbg

            ny -= 12f + 56f

            box.setOnCheckListener { if (it) aGoldenBtn.enable() }
        }

    }

}