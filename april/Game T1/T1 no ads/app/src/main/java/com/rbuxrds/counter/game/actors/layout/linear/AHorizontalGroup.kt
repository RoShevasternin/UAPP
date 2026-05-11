package com.rbuxrds.counter.game.actors.layout.linear

import com.badlogic.gdx.scenes.scene2d.Actor
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.layout.DirectionH
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

// ═════════════════════════════════════════════════════════════════════════════
//  AHorizontalGroup
//
//  Розташовує дітей горизонтально. Повністю динамічна.
//
//  ┌──────────────────────────────────────────────────┐
//  │ paddingLeft                         paddingRight │
//  │  ┌────────┐  gap  ┌────────┐  gap  ┌────────┐    │
//  │  │ child0 │       │ child1 │       │ child2 │    │
//  │  └────────┘       └────────┘       └────────┘    │
//  └──────────────────────────────────────────────────┘
//     alignV визначає y кожної дитини
//
//  ПАРАМЕТРИ:
//    gap        — відступ між дітьми
//    alignH     — розміщення вздовж горизонтальної осі (LEFT/RIGHT/CENTER/SPREAD)
//    alignV     — вирівнювання кожної дитини по вертикалі
//    direction  — RIGHT (перший зліва) або LEFT (перший справа)
//    wrapWidth  — true: ширина групи = сума ширин дітей + gap + padding
//    wrapHeight — true: висота групи = висота найвищої дитини + padding
//
//  ВСІ ПАРАМЕТРИ ЗМІНЮВАНІ В RUNTIME.
//
//  ВИКОРИСТАННЯ:
//    val group = AHorizontalGroup(screen, gap = 20f, alignV = AlignV.CENTER)
//    group.setSize(2000f, 200f)
//    addActor(group)
//    group.addActor(aIcon1)
//    group.addActor(aIcon2)
// ═════════════════════════════════════════════════════════════════════════════

class AHorizontalGroup(
    screen     : AdvancedScreen,
    gap        : Float      = 0f,
    alignH     : AlignH = AlignH.LEFT,
    alignV     : AlignV = AlignV.BOTTOM,
    direction  : DirectionH = DirectionH.RIGHT,
    wrapWidth  : Boolean    = false,
    wrapHeight : Boolean    = false,
) : ALinearGroup(screen) {

    var gap        = gap;        set(v) { field = v; relayout() }
    var alignH     = alignH;     set(v) { field = v; relayout() }
    var alignV     = alignV;     set(v) { field = v; relayout() }
    var direction  = direction;  set(v) { field = v; relayout() }
    var wrapWidth  = wrapWidth;  set(v) { field = v; relayout() }
    var wrapHeight = wrapHeight; set(v) { field = v; relayout() }

    // ── getPrefWidth / getPrefHeight — для ScrollPane ─────────────────────────
    // ScrollPane визначає розмір контенту через ці методи, а не через width/height.
    // Без них ScrollPane думає що контент має розмір 0 і скрол не працює.

    override fun getPrefWidth()  = width
    override fun getPrefHeight() = height

    // ── layout() ─────────────────────────────────────────────────────────────

    override fun layout() {
        if (children.isEmpty) return

        // Wrap: виставляємо розміри і одразу розміщуємо дітей в тому ж кадрі.
        // НЕ робимо return — інакше один кадр діти на старих позиціях → дрижання.
        if (wrapWidth) {
            val needed = totalChildrenWidth() + paddingLeft + paddingRight
            if (width != needed) { width = needed }
        }
        if (wrapHeight) {
            val needed = (children.maxOfOrNull { it.height } ?: 0f) + paddingTop + paddingBottom
            if (height != needed) { height = needed }
        }

        val list = orderedChildren()
        placeX(list)
        placeY(list)
    }

    // ── Horizontal placement ──────────────────────────────────────────────────

    private fun placeX(list: List<Actor>) {
        when (alignH) {

            AlignH.LEFT -> {
                var x = paddingLeft
                list.forEach { actor ->
                    actor.x = x
                    x       += actor.width + gap
                }
            }

            AlignH.RIGHT -> {
                var x = width - paddingRight
                list.forEach { actor ->
                    x       -= actor.width
                    actor.x  = x
                    x       -= gap
                }
            }

            AlignH.CENTER -> {
                val totalW = totalChildrenWidth()
                var x      = paddingLeft + (innerWidth - totalW) / 2f
                list.forEach { actor ->
                    actor.x = x
                    x       += actor.width + gap
                }
            }

            // Розмістить дітей по всій ширині
            AlignH.SPREAD -> {
                when {
                    list.isEmpty() -> return
                    list.size == 1 -> list[0].x = paddingLeft
                    else -> {
                        val totalW  = list.sumOf { it.width.toDouble() }.toFloat()
                        val autoGap = (innerWidth - totalW) / (list.size - 1)
                        var x       = paddingLeft
                        list.forEach { actor ->
                            actor.x = x
                            x       += actor.width + autoGap
                        }
                    }
                }
            }
        }
    }

    // ── Vertical placement (y кожної дитини) ─────────────────────────────────

    private fun placeY(list: List<Actor>) {
        list.forEach { actor ->
            actor.y = when (alignV) {
                AlignV.BOTTOM -> paddingBottom
                AlignV.CENTER -> paddingBottom + (innerHeight - actor.height) / 2f
                AlignV.TOP    -> height - paddingTop - actor.height
                AlignV.SPREAD -> paddingBottom
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun orderedChildren(): List<Actor> {
        val list = children.toList()
        return if (direction == DirectionH.RIGHT) list else list.reversed()
    }

    private fun totalChildrenWidth(): Float {
        if (children.isEmpty) return 0f
        val sum  = children.sumOf { it.width.toDouble() }.toFloat()
        val gaps = gap * (children.size - 1).coerceAtLeast(0)
        return sum + gaps
    }
}