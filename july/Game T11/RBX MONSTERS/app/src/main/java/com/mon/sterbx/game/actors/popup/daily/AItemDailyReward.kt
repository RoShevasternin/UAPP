package com.mon.sterbx.game.actors.popup.daily

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.model.PlayerModel
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.actor.setFontColor
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter

import com.mon.sterbx.game.utils.gdxGame

class AItemDailyReward(
    override val screen: AdvancedScreen,
    private val isBig: Boolean = false,
): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDay = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+Day")
        .setSize(14)
    private val parameterReward = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+")
        .setSize(10)

    private val lsDay     = FontFactory.create(screen, parameterDay, screen.fontGenerator_BeVietnamPro_Bold, GameColor.black_060606)
    private val lsReward  = FontFactory.create(screen, parameterReward, screen.fontGenerator_BeVietnamPro_Regular, Color.BLACK)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = Label("Day 0", lsDay)
    private val aRewardLbl = Label("+25", lsReward)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateClaim   = if (isBig) gdxGame.assetsAll.d7_claim   else gdxGame.assetsAll.claim
    private val stateClaimed = if (isBig) gdxGame.assetsAll.d7_claimed else gdxGame.assetsAll.claimed
    private val stateClose   = if (isBig) gdxGame.assetsAll.d7_close   else gdxGame.assetsAll.close

    private var reward = 0L

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aItemImg) { fillParent() }

        if (isBig.not()) addDayLbl()
        if (isBig.not()) addRewardLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addDayLbl() {
        aDayLbl.setSize(39f, 15f)
        add(aDayLbl) { centerX(); topToTop(margin = 8f) }
        aDayLbl.setAlignment(Align.center)
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(24f, 11f)
        add(aRewardLbl) { centerX(); bottomToBottom(margin = 17f) }
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
                aRewardLbl.setFontColor(Color.BLACK)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
            }

            DailyRewardState.CLAIMED -> {
                aRewardLbl.setFontColor(Color.BLACK)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
            }

            DailyRewardState.LOCKED -> {
                aRewardLbl.setFontColor(GameColor.gray_818181)
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