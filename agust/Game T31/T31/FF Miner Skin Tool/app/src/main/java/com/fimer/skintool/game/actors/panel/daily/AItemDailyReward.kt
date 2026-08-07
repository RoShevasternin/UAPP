package com.fimer.skintool.game.actors.panel.daily

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.fimer.skintool.game.actors.label.AMsdfLabel
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.model.PlayerModel
import com.fimer.skintool.game.utils.GameColor
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.font.msdf.MsdfStyle
import com.fimer.skintool.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontNunitoSans_Bold, 8f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = AMsdfLabel("Day 0", styleDef)
    private val aRewardLbl = AMsdfLabel("+25", styleDef, 6f)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateClaim   = gdxGame.assetsAll.claim
    private val stateClaimed = gdxGame.assetsAll.claimed
    private val stateClose   = gdxGame.assetsAll.close

    private var reward = 0L

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aItemImg) { fillParent() }

        addDayLbl()
        addRewardLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addDayLbl() {
        aDayLbl.setSize(24f, 9f)
        add(aDayLbl) { centerX(); topToTop(margin = 4f) }
        aDayLbl.setAlignment(Align.center)
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(17f, 7f)
        add(aRewardLbl) { centerX(); bottomToBottom(margin = 4f) }
        aRewardLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(day: Int) {
        reward = PlayerModel.LIST_REWARD[day - 1]

        aDayLbl.setText("Day $day")
        aRewardLbl.setText("+ $reward")
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    fun setState(state: DailyRewardState) {
        when(state) {

            DailyRewardState.CLAIMED -> {
                aDayLbl.setTextColor(Color.WHITE)
                aRewardLbl.setTextColor(Color.WHITE)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
            }

            DailyRewardState.CLAIM -> {
                aDayLbl.setTextColor(GameColor.brown_352100)
                aRewardLbl.setTextColor(GameColor.brown_352100)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
            }

            DailyRewardState.LOCKED -> {
                aDayLbl.setTextColor(Color.WHITE)
                aRewardLbl.setTextColor(Color.WHITE)
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