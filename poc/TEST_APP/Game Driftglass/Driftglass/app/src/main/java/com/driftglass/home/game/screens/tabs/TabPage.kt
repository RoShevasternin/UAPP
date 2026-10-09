package com.driftglass.home.game.screens.tabs

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.driftglass.home.game.actors.AScrollPane
import com.driftglass.home.game.actors.layout.AColumn
import com.driftglass.home.game.actors.layout.ARow
import com.driftglass.home.game.actors.ui.AIconButton
import com.driftglass.home.game.screens.AppScreen
import com.driftglass.home.game.screens.ui.group
import com.driftglass.home.game.utils.Dimens
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// TabPage — вкладка AppScreen: вертикальний скрол над таббаром. Підклас наповнює
// column у build(); rebuild() — перебудувати вміст на місці (напр., змінились обрані).
// ─────────────────────────────────────────────────────────────────────────────
abstract class TabPage(val app: AppScreen) : AdvancedGroup() {

    override val screen = app

    /** Ширина контенту між бічними відступами. */
    protected val W = app.worldWidth - Dimens.SIDE * 2

    protected val column = AColumn(app, px(14f), padTop = app.safeStatusBarUI + px(10f), padBottom = px(20f), padX = Dimens.SIDE)
    protected val scroll = AScrollPane(column)

    override fun addActorsOnGroup() {
        touchable = Touchable.childrenOnly
        column.setSize(width, 1f)
        addActor(scroll)
        scroll.setBounds(0f, app.tabBarH, width, height - app.tabBarH)
        build(column)
    }

    abstract fun build(col: AColumn)

    open fun onShown() {}
    open fun onResumed() {}

    fun scrollToTop() { scroll.scrollY = 0f }

    /** Перебудувати на наступному кадрі: часто кличуть з кліку по кнопці, яку rebuild і видаляє. */
    fun rebuild() = com.driftglass.home.game.utils.runGDX { doRebuild() }

    private fun doRebuild() {
        if (stage == null) return
        val y = scroll.scrollY
        column.disposeAndClearChildren()
        build(column)
        scroll.layout(); scroll.scrollY = y; scroll.updateVisualScroll()
    }

    /** Шапка вкладки: заголовок Geologica 800/26 + (необов.) кнопка праворуч. */
    protected fun header(title: String, action: AIconButton? = null): AdvancedGroup {
        val t = lbl(title, msdf.disp(26f))
        return group(app, W, maxOf(t.height, px(38f))) {
            t.setPosition(0f, (height - t.height) / 2f); addActor(t)
            action?.let { it.setBounds(width - px(38f), (height - px(38f)) / 2f, px(38f), px(38f)); addActor(it) }
        }
    }

    /** Горизонтальна стрічка на всю ширину (чіпи). */
    protected fun hScroll(row: ARow, h: Float): AScrollPane {
        row.setSize(1f, h)
        return AScrollPane(row, scrollX = true, scrollY = false).apply {
            setSize(app.worldWidth, h)
            userObject = AColumn.FULL_BLEED
        }
    }
}
