package com.skindustry.skinly.game.screens.state

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.skindustry.skinly.game.actors.popup.APopupUnlock
import com.skindustry.skinly.game.utils.actor.animHideAndDisable
import com.skindustry.skinly.game.utils.actor.animShowAndEnable
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.screenState.ScreenContext
import com.skindustry.skinly.game.utils.screenState.ScreenState

class StateUnlockPopup(
    context             : ScreenContext,
    private val popup   : APopupUnlock,
    private val dim     : Image,
) : ScreenState(context) {

    var onWatch : (() -> Unit)? = null
    // true — розблоковано (монети списано), закриваємо; false — не вистачило, лишаємо
    var onCoins : (() -> Boolean)? = null
    var onCancel: (() -> Unit)? = null

    // Хуки відкриття/закриття — екрани з нативкою ховають її на час попапа:
    // нативка — Android-view поверх GDX і інакше перекриває низ діалогу
    var onOpened: (() -> Unit)? = null
    var onClosed: (() -> Unit)? = null

    override fun onEnter() {
        onOpened?.invoke()
        dim.animShowAndEnable()
        popup.animShowAndEnable()

        popup.onWatch = {
            //animDelay(1f) { goBack() }

            context.dismiss()  // закриваємо діалог
            onWatch?.invoke()   // показуємо рекламу
        }
        popup.onCoins = {
            if (onCoins?.invoke() == true) context.dismiss()
        }
        popup.onCancel = {
            context.dismiss()
            onCancel?.invoke()
        }
    }

    override fun onExit() {
        dim.animHideAndDisable()
        popup.animHideAndDisable()
        onClosed?.invoke()
    }
}