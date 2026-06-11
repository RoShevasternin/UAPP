package com.bossrbx.rbxcalculator.game.screens.main.flipCard.state

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.bossrbx.rbxcalculator.game.actors.panel.flipCard.ACardReward
import com.bossrbx.rbxcalculator.game.actors.panel.flipCard.APanelCongratulations
import com.bossrbx.rbxcalculator.game.actors.panel.flipCard.APanelFlipCard
import com.bossrbx.rbxcalculator.game.utils.TIME_SCREEN_STATE
import com.bossrbx.rbxcalculator.game.utils.actor.animHideAndDisable
import com.bossrbx.rbxcalculator.game.utils.actor.animShowAndEnable
import com.bossrbx.rbxcalculator.game.utils.actor.enable
import com.bossrbx.rbxcalculator.game.utils.gdxGame
import com.bossrbx.rbxcalculator.game.utils.screenState.ScreenContext
import com.bossrbx.rbxcalculator.game.utils.screenState.ScreenState

class StateCongratulation(
    context                   : ScreenContext,
    private val dim           : Image,
    private val panel         : APanelCongratulations,
    private val cardReward    : ACardReward,
    private val aPanelFlipCard: APanelFlipCard,
) : ScreenState(context) {

    override fun onEnter() {
        dim.zIndex        = Int.MAX_VALUE - 2
        panel.zIndex      = Int.MAX_VALUE - 1
        cardReward.zIndex = Int.MAX_VALUE

        aPanelFlipCard.animHideAndDisable(TIME_SCREEN_STATE)
        dim.animShowAndEnable(TIME_SCREEN_STATE)
        panel.animShowAndEnable(TIME_SCREEN_STATE)

        animEnterCardReward()

        gdxGame.soundUtil.apply { play(WIN) }
    }

    override fun onExit() {
        cardReward.clearActions()
        dim.animHideAndDisable(TIME_SCREEN_STATE)
        panel.animHideAndDisable(TIME_SCREEN_STATE)
        animExitCardReward()
        aPanelFlipCard.animShowAndEnable(TIME_SCREEN_STATE)
    }

    // ------------------------------------------------------------------------
    // Animations
    // ------------------------------------------------------------------------

    private fun animEnterCardReward() {
        cardReward.enable()
        cardReward.setOrigin(Align.center)
        cardReward.color.a = 0f
        cardReward.setScale(0f)
        cardReward.rotation = -12f
        cardReward.clearActions()

        cardReward.addAction(
            Actions.sequence(

                Actions.delay(0.15f),

                // 1. Вилітає — як карту кидають на стіл
                Actions.parallel(
                    Actions.fadeIn(0.4f, Interpolation.smooth),
                    Actions.scaleTo(1.15f, 1.15f, 0.4f, Interpolation.swingOut),
                    Actions.rotateTo(3f, 0.4f, Interpolation.smooth)
                ),

                // 2. Осідає рівно
                Actions.parallel(
                    Actions.scaleTo(1f, 1f, 0.2f, Interpolation.smooth),
                    Actions.rotateTo(0f, 0.2f, Interpolation.smooth)
                ),

                // 3. Два пульси — "вау, ось твоя нагорода!"
                Actions.scaleTo(1.08f, 1.08f, 0.16f, Interpolation.sine),
                Actions.scaleTo(1f, 1f, 0.16f, Interpolation.sine),
                Actions.scaleTo(1.05f, 1.05f, 0.13f, Interpolation.sine),
                Actions.scaleTo(1f, 1f, 0.13f, Interpolation.sine),

                // 4. Левітація — нескінченно поки юзер не забере
                Actions.forever(
                    Actions.sequence(
                        Actions.moveBy(0f, 6f, 1.2f, Interpolation.sine),
                        Actions.moveBy(0f, -6f, 1.2f, Interpolation.sine),
                    )
                )
            )
        )
    }

    private fun animExitCardReward() {
        // Скидаємо зміщення від левітації перед виходом
        val originalY = cardReward.y - (cardReward.y % 6f)

        cardReward.addAction(
            Actions.sequence(
                // Швидко повертаємось на оригінальну позицію
                Actions.moveTo(cardReward.x, originalY, 0.1f),
                // Зникаємо з легким обертанням
                Actions.parallel(
                    Actions.fadeOut(0.25f, Interpolation.smooth),
                    Actions.scaleTo(0f, 0f, 0.25f, Interpolation.fastSlow),
                    Actions.rotateTo(8f, 0.25f, Interpolation.smooth)
                ),
                Actions.run {
                    cardReward.setScale(1f)
                    cardReward.rotation = 0f
                }
            )
        )
    }
}