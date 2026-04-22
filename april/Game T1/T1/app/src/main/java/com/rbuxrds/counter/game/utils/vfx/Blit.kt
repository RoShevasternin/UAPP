package com.rbuxrds.counter.game.utils.vfx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable

/**
 * Raw GL fullscreen blit — аналог Unity's Graphics.Blit.
 *
 * SpriteBatch малює спрайти з матрицями і vertex буфером. Для FBO-to-FBO
 * операцій це зайво — нам не потрібні трансформації, нам просто треба
 * "пропустити" пікселі однієї текстури через шейдер у другу.
 *
 * Blit вирішує це через один fullscreen quad (2 трикутники = весь екран)
 * у NDC координатах (-1..1). Mesh компілюється ОДИН раз при першому виклику
 * і живе до кінця гри. Кожен blit-виклик — це лише shader.bind() + mesh.render().
 *
 * Vertex shader [VERT] оголошує v_color = vec4(1.0) для сумісності з
 * fragment shaders що очікують v_color (всі наші шейдери множать gl_FragColor * v_color).
 */
object Blit : Disposable {

    /** -------------------------------------------------------------------------
    // Vertex shader для всіх ефектів.
    //
    // Ключові відмінності від стандартного LibGDX vertex shader:
    //   • немає u_projTrans — вершини вже в NDC-просторі, матриця не потрібна
    //   • немає a_color атрибуту — v_color = vec4(1.0) константа (без tint)
    //   • gl_Position = a_position напряму — без трансформацій
    //
    // Fragment shaders що використовують v_color (наприклад gaussianBlurFS)
    // отримують vec4(1.0) — тобто gl_FragColor * v_color = gl_FragColor * 1.0
    // що не змінює результат. Це дає повну зворотну сумісність.
    // ------------------------------------------------------------------------- */
    val VERT = """
        attribute vec4 a_position;
        attribute vec2 a_texCoord0;
        varying  vec2  v_texCoords;
        varying  vec4  v_color;
        void main() {
            v_texCoords = a_texCoord0;
            v_color     = vec4(1.0);
            gl_Position = a_position;
        }
    """.trimIndent()

    /** -------------------------------------------------------------------------
    // Fullscreen quad у NDC координатах.
    //
    // NDC (Normalized Device Coordinates): x,y від -1 до 1.
    // UV (texture coords): u,v від 0 до 1.
    //
    // Mesh живе в пам'яті весь час гри — compile once, reuse forever.
    // isStatic=true означає що дані завантажуються в GPU один раз.
    // ------------------------------------------------------------------------- */
    private val mesh: Mesh by lazy {
        Mesh(
            true, 4, 6,
            VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
            VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "${ShaderProgram.TEXCOORD_ATTRIBUTE}0")
        ).apply {
            //         NDC x,  NDC y,   U,   V
            setVertices(floatArrayOf(
                -1f, -1f,   0f, 0f,   // bottom-left
                1f, -1f,   1f, 0f,   // bottom-right
                1f,  1f,   1f, 1f,   // top-right
                -1f,  1f,   0f, 1f,   // top-left
            ))
            setIndices(shortArrayOf(0, 1, 2, 2, 3, 0)) // 2 трикутники
        }
    }

    /** -------------------------------------------------------------------------
    // FBO → FBO blit з шейдером.
    //
    // Основний метод для всіх effect passes.
    //
    // @param src      вхідний FrameBuffer (джерело пікселів)
    // @param dst      цільовий FrameBuffer (куди записати результат)
    // @param shader   шейдер для обробки
    // @param uniforms лямбда для передачі uniform параметрів і додаткових текстур
    //
    // Після виклику blend відновлюється в стандартний GL_SRC_ALPHA стан.
    // SpriteBatch стан не зачіпається взагалі — Blit не знає про batch.
    // ------------------------------------------------------------------------- */
    fun blit(
        src     : FrameBuffer,
        dst     : FrameBuffer,
        shader  : ShaderProgram,
        uniforms: (ShaderProgram) -> Unit = {}
    ) {
        dst.begin()
        clearAndRender(src.colorBufferTexture, shader, uniforms)
        dst.end()
    }

    /** -------------------------------------------------------------------------
    // Texture → FBO blit.
    //
    // Використовується для зовнішніх текстур — наприклад скріншот фону
    // в ABlurBack, або будь-яка текстура що не прийшла з FBO.
    // ------------------------------------------------------------------------- */
    fun blit(
        src     : Texture,
        dst     : FrameBuffer,
        shader  : ShaderProgram,
        uniforms: (ShaderProgram) -> Unit = {}
    ) {
        dst.begin()
        clearAndRender(src, shader, uniforms)
        dst.end()
    }

    private fun clearAndRender(texture: Texture, shader: ShaderProgram, uniforms: (ShaderProgram) -> Unit) {
        // Очищаємо ціль прозорим кольором
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Premultiplied alpha blend — FBO контент вже premultiplied
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFuncSeparate(
            GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA,  // RGB канал
            GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA   // Alpha канал
        )

        shader.bind()
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        texture.bind(0)
        shader.setUniformi("u_texture", 0)  // unit 0 = src
        uniforms(shader)                     // додаткові uniforms (напрямок blur, тощо)

        mesh.render(shader, GL20.GL_TRIANGLES)

        // Відновлюємо стандартний blend для SpriteBatch що йде після нас
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun dispose() {
        runCatching { mesh.dispose() }
    }
}