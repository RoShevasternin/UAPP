package com.rbuxrds.counterds.game.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.sqrt

class ShapeDrawerUtil(batch: Batch): Disposable {

    private val disposableSet = mutableSetOf<Disposable>()

    val drawer = ShapeDrawer(batch, getRegion())

    override fun dispose() {
        disposableSet.disposeAll()
    }

    fun update() {
        drawer.update()
    }

    fun getRegion(color: Color = Color.WHITE): TextureRegion {
        return TextureRegion(getTexture(color), 0, 0, 4, 4)
    }

    // Закруглений прямокутник з вертикальним градієнтом top→bottom — «текстура»
    // під кнопки/плашки в стилі плиток меню, без пакування в атлас.
    // Малюється в scale разів більшою за UI-розмір (як атлас @2x), щоб край
    // не мив при масштабуванні під екран. Рядок за рядком: для кутових рядків
    // відступ зліва/справа = r - sqrt(r² - dy²), решта — на всю ширину.
    fun getRoundedRegion(
        width : Int, height: Int, radius: Int,
        top   : Color, bottom: Color = top,
        scale : Int = 3,
    ): TextureRegion {
        val w = width * scale; val h = height * scale; val r = radius * scale
        val pixmap = Pixmap(w, h, Pixmap.Format.RGBA8888)
        val rowColor = Color()
        for (y in 0 until h) {
            val dy = when {
                y < r     -> r - y - 0.5f
                y >= h - r -> y - (h - r) + 0.5f
                else      -> 0f
            }
            val inset = (r - sqrt((r * r - dy * dy).coerceAtLeast(0f))).toInt()
            rowColor.set(top).lerp(bottom, y / (h - 1f))
            pixmap.setColor(rowColor)
            pixmap.fillRectangle(inset, y, w - inset * 2, 1)
        }
        val texture = Texture(pixmap).also {
            it.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            pixmap.dispose()
            disposableSet.add(it)
        }
        return TextureRegion(texture)
    }

    fun getTexture(color: Color = Color.WHITE): Texture {
        val pixmap = Pixmap(4, 4, Pixmap.Format.RGBA8888).apply {
            setColor(color)
            fill()
        }
        val texture = Texture(pixmap).also {
            pixmap.dispose()
            disposableSet.add(it)
        }

        return texture
    }

}