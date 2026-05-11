package com.rbuxrds.counter.game.actors.layout.constraintLayout

import com.badlogic.gdx.scenes.scene2d.Actor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

// ═════════════════════════════════════════════════════════════════════════════
//  AConstraintLayout
//
//  ConstraintLayout для libGDX.
//  Підтримує: позиціонування, розміри (FIXED / MATCH_PARENT / PERCENT),
//  snapshot-detection (реагує на зміну розміру anchor-ів і самого layout).
//
//  ПОРЯДОК LAYOUT ДЛЯ КОЖНОГО АКТОРА:
//    1. Виставити розмір (dimension resolution)
//    2. Виставити позицію (constraint resolution)
//
//  ВАЖЛИВО: якщо B залежить від A → add(A) перед add(B).
// ═════════════════════════════════════════════════════════════════════════════

open class AConstraintLayout(override val screen: AdvancedScreen) : AdvancedGroup() {

    private val rules           = LinkedHashMap<Actor, CLParams>()
    private val snapshots       = HashMap<Actor, FloatArray>()
    private var snapshotEntries = emptyList<Pair<Actor, FloatArray>>()

    init { watch(this) }

    // ── Public API ────────────────────────────────────────────────────────────

    fun add(actor: Actor, block: CLParams.() -> Unit): Actor {
        val params = CLParams(this).apply(block)

        // Розмір потрібен тільки якщо FIXED — інакше він виставляється в layout()
        if (params.widthMode == Dimension.FIXED && params.heightMode == Dimension.FIXED) {
            require(actor.width > 0f || actor.height > 0f) {
                "AConstraintLayout.add(): встанови setSize() або використай fillParent()/fillWidth()/fillHeight().\n" +
                        "Actor: ${actor::class.simpleName}"
            }
        }

        rules[actor] = params
        addActor(actor)
        watch(actor)
        params.allAnchors().forEach { watch(it) }
        rebuildSnapshotEntries()

        // Виставляємо розмір і позицію одразу — без кадру затримки
        applyDimension(actor, params)
        applyPosition(actor, params)
        return actor
    }

    fun update(actor: Actor, block: CLParams.() -> Unit) {
        val params = rules[actor] ?: CLParams(this)
        params.apply(block)
        rules[actor] = params
        params.allAnchors().forEach { watch(it) }
        rebuildSnapshots()
        applyDimension(actor, params)
        applyPosition(actor, params)
    }

    fun detach(actor: Actor) {
        rules.remove(actor)
        rebuildSnapshots()
    }

    fun clearConstrained() {
        rules.clear()
        snapshots.clear()
        snapshotEntries = emptyList()
        watch(this)
        clearChildren()
    }

    // ── removeActor — каскадне очищення ──────────────────────────────────────

    override fun removeActor(actor: Actor): Boolean {
        if (isUsedAsAnchor(actor)) removeRulesWithAnchor(actor)
        rules.remove(actor)
        rebuildSnapshots()
        return super.removeActor(actor)
    }

    override fun addActorsOnGroup() {}

    // ── act(): snapshot detection ─────────────────────────────────────────────

    override fun act(delta: Float) {
        super.act(delta)
        if (rules.isEmpty()) return

        var dirty = false
        for ((actor, snap) in snapshotEntries) {
            if (snap[0] != actor.x     || snap[1] != actor.y ||
                snap[2] != actor.width || snap[3] != actor.height) {
                snap[0] = actor.x;     snap[1] = actor.y
                snap[2] = actor.width; snap[3] = actor.height
                dirty = true
            }
        }
        if (dirty) invalidate()
    }

    // ── layout(): розмір → позиція ────────────────────────────────────────────
    //
    // Порядок важливий:
    //   1. applyDimension — актор отримує правильний розмір
    //   2. applyPosition  — позиція рахується вже з правильним розміром

    override fun layout() {
        rules.forEach { (actor, params) ->
            applyDimension(actor, params)
            applyPosition(actor, params)
        }
    }

