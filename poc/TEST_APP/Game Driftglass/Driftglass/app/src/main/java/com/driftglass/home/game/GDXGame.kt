package com.driftglass.home.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.driftglass.home.android.AndroidBridge
import com.driftglass.home.core.i18n.L
import com.driftglass.home.core.i18n.Lang
import com.driftglass.home.core.logic.PowerPolicy
import com.driftglass.home.core.logic.ShufflePolicy
import com.driftglass.home.core.model.Shuffle
import com.driftglass.home.core.model.Wallpaper
import com.driftglass.home.game.manager.NavigationManager
import com.driftglass.home.game.manager.SoundManager
import com.driftglass.home.game.manager.SpriteManager
import com.driftglass.home.game.manager.util.SoundUtil
import com.driftglass.home.game.manager.util.SpriteUtil
import com.driftglass.home.game.manager.util.VibroUtil
import com.driftglass.home.game.model.AppModel
import com.driftglass.home.game.platform.PlatformBridge
import com.driftglass.home.game.platform.PlatformEvents
import com.driftglass.home.game.screens.AppScreen
import com.driftglass.home.game.screens.LauncherScreen
import com.driftglass.home.game.screens.OnboardingScreen
import com.driftglass.home.game.screens.SplashScreen
import com.driftglass.home.game.screens.base.DgScreen
import com.driftglass.home.game.utils.AppIconCache
import com.driftglass.home.game.utils.GameColor
import com.driftglass.home.game.utils.ShaderClock
import com.driftglass.home.game.utils.advanced.AdvancedGame
import com.driftglass.home.game.utils.disposeAll
import com.driftglass.home.game.utils.font.msdf.MsdfManager
import com.driftglass.home.game.utils.vfx.Blit
import com.driftglass.home.game.utils.vfx.VfxShaderCache
import com.driftglass.home.game.wallpaper.WallpaperRenderer
import com.driftglass.home.util.currentClassName
import com.driftglass.home.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// ═════════════════════════════════════════════════════════════════════════════
//  GDXGame — корінь GDX-частини. Android знає лише через PlatformBridge,
//  події Android → GDX приходять сюди (PlatformEvents) уже в GL-потоці.
//
//  Тут же «живе» все, що спільне для екранів: шпалери на Home, цикл дня,
//  Auto-shuffle, режим економії (fps), паралакс, мова.
// ═════════════════════════════════════════════════════════════════════════════
class GDXGame(val bridge: PlatformBridge) : AdvancedGame(), PlatformEvents {

    // ------------------------------------------------------------------------
    // Assets
    // ------------------------------------------------------------------------
    val assetsAll by lazy { SpriteUtil.All() }
    val soundUtil by lazy { SoundUtil() }
    val vibroUtil by lazy { VibroUtil() }

    // ------------------------------------------------------------------------
    // Managers
    // ------------------------------------------------------------------------
    lateinit var assetManager     : AssetManager      private set
    lateinit var navigationManager: NavigationManager private set
    lateinit var spriteManager    : SpriteManager     private set
    lateinit var soundManager     : SoundManager      private set
    lateinit var msdfManager      : MsdfManager       private set
    lateinit var wallpapers       : WallpaperRenderer private set
    val appIcons = AppIconCache()

    val coroutine = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val model = AppModel(coroutine)

    /** Асети й стан готові (SplashScreen). До цього події Android лише запам'ятовуємо. */
    var isReady = false
        private set
    private var pendingHome = false
    var appsVersion = 0
        private set

    // ── Що відкрити на екранах перегляду й Studio ───────────────────────────
    /** PreviewScreen: id шпалер. */
    var previewId: String = "velvet-aurora"
    /** StudioScreen: чернетка (копія каталогу або створених). */
    var draft: Wallpaper = com.driftglass.home.core.model.Catalog.ALL.first()

    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    // ── Енергія, паралакс ────────────────────────────────────────────────────
    var isSaverOn = false
        private set
    private var powerCheckIn = 0f
    /** Нахил телефона -1..1 (згладжений), для u_par шпалер. Нуль, якщо паралакс вимкнено. */
    val parallax = Vector2()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun create() {
        ShaderProgram.pedantic = false
        VfxShaderCache.reset()
        Blit.reset()

        assetManager      = AssetManager()
        spriteManager     = SpriteManager(assetManager)
        soundManager      = SoundManager(assetManager)
        msdfManager       = MsdfManager()
        wallpapers        = WallpaperRenderer()
        navigationManager = NavigationManager(this)

        applyLanguage()
        (bridge as? AndroidBridge)?.attach(this)
        navigationManager.navigate(SplashScreen::class.java.name)
    }

