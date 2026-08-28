package com.selftest.mindora.game.actors.panel.daily

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.model.PlayerModel
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDey    = MsdfStyle(msdf, msdf.fontMontserrat_Bold, 16f, GameColor.white_80)
    private val styleReward = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 14f, GameColor.white_80)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = AMsdfLabel("Day 0", styleDey)
    private val aRewardLbl = AMsdfLabel("+25", styleReward)

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
        aDayLbl.setSize(43f, 20f)
        add(aDayLbl) { centerX(); topToTop(margin = 15f) }
        aDayLbl.setAlignment(Align.center)
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(32f, 17f)
        add(aRewardLbl) { centerX(); bottomToBottom(margin = 7f) }
        aRewardLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(day: Int) {
        reward = gdxGame.activity.appConfig.economy.dailyReward[day - 1]

        aDayLbl.setText("Day $day")
        aRewardLbl.setText("+ $reward")
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    fun setState(state: DailyRewardState) {
        when(state) {

            DailyRewardState.CLAIMED -> {
                aDayLbl.setTextColor(GameColor.white_80)
                aRewardLbl.setTextColor(GameColor.white_80)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
            }

            DailyRewardState.CLAIM -> {
                aDayLbl.setTextColor(Color.WHITE)
                aRewardLbl.setTextColor(Color.WHITE)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
            }

            DailyRewardState.LOCKED -> {
                aDayLbl.setTextColor(GameColor.gray_7C7C7C)
                aRewardLbl.setTextColor(GameColor.gray_7C7C7C)
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