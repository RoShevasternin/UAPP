package com.sakurbx.fungambx.game.actors.panel.daily

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.model.PlayerModel
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.actor.setFontColor
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.font.setDoubleShadow
import com.sakurbx.fungambx.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDay = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+Day")
        .setSize(10)
        .setDoubleShadow()
    private val parameterDayGray = parameterDay.copy().setDoubleShadow(GameColor.gray_4E4E4E, GameColor.gray_C9C9C9)
    private val parameterReward = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+")
        .setSize(8)

    private val lsDay     = FontFactory.create(screen, parameterDay, screen.fontGenerator_Laila_Bold, GameColor.beige_FFFAD3)
    private val lsDayGray = FontFactory.create(screen, parameterDayGray, screen.fontGenerator_Laila_Bold)
    private val lsReward  = FontFactory.create(screen, parameterReward, screen.fontGenerator_Kedebideri_ExtraBold, GameColor.purple_890047)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = Label("Day 0", lsDay)
    private val aRewardLbl = Label("+25", lsReward)

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
        aDayLbl.setSize(31f, 10f)
        add(aDayLbl) { centerX(); bottomToBottom(margin = 5f) }
        aDayLbl.setAlignment(Align.center)
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(18f, 8f)
        add(aRewardLbl) { centerX(); topToTop(margin = 7f) }
        aRewardLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(day: Int) {
        reward = PlayerModel.LIST_REWARD[day - 1]

        aDayLbl.setText("Day $day")
        aRewardLbl.setText("+$reward")
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    fun setState(state: DailyRewardState) {
        when(state) {

            DailyRewardState.CLAIM -> {
                aDayLbl.style = lsDay
                aDayLbl.setFontColor(GameColor.beige_FFFAD3)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
            }

            DailyRewardState.CLAIMED -> {
                aDayLbl.style = lsDay
                aRewardLbl.setText("")
                aDayLbl.setFontColor(GameColor.beige_FFFAD3)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
            }

            DailyRewardState.LOCKED -> {
                aDayLbl.style = lsDayGray
                aRewardLbl.setText("")
                aDayLbl.setFontColor(Color.WHITE)
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