    override fun render() {
        val dt = Gdx.graphics.deltaTime
        ShaderClock.update()
        updatePower(dt)
        updateParallax(dt)
        ScreenUtils.clear(backgroundColor)
        super.render()
    }

    override fun resume() {
        super.resume()
        Blit.dispose()
    }

    override fun dispose() {
        try {
            coroutine.cancel()
            disposableSet.disposeAll()
            disposeAll(appIcons, assetManager, soundUtil, VfxShaderCache, Blit, msdfManager, wallpapers)
            super.dispose()
            log("dispose $currentClassName")
        } catch (e: Exception) {
            log("exception: ${e.message}")
        }
    }

    private fun Iterable<Disposable>.disposeAll() = forEach { it.dispose() }

    // ------------------------------------------------------------------------
    // Мова: лише англійська (рішення VELDAN 09.10.2026). uk/ru лишились у Strings на потім.
    // ------------------------------------------------------------------------
    fun applyLanguage() {
        L.lang = Lang.EN
    }

    // ------------------------------------------------------------------------
    // Режим з картки нашого сервера (кеш AdPref або DEFAULT), див. AppFlags
    // ------------------------------------------------------------------------
    val flags get() = bridge.appFlags()

    /** Потрібен екран-вимога: роль обов'язкова («наша реклама»), а її немає. */
    val needsHomeRole: Boolean get() = flags.homeRequired && !bridge.isDefaultHome()

    // ------------------------------------------------------------------------
    // Бізнес-логіка UAPP: черга старту, app_open, інтерстішал (adsmodule вирішує провайдера),
    // як у Redwave / Framefall (рішення VELDAN 15.10.2026)
    // ------------------------------------------------------------------------
    /**
     * Лише всередині застосунку: вкладки, Preview, Studio. На лаунчері (роль HOME) app_open / інтерстішалів
     * немає — там своя вкладка на «Додому» / «Недавні» (MainActivity, pl=app_open); на онбордингу й
     * екрані-вимозі — теж ні.
     */
    private val isInApp: Boolean get() = currentScreen.let { it is AppScreen || it is com.driftglass.home.game.screens.PreviewScreen || it is com.driftglass.home.game.screens.StudioScreen }

    /** Черга старту (опт-ін → app_open) уже була в цьому процесі. */
    var startupFlowDone = false
        private set

    /** Splash: чи поведе перший екран у застосунок (тоді — черга старту). Перший запуск — ні. */
    fun firstScreenIsApp(): Boolean = model.state.onboarded && !needsHomeRole && !pendingHome

    /**
     * Черга старту UAPP: опт-ін на пуші → стартовий app_open → onDone. Раз на процес: зі Splash,
     * а якщо процес стартував як лаунчер або з онбордингу — на першому вході в застосунок.
     */
    fun runStartupFlow(onDone: () -> Unit = {}) {
        if (startupFlowDone) { onDone(); return }
        startupFlowDone = true
        bridge.runStartupFlow(onDone)
    }

    /**
     * Повернулись у Driftglass / тапнули іконку. Навігація з onAppIconPressed ще в черзі GL —
     * екран перевіряємо наступним проходом черги, коли AppScreen уже стоїть.
     */
    override fun onAppForeground(awayMs: Long, fromIcon: Boolean) {
        if (!isReady || !model.state.onboarded || needsHomeRole) return
        com.driftglass.home.game.utils.runGDX {
            if (!isInApp) return@runGDX
            if (!startupFlowDone) runStartupFlow() else bridge.appOpenOnReturn()
        }
    }

    /**
     * Режим змінився на льоту (новий конфіг / інтернет). Послаблення діє одразу: роль стала
     * необов'язковою, а людина стоїть на екрані-вимозі → в апку. Посилення (роль стала
     * обов'язковою) — на наступному вході (resume / старт), щоб не викидати посеред дії.
     */
    override fun onFlagsChanged(flags: com.driftglass.home.core.config.AppFlags) {
        log("flags changed: $flags")
        if (!isReady) return
        val s = currentScreen
        if (s is OnboardingScreen && model.state.onboarded && !needsHomeRole) navigationManager.navigateRoot(AppScreen::class.java.name)
    }

