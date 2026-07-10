package com.rbuxrds.counter.game.utils.vfx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.ScreenUtils
import com.rbuxrds.counter.game.utils.Block
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen

/**
 * Scene2D актор з ping-pong ефект пайплайном.
 *
 * ─── GC АНАЛІЗ АЛОКАЦІЙ В preRender() ──────────────────────────────────────
 *
 * VfxContext  (~32 bytes): 4 примітиви. Android ART young GC — практично безкоштовно.
 * PingPong    (~24 bytes): 3 поля. Самі FBO беруться з пулу (reused). Wrapper — дрібниця.
 * TextureRegion          : PRE-ALLOCATED. UV координати + flip прапори = варто зберегти.
 *                          Два окремих регіони: staticRegion для isStaticEffect=true,
 *                          dynamicRegion для isStaticEffect=false — щоб не конфліктували.
 *
 * Для isStaticEffect=true (blur фон, маска тощо) preRender() запускається ОДИН раз,
 * після чого лише draw(). Тобто для більшості ефектів алокацій у render loop немає.
 */
open class VfxGroup(
    override val screen: AdvancedScreen
) : AdvancedGroup(), PreRenderable {

    // ─── Ефекти ───────────────────────────────────────────────────────────────
    // Backing property патерн:
    //   _effects — приватний MutableList, тільки VfxGroup може змінювати
    //   effects  — публічний read-only List, зовні видно але не змінити
    //   getEffect() inline читає публічний effects — без @PublishedApi

    private val _effects         = mutableListOf<VfxEffect>()
    val effects: List<VfxEffect> = _effects

    fun addEffect(effect: VfxEffect): VfxGroup   { _effects.add(effect); return this }
    fun removeEffect(effect: VfxEffect): VfxGroup { _effects.remove(effect); rerenderOnce(); return this }
    fun clearEffects(): VfxGroup                  { _effects.clear(); rerenderOnce(); return this }

    inline fun <reified T : VfxEffect> getEffect(): T? = effects.filterIsInstance<T>().firstOrNull()

    // ─── Static effect ────────────────────────────────────────────────────────

    open var isStaticEffect = false
        set(value) {
            if (!value) { releaseCached(); needsUpdate = true }
            else needsUpdate = true
            field = value
        }

    fun rerenderOnce() { needsUpdate = true }

    private var cachedFbo : FrameBuffer? = null
    private var pendingFbo: FrameBuffer? = null
    private var needsUpdate              = true

    // ─── Pre-allocated TextureRegion — без GC в render loop ──────────────────
    // staticRegion  → для isStaticEffect=true (живе між кадрами разом з cachedFbo)
    // dynamicRegion → для isStaticEffect=false (живе від preRender до draw)
    // Два окремих: можуть одночасно вказувати на різні FBO без конфліктів.

    private val staticRegion  = TextureRegion()
    private val dynamicRegion = TextureRegion()
    private var textureResult : TextureRegion? = null

    // ─── Camera (pre-allocated) ───────────────────────────────────────────────

    private val camera   = OrthographicCamera()
    private val identity = Matrix4().idt()
    private val tmpProj  = Matrix4()
    private val tmpTrans = Matrix4()

    // ─── Lifecycle ─────────────────────────────────────────────────────────────

    override fun addActorsOnGroup() { setupCamera() }

    override fun sizeChanged() {
        super.sizeChanged()
        if (width > 0f && height > 0f) { setupCamera(); needsUpdate = true }
    }

    override fun dispose() {
        releaseCached()
        pendingFbo?.let { runCatching { screen.renderPipeline.vfxPool.free(it) } }
        pendingFbo = null
        super.dispose()
    }

    // ─── PreRenderable ─────────────────────────────────────────────────────────

    override fun preRender(batch: Batch, parentAlpha: Float) {
        if (isStaticEffect && !needsUpdate) return
        if (stage == null || !isVisible) return

        val pool = screen.renderPipeline.vfxPool

        // Фізичні пікселі FBO через viewport scale
        val vp     = stage!!.viewport
        val scaleX = vp.screenWidth.toFloat()  / vp.worldWidth.coerceAtLeast(1f)
        val scaleY = vp.screenHeight.toFloat() / vp.worldHeight.coerceAtLeast(1f)
        val bufW   = (width  * scaleX).toInt().coerceAtLeast(1)
        val bufH   = (height * scaleY).toInt().coerceAtLeast(1)

        // VfxContext — дрібна алокація, ART young GC, прийнятно
        val context = VfxContext(width, height, bufW, bufH)

        // Зберігаємо стан batch без алокацій (примітиви)
        val savedR   = batch.color.r
        val savedG   = batch.color.g
        val savedB   = batch.color.b
        val savedA   = batch.color.a
        val savedSrc = batch.blendSrcFunc
        val savedDst = batch.blendDstFunc

        if (batch.isDrawing) batch.end()

        // Крок 1: preRender дочірніх VfxGroup / PreRenderableGroup
        batch.begin()
        children.begin()
        for (i in 0 until children.size) renderPreRenderables(children[i], batch, 1f)
        children.end()
        if (batch.isDrawing) batch.end()

        // Крок 2: рендер дітей у pingPong.dst
        // PingPong — дрібна алокація (~24 bytes), FBOs беруться з пулу
        val pingPong = PingPong(pool, bufW, bufH)

        pingPong.dst.begin()
        ScreenUtils.clear(Color.CLEAR, true)
        batch.begin()
        batch.setBlendFunctionSeparate(
            GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,
            GL20.GL_ONE,       GL20.GL_ONE_MINUS_SRC_ALPHA
        )
        batch.withMatrix(camera.combined, identity) {
            drawChildrenWithoutTransform(batch, 1f)
        }
        batch.end()
        pingPong.dst.end()
        pingPong.swap()  // src = знімок дітей, dst = вільний для ефектів

        // Крок 3: ефект пайплайн
        for (effect in effects) effect.render(pingPong, context)

        // Крок 4: зберігаємо результат
        pool.free(pingPong.dst)  // dst більше не потрібен
        val resultFbo = pingPong.src

        if (isStaticEffect) {
            releaseCached()
            cachedFbo = resultFbo
            // Pre-allocated region: оновлюємо UV без нової алокації
            updateRegion(staticRegion, resultFbo)
            textureResult = staticRegion
            needsUpdate   = false
        } else {
            pendingFbo?.let { pool.free(it) }
            pendingFbo = resultFbo
            // Pre-allocated region: оновлюємо UV без нової алокації
            updateRegion(dynamicRegion, resultFbo)
            textureResult = dynamicRegion
        }

        // Крок 5: відновлюємо стан
        stage?.viewport?.apply()
        batch.projectionMatrix = stage?.camera?.combined ?: batch.projectionMatrix
        batch.transformMatrix  = identity
        batch.setBlendFunction(savedSrc, savedDst)
        batch.setColor(savedR, savedG, savedB, savedA)
        batch.begin()
    }

    // ─── Draw ──────────────────────────────────────────────────────────────────

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val tex = textureResult ?: return
        if (batch == null) return

        val a       = color.a * parentAlpha
        val prevSrc = batch.blendSrcFunc
        val prevDst = batch.blendDstFunc

        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.setColor(color.r * a, color.g * a, color.b * a, a)
        batch.draw(tex, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        batch.setBlendFunction(prevSrc, prevDst)
        batch.setColor(Color.WHITE)

        pendingFbo?.let { screen.renderPipeline.vfxPool.free(it); pendingFbo = null }
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private fun setupCamera() {
        camera.setToOrtho(false, width, height)
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()
    }

    private fun releaseCached() {
        cachedFbo?.let {
            runCatching { screen.renderPipeline.vfxPool.free(it) }
            cachedFbo = null; textureResult = null
        }
    }

    /** -------------------------------------------------------------------------
    // Оновлює pre-allocated TextureRegion без нових алокацій.
    // Явно встановлює UV для вертикального flip (FBO в OpenGL перевернутий по Y).
    // НЕ використовуємо flip() — він накопичується при повторних викликах.
    // ------------------------------------------------------------------------- */
    private fun updateRegion(region: TextureRegion, fbo: FrameBuffer) {
        region.setTexture(fbo.colorBufferTexture)
        region.u  = 0f;  region.v  = 1f   // top-left  (flipped Y)
        region.u2 = 1f;  region.v2 = 0f   // bottom-right
    }

    private inline fun Batch.withMatrix(proj: Matrix4, trans: Matrix4, block: Block) {
        tmpProj.set(projectionMatrix);  tmpTrans.set(transformMatrix)
        projectionMatrix = proj;        transformMatrix  = trans
        block()
        projectionMatrix = tmpProj;     transformMatrix  = tmpTrans
    }
}