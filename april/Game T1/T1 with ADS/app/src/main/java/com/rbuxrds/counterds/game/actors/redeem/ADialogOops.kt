package com.rbuxrds.counterds.game.actors.redeem

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counterds.game.actors.button.base.AButtonStyles
import com.rbuxrds.counterds.game.actors.button.base.AButtonTexture
import com.rbuxrds.counterds.game.utils.actor.addAndFillActor
import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counterds.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counterds.game.utils.gdxGame

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