package com.driftglass.home.game.actors.layout

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.advanced.AdvancedScreen

// ═════════════════════════════════════════════════════════════════════════════
//  AColumn / ARow — flex-колонка і flex-рядок для скрол-екранів (.ascroll, .car).
//
//  Чому не AAutoLayout: секції тут з'являються/зникають (черга, помилка),
//  а AAutoLayout рахує gap і для невидимих дітей. Тут — лише видимі, і
//  pref-розмір = вміст, як того хоче ScrollPane (інакше він розтягує вміст
//  на всю область і скрол зникає).
//
//  Дитина з userObject = FULL_BLEED стає на x = 0 (карусель «на всю ширину»).
// ═════════════════════════════════════════════════════════════════════════════
class AColumn(
    override val screen: AdvancedScreen,
    var gap: Float,
    var padTop: Float = 0f,
    var padBottom: Float = 0f,
    var padX: Float = 0f,
) : AdvancedGroup() {

    private var total = 0f

    override fun addActorsOnGroup() {}

    override fun getPrefWidth()  = width
    override fun getPrefHeight() = total

    override fun act(delta: Float) {
        super.act(delta)
        relayout()
    }

    fun relayout() {
        var t = padTop + padBottom
        var n = 0
        children.forEach { if (it.isVisible) { t += it.height; n++ } }
        t += gap * (n - 1).coerceAtLeast(0)
        if (t != total) {
            total = t
            val sp = parent as? ScrollPane
            if (sp != null) sp.invalidate() else height = t
        }
        var cy = height - padTop
        children.forEach { a ->
            if (!a.isVisible) return@forEach
            cy -= a.height
            a.setPosition(if (a.userObject === FULL_BLEED) 0f else padX, cy)
            cy -= gap
        }
    }

    companion object { val FULL_BLEED = Any() }
}

class ARow(
    override val screen: AdvancedScreen,
    var gap: Float,
    var padX: Float = 0f,
) : AdvancedGroup() {

    private var total = 0f

    override fun addActorsOnGroup() {}

    override fun getPrefWidth()  = total
    override fun getPrefHeight() = height

    override fun act(delta: Float) {
        super.act(delta)
        relayout()
    }

    fun relayout() {
        var t = padX * 2
        var n = 0
        children.forEach { if (it.isVisible) { t += it.width; n++ } }
        t += gap * (n - 1).coerceAtLeast(0)
        if (t != total) {
            total = t
            val sp = parent as? ScrollPane
            if (sp != null) sp.invalidate() else width = t
        }
        var cx = padX
        children.forEach { a: Actor ->
            if (!a.isVisible) return@forEach
            a.setPosition(cx, height - a.height)
            cx += a.width + gap
        }
    }
}
