package com.skindustry.skinly.game.actors.coin

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen

// Іконка монети: помаранчеве коло + світліша серцевина. Генерується
// drawerUtil — окремої текстури в атласі під монету немає і не потрібно.
class ACoinIcon(
    override val screen: AdvancedScreen,
    private val size   : Int,
) : AdvancedGroup() {

    private val inner = (size * 0.62f).toInt()

    private val aOuterImg = Image(screen.drawerUtil.getRoundedRegion(size, size, size / 2, GameColor.coin_F59E0B, GameColor.coin_EA580C))
    private val aInnerImg = Image(screen.drawerUtil.getRoundedRegion(inner, inner, inner / 2, GameColor.coin_FDE047, GameColor.coin_F59E0B))

    override fun addActorsOnGroup() {
        addAndFillActor(aOuterImg)
        addActor(aInnerImg)
        val offset = (size - inner) / 2f
        aInnerImg.setBounds(offset, offset, inner.toFloat(), inner.toFloat())
    }
}
