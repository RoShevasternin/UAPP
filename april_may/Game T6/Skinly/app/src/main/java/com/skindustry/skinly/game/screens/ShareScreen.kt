package com.skindustry.skinly.game.screens

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.actors.panel.share.APanelTopShare
import com.skindustry.skinly.game.actors.panel.share.APanelShare
import com.skindustry.skinly.game.utils.Block
import com.skindustry.skinly.game.utils.GLOBAL_sharedSkinPath
import com.skindustry.skinly.game.utils.TIME_ANIM_SCREEN
import com.skindustry.skinly.game.utils.actor.animDelay
import com.skindustry.skinly.game.utils.actor.animHide
import com.skindustry.skinly.game.utils.actor.animShow
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class ShareScreen : AdvancedScreen() {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTop          by lazy { APanelTopShare(this) }
    private val aFrameSkinImg by lazy { Image(gdxGame.assetsAll.FRAME_SKIN) }
    private val aSkinImg      by lazy { Image() }
    private val aPanelShare   by lazy { APanelShare(this) }

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private var skinTexture: Texture? = null

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, adBannerUI))
        gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

    override fun hide() {
        super.hide()
        gdxGame.activity.hideNative()
    }

    override fun dispose() {
        super.dispose()
        skinTexture?.dispose()
        skinTexture = null
    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addTop()
        addFrameSkinImg()
        addSkinImg()
        addPanelShare()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addTop() {
        aTop.setSize(WIDTH, 56f)
        add(aTop) { centerX(); topToTop() }

        aTop.setTitle("Your skin")
        aTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addFrameSkinImg() {
        aFrameSkinImg.setSize(344f, 344f)
        add(aFrameSkinImg) { centerX(); topToBottom(aTop) }
    }

    private fun AConstraintLayout.addSkinImg() {
        val file = FileHandle(GLOBAL_sharedSkinPath)
        if (file.exists()) {
            skinTexture = Texture(file).apply {
                // Гладке масштабування при відображенні
                setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            }
            aSkinImg.drawable = TextureRegionDrawable(skinTexture)
        }

        aSkinImg.setSize(344f, 344f)
        add(aSkinImg) { center(aFrameSkinImg) }
    }

    private fun AConstraintLayout.addPanelShare() {
        aPanelShare.height = 88f
        add(aPanelShare) { centerX(); topToBottom(aSkinImg); matchWidth() }
    }

}