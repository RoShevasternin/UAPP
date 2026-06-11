package com.skindustry.skinly.game.actors.panel.blokcy

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.skindustry.skinly.game.actors.layout.AlignH
import com.skindustry.skinly.game.actors.layout.AlignV
import com.skindustry.skinly.game.utils.actor.addActorAligned
import com.skindustry.skinly.game.utils.actor.setSize
import com.skindustry.skinly.game.utils.advanced.AdvancedGroup
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class ACardBlokcy(
    override val screen: AdvancedScreen,
    text      : String,
    labelStyle: Label.LabelStyle,
    texture   : Texture,
): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aImg = Image(texture)
    private val aLbl = Label(text, labelStyle)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(Image(gdxGame.assetsAll.BLOKCY_CARD))
        addImg()
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addImg() {
        aImg.setSize(282f, 372f)
        addActorAligned(aImg, AlignH.CENTER, AlignV.TOP)
        aImg.y -= 24f
    }
    private fun addLbl() {
        aLbl.setSize(121f, 30f)
        addActorAligned(aLbl, AlignH.CENTER, AlignV.BOTTOM)
        aLbl.y += 24f

        aLbl.setAlignment(Align.center)
    }

}