    /** Перехід уперед у застосунку (вкладка, Preview, Studio) — front-інтерстішал за частотою з картки. */
    fun adFront() {
        if (!isReady || !isInApp) return
        if (maybeInterstitial()) return
        bridge.onFrontNavigation()
    }

    /** «Назад» з Preview / Studio в застосунок — back-інтерстішал. */
    fun adBack() {
        if (!isReady) return
        bridge.onBackNavigation()
    }

    /** Застосували / зберегли шпалери → лічильник; інтерстішал — коли людина побачила тост. */
    fun afterWallpaperAction() {
        bridge.onWallpaperAction()
        com.badlogic.gdx.utils.Timer.schedule(object : com.badlogic.gdx.utils.Timer.Task() {
            override fun run() { maybeInterstitial() }
        }, 1.5f)
    }

    /**
     * showInterstitial (pl=interstitial) кожні 2 дії зі шпалерами — у природній паузі: після тосту
     * або на наступному переході. Не поверх шторки; не настав час — нічого (лічильник чекає).
     */
    fun maybeInterstitial(): Boolean {
        if (!isReady || !isInApp) return false
        if ((currentScreen as? DgScreen)?.sheet != null) return false
        if (!bridge.isInterstitialDue()) return false
        bridge.showInterstitial()
        return true
    }

    // ------------------------------------------------------------------------
    // Енергія: режим економії = 30 fps, половина швидкості, менший FBO шпалер
    // ------------------------------------------------------------------------
    private fun updatePower(dt: Float) {
        powerCheckIn -= dt
        if (powerCheckIn > 0f) return
        powerCheckIn = 2f
        val b = bridge.battery()
        val on = PowerPolicy.saverOn(model.state.settings.saver, b.pct, b.charging)
        if (on != isSaverOn) {
            isSaverOn = on
            log("battery saver = $on (battery ${b.pct}%, charging ${b.charging})")
        }
        Gdx.graphics.setForegroundFPS(if (on) PowerPolicy.FPS_SAVER else PowerPolicy.FPS_NORMAL)
    }

