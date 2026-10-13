package com.vidtools.framefall.game.screens.tabs

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.vidtools.framefall.game.actors.AScrollPane
import com.vidtools.framefall.game.actors.layout.AColumn
import com.vidtools.framefall.game.actors.layout.ARow
import com.vidtools.framefall.game.actors.ui.ATap
import com.vidtools.framefall.game.screens.AppScreen
import com.vidtools.framefall.game.utils.Dimens
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// ATabPage — вкладка AppScreen: вертикальний скрол (.ascroll: padding 44/16/18,
// gap 18) над міні-плеєром і таббаром. Підклас наповнює column у build().
// ─────────────────────────────────────────────────────────────────────────────
abstract class ATabPage(val app: AppScreen) : AdvancedGroup() {

    override val screen = app

    /** Ширина контенту між бічними відступами. */
    protected val W = app.worldWidth - Dimens.SIDE * 2

    protected val column = AColumn(app, Dimens.GAP_SECTION, padTop = app.safeStatusBarUI + px(10f), padBottom = px(18f), padX = Dimens.SIDE)
    protected val scroll = AScrollPane(column)
    private var lastBottom = -1f

    override fun addActorsOnGroup() {
        touchable = com.badlogic.gdx.scenes.scene2d.Touchable.childrenOnly
        column.setSize(width, 1f)
        addActor(scroll)
        layoutScroll()
        build(column)
    }

    abstract fun build(col: AColumn)

    open fun onShown() {}

    fun scrollToTop() { scroll.scrollY = 0f }

    override fun act(delta: Float) {
        super.act(delta)
        if (app.pageBottom() != lastBottom) layoutScroll()
    }

    private fun layoutScroll() {
        lastBottom = app.pageBottom()
        scroll.setBounds(0f, lastBottom, width, height - lastBottom)
    }

    // ------------------------------------------------------------------------
    // Хелпери розмітки
    // ------------------------------------------------------------------------
    /** Горизонтальна карусель на всю ширину (.car / .moods / .feat). */
    protected fun hScroll(row: ARow, h: Float): AScrollPane {
        row.setSize(1f, h)
        return AScrollPane(row, scrollX = true, scrollY = false).apply {
            setSize(app.worldWidth, h)
            userObject = AColumn.FULL_BLEED
        }
    }

    /** .sec-h: h3 Onest 700/16 (+ акцент redHi) і праворуч дія/підпис. */
    protected fun secHeader(title: String, accent: String? = null, right: String? = null, rightIsAction: Boolean = false, onRight: () -> Unit = {}): AdvancedGroup {
        val h3 = lbl(title, msdf.bold(16f))
        val ac = accent?.let { lbl(" $it", msdf.bold(16f, GameColor.accentText_FFB04A)) }
        val r = right?.let { lbl(it, if (rightIsAction) msdf.bold(12.5f, GameColor.accentText_FFB04A) else msdf.regular(12.5f, GameColor.muted_B3A293)) }
        return object : AdvancedGroup() {
            override val screen = app
            override fun addActorsOnGroup() {
                h3.setPosition(0f, 0f); addActor(h3)
                ac?.let { it.setPosition(h3.width, 0f); addActor(it) }
                r?.let { lbl ->
                    if (rightIsAction) {
                        val tap = object : ATap(app, 0.95f) { override fun addContent() { addActor(lbl); lbl.setPosition(0f, (height - lbl.height) / 2f) } }.onClick(onRight)
                        tap.setBounds(width - lbl.width, 0f, lbl.width, h3.height)
                        addActor(tap)
                    } else { lbl.setPosition(width - lbl.width, (h3.height - lbl.height) / 2f); addActor(lbl) }
                }
            }
        }.apply { setSize(W, h3.height) }
    }

    /** .empty: muted 13 по центру, відступи 18. */
    protected fun emptyText(text: String, w: Float = W): Actor =
        lbl(text, msdf.regular(13f, GameColor.muted_B3A293), Align.center).apply {
            setWrap(true); width = w; height = prefHeight + px(36f)
        }

    @Suppress("unused") protected val white = Color.WHITE
}
