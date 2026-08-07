package com.racing.funtols.game.controller

// ═════════════════════════════════════════════════════════════════════════════
//  MatchController — вся логіка гри "знайди пару".
//
//  Не знає нічого про LibGDX, актори й анімації — тільки правила:
//    • колода з pairCount пар (6 пар = 12 карток), перемішана
//    • одночасно може бути відкрито максимум 2 картки
//    • збіг → картки лишаються відкритими назавжди
//    • не збіг → картки треба закрити назад
//    • усі пари знайдено → перемога
//
//  В'юшка (APanelMatch) слухає колбеки, програє анімації і, коли закінчила,
//  повідомляє про це через onCompareFinished(). Саме так вирішується вимога
//  "хоч скільки клікай — відкриється тільки 2": поки анімація не завершилась,
//  isLocked = true і всі кліки ігноруються.
// ═════════════════════════════════════════════════════════════════════════════

class MatchController(private val pairCount: Int = 6) {

    // ------------------------------------------------------------------------
    // Deck
    // ------------------------------------------------------------------------
    // faces[i] — індекс картинки (0..pairCount-1) для картки з позицією i.
    // Кожен індекс зустрічається рівно 2 рази → це і є пари.
    private val faces = MutableList(pairCount * 2) { it / 2 }

    // Індекси карток, які вже вгадані (лишаються відкритими)
    private val matched = mutableSetOf<Int>()

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    private var firstIndex : Int? = null
    private var secondIndex: Int? = null

    // Ввід заблоковано, поки в'юшка програє анімацію порівняння
    var isLocked = false
        private set

    val cardCount get() = pairCount * 2
    val isWin     get() = matched.size == cardCount

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onOpen     : (Int) -> Unit      = {}            // відкрити картку
    var onMatch    : (Int, Int) -> Unit = { _, _ -> }   // пара збіглась
    var onMismatch : (Int, Int) -> Unit = { _, _ -> }   // пара не збіглась
    var onWin      : () -> Unit         = {}            // всі пари знайдено

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun faceOf(index: Int) = faces[index]

    /** Нова роздача: перемішати колоду і скинути стан */
    fun newGame() {
        faces.shuffle()
        matched.clear()
        firstIndex  = null
        secondIndex = null
        isLocked    = false
    }

    /** Клік по картці з позицією [index] */
    fun onCardClick(index: Int) {
        if (isLocked)          return   // вже відкрито 2 — чекаємо анімацію
        if (index in matched)  return   // вже вгадана пара
        if (index == firstIndex) return // повторний клік по тій самій картці

        onOpen(index)

        // Перша картка пари — просто запам'ятовуємо і чекаємо другу
        val first = firstIndex
        if (first == null) {
            firstIndex = index
            return
        }

        // Друга картка — блокуємо ввід і порівнюємо
        secondIndex = index
        isLocked    = true

        if (faces[first] == faces[index]) {
            matched.add(first)
            matched.add(index)
            onMatch(first, index)
        } else {
            onMismatch(first, index)
        }
    }

    /** В'юшка викликає, коли анімація порівняння завершилась */
    fun onCompareFinished() {
        firstIndex  = null
        secondIndex = null
        isLocked    = false

        if (isWin) onWin()
    }
}