package com.driftglass.home.game.screens.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.model.Wallpaper
import com.driftglass.home.game.actors.ui.ARect
import com.driftglass.home.game.actors.ui.ASheet
import com.driftglass.home.game.actors.ui.ATap
import com.driftglass.home.game.actors.ui.AToggle
import com.driftglass.home.game.actors.wallpaper.AThumb
import com.driftglass.home.game.platform.LauncherApp
import com.driftglass.home.game.screens.base.DgScreen
import com.driftglass.home.game.utils.Dimens
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.actor.ellipsize
import com.driftglass.home.game.utils.actor.icon
import com.driftglass.home.game.utils.actor.lbl
import com.driftglass.home.game.utils.advanced.AdvancedGroup
import com.driftglass.home.game.utils.advanced.AdvancedScreen
import com.driftglass.home.game.utils.assets
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.msdf
import com.driftglass.home.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// Спільні будівельні блоки екранів Driftglass (як класи CSS прототипу).
// ─────────────────────────────────────────────────────────────────────────────

/** .gl: скло — glass 42 % + рамка white 14 %. */
fun glass(screen: AdvancedScreen, radius: Float = Dimens.R_GLASS, fill: Color = GameColor.glass) =
    ARect(screen, radius, fill, stroke = GameColor.white_14)

/** Група з довільним вмістом: build() кличеться, коли вже є розмір. */
fun group(screen: AdvancedScreen, w: Float, h: Float, build: AdvancedGroup.() -> Unit): AdvancedGroup =
    object : AdvancedGroup() {
        override val screen = screen
        override fun addActorsOnGroup() { build() }
    }.apply { setSize(w, h) }

/** Натискна група з вмістом. */
fun tap(screen: AdvancedScreen, w: Float, h: Float, scale: Float = 0.97f, build: ATap.() -> Unit): ATap =
    object : ATap(screen, scale) {
        override fun addContent() { build() }
    }.apply { setSize(w, h) }

/** .lh: заголовок секції капсом, JetBrains Mono 600/10.5. */
fun sectionLabel(text: String) = lbl(text, msdf.label(10.5f, GameColor.dim))

/** Іконка застосунку лаунчера (.app): квадрат з іконкою системи + підпис з тінню. app = null — сама Driftglass. */
fun appIcon(screen: AdvancedScreen, app: LauncherApp?, w: Float, iconSize: Float, label: Boolean): ATap {
    val h = iconSize + if (label) px(5f + 14f) else 0f
    return tap(screen, w, h, 0.92f) {
        // розмір клітинки — ДО apply: всередині Image.apply width/height — це розміри самого Image (0),
        // через що іконки з'їжджали вліво й під підпис (Redmi, 09.10.2026)
        val cw = width; val ch = height
        val img = Image().apply { setBounds((cw - iconSize) / 2f, ch - iconSize, iconSize, iconSize); touchable = Touchable.disabled }
        if (app == null) img.drawable = TextureRegionDrawable(assets.LOGO)
        else gdxGame.appIcons.forApp(app, 144) { tex -> tex?.let { img.drawable = TextureRegionDrawable(it) } }
        addActor(img)
        if (label) {
            val name = app?.label ?: "Driftglass"
            val l = lbl(name, msdf.medium(10.5f, Color.WHITE).apply { dropShadow(0f, 1f, 3f, GameColor.black_55) }).ellipsize(width)
            l.setAlignment(Align.center); l.setPosition(0f, 0f); l.width = width
            addActor(l)
        }
    }
}

/**
 * Папка лаунчера (авто-папки HomeLayout): скляний квадрат з 4 міні-іконками 2×2 + підпис.
 * Вигляд як у MIUI / Pixel — людина впізнає папку з першого погляду.
 */
