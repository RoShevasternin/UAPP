package com.vidtools.framefall.game.screens.sheets

import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.model.MediaKind
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.game.actors.ui.ARect
import com.vidtools.framefall.game.actors.ui.ASheet
import com.vidtools.framefall.game.actors.ui.AVideoThumb
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.ellipsize
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

/** ⋮ на відео: шапка (мініатюра + назва + інфо), шлях файлу, дії: Play, Studio, фон Home, Share, Delete. */
class VideoMenuSheet(
    screen: AdvancedScreen,
    private val video: Video,
    private val onDeleted: () -> Unit = {},
) : ASheet(screen) {

    private val optH = px(46f)
    private val isVideo = video.kind == MediaKind.VIDEO
    private val opts get() = if (isVideo) 5 else 3
    override val contentHeight: Float get() = px(54f) + px(10f) + px(30f) + px(12f) + opts * optH + (opts - 1) * px(4f)

    override fun buildContent(w: Float) {
        var y = contentHeight
        val th = AVideoThumb(screen, px(10f), showProgress = false).apply { setBounds(0f, y - px(54f), px(96f), px(54f)) }
        th.setVideo(video); add(th)
        val tx = px(96f + 12f)
        val t = lbl(video.title, msdf.bold(15f)).ellipsize(w - tx)
        val s = lbl(listOf(video.credit, videoInfo(video)).joinToString(" · "), msdf.regular(12f, GameColor.muted_B3A293)).ellipsize(w - tx)
        t.setPosition(tx, y - px(27f) + px(1f)); s.setPosition(tx, y - px(27f) - s.height - px(1f))
        add(t); add(s)
        y -= px(54f) + px(10f)

        // Де лежить файл — щоб було видно, що саме видаляємо
        val path = video.localUri?.substringAfter("/storage/emulated/0/")?.substringAfter("file://")?.let { java.net.URLDecoder.decode(it, "UTF-8") } ?: "—"
        add(ARect(screen, px(10f), GameColor.white_4).apply { setBounds(0f, y - px(30f), w, px(30f)) })
        add(icon(assets.ic_folder, px(14f), GameColor.muted_B3A293).apply { setPosition(px(10f), y - px(15f) - px(7f)) })
        val pl = lbl(path, msdf.mono(11f, GameColor.muted_B3A293)).ellipsize(w - px(34f))
        pl.setPosition(px(30f), y - px(15f) - pl.height / 2f); add(pl)
        y -= px(30f) + px(12f)

        val rows = buildList {
            add(optionRow(screen, assets.ic_play_fill, Copy.Menu.PLAY) { close(); gdxGame.openVideo(video) })
            if (isVideo) {
                add(optionRow(screen, assets.ic_wand, Copy.Menu.STUDIO) { close(); gdxGame.openStudio("trim", video) })
                add(optionRow(screen, assets.ic_home, Copy.Menu.HOME_BG) { close(); gdxGame.openStudio("loop", video) })
            }
            add(optionRow(screen, assets.ic_share, Copy.Menu.SHARE) { gdxGame.bridge.shareVideo(video); close() })
            add(optionRow(screen, assets.ic_trash, Copy.Menu.DELETE, danger = true) { delete() })
        }
        rows.forEach { r -> y -= optH; r.setBounds(0f, y, w, optH); add(r); y -= px(4f) }
    }

    /** Файл видаляється з диска; з бібліотеки — лише якщо файла справді більше немає. */
    private fun delete() {
        val m = gdxGame.model
        close()
        gdxGame.bridge.deleteVideo(video) { ok ->
            if (!ok && gdxGame.bridge.fileExists(video.localUri)) { gdxGame.toast("Couldn’t delete the file · ${video.title}"); return@deleteVideo }
            m.update { s -> s.copy(library = s.library.filterNot { it.id == video.id }, studioVideoId = s.studioVideoId.takeIf { it != video.id }) }
            gdxGame.covers.invalidateVideo(video)
            gdxGame.toast(Copy.Menu.DELETED)
            onDeleted()
        }
    }
}
