package com.racing.funtols.game.screens.home.outfit

import com.badlogic.gdx.math.Vector2
import com.racing.funtols.game.actors.panel.outfit.data.ClothingCategory
import com.racing.funtols.game.actors.panel.outfit.data.ClothingData
import com.racing.funtols.adsmodule.AdSizeManager
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.actors.panel.APanelTop
import com.racing.funtols.game.actors.panel.outfit.AFilterTab
import com.racing.funtols.game.actors.panel.outfit.APanelFilter
import com.racing.funtols.game.actors.panel.outfit.APanelOutfit
import com.racing.funtols.game.actors.panel.outfit.OutfitController
import com.racing.funtols.game.utils.Block
import com.racing.funtols.game.utils.TIME_ANIM_SCREEN
import com.racing.funtols.game.utils.actor.animHide
import com.racing.funtols.game.utils.actor.animShow
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame
import com.racing.funtols.game.utils.runGDX
import kotlinx.coroutines.launch

class ClothingScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val lsTab   by lazy { MsdfStyle(msdf, msdf.fontBarlow_Regular, 14f) }
    private val lsTitle by lazy { MsdfStyle(msdf, msdf.fontBarlow_Bold, 14f) }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop      by lazy { APanelTop(this) }
    private val aPanelFilter   by lazy { APanelFilter(this) }
    private val aPanelOutfit   by lazy { APanelOutfit(this) }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val filterTabs by lazy {
        ClothingCategory.entries.associateWith { cat -> AFilterTab(this, lsTab).apply {
            setSize(1f, 36f)
            setText(cat.title)
        } }
    }

    private val controller by lazy {
        OutfitController(
            screen      = this,
            filterTabs  = filterTabs,
            panel       = aPanelOutfit,
            labelStyleT = lsTitle,
            textureSize = Vector2(80f, 85f),
            items       = ClothingData.items(),
            allCategory = ClothingCategory.ALL,
        )
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBottomUI))
        gdxGame.activity.showNativeAt(coords.y)

        rootConstraintLayout.color.a = 0f

        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addPanelFilter()
        addPanelClothing()

        controller.initialize()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        rootConstraintLayout.animHide(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        rootConstraintLayout.animShow(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(344f, 32f)
        add(aPanelTop) { centerX(); topToTop(margin = 16f) }

        aPanelTop.setTitle("ALL CLOTHING")
    }

    private fun AConstraintLayout.addPanelFilter() {
        aPanelFilter.height = 36f
        add(aPanelFilter) { centerX(); topToBottom(aPanelTop, 24f); matchWidth() }

        aPanelFilter.setListFilterTab(filterTabs.values.toList())
    }

    private fun AConstraintLayout.addPanelClothing() {
        aPanelOutfit.width = 344f
        add(aPanelOutfit) { centerX(); topToBottom(aPanelFilter, 16f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelOutfit) { marginBottom = screen.adBottomUI + 36f } } } }
    }

}