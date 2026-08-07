package com.diam.ondbit.game.screens.home.map

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.actors.panel.APanelTop
import com.diam.ondbit.game.utils.Block
import com.diam.ondbit.game.utils.GameColor
import com.diam.ondbit.game.utils.TIME_ANIM_SCREEN
import com.diam.ondbit.game.utils.actor.animHide
import com.diam.ondbit.game.utils.actor.animShow
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.global.GLOBAL_LIST_MAP_DESCRIPTIONS
import com.diam.ondbit.game.utils.global.GLOBAL_LIST_MAP_NAMES
import com.diam.ondbit.game.utils.global.GLOBAL_SELECTED_MAP_INDEX
import java.util.Locale

class MapScreen: AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef by lazy { MsdfStyle(msdf, msdf.fontSpaceGrotesk_Medium, 16f) }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private val currentIndex = GLOBAL_SELECTED_MAP_INDEX

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelTop by lazy { APanelTop(this) }
    private val aMaoImg   by lazy { Image(gdxGame.assetsAll.listMap[currentIndex]) }
    private val aDescLbl  by lazy { AMsdfLabel(GLOBAL_LIST_MAP_DESCRIPTIONS[currentIndex], styleDef) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        rootConstraintLayout.color.a = 0f
        setBackground(gdxGame.assetsLoader.BACKGROUND)

        super.show()
        animShowScreen()
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addMapImg()
        addDescLbl()
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

        aPanelTop.setTitle(GLOBAL_LIST_MAP_NAMES[currentIndex].uppercase(Locale.ROOT))
    }

    private fun AConstraintLayout.addMapImg() {
        aMaoImg.setSize(344f, 214f)
        add(aMaoImg) { centerX(); topToBottom(aPanelTop, 24f) }
    }

    private fun AConstraintLayout.addDescLbl() {
        aDescLbl.setSize(344f, 208f)
        add(aDescLbl) { centerX(); topToBottom(aMaoImg, 16f) }
        aDescLbl.wrap = true
        aDescLbl.setAlignment(Align.top, Align.center)
    }

}