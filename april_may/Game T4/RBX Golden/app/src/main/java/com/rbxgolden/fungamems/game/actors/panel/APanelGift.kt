package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.utils.actor.disable
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame

class APanelGift(
    override val screen: AdvancedScreen
) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.PANEL_GIFT)
    private val aClaimBtn = Actor()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPanelImg()
        addClaimBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPanelImg() {
        add(aPanelImg) { fillParent() }
    }

    private fun addClaimBtn() {
        addActor(aClaimBtn)
        aClaimBtn.setBounds(32f, 78f, 312f, 56f)

        aClaimBtn.setOnClickListener {
            disable()
            gdxGame.modelPlayer.addRbx(50)
            gdxGame.activity.onBackNavigation()
            screen.animHideScreen { gdxGame.navigationManager.back() }
        }
    }

}