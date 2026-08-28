package com.selftest.mindora.game.actors.progress

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.selftest.mindora.game.actors.vfx.AMask
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedGroup
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.gdxGame
import com.selftest.mindora.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AProgressItemPortrait(override val screen: AdvancedScreen): AdvancedGroup() {

    private val LENGTH = 175f

    private val progressImage   = Image(screen.drawerUtil.getTexture(GameColor.purple_9979FF))
    private val mask            = AMask(screen, gdxGame.assetsAll.progress_mask_portrait)

    private val onePercentX = LENGTH / 100f

    // 0 .. 100 %
    val progressPercentFlow = MutableStateFlow(0f)


    override fun addActorsOnGroup() {
        addMask()

        coroutine?.launch {
            progressPercentFlow.collect { percent ->
                runGDX {
                    progressImage.x = (percent * onePercentX) - LENGTH
                }
            }
        }

    }

    // ---------------------------------------------------
    // Add Actors
    // ---------------------------------------------------
    private fun AdvancedGroup.addMask() {
        addAndFillActor(mask)
        mask.addProgress()
    }

    private fun AdvancedGroup.addProgress() {
        addAndFillActor(progressImage)
    }

}