fun folderIcon(screen: AdvancedScreen, title: String, apps: List<LauncherApp>, w: Float, iconSize: Float, label: Boolean): ATap {
    val h = iconSize + if (label) px(5f + 14f) else 0f
    return tap(screen, w, h, 0.92f) {
        val cw = width; val ch = height
        val box = group(screen, iconSize, iconSize) {
            addAndFillActor(ARect(screen, iconSize * 0.27f, GameColor.white_20, stroke = GameColor.white_20))
            val pad = iconSize * 0.14f; val gap = iconSize * 0.08f
            val mini = (iconSize - pad * 2 - gap) / 2f
            apps.take(4).forEachIndexed { i, a ->
                val col = i % 2; val row = i / 2
                val img = Image().apply { setBounds(pad + col * (mini + gap), iconSize - pad - mini - row * (mini + gap), mini, mini) }
                gdxGame.appIcons.forApp(a, 144) { tex -> tex?.let { img.drawable = TextureRegionDrawable(it) } }
                addActor(img)
            }
        }
        box.setPosition((cw - iconSize) / 2f, ch - iconSize); box.touchable = Touchable.disabled
        addActor(box)
        if (label) {
            val l = lbl(title, msdf.medium(10.5f, Color.WHITE).apply { dropShadow(0f, 1f, 3f, GameColor.black_55) }).ellipsize(cw)
            l.setAlignment(Align.center); l.setPosition(0f, 0f); l.width = cw
            addActor(l)
        }
    }
}

/** Картка шпалер у сітці: мініатюра 9:16 + підпис + серце, якщо в обраному. */
fun wallpaperCard(screen: AdvancedScreen, w: Wallpaper, cw: Float, small: Boolean = false, onClick: () -> Unit): ATap {
    val ch = cw * 16f / 9f
    return tap(screen, cw, ch, 0.97f) {
        addAndFillActor(AThumb(screen, w, if (small) px(14f) else px(18f)))
        // підкладка під підпис: прозоро → 60 % чорного
        addActor(ARect(screen, if (small) px(14f) else px(18f), Color(0f, 0f, 0f, 0f), Color(0f, 0f, 0f, 0.6f), angleCss = 180f, mid = 1f, start = 0.55f).apply { setBounds(0f, 0f, width, height); touchable = Touchable.disabled })
        val pad = if (small) px(8f) else px(10f)
        val name = lbl(displayName(w), msdf.semibold(if (small) 11f else 12.5f, Color.WHITE)).ellipsize(width - pad * 2)
        if (small) {
            name.setPosition(pad, pad); addActor(name)
        } else {
            val st = lbl(L.style(w.style).uppercase(), msdf.label(9.5f, GameColor.white_70, spacing = 8f))
            st.setPosition(pad, pad); name.setPosition(pad, st.y + st.height + px(2f))
            addActor(st); addActor(name)
        }
        if (gdxGame.model.state.isFavorite(w.id)) {
            val hb = group(screen, px(28f), px(28f)) {
                addAndFillActor(ARect(screen, px(10f), GameColor.black_35))
                val ic = icon(assets.ic_heart_fill, px(15f), GameColor.rose_FF7AA8); ic.setPosition((width - ic.width) / 2f, (height - ic.height) / 2f); addActor(ic)
            }
            hb.setPosition(width - hb.width - px(8f), height - hb.height - px(8f)); hb.touchable = Touchable.disabled
            addActor(hb)
        }
    }.apply { setOnClickListener { onClick() } }
}

/** Ім'я шпалер мовою людини: каталог — бренд, створені — «Скло #N». */
fun displayName(w: Wallpaper): String = if (w.isMine) L.glassName(w.number) else w.name

/**
 * Рядок налаштувань (.it): заголовок + підпис, праворуч — перемикач або текст.
 * Перший/останній у групі заокруглені більше (як у прототипі) — задає [corner].
 */
