package com.diam.ondbit.game.screens.home.outfit

import com.badlogic.gdx.math.Vector2
import com.diam.ondbit.adsmodule.AdSizeManager
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTop
import com.diam.ondbit.game.actors.panel.outfit.AFilterTab
import com.diam.ondbit.game.actors.panel.outfit.APanelFilter
import com.diam.ondbit.game.actors.panel.outfit.APanelOutfit
import com.diam.ondbit.game.actors.panel.outfit.OutfitController
import com.diam.ondbit.game.actors.panel.outfit.data.ClothingCategory
import com.diam.ondbit.game.actors.panel.outfit.data.ClothingData
import com.diam.ondbit.game.actors.panel.outfit.data.GearCategory
import com.diam.ondbit.game.actors.panel.outfit.data.GearData
import com.diam.ondbit.game.utils.Block
import com.diam.ondbit.game.utils.TIME_ANIM_SCREEN
import com.diam.ondbit.game.utils.actor.animHide
import com.diam.ondbit.game.utils.actor.animShow
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.runGDX
import kotlinx.coroutines.launch

class ClothingScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val lsTab   by lazy { MsdfStyle(msdf, msdf.fontSpaceGrotesk_Bold, 14f) }
    private val lsTitle by lazy { MsdfStyle(msdf, msdf.fontSpaceGrotesk_Medium, 18f) }

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
            textureSize = Vector2(147f, 147f),
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
        setBackground(gdxGame.assetsLoader.BACKGROUND)

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
        addPanelItems()

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

        aPanelTop.setTitle("CLOTHING")
    }

    private fun AConstraintLayout.addPanelFilter() {
        aPanelFilter.height = 36f
        add(aPanelFilter) { centerX(); topToBottom(aPanelTop, 24f); matchWidth() }

        aPanelFilter.setListFilterTab(filterTabs.values.toList())
    }

    private fun AConstraintLayout.addPanelItems() {
        aPanelOutfit.width = 344f
        add(aPanelOutfit) { centerX(); topToBottom(aPanelFilter, 16f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelOutfit) { marginBottom = screen.adBottomUI + 36f } } } }
    }

}