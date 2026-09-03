package com.selftest.mindora.game.actors.checkbox.base

class ACheckBoxGroup {

    var currentCheckedCheckBox: ACheckBoxBase? = null
        private set

    /**
     * Програмно вибрати потрібний checkbox.
     *
     * @param invokeBlock false — вибрати ТИХО, без onCheckListener. Потрібно
     *        при відновленні збереженого стану: інакше «намалювати попередню
     *        відповідь» виглядало б для екрана як новий тап користувача
     *        (у TestScreen це гортало б питання вперед само по собі).
     */
    fun select(checkBox: ACheckBoxBase, invokeBlock: Boolean = true) {
        currentCheckedCheckBox?.uncheck(invokeBlock = false)
        currentCheckedCheckBox = checkBox
        checkBox.check(invokeBlock = invokeBlock)
    }

    // Скинути вибір
    fun clear() {
        currentCheckedCheckBox?.uncheck(invokeBlock = false)
        currentCheckedCheckBox = null
    }

    // Чи є хоч один вибраний
    val hasSelection get() = currentCheckedCheckBox != null

    // Internal — викликається з ACheckBoxBase
    internal fun onChecked(checkBox: ACheckBoxBase) {
        currentCheckedCheckBox?.uncheck(invokeBlock = false)
        currentCheckedCheckBox = checkBox
    }
}