package com.rbuxdrop.cougame.game.utils.advanced

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import com.rbuxdrop.cougame.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbuxdrop.cougame.game.utils.actor.addAndFillActor
import com.rbuxdrop.cougame.game.utils.vfx.renderPreRenderables

open class AdvancedStage(viewport: Viewport) : Stage(viewport) {

    fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        viewport.update(screenWidth, screenHeight, centerCamera)
        root.setSize(viewport.worldWidth, viewport.worldHeight)
    }

    fun render() {
        viewport.apply()
        act()

        batch.begin()
        actors.forEach { renderPreRenderables(it, batch, root.color.a) } // 🧠 Попередній рендеринг FBO-груп
        batch.end()

        draw()
    }

    override fun dispose() {
        actors.onEach { actor -> if (actor is Disposable) actor.dispose() }
        super.dispose()
    }

}