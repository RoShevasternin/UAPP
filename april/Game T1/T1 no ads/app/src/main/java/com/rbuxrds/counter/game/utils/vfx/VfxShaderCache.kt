package com.rbuxrds.counter.game.utils.vfx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable

// ─────────────────────────────────────────────────────────────────────────────
// VfxShaderCache — глобальний кеш шейдерів
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Централізований кеш ShaderProgram об'єктів.
 *
 * Ключ = шлях до fragment shader файлу (наприклад "shader/blur/gaussianBlurFS.glsl").
 * Значення = скомпільований ShaderProgram.
 *
 * Перший виклик get() для певного шляху компілює шейдер (одноразова операція).
 * Всі наступні виклики з тим самим шляхом повертають той самий об'єкт — без
 * перекомпіляції, без дублювання в GPU пам'яті.
 *
 * Vertex shader за замовчуванням = Blit.VERT (NDC quad, v_color=1.0).
 * Якщо потрібен кастомний vertex shader — передай у get() окремо.
 *
 * ВАЖЛИВО: ніколи не dispose шейдери через VfxEffect.dispose() — вони shared.
 * Dispose відбувається один раз при виході з гри через VfxShaderCache.dispose().
 */
object VfxShaderCache: Disposable {

    private val cache = HashMap<String, ShaderProgram>()

    /**
     * Отримати ShaderProgram за шляхом до fragment shader.
     * Якщо шейдер ще не компілювався — компілює і кешує.
     * Якщо вже компілювався — повертає кешований об'єкт.
     */
    fun get(fragmentPath: String, vertexSrc: String = Blit.VERT): ShaderProgram =
        cache.getOrPut(fragmentPath) {
            val fragSrc = Gdx.files.internal(fragmentPath).readString()
            ShaderProgram(vertexSrc, fragSrc).also { shader ->
                if (!shader.isCompiled) {
                    throw IllegalStateException(
                        "VfxShaderCache: не вдалось скомпілювати '$fragmentPath':\n${shader.log}"
                    )
                }
            }
        }

    /** Dispose всіх шейдерів. Викликати тільки при виході з гри. */
    override fun dispose() {
        cache.values.forEach { runCatching { it.dispose() } }
        cache.clear()
    }
}