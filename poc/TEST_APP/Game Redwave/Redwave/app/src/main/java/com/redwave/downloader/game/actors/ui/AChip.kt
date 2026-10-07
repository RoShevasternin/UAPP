package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.font.msdf.MsdfStyle
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AChip — «пігулка». Розмір рахує сам (HUG) від тексту й відступів.
//   RED   — .chip-r: фон red 14 %, текст pink, Onest 700/10.5, іконка 13
//   GLASS — .chip-w: фон white 16 %, 700/10 uppercase
//   SRC   — .srcs span: рамка, фон white 4 %, muted 600/10.5, іконка 12
//   MOOD  — .mood: тоглиться; вимкнений — рамка, увімкнений — градієнт
//   GREEN — .chip-g: фон ok 14 %, текст ok, 700/11.5, іконка 14
//   KIND  — .chip-k: квадратніший (радіус 6), mono 700/10, white 16 %
// ─────────────────────────────────────────────────────────────────────────────
class AChip(
    screen: AdvancedScreen,
    text: String,
    val kind: Kind,
    iconRegion: TextureRegion? = null,
) : ATap(screen, 0.95f) {

    enum class Kind { RED, GLASS, SRC, MOOD, GREEN, KIND }

    private val style: MsdfStyle = when (kind) {
        Kind.RED   -> msdf.bold(10.5f, GameColor.pink_FF8A98)
        Kind.GLASS -> msdf.bold(10f, Color.WHITE).apply { letterSpacing = 6f }
        Kind.SRC   -> msdf.semibold(10.5f, GameColor.muted_A8949B)
        Kind.MOOD  -> msdf.bold(12.5f, GameColor.text_FBF1F2)
        Kind.GREEN -> msdf.bold(11.5f, GameColor.ok_3DDC97)
        Kind.KIND  -> msdf.monoSemi(10f, Color.WHITE)
    }
    private val label: AMsdfLabel = lbl(if (kind == Kind.GLASS) text.uppercase() else text, style)
    private val icSize = when (kind) { Kind.SRC -> px(12f); Kind.GREEN -> px(14f); else -> px(13f) }
    private val ic = iconRegion?.let { icon(it, icSize, style.color) }

    private val padX = when (kind) { Kind.MOOD -> px(14f); Kind.GLASS, Kind.SRC -> px(8f); Kind.KIND -> px(7f); Kind.GREEN -> px(10f); else -> px(9f) }
    private val padY = when (kind) { Kind.MOOD -> px(8f); Kind.KIND -> px(4f); Kind.GLASS, Kind.SRC -> px(5f); else -> px(6f) }
    private val gap = px(5f)

    private val bg = when (kind) {
        Kind.RED   -> ARect(screen, 999f, GameColor.red_14)
        Kind.GLASS -> ARect(screen, 999f, GameColor.white_16)
        Kind.SRC   -> ARect(screen, 999f, GameColor.white_4, stroke = GameColor.line_white_7)
        Kind.MOOD  -> ARect(screen, 999f, GameColor.white_4, stroke = GameColor.line_white_7)
        Kind.GREEN -> ARect(screen, 999f, GameColor.ok_14)
        Kind.KIND  -> ARect(screen, px(6f), GameColor.white_16)
    }

    var isOn = false
        set(v) {
            field = v
            if (kind == Kind.MOOD) {
                if (v) bg.fill(GameColor.gradTop_FF5468, GameColor.gradBot_E3002B).stroke(null)
                else bg.fill(GameColor.white_4).stroke(GameColor.line_white_7)
            }
        }

    init {
        val w = padX * 2 + label.width + (ic?.let { it.width + gap } ?: 0f)
        val h = padY * 2 + maxOf(label.height * 0.78f, icSize)
        setSize(w, h)
        if (kind != Kind.MOOD) touchable = com.badlogic.gdx.scenes.scene2d.Touchable.childrenOnly
    }

    override fun addContent() {
        addAndFillActor(bg)
        var x = padX
        ic?.let { addActor(it); it.setPosition(x, (height - it.height) / 2f); x += it.width + gap }
        addActor(label)
        label.setPosition(x, (height - label.height) / 2f)
    }
}
