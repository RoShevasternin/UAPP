package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.adsmodule.AdSizeManager
import com.rbxgolden.fungamems.game.actors.AScrollPane
import com.rbxgolden.fungamems.game.actors.ATmpGroup
import com.rbxgolden.fungamems.game.actors.layout.AlignH
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.layout.linear.AVerticalGroup
import com.rbxgolden.fungamems.game.screens.main.CharacterScreen
import com.rbxgolden.fungamems.game.screens.main.ConverterScreen
import com.rbxgolden.fungamems.game.screens.main.DailyRewardScreen
import com.rbxgolden.fungamems.game.utils.ConverterType
import com.rbxgolden.fungamems.game.utils.GLOBAL_SELECTED_CHARACTER_INDEX
import com.rbxgolden.fungamems.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbxgolden.fungamems.game.utils.actor.setBounds
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.game.utils.actor.setOnTouchListener
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.launch

class APanelSelectCharacters(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, alignH = AlignH.CENTER, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val listContentImg = List(2) { Image(gdxGame.assetsAll.listSC[it]) }
    private val listBtn        = List(24) { Actor() }
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
        val contentH = 1200f

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
            it.addListContentImg()
            it.addListBtn()
        }

    }

    private fun AdvancedGroup.addListContentImg() {
        val listBounds = listOf(
            Rectangle(16f, 604f, 343f, 580f),
            Rectangle(16f, 16f, 343f, 580f),
        )

        listContentImg.forEachIndexed { index, img ->
            addActor(img)
            img.setBounds(listBounds[index])
        }

    }

    private fun AdvancedGroup.addListBtn() {
        var nx = 16f
        var ny = 1045f
        listBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 109f, 139f)

            nx += 8f + 109f
            if (index.inc() % 3 == 0) {
                nx = 16f
                ny -= 8f + 139f
            }

            btn.setOnTouchListener {
                screen.animHideScreen {
                    GLOBAL_SELECTED_CHARACTER_INDEX = index
                    gdxGame.navigationManager.navigate(CharacterScreen::class.java.name, screen::class.java.name)
                }
            }

        }
    }

}