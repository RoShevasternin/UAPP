package com.sakurbx.fungambx.game.actors.panel.guess

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame

class AItemGuess(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aItemImg   = Image()

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
    fun setState(state: State) {
        when (state) {
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