package com.vidtools.framefall.game.screens

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.vidtools.framefall.businesModule.backend.Bt
import com.vidtools.framefall.game.actors.ui.ATabBar
import com.vidtools.framefall.game.screens.base.FramefallScreen
import com.vidtools.framefall.game.screens.base.statusScrim
import com.vidtools.framefall.game.screens.tabs.ATabPage
import com.vidtools.framefall.game.screens.tabs.DiscoverTab
import com.vidtools.framefall.game.screens.tabs.LibraryTab
import com.vidtools.framefall.game.screens.tabs.SaveTab
import com.vidtools.framefall.game.screens.tabs.StudioTab
import com.vidtools.framefall.game.utils.GameColor
import com.vidtools.framefall.game.utils.gdxGame
import com.vidtools.framefall.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AppScreen — 4 вкладки (Save / Discover / Library / Studio) в одному екрані:
// таббар не перестворюється на кожен тап, скрол кожної вкладки зберігається.
//
// Вкладка на вході — NavigationManager.key (TAB_*); інакше остання відкрита.
// «Назад»: не-Save → Save; Save → Launcher (роль наша) або moveTaskToBack.
// ─────────────────────────────────────────────────────────────────────────────
class AppScreen : FramefallScreen() {

    companion object {
        const val TAB_SAVE = 0
        const val TAB_DISCOVER = 1
        const val TAB_LIBRARY = 2
        const val TAB_STUDIO = 3

        /** Остання вкладка — щоб повернення з плеєра / Studio вело туди ж. */
        private var lastTab = TAB_SAVE

        /** screen_view кожної вкладки: Bt зі спільного словника, block — не перейменовувати після релізу. */
        private val TAB_ANALYTICS = listOf(
            Bt.TOOL    to "save_tab",
            Bt.CATALOG to "discover_tab",
            Bt.CATALOG to "library_tab",
            Bt.TOOL    to "studio_tab",
        )
    }

    private val pages = arrayOfNulls<ATabPage>(4)
    private var current = -1

    private lateinit var tabBar: ATabBar

    private val tabBarH get() = px(62f) + safeNavBarUI

    override val toastBottom: Float get() = pageBottom() + px(14f)

    override fun show() {
        super.show()
        gdxGame.bridge.openHomeScreen()   // TikTok: вхід у застосунок (у UAPP — показ MainScreen)
    }

    override fun buildContent() {
        // Зміна вкладки — front-навігація: інтерстішал за частотою з картки (або «кожні 2 відео», якщо настав)
        tabBar = ATabBar(this, safeNavBarUI) { i -> val changed = i != current; select(i); if (changed) gdxGame.adFront() }
        tabBar.setBounds(0f, 0f, worldWidth, tabBarH)

        // Смуга під статус-баром: скрол пливе ПІД нею, системна панель читається
        content.addActor(statusScrim(this, GameColor.background))
        content.addActor(tabBar)

        val start = gdxGame.navigationManager.key ?: if (gdxGame.sharedText != null) TAB_SAVE else lastTab
        select(start)
    }

    /** Низ області вкладки — верх таббару. */
    fun pageBottom(): Float = tabBarH

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
        val (bt, block) = TAB_ANALYTICS[i]
        gdxGame.bridge.screenView(bt, block)
    }

    private fun createPage(i: Int): ATabPage {
        val p = when (i) {
            TAB_DISCOVER -> DiscoverTab(this)
            TAB_LIBRARY  -> LibraryTab(this)
            TAB_STUDIO   -> StudioTab(this)
            else         -> SaveTab(this)
        }
        content.addActorAt(0, p)   // під таббаром
        p.setBounds(0f, 0f, worldWidth, worldHeight)
        return p
    }

    /** «Поділитися → Framefall», коли AppScreen уже відкритий. */
    fun consumeSharedText() {
        select(TAB_SAVE)
        (pages[TAB_SAVE] as? SaveTab)?.consumeSharedText()
    }

    override fun onBackPressed() {
        if (sheet != null) { super.onBackPressed(); return }
        when {
            current != TAB_SAVE -> select(TAB_SAVE)
            gdxGame.bridge.isDefaultHome() -> gdxGame.navigationManager.navigateRoot(LauncherScreen::class.java.name)
            else -> gdxGame.navigationManager.exit()
        }
    }
}
