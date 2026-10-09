package com.driftglass.home.game.actors.wallpaper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.driftglass.home.core.model.Wallpaper
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.vfx.VfxImage
import com.driftglass.home.game.utils.vfx.effects.RoundImageEffect

/**
 * Мініатюра шпалер з заокругленими кутами. Рендериться ліниво в act() (поза batch сцени),
 * у пів-роздільності актора (до 360 px завширшки), далі — кеш WallpaperRenderer.
 */
class AThumb(screen: AdvancedScreen, private val w: Wallpaper, radius: Float) : VfxImage(screen) {

    private val fx = RoundImageEffect().apply { this.radius = radius }
    private var ready = false

    init { effect = fx }

    override fun act(delta: Float) {
        super.act(delta)
        if (ready || width < 2f || stage == null) return
        val sx = Gdx.graphics.width / screen.worldWidth
        val k = (sx * 0.5f).coerceAtMost(360f / width)
        drawable = TextureRegionDrawable(gdxGame.wallpapers.thumb(w, (width * k).toInt(), (height * k).toInt()))
        ready = true
    }
}
