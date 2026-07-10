package com.rbxrush.rushrbx.game.actors.panel.guess

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter
import com.rbxrush.rushrbx.game.utils.gdxGame

class AItemGuess(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterReward = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+RBX")
        .setSize(10)

    private val lsReward = FontFactory.create(screen, parameterReward, screen.fontGenerator_Fredoka_Bold, GameColor.black_2C2C2C)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()
    private val aRewardLbl = Label("", lsReward)

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private val stateClose = gdxGame.assetsAll.close_f
    private val stateWin   = gdxGame.assetsAll.win_f
    private val stateLose  = gdxGame.assetsAll.lose_f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aItemImg) { fillParent() }
        addRewardLbl()
    }

    private fun addRewardLbl() {
        aRewardLbl.setSize(50f, 11f)
        add(aRewardLbl) { centerX(); topToTop(margin = 8f) }
        aRewardLbl.setAlignment(Align.center)
        aRewardLbl.isVisible = false
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setState(state: State, reward: Long = 0L) {
        when (state) {
            State.CLOSE -> {
                aItemImg.drawable = TextureRegionDrawable(stateClose)
                aRewardLbl.isVisible = false
            }
            State.WIN -> {
                aItemImg.drawable = TextureRegionDrawable(stateWin)
                aRewardLbl.setText("+$reward RBX")
                aRewardLbl.isVisible = true
            }
            State.LOSE -> {
                aItemImg.drawable = TextureRegionDrawable(stateLose)
                aRewardLbl.isVisible = false
            }
        }
    }

    // ------------------------------------------------------------------------
    // enum State
    // ------------------------------------------------------------------------
    enum class State {
        CLOSE,
        WIN,
        LOSE
    }
}