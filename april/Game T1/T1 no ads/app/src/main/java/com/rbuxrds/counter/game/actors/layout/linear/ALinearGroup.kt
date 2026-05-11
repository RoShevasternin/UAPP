package com.rbuxrds.counter.game.actors.layout.linear

import com.badlogic.gdx.scenes.scene2d.Actor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

// ═════════════════════════════════════════════════════════════════════════════
//  ALinearGroup — спільна база для AVerticalGroup і AHorizontalGroup
//
//  Відповідає за:
//    • Snapshot-система: act() відстежує w/h кожної дитини
//      Якщо щось змінилось → invalidate() → layout() перед draw
//    • childrenChanged() / sizeChanged() → invalidate()
//    • Padding API
//    • relayout() — форсований перерахунок
// ═════════════════════════════════════════════════════════════════════════════

abstract class ALinearGroup(
    override val screen: AdvancedScreen
) : AdvancedGroup() {

    // ── Padding ───────────────────────────────────────────────────────────────

    var paddingTop    = 0f; set(v) { field = v; relayout() }
    var paddingBottom = 0f; set(v) { field = v; relayout() }
    var paddingLeft   = 0f; set(v) { field = v; relayout() }
    var paddingRight  = 0f; set(v) { field = v; relayout() }

    fun setPadding(all: Float)                      { paddingTop = all;      paddingBottom = all;        paddingLeft = all;        paddingRight = all }
    fun setPadding(vertical: Float, horizontal: Float) { paddingTop = vertical; paddingBottom = vertical;  paddingLeft = horizontal; paddingRight = horizontal }

    protected val innerWidth  get() = (width  - paddingLeft  - paddingRight).coerceAtLeast(0f)
    protected val innerHeight get() = (height - paddingTop   - paddingBottom).coerceAtLeast(0f)

    // ── Snapshot ──────────────────────────────────────────────────────────────
    // Відстежуємо тільки w/h дітей — позицію ми самі виставляємо в layout()

    private val snapshots       = HashMap<Actor, FloatArray>()
    private var snapshotEntries = emptyList<Pair<Actor, FloatArray>>()

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun addActorsOnGroup() {}

    override fun childrenChanged() {
        super.childrenChanged()
        rebuildSnapshots()
        relayout()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        relayout()
    }

    // ── act(): snapshot detection ─────────────────────────────────────────────
    // N * 2 Float порівнянь за кадр — мікросекунди

    override fun act(delta: Float) {
        super.act(delta)
        if (snapshotEntries.isEmpty()) return

        var dirty = false
        for ((actor, snap) in snapshotEntries) {
            if (snap[0] != actor.width || snap[1] != actor.height) {
                snap[0] = actor.width
                snap[1] = actor.height
                dirty   = true
            }
        }
        if (dirty) invalidate()
    }

    // ── Public ────────────────────────────────────────────────────────────────

    fun relayout() { invalidate() }

    // ── Snapshot management ───────────────────────────────────────────────────

    private fun rebuildSnapshots() {
        snapshots.clear()
        children.forEach { snapshots[it] = floatArrayOf(it.width, it.height) }
        snapshotEntries = snapshots.entries.map { it.key to it.value }
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    override fun dispose() {
        snapshots.clear()
        snapshotEntries = emptyList()
        super.dispose()
    }
}