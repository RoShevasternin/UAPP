package com.diam.ondbit.game.actors.progress

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.diam.ondbit.game.actors.label.AMsdfLabel
import com.diam.ondbit.game.actors.vfx.AMask
import com.diam.ondbit.game.utils.GameColor
import com.diam.ondbit.game.utils.advanced.AdvancedGroup
import com.diam.ondbit.game.utils.advanced.AdvancedScreen
import com.diam.ondbit.game.utils.font.msdf.MsdfStyle
import com.diam.ondbit.game.utils.gdxGame
import com.diam.ondbit.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AProgressQuiz(override val screen: AdvancedScreen): AdvancedGroup() {

    private val msdf by lazy { gdxGame.msdfManager }

    private val styleDef = MsdfStyle(msdf, msdf.fontPoppins_Medium, 8f)

    private val LENGTH = 334f

    private val backgroundImage = Image(gdxGame.assetsAll.progress_back)
    private val progressImage   = Image(gdxGame.assetsAll.progress)
    private val mask            = AMask(screen)

    private val label = AMsdfLabel("", styleDef)

    private val onePercentX = LENGTH / 100f

    // 0 .. 100 %
    val progressPercentFlow = MutableStateFlow(0f)


    override fun addActorsOnGroup() {
        addBackground()
        addMask()
        addLabel()

        coroutine?.launch {
            progressPercentFlow.collect { percent ->
                runGDX {
                    progressImage.x = (percent * onePercentX) - LENGTH
                    label.setText("${percent.toInt()}%")
                }
            }
        }

    }

    // ---------------------------------------------------
    // Add Actors
    // ---------------------------------------------------
    private fun AdvancedGroup.addBackground() {
        addAndFillActor(backgroundImage)
    }

    private fun AdvancedGroup.addMask() {
        addActor(mask)
        mask.setBounds(5f, 5f, 334f, 10f)
        mask.addProgress()
    }

    private fun AdvancedGroup.addProgress() {
        addAndFillActor(progressImage)
    }

    private fun AdvancedGroup.addLabel() {
        addActor(label)
        label.setBounds(318f, 6f, 20f, 8f)
        label.setAlignment(Align.right)
    }

}