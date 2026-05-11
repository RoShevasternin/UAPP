package com.rbuxrds.counterds.game.utils.wizardHelper

import com.rbuxrds.counterds.game.utils.advanced.AdvancedGroup

// Крок — просто інтерфейс
interface WizardStep {
    val group: AdvancedGroup
    val title: String

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onEnterBlock: () -> Unit

    fun onEnter() {}  // викликається коли крок стає активним
    fun onExit()  {}  // викликається коли йдемо далі
}