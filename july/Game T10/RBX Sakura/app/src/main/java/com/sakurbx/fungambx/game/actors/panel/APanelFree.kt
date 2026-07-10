package com.sakurbx.fungambx.game.actors.panel

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.button.base.AButtonAnim
import com.sakurbx.fungambx.game.actors.button.base.AButtonStyles
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.gdxGame

class APanelFree(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP_GUESS)
    private val aSakuraImg   = Image(gdxGame.assetsAll.POPUP_SAKURA)
    private val aRewardBtn   = AButtonAnim(screen, AButtonStyles.Anim.FREE)


    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onGetPrize: (Long) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }
        addSakuraImg()
        addRewardBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addSakuraImg() {
        aSakuraImg.setSize(265f, 314f)
        add(aSakuraImg) { centerX(); bottomToBottom(margin = 11f) }

        aSakuraImg.setOrigin(Align.bottom)   // центр масштабування — низ посередині

        aSakuraImg.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.04f, 1.04f, 1.6f, Interpolation.sine),
                    Actions.scaleTo(1.00f, 1.00f, 1.6f, Interpolation.sine),
                )
            )
        )
    }

    private fun addRewardBtn() {
        aRewardBtn.setSize(267f, 39f)
        add(aRewardBtn) { centerX(); bottomToBottom(margin = 26f) }

        aRewardBtn.setOnClickListener {
            gdxGame.soundUtil.apply { play(REWARD) }
            onGetPrize(200)
        }
    }

}