package com.skindustry.skinly.game.actors.panel.personalization

import com.skindustry.skinly.game.actors.button.base.AButtonAnim
import com.skindustry.skinly.game.actors.button.base.AButtonStyles
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.screens.SettingsScreen
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class APanelTopPersonalization(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBackBtn   = AButtonAnim(screen, AButtonStyles.Anim.BACK)
    private val aEraserBtn = AButtonAnim(screen, AButtonStyles.Anim.ERASER)
    private val aShareBtn  = AButtonAnim(screen, AButtonStyles.Anim.SHARE)

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onBack   = {}
    var onEraser = {}
    var onShare  = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBackBtn()
        addShareBtn()
        addEraserBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBackBtn() {
        aBackBtn.setSize(50f, 50f)
        add(aBackBtn) { startToStart(margin = 3f); centerY() }
        aBackBtn.setOnClickListener { onBack() }
    }

    private fun addShareBtn() {
        aShareBtn.setSize(50f, 50f)
        add(aShareBtn) { endToEnd(margin = 3f); centerY() }
        aShareBtn.setOnClickListener { onShare() }
    }

    private fun addEraserBtn() {
        aEraserBtn.setSize(50f, 50f)
        add(aEraserBtn) { endToStart(aShareBtn, margin = -2f); centerY() }
        aEraserBtn.setOnClickListener { onEraser() }
    }

}