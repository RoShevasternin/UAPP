package com.redwave.downloader.game.manager.util

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.redwave.downloader.game.manager.SpriteManager

class SpriteUtil {

    class All {
        private fun icon(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
            ?: error("Регіон '$name' відсутній в atlas/all.atlas — перепакуй: sh ./gradlew :app:packAtlas")

        // ── Іконки (білі, тонуються actor.color) ─────────────────────────────
        val ic_ban        = icon("ic_ban")
        val ic_bell       = icon("ic_bell")
        val ic_camera     = icon("ic_camera")
        val ic_check      = icon("ic_check")
        val ic_chev_down  = icon("ic_chev_down")
        val ic_chev_left  = icon("ic_chev_left")
        val ic_chev_right = icon("ic_chev_right")
        val ic_clip       = icon("ic_clip")
        val ic_cloud      = icon("ic_cloud")
        val ic_compass    = icon("ic_compass")
        val ic_download   = icon("ic_download")
        val ic_file       = icon("ic_file")
        val ic_folder     = icon("ic_folder")
        val ic_gear       = icon("ic_gear")
        val ic_globe      = icon("ic_globe")
        val ic_heart      = icon("ic_heart")
        val ic_home       = icon("ic_home")
        val ic_image      = icon("ic_image")
        val ic_library    = icon("ic_library")
        val ic_link       = icon("ic_link")
        val ic_moon       = icon("ic_moon")
        val ic_more       = icon("ic_more")
        val ic_msg        = icon("ic_msg")
        val ic_next_fill  = icon("ic_next_fill")
        val ic_pause_fill = icon("ic_pause_fill")
        val ic_phone      = icon("ic_phone")
        val ic_play_fill  = icon("ic_play_fill")
        val ic_prev_fill  = icon("ic_prev_fill")
        val ic_repeat     = icon("ic_repeat")
        val ic_rss        = icon("ic_rss")
        val ic_scissors   = icon("ic_scissors")
        val ic_search     = icon("ic_search")
        val ic_share      = icon("ic_share")
        val ic_shield     = icon("ic_shield")
        val ic_shuffle    = icon("ic_shuffle")
        val ic_sliders    = icon("ic_sliders")
        val ic_timer      = icon("ic_timer")
        val ic_wifi_off   = icon("ic_wifi_off")
        val ic_x          = icon("ic_x")
        val ph_glyph      = icon("ph_glyph")

        // ── Текстури ─────────────────────────────────────────────────────────
        val LOGO        = SpriteManager.EnumTexture.LOGO.data.texture
        val GLOW        = SpriteManager.EnumTexture.GLOW.data.texture
        val FADE_V      = SpriteManager.EnumTexture.FADE_V.data.texture
        val LAUNCHER_BG = SpriteManager.EnumTexture.LAUNCHER_BG.data.texture
        val FEAT_LIVE   = SpriteManager.EnumTexture.FEAT_LIVE.data.texture
        val FEAT_NIGHT  = SpriteManager.EnumTexture.FEAT_NIGHT.data.texture
        val FEAT_VINYL  = SpriteManager.EnumTexture.FEAT_VINYL.data.texture

        val listOnboarding = SpriteManager.EnumTextureGroup.ONBOARDING.data.textures
    }

}
