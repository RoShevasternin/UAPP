package com.skindustry.skinly.game.utils

import android.content.Context
import androidx.core.content.edit
import com.skindustry.skinly.appContext

// ═══════════════════════════════════════════════════════════════════════════
// Онбординг (Onboarding → Selector_1..3) показується ОДИН раз — при першому
// запуску. Прапорець ставиться в момент, коли юзер уперше доходить до головного
// екрана (HomeScreen.show), читається в LoaderScreen.
//
// SharedPreferences, а не DataStore: читання синхронне на GDX-потоці в момент
// навігації, без корутин і без гонки з асинхронним first().
// ═══════════════════════════════════════════════════════════════════════════

object Onboarding {

    private const val PREFS    = "onboarding"
    private const val KEY_DONE = "done"

    private val prefs get() = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    val isDone: Boolean get() = prefs.getBoolean(KEY_DONE, false)

    fun markDone() = prefs.edit { putBoolean(KEY_DONE, true) }

}
