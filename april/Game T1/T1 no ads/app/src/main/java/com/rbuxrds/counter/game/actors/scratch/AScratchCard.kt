package com.rbuxrds.counter.game.actors.scratch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.vfx.VfxEffect
import com.rbuxrds.counter.game.utils.vfx.VfxShaderCache
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AScratchCard(
    override val screen      : AdvancedScreen,
    cardDrawable             : TextureRegionDrawable,
    private val scratchRadius: Float = 0.07f,
) : AdvancedGroup() {

    // Колбек: передає % УНІКАЛЬНОЇ подряпаної площі (0..100)
    // Росте тільки коли стирається нова область, не при повторному проведенні
    var onScratched: ((percent: Float) -> Unit)? = null

    private val image = Image(cardDrawable)

    private var maskFbo    : FrameBuffer? = null
    private var maskRegion : TextureRegion? = null
    private val maskCamera = OrthographicCamera()

    private val drawShader: ShaderProgram by lazy {
        VfxShaderCache.get("shader/scratch/scratchDrawFS.glsl", DRAW_VERT)
    }
    private val revealShader: ShaderProgram by lazy {
        VfxShaderCache.get("shader/scratch/scratchRevealFS.glsl", VfxEffect.Companion.BATCH_VERT)
    }

    // ─── Grid-based відсоток ──────────────────────────────────────────────
    // Ділимо поверхню на GRID_SIZE × GRID_SIZE комірок.
    // Коли нова комірка подряпана вперше — відсоток росте.
    // Повторне проведення по тій самій комірці → відсоток не змінюється.

    private val GRID_SIZE      = 20  // 20×20 = 400 комірок
    private val scratchedCells = HashSet<Int>(GRID_SIZE * GRID_SIZE)

    private fun cellKey(normX: Float, normY: Float): Int {
        val col = (normX * GRID_SIZE).toInt().coerceIn(0, GRID_SIZE - 1)
        val row = (normY * GRID_SIZE).toInt().coerceIn(0, GRID_SIZE - 1)
        return row * GRID_SIZE + col
    }

    private fun scratchPercent(): Float =
        scratchedCells.size.toFloat() / (GRID_SIZE * GRID_SIZE) * 100f

    // ─── Lifecycle ────────────────────────────────────────────────────────

    override fun addActorsOnGroup() {
        addAndFillActor(image)
        touchable = Touchable.enabled

        addListener(object : ActorGestureListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                scratch(x, y)
            }
            override fun pan(event: InputEvent, x: Float, y: Float, deltaX: Float, deltaY: Float) {
                scratch(x, y)
            }
        })
    }

    override fun sizeChanged() {
        super.sizeChanged()
        if (width > 0f && height > 0f) createMaskFbo()
    }

    override fun dispose() {
        maskFbo?.dispose()
        maskFbo    = null
        maskRegion = null
        super.dispose()
    }

    // ─── Public API ───────────────────────────────────────────────────────

    fun reset() {
        // Скидаємо маску FBO
        maskFbo?.let { fbo ->
            fbo.begin()
            Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            fbo.end()
            stage?.viewport?.apply()
        }
        // Скидаємо grid — відсоток повернеться до 0
        scratchedCells.clear()
        onScratched?.invoke(0f)
    }

    // ─── Draw ─────────────────────────────────────────────────────────────

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val mask = maskRegion ?: run { super.draw(batch, parentAlpha); return }
        if (batch == null) return

        val prevSrc = batch.blendSrcFunc
        val prevDst = batch.blendDstFunc

        batch.shader = revealShader
        revealShader.setUniformi("u_texture", 0)

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
        mask.texture.bind(1)
        revealShader.setUniformi("u_mask", 1)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)

        val region = (image.drawable as? TextureRegionDrawable)?.region
        if (region != null) {
            revealShader.setUniformf("u_uvMin", region.u,  region.v)
            revealShader.setUniformf("u_uvMax", region.u2, region.v2)
        } else {
            revealShader.setUniformf("u_uvMin", 0f, 0f)
            revealShader.setUniformf("u_uvMax", 1f, 1f)
        }

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        super.draw(batch, parentAlpha)
        batch.shader = null
        batch.setBlendFunction(prevSrc, prevDst)
    }

    // ─── Scratch ──────────────────────────────────────────────────────────

    private fun scratch(localX: Float, localY: Float) {
        val fbo = maskFbo ?: return

        val normX =       (localX / width) .coerceIn(0f, 1f)
        val normY = 1f - (localY / height).coerceIn(0f, 1f)

        // Малюємо коло на маску
        fbo.begin()
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glViewport(0, 0, fbo.width, fbo.height)

        drawShader.bind()
        drawShader.setUniformf("u_center", normX, normY)
        drawShader.setUniformf("u_radius", scratchRadius)
        drawFullscreenQuad()

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        fbo.end()
        stage?.viewport?.apply()

        // Позначаємо всі комірки в радіусі як подряпані
        // Радіус в одиницях сітки
        val radiusCells = (scratchRadius * GRID_SIZE).toInt() + 1
        val centerCol   = (normX * GRID_SIZE).toInt().coerceIn(0, GRID_SIZE - 1)
        val centerRow   = (normY * GRID_SIZE).toInt().coerceIn(0, GRID_SIZE - 1)

        var hasNew = false
        for (dr in -radiusCells..radiusCells) {
            for (dc in -radiusCells..radiusCells) {
                val col = (centerCol + dc).coerceIn(0, GRID_SIZE - 1)
                val row = (centerRow + dr).coerceIn(0, GRID_SIZE - 1)
                val key = row * GRID_SIZE + col
                if (scratchedCells.add(key)) hasNew = true
            }
        }

        // Повідомляємо тільки якщо з'явились нові подряпані комірки
        if (hasNew) onScratched?.invoke(scratchPercent())
    }

    // ─── Helpers ─────────────────────────────────────────────────────────

    private val quadVertices = floatArrayOf(
        -1f, -1f, 0f, 0f,
        1f, -1f, 1f, 0f,
        1f,  1f, 1f, 1f,
        -1f,  1f, 0f, 1f,
    )

    private fun drawFullscreenQuad() {
        val gl     = Gdx.gl
        val posLoc = drawShader.getAttributeLocation("a_position")
        val uvLoc  = drawShader.getAttributeLocation("a_texCoord0")

        val buf = ByteBuffer
            .allocateDirect(quadVertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .also { it.put(quadVertices); it.flip() }

        if (posLoc >= 0) { gl.glEnableVertexAttribArray(posLoc); buf.position(0); gl.glVertexAttribPointer(posLoc, 2, GL20.GL_FLOAT, false, 16, buf) }
        if (uvLoc  >= 0) { gl.glEnableVertexAttribArray(uvLoc);  buf.position(2); gl.glVertexAttribPointer(uvLoc,  2, GL20.GL_FLOAT, false, 16, buf) }

        gl.glDrawArrays(GL20.GL_TRIANGLE_FAN, 0, 4)

        if (posLoc >= 0) gl.glDisableVertexAttribArray(posLoc)
        if (uvLoc  >= 0) gl.glDisableVertexAttribArray(uvLoc)
    }

    private fun createMaskFbo() {
        maskFbo?.dispose()
        val w   = width.toInt().coerceAtLeast(1)
        val h   = height.toInt().coerceAtLeast(1)
        val fbo = FrameBuffer(Pixmap.Format.RGBA8888, w, h, false)
        maskFbo = fbo

        fbo.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        fbo.end()

        maskRegion = TextureRegion(fbo.colorBufferTexture).apply { flip(false, true) }
        maskCamera.setToOrtho(false, w.toFloat(), h.toFloat())
        maskCamera.update()
    }

    companion object {
        private val DRAW_VERT = """
            attribute vec4 a_position;
            attribute vec2 a_texCoord0;
            varying vec2 v_texCoords;
            void main() {
                v_texCoords = a_texCoord0;
                gl_Position = a_position;
            }
        """.trimIndent()
    }
}