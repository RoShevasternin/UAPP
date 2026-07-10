package com.rbuxrds.counter.game.actors.redeem

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counter.game.actors.button.base.AButtonStyles
import com.rbuxrds.counter.game.actors.button.base.AButtonTexture
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame

class ADialogOops(override val screen: AdvancedScreen) : AdvancedGroup() {

    var onOk = {}

    private val aPanelImg  = Image(gdxGame.assetsAll.POPUP_REDEEM)
    private val aOkBtn     = AButtonTexture(screen, AButtonStyles.OK)

    override fun addActorsOnGroup() {
        addAndFillActor(aPanelImg)
        addActor(aOkBtn)

        aOkBtn.setBounds(16f, 20f, 284f, 56f)
        aOkBtn.setOnClickListener { onOk() }
    }


}