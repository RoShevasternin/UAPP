package com.selftest.mindora.game.actors.panel.home.main

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.AMainMediumButton
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

class APanelItemDaily(screen: AdvancedScreen): AConstraintLayout(screen) {

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
    private val aBgImg       = Image(gdxGame.assetsAll.ITEM_DAILY)
    private val aProgressLbl = AMsdfLabel("Start your streak today and watch rewards grow", styleDef)
    private val aClaimBtn    = AMainMediumButton(screen, "Claim Reward")

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onClaim = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addProgressLbl()
        addClaimBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBgImg() {
        add(aBgImg) { fillParent() }
    }

    private fun addProgressLbl() {
        aProgressLbl.setSize(175f, 28f)
        add(aProgressLbl) { startToStart(margin = 17f); topToTop(margin = 43f) }

        aProgressLbl.setAlignment(Align.topLeft)
        aProgressLbl.wrap = true
    }

    private fun addClaimBtn() {
        aClaimBtn.setSize(150f, 34f)
        add(aClaimBtn) { startToStart(margin = 17f); bottomToBottom(margin = 17f) }

        aClaimBtn.setOnClickListener { onClaim() }
    }

}