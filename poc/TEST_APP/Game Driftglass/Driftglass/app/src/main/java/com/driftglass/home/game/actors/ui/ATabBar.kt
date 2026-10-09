package com.driftglass.home.game.actors.ui

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.driftglass.home.core.i18n.L
import com.driftglass.home.game.actors.label.AMsdfLabel
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.icon
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.assets
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px

/** .tabbar: Огляд / Studio / Моє скло / Налаштування; іконка 21 + Onest 10.5; активна — бірюзова. */
class ATabBar(
    override val screen: AdvancedScreen,
    private val bottomInset: Float,
    private val onSelect: (Int) -> Unit,
) : AdvancedGroup() {

    private val items: List<Pair<TextureRegion, String>> = listOf(
        assets.ic_sparkle to L.discover, assets.ic_sliders to L.studio, assets.ic_layers to L.myGlass, assets.ic_gear to L.settings,
    )
    private val icons = ArrayList<Image>()
    private val labels = ArrayList<AMsdfLabel>()
    var selected = -1
        private set

    override fun addActorsOnGroup() {
        addAndFillActor(Image(screen.drawerUtil.whiteRegion).apply { color.set(GameColor.tabbar) })
        addActor(Image(screen.drawerUtil.whiteRegion).apply { color.set(GameColor.white_08); setBounds(0f, this@ATabBar.height - 1.1f, this@ATabBar.width, 1.1f) })
        val pad = px(10f)
        val iw = (width - pad * 2) / items.size
        val barH = height - bottomInset
        items.forEachIndexed { i, (region, title) ->
            val ic = icon(region, px(21f), GameColor.white_55)
            val l = lbl(title, msdf.medium(10.5f, GameColor.white_55))
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
        icons.forEachIndexed { k, ic -> ic.color.set(if (k == i) GameColor.teal_5FF2D1 else GameColor.white_55) }
        labels.forEachIndexed { k, l -> l.setTextColor(if (k == i) GameColor.teal_5FF2D1 else GameColor.white_55) }
    }
}
