package com.diam.ondbit.game.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.video.VideoPlayer

class AVideoActor(player: VideoPlayer? = null) : Actor() {

    var player: VideoPlayer? = player
        private set

    // ------------------------------------------------------------------------
    // Заміна плеєра
    // ------------------------------------------------------------------------
    fun setPlayer(newPlayer: VideoPlayer?) {
        // старий ставимо на паузу (не dispose — ним володіє хтось ззовні)
        player?.pause()

        player = newPlayer
        newPlayer?.play()
    }

    // ------------------------------------------------------------------------
    // Render
    // ------------------------------------------------------------------------
    override fun act(delta: Float) {
        super.act(delta)
        player?.update()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null) return
        val p = player ?: return
        val texture: Texture = p.texture ?: return

        val vidW = p.videoWidth.toFloat()
        val vidH = p.videoHeight.toFloat()
        if (vidW <= 0f || vidH <= 0f) return

        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)

        // UV-обрізка проти зеленого padding (ширина не кратна 16)
        val u2 = (vidW / texture.width).coerceAtMost(1f)
        val v2 = (vidH / texture.height).coerceAtMost(1f)

        val c = color
        batch.setColor(c.r, c.g, c.b, c.a * parentAlpha)
        batch.draw(texture, x, y, width, height, 0f, v2, u2, 0f)
    }
}