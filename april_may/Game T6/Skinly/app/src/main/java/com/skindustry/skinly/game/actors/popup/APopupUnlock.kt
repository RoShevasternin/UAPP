package com.skindustry.skinly.game.actors.popup

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.actor.setOnClickListener
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class APopupUnlock(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPopupImg  = Image(gdxGame.assetsAll.POPUP_UNLOCK)
    private val aWatchBtn  = Actor()
    private val aCancelBtn = Actor()

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onWatch  = {}
    var onCancel = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aPopupImg) { fillParent() }
        addWatchBtn()
        addCancelBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addWatchBtn() {
        addActor(aWatchBtn)
        aWatchBtn.setBounds(16f, 64f, 312f, 56f)

        aWatchBtn.setOnClickListener(null) {
            gdxGame.soundUtil.apply { play(UNLOCK) }
            onWatch()
        }
    }

    private fun addCancelBtn() {
        addActor(aCancelBtn)
        aCancelBtn.setBounds(56f, 7f, 232f, 42f)

        aCancelBtn.setOnClickListener { onCancel() }
    }

}