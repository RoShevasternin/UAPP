package com.rbxrush.rushrbx.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.model.PlayerModel
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.actor.setFontColor
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter
import com.rbxrush.rushrbx.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+Day")
        .setSize(10)

    private val lsDef = FontFactory.create(screen, parameter, screen.fontGenerator_Fredoka_Medium, GameColor.black_2C2C2C)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = Label("Day 0", lsDef)
    private val aRewardLbl = Label("+25", lsDef)

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
        aItemImg.setSize(32f, 32f)
        add(aItemImg) { center() }

        addDayLbl()
        addRewardLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addDayLbl() {
        aDayLbl.setSize(27f, 11f)
        add(aDayLbl) { centerX(); bottomToTop(margin = 6f) }
        aDayLbl.setAlignment(Align.center)
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(21f, 11f)
        add(aRewardLbl) { centerX(); topToBottom(margin = 6f) }
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
                //aDayLbl.setFontColor(GameColor.black_2C2C2C)
                //aRewardLbl.setFontColor(GameColor.black_2C2C2C)

                aItemImg.setSize(48f, 48f)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
            }

            DailyRewardState.CLAIMED -> {
                aItemImg.setSize(32f, 32f)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
            }

            DailyRewardState.LOCKED -> {
                aItemImg.setSize(32f, 32f)
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