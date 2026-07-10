package com.rbuxrds.counter.game.actors.layout.linear

import com.badlogic.gdx.scenes.scene2d.Actor
import com.rbuxrds.counter.game.actors.AScrollPane
import com.rbuxrds.counter.game.actors.layout.AlignH
import com.rbuxrds.counter.game.actors.layout.AlignV
import com.rbuxrds.counter.game.actors.layout.DirectionH
import com.rbuxrds.counter.game.actors.layout.DirectionV
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

// ═════════════════════════════════════════════════════════════════════════════
//  ATableGroup
//
//  Динамічна таблиця/сітка. Повністю синхронна — не потребує корутин.
//
//  АРХІТЕКТУРА:
//    ATableGroup
//    └── AVerticalGroup (rows)           ← wrap=true, росте під рядки
//        ├── AHorizontalGroup (row 0)    ← wrap=true по висоті
//        │   ├── Actor
//        │   └── Actor
//        ├── AHorizontalGroup (row 1)
//        │   └── Actor
//        └── ...
//
//  ЯК ПРАЦЮЄ add(actor):
//    1. Виміряти чи вміститься актор у поточний рядок
//    2. Якщо так — додати
//    3. Якщо ні (або рядок вже повний по columns) — створити новий рядок
//    4. Snapshot-система в AHorizontalGroup автоматично перерахує layout
//       якщо якийсь актор зміниться в розмірі
//
//  ЯК ПРАЦЮЄ remove(actor):
//    1. Знайти рядок через actorRow map
//    2. Видалити з рядка
//    3. Якщо рядок порожній — видалити рядок
//    4. НЕ переносить акторів між рядками (gap залишається)
//       Якщо треба перепакувати — використовуй repack()
//
//  ПАРАМЕТРИ:
//    columns   — 0: авто по ширині;  N: фіксована кількість колонок у рядку
//    cellW/H   — 0f: розмір актора;  >0: override розміру
//    gapH/V    — горизонтальний і вертикальний gap
//    paddingH/V— зовнішній padding
//    alignH    — вирівнювання в рядку
//    alignV    — вирівнювання рядків
//    scrollable— вертикальний scroll
//
//  ВИКОРИСТАННЯ:
//    val table = ATableGroup(screen, columns = 4, cellW = 400f, cellH = 400f, gapH = 20f, gapV = 20f)
//    table.setSize(2000f, 3000f)
//    addActor(table)
//
//    table.add(aCube)          // синхронно — без корутин
//    table.remove(aCube)       // видалення
//    table.repack()            // перепакувати всі актори в нові рядки (після видалень)
//    table.clear()             // очистити все
// ═════════════════════════════════════════════════════════════════════════════

