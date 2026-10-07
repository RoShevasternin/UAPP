package com.redwave.downloader.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.redwave.downloader.game.actors.ui.AMiniPlayer
import com.redwave.downloader.game.actors.ui.ATabBar
import com.redwave.downloader.game.screens.base.RedwaveScreen
import com.redwave.downloader.game.screens.tabs.ATabPage
import com.redwave.downloader.game.screens.tabs.DiscoverTab
import com.redwave.downloader.game.screens.tabs.HomeTab
import com.redwave.downloader.game.screens.tabs.LibraryTab
import com.redwave.downloader.game.screens.tabs.RingtoneTab
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.screens.base.statusScrim
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AppScreen — 4 вкладки (Home / Discover / Library / Ringtone) в одному екрані:
// таббар і міні-плеєр не перестворюються на кожен тап, скрол кожної вкладки
// зберігається (SCREENS.md, «Структура екранів»).
//
// Вкладка на вході — NavigationManager.key (TAB_*); інакше остання відкрита.
// «Назад»: не-Home → Home; Home → Launcher (роль наша) або moveTaskToBack.
// ─────────────────────────────────────────────────────────────────────────────
class AppScreen : RedwaveScreen() {

    companion object {
        const val TAB_HOME = 0
        const val TAB_DISCOVER = 1
        const val TAB_LIBRARY = 2
        const val TAB_RINGTONE = 3

        /** Остання вкладка — щоб повернення з плеєра вело туди ж. */
        private var lastTab = TAB_HOME
    }

    private val pages = arrayOfNulls<ATabPage>(4)
    private var current = -1

    private lateinit var tabBar: ATabBar
    private lateinit var mini: AMiniPlayer

    private val tabBarH get() = px(62f) + safeNavBarUI
    private val miniH = px(56f)

    override val toastBottom: Float get() = pageBottom() + px(14f)

    override fun buildContent() {
        tabBar = ATabBar(this, safeNavBarUI) { select(it) }
        tabBar.setBounds(0f, 0f, worldWidth, tabBarH)

        mini = AMiniPlayer(this) { openPlayer() }
        mini.setBounds(px(8f), tabBarH + px(6f), worldWidth - px(16f), miniH)

        // Смуга під статус-баром: скрол пливе ПІД нею, системна панель читається (VELDAN, 07.10.2026)
        content.addActor(statusScrim(this, GameColor.background))
        content.addActor(tabBar)
        content.addActor(mini)

        val start = gdxGame.navigationManager.key ?: if (gdxGame.sharedText != null) TAB_HOME else lastTab
        select(start)
    }

    /** Висота області вкладки: від верху до міні-плеєра (або таббару). */
    fun pageBottom(): Float = tabBarH + if (mini.isVisible) miniH + px(6f) * 2 else 0f

    fun select(i: Int) {
        if (i == current) { pages[i]?.scrollToTop(); return }
        pages.getOrNull(current)?.isVisible = false
        val page = pages[i] ?: createPage(i).also { pages[i] = it }
        page.isVisible = true
        page.color.a = 0f; page.y = -px(10f)
        page.clearActions()
        page.addAction(Actions.parallel(Actions.fadeIn(0.25f), Actions.moveTo(0f, 0f, 0.3f, Interpolation.pow3Out)))
        current = i
        lastTab = i
        tabBar.select(i)
        page.onShown()
    }

    private fun createPage(i: Int): ATabPage {
        val p = when (i) {
            TAB_DISCOVER -> DiscoverTab(this)
            TAB_LIBRARY  -> LibraryTab(this)
            TAB_RINGTONE -> RingtoneTab(this)
            else         -> HomeTab(this)
        }
        content.addActorAt(0, p)   // під таббаром і міні-плеєром
        p.setBounds(0f, 0f, worldWidth, worldHeight)
        return p
    }

    override fun render(delta: Float) {
        val hasNow = gdxGame.player.now != null
        if (mini.isVisible != hasNow) mini.isVisible = hasNow
        super.render(delta)
    }

    fun openPlayer() {
        if (gdxGame.player.now == null) return
        gdxGame.navigationManager.navigate(PlayerScreen::class.java.name, AppScreen::class.java.name)
    }

    /** «Поділитися → Redwave», коли AppScreen уже відкритий. */
    fun consumeSharedText() {
        select(TAB_HOME)
        (pages[TAB_HOME] as? HomeTab)?.consumeSharedText()
    }

    override fun onBackPressed() {
        if (sheet != null) { super.onBackPressed(); return }
        when {
            current != TAB_HOME -> select(TAB_HOME)
            gdxGame.bridge.isDefaultHome() -> gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name)
            else -> gdxGame.navigationManager.exit()
        }
    }
}
