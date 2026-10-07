package com.example.homelauncher

import android.app.ActivityOptions
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.UserHandle
import android.os.UserManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.homelauncher.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Collator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var launcherApps: LauncherApps
    private val appsAdapter = AppsAdapter(onClick = ::launchApp, onLongClick = ::openAppInfo)
    private var loadJob: Job? = null
    private var homePressCount = 0

    private var roleRequestStartedAt = 0L

    /**
     * Auto-open the Custom Tab on the next onResume. Armed by a HOME press (onNewIntent) and by
     * leaving to another app (onStop); disarmed right before the tab is launched, so closing the
     * tab lands on the launcher instead of re-opening it.
     */
    private var shouldLaunchCustomTab = true

    /**
     * Our own Custom Tab is what covers the launcher. Without this, onStop (fired because the TAB
     * hid us) would re-arm the flag, and closing the tab would open it again — the infinite loop
     * the flag exists to prevent. A HOME press while the tab is on top also counts as dismissing
     * it, not as "returning from another app".
     */
    private var ownTabInFront = false

    /**
     * Receives the user's answer from the system "Default home app" role dialog (API 29+).
     *
     * The actual role state decides, not the result code. An explicit Cancel is respected — no
     * second prompt. Settings are opened only when the system returned instantly without showing
     * the dialog (OEM suppression or "Don't ask again").
     *
     * Note (MIUI/HyperOS): right after the grant the system itself may show "Choose home app"
     * (MiuiResolverActivity) because of a stale preferred-home entry. The role is already ours
     * at that point; the app cannot prevent that chooser.
     */
    private val requestHomeRole =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val isDefault = isDefaultHomeApp()
            val elapsed = SystemClock.elapsedRealtime() - roleRequestStartedAt
            Log.i(TAG, "Role request result: code=${result.resultCode}, isDefault=$isDefault, after ${elapsed}ms")
            if (!isDefault && elapsed < DIALOG_SUPPRESSED_MS) openHomeSettings()
            renderStatus()
        }

    /** Pre-API 29 path and fallback: we cannot know the choice, so status is re-read in onResume. */
    private val openSettings =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            renderStatus()
        }

    /** Keeps the grid in sync with installs, removals, updates and SD-card/work-profile changes. */
    private val packageCallback = object : LauncherApps.Callback() {
        override fun onPackageAdded(packageName: String, user: UserHandle) = loadApps()
        override fun onPackageRemoved(packageName: String, user: UserHandle) = loadApps()
        override fun onPackageChanged(packageName: String, user: UserHandle) = loadApps()
        override fun onPackagesAvailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) = loadApps()
        override fun onPackagesUnavailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) = loadApps()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Wallpaper shows through the window, so system bar icons stay light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homePressCount = savedInstanceState?.getInt(KEY_HOME_PRESSES) ?: 0
        roleRequestStartedAt = savedInstanceState?.getLong(KEY_ROLE_REQUEST_AT) ?: 0L
        launcherApps = getSystemService()!!

        binding.appsGrid.layoutManager = GridLayoutManager(this, GRID_COLUMNS)
        binding.appsGrid.adapter = appsAdapter

        // Like any launcher: Back never leaves the home screen, it just returns the grid to the top.
        // When we are not the default home (opened from the app drawer), Back exits normally.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    binding.homeCardScrim.isVisible -> hideHomeCard()
                    isDefaultHomeApp() -> binding.appsGrid.smoothScrollToPosition(0)
                    else -> finish()
                }
            }
        })

        // XML android:clipToOutline is API 31+; the property works from API 21.
        binding.homeCardImage.clipToOutline = true
        binding.homeCardScrim.setOnClickListener { hideHomeCard() }
        // The card consumes its own taps, so only the scrim around it dismisses.
        binding.homeCard.setOnClickListener { openInCustomTab(CUSTOM_TAB_URL) }

        binding.setDefaultButton.setOnClickListener {
            if (isDefaultHomeApp()) openHomeSettings() else requestDefaultHome()
        }

        launcherApps.registerCallback(packageCallback, Handler(Looper.getMainLooper()))
        loadApps()

        // Prompt once per fresh start, not on every configuration change / recreation.
        if (savedInstanceState == null && !isDefaultHomeApp()) {
            requestDefaultHome()
        }
    }

    override fun onDestroy() {
        launcherApps.unregisterCallback(packageCallback)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        // The user may have changed the default launcher in Settings while we were paused.
        renderStatus()
        // Every return to the home screen (HOME from another app, HOME while on top, leaving an
        // app with Back) passes through onResume — onNewIntent is always followed by it.
        showHomeCard()

        ownTabInFront = false
        // Only as the default home: opened from the app drawer we are a normal app.
        if (shouldLaunchCustomTab && isDefaultHomeApp()) {
            shouldLaunchCustomTab = false            // FIRST — the tab's own onStop/onResume must not loop
            Log.i(TAG, "Auto-opening Custom Tab")
            openInCustomTab(CUSTOM_TAB_URL)
        } else {
            Log.i(TAG, "Landed on launcher (no auto tab)")
        }
    }

    override fun onStop() {
        super.onStop()
        // Another app covered the launcher → next return to Home opens the tab again.
        // Our own tab covering us does NOT count (see ownTabInFront).
        if (!ownTabInFront) shouldLaunchCustomTab = true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.action == Intent.ACTION_MAIN || intent.hasCategory(Intent.CATEGORY_HOME)) {
            if (!ownTabInFront) shouldLaunchCustomTab = true
        }
        if (intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME)) {
            homePressCount++
            Log.i(TAG, "HOME pressed while launcher is on top (#$homePressCount)")
            binding.appsGrid.smoothScrollToPosition(0)
            renderStatus()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_HOME_PRESSES, homePressCount)
        outState.putLong(KEY_ROLE_REQUEST_AT, roleRequestStartedAt)
    }

    /** Loads launchable activities of every profile (personal + work) off the main thread. */
    private fun loadApps() {
        loadJob?.cancel()
        loadJob = lifecycleScope.launch {
            val apps = withContext(Dispatchers.IO) { queryApps() }
            appsAdapter.submitList(apps)
            Log.d(TAG, "Loaded ${apps.size} apps")
        }
    }

    private fun queryApps(): List<AppEntry> {
        val profiles = getSystemService<UserManager>()?.userProfiles.orEmpty()
        val collator = Collator.getInstance()
        return profiles
            .flatMap { user -> launcherApps.getActivityList(null, user) }
            .filter { it.applicationInfo.packageName != packageName }
            .map { info ->
                AppEntry(
                    component = info.componentName,
                    user = info.user,
                    label = info.label.toString(),
                    icon = info.getBadgedIcon(0),
                )
            }
            .sortedWith { a, b -> collator.compare(a.label, b.label) }
    }

    private fun launchApp(app: AppEntry, view: View) {
        val bounds = Rect().also { view.getGlobalVisibleRect(it) }
        val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
        try {
            launcherApps.startMainActivity(app.component, app.user, bounds, options.toBundle())
        } catch (e: Exception) {
            // ActivityNotFoundException / SecurityException: app was just removed or disabled.
            Log.w(TAG, "Cannot launch ${app.component}", e)
            loadApps()
        }
    }

    private fun openAppInfo(app: AppEntry, view: View) {
        val bounds = Rect().also { view.getGlobalVisibleRect(it) }
        launcherApps.startAppDetailsActivity(app.component, app.user, bounds, null)
    }

    private fun isDefaultHomeApp(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService<RoleManager>()
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                return roleManager.isRoleHeld(RoleManager.ROLE_HOME)
            }
        }
        // Legacy check: which activity resolves the HOME intent by default?
        val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolved = packageManager.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolved?.activityInfo?.packageName == packageName
    }

    private fun requestDefaultHome() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService<RoleManager>()
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                roleRequestStartedAt = SystemClock.elapsedRealtime()
                requestHomeRole.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME))
                return
            }
        }
        openHomeSettings()
    }

    /** Opens the system "Default home app" screen; falls back to general Settings on odd OEM builds. */
    private fun openHomeSettings() {
        try {
            openSettings.launch(Intent(Settings.ACTION_HOME_SETTINGS))
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "ACTION_HOME_SETTINGS not available, opening Settings", e)
            openSettings.launch(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun renderStatus() {
        val isDefault = isDefaultHomeApp()
        binding.statusText.setText(
            if (isDefault) R.string.status_default else R.string.status_not_default
        )
        binding.setDefaultButton.setText(
            if (isDefault) R.string.action_change_default else R.string.action_set_default
        )
        binding.homePressText.text = getString(R.string.home_presses, homePressCount)
    }

    /** Fades the scrim in and pops the card from slightly below; restarts if already visible. */
    private fun showHomeCard() {
        val scrim = binding.homeCardScrim
        val card = binding.homeCard
        scrim.animate().cancel()
        card.animate().cancel()

        scrim.isVisible = true
        scrim.alpha = 0f
        card.alpha = 0f
        card.scaleX = CARD_START_SCALE
        card.scaleY = CARD_START_SCALE
        card.translationY = resources.displayMetrics.density * CARD_START_OFFSET_DP

        scrim.animate().alpha(1f).setDuration(CARD_ANIM_MS / 2).start()
        card.animate()
            .alpha(1f).scaleX(1f).scaleY(1f).translationY(0f)
            .setDuration(CARD_ANIM_MS)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
    }

    /**
     * Opens [url] in a Custom Tab when some installed browser supports them, otherwise hands it
     * to any app that can view it. Requires the CustomTabsService / VIEW <queries> on API 30+.
     */
    private fun openInCustomTab(url: String) {
        ownTabInFront = true
        val uri = url.toUri()
        val customTabsPackage = CustomTabsClient.getPackageName(this, null)
        try {
            if (customTabsPackage != null) {
                CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build()
                    .apply { intent.setPackage(customTabsPackage) }
                    .launchUrl(this, uri)
            } else {
                Log.i(TAG, "No Custom Tabs browser, falling back to ACTION_VIEW")
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        } catch (e: ActivityNotFoundException) {
            ownTabInFront = false      // nothing opened — we stay visible
            Log.w(TAG, "No app can open $url", e)
            Toast.makeText(this, R.string.no_browser, Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideHomeCard() {
        binding.homeCard.animate().cancel()
        binding.homeCardScrim.animate()
            .alpha(0f)
            .setDuration(CARD_ANIM_MS / 2)
            .withEndAction { binding.homeCardScrim.isVisible = false }
            .start()
    }

    private companion object {
        const val TAG = "HomeLauncher"
        const val CUSTOM_TAB_URL = "https://google.com"
        const val KEY_HOME_PRESSES = "home_presses"
        const val KEY_ROLE_REQUEST_AT = "role_request_at"
        const val GRID_COLUMNS = 4
        /** Faster than any human answer: the role dialog was never actually shown. */
        const val DIALOG_SUPPRESSED_MS = 700L
        const val CARD_ANIM_MS = 400L
        const val CARD_START_SCALE = 0.9f
        const val CARD_START_OFFSET_DP = 24f
    }
}
