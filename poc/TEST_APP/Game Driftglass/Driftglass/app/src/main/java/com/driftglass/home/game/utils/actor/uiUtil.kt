package com.driftglass.home.game.utils.actor

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.driftglass.home.game.actors.label.AMsdfLabel
import com.driftglass.home.game.utils.font.msdf.MsdfStyle

// ─────────────────────────────────────────────────────────────────────────────
// Дрібні фабрики UI: лейбл за розміром тексту, іконка з атласу, «зверху вниз».
// ─────────────────────────────────────────────────────────────────────────────

/** Лейбл розміром рівно під текст (pack). */
fun lbl(text: CharSequence, style: MsdfStyle, align: Int = Align.left): AMsdfLabel =
    AMsdfLabel(safeText(text), style).apply {
        setAlignment(align)
        touchable = Touchable.disabled
        pack()
    }

/**
 * Символи, яких немає в Onest/JetBrains Mono (assets/fonts/README.md → «Покриття»):
 * нерозривний дефіс U+2011 є лише в Archivo — у решті шрифтів гліф просто зникне.
 */
fun safeText(text: CharSequence): String = text.toString().replace('\u2011', '-')

/** Однорядковий лейбл фіксованої ширини з «…» (text-overflow: ellipsis). */
fun AMsdfLabel.ellipsize(maxWidth: Float): AMsdfLabel {
    setEllipsis(true)
    width = maxWidth
    return this
}

/** Біла іконка з атласу, тонована кольором (як currentColor у SVG прототипу). */
fun icon(region: TextureRegion, size: Float, color: Color = Color.WHITE): Image =
    Image(region).apply {
        setScaling(Scaling.fit)
        setSize(size, size)
        this.color.set(color)
        touchable = Touchable.disabled
    }

/** y для актора, що має стояти [top] від верху батька висотою [parentH]. */
fun Actor.setTop(parentH: Float, top: Float) { y = parentH - top - height }

/** Центр актора по вертикалі на лінії [cy]. */
fun Actor.centerY(cy: Float) { y = cy - height / 2f }
fun Actor.centerX(cx: Float) { x = cx - width / 2f }