    // ── Dimension resolution ──────────────────────────────────────────────────
    //
    // MATCH_CONSTRAINT: розмір = відстань між двома anchor-ами.
    //
    //   matchHeight() + topToBottom(A) + bottomToBottom():
    //     top boundary  = edgeBottom(A) - marginTop
    //     bottom boundary = edgeBottom(layout) + marginBottom = 0 + marginBottom
    //     height = top boundary - bottom boundary
    //
    //   matchWidth() + startToStart() + endToEnd(margin=40):
    //     left boundary  = edgeLeft(layout) + marginStart = 0 + marginStart
    //     right boundary = edgeRight(layout) - marginEnd
    //     width = right boundary - left boundary
    //
    // ВАЖЛИВО: applyDimension викликається ДО applyPosition.
    //   Тому margin з constraint-ів враховуються і в розмірі і в позиції.
    //   Не треба дублювати margin — вони застосовуються один раз в applyDimension,
    //   а applyPosition просто виставляє x/y від anchor без margin (бо розмір вже вірний).

    private fun applyDimension(actor: Actor, p: CLParams) {
        val newW = when (p.widthMode) {
            Dimension.FIXED            -> actor.width
            Dimension.MATCH_PARENT     -> width
            Dimension.PERCENT          -> width * p.widthPercent
            Dimension.MATCH_CONSTRAINT -> resolveMatchWidth(p)
        }
        val newH = when (p.heightMode) {
            Dimension.FIXED            -> actor.height
            Dimension.MATCH_PARENT     -> height
            Dimension.PERCENT          -> height * p.heightPercent
            Dimension.MATCH_CONSTRAINT -> resolveMatchHeight(p)
        }
        if (newW != actor.width || newH != actor.height) {
            actor.setSize(newW, newH)
        }
    }

    // Ширина між startTo* і endTo* anchor-ами з урахуванням margin
    private fun resolveMatchWidth(p: CLParams): Float {
        val left = when {
            p.startToStartActor != null -> edgeLeft(p.startToStartActor!!)  + p.marginStart
            p.startToEndActor   != null -> edgeRight(p.startToEndActor!!)   + p.marginStart
            else                        -> p.marginStart
        }
        val right = when {
            p.endToEndActor   != null -> edgeRight(p.endToEndActor!!)  - p.marginEnd
            p.endToStartActor != null -> edgeLeft(p.endToStartActor!!) - p.marginEnd
            else                      -> width - p.marginEnd
        }
        return (right - left).coerceAtLeast(0f)
    }

    // Висота між topTo* і bottomTo* anchor-ами з урахуванням margin
    private fun resolveMatchHeight(p: CLParams): Float {
        val top = when {
            p.topToTopActor    != null -> edgeTop(p.topToTopActor!!)       - p.marginTop
            p.topToBottomActor != null -> edgeBottom(p.topToBottomActor!!) - p.marginTop
            else                       -> height - p.marginTop
        }
        val bottom = when {
            p.bottomToBottomActor != null -> edgeBottom(p.bottomToBottomActor!!) + p.marginBottom
            p.bottomToTopActor    != null -> edgeTop(p.bottomToTopActor!!)       + p.marginBottom
            else                          -> p.marginBottom
        }
        return (top - bottom).coerceAtLeast(0f)
    }

    // ── Position resolution ───────────────────────────────────────────────────

    private fun applyPosition(actor: Actor, p: CLParams) {
        actor.setPosition(resolveX(actor, p), resolveY(actor, p))
    }

