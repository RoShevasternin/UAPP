package com.mon.sterbx.game.actors.loader

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mon.sterbx.game.actors.vfx.AMask
import com.mon.sterbx.game.utils.advanced.AdvancedGroup
import com.mon.sterbx.game.utils.advanced.AdvancedScreen
import com.mon.sterbx.game.utils.font.FontFactory
import com.mon.sterbx.game.utils.font.FontParameter
import com.mon.sterbx.game.utils.gdxGame
import com.mon.sterbx.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AProgressLoader(override val screen: AdvancedScreen): AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameterDef = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "%")
        .setSize(10)

    private val lsDef = FontFactory.create(screen, parameterDef, screen.fontGenerator_BeVietnamPro_Bold)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private var LENGTH      = 295f
    private var onePercentX = LENGTH / 100f

    // 0 .. 100 %
    val progressPercentFlow = MutableStateFlow(0f)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val backgroundImage   = Image(gdxGame.assetsLoader.progress_bg)
    private val progressImage     = Image(gdxGame.assetsLoader.progress)
    private val mask              = AMask(screen)
    private val aLbl              = Label("", lsDef)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(backgroundImage)
        addMask()
        addLbl()

        coroutine?.launch { progressPercentFlow.collect { percent -> runGDX { updateProgressX(percent) } } }
    }

    // ---------------------------------------------------
    // Add Actors
    // ---------------------------------------------------

    private fun AdvancedGroup.addMask() {
        addActor(mask)
        mask.setBounds(5f, 5f, 295f, 10f)
        mask.addProgress()
    }

    private fun AdvancedGroup.addProgress() {
        addAndFillActor(progressImage)
    }

    private fun addLbl() {
        addActor(aLbl)
        aLbl.setBounds(275f, 0f, 21f, 20f)
        aLbl.setAlignment(Align.right)
    }

    // ---------------------------------------------------
    // Logic
    // ---------------------------------------------------
    private fun updateProgressX(percent: Float) {
        progressImage.x = (percent * onePercentX) - LENGTH
    }

    fun setProgressPercent(percent: Float) {
        progressPercentFlow.value = percent
        runGDX { aLbl.setText("${percent.roundToInt()}%") }
    }


}