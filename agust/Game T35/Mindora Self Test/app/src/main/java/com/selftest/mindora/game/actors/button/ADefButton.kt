package com.selftest.mindora.game.actors.button

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.base.AButtonBase
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.ui.ARoundRect
import com.selftest.mindora.game.utils.actor.disable
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle

class ADefButton(
    override val screen: AdvancedScreen,
    text: String,
    styleMsdf: MsdfStyle,
) : AButtonBase(screen) {

    companion object {
        const val H_BTN = 44f

        private const val RADIUS = 15f

        // GHOST: майже прозора заливка + тонка
        private const val GHOST_FILL         = 0.08f
        private const val GHOST_STROKE       = 2f      // ≥ aaWidth, інакше лінія не добере альфи
        private const val GHOST_STROKE_ALPHA = 0.22f

        // Прес: масштаб + притемнення фону
        private const val PRESS_SCALE  = 0.94f
        private const val PRESS_DIM    = 0.80f
        private const val PRESS_TIME   = 0.08f
        private const val UNPRESS_TIME = 0.18f

        // Disabled
        private const val DISABLED_ALPHA = 0.40f
        private const val FADE_TIME      = 0.15f
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBg = ARoundRect(screen)
    val label = AMsdfLabel(text, styleMsdf)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------

    /**
     * Бекінг-поле, а не делегат до aBg: applyVariant() перезаписує фон
     * повністю і мусить знати, який радіус ВІДНОВИТИ. Делегат цю памʼять
     * втрачав — кастомний радіус зникав при першій зміні варіанта.
     */
    var radius: Float = RADIUS
        set(value) { field = value; aBg.radius = value }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        super.addActorsOnGroup()   // origin + слухач тапів

        addAndFillActor(aBg)
        addAndFillActor(label)

        label.disable()
        label.setAlignment(Align.center)

        applyVariant()   // ініціалізатор поля НЕ проходить через сеттер
    }

    // ------------------------------------------------------------------------
    // Look
    // ------------------------------------------------------------------------

    /** Геометрія й заливка — те, що НЕ залежить від теми. */
    private fun applyVariant() {
        aBg.radius = radius

        aBg.fillAlpha   = GHOST_FILL
        aBg.strokeWidth = GHOST_STROKE
        aBg.strokeAlpha = GHOST_STROKE_ALPHA
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setText(text: CharSequence) { label.setText(text) }

    // ------------------------------------------------------------------------
    // States
    // ------------------------------------------------------------------------
    override fun press() {
        clearActions(); aBg.clearActions()
        addAction(Actions.scaleTo(PRESS_SCALE, PRESS_SCALE, PRESS_TIME, Interpolation.fastSlow))
        aBg.addAction(Actions.alpha(PRESS_DIM, PRESS_TIME))
    }

    override fun unpress() {
        clearActions(); aBg.clearActions()
        addAction(Actions.scaleTo(1f, 1f, UNPRESS_TIME, Interpolation.fastSlow))
        aBg.addAction(Actions.alpha(1f, UNPRESS_TIME))
    }

    override fun disable() {
        touchable = Touchable.disabled
        fadeParts(DISABLED_ALPHA)
    }

    override fun enable() {
        touchable = Touchable.enabled
        clearActions()
        addAction(Actions.scaleTo(1f, 1f, UNPRESS_TIME, Interpolation.fastSlow))
        fadeParts(1f)
    }

    /** Гасимо частини окремо, а не групу: у групи альфа зайнята прес-анімацією. */
    private fun fadeParts(alpha: Float) {
        aBg.clearActions();   aBg.addAction(Actions.alpha(alpha, FADE_TIME))
        label.clearActions(); label.addAction(Actions.alpha(alpha, FADE_TIME))
    }

}