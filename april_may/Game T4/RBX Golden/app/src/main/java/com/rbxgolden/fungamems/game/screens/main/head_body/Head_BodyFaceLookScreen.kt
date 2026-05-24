package com.rbxgolden.fungamems.game.screens.main.head_body

import com.badlogic.gdx.math.Vector2
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.panel.APanelTop
import com.rbxgolden.fungamems.game.actors.panel.accessories.AHeaderAccessories
import com.rbxgolden.fungamems.game.actors.panel.accessories.APanelAccessoriesAll
import com.rbxgolden.fungamems.game.actors.panel.accessories.APanelAccessoriesFace
import com.rbxgolden.fungamems.game.actors.panel.clothing.AHeaderClothing
import com.rbxgolden.fungamems.game.actors.panel.clothing.APanelClothingAll
import com.rbxgolden.fungamems.game.actors.panel.head_body.AHeaderHead_Body
import com.rbxgolden.fungamems.game.actors.panel.head_body.APanelHead_BodyFaceLook
import com.rbxgolden.fungamems.game.actors.panel.head_body.APanelHead_BodyFaceShapes
import com.rbxgolden.fungamems.game.actors.panel.select.APanelSelect1
import com.rbxgolden.fungamems.game.utils.Block
import com.rbxgolden.fungamems.game.utils.TIME_ANIM_SCREEN
import com.rbxgolden.fungamems.game.utils.actor.animDelay
import com.rbxgolden.fungamems.game.utils.actor.animHide
import com.rbxgolden.fungamems.game.utils.actor.animShow
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame

class Head_BodyFaceLookScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop    by lazy { APanelTop(this) }
    private val aHeader      by lazy { AHeaderHead_Body(this, gdxGame.assetsAll.listHead_BodyHeader[2]) }
    private val aPanelSelect by lazy { APanelHead_BodyFaceLook(this) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, safeBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addHeader()
        addPanelSelect()
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
        aPanelTop.setTitle("All Head & Body")
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addHeader() {
        aHeader.height = 72f
        add(aHeader) {
            centerX(); topToBottom(aPanelTop)
            matchWidth()
        }
    }

    private fun AConstraintLayout.addPanelSelect() {
        aPanelSelect.width = WIDTH
        add(aPanelSelect) {
            centerX(); topToBottom(aHeader); bottomToBottom()
            matchHeight()
        }
    }

}