package com.skindustry.skinly.game.actors.panel.share

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.skindustry.skinly.game.actors.layout.autoLayout.AAutoLayout
import com.skindustry.skinly.game.utils.GLOBAL_sharedSkinPath
import com.skindustry.skinly.game.utils.actor.setOnClickListener
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class APanelShare(override val screen: AdvancedScreen): AAutoLayout(
    screen     = screen,
    direction  = Direction.HORIZONTAL,
    gapMain    = 16f,
    alignMain  = AlignMain.CENTER,
    alignCross = AlignCross.CENTER,
) {

    private val listShareIcon = listOf(
        gdxGame.assetsAll.share_meta,
        gdxGame.assetsAll.share_insta,
        gdxGame.assetsAll.share_whats,
        gdxGame.assetsAll.share_otherr,
    )

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val listIconImg = List(4) { Image(listShareIcon[it]) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        val listBlock = listOf<() -> Unit>(
            { gdxGame.activity.shareManager.shareToFacebook(GLOBAL_sharedSkinPath) },
            { gdxGame.activity.shareManager.shareToInstagram(GLOBAL_sharedSkinPath) },
            { gdxGame.activity.shareManager.shareToWhatsApp(GLOBAL_sharedSkinPath) },
            { gdxGame.activity.shareManager.shareImage(GLOBAL_sharedSkinPath) },
        )

        listIconImg.forEachIndexed { index, img ->
            img.setSize(56f, 56f)
            add(img)
            img.setOnClickListener { listBlock[index].invoke() }
        }
    }

}