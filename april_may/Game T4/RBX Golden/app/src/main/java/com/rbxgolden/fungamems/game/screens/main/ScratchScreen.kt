package com.rbxgolden.fungamems.game.screens.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.panel.APanelRBX
import com.rbxgolden.fungamems.game.actors.panel.APanelTop
import com.rbxgolden.fungamems.game.actors.panel.scratch.ADialog
import com.rbxgolden.fungamems.game.actors.panel.scratch.AScratch
import com.rbxgolden.fungamems.game.utils.Block
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.NumberFormatter
import com.rbxgolden.fungamems.game.utils.TIME_ANIM_SCREEN
import com.rbxgolden.fungamems.game.utils.actor.animDelay
import com.rbxgolden.fungamems.game.utils.actor.animHide
import com.rbxgolden.fungamems.game.utils.actor.animHideAndDisable
import com.rbxgolden.fungamems.game.utils.actor.animShow
import com.rbxgolden.fungamems.game.utils.actor.animShowAndEnable
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.launch

class ScratchScreen: AdvancedScreen() {

    private val text = "Scratch off the top layer of the card with your finger to reveal your prize!"

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(text)
        .setSize(14)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop  by lazy { APanelTop(this) }
    private val aScratch   by lazy { AScratch(this) }
    private val aTextLbl   by lazy { Label(text, FontFactory.create(this, parameter, fontGenerator_Medium, Color.WHITE)) }
    private val aPanelRBX  by lazy { APanelRBX(this) }

    private val aDimImg by lazy { Image(drawerUtil.getTexture(GameColor.background_80)) }
    private val aDialog by lazy { ADialog(this) }


    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        //val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, safeBannerUI))
        //gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

//    override fun hide() {
//        super.hide()
//        gdxGame.activity.hideNative()
//    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addScratch()
        addTextLbl()
        addPanelRBX()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(WIDTH, 56f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle("Scratch And Win")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addScratch() {
        aScratch.setSize(344f, 272f)
        add(aScratch) { centerX(); topToBottom(aPanelTop, 24f) }

        aScratch.onResult = { result ->
            log("aScratch result: $result")
            gdxGame.modelPlayer.addRbx(result.sum.toLong())

            showDialog(result.sum.toLong())
        }
    }

    private fun AConstraintLayout.addTextLbl() {
        aTextLbl.setSize(344f, 44f)
        add(aTextLbl) { centerX(); topToBottom(aScratch, 24f) }

        aTextLbl.setAlignment(Align.center)
        aTextLbl.wrap = true
    }

    private fun AConstraintLayout.addPanelRBX() {
        aPanelRBX.setSize(89f, 40f)
        add(aPanelRBX) { centerX(); topToBottom(aPanelRBX, 24f) }

        coroutine?.launch {
            gdxGame.modelPlayer.rbxFlow.collect { rbx ->
                runGDX {
                    val rbxFormat = NumberFormatter.format(rbx)
                    aPanelRBX.setText(rbxFormat)
                }
            }
        }
    }

    private fun AConstraintLayout.showDialog(reward: Long) {
        aDialog.onClaim = {
            screen.animHideScreen { gdxGame.navigationManager.back() }
            //aDimImg.animHideAndDisable(0.15f) { aDimImg.remove() }
            //aDialog.animHideAndDisable(0.15f) { aDialog.isDisposeOnRemove = false; aDialog.remove() }
        }

        aDimImg.animHideAndDisable()
        aDialog.animHideAndDisable()

        add(aDimImg) { fillParent() }

        aDialog.setSize(316f, 282f)
        add(aDialog) {
            centerX()
            centerY()

            verticalBias = 0.70f
        }

        aDialog.setReward(reward)

        aDimImg.animShowAndEnable(TIME_ANIM_SCREEN)
        aDialog.animShowAndEnable(TIME_ANIM_SCREEN)
    }

}