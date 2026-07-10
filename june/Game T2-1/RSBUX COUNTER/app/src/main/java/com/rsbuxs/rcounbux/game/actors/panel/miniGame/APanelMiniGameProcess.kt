package com.rsbuxs.rcounbux.game.actors.panel.miniGame

import com.rsbuxs.rcounbux.game.actors.layout.constraintLayout.AConstraintLayout
import com.rsbuxs.rcounbux.game.actors.vfx.AMask
import com.rsbuxs.rcounbux.game.utils.advanced.AdvancedScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class APanelMiniGameProcess(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aMask = AMask(screen)

    // ------------------------------------------------------------------------
    // Pool
    // ------------------------------------------------------------------------
    private lateinit var pool: RbuxPool

    // Інтервал між спавном нових rbux (секунди)
    private val spawnIntervalMs = 800L

    private var isRunning = false

    // Колбек попадання
    var onHit: (() -> Unit)? = null

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aMask) { fillParent() }

        // Створюємо пул і одразу додаємо всі об'єкти на маску
        pool = RbuxPool(screen, poolSize = 10)
        pool.allObjects().forEach { aMask.addActor(it) }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    // Старт гри — починаємо спавнити rbux
    fun start() {
        isRunning = true
        spawnLoop()
    }

    // Стоп гри — повертаємо всіх в пул
    fun stop() {
        isRunning = false
        pool.recycleAll()
    }

    // ------------------------------------------------------------------------
    // Spawner
    // ------------------------------------------------------------------------

    private fun spawnLoop() {
        coroutine?.launch {
            while (isRunning) {
                spawnRbux()
                delay(spawnIntervalMs)
            }
        }
    }

    private fun spawnRbux() {
        val obj = pool.obtain() ?: return // пул вичерпано — пропускаємо

        obj.onHit = {
            onHit?.invoke()
            pool.recycle(obj)
        }

        obj.onMiss = {
            pool.recycle(obj)
        }

        obj.startFall(
            parentWidth  = aMask.width,
            parentHeight = aMask.height
        )
    }
}