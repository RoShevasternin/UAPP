package com.mon.sterbx.game.actors.popup

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.ALabel
import com.mon.sterbx.game.actors.button.base.AButtonAnim
import com.mon.sterbx.game.actors.button.base.AButtonStyles
import com.mon.sterbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.mon.sterbx.game.utils.GameColor
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter
import com.mon.sterbx.game.utils.gdxGame

class APopup(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "RBX")
        .setSize(24)
        .setBorder(1f, Color.valueOf("733600"))

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_BeVietnamPro_Bold, GameColor.orange_F0862A)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP)
    private val aResultLbl   = ALabel(screen, "0 RBX", lsDef)
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
        addRewardBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addRewardLbl() {
        aResultLbl.setSize(50f, 26f)
        add(aResultLbl) { startToStart(margin = 167f); bottomToBottom(margin = 109f) }
        aResultLbl.rotation = -2.5f
    }

    private fun addRewardBtn() {
        aRewardBtn.setSize(312f, 44f)
        add(aRewardBtn) { centerX(); bottomToBottom(margin = 14f) }

        aRewardBtn.setOnClickListener {
            gdxGame.soundUtil.apply { play(REWARD) }
            onContinue()
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setReward(reward: Long) {
        aResultLbl.label.setText(reward.toString())
    }

}