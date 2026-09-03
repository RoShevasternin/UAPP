package com.selftest.mindora.game.actors.progress

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.vfx.AMask
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedGroup
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame

// ─────────────────────────────────────────────────────────────────────────────
// Прогрес проходження теста (Figma: Progressbar_Test, 344×5).
//
// РЕАЛІЗАЦІЯ — маска, а не два прямокутники: заливка це суцільна плашка на
// всю ширину, яка ВИЇЖДЖАЄ зліва направо, а mask_progress_test обрізає її по
// формі треку з круглими торцями. Плюс проти двох ARoundRect: торці й радіус
// приходять з текстури дизайнера, а не підбираються числом у коді.
//
//   0%   → x = -width  (плашка повністю за лівим краєм маски)
//   100% → x = 0       (плашка збігається з маскою)
// ─────────────────────────────────────────────────────────────────────────────
class AProgressTestBar(override val screen: AdvancedScreen) : AdvancedGroup() {

    companion object {
        /** Тривалість переїзду між питаннями. 0 = миттєво. */
        private const val TIME_MOVE = 0.25f
    }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBg       = Image(gdxGame.assetsAll.progress_back_test)
    private val aMask     = AMask(screen, gdxGame.assetsAll.mask_progress_test)
    private val aFillImg  = Image(screen.drawerUtil.getTexture(GameColor.pink_A76EFF))

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Остання задана частка. Потрібна для sizeChanged: якщо ширину виставили
    // ПІСЛЯ першого setProgress, позицію треба перерахувати — інакше бар
    // лишиться порахованим під нульову ширину.
    private var fraction = 0f

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aBg)
        addAndFillActor(aMask)
        aMask.addAndFillActor(aFillImg)

        applyPosition(animated = false)
    }

    override fun sizeChanged() {
        super.sizeChanged()
        applyPosition(animated = false)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * @param fraction 0..1 — рівно те, що віддає TestController.progressFraction.
     *        Не відсотки: одна одиниця виміру на весь ланцюжок, інакше
     *        десь загубиться множення на 100.
     */
    fun setProgress(fraction: Float, animated: Boolean = true) {
        this.fraction = fraction.coerceIn(0f, 1f)
        applyPosition(animated)
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    private fun applyPosition(animated: Boolean) {
        // width, а не константа 344: розмір виставляє екран через setSize, і
        // друге зашите число розійшлося б з ним мовчки.
        val targetX = -width * (1f - fraction)

        aFillImg.clearActions()
        if (animated && TIME_MOVE > 0f) {
            aFillImg.addAction(Actions.moveTo(targetX, aFillImg.y, TIME_MOVE))
        } else {
            aFillImg.x = targetX
        }
    }
}