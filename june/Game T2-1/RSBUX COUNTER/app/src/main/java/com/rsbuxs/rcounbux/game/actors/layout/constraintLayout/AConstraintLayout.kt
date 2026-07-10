package com.rsbuxs.rcounbux.game.actors.layout.constraintLayout

import com.badlogic.gdx.scenes.scene2d.Actor
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedGroup
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen

// ═════════════════════════════════════════════════════════════════════════════
//  AConstraintLayout  —  оптимізована версія (push-модель + кешовані anchors)
//
//  ОПТИМІЗАЦІЇ:
//    1. anyDirty прапорець     — act() виходить за 1 перевірку якщо нічого не змінилось
//    2. Node.anchors кеш       — allAnchors() не створює новий List кожен виклик
//    3. anchorSnapshots        — відстежуємо тільки зовнішні anchor-и, не всіх акторів
//    4. hasExternalAnchors     — checkAnchors() пропускається якщо нема зовнішніх anchor-ів
//    5. markDependentsDirty    — dirty тільки залежні від зміненого anchor
//    6. isLayouting guard      — захист від рекурсії layout() → setSize → sizeChanged → layout()
//    7. node.dirty = false ДО resolveNode — якщо resolve знову помітить dirty, він піде в чергу
//    8. Нуль алокацій hot path — ArrayList reuse, removeNodesWithAnchor без filter/map
//
//  ПОРЯДОК LAYOUT ДЛЯ КОЖНОГО АКТОРА:
//    1. Виставити розмір (dimension resolution)
//    2. Виставити позицію (constraint resolution)
//
//  ВАЖЛИВО: якщо B залежить від A → add(A) перед add(B).
//  PUBLIC API — ідентичний попередній версії, клієнтський код не треба міняти.
// ═════════════════════════════════════════════════════════════════════════════

