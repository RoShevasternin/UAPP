package com.redwave.downloader.game.screens.base

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.redwave.downloader.game.actors.ui.ASheet
import com.redwave.downloader.game.actors.ui.AToast
import com.redwave.downloader.game.utils.Block
import com.redwave.downloader.game.utils.TIME_ANIM_SCREEN
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// RedwaveScreen — спільне для екранів Redwave:
//   • content — група на весь екран, у якій екран будує себе (y вгору,
//     safe area — через safeStatusBarUI / safeNavBarUI);
//   • поява: alpha + зсув 10 знизу (.s.enter у прототипі), 0.27 с;
//   • тост (AToast) і шторки (ASheet) поверх усього; «Назад» спершу закриває шторку.
// ─────────────────────────────────────────────────────────────────────────────
abstract class RedwaveScreen : AdvancedScreen() {

    val content = object : AdvancedGroup() {
        override val screen = this@RedwaveScreen
        override fun addActorsOnGroup() { buildContent() }
    }

    protected val toast by lazy { AToast(this) }
    var sheet: ASheet? = null
        private set

    /** Відстань тосту від низу (над таббаром на AppScreen). */
    open val toastBottom: Float get() = safeNavBarUI + px(112f - 38f)

    abstract fun buildContent()

    override fun show() {
        super.show()
        // rootConstraintLayout з T35 лежить поверх content на весь екран — без цього він ковтає всі дотики
        rootConstraintLayout.touchable = Touchable.childrenOnly
    }

    override fun Group.addActorsOnStageUI() {
        addActor(content)
        content.setSize(worldWidth, worldHeight)
        addActor(toast)
        animShowScreen()
    }

    fun showToast(text: String) = toast.show(text, worldWidth, toastBottom)

    fun openSheet(s: ASheet) {
        sheet?.close()
        sheet = s
        s.onClosed = { if (sheet === s) sheet = null }
        stageUI.root.addActor(s)
        s.setBounds(0f, 0f, worldWidth, worldHeight)
        toast.toFront()
    }

    override fun onBackPressed() {
        val s = sheet
        if (s != null) { s.close(); return }
        super.onBackPressed()
    }

    override fun animShowScreen(blockEnd: Block) {
        content.color.a = 0f
        content.y = -px(10f)
        content.addAction(Actions.sequence(
            Actions.parallel(Actions.fadeIn(TIME_ANIM_SCREEN), Actions.moveTo(0f, 0f, TIME_ANIM_SCREEN, Interpolation.pow3Out)),
            Actions.run(blockEnd),
        ))
    }

    override fun animHideScreen(blockEnd: Block) {
        content.touchable = Touchable.disabled
        content.addAction(Actions.sequence(Actions.fadeOut(0.12f), Actions.run(blockEnd)))
    }
}

/**
 * Смуга під статус-баром для скрол-екранів: зверху колір фону (alpha), донизу — прозорість.
 * Контент проїжджає під нею, а годинник/іконки системи лишаються читабельними.
 */
fun statusScrim(screen: RedwaveScreen, bg: com.badlogic.gdx.graphics.Color, alpha: Float = 0.97f): com.redwave.downloader.game.actors.ui.ARect {
    val h = screen.safeStatusBarUI + px(18f)
    val top = bg.cpy().apply { a = alpha }
    val bottom = bg.cpy().apply { a = 0f }
    // суцільна на всю висоту статус-бару, далі — плавно в нуль
    return com.redwave.downloader.game.actors.ui.ARect(screen, 0f, top, bottom, angleCss = 180f, start = screen.safeStatusBarUI / h).apply {
        setBounds(-px(4f), screen.worldHeight - h, screen.worldWidth + px(8f), h + px(4f))
        touchable = Touchable.disabled
    }
}
