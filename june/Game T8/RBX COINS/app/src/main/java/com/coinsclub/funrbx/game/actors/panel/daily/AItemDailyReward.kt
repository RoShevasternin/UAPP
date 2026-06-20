package com.coinsclub.funrbx.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.model.PlayerModel
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.actor.setFontColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow
import com.coinsclub.funrbx.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+Day")
        .setSize(10)
        .setBorderAndShadow()

    private val lsDef = FontFactory.create(screen, parameter, screen.fontGenerator_LuckiestGuy_Regular, GameColor.gray_837B9D)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = Label("Day 0", lsDef)
    private val aRewardLbl = Label("+25", lsDef)

    private val aClaimCircleImg = Image(gdxGame.assetsAll.claimed_circle)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateClaim   = gdxGame.assetsAll.claim
    private val stateClaimed = gdxGame.assetsAll.claimed
    private val stateClose   = gdxGame.assetsAll.close

    private var reward = 0L

    var is7 = false

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        aItemImg.setSize(32f, 32f)
        add(aItemImg) { center() }

        addDayLbl()
        addRewardLbl()
        addClaimCircleImg()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addDayLbl() {
        aDayLbl.setSize(24f, 10f)
        add(aDayLbl) { centerX(); bottomToTop(margin = 3f) }
        aDayLbl.setAlignment(Align.center)
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(21f, 10f)
        add(aRewardLbl) { centerX(); topToBottom(margin = 4f) }
        aRewardLbl.setAlignment(Align.center)
    }

    private fun addClaimCircleImg() {
        aClaimCircleImg.setSize(8f, 9f)
        add(aClaimCircleImg) { startToEnd(margin = 3f); bottomToBottom(margin = 11f) }
        aClaimCircleImg.color.a = 0f
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
                aDayLbl.setFontColor(GameColor.white_FFF5E3)
                aRewardLbl.setFontColor(GameColor.white_FFF5E3)

                aItemImg.setSize(36f, 32f)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)

                aClaimCircleImg.color.a = 0f
            }

            DailyRewardState.CLAIMED -> {
                aDayLbl.setFontColor(GameColor.green_6EF033)
                aRewardLbl.setFontColor(GameColor.green_6EF033)

                aItemImg.setSize(32f, 32f)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)

                aClaimCircleImg.color.a = if (is7) 0f else 1f
            }

            DailyRewardState.LOCKED -> {
                aDayLbl.setFontColor(GameColor.gray_837B9D)
                aRewardLbl.setFontColor(GameColor.gray_837B9D)

                aItemImg.setSize(32f, 32f)
                aItemImg.drawable = TextureRegionDrawable(stateClose)

                aClaimCircleImg.color.a = 0f
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