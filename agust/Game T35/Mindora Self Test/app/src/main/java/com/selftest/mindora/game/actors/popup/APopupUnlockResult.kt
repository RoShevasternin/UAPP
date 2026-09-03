package com.selftest.mindora.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.button.base.AButtonAnim
import com.selftest.mindora.game.actors.button.base.AButtonStyles
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame

class APopupUnlockResult(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg  = Image(gdxGame.assetsAll.POPUP_UNLOCK_RESULT)
    private val aUnlockBtn = AButtonAnim(screen, AButtonStyles.Anim.UNLOCK_RESULT)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onUnlock: () -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }
        addUnlockBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addUnlockBtn() {
        aUnlockBtn.setSize(276f, 50f)
        add(aUnlockBtn) { centerX(); bottomToBottom(margin = 24f) }

        aUnlockBtn.setOnClickListener { onUnlock() }
    }

}