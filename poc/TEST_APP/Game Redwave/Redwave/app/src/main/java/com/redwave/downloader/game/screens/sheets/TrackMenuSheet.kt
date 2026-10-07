package com.redwave.downloader.game.screens.sheets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.game.actors.ui.ACover
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASheet
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.utils.Fmt
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

/** ⋮ на треку: шапка (обкладинка + назва) і дії .opt: Play, Make ringtone, Share, Delete. */
class TrackMenuSheet(
    screen: AdvancedScreen,
    private val track: Track,
    private val onRingtone: (Track) -> Unit,
) : ASheet(screen) {

    private val optH = px(46f)
    override val contentHeight: Float get() = px(52f) + px(12f) + 4 * optH + 3 * px(4f)

    override fun buildContent(w: Float) {
        var y = contentHeight
        val c = ACover(screen, px(12f)).apply { setBounds(0f, y - px(52f), px(52f), px(52f)) }
        c.setTrack(track); add(c)
        val t = lbl(track.title, msdf.bold(15f)).ellipsize(w - px(64f))
        val s = lbl("${track.artist} · ${Fmt.time(track.durationMs)} · ${track.format} · ${Fmt.size(track.sizeBytes)}", msdf.regular(12f, GameColor.muted_A8949B)).ellipsize(w - px(64f))
        t.setPosition(px(64f), y - px(26f) + px(1f)); s.setPosition(px(64f), y - px(26f) - s.height - px(1f))
        add(t); add(s)
        y -= px(52f) + px(12f)

        val opts: List<Triple<TextureRegion, String, () -> Unit>> = listOf(
            Triple(assets.ic_play_fill, "Play") { gdxGame.player.play(track); close() },
            Triple(assets.ic_scissors, "Make ringtone") { close(); onRingtone(track) },
            Triple(assets.ic_share, "Share") { gdxGame.bridge.shareTrack(track); close() },
            Triple(assets.ic_x, "Delete from library") { delete() },
        )
        opts.forEachIndexed { i, (ic, label, act) ->
            y -= optH
            val danger = i == opts.lastIndex
            val opt = object : ATap(screen, 0.98f) {
                override fun addContent() {
                    addAndFillActor(ARect(screen, px(14f), GameColor.white_4))
                    val col = if (danger) GameColor.pink_FF8A98 else Color.WHITE
                    val im = icon(ic, px(18f), col); im.setPosition(px(12f), (height - im.height) / 2f)
                    val l = lbl(label, msdf.semibold(14f, col)); l.setPosition(px(42f), (height - l.height) / 2f)
                    addActor(im); addActor(l)
                }
            }.onClick(act)
            opt.setBounds(0f, y, w, optH); add(opt)
            y -= px(4f)
        }
    }

    private fun delete() {
        val m = gdxGame.model
        if (gdxGame.player.snap.trackId == track.id) gdxGame.bridge.pause()
        gdxGame.bridge.deleteTrack(track) { }
        m.update { s -> s.copy(library = s.library.filterNot { it.id == track.id }, favorites = s.favorites - track.id) }
        gdxGame.covers.invalidateTrack(track.id)
        gdxGame.toast("Deleted · ${track.title}")
        close()
    }
}
