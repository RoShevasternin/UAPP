package com.rbuxrds.counter.game.actors.layout.linear

import com.badlogic.gdx.scenes.scene2d.Actor
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.layout.DirectionV
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

// ═════════════════════════════════════════════════════════════════════════════
//  AVerticalGroup
//
//  Розташовує дітей вертикально. Повністю динамічна.
//
//  COORDINATE SYSTEM (libGDX y=0 знизу):
//
//    ┌──────────────────────┐  ← y = height
//    │     paddingTop       │
//    │  ┌────────────────┐  │
//    │  │    child[0]    │  │  ← перший при DOWN
//    │  └────────────────┘  │
//    │        gap           │
//    │  ┌────────────────┐  │
//    │  │    child[1]    │  │
//    │  └────────────────┘  │
//    │     paddingBottom    │
//    └──────────────────────┘  ← y = 0
//
//  ПАРАМЕТРИ:
//    gap       — відступ між дітьми
//    alignV    — розміщення вздовж вертикальної осі (TOP/BOTTOM/CENTER/SPREAD)
//    alignH    — вирівнювання кожної дитини по горизонталі
//    direction — DOWN (перший зверху) або UP (перший знизу)
//    wrap      — true: висота групи підлаштовується під дітей
//
//  ВСІ ПАРАМЕТРИ ЗМІНЮВАНІ В RUNTIME:
//    group.gap = 40f          → одразу перерахує наступний кадр
//    group.alignH = AlignH.CENTER
//
//  ДИНАМІКА:
//    Дитина змінює розмір через Actions або напряму →
//    snapshot підхопить → layout() перерахує всі позиції ✓
//
//  ВИКОРИСТАННЯ:
//    val group = AVerticalGroup(screen, gap = 20f, alignH = AlignH.CENTER, wrap = true)
//    group.setSize(600f, 0f)   // при wrap=true height виставиться сам
//    addActor(group)
//    group.addActor(aBtn1)
//    group.addActor(aBtn2)     // стандартний addActor — нічого особливого
// ═════════════════════════════════════════════════════════════════════════════

open class AVerticalGroup(
    screen        : AdvancedScreen,
    gap           : Float      = 0f,
    alignV        : AlignV     = AlignV.TOP,
    alignH        : AlignH     = AlignH.LEFT,
    direction     : DirectionV = DirectionV.DOWN,
    wrap          : Boolean    = false,
    wrapMinHeight : Float      = 0f,
) : ALinearGroup(screen) {

    var gap           = gap;           set(v) { field = v; relayout() }
    var alignV        = alignV;        set(v) { field = v; relayout() }
    var alignH        = alignH;        set(v) { field = v; relayout() }
    var direction     = direction;     set(v) { field = v; relayout() }
    var wrap          = wrap;          set(v) { field = v; relayout() }
    var wrapMinHeight = wrapMinHeight; set(v) { field = v; relayout() }

    // ── getPrefWidth / getPrefHeight — для ScrollPane ─────────────────────────
    // ScrollPane визначає розмір контенту через ці методи, а не через width/height.
    // Без них ScrollPane думає що контент має розмір 0 і скрол не працює.
    override fun getPrefWidth()  = width
    override fun getPrefHeight() = height

    // ── layout() ─────────────────────────────────────────────────────────────

    override fun layout() {
        if (children.isEmpty) return

        // Wrap: виставляємо висоту групи під дітей
        // Важливо: НЕ робимо return після зміни height —
        // розміщуємо дітей одразу з новою висотою в тому ж кадрі.
        // Без цього діти один кадр на старих позиціях → дрижання.
        if (wrap) {
            val needed = (totalChildrenHeight() + paddingTop + paddingBottom).coerceAtLeast(wrapMinHeight)  // ← не менше мінімуму
            if (height != needed) height = needed
        }

        val list = orderedChildren()
        placeY(list)
        placeX(list)
    }

    // ── Vertical placement ────────────────────────────────────────────────────

    private fun placeY(list: List<Actor>) {
        when (alignV) {

            AlignV.TOP -> {
                // Стартуємо від верху, йдемо вниз
                var y = height - paddingTop
                list.forEach { actor ->
                    y    -= actor.height
                    actor.y = y
                    y    -= gap
                }
            }

            AlignV.BOTTOM -> {
                // Стартуємо від низу, йдемо вгору
                var y = paddingBottom
                list.forEach { actor ->
                    actor.y = y
                    y       += actor.height + gap
                }
            }

            AlignV.CENTER -> {
                val totalH = totalChildrenHeight()
                // Центруємо блок дітей по вертикалі
                // y стартової дитини (знизу блоку)
                var y = (height - totalH) / 2f
                // Розміщуємо знизу вгору щоб перший у list був зверху
                list.reversed().forEach { actor ->
                    actor.y = y
                    y       += actor.height + gap
                }
            }

            // Розмістить дітей по всій висоті
            AlignV.SPREAD -> {
                when {
                    list.isEmpty() -> return
                    list.size == 1 -> list[0].y = height - paddingTop - list[0].height
                    else -> {
                        val totalH   = list.sumOf { it.height.toDouble() }.toFloat()
                        val autoGap  = (innerHeight - totalH) / (list.size - 1)
                        // Від низу вгору
                        var y = paddingBottom
                        list.reversed().forEach { actor ->
                            actor.y = y
                            y       += actor.height + autoGap
                        }
                    }
                }
            }
        }
    }

    // ── Horizontal placement (x кожної дитини) ────────────────────────────────

    private fun placeX(list: List<Actor>) {
        list.forEach { actor ->
            actor.x = when (alignH) {
                AlignH.LEFT   -> paddingLeft
                AlignH.CENTER -> paddingLeft + (innerWidth - actor.width) / 2f
                AlignH.RIGHT  -> width - paddingRight - actor.width
                AlignH.SPREAD -> paddingLeft
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun orderedChildren(): List<Actor> {
        val list = children.toList()
        return if (direction == DirectionV.DOWN) list else list.reversed()
    }

    private fun totalChildrenHeight(): Float {
        if (children.isEmpty) return 0f
        val sum  = children.sumOf { it.height.toDouble() }.toFloat()
        val gaps = gap * (children.size - 1).coerceAtLeast(0)
        return sum + gaps
    }
}