class ATableGroup(
    override val screen: AdvancedScreen,

    val columns   : Int     = 0,     // 0 = авто по ширині
    val cellW     : Float   = 0f,    // 0 = розмір актора
    val cellH     : Float   = 0f,

    val gapH      : Float   = 0f,
    val gapV      : Float   = 0f,

    val paddingH  : Float   = 0f,
    val paddingV  : Float   = 0f,

    val alignH    : AlignH = AlignH.LEFT,
    val alignV    : AlignV = AlignV.TOP,

    val scrollable: Boolean = false,

    ) : AdvancedGroup() {

    // ── Internal ──────────────────────────────────────────────────────────────

    private val vGroup = AVerticalGroup(
        screen    = screen,
        gap       = gapV,
        alignV    = alignV,
        alignH    = AlignH.LEFT,
        direction = DirectionV.DOWN,
        wrap      = true,
    ).also {
        it.paddingTop    = paddingV
        it.paddingBottom = paddingV
    }

    private val scrollPane = if (scrollable) AScrollPane(vGroup) else null

    // actor → рядок де він знаходиться. O(1) lookup для remove.
    private val actorRow = LinkedHashMap<Actor, AHorizontalGroup>()

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun addActorsOnGroup() {
        vGroup.setSize(width, height)
        if (scrollPane != null) addAndFillActor(scrollPane)
        else                    addAndFillActor(vGroup)
        ensureRow()   // перший порожній рядок
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Додає актора в таблицю. Повністю синхронно.
     * Автоматично переходить на наступний рядок якщо поточний заповнений.
     */
    fun add(actor: Actor) {
        applyCellSize(actor)

        if (needsNewRow(actor)) ensureRow(forceNew = true)

        currentRow().addActor(actor)
        actorRow[actor] = currentRow()
    }

    /**
     * Видаляє актора з таблиці.
     * Якщо рядок став порожнім — видаляє і рядок (крім останнього).
     * Не dispose-ить актора.
     */
    fun remove(actor: Actor) {
        val row = actorRow.remove(actor) ?: return
        safeRemoveFromRow(row, actor)
        if (row.children.isEmpty && vGroup.children.size > 1) {
            vGroup.removeActor(row)
        }
    }

    /**
     * Перепаковує всіх акторів заново — корисно після серії remove().
     * Усі актори розміщуються рівномірно без "дірок".
     */
    fun repack() {
        val all = actorRow.keys.toList()
        clearInternal()
        all.forEach { add(it) }
    }

    /**
     * Повністю очищає таблицю.
     * Dispose дітей якщо вони AdvancedGroup (стандартна поведінка clearChildren).
     */
    fun clearTable() {
        super.clear()
        clearInternal()
    }

    /**
     * Всі актори у порядку додавання.
     */
    fun actors(): List<Actor> = actorRow.keys.toList()

    /**
     * Кількість акторів у таблиці.
     */
    val size: Int get() = actorRow.size

    // ── Row logic ─────────────────────────────────────────────────────────────

    private fun needsNewRow(actor: Actor): Boolean {
        val row = currentRow()

        // Режим фіксованих колонок
        if (columns > 0) return row.children.size >= columns

        // Режим авто-ширини: перевіряємо чи вміститься актор
        val actorW    = if (cellW > 0f) cellW else actor.width
        val usedWidth = row.children.sumOf { it.width.toDouble() }.toFloat() +
                gapH * row.children.size   // gap після кожного вже доданого
        val available = vGroup.width - paddingH * 2 - usedWidth

        return actorW > available
    }

    private fun ensureRow(forceNew: Boolean = false): AHorizontalGroup {
        val last = vGroup.children.lastOrNull() as? AHorizontalGroup
        if (last != null && !forceNew) return last

        val row = AHorizontalGroup(
            screen     = screen,
            gap        = gapH,
            alignH     = alignH,
            alignV     = AlignV.CENTER,
            direction  = DirectionH.RIGHT,
            wrapHeight = true,
        ).also {
            it.paddingLeft  = paddingH
            it.paddingRight = paddingH
            it.width        = vGroup.width
        }

        vGroup.addActor(row)
        return row
    }

    private fun currentRow(): AHorizontalGroup {
        val last = vGroup.children.lastOrNull() as? AHorizontalGroup
        return last ?: ensureRow()
    }

    private fun clearInternal() {
        actorRow.clear()
        vGroup.clearChildren()
        ensureRow()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun applyCellSize(actor: Actor) {
        if (cellW > 0f) actor.width  = cellW
        if (cellH > 0f) actor.height = cellH
    }

    /** Видаляє актора з рядка без dispose */
    private fun safeRemoveFromRow(row: AHorizontalGroup, actor: Actor) {
        val wasDispose = (actor as? AdvancedGroup)?.isDisposeOnRemove
        (actor as? AdvancedGroup)?.isDisposeOnRemove = false
        row.removeActor(actor)
        wasDispose?.let { actor.isDisposeOnRemove = it }
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    override fun dispose() {
        actorRow.clear()
        super.dispose()
    }
}