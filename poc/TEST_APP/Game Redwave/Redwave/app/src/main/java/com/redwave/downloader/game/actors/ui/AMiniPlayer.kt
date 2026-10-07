package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

/**
 * .mini: 56 заввишки, радіус 16, градієнт 90° #3D0D18 → #1F1317, рамка redHi 20 %,
 * обкладинка 40, кнопка 36 біла (іконка ink), лінія прогресу 2 px знизу.
 */
class AMiniPlayer(screen: AdvancedScreen, private val onOpen: () -> Unit) : ATap(screen, 0.985f) {

    private val cover = ACover(screen, px(10f))
    private lateinit var title: AMsdfLabel
    private lateinit var artist: AMsdfLabel
    private lateinit var btnIcon: Image
    private val prog = ARect(screen, 0f, GameColor.red_FF2E4D)
    private var trackId: String? = null
    private var playing: Boolean? = null

    override fun addContent() {
        addAndFillActor(ARect(screen, px(16f), GameColor.miniA_3D0D18, GameColor.miniB_1F1317, angleCss = 90f, stroke = GameColor.redHi_25.cpy().apply { a = 0.2f }))
        cover.setBounds(px(8f), (height - px(40f)) / 2f + px(1f), px(40f), px(40f)); addActor(cover)
        val mx = px(8f + 40f + 10f)
        val maxW = width - mx - px(36f + 8f + 10f)
        title = lbl("", msdf.bold(13f)).ellipsize(maxW)
        artist = lbl("", msdf.regular(11.5f, GameColor.muted_A8949B)).ellipsize(maxW)
        title.setPosition(mx, height / 2f + px(1f)); artist.setPosition(mx, height / 2f - artist.height + px(1f))
        addActor(title); addActor(artist)

        val btn = object : ATap(screen, 0.9f) {
            override fun addContent() {
                addAndFillActor(ARect(screen, 999f, Color.WHITE))
                btnIcon = icon(assets.ic_play_fill, px(15f), GameColor.miniInk_14080B)
                addActor(btnIcon); btnIcon.setPosition((width - btnIcon.width) / 2f, (height - btnIcon.height) / 2f)
            }
        }.onClick { gdxGame.player.toggle() }
        btn.setBounds(width - px(8f) - px(36f), (height - px(36f)) / 2f + px(1f), px(36f), px(36f))
        addActor(btn)

        addActor(prog)
        onClick { onOpen() }
        refresh()
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (::title.isInitialized) refresh()
    }

    private fun refresh() {
        val p = gdxGame.player
        val t: Track? = p.now
        if (t?.id != trackId) {
            trackId = t?.id
            cover.setTrack(t)
            title.setText(t?.title ?: ""); artist.setText(t?.artist ?: "")
        }
        if (p.isPlaying != playing) {
            playing = p.isPlaying
            btnIcon.drawable = TextureRegionDrawable(if (p.isPlaying) assets.ic_pause_fill else assets.ic_play_fill)
        }
        val inset = px(10f)
        prog.setBounds(inset, px(1f), (width - inset * 2) * p.progress, px(2f))
    }
}