    private fun resolveX(actor: Actor, p: CLParams): Float {
        val w = actor.width

        // MATCH_CONSTRAINT: x = left_boundary
        // Розмір вже = right_boundary - left_boundary, тому просто ставимо від лівого краю
        if (p.widthMode == Dimension.MATCH_CONSTRAINT) {
            return when {
                p.startToStartActor != null -> edgeLeft(p.startToStartActor!!)  + p.marginStart
                p.startToEndActor   != null -> edgeRight(p.startToEndActor!!)   + p.marginStart
                else                        -> p.marginStart
            }
        }

        // FIXED / MATCH_PARENT / PERCENT — стандартна логіка
        val startX: Float? = when {
            p.startToStartActor != null -> edgeLeft(p.startToStartActor!!)  + p.marginStart
            p.startToEndActor   != null -> edgeRight(p.startToEndActor!!)   + p.marginStart
            else -> null
        }
        val endX: Float? = when {
            p.endToEndActor   != null -> edgeRight(p.endToEndActor!!)  - w - p.marginEnd
            p.endToStartActor != null -> edgeLeft(p.endToStartActor!!) - w - p.marginEnd
            else -> null
        }
        return when {
            startX != null && endX != null -> startX + (endX - startX) * p.horizontalBias
            startX != null                 -> startX
            endX   != null                 -> endX
            else                           -> actor.x
        }
    }

    private fun resolveY(actor: Actor, p: CLParams): Float {
        val h = actor.height

        // MATCH_CONSTRAINT: y = bottom_boundary
        // Розмір вже = top_boundary - bottom_boundary, тому просто ставимо від нижнього краю
        if (p.heightMode == Dimension.MATCH_CONSTRAINT) {
            return when {
                p.bottomToBottomActor != null -> edgeBottom(p.bottomToBottomActor!!) + p.marginBottom
                p.bottomToTopActor    != null -> edgeTop(p.bottomToTopActor!!)       + p.marginBottom
                else                          -> p.marginBottom
            }
        }

        // FIXED / MATCH_PARENT / PERCENT — стандартна логіка
        val bottomY: Float? = when {
            p.bottomToBottomActor != null -> edgeBottom(p.bottomToBottomActor!!) + p.marginBottom
            p.bottomToTopActor    != null -> edgeTop(p.bottomToTopActor!!)       + p.marginBottom
            else -> null
        }
        val topY: Float? = when {
            p.topToTopActor    != null -> edgeTop(p.topToTopActor!!)       - h - p.marginTop
            p.topToBottomActor != null -> edgeBottom(p.topToBottomActor!!) - h - p.marginTop
            else -> null
        }
        return when {
            bottomY != null && topY != null -> bottomY + (topY - bottomY) * p.verticalBias
            bottomY != null                 -> bottomY
            topY    != null                 -> topY
            else                            -> actor.y
        }
    }

    // ── Edge helpers ──────────────────────────────────────────────────────────

    private fun edgeLeft(a: Actor)   = if (a === this) 0f     else a.x
    private fun edgeRight(a: Actor)  = if (a === this) width  else a.x + a.width
    private fun edgeBottom(a: Actor) = if (a === this) 0f     else a.y
    private fun edgeTop(a: Actor)    = if (a === this) height else a.y + a.height

    // ── Snapshot management ───────────────────────────────────────────────────

    private fun watch(actor: Actor) {
        snapshots.getOrPut(actor) {
            floatArrayOf(actor.x, actor.y, actor.width, actor.height)
        }
    }

    private fun rebuildSnapshots() {
        snapshots.clear()
        watch(this)
        rules.forEach { (actor, params) ->
            watch(actor)
            params.allAnchors().forEach { watch(it) }
        }
        rebuildSnapshotEntries()
    }

    private fun rebuildSnapshotEntries() {
        snapshotEntries = snapshots.entries.map { it.key to it.value }
    }

    // ── Anchor safety ─────────────────────────────────────────────────────────

    private fun isUsedAsAnchor(actor: Actor): Boolean =
        rules.values.any { actor in it.allAnchors() }

    private fun removeRulesWithAnchor(anchor: Actor) {
        rules.entries
            .filter { (_, p) -> anchor in p.allAnchors() }
            .map    { it.key }
            .forEach { rules.remove(it) }
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    override fun dispose() {
        rules.clear()
        snapshots.clear()
        snapshotEntries = emptyList()
        super.dispose()
    }
}