package com.rbuxdrop.cougame.game.actors.particleEffect

import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.scenes.scene2d.Group

class AParticleEffectPool(
    sourceEffect   : ParticleEffect,
    initialCapacity: Int = 2,
    maxSize        : Int = 16,
) {

    // ------------------------------------------------------------------------
    // Pool
    // ------------------------------------------------------------------------

    private val pool = ParticleEffectPool(sourceEffect, initialCapacity, maxSize)

    // ------------------------------------------------------------------------
    // Spawn
    // ------------------------------------------------------------------------

    fun spawn(
        parent : Group,
        x      : Float,
        y      : Float,
        setup  : (AParticleEffectActor.() -> Unit)? = null,
    ) {
        val pooled = pool.obtain()
        val actor  = PooledActor(pooled)

        setup?.invoke(actor)
        actor.setPosition(x, y)
        parent.addActor(actor)
        actor.start(true)
    }

    // ------------------------------------------------------------------------
    // Pooled Actor
    // ------------------------------------------------------------------------

    private inner class PooledActor(
        private val pooled: ParticleEffectPool.PooledEffect,
    ) : AParticleEffectActor(pooled) {

        override fun act(delta: Float) {
            super.act(delta)
            if (pooled.isComplete) {
                pool.free(pooled)

                remove()
            }
        }
    }

}