fun settingsRow(screen: AdvancedScreen, w: Float, title: String, sub: String?, toggle: Boolean? = null, value: String? = null, onClick: () -> Unit): ATap {
    val h = if (sub != null) px(58f) else px(46f)
    return tap(screen, w, h, 0.985f) {
        addAndFillActor(ARect(screen, px(14f), GameColor.white_05))
        val pad = px(14f)
        val right: Actor? = when {
            toggle != null -> AToggle(screen, toggle)
            value != null  -> lbl(value, msdf.medium(13f, GameColor.muted))
            else           -> null
        }
        right?.let { it.setPosition(width - pad - it.width, (height - it.height) / 2f); addActor(it) }
        val tw = width - pad * 2 - (right?.width?.plus(px(10f)) ?: 0f)
        val t = lbl(title, msdf.medium(13.5f)).ellipsize(tw)
        if (sub != null) {
            val s = lbl(sub, msdf.regular(11.5f, GameColor.white_55)).ellipsize(tw)
            val total = t.height + px(2f) + s.height
            t.setPosition(pad, (height + total) / 2f - t.height); s.setPosition(pad, t.y - px(2f) - s.height)
            addActor(t); addActor(s)
        } else { t.setPosition(pad, (height - t.height) / 2f); addActor(t) }
    }.apply { setOnClickListener { onClick() } }
}

// ─────────────────────────────────────────────────────────────────────────────
// Меню знизу: список дій (довге натискання на Home / на іконці)
// ─────────────────────────────────────────────────────────────────────────────
class MenuSheet(screen: DgScreen, private val title: String?, private val items: List<Triple<TextureRegion, String, () -> Unit>>, private val dangerLast: Boolean = false) : ASheet(screen) {
    private val rowH = px(48f)
    override val contentHeight: Float get() = items.size * rowH + (if (title != null) px(34f) else 0f)
    override fun buildContent(w: Float) {
        var y = contentHeight
        title?.let { y -= px(26f); val l = lbl(it, msdf.semibold(15f)).ellipsize(w); l.setPosition(0f, y); add(l); y -= px(8f) }
        items.forEachIndexed { i, (region, text, action) ->
            val danger = dangerLast && i == items.lastIndex
            val col = if (danger) GameColor.danger_FF8A8A else Color.WHITE
            y -= rowH
            val row = tap(screen, w, rowH, 0.98f) {
                val ic = icon(region, px(19f), col); ic.setPosition(px(4f), (height - ic.height) / 2f); addActor(ic)
                val l = lbl(text, msdf.medium(14.5f, col)); l.setPosition(px(38f), (height - l.height) / 2f); addActor(l)
            }.apply { setOnClickListener { close(); action() } }
            row.setPosition(0f, y); add(row)
        }
    }
}

/** «Живим шпалерам потрібен Driftglass Home» — Apply без ролі. */
class NeedHomeSheet(screen: DgScreen, private val onStill: () -> Unit, private val onHome: () -> Unit) : ASheet(screen) {
    override val contentHeight: Float get() = px(160f)
    override fun buildContent(w: Float) {
        val t = lbl(L.needTitle, msdf.title(18f)).apply { setWrap(true); width = w; height = prefHeight }
        val b = lbl(L.needBody, msdf.regular(13f, GameColor.muted)).apply { setWrap(true); width = w; height = prefHeight }
        val bh = px(44f)
        val still = com.driftglass.home.game.actors.ui.AButton(screen, L.stillOnly, com.driftglass.home.game.actors.ui.AButton.Kind.GHOST)
        val home = com.driftglass.home.game.actors.ui.AButton(screen, L.turnOnHome, com.driftglass.home.game.actors.ui.AButton.Kind.TEAL, assets.ic_home)
        still.setBounds(0f, 0f, (w - px(10f)) * 0.42f, bh)
        home.setBounds(still.width + px(10f), 0f, w - still.width - px(10f), bh)
        still.setOnClickListener { close(); onStill() }
        home.setOnClickListener { close(); onHome() }
        b.setPosition(0f, bh + px(16f)); t.setPosition(0f, b.y + b.height + px(8f))
        add(t); add(b); add(still); add(home)
    }
}
