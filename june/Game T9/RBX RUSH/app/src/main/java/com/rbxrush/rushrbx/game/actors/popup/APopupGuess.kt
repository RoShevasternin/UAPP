package com.rbxrush.rushrbx.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.actor.setOnClickListener
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter
import com.rbxrush.rushrbx.game.utils.gdxGame

class APopupGuess(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(60)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Fredoka_Bold, GameColor.yellow_FACA4F)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP_GUESS)
    private val aResultLbl   = Label("0 RBX", lsDef)
    private val aContinueBtn = Actor()
    private val aMoreBtn     = Actor()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onContinue = {}
    var onMore     = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }
        addRewardLbl()
        addContinue()
        addMore()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addRewardLbl() {
        aResultLbl.setSize(280f, 60f)
        add(aResultLbl) { centerX(); topToTop(margin = 100f) }
        aResultLbl.setAlignment(Align.center)
        aResultLbl.setEllipsis(true)
    }

    private fun addContinue() {
        aContinueBtn.setSize(280f, 56f)
        add(aContinueBtn) { centerX(); topToTop(margin = 180f) }

        aContinueBtn.setOnClickListener {
            gdxGame.soundUtil.apply { play(REWARD) }
            onContinue()
        }
    }

    private fun addMore() {
        aMoreBtn.setSize(280f, 56f)
        add(aMoreBtn) { centerX(); topToTop(margin = 245f) }

        aMoreBtn.setOnClickListener {
            gdxGame.soundUtil.apply { play(REWARD) }
            onMore()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(reward: Long) {
        aResultLbl.setText("$reward RBX")
    }

    fun setMoreVisible(visible: Boolean) {
        aMoreBtn.isVisible = visible
        aMoreBtn.touchable = if (visible) Touchable.enabled else Touchable.disabled
    }
}