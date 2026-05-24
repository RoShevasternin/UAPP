package com.bossrbx.rbxcalculator.game.actors.panel.dailyReward

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.bossrbx.rbxcalculator.game.actors.layout.constraintLayout.AConstraintLayout
import com.bossrbx.rbxcalculator.game.utils.GameColor
import com.bossrbx.rbxcalculator.game.utils.actor.setFontColor
import com.bossrbx.rbxcalculator.game.utils.advanced.AdvancedScreen
import com.bossrbx.rbxcalculator.game.utils.font.FontFactory
import com.bossrbx.rbxcalculator.game.utils.font.FontParameter
import com.bossrbx.rbxcalculator.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter12 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "Day")
        .setSize(12)
    private val parameter24 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+")
        .setSize(24)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = Label("Day 0", FontFactory.create(screen, parameter12, screen.fontGenerator_Medium, GameColor.gray_333333))
    private val aRewardLbl = Label("+25", FontFactory.create(screen, parameter24, screen.fontGenerator_FIRENIGHT, GameColor.gray_333333))

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateClaim   = gdxGame.assetsAll.CLAIM
    private val stateClaimed = gdxGame.assetsAll.CLAIMED
    private val stateClose   = gdxGame.assetsAll.CLOSE

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
        aDayLbl.setSize(30f, 20f)
        add(aDayLbl) { centerX(); topToTop(margin = 8f) }
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(32f, 32f)
        add(aRewardLbl) { centerX(); bottomToBottom(margin = 8f) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(day: Int) {
        reward = gdxGame.modelPlayer.listReward[day - 1]

        aDayLbl.setText("Day $day")
        aRewardLbl.setText("+$reward")
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    fun setState(state: DailyRewardState) {
        when(state) {

            DailyRewardState.CLAIM -> {
                aDayLbl.setFontColor(Color.WHITE)
                aRewardLbl.setFontColor(Color.WHITE)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
            }

            DailyRewardState.CLAIMED -> {
                aDayLbl.setText("")
                aRewardLbl.setFontColor(GameColor.green_55BF40)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
            }

            DailyRewardState.LOCKED -> {
                aDayLbl.setFontColor(GameColor.gray_333333)
                aRewardLbl.setFontColor(GameColor.gray_333333)
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