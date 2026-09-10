package com.rbxrush.rushrbx.game.utils

import android.content.Context
import androidx.core.content.edit
import com.rbxrush.rushrbx.appContext

// ═══════════════════════════════════════════════════════════════════════════
// Онбординг (OnboardingScreen → Selector_1..4) показується ОДИН раз — при
// першому запуску. Прапорець ставиться, коли юзер уперше доходить до меню
// (HomeScreen.show), читається в LoaderScreen при виборі першого екрана.
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
