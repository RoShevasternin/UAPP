package com.rbuxrds.counter.game.utils.wizardHelper

import com.rbuxrds.counter.game.utils.Block

class WizardController(
    private val steps   : List<WizardStep>,
    private val onFinish: () -> Unit        // коли всі кроки пройдено
) {
    private var currentIndex = 0

    val currentStep get() = steps[currentIndex]
    val isFirst     get() = currentIndex == 0
    val isLast      get() = currentIndex == steps.lastIndex

    fun next() {
        if (isLast) { onFinish(); return }
        currentStep.onExit()
        currentIndex++
        currentStep.onEnter()
    }

    fun back(finishBlock: Block = {}) {
        if (isFirst) {
            finishBlock()
            return
        }
        currentStep.onExit()
        currentIndex--
        currentStep.onEnter()
    }
}