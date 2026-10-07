package com.redwave.downloader.game.screens.sheets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.game.actors.ui.ARect
import com.redwave.downloader.game.actors.ui.ASheet
import com.redwave.downloader.game.actors.ui.ATap
import com.redwave.downloader.game.platform.LauncherApp
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.ellipsize
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.assets
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

/**
 * Довге натискання на іконку в лаунчері (Remote Config is_uninstall = true) — як у системного
 * лаунчера: «App info» і «Uninstall». app = null — сама Redwave (іконка в доку).
 * Системні апки й апки робочого профілю — лише «App info».
 */
class AppMenuSheet(
    screen: AdvancedScreen,
    private val app: LauncherApp?,
) : ASheet(screen) {

    private val optH = px(46f)
    private val canUninstall = gdxGame.bridge.canUninstall(app)
    private val optCount get() = if (canUninstall) 2 else 1

    override val contentHeight: Float get() = px(48f) + px(14f) + optCount * optH + (optCount - 1) * px(4f)

    override fun buildContent(w: Float) {
        var y = contentHeight
        val img = Image().apply { setBounds(0f, y - px(48f), px(48f), px(48f)) }
        if (app == null) img.drawable = TextureRegionDrawable(assets.LOGO)
        else gdxGame.covers.forApp(app, 144) { tex -> tex?.let { img.drawable = TextureRegionDrawable(it) } }
        add(img)
        val name = lbl(app?.label ?: Copy.APP_NAME, msdf.bold(16f)).ellipsize(w - px(62f))
        name.setPosition(px(62f), y - px(24f) - name.height / 2f)
        add(name)
        y -= px(48f) + px(14f)

        val opts = buildList<Triple<TextureRegion, String, () -> Unit>> {
            add(Triple(assets.ic_gear, Copy.Launcher.APP_INFO) { close(); gdxGame.bridge.openAppInfo(app) })
            if (canUninstall) add(Triple(assets.ic_x, Copy.Launcher.UNINSTALL) { close(); gdxGame.bridge.uninstallApp(app) })
        }
        opts.forEachIndexed { i, (ic, label, act) ->
            y -= optH
            val danger = label == Copy.Launcher.UNINSTALL
            val opt = object : ATap(screen, 0.98f) {
                override fun addContent() {
                    addAndFillActor(ARect(screen, px(14f), GameColor.white_4))
                    val col = if (danger) GameColor.pink_FF8A98 else Color.WHITE
                    val im = icon(ic, px(18f), col); im.setPosition(px(12f), (height - im.height) / 2f)
                    val l = lbl(label, msdf.semibold(14f, col)); l.setPosition(px(42f), (height - l.height) / 2f)
                    addActor(im); addActor(l)
                }
            }.onClick(act)
            opt.setBounds(0f, y, w, optH); add(opt)
            if (i < opts.lastIndex) y -= px(4f)
        }
    }
}
