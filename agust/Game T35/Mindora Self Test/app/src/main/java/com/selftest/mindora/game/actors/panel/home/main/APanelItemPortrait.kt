package com.selftest.mindora.game.actors.panel.home.main

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.progress.AProgressItemPortrait
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

class APanelItemPortrait(screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef by lazy {
        MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, GameColor.white_80)
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg       = Image(gdxGame.assetsAll.ITEM_PORTRAIT)
    private val aProgressLbl = AMsdfLabel("0 of 5 dimensions unlocked", styleDef)
    private val aProgress    = AProgressItemPortrait(screen)
    private val aLockImg     = Image(gdxGame.assetsAll.lock)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addProgressLbl()
        addProgress()
        addLockImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBgImg() {
        aBgImg.setSize(362f, 121f)
        add(aBgImg) { center() }
    }

    private fun addProgressLbl() {
        aProgressLbl.setSize(165f, 14f)
        add(aProgressLbl) { startToStart(margin = 112f); topToTop(margin = 51f) }
    }

    private fun addProgress() {
        aProgress.setSize(175f, 5f)
        add(aProgress) { startToStart(margin = 112f); bottomToBottom(margin = 25f) }
    }

    private fun addLockImg() {
        aLockImg.setSize(23f, 29f)
        add(aLockImg) { startToStart(margin = 42f); bottomToBottom(margin = 40f) }
    }

}