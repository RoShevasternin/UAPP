package com.rbuxrds.counterds.adsmodule

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import androidx.core.content.edit

// Зберігає дані реклами між запусками додатку
// Щоб при наступному запуску реклама одразу працювала
// без очікування відповіді від Firebase
class AdPref(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("rbux_ads_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    // ── RemoteConfig ──────────────────────────────────────────────────────────
    // Зберігаємо весь конфіг як JSON рядок

    fun saveConfig(model: RemoteConfigModel) {
        prefs.edit { putString(KEY_CONFIG, gson.toJson(model)) }
    }

    fun loadConfig(): RemoteConfigModel? {
        val json = prefs.getString(KEY_CONFIG, null) ?: return null
        return runCatching { gson.fromJson(json, RemoteConfigModel::class.java) }.getOrNull()
    }

    // ── UserType ──────────────────────────────────────────────────────────────
    // Зберігаємо тип юзера — визначається один раз і більше не змінюється

    fun saveUserType(type: UserType) {
        prefs.edit { putString(KEY_USER_TYPE, type.name) }
    }

    fun loadUserType(): UserType? {
        val name = prefs.getString(KEY_USER_TYPE, null) ?: return null
        return runCatching { UserType.valueOf(name) }.getOrNull()
    }

    // ── Navigation counters ───────────────────────────────────────────────────
    // Лічильник front навігації — зберігається між сесіями
    // (коли reset_on_app_restart = false)

    var frontNavCount: Int
        get() = prefs.getInt(KEY_FRONT_NAV, 0)
        set(value) = prefs.edit { putInt(KEY_FRONT_NAV, value) }

    fun resetFrontNav() {
        frontNavCount = 0
    }

    companion object {
        private const val KEY_CONFIG    = "remote_config_json"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_FRONT_NAV = "front_nav_count"
    }
}