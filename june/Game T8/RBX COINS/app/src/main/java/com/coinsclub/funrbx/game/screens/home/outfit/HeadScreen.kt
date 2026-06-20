package com.coinsclub.funrbx.game.screens.home.outfit

import com.badlogic.gdx.math.Vector2
import com.coinsclub.funrbx.adsmodule.AdSizeManager
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.actors.panel.APanelTop
import com.coinsclub.funrbx.game.actors.panel.outfit.AFilterTab
import com.coinsclub.funrbx.game.actors.panel.outfit.APanelFilter
import com.coinsclub.funrbx.game.actors.panel.outfit.APanelOutfit
import com.coinsclub.funrbx.game.actors.panel.outfit.OutfitController
import com.coinsclub.funrbx.game.actors.panel.outfit.data.HeadCategory
import com.coinsclub.funrbx.game.actors.panel.outfit.data.HeadData
import com.coinsclub.funrbx.game.utils.Block
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.TIME_ANIM_SCREEN
import com.coinsclub.funrbx.game.utils.actor.animDelay
import com.coinsclub.funrbx.game.utils.actor.animHide
import com.coinsclub.funrbx.game.utils.actor.animShow
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame
import com.coinsclub.funrbx.game.utils.runGDX
import kotlinx.coroutines.launch

class HeadScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterTab = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)
        .setBorderAndShadow(border = 1f, shadowX = 2, shadowY = 1)
    private val parameterCard = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(18)
        .setBorderAndShadow()

    private val lsTab  by lazy { FontFactory.create(this, parameterTab, fontGenerator_LuckiestGuy_Regular, GameColor.white_FFF5E3) }
    private val lsCard by lazy { FontFactory.create(this, parameterCard, fontGenerator_LuckiestGuy_Regular, GameColor.white_FFF5E3) }

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
        setBackBackground(gdxGame.assetsAll.BACKGROUND_ALL)

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

        aPanelTop.setTitle("HEAD & BODY")
    }

    private fun AConstraintLayout.addPanelFilter() {
        aPanelFilter.height = 26f
        add(aPanelFilter) { centerX(); topToBottom(aPanelTop, 16f); matchWidth() }

        aPanelFilter.setListFilterTab(filterTabs.values.toList())
    }

    private fun AConstraintLayout.addPanelClothing() {
        aPanelOutfit.width = 346f
        add(aPanelOutfit) { centerX(); topToBottom(aPanelFilter, 16f); bottomToBottom(); matchHeight() }

        coroutine?.launch { AdSizeManager.adBottomFlow.collect { runGDX { update(aPanelOutfit) { marginBottom += screen.adBottomUI } } } }
    }

}