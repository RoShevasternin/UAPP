package com.rsbuxs.rcounbux.game.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rsbuxs.rcounbux.game.actors.label.ALabel
import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import com.rsbuxs.rcounbux.game.utils.font.FontParameter
import com.rsbuxs.rcounbux.game.utils.gdxGame

class ATimer(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(16)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.panel_gradient)
    private val aTimerLbl = ALabel(screen, "", Color.WHITE, parameter, screen.fontGenerator_Bold)

    // ------------------------------------------------------------------------
    // Timer State
    // ------------------------------------------------------------------------
    private var timeLeft = 0f
    private var isRunning = false

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onTimeout = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPanelImg) { fillParent() }
        add(aTimerLbl) { fillParent() }

        aTimerLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun start(seconds: Int) {
        timeLeft = seconds.toFloat()
        isRunning = true
        aTimerLbl.color = Color.WHITE
        updateLabel()
    }

    fun stop() {
        isRunning = false
    }

    fun reset(seconds: Int) {
        timeLeft = seconds.toFloat()
        updateLabel()
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    override fun act(delta: Float) {
        super.act(delta)

        if (!isRunning) return

        timeLeft -= delta

        if (timeLeft <= 0f) {
            timeLeft = 0f
            isRunning = false
            updateLabel()
            onTimeout()
            return
        }

        updateLabel()

        // 🔥 Danger zone
        if (timeLeft <= 6f) {
            aTimerLbl.setLabelColor(Color.RED)
        }
    }

    // ------------------------------------------------------------------------
    // Utils
    // ------------------------------------------------------------------------
    private fun updateLabel() {
        val totalSeconds = timeLeft.toInt()

        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        aTimerLbl.setText("%d:%02d".format(minutes, seconds))
    }
}