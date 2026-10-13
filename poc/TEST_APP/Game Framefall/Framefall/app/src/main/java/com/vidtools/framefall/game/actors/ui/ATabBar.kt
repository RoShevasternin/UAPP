package com.vidtools.framefall.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.vidtools.framefall.game.actors.label.AMsdfLabel
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.actor.icon
import com.vidtools.framefall.game.utils.actor.lbl
import com.vidtools.framefall.game.utils.advanced.AdvancedGroup
import com.vidtools.framefall.game.utils.advanced.AdvancedScreen
import com.vidtools.framefall.game.utils.assets
import com.vidtools.framefall.game.utils.msdf
import com.vidtools.framefall.game.utils.px

/** .tabbar: 4 кнопки (іконка 20 + Onest 700/10.5); активна — біла з помаранчевою іконкою. */
class ATabBar(
    override val screen: AdvancedScreen,
    private val bottomInset: Float,
    private val onSelect: (Int) -> Unit,
) : AdvancedGroup() {

    private val items: List<Pair<TextureRegion, String>> = listOf(
        assets.ic_download to "Save", assets.ic_compass to "Discover", assets.ic_grid to "Library", assets.ic_wand to "Studio",
    )
    private val icons = ArrayList<Image>()
    private val labels = ArrayList<AMsdfLabel>()
    var selected = -1
        private set

    override fun addActorsOnGroup() {
        addAndFillActor(Image(screen.drawerUtil.whiteRegion).apply { color.set(GameColor.tabbar_0C0806) })
        addActor(Image(screen.drawerUtil.whiteRegion).apply { color.set(GameColor.line_white_7); setBounds(0f, this@ATabBar.height - 1.1f, this@ATabBar.width, 1.1f) })
        val pad = px(6f)
        val iw = (width - pad * 2) / items.size
        val barH = height - bottomInset
        items.forEachIndexed { i, (region, title) ->
            val ic = icon(region, px(20f), GameColor.tabOff_8C7B6D)
            val l = lbl(title, msdf.bold(10.5f, GameColor.tabOff_8C7B6D))
            icons += ic; labels += l
            val tab = object : ATap(screen, 0.94f) {
                override fun addContent() {
                    val total = ic.height + px(3f) + l.height * 0.8f
                    val top = (height + total) / 2f
                    ic.setPosition((width - ic.width) / 2f, top - ic.height)
                    l.setPosition((width - l.width) / 2f, ic.y - px(3f) - l.height * 0.9f)
                    addActor(ic); addActor(l)
                }
            }.onClick { onSelect(i) }
            tab.setBounds(pad + i * iw, bottomInset + px(2f), iw, barH - px(4f))
            addActor(tab)
        }
        if (selected >= 0) select(selected)
    }

    fun select(i: Int) {
        selected = i
        if (icons.isEmpty()) return
        icons.forEachIndexed { k, ic -> ic.color.set(if (k == i) GameColor.accent_FF8A2A else GameColor.tabOff_8C7B6D) }
        labels.forEachIndexed { k, l -> l.setTextColor(if (k == i) Color.WHITE else GameColor.tabOff_8C7B6D) }
    }
}
