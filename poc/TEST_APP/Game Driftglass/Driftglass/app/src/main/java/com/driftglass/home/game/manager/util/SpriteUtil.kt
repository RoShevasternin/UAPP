package com.driftglass.home.game.manager.util

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.driftglass.home.game.manager.SpriteManager

class SpriteUtil {

    class All {
        private fun icon(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
            ?: error("Регіон '$name' відсутній в atlas/all.atlas — перепакуй: sh ./gradlew :app:packAtlas")

        // ── Іконки (білі 96 px, тонуються actor.color) — ../assets/tools/render_icons.py ──
        val ic_aur        = icon("ic_aur")
        val ic_back       = icon("ic_back")
        val ic_bolt       = icon("ic_bolt")
        val ic_check      = icon("ic_check")
        val ic_chev_right = icon("ic_chev_right")
        val ic_cycle      = icon("ic_cycle")
        val ic_dice       = icon("ic_dice")
        val ic_drop       = icon("ic_drop")
        val ic_gear       = icon("ic_gear")
        val ic_globe      = icon("ic_globe")
        val ic_grid       = icon("ic_grid")
        val ic_heart      = icon("ic_heart")
        val ic_heart_fill = icon("ic_heart_fill")
        val ic_hex        = icon("ic_hex")
        val ic_home       = icon("ic_home")
        val ic_image      = icon("ic_image")
        val ic_info       = icon("ic_info")
        val ic_layers     = icon("ic_layers")
        val ic_lock       = icon("ic_lock")
        val ic_mesh       = icon("ic_mesh")
        val ic_moon       = icon("ic_moon")
        val ic_plus       = icon("ic_plus")
        val ic_search     = icon("ic_search")
        val ic_shuffle    = icon("ic_shuffle")
        val ic_sliders    = icon("ic_sliders")
        val ic_sparkle    = icon("ic_sparkle")
        val ic_sun        = icon("ic_sun")
        val ic_trash      = icon("ic_trash")
        val ic_wave       = icon("ic_wave")
        val ic_x          = icon("ic_x")

        // ── Текстури ─────────────────────────────────────────────────────────
        val LOGO   = SpriteManager.EnumTexture.LOGO.data.texture
        val GLOW   = SpriteManager.EnumTexture.GLOW.data.texture
        val FADE_V = SpriteManager.EnumTexture.FADE_V.data.texture
    }
}
