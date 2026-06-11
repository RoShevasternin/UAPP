package com.skindustry.skinly.game.actors.panel.blokcy

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.skindustry.skinly.game.actors.layout.AlignH
import com.skindustry.skinly.game.actors.layout.AlignV
import com.skindustry.skinly.game.utils.actor.addActorAligned
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class APanelPoints(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listImg = List(3) { Image(gdxGame.assetsAll.point_gray) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        var nx = 0f
        listImg.forEachIndexed { index, img ->
            addActor(img)
            img.setBounds(nx, 0f, 12f, 12f)
            nx += 16f + 12f
        }

        listImg.first().drawable = TextureRegionDrawable(gdxGame.assetsAll.point_black)
    }

    fun setActive(index: Int) {
        listImg.forEachIndexed { i, img ->
            img.drawable = TextureRegionDrawable(
                if (i == index) gdxGame.assetsAll.point_black
                else            gdxGame.assetsAll.point_gray
            )
        }
    }

}