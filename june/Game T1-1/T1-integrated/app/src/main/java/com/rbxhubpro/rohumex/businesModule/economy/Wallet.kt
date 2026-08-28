package com.rbxhubpro.rohumex.businesModule.economy

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rbxhubpro.rohumex.businesModule.Biz
import com.rbxhubpro.rohumex.businesModule.backend.Bt
import com.rbxhubpro.rohumex.businesModule.backend.Events
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════════
// ПРАВКА 5 — кошелёк. ЕДИНАЯ точка мутации баланса на всё приложение.
//
// Каждое движение монет автоматически уходит событием (coins_earned /
// coins_spent): событие живёт РЯДОМ с мутацией, а раз мутация возможна
// только здесь — «забыть событие» физически нельзя.
//
// ПРАВИЛА ВЫЗЫВАЮЩЕГО (нарушение = дыры в отчётах):
//   · никаких своих хранилищ баланса, prefs, `var coins` по экранам;
//   · никогда не звать Events.coinsEarned/coinsSpent рядом с add()/spend() —
//     будет дубль;
//   · списание ТОЛЬКО через spend(): add() с минусом сломает обе метрики.
//
// ХРАНИЛИЩЕ — SharedPreferences: баланс это один Long, DataStore здесь дал бы
// корутинную обвязку без выигрыша. Точка одна, замена не тронет вызывающих.
//
// ПОТОКИ: зовётся из GDX render-потока, main и Worker'а — всё под
// synchronized(this). Запись в prefs синхронная (commit не нужен: apply
// переживает штатное убийство процесса, а баланс не транзакция).
// ═══════════════════════════════════════════════════════════════════════════

object Wallet {

    private const val PREFS       = "wallet"
    private const val KEY_BALANCE = "balance"

    // ------------------------------------------------------------------------
    // Storage
    // ------------------------------------------------------------------------
    // Ленивая инициализация, а не init() из Biz.install: стартовый баланс
    // берётся из конфига (economy.start_balance), а конфиг в App.onCreate
    // может быть ещё не поднят из кэша AdPref. Первое РЕАЛЬНОЕ обращение к
    // кошельку случается заведомо позже — на экране, когда конфиг уже есть.
    private val prefs: SharedPreferences by lazy {
        Biz.appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).also { p ->
            // Ключ появляется ОДИН раз за жизнь установки: смена
            // start_balance в карточке не переписывает балансы живущих.
            if (!p.contains(KEY_BALANCE)) {
                p.edit { putLong(KEY_BALANCE, Econ.startBalance.toLong()) }
            }
            _balance.value = p.getLong(KEY_BALANCE, 0L)
        }
    }

    private val _balance = MutableStateFlow(0L)

    // ------------------------------------------------------------------------
    // Чтение
    // ------------------------------------------------------------------------

    /**
     * Реактивный баланс: подписался один раз — UI обновляется сам после любой
     * мутации, откуда бы она ни пришла (экран, диплинк, опт-ин).
     *
     * ⚠️ StateFlow доставляет значение в потоке ПОДПИСЧИКА. Если собирать в
     * CoroutineScope(Dispatchers.Default), колбэк придёт с фонового потока —
     * трогать оттуда UI/scene2d нельзя. Оборачивайте обновление в
     * runOnUiThread / Gdx.app.postRunnable (см. observe() ниже).
     */
    val balanceFlow: StateFlow<Long> get() = prefs.let { _balance.asStateFlow() }

    /** Текущее значение одним числом — для разовых проверок и логов. */
    val balance: Long get() = prefs.let { _balance.value }

    // ------------------------------------------------------------------------
    // Мутации
    // ------------------------------------------------------------------------

    /**
     * Начисление. coins_earned шлёт САМ кошелёк.
     * @param bt    тип механики из общего словаря
     * @param block локальное имя экрана (то же, что в screen_view)
     */
    fun add(amount: Int, bt: Bt, block: String) {
        if (amount <= 0) return
        val next = synchronized(this) {
            val v = prefs.getLong(KEY_BALANCE, 0L) + amount
            prefs.edit { putLong(KEY_BALANCE, v) }
            v
        }
        _balance.value = next
        Events.coinsEarned(bt = bt, block = block, amount = amount)
    }

    /**
     * Списание — цена попытки или штраф.
     * @return false = не хватает. Списания и события НЕТ, баланс не уходит
     *         в минус; вызывающий решает, что показать.
     */
    fun spend(amount: Int, bt: Bt, block: String): Boolean {
        if (amount <= 0) return true
        val next = synchronized(this) {
            val v = prefs.getLong(KEY_BALANCE, 0L)
            if (v < amount) return@synchronized null
            val n = v - amount
            prefs.edit { putLong(KEY_BALANCE, n) }
            n
        } ?: return false

        _balance.value = next
        Events.coinsSpent(bt = bt, block = block, amount = amount)
        return true
    }

}