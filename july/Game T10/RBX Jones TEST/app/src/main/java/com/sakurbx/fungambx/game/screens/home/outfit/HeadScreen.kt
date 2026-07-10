package com.sakurbx.fungambx.game.screens.home.outfit

import com.badlogic.gdx.math.Vector2
import com.sakurbx.fungambx.adsmodule.AdSizeManager
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.actors.panel.APanelTop
import com.sakurbx.fungambx.game.actors.panel.outfit.AFilterTab
import com.sakurbx.fungambx.game.actors.panel.outfit.APanelFilter
import com.sakurbx.fungambx.game.actors.panel.outfit.APanelOutfit
import com.sakurbx.fungambx.game.actors.panel.outfit.OutfitController
import com.sakurbx.fungambx.game.actors.panel.outfit.data.HeadCategory
import com.sakurbx.fungambx.game.actors.panel.outfit.data.HeadData
import com.sakurbx.fungambx.game.utils.Block
import com.sakurbx.fungambx.game.utils.TIME_ANIM_SCREEN
import com.sakurbx.fungambx.game.utils.actor.animDelay
import com.sakurbx.fungambx.game.utils.actor.animHide
import com.sakurbx.fungambx.game.utils.actor.animShow
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame
import com.sakurbx.fungambx.game.utils.runGDX
import kotlinx.coroutines.launch

class HeadScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterTab = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)
    private val parameterCard = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(12)

    private val lsTab  by lazy { FontFactory.create(this, parameterTab, fontGenerator_Laila_Bold) }
    private val lsCard by lazy { FontFactory.create(this, parameterCard, fontGenerator_Laila_Bold) }

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
        HeadCategory.entries.associateWith { cat -> AFilterTab(this, lsTab).apply {
            setSize(1f, 26f)
            setText(cat.title)
        } }
    }

    private val controller by lazy {
        OutfitController(
            screen      = this,
            filterTabs  = filterTabs,
            panel       = aPanelOutfit,
            labelStyle  = lsCard,
            textureSize = Vector2(109f, 109f),
            items       = HeadData.items(),
            allCategory = HeadCategory.ALL,
        )
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBottomUI))
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
        addPanelFilter()
        addPanelClothing()

        controller.initialize()
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
        aPanelTop.height = 72f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle("All Head & Body")
    }

    private fun AConstraintLayout.addPanelFilter() {
        aPanelFilter.height = 26f
        add(aPanelFilter) { centerX(); topToBottom(aPanelTop, 16f); matchWidth() }

        aPanelFilter.setListFilterTab(filterTabs.values.toList())
    }

    private fun AConstraintLayout.addPanelClothing() {
        aPanelOutfit.width = 344f
        add(aPanelOutfit) { centerX(); topToBottom(aPanelFilter, 16f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelOutfit) { marginBottom += screen.adBottomUI } } } }
    }

}