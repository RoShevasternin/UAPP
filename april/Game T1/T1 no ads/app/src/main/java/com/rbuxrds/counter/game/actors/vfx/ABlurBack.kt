package com.rbuxrds.counter.game.actors.vfx

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.captureScreenShot
import com.rbuxrds.counter.game.utils.vfx.BlurEffect
import com.rbuxrds.counter.game.utils.vfx.MaskEffect
import com.rbuxrds.counter.game.utils.vfx.VfxGroup
import com.rbuxrds.counter.util.currentClassName

/**
 * Розмитий фон з маскою — один VfxGroup з двома ефектами в ланцюгу.
 *
 * ─── Раніше ─────────────────────────────────────────────────────────────────
 * AMask (VfxGroup) → pipeline 1
 *   ABlur (VfxGroup) → pipeline 2
 *     Image(screenshot)
 *
 * 2 вкладені VfxGroup = 2 pipeline = 6 FBO bind/unbind per frame.
 *
 * ─── Тепер ──────────────────────────────────────────────────────────────────
 * ABlurBack (VfxGroup) → 1 pipeline з 2 ефектами
 *   Image(screenshot)
 *
 * 1 pipeline = 3 FBO bind/unbind per frame — вдвічі менше.
 *
 * ─── Порядок ефектів ────────────────────────────────────────────────────────
 * screenshot → BlurEffect → MaskEffect → екран
 *
 * Спочатку blur (розмиваємо весь знімок), потім mask (обрізаємо краї).
 * Якщо навпаки (mask → blur) — blur розмив би краї маски.
 */
class ABlurBack(
    override val screen: AdvancedScreen,
    maskTexture: Texture? = null,
) : VfxGroup(screen) {

    private val blurEffect = BlurEffect(radius = 0f)
    private val maskEffect = MaskEffect(maskTexture)

    var radiusBlur: Float = 0f
        set(value) { blurEffect.radius = value; field = value }

    val isBlurEnabled: Boolean get() = blurEffect.radius > 0f

    /** Змінити маску після створення */
    var maskTexture: Texture?
        get()      = maskEffect.maskTexture
        set(value) { maskEffect.maskTexture = value; rerenderOnce() }

    private var isScreenshotCaptured = false

    // Перевизначаємо isStaticEffect щоб скидати isScreenshotCaptured при false
    override var isStaticEffect: Boolean
        get()      = super.isStaticEffect
        set(value) {
            if (!value) isScreenshotCaptured = false
            super.isStaticEffect = value
        }

    private val vecTmp           = Vector2()
    private val vecGroupPosition = Vector2()
    private val vecBottomLeft    = Vector2()
    private val vecTopRight      = Vector2()
    private val boundsScreenShot = Rectangle()

    private lateinit var regionScreenShot: TextureRegion

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()  // ← camera setup — обов'язково

        addEffect(blurEffect)  // 1й pass
        addEffect(maskEffect)  // 2й pass

        updateBoundsScreenShot()
        regionScreenShot = TextureRegion(
            Texture(
                boundsScreenShot.width.toInt().coerceAtLeast(1),
                boundsScreenShot.height.toInt().coerceAtLeast(1),
                Pixmap.Format.RGB888
            )
        ).apply { flip(false, true) }

        addAndFillActor(Image(regionScreenShot))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (!isBlurEnabled) return
        if (batch == null) throw Exception("Error draw: ${this::class.simpleName}")

        val needCapture = !super.isStaticEffect || !isScreenshotCaptured

        if (needCapture) {
            // flush перед glReadPixels щоб всі попередні draw команди
            // відобразились на framebuffer до захоплення знімку
            batch.flush()
            updateBoundsScreenShot()
            captureScreenShot(
                regionScreenShot,
                boundsScreenShot.x.toInt(), boundsScreenShot.y.toInt(),
                boundsScreenShot.width.toInt(), boundsScreenShot.height.toInt()
            )
            if (super.isStaticEffect) isScreenshotCaptured = true
        }

        super.draw(batch, parentAlpha)
    }

    /**
     * Захопити новий знімок і перерендерити.
     * Використовується після появи dim overlay або зміни вмісту під blur.
     */
    fun captureOnce() {
        isScreenshotCaptured = false
        rerenderOnce()
    }

    private fun updateBoundsScreenShot() {
        vecGroupPosition.set(localToStageCoordinates(vecTmp.set(0f, 0f)))
        val bl = vecBottomLeft.set(vecGroupPosition.x, vecGroupPosition.y)
        val tr = vecTopRight.set(vecGroupPosition.x + width, vecGroupPosition.y + height)
        screen.viewportUI.project(bl)
        screen.viewportUI.project(tr)
        boundsScreenShot.set(bl.x, bl.y, tr.x - bl.x, tr.y - bl.y)
    }

    override fun getRotation() = 0f
    override fun setRotation(degrees: Float)      { throw Exception("$currentClassName: NOT setRotation") }
    override fun rotateBy(amountInDegrees: Float) { throw Exception("$currentClassName: NOT rotateBy") }
    override fun getScaleX(): Float = 1f
    override fun getScaleY(): Float = 1f
}