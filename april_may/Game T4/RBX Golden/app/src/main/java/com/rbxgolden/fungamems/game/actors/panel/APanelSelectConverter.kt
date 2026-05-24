package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.adsmodule.AdSizeManager
import com.rbxgolden.fungamems.game.actors.AScrollPane
import com.rbxgolden.fungamems.game.actors.ATmpGroup
import com.rbxgolden.fungamems.game.actors.layout.AlignH
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.layout.linear.AVerticalGroup
import com.rbxgolden.fungamems.game.screens.main.ConverterScreen
import com.rbxgolden.fungamems.game.screens.main.DailyRewardScreen
import com.rbxgolden.fungamems.game.utils.ConverterType
import com.rbxgolden.fungamems.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.game.utils.actor.setOnTouchListener
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.launch

class APanelSelectConverter(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, alignH = AlignH.CENTER, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val aPanelImg      = Image(gdxGame.assetsAll.PANEL_SELECT_CONVERTER)
    private val listBtn        = List(6) { Actor() }
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
        val contentW = 376f
        val contentH = 408f

        aVerticalGroup.setSize(width, 1f)
        aContentGroup.setSize(contentW, contentH)

        aVerticalGroup.addActor(aContentGroup)

        val space = aScrollPane.height - contentH
        if (space > 0) aVerticalGroup.paddingBottom += space

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    if (screen.adBottomUI >= 0f) aVerticalGroup.paddingBottom += screen.adBottomUI
                    log("APanelMain adBottomUI = ${screen.adBottomUI} | banner = ${screen.safeBannerUI}")
                }
            }
        }

        aContentGroup.also {
            it.addContentImg()
            it.addListBtn()
        }

    }

    private fun AdvancedGroup.addContentImg() {
        addAndFillActor(aPanelImg)
    }

    private fun AdvancedGroup.addListBtn() {
        var nx = 16f
        var ny = 272f
        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 168f, 120f)

            nx += 8f + 168f
            if (index.inc() % 2 == 0) {
                nx = 16f
                ny -= 8f + 120f
            }

            btn.setOnTouchListener {

                when(ConverterType.entries[index]) {
                    ConverterType.DAILY_FREE_RBX -> {
                        screen.animHideScreen {
                            gdxGame.navigationManager.navigate(DailyRewardScreen::class.java.name, screen::class.java.name)
                        }
                    }
                    else -> {
                        screen.animHideScreen {
                            GLOBAL_SELECTED_CONVERTER_TYPE = ConverterType.entries[index]
                            gdxGame.navigationManager.navigate(ConverterScreen::class.java.name, screen::class.java.name)
                        }
                    }
                }

            }

        }
    }

}