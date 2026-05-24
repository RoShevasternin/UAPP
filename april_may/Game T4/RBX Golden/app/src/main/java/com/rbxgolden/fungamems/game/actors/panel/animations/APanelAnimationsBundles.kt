package com.rbxgolden.fungamems.game.actors.panel.animations

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

class APanelAnimationsBundles(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, alignH = AlignH.CENTER, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val listContentImg = List(1) { Image(gdxGame.assetsAll.listAnimationsPanel[1]) }
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

        aContentGroup.addAndFillActor(listContentImg.first())
    }

}