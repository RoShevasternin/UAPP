package com.robuxe.robuxtracker.freerobux.Utils

import android.content.Context
import android.content.SharedPreferences

class RC_AppPrefrence private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val LAST_DAILY_TIME = "last_daily_time"
        private const val PREF_NAME = "RBXCal"
        private const val TOTAL_DIAMONDS = "MYTOTALDMNDS"

        @Volatile
        private var instance: RC_AppPrefrence? = null

        fun getInstance(context: Context): RC_AppPrefrence {
            return instance ?: synchronized(this) {
                instance ?: RC_AppPrefrence(context).also { instance = it }
            }
        }
    }

    var lastDailyTime: Long
        get() = sharedPreferences.getLong(LAST_DAILY_TIME, 0L)
        set(time) {
            sharedPreferences.edit().putLong(LAST_DAILY_TIME, time).apply()
        }

    var totalDiamonds: Int
        get() = sharedPreferences.getInt(TOTAL_DIAMONDS, 0)
        set(totalDiamonds) {
            sharedPreferences.edit().putInt(TOTAL_DIAMONDS, totalDiamonds).apply()
        }

    fun addDiamonds(diamonds: Int) {
        val currentTotal = totalDiamonds
        totalDiamonds = currentTotal + diamonds
    }
}
