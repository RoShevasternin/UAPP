package com.rbxtreasure.fungamers.game.actors.panel.finds

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbxtreasure.fungamers.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxtreasure.fungamers.game.model.PlayerModel
import com.rbxtreasure.fungamers.game.utils.GameColor
import com.rbxtreasure.fungamers.game.utils.actor.setFontColor
import com.rbxtreasure.fungamers.game.utils.advanced.AdvancedScreen
import com.rbxtreasure.fungamers.game.utils.font.FontFactory
import com.rbxtreasure.fungamers.game.utils.font.FontParameter
import com.rbxtreasure.fungamers.game.utils.gdxGame

class AItemFinds(override val screen: AdvancedScreen): AConstraintLayout(screen) {

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