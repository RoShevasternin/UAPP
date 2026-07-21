package com.mon.sterbx.game.screens.home.outfit

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.mon.sterbx.adsmodule.AdSizeManager
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.actors.panel.APanelTop
import com.mon.sterbx.game.actors.panel.outfit.AFilterTab
import com.mon.sterbx.game.actors.panel.outfit.APanelFilter
import com.mon.sterbx.game.actors.panel.outfit.APanelOutfit
import com.mon.sterbx.game.actors.panel.outfit.OutfitController
import com.mon.sterbx.game.actors.panel.outfit.data.ClothingCategory
import com.mon.sterbx.game.actors.panel.outfit.data.ClothingData
import com.mon.sterbx.game.utils.Block
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.TIME_ANIM_SCREEN
import com.mon.sterbx.game.utils.actor.animDelay
import com.mon.sterbx.game.utils.actor.animHide
import com.mon.sterbx.game.utils.actor.animShow
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter

import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.runGDX
import kotlinx.coroutines.launch

class ClothingScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterTab = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val parameterCardTitle = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)
    private val parameterCardDesc = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    private val lsTab  by lazy { FontFactory.create(this, parameterTab, fontGenerator_BeVietnamPro_Regular, Color.BLACK) }

    private val lsTitle by lazy { FontFactory.create(this, parameterCardTitle, fontGenerator_BeVietnamPro_Bold, GameColor.black_060606) }
    private val lsDesc  by lazy { FontFactory.create(this, parameterCardDesc, fontGenerator_BeVietnamPro_Regular, GameColor.black_373737) }

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
            setSize(1f, 38f)
            setText(cat.title)
        } }
    }

    private val controller by lazy {
        OutfitController(
            screen      = this,
            filterTabs  = filterTabs,
            panel       = aPanelOutfit,
            labelStyleT = lsTitle,
            labelStyleD = lsDesc,
            textureSize = Vector2(95f, 95f),
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
        setBackground(gdxGame.assetsAll.BACKGROUND_YELLOW)

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
        aPanelTop.setSize(WIDTH, 42f)
        add(aPanelTop) { centerX(); topToTop(margin = 12f) }

        aPanelTop.setTitle("CLOTHES")
    }

    private fun AConstraintLayout.addPanelFilter() {
        aPanelFilter.height = 38f
        add(aPanelFilter) { centerX(); topToBottom(aPanelTop, 16f); matchWidth() }

        aPanelFilter.setListFilterTab(filterTabs.values.toList())
    }

    private fun AConstraintLayout.addPanelClothing() {
        aPanelOutfit.width = 344f
        add(aPanelOutfit) { centerX(); topToBottom(aPanelFilter, 16f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelOutfit) { marginBottom = screen.adBottomUI + 36f } } } }
    }

}