open class AConstraintLayout(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ── Внутрішній вузол ──────────────────────────────────────────────────────
    private class Node(val actor: Actor, val params: CLParams) {
        var dirty: Boolean = true

        // Кешований список anchor-ів — оновлюється тільки в add() і update()
        // В hot path (markDependentsDirty, isUsedAsAnchor) — нуль алокацій
        var anchors: List<Actor> = params.allAnchors()
            private set

        fun refreshAnchors() { anchors = params.allAnchors() }
    }

    // LinkedHashMap — зберігає порядок додавання (важливо: B залежить від A)
    private val nodes = LinkedHashMap<Actor, Node>()

    // Черга вузлів що потребують перерахунку
    private val dirtyQueue = ArrayDeque<Node>()

    // Швидкий прапорець — чи є взагалі що обробляти цього кадру
    private var anyDirty = false

    // Захист від рекурсії: layout() → resolveNode → setSize → sizeChanged → layout()
    private var isLayouting = false

    // Snapshot-и тільки для ЗОВНІШНІХ anchor-ів (не this)
    // layout (this) відстежується через sizeChanged() — без snapshot
    private val anchorSnapshots  = HashMap<Actor, FloatArray>()
    private val anchorActorsList = ArrayList<Actor>()   // для ітерації без алокацій
    private val anchorArraysList = ArrayList<FloatArray>()

    // Оптимізація: якщо нема зовнішніх anchor-ів — checkAnchors() не викликається взагалі
    private var hasExternalAnchors = false

    // ── Public API ────────────────────────────────────────────────────────────

    fun add(actor: Actor, block: CLParams.() -> Unit): Actor {
        val params = CLParams(this).apply(block)

        if (params.widthMode == Dimension.FIXED && params.heightMode == Dimension.FIXED) {
            require(actor.width > 0f || actor.height > 0f) {
                "AConstraintLayout.add(): встанови setSize() або використай fillParent()/fillWidth()/fillHeight().\n" +
                        "Actor: ${actor::class.simpleName}"
            }
        }

        val node = Node(actor, params)
        nodes[actor] = node

        // Реєструємо тільки зовнішні anchor-и (this відстежується через sizeChanged)
        node.anchors.forEach { anchor ->
            if (anchor !== this) watchAnchor(anchor)
        }

        addActor(actor)

        // Виставляємо розмір і позицію одразу — без кадру затримки
        resolveNode(node)
        node.dirty = false

        return actor
    }

    fun update(actor: Actor, block: CLParams.() -> Unit) {
        val node = nodes[actor] ?: return
        node.params.apply(block)
        node.refreshAnchors()
        node.anchors.forEach { anchor ->
            if (anchor !== this) watchAnchor(anchor)
        }
        resolveNode(node)
        node.dirty = false
    }

    fun detach(actor: Actor) {
        nodes.remove(actor)
        // Не викликаємо markDependentsDirty — actor вже видалений,
        // залежні від нього отримають некоректну позицію однаково.
        // Якщо потрібно — caller сам оновить залежних через update().
    }

    fun clearConstrained() {
        nodes.clear()
        dirtyQueue.clear()
        anyDirty = false
        anchorSnapshots.clear()
        anchorActorsList.clear()
        anchorArraysList.clear()
        hasExternalAnchors = false
        clearChildren()
    }

    override fun removeActor(actor: Actor): Boolean {
        if (isUsedAsAnchor(actor)) removeNodesWithAnchor(actor)
        nodes.remove(actor)
        return super.removeActor(actor)
    }

    override fun addActorsOnGroup() {}

    // ── sizeChanged: розмір layout змінився ──────────────────────────────────
    //
    // Викликається LibGDX коли змінюється width/height цієї групи.
    // Помічаємо всіх dirty — перерахуються в наступному act().
    // Не перераховуємо одразу щоб не конфліктувати з isLayouting guard.

    override fun sizeChanged() {
        super.sizeChanged()
        if (isLayouting) return // вже в процесі layout — не запускаємо знову
        if (nodes.isNotEmpty()) {
            nodes.values.forEach { scheduleDirty(it) }
        }
    }

    // ── act(): тільки dirty вузли ─────────────────────────────────────────────
    //
    // Типовий кадр для статичного UI:
    //   hasExternalAnchors = false → checkAnchors() пропускається
    //   anyDirty = false           → виходимо після 2 перевірок, 0 ітерацій
    //
    // Типовий кадр для динамічного UI (щось рухається):
    //   checkAnchors() знаходить змінений anchor
    //   markDependentsDirty() помічає тільки залежних (не всіх)
    //   while(dirtyQueue) обробляє тільки змінених

    override fun act(delta: Float) {
        super.act(delta)

        if (hasExternalAnchors) checkAnchors()

        if (!anyDirty) return

        isLayouting = true
        while (dirtyQueue.isNotEmpty()) {
            val node = dirtyQueue.removeFirst()
            if (!node.dirty) continue
            node.dirty = false      // скидаємо ДО resolveNode:
            resolveNode(node)       // якщо resolve викличе setSize → sizeChanged →
            // scheduleDirty → node.dirty = true знову → піде в чергу
        }
        isLayouting = false

        anyDirty = dirtyQueue.isNotEmpty()
    }

    // layout() — LibGDX може викликати після invalidate() або зміни розміру.
    // У новій версії основна логіка в act(), але override потрібен для сумісності.
    override fun layout() {
        if (isLayouting) return
        isLayouting = true
        nodes.values.forEach { node ->
            node.dirty = false
            resolveNode(node)
        }
        dirtyQueue.clear()
        anyDirty = false
        isLayouting = false
    }

    // ── Resolve: розмір → позиція ─────────────────────────────────────────────

    private fun resolveNode(node: Node) {
        applyDimension(node.actor, node.params)
        applyPosition(node.actor, node.params)
    }

    // ── Dimension resolution ──────────────────────────────────────────────────

    private fun applyDimension(actor: Actor, p: CLParams) {
        val newW = when (p.widthMode) {
            Dimension.FIXED            -> actor.width
            Dimension.MATCH_PARENT     -> width
            Dimension.PERCENT          -> width  * p.widthPercent
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

        if (p.widthMode == Dimension.MATCH_CONSTRAINT) {
            return when {
                p.startToStartActor != null -> edgeLeft(p.startToStartActor!!)  + p.marginStart
                p.startToEndActor   != null -> edgeRight(p.startToEndActor!!)   + p.marginStart
                else                        -> p.marginStart
            }
        }

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

        if (p.heightMode == Dimension.MATCH_CONSTRAINT) {
            return when {
                p.bottomToBottomActor != null -> edgeBottom(p.bottomToBottomActor!!) + p.marginBottom
                p.bottomToTopActor    != null -> edgeTop(p.bottomToTopActor!!)       + p.marginBottom
                else                          -> p.marginBottom
            }
        }

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

    // ── Anchor watching ───────────────────────────────────────────────────────

    private fun watchAnchor(anchor: Actor) {
        if (anchor in anchorSnapshots) return // вже відстежується
        val snap = floatArrayOf(anchor.x, anchor.y, anchor.width, anchor.height)
        anchorSnapshots[anchor] = snap
        anchorActorsList.add(anchor)
        anchorArraysList.add(snap)
        hasExternalAnchors = true
    }

    // Ітерує тільки зовнішні anchor-и — без алокацій (indices loop)
    private fun checkAnchors() {
        for (i in anchorActorsList.indices) {
            val anchor = anchorActorsList[i]
            val snap   = anchorArraysList[i]
            if (snap[0] != anchor.x     || snap[1] != anchor.y     ||
                snap[2] != anchor.width || snap[3] != anchor.height) {
                snap[0] = anchor.x;     snap[1] = anchor.y
                snap[2] = anchor.width; snap[3] = anchor.height
                markDependentsDirty(anchor)
            }
        }
    }

    // ── Dirty propagation ─────────────────────────────────────────────────────

    private fun markDependentsDirty(changedAnchor: Actor) {
        // node.anchors — кешований список, нуль алокацій
        nodes.values.forEach { node ->
            if (changedAnchor in node.anchors) scheduleDirty(node)
        }
    }

    private fun scheduleDirty(node: Node) {
        if (!node.dirty) {
            node.dirty = true
            dirtyQueue.addLast(node)
            anyDirty = true
        }
    }

    // ── Anchor safety ─────────────────────────────────────────────────────────

    // node.anchors — кешований список, нуль алокацій
    private fun isUsedAsAnchor(actor: Actor): Boolean =
        nodes.values.any { actor in it.anchors }

    // Без filter{}.map{}.forEach{} — одна ітерація, один тимчасовий список
    private fun removeNodesWithAnchor(anchor: Actor) {
        val toRemove = ArrayList<Actor>()
        nodes.forEach { (key, node) ->
            if (anchor in node.anchors) toRemove.add(key)
        }
        toRemove.forEach { nodes.remove(it) }
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    override fun dispose() {
        nodes.clear()
        dirtyQueue.clear()
        anchorSnapshots.clear()
        anchorActorsList.clear()
        anchorArraysList.clear()
        anyDirty = false
        hasExternalAnchors = false
        super.dispose()
    }
}