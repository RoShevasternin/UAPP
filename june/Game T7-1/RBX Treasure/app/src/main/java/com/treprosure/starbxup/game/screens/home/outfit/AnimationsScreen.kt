package com.treprosure.starbxup.game.screens.home.outfit

import com.badlogic.gdx.math.Vector2
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.actors.panel.APanelTop
import com.treprosure.starbxup.game.actors.panel.outfit.AFilterTab
import com.treprosure.starbxup.game.actors.panel.outfit.APanelFilter
import com.treprosure.starbxup.game.actors.panel.outfit.APanelOutfit
import com.treprosure.starbxup.game.actors.panel.outfit.OutfitController
import com.treprosure.starbxup.game.actors.panel.outfit.data.AccessoriesCategory
import com.treprosure.starbxup.game.actors.panel.outfit.data.AccessoriesData
import com.treprosure.starbxup.game.actors.panel.outfit.data.AnimationsCategory
import com.treprosure.starbxup.game.actors.panel.outfit.data.AnimationsData
import com.treprosure.starbxup.game.actors.panel.outfit.data.ClothingCategory
import com.treprosure.starbxup.game.actors.panel.outfit.data.ClothingData
import com.treprosure.starbxup.game.utils.Block
import com.treprosure.starbxup.game.utils.GameColor
import com.treprosure.starbxup.game.utils.TIME_ANIM_SCREEN
import com.treprosure.starbxup.game.utils.actor.animDelay
import com.treprosure.starbxup.game.utils.actor.animHide
import com.treprosure.starbxup.game.utils.actor.animShow
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.font.FontFactory
import com.treprosure.starbxup.game.utils.font.FontParameter
import com.treprosure.starbxup.game.utils.gdxGame

class AnimationsScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterTab = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)
    private val parameterCard = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(20)

    private val labelStyleTab  by lazy { FontFactory.create(this, parameterTab, fontGenerator_Anton_Regular, GameColor.beige_E2CEAA) }
    private val labelStyleCard by lazy { FontFactory.create(this, parameterCard, fontGenerator_Anton_Regular, GameColor.beige_E2CEAA) }

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
        AnimationsCategory.entries.associateWith { cat -> AFilterTab(this, labelStyleTab).apply {
            setSize(1f, 33f)
            setText(cat.title)
        } }
    }

    private val controller by lazy {
        OutfitController(
            screen      = this,
            filterTabs  = filterTabs,
            panel       = aPanelOutfit,
            labelStyle  = labelStyleCard,
            textureSize = Vector2(109f, 109f),
            items       = AnimationsData.items(),
            allCategory = AnimationsCategory.ALL,
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
        aPanelTop.height = 56f
        add(aPanelTop) { centerX(); topToTop(); matchWidth() }

        aPanelTop.setTitle("ALL ANIMATIONS")
    }

    private fun AConstraintLayout.addPanelFilter() {
        aPanelFilter.setSize(344f, 33f)
        add(aPanelFilter) { centerX(); topToBottom(aPanelTop, 16f) }

        aPanelFilter.setListFilterTab(filterTabs.values.toList())
    }

    private fun AConstraintLayout.addPanelClothing() {
        aPanelOutfit.width = 344f
        add(aPanelOutfit) { centerX(); topToBottom(aPanelFilter, 16f); bottomToBottom(); matchHeight() }
    }

}