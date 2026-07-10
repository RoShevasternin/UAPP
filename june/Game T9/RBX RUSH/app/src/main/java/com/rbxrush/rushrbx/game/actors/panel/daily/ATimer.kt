package com.rbxrush.rushrbx.game.actors.panel.daily

import android.annotation.SuppressLint
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxrush.rushrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxrush.rushrbx.game.utils.GameColor
import com.rbxrush.rushrbx.game.utils.advanced.AdvancedScreen
import com.rbxrush.rushrbx.game.utils.font.FontFactory
import com.rbxrush.rushrbx.game.utils.font.FontParameter
import com.rbxrush.rushrbx.game.utils.gdxGame

class ATimer(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + ":")
        .setSize(12)

    private val lsTimer = FontFactory.create(screen, parameter, screen.fontGenerator_Fredoka_Regular, GameColor.black_2C2C2C)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg   = Image(gdxGame.assetsAll.panel_daily)
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
        add(aBgImg) { fillParent() }
        addTimeLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addTimeLbl() {
        add(aTimeLbl) { startToStart(margin = 12f); endToEnd(margin = 12f); centerY() }
        aTimeLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
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

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
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

        aTimeLbl.pack()
        width = (12f + aTimeLbl.width + 12f).coerceAtLeast(70f)
    }



}