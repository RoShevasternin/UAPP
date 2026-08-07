package com.diam.ondbit.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.diam.ondbit.game.actors.layout.constraintLayout.AConstraintLayout
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.gdxGame

class AItemDailyReward(
    override val screen: AdvancedScreen,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg = Image()

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateClaim   = gdxGame.assetsAll.claim
    private val stateClaimed = gdxGame.assetsAll.claimed
    private val stateClose   = gdxGame.assetsAll.close

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aItemImg) { fillParent() }
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    fun setState(state: DailyRewardState) {
        when(state) {

            DailyRewardState.CLAIM -> {
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
            }

            DailyRewardState.CLAIMED -> {
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
            }

            DailyRewardState.LOCKED -> {
                aItemImg.drawable = TextureRegionDrawable(stateClose)
            }

        }
    }

    // ------------------------------------------------------------------------
    // enum State
    // ------------------------------------------------------------------------
    enum class DailyRewardState {
        CLAIM,
        CLAIMED,
        LOCKED
    }

}