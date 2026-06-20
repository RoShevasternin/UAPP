package com.coinsclub.funrbx.game.actors.panel.guess

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class AItemGuess(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg = Image()

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
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    fun setState(state: State) {
        when(state) {

            State.CLOSE -> {
                aItemImg.drawable = TextureRegionDrawable(stateClose)
            }

            State.WIN -> {
                aItemImg.drawable = TextureRegionDrawable(stateWin)
            }

            State.LOSE -> {
                aItemImg.drawable = TextureRegionDrawable(stateLose)
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