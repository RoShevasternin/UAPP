package com.rbxgolden.fungamems.game.actors.panel.dailyReward

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.actor.setFontColor
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import com.rbxgolden.fungamems.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "Day + RBX")
        .setSize(20)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = Label("Day 0", FontFactory.create(screen, parameter, screen.fontGenerator_Bold, GameColor.gray_5C))
    private val aRewardLbl = Label("+100 RBX", FontFactory.create(screen, parameter, screen.fontGenerator_Bold, GameColor.gray_5C))

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
        aDayLbl.setSize(100f, 24f)
        add(aDayLbl) { startToStart(margin = 52f); centerY() }
    }

    private fun addRewardLbl() {
        addActor(aRewardLbl)
        aRewardLbl.setBounds(223f, 13f, 97f, 24f)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(day: Int) {
        reward = gdxGame.modelPlayer.listReward[day - 1]

        aRewardLbl.setText("+$reward RBX")
        aDayLbl.setText("Day $day")
    }

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    fun setState(state: DailyRewardState) {


        when(state) {

            DailyRewardState.CLAIM -> {
                aDayLbl.setFontColor(Color.BLACK)
                aRewardLbl.setFontColor(Color.BLACK)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
                //aRewardLbl.setBounds(252f, 15f, 80f, 22f)
            }

            DailyRewardState.CLAIMED -> {
                aDayLbl.setFontColor(GameColor.green_28)
                aRewardLbl.setFontColor(GameColor.green_28)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
                //aRewardLbl.setBounds(252f, 35f, 80f, 22f)
            }

            DailyRewardState.LOCKED -> {
                aDayLbl.setFontColor(GameColor.gray_5C)
                aRewardLbl.setFontColor(GameColor.gray_5C)
                aItemImg.drawable = TextureRegionDrawable(stateClose)
                //aRewardLbl.setBounds(214f, 25f, 82f, 22f)
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