    private fun updateParallax(dt: Float) {
        val on = model.state.settings.parallax && Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)
        // Портрет: accelX — нахил вліво/вправо, accelY — від себе / на себе (≈9.8 у вертикалі)
        val tx = if (on) MathUtils.clamp(-Gdx.input.accelerometerX / 4.5f, -1f, 1f) else 0f
        val ty = if (on) MathUtils.clamp((Gdx.input.accelerometerY - 6.5f) / 4.5f, -1f, 1f) else 0f
        val k = (dt * 3f).coerceAtMost(1f)
        parallax.x += (tx - parallax.x) * k
        parallax.y += (ty - parallax.y) * k
    }

    // ------------------------------------------------------------------------
    // Старт після Splash
    // ------------------------------------------------------------------------
    fun onReady() {
        isReady = true
        applyLanguage()
        checkScheduledShuffle()
    }

    /** Перший екран після Splash / після надання ролі. */
    fun navigateFirst() {
        val nav = navigationManager
        val home = bridge.isDefaultHome()
        // Роль надали, а онбординг не позначено: Android при наданні ролі перезапускає MainActivity
        // як HOME, і колбек ролі гине разом зі старою (Redwave, 07.10.2026). Роль наша = онбординг пройдено.
        if (!model.state.onboarded && home) {
            model.update { it.copy(onboarded = true) }
            pendingHome = false
            nav.navigateRoot(LauncherScreen::class.java.name)
            return
        }
        when {
            !model.state.onboarded || needsHomeRole -> nav.navigateRoot(OnboardingScreen::class.java.name)
            pendingHome && home    -> nav.navigateRoot(LauncherScreen::class.java.name)
            else                   -> { nav.navigateRoot(AppScreen::class.java.name); runStartupFlow() }
        }
        pendingHome = false
    }

    fun toast(text: String) {
        val s = currentScreen as? DgScreen
        if (s != null) s.showToast(text) else bridge.showToast(text)
    }

    // ------------------------------------------------------------------------
    // Шпалери: застосувати, змінити, нерухомий кадр
    // ------------------------------------------------------------------------
    /** Поставити шпалери на Home (вимикає цикл дня — людина обрала сама). Кадр на екран блокування — за налаштуванням. */
    fun applyWallpaper(id: String, alsoLock: Boolean = model.state.settings.lockStill) {
        bridge.onWallpaperAction()   // інтерстішал — на наступній паузі в застосунку (Apply веде на лаунчер)
        model.update { it.copy(applied = id, dayCycle = false, changedAt = System.currentTimeMillis()) }
        log("apply $id → Home")
        if (alsoLock && model.state.settings.lockStill) model.wallpaper(id)?.let { setStill(it, system = false, lock = true) {} }
    }

    /** Ручний (подвійний тап / кнопка) або автоматичний (розблокування / розклад) shuffle. */
    fun shuffle(auto: Boolean): Wallpaper? {
        val s = model.state
        val cur = model.homeWallpaper().id
        val next = ShufflePolicy.pickNext(ShufflePolicy.pool(s, auto), cur)
        if (next == cur) return null
        model.update { it.copy(applied = next, dayCycle = if (auto) it.dayCycle else false, changedAt = System.currentTimeMillis()) }
        log("shuffle(auto=$auto): $cur → $next")
        val w = model.wallpaper(next)
        if (w != null && s.settings.lockStill) setStill(w, system = false, lock = true) {}
        return w
    }

    private fun checkScheduledShuffle() {
        val s = model.state
        if (s.dayCycle) return
        if (ShufflePolicy.isDue(s.shuffle, s.changedAt, System.currentTimeMillis())) shuffle(auto = true)
    }

    /**
     * Нерухомий кадр у WallpaperManager. Пікселі — у GL-потоці, PNG — у фоні.
     * Розмір — екран телефона (Gdx.graphics), фаза анімації — як у прототипі (8 + seed·3).
     */
    fun setStill(w: Wallpaper, system: Boolean, lock: Boolean, onDone: (Boolean) -> Unit) {
        val pw = Gdx.graphics.width.coerceAtMost(1440); val ph = (pw * Gdx.graphics.height.toFloat() / Gdx.graphics.width).toInt()
        val pm = wallpapers.stillPixmap(w, 8f + w.seed * 3f, pw, ph)
        coroutine.launch(Dispatchers.Default) {
            val png = runCatching { wallpapers.encode(pm) }.getOrNull()
            com.driftglass.home.game.utils.runGDX { pm.dispose() }
            if (png == null) { com.driftglass.home.game.utils.runGDX { onDone(false) }; return@launch }
            bridge.setStillWallpaper(png, system, lock, onDone)
        }
    }

    // ------------------------------------------------------------------------
    // PlatformEvents
    // ------------------------------------------------------------------------
    override fun onHomePressed() {
        if (!isReady) { pendingHome = true; return }
        if (!bridge.isDefaultHome()) return
        val s = currentScreen
        if (s is LauncherScreen) s.onHomeAgain()
        else if (s !is SplashScreen) navigationManager.navigateRoot(LauncherScreen::class.java.name)
    }

    override fun onAppIconPressed() {
        pendingHome = false
        if (!isReady || !model.state.onboarded) return
        if (currentScreen is LauncherScreen) navigationManager.navigate(AppScreen::class.java.name, LauncherScreen::class.java.name)
    }

    override fun onAppsChanged() {
        appIcons.invalidate()
        appsVersion++
    }

    override fun onUnlocked() {
        if (!isReady || model.state.dayCycle) return
        if (model.state.shuffle == Shuffle.UNLOCK) shuffle(auto = true)
    }

    override fun onAppResumed() {
        if (!isReady) return
        applyLanguage()
        // Роль обов'язкова, а її зняли (Налаштування → Головний екран) — назад на екран-вимогу
        val s = currentScreen
        if (needsHomeRole && model.state.onboarded && s !is OnboardingScreen && s !is SplashScreen) {
            log("home required, role lost → requirement screen")
            navigationManager.navigateRoot(OnboardingScreen::class.java.name)
            return
        }
        checkScheduledShuffle()
        (currentScreen as? DgScreen)?.onResumed()
    }

    /** Іконка Driftglass на НАШОМУ лаунчері — відкрити застосунок. */
    fun openAppFromLauncher(tab: Int? = null) {
        navigationManager.navigate(AppScreen::class.java.name, LauncherScreen::class.java.name, tab)
        onAppForeground(awayMs = -1L, fromIcon = true)
    }
}
