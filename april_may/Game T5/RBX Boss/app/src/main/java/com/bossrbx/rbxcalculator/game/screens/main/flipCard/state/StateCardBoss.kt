package com.bossrbx.rbxcalculator.game.screens.main.flipCard.state

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.utils.Block
import com.bossrbx.rbxcalculator.game.utils.TIME_SCREEN_STATE
import com.bossrbx.rbxcalculator.game.utils.actor.animDelay
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShowAndEnable
import com.bossrbx.rbxcalculator.game.utils.actor.enable
import com.bossrbx.rbxcalculator.game.utils.screenState.ScreenContext
import com.bossrbx.rbxcalculator.game.utils.screenState.ScreenState
import org.jetbrains.annotations.Blocking

class StateCardBoss(
    context             : ScreenContext,
    private val dim     : Image,
    private val cardBoss: Image,
    val blockCongratulation: Block
) : ScreenState(context) {

    override fun onEnter() {
        dim.zIndex     = Int.MAX_VALUE - 1
        cardBoss.zIndex = Int.MAX_VALUE

        // Dim — звичайна поява
        dim.animShowAndEnable(TIME_SCREEN_STATE)

        animEnterCard { blockCongratulation() }
    }

    override fun onExit() {
        cardBoss.clearActions()
        dim.animHideAndDisable(TIME_SCREEN_STATE)

        animExitCard()
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animEnterCard(onEnd: Block) {
        // Card Boss — крута анімація
        cardBoss.enable()
        cardBoss.setOrigin(Align.center)
        cardBoss.color.a = 0f
        cardBoss.setScale(0f)
        cardBoss.clearActions()

        cardBoss.addAction(
            Actions.sequence(

                // 1. Виростає з нуля + з'являється
                Actions.parallel(
                    Actions.fadeIn(0.4f, Interpolation.smooth),
                    Actions.scaleTo(1.15f, 1.15f, 0.4f, Interpolation.swingOut)
                ),

                // 2. Осідає до нормального розміру
                Actions.scaleTo(1f, 1f, 0.15f, Interpolation.smooth),

                // 3. Пульс 1 — збільшується
                Actions.scaleTo(1.08f, 1.08f, 0.18f, Interpolation.sine),
                // Пульс 1 — зменшується
                Actions.scaleTo(1f, 1f, 0.18f, Interpolation.sine),

                // 4. Пульс 2 — менший
                Actions.scaleTo(1.05f, 1.05f, 0.15f, Interpolation.sine),
                // Пульс 2 — зменшується до норми
                Actions.scaleTo(1f, 1f, 0.15f, Interpolation.sine),

                Actions.run { onEnd() }
            )
        )
    }

    private fun animExitCard() {
        // Карта зникає назад — зменшується і зникає
        cardBoss.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.fadeOut(0.25f, Interpolation.smooth),
                    Actions.scaleTo(0f, 0f, 0.25f, Interpolation.fastSlow)
                ),
                Actions.run { cardBoss.setScale(1f) }
            )
        )
    }

}