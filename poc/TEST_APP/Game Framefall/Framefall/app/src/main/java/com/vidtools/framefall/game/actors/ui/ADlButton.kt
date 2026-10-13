package com.vidtools.framefall.game.actors.ui

import com.vidtools.framefall.game.controller.DiscoverController.ItemState
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// ADlButton — .dlb: ⬇ (рамка помаранч 35 %, фон 10 %) → кільце прогресу →
// ✓ (зелена; тап — грати). Стан опитує сам щокадру через [state].
// ─────────────────────────────────────────────────────────────────────────────
class ADlButton(
    screen: AdvancedScreen,
    private val state: () -> ItemState,
    private val onDownload: () -> Unit,
    private val onPlay: (videoId: String) -> Unit,
) : ATap(screen, 0.9f) {

    private val bgIdle = ARect(screen, 999f, GameColor.accent_10, stroke = GameColor.accentText_35)
    private val bgDone = ARect(screen, 999f, GameColor.ok_12, stroke = GameColor.ok_35)
    private val icDl   = icon(assets.ic_download, px(17f), GameColor.err_FF7A6B)
    private val icOk   = icon(assets.ic_check, px(17f), GameColor.ok_47E0A0)
    private val ring   = AProgressRing(screen, px(3f), GameColor.white_10, GameColor.accent_FF8A2A)
    private var last: ItemState? = null

    init { setSize(px(38f), px(38f)) }

    override fun addContent() {
        addAndFillActors(bgIdle, bgDone)
        addActor(ring); ring.setBounds(0f, 0f, width, height)
        listOf(icDl, icOk).forEach { addActor(it); it.setPosition((width - it.width) / 2f, (height - it.height) / 2f) }
        onClick {
            when (val s = state()) {
                ItemState.None -> onDownload()
                is ItemState.Done -> onPlay(s.videoId)
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
