package com.rbxgolden.fungamems.game.actors.panel.head_body

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
import com.rbxgolden.fungamems.game.utils.actor.setBounds
import com.rbxgolden.fungamems.game.utils.actor.setSize
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.launch

class APanelHead_BodyAll(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, alignH = AlignH.CENTER, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val listContentImg = List(3) { Image(gdxGame.assetsAll.listHead_BodyPanel[it]) }
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
        val contentH = 3111f

        aVerticalGroup.setSize(width, 1f)
        aContentGroup.setSize(width, contentH)

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

        aContentGroup.addListContentImg()
    }

    private fun AdvancedGroup.addListContentImg() {
        val listBounds = listOf(
            Rectangle(0f, 2074f, 376f, 1037f),
            Rectangle(0f, 1037f, 376f, 1037f),
            Rectangle(0f, 0f, 376f, 1037f),
        )

        listContentImg.forEachIndexed { index, img ->
            addActor(img)
            img.setBounds(listBounds[index])
        }

    }

}