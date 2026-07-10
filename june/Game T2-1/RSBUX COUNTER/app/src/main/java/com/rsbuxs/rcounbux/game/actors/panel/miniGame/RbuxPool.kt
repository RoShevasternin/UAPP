package com.rsbuxs.rcounbux.game.actors.panel.miniGame

import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen

class RbuxPool(
    private val screen   : AdvancedScreen,
    private val poolSize : Int = 10,
) {

    private val pool    = ArrayDeque<ARbuxFalling>()
    private val active  = mutableListOf<ARbuxFalling>()

    init {
        repeat(poolSize) {
            pool.addLast(ARbuxFalling(screen).apply {
                setSize(24f, 24f)
                isVisible = false
            })
        }
    }

    // Отримати вільний об'єкт з пулу
    fun obtain(): ARbuxFalling? {
        val obj = pool.removeFirstOrNull() ?: return null
        active.add(obj)
        return obj
    }

    // Повернути об'єкт в пул
    fun recycle(obj: ARbuxFalling) {
        obj.reset()
        active.remove(obj)
        pool.addLast(obj)
    }

    // Повернути всі активні об'єкти
    fun recycleAll() {
        active.toList().forEach { recycle(it) }
    }

    // Всі активні об'єкти (для додавання на сцену)
    fun allObjects(): List<ARbuxFalling> = pool + active

    val activeCount: Int get() = active.size
    val freeCount:   Int get() = pool.size
}