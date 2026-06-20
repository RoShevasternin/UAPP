package com.treprosure.starbxup.game.actors.panel.daily

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.treprosure.starbxup.game.actors.layout.constraintLayout.AConstraintLayout
import com.treprosure.starbxup.game.model.PlayerModel
import com.treprosure.starbxup.game.utils.GameColor
import com.treprosure.starbxup.game.utils.actor.setFontColor
import com.treprosure.starbxup.game.utils.advanced.AdvancedScreen
import com.treprosure.starbxup.game.utils.font.FontFactory
import com.treprosure.starbxup.game.utils.font.FontParameter
import com.treprosure.starbxup.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter20 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "Day")
        .setSize(20)
    private val parameter16 = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = Label("Day 0", FontFactory.create(screen, parameter20, screen.fontGenerator_Anton_Regular, GameColor.gray_3B3937))
    private val aRewardLbl = Label("+25", FontFactory.create(screen, parameter16, screen.fontGenerator_AlanSans_Medium, GameColor.gray_3B3937))

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
        aDayLbl.setSize(43f, 24f)
        add(aDayLbl) { startToStart(margin = 60f); centerY() }
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(68f, 19f)
        add(aRewardLbl) { endToEnd(margin = 16f); centerY() }
        aRewardLbl.setAlignment(Align.right)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(day: Int) {
        reward = PlayerModel.LIST_REWARD[day - 1]

        aDayLbl.setText("Day $day")
        aRewardLbl.setText("$reward RBX")
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    fun setState(state: DailyRewardState) {
        when(state) {

            DailyRewardState.CLAIM -> {
                aDayLbl.setFontColor(GameColor.beige_E2CEAA)
                aRewardLbl.setFontColor(GameColor.yellow_DDA334)
                update(aRewardLbl) { verticalBias = 0.3f }
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
            }

            DailyRewardState.CLAIMED -> {
                aDayLbl.setFontColor(GameColor.green_3FAA2A)
                aRewardLbl.setFontColor(GameColor.green_3FAA2A)
                update(aRewardLbl) { verticalBias = 0.3f }
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
            }

            DailyRewardState.LOCKED -> {
                aDayLbl.setFontColor(GameColor.gray_3B3937)
                aRewardLbl.setFontColor(GameColor.gray_3B3937)
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