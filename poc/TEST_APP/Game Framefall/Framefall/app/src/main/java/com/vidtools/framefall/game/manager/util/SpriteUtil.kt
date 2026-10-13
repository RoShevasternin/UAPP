package com.vidtools.framefall.game.manager.util

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.vidtools.framefall.game.manager.SpriteManager

class SpriteUtil {

    class All {
        private fun icon(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
            ?: error("Регіон '$name' відсутній в atlas/all.atlas — перепакуй: sh ./gradlew :app:packAtlas")

        // ── Іконки (білі, тонуються actor.color) — з прототипу Framefall (P), 96 px ──
        val ic_alert      = icon("ic_alert")
        val ic_ban        = icon("ic_ban")
        val ic_bell       = icon("ic_bell")
        val ic_camera     = icon("ic_camera")
        val ic_check      = icon("ic_check")
        val ic_chev_down  = icon("ic_chev_down")
        val ic_chev_left  = icon("ic_chev_left")
        val ic_chev_right = icon("ic_chev_right")
        val ic_chev_up    = icon("ic_chev_up")
        val ic_clip       = icon("ic_clip")
        val ic_clock      = icon("ic_clock")
        val ic_cloud      = icon("ic_cloud")
        val ic_compass    = icon("ic_compass")
        val ic_compress   = icon("ic_compress")
        val ic_download   = icon("ic_download")
        val ic_expand     = icon("ic_expand")
        val ic_ext        = icon("ic_ext")
        val ic_file       = icon("ic_file")
        val ic_film       = icon("ic_film")
        val ic_folder     = icon("ic_folder")
        val ic_fwd10      = icon("ic_fwd10")
        val ic_gauge      = icon("ic_gauge")
        val ic_gear       = icon("ic_gear")
        val ic_gif        = icon("ic_gif")
        val ic_globe      = icon("ic_globe")
        val ic_grid       = icon("ic_grid")
        val ic_heart      = icon("ic_heart")
        val ic_home       = icon("ic_home")
        val ic_image      = icon("ic_image")
        val ic_info       = icon("ic_info")
        val ic_layers     = icon("ic_layers")
        val ic_link       = icon("ic_link")
        val ic_lock       = icon("ic_lock")
        val ic_loop       = icon("ic_loop")
        val ic_moon       = icon("ic_moon")
        val ic_more       = icon("ic_more")
        val ic_msg        = icon("ic_msg")
        val ic_music      = icon("ic_music")
        val ic_mute       = icon("ic_mute")
        val ic_pause_fill = icon("ic_pause_fill")
        val ic_phone      = icon("ic_phone")
        val ic_pip        = icon("ic_pip")
        val ic_play_fill  = icon("ic_play_fill")
        val ic_plus       = icon("ic_plus")
        val ic_rew10      = icon("ic_rew10")
        val ic_scissors   = icon("ic_scissors")
        val ic_search     = icon("ic_search")
        val ic_share      = icon("ic_share")
        val ic_shield     = icon("ic_shield")
        val ic_sliders    = icon("ic_sliders")
        val ic_split      = icon("ic_split")
        val ic_star       = icon("ic_star")
        val ic_timer      = icon("ic_timer")
        val ic_trash      = icon("ic_trash")
        val ic_volume     = icon("ic_volume")
        val ic_wand       = icon("ic_wand")
        val ic_wifi       = icon("ic_wifi")
        val ic_wifi_off   = icon("ic_wifi_off")
        val ic_x          = icon("ic_x")
        val ph_glyph      = icon("ph_glyph")

        /** Іконка інструмента Studio за id (Copy.Studio.TOOLS). */
        fun toolIcon(id: String): TextureRegion = when (id) {
            "trim" -> ic_scissors; "audio" -> ic_music; "gif" -> ic_gif; "status" -> ic_split
            "frame" -> ic_image; "compress" -> ic_compress; "loop" -> ic_home; else -> ic_wand
        }

        // ── Текстури ─────────────────────────────────────────────────────────
        val LOGO        = SpriteManager.EnumTexture.LOGO.data.texture
        val GLOW        = SpriteManager.EnumTexture.GLOW.data.texture
        val FADE_V      = SpriteManager.EnumTexture.FADE_V.data.texture
        val LAUNCHER_BG = SpriteManager.EnumTexture.LAUNCHER_BG.data.texture
        val BG_SINTEL   = SpriteManager.EnumTexture.BG_SINTEL.data.texture
        val FEAT_SINTEL = SpriteManager.EnumTexture.FEAT_SINTEL.data.texture

        val listOnboarding = SpriteManager.EnumTextureGroup.ONBOARDING.data.textures
    }

}
