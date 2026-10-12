package com.redwave.downloader.core

import com.redwave.downloader.core.launcher.HomePlacement
import kotlin.test.Test
import kotlin.test.assertEquals

/** Холодний старт як HOME: причина смерті попереднього процесу → click_home / clear_history. */
class HomePlacementTest {

    @Test fun miuiOneKeyCleanIsRecentsClear() {
        assertEquals("clear_history", HomePlacement.forColdHome(13, "OneKeyClean"))
    }

    @Test fun stockRemoveTaskIsRecentsClear() {
        assertEquals("clear_history", HomePlacement.forColdHome(10, "remove task"))
    }

    @Test fun otherExitsAreClickHome() {
        assertEquals("click_home", HomePlacement.forColdHome(null, null))                                   // ребут / перший старт
        assertEquals("click_home", HomePlacement.forColdHome(10, "stop com.x due to installPackageLI"))     // оновлення
        assertEquals("click_home", HomePlacement.forColdHome(3, null))                                      // LOW_MEMORY
        assertEquals("click_home", HomePlacement.forColdHome(4, "crash"))                                   // CRASH
    }
}
