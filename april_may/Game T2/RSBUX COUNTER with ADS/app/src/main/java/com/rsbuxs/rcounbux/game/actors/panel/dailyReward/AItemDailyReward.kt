package com.rsbuxs.rcounbux.game.actors.panel.dailyReward

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rsbuxs.rcounbux.game.actors.label.ALabel
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.utils.GameColor
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.font.FontParameter
import com.rsbuxs.rcounbux.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter24 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(24)
        .setBorder(5f, GameColor.background)
    private val parameter20 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+RBX")
        .setSize(20)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image(gdxGame.assetsAll.daily_close)
    private val aDayLbl    = ALabel(screen, "01", Color.WHITE, parameter24, screen.fontGenerator_Bold)
    private val aRewardLbl = ALabel(screen, "+10 RBX", GameColor.background, parameter20, screen.fontGenerator_Bold)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateClaim   = gdxGame.assetsAll.daily_claim
    private val stateClaimed = gdxGame.assetsAll.daily_claimed
    private val stateClose   = gdxGame.assetsAll.daily_close

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
        aDayLbl.setSize(40f, 32f)
        add(aDayLbl) {
            startToStart(margin = -4f)
            topToTop()
        }
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(82f, 24f)
        add(aRewardLbl) {
            centerX()
            bottomToBottom(margin = 36f)
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(day: Int) {
        reward = day * 5L
        aDayLbl.setText(day.toString().padStart(2, '0'))
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    fun setState(state: DailyRewardState) {

        when(state) {

            DailyRewardState.CLAIM -> {
                aDayLbl.setLabelColor(GameColor.green_81)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)

                aRewardLbl.setText("+$reward RBX")
                aRewardLbl.setLabelColor(GameColor.background)
            }

            DailyRewardState.CLAIMED -> {
                aDayLbl.setLabelColor(GameColor.gray_40)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
                aRewardLbl.setText("")
            }

            DailyRewardState.LOCKED -> {
                aDayLbl.setLabelColor(Color.WHITE)
                aItemImg.drawable = TextureRegionDrawable(stateClose)
                aRewardLbl.setText("")
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