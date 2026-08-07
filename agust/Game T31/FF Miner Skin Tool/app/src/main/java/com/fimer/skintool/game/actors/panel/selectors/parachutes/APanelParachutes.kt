package com.fimer.skintool.game.actors.panel.selectors.parachutes

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.fimer.skintool.game.actors.AScrollPane
import com.fimer.skintool.game.actors.label.AMsdfLabel
import com.fimer.skintool.game.actors.layout.autoLayout.AAutoLayout
import com.fimer.skintool.game.actors.layout.constraintLayout.AConstraintLayout
import com.fimer.skintool.game.actors.panel.APanelProtocol
import com.fimer.skintool.game.data.selectors.ParachutesData
import com.fimer.skintool.game.utils.advanced.AdvancedScreen
import com.fimer.skintool.game.utils.gdxGame
import com.fimer.skintool.game.utils.global.GLOBAL_PARACHUTES_INDEX

class APanelParachutes(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aTable = AAutoLayout(
        screen        = screen,
        direction     = AAutoLayout.Direction.VERTICAL,
        sizingH       = AAutoLayout.Sizing.HUG,
        gapMain       = 8f,
        paddingBottom = 20f
    )

    private val aScrollPane = AScrollPane(aTable)

    private val aGroup   = AConstraintLayout(screen)
    private val aContent = Image(gdxGame.assetsAll.PANEL_EMOTES)

    private val data = ParachutesData.items()[GLOBAL_PARACHUTES_INDEX]

    private val aIcon    = Image(data.texture)
    private val aTitle   = AMsdfLabel(gdxGame.msdfManager, gdxGame.msdfManager.fontBowlbyOneSC_Regular, data.name, 16f)
    private val aDesc    = AMsdfLabel(gdxGame.msdfManager, gdxGame.msdfManager.fontNunitoSans_Regular, data.desc, 14f)

    private val aProtocol = APanelProtocol(screen)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aScrollPane) { fillParent() }
        aTable.setUpTable()
    }

    override fun sizeChanged() {
        super.sizeChanged()
        aTable.minH = height
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun AAutoLayout.setUpTable() {
        aTable.width = width
        addContent()
    }

    private fun AAutoLayout.addContent() {
        aGroup.setSize(344f, 793f)
        add(aGroup)
        aGroup.addToContent()
    }

    private fun AConstraintLayout.addToContent() {
        add(aContent) { fillParent() }

        aIcon.setSize(177f, 177f)
        add(aIcon) { centerX(); topToTop() }

        aTitle.setSize(296f, 16f)
        add(aTitle) { centerX(); topToBottom(aIcon, 40f) }

        aDesc.setSize(296f, 252f)
        aDesc.wrap = true
        add(aDesc) { centerX(); topToBottom(aTitle, 8f) }
        aDesc.setAlignment(Align.topLeft)

        aProtocol.setSize(296f, 260f)
        add(aProtocol) { centerX(); topToBottom(aDesc, 16f) }
    }

}