package com.selftest.mindora.game.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.selftest.mindora.game.actors.share.APanelShareCard
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.util.log
import java.io.File

// ═════════════════════════════════════════════════════════════════════════════
//  ShareCardRenderer — малює APanelShareCard у PNG і віддає файл.
//
//  ЧОМУ FBO, А НЕ ЗНІМОК ЕКРАНА. glReadPixels дав би те, що бачить юзер:
//  вертикальну картку в скролі, обрізану вьюпортом, з кнопками і банером.
//  Тут картка взагалі ніколи не показується — вона живе у власній сцені й
//  малюється офскрін одразу в потрібному форматі 1:1.
//
//  ⚠️ ВЛАСНА СЦЕНА ПОТРІБНА НЕ ДЛЯ ЗРУЧНОСТІ, А ЗАРАДИ РІЗКОСТІ ТЕКСТУ.
//  AMsdfLabel рахує згладжування так:
//      pxPerWorld = stage.viewport.screenWidth / worldWidth
//  тобто бере масштаб зі СЦЕНИ, а не з поточної матриці. Якби картку малювали
//  в FBO 1080², лишивши її на екранній сцені (720 px на 376 юнітів), MSDF
//  вважав би масштаб 1.91 замість справжніх 2.87 — і текст поплив би.
//  Тут viewport оновлений рівно на PX, тож рахунок збігається з реальністю.
//
//  ФАЙЛ ОДИН І ТОЙ САМИЙ на кожен шер (перезаписується): кеш не росте, а
//  ділитись двома картками одночасно неможливо.
// ═════════════════════════════════════════════════════════════════════════════
class ShareCardRenderer(
    screen: AdvancedScreen,
    private val fileName: String,
) : Disposable {

    companion object {
        /** Сторона PNG у пікселях. Збігається з share.png — фон 1:1, без ресемплу. */
        private const val PX = 1080

        /** Підпапка в filesDir. Має збігатися з res/xml/provider_paths.xml. */
        private const val DIR = "share"
    }

    // ------------------------------------------------------------------------
    // Scene
    // ------------------------------------------------------------------------
    private val viewport = FitViewport(APanelShareCard.SIZE, APanelShareCard.SIZE)

    // Батч спільний з екраном — своя сцена не має володіти другим SpriteBatch.
    private val stage = Stage(viewport, screen.stageUI.batch)

    val card = APanelShareCard(screen)

    init {
        viewport.update(PX, PX, true)

        // Спершу розмір, потім stage: AdvancedGroup кличе addActorsOnGroup
        // тільки коли є і те, і те.
        card.setSize(APanelShareCard.SIZE, APanelShareCard.SIZE)
        stage.addActor(card)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * Кликати з GL-потоку (колбек кнопки — це він і є) ПІСЛЯ card.bind().
     * @return файл PNG або null, якщо щось пішло не так.
     */
    fun render(): File? {
        var fbo   : FrameBuffer? = null
        var pixmap: Pixmap?      = null

        return try {
            fbo = FrameBuffer(Pixmap.Format.RGBA8888, PX, PX, false)
            fbo.begin()

            Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            // Stage.draw() ставить лише матрицю камери, glViewport — ні.
            viewport.update(PX, PX, true)
            viewport.apply(true)

            stage.act(0f)
            stage.draw()

            // ⚠️ ДО fbo.end(): читаємо з прив'язаного фреймбуфера.
            pixmap = Pixmap.createFromFrameBuffer(0, 0, PX, PX)
            fbo.end()

            val handle = Gdx.files.local("$DIR/$fileName")
            handle.parent().mkdirs()

            // flipY: GL віддає знизу вгору, PNG зберігається зверху вниз.
            PixmapIO.writePNG(handle, pixmap, -1, true)

            handle.file()
        } catch (e: Exception) {
            log("ShareCardRenderer error: ${e.message}")
            null
        } finally {
            pixmap?.dispose()
            fbo?.dispose()
        }
    }

    // ------------------------------------------------------------------------
    // Dispose
    // ------------------------------------------------------------------------
    override fun dispose() {
        card.dispose()
        stage.dispose()   // батч не наш — Stage його не чіпає
    }
}
