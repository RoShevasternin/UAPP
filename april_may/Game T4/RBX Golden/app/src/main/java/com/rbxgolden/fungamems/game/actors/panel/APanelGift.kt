package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.utils.actor.disable
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.businesModule.backend.Bt
import com.rbxgolden.fungamems.businesModule.backend.Events
import com.rbxgolden.fungamems.businesModule.economy.Econ
import com.rbxgolden.fungamems.businesModule.economy.Wallet
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
            // Дефолт 50 = сьогоднішня поведінка; ключ economy.rewards.gift_screen
            val reward = Econ.reward(BLOCK, 50)
            Wallet.add(reward, bt = Bt.GIFT, block = BLOCK)
            Events.featureComplete(bt = Bt.GIFT, block = BLOCK, amount = reward)
            gdxGame.activity.onBackNavigation()
            screen.animHideScreen { gdxGame.navigationManager.back() }
        }
    }

    companion object {
        private const val BLOCK = "gift_screen"
    }

}