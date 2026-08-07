package com.fimer.skintool.game.actors.panel.tips

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.fimer.skintool.game.actors.label.AMsdfLabel
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.data.TipsData
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.font.msdf.MsdfStyle
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.global.GLOBAL_TIPS_INDEX

class APanelTips(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleTitle = MsdfStyle(msdf, msdf.fontBowlbyOneSC_Regular, 16f)
    private val styleText  = MsdfStyle(msdf, msdf.fontNunitoSans_Regular, 14f)

    private val data = TipsData.items()[GLOBAL_TIPS_INDEX]

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.panel_tips)
    private val aTitleLbl = AMsdfLabel(data.name, styleTitle)
    private val aTextLbl  = AMsdfLabel(data.text, styleText)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }
        addLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addLbl() {
        aTitleLbl.wrap = true
        aTextLbl.wrap  = true

        add(aTitleLbl) { startToStart(margin = 24f); endToEnd(margin = 24f); topToTop(margin = 24f); matchWidth() }
        add(aTextLbl) { startToStart(margin = 24f); endToEnd(margin = 24f); topToBottom(aTitleLbl, 16f); matchWidth() }

        aTitleLbl.pack()
        aTextLbl.pack()


        height = (24f + aTitleLbl.height + 16f + aTextLbl.height + 24f)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    private fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

    private fun setText(text: String) {
        aTextLbl.setText(text)
    }

}