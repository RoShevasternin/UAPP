package com.rbuxrds.counter.game.utils.font

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PixmapPacker
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter

class FontParameter : FreeTypeFontParameter() {

    init {
        setLinear()
    }

    fun setLinear(): FontParameter {
        minFilter = Texture.TextureFilter.Linear
        magFilter = Texture.TextureFilter.Linear
        return this
    }
    fun setSize(size: Int): FontParameter {
        this.size = size
        return this
    }
    fun setCharacters(characters: CharType): FontParameter {
        this.characters = characters.chars
        return this
    }
    fun setCharacters(chars: String): FontParameter {
        this.characters = chars
        return this
    }
    fun setBorder(width: Float, color: Color): FontParameter {
        this.borderWidth = width
        this.borderColor = color
        return this
    }
    fun setShadow(offsetX: Int, offsetY: Int, color: Color): FontParameter {
        this.shadowOffsetX = offsetX
        this.shadowOffsetY = offsetY
        this.shadowColor   = color
        return this
    }

    fun copy(): FontParameter {
        return FontParameter().also { new ->

            new.size = this.size
            new.mono = this.mono
            new.hinting = this.hinting
            new.color = Color(this.color)
            new.gamma = this.gamma
            new.renderCount = this.renderCount

            new.borderWidth = this.borderWidth
            new.borderColor = Color(this.borderColor)
            new.borderStraight = this.borderStraight
            new.borderGamma = this.borderGamma

            new.shadowOffsetX = this.shadowOffsetX
            new.shadowOffsetY = this.shadowOffsetY
            new.shadowColor = Color(this.shadowColor)

            new.spaceX = this.spaceX
            new.spaceY = this.spaceY

            new.padTop = this.padTop
            new.padLeft = this.padLeft
            new.padBottom = this.padBottom
            new.padRight = this.padRight

            new.characters = this.characters
            new.kerning = this.kerning
            new.packer = this.packer
            new.flip = this.flip
            new.genMipMaps = this.genMipMaps

            new.minFilter = this.minFilter
            new.magFilter = this.magFilter
            new.incremental = this.incremental
        }
    }

    fun reset(): FontParameter {

        size = 16
        mono = false
        hinting = FreeTypeFontGenerator.Hinting.AutoMedium
        color = Color.WHITE.cpy()
        gamma = 1.8f
        renderCount = 2

        borderWidth = 0f
        borderColor = Color.BLACK.cpy()
        borderStraight = false
        borderGamma = 1.8f

        shadowOffsetX = 0
        shadowOffsetY = 0
        shadowColor = Color(0f, 0f, 0f, 0.75f)

        spaceX = 0
        spaceY = 0

        padTop = 0
        padLeft = 0
        padBottom = 0
        padRight = 0

        characters = FreeTypeFontGenerator.DEFAULT_CHARS
        kerning = true
        packer = null
        flip = false
        genMipMaps = false

        minFilter = Texture.TextureFilter.Linear
        magFilter = Texture.TextureFilter.Linear
        incremental = false

        return this
    }

    enum class CharType(val chars: String) {
        SYMBOLS       ("\"!`?'•.,;:()[]{}<>|/@\\^\$€—%-+=#_&~*’…«»❤°™\""                  ),
        NUMBERS       ("1234567890"                                                        ),
        LATIN         ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"              ),
        CYRILLIC      ("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЄЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэєюяІЇії"),

        LATIN_CYRILLIC(LATIN.chars.plus(CYRILLIC.chars)                                         ),
        ALL           (SYMBOLS.chars.plus(NUMBERS.chars).plus(LATIN.chars).plus(CYRILLIC.chars) ),
    }

}