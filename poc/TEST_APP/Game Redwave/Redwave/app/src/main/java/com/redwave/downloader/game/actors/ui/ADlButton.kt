package com.redwave.downloader.game.actors.ui

import com.redwave.downloader.game.controller.DiscoverController.ItemState
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// ADlButton — .dlb: ⬇ (рамка redHi 35 %, фон red 10 %) → кільце прогресу →
// ✓ (зелена; тап — грати). Стан опитує сам щокадру через [state].
// ─────────────────────────────────────────────────────────────────────────────
class ADlButton(
    screen: AdvancedScreen,
    private val state: () -> ItemState,
    private val onDownload: () -> Unit,
    private val onPlay: (trackId: String) -> Unit,
) : ATap(screen, 0.9f) {

    private val bgIdle = ARect(screen, 999f, GameColor.red_10, stroke = GameColor.redHi_35)
    private val bgDone = ARect(screen, 999f, GameColor.ok_12, stroke = GameColor.ok_35)
    private val icDl   = icon(assets.ic_download, px(17f), GameColor.pink_FF8A98)
    private val icOk   = icon(assets.ic_check, px(17f), GameColor.ok_3DDC97)
    private val ring   = AProgressRing(screen, px(3f), GameColor.white_10, GameColor.red_FF2E4D)
    private var last: ItemState? = null

    init { setSize(px(38f), px(38f)) }

    override fun addContent() {
        addAndFillActors(bgIdle, bgDone)
        addActor(ring); ring.setBounds(0f, 0f, width, height)
        listOf(icDl, icOk).forEach { addActor(it); it.setPosition((width - it.width) / 2f, (height - it.height) / 2f) }
        onClick {
            when (val s = state()) {
                ItemState.None -> onDownload()
                is ItemState.Done -> onPlay(s.trackId)
                else -> {}
            }
        }
        refresh()
    }

    override fun act(delta: Float) {
        super.act(delta)
        refresh()
    }

    private fun refresh() {
        val s = state()
        if (s is ItemState.Running) ring.progress = s.progress
        if (last != null && s::class == last!!::class) return
        last = s
        bgIdle.isVisible = s is ItemState.None; icDl.isVisible = s is ItemState.None
        bgDone.isVisible = s is ItemState.Done; icOk.isVisible = s is ItemState.Done
        ring.isVisible = s is ItemState.Running
    }
}
