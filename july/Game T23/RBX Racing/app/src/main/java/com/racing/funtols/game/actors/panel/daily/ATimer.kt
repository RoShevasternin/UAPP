package com.racing.funtols.game.actors.panel.daily

import android.annotation.SuppressLint
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.racing.funtols.game.actors.label.AMsdfLabel
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.utils.GameColor
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.font.msdf.MsdfStyle
import com.racing.funtols.game.utils.gdxGame

class ATimer(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleDef = MsdfStyle(msdf, msdf.fontBarlow_Regular, 14f, GameColor.black_1A1A1A)

    private val aTimeLbl = AMsdfLabel("00:00:00", styleDef)

    private var endTimeMillis = 0L

    var onFinish: () -> Unit = {}

    override fun addActorsOnGroup() {
        addTimeLbl()
    }

    private fun addTimeLbl() {
        add(aTimeLbl) { fillParent() }
        aTimeLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun start(seconds: Long) {
        stop()
        if (seconds <= 0L) {
            updateText(0L)
            return
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
    }
}