package com.sakurbx.fungambx.game.actors.popup

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.sakurbx.fungambx.game.actors.button.base.AButtonAnim
import com.sakurbx.fungambx.game.actors.button.base.AButtonStyles
import com.sakurbx.fungambx.game.actors.layout.constraintLayout.AConstraintLayout
import com.sakurbx.fungambx.game.utils.GameColor
import com.sakurbx.fungambx.game.utils.advanced.AdvancedScreen
import com.sakurbx.fungambx.game.utils.font.FontFactory
import com.sakurbx.fungambx.game.utils.font.FontParameter
import com.sakurbx.fungambx.game.utils.gdxGame

class APopupSakura(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(27)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_Laila_Bold, GameColor.pink_F066AE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP)
    private val aSakuraImg   = Image(gdxGame.assetsAll.POPUP_SAKURA)
    private val aResultLbl   = Label("0 RBX", lsDef)
    private val aRewardBtn   = AButtonAnim(screen, AButtonStyles.Anim.CLAIM)


    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onContinue = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }
        addRewardLbl()
        addSakuraImg()
        addRewardBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addRewardLbl() {
        aResultLbl.setSize(118f, 26f)
        add(aResultLbl) { centerX(); topToTop(margin = 83f) }
        aResultLbl.setAlignment(Align.center)
        aResultLbl.setEllipsis(true)
    }

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
            onContinue()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(reward: Long) {
        aResultLbl.setText("$reward RBX")
    }

}