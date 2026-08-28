package com.rbxhubpro.rohumex.businesModule.economy

import com.rbxhubpro.rohumex.adsmodule.AdConfig
import com.rbxhubpro.rohumex.util.log

// ═══════════════════════════════════════════════════════════════════════════
// ПРАВКА 5 — экономика из конфига. Лестница из двух ступеней (см.
// APK_INTEGRATION.md, правка 5): (1) блок economy в карточке ЭТОГО приложения
// на сервере; (2) блока/ключа нет → зашитый дефолт APK, т.е. сегодняшнее
// поведение. Дефолт — сам APK; конфиг — оверрайд поверх, чтобы экономику
// можно было КРУТИТЬ без релиза (позже /appconfig раздаст разным установкам
// разные значения — экономика как ось теста, sticky по iid).
//
// Читает AdConfig.remoteConfig на каждый вызов намеренно: конфиг подъезжает
// асинхронно (кэш AdPref → ответ сервера), кэшировать значения здесь —
// значит поймать старые числа на первый экран.
// ═══════════════════════════════════════════════════════════════════════════

object Econ {

    private val cfg get() = AdConfig.remoteConfig?.economy

    val startBalance    : Int get() = cfg?.startBalance     ?: 100
    val pushOptInReward : Int get() = cfg?.pushOptInReward  ?: 100
    val cashoutThreshold: Int get() = cfg?.cashoutThreshold ?: 10_000

    // Карты по block-именам — ключ ОБЯЗАН совпадать со строкой block в
    // событиях этого экрана, иначе карточка крутит воздух.
    fun reward (block: String, def: Int): Int = cfg?.rewards  ?.get(block) ?: def
    fun price  (block: String, def: Int): Int = cfg?.prices   ?.get(block) ?: def
    fun penalty(block: String, def: Int): Int = cfg?.penalties?.get(block) ?: def
    fun quest  (key  : String, def: Int): Int = cfg?.quests   ?.get(key)   ?: def

    // ВОЗВРАЩЕНО (было потеряно при рефакторинге): наборы однотипных значений —
    // 12 секторов колеса, 12 номиналов скретч-карты. Одним скаляром это не
    // адресуется, поэтому отдельная карта economy.rewards_list.
    //
    // ⚠️ Сверка длины обязательна: порядок значений = порядок enum Result, а
    // сами номиналы НАРИСОВАНЫ на текстуре колеса/карточки. Список другой длины
    // сдвинул бы соответствие «сектор ↔ сумма», и человек видел бы одно число, а
    // получал другое. Не совпало — молча берём дефолт из APK (то, что нарисовано).
    fun rewardList(key: String, def: IntArray): IntArray {
        val list = cfg?.rewardsList?.get(key) ?: return def
        if (list.size != def.size) {
            log("Econ.rewardList($key): длина ${list.size} != ${def.size} — берём дефолт APK")
            return def
        }
        return list.toIntArray()
    }
}
