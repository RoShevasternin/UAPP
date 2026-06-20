package com.coinsclub.funrbx.game.actors.panel.daily

import android.annotation.SuppressLint
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.GameColor
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.font.FontFactory
import com.coinsclub.funrbx.game.utils.font.FontParameter
import com.coinsclub.funrbx.game.utils.font.setBorderAndShadow

class ATimer(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + ":")
        .setSize(18)
        .setBorderAndShadow(border = 5f, shadowX = 6, shadowY = 4)

    private val lsTimer = FontFactory.create(screen, parameter, screen.fontGenerator_LuckiestGuy_Regular, GameColor.white_D9D4EB)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTimeLbl = Label("00:00:00", lsTimer)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private var endTimeMillis = 0L

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onFinish: () -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addTimeLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addTimeLbl() {
        add(aTimeLbl) { fillParent() }
        aTimeLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    fun start(seconds: Long) {
        stop()
        if (seconds <= 0L) {
            updateText(0L)
            return                  // ← БЕЗ onFinish() тут
        }

        endTimeMillis = System.currentTimeMillis() + seconds * 1000L
        updateText(seconds)

        addAction(Actions.forever(Actions.sequence(
            Actions.delay(1f),
            Actions.run { tick() }
        )))
    }

    fun stop() {
        clearActions()
    }

    private fun tick() {
        val remain = (endTimeMillis - System.currentTimeMillis()) / 1000L
        if (remain <= 0L) {
            updateText(0L)
            stop()
            onFinish()
        } else {
            updateText(remain)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateText(seconds: Long) {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        aTimeLbl.setText(String.format("%02d:%02d:%02d", h, m, s))
    }
}