package com.redwave.downloader.core.launcher

import com.redwave.downloader.core.config.AppFlags

// ═════════════════════════════════════════════════════════════════════════════
//  HomePlacement — який плейсмент у вкладки, коли Redwave (роль HOME) стартує ХОЛОДНО як HOME.
//
//  Очищення «Недавніх» знімає наш home-таск разом із процесом: на MIUI — OneKeyClean
//  (reason 13 OTHER KILLS BY SYSTEM, description «OneKeyClean»), на стоковому Android —
//  removeTask (reason 10 USER REQUESTED, description «remove task»). Живого onResume немає,
//  onDestroy приходить не завжди (заміряно на Redmi 12.10.2026), тож дивимось причину смерті
//  попереднього процесу (ApplicationExitInfo, Android 11+).
//  Нестача пам'яті (LOW_MEMORY), оновлення апки (FORCE STOP), ребут — це не «Недавні» → click_home.
//  Чистий Kotlin: тестується JVM-тестом.
// ═════════════════════════════════════════════════════════════════════════════
object HomePlacement {

    private const val REASON_USER_REQUESTED = 10   // ApplicationExitInfo.REASON_USER_REQUESTED
    private const val REASON_OTHER          = 13   // ApplicationExitInfo.REASON_OTHER

    /** Процес помер через очищення «Недавніх». */
    fun isRecentsClear(reason: Int, description: String?): Boolean {
        val d = description.orEmpty().lowercase()
        return when (reason) {
            REASON_OTHER          -> "clean" in d || "remove task" in d || d.isEmpty()
            REASON_USER_REQUESTED -> "remove task" in d
            else                  -> false
        }
    }

    /** reason / description — останнього виходу процесу, null — нового виходу немає (ребут, перший старт). */
    fun forColdHome(reason: Int?, description: String?): String =
        if (reason != null && isRecentsClear(reason, description)) AppFlags.PL_CLEAR_HISTORY else AppFlags.PL_CLICK_HOME
}
