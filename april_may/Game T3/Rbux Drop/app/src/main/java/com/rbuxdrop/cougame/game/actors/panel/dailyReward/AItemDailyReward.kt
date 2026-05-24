package com.rbuxdrop.cougame.game.actors.panel.dailyReward

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbuxdrop.cougame.game.actors.label.ALabel
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.GameColor
import com.rbuxdrop.cougame.game.utils.advanced.AdvancedScreen
import com.rbuxdrop.cougame.game.utils.font.FontParameter
import com.rbuxdrop.cougame.game.utils.gdxGame

class AItemDailyReward(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "Day + RBX")
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aDayLbl    = ALabel(screen, "Day 0", GameColor.gary_7F, parameter, screen.fontGenerator_Bold)
    private val aRewardLbl = ALabel(screen, "+100 RBX", GameColor.gary_7F, parameter, screen.fontGenerator_Bold)

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
        aDayLbl.setSize(42f, 22f)
        add(aDayLbl) { startToStart(margin = 76f); centerY() }
    }

    private fun addRewardLbl() {
        addActor(aRewardLbl)
        aRewardLbl.setBounds(214f, 25f, 82f, 22f)
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
                aDayLbl.setLabelColor(Color.WHITE)
                aRewardLbl.setLabelColor(GameColor.purple_3D)
                aItemImg.drawable = TextureRegionDrawable(stateClaim)
                aRewardLbl.setBounds(252f, 15f, 80f, 22f)
            }

            DailyRewardState.CLAIMED -> {
                aDayLbl.setLabelColor(GameColor.green_4B)
                aRewardLbl.setLabelColor(GameColor.green_4B)
                aItemImg.drawable = TextureRegionDrawable(stateClaimed)
                aRewardLbl.setBounds(252f, 35f, 80f, 22f)
            }

            DailyRewardState.LOCKED -> {
                aDayLbl.setLabelColor(GameColor.gary_7F)
                aRewardLbl.setLabelColor(GameColor.gary_7F)
                aItemImg.drawable = TextureRegionDrawable(stateClose)
                aRewardLbl.setBounds(214f, 25f, 82f, 22f)
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