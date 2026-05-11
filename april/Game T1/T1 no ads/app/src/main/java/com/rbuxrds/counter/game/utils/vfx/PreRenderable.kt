package com.rbuxrds.counter.game.utils.vfx

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group

interface PreRenderable {
    fun preRender(batch: Batch, parentAlpha: Float)
}

/** -------------------------------------------------------------------------
// Рекурсивно обходить дерево акторів і викликає preRender() для всіх PreRenderable.
// Ключова логіка:
// - Якщо актор є [PreRenderable] → викликаємо preRender() і НЕ йдемо глибше вручну,
//   бо сам preRender() через [com.rbuxrds.counter.game.utils.advanced.preRenderGroup.PreRenderableGroup.preRenderChildren] рекурсивно обробить своїх дітей.
// - Якщо актор — звичайна Group → йдемо вглиб, шукаємо PreRenderable.
// - Невидимих акторів пропускаємо.
// ------------------------------------------------------------------------- */

fun renderPreRenderables(actor: Actor, batch: Batch, parentAlpha: Float) {
    if (actor.isVisible.not()) return

    val currentAlpha = actor.color.a * parentAlpha

    when (actor) {
        is PreRenderable -> actor.preRender(batch, currentAlpha)
        is Group         -> actor.children.forEach { child -> renderPreRenderables(child, batch, currentAlpha) }
    }

}