package com.driftglass.home.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.driftglass.home.game.actors.ui.ATabBar
import com.driftglass.home.game.screens.base.DgScreen
import com.driftglass.home.game.screens.base.statusScrim
import com.driftglass.home.game.screens.tabs.DiscoverTab
import com.driftglass.home.game.screens.tabs.MyGlassTab
import com.driftglass.home.game.screens.tabs.SettingsTab
import com.driftglass.home.game.screens.tabs.TabPage
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.gdxGame
import com.driftglass.home.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AppScreen — застосунок: вкладки Огляд / Studio / Моє скло / Налаштування.
// Studio — окремий екран на весь екран (живі шпалери під панеллю), тому вкладка
// «Studio» відкриває StudioScreen, а не сторінку тут.
// Вкладка на вході — NavigationManager.key (TAB_*); інакше остання відкрита.
// «Назад»: не-Огляд → Огляд; Огляд → Home (роль наша) або moveTaskToBack.
// ─────────────────────────────────────────────────────────────────────────────
class AppScreen : DgScreen() {

    companion object {
        const val TAB_DISCOVER = 0
        const val TAB_STUDIO = 1
        const val TAB_MY_GLASS = 2
        const val TAB_SETTINGS = 3
        private var lastTab = TAB_DISCOVER
    }

    private val pages = arrayOfNulls<TabPage>(4)
    private var current = -1
    private lateinit var tabBar: ATabBar

    val tabBarH get() = px(60f) + safeNavBarUI
    override val toastBottom: Float get() = tabBarH + px(14f)

    override fun buildContent() {
        tabBar = ATabBar(this, safeNavBarUI) { i ->
            if (i == TAB_STUDIO) openStudio() else select(i)
        }
        tabBar.setBounds(0f, 0f, worldWidth, tabBarH)
        content.addActor(statusScrim(this, GameColor.background))
        content.addActor(tabBar)
        val start = gdxGame.navigationManager.key?.takeIf { it != TAB_STUDIO } ?: lastTab
        select(start)
    }

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

    private fun createPage(i: Int): TabPage {
        val p = when (i) {
            TAB_MY_GLASS -> MyGlassTab(this)
            TAB_SETTINGS -> SettingsTab(this)
            else         -> DiscoverTab(this)
        }
        content.addActorAt(0, p)
        p.setBounds(0f, 0f, worldWidth, worldHeight)
        return p
    }

    fun openStudio(w: com.driftglass.home.core.model.Wallpaper? = null) {
        gdxGame.draft = (w ?: gdxGame.model.homeWallpaper()).copy()
        gdxGame.navigationManager.navigate(StudioScreen::class.java.name, AppScreen::class.java.name)
    }

    fun openPreview(id: String) {
        gdxGame.previewId = id
        gdxGame.navigationManager.navigate(PreviewScreen::class.java.name, AppScreen::class.java.name)
    }

    /** Мова чи роль змінились — перебудувати сторінки (тексти беруться під час побудови). */
    fun rebuildAll(tab: Int = current) {
        gdxGame.navigationManager.navigateRoot(AppScreen::class.java.name, tab)
    }

    override fun onResumed() {
        pages.forEach { it?.onResumed() }
    }

    override fun onBackPressed() {
        if (sheet != null) { super.onBackPressed(); return }
        when {
            current != TAB_DISCOVER -> select(TAB_DISCOVER)
            gdxGame.bridge.isDefaultHome() -> gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name)
            else -> gdxGame.navigationManager.exit()
        }
    }
}
