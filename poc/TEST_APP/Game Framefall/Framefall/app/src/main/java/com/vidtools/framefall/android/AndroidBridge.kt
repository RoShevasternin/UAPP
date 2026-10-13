package com.vidtools.framefall.android

import android.app.DownloadManager
import android.app.role.RoleManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.os.SystemClock
import android.os.UserManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.vidtools.framefall.MainActivity
import com.vidtools.framefall.android.studio.StudioEngine
import com.vidtools.framefall.android.video.ExoVideoPlayer
import com.vidtools.framefall.businesModule.Biz
import com.vidtools.framefall.businesModule.backend.Bt
import com.vidtools.framefall.businesModule.backend.Events
import com.vidtools.framefall.core.config.AppFlags
import com.vidtools.framefall.core.link.VideoSniffer
import com.vidtools.framefall.core.model.DownloadStatus
import com.vidtools.framefall.core.model.HomeBg
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.game.platform.DownloadProgress
import com.vidtools.framefall.game.platform.DownloadRequest
import com.vidtools.framefall.game.platform.LauncherApp
import com.vidtools.framefall.game.platform.PlatformBridge
import com.vidtools.framefall.game.platform.PlatformEvents
import com.vidtools.framefall.game.platform.ProbeResult
import com.vidtools.framefall.game.platform.StorageInfo
import com.vidtools.framefall.game.platform.StudioJob
import com.vidtools.framefall.game.platform.StudioOutput
import com.vidtools.framefall.game.platform.TextInputRequest
import com.vidtools.framefall.game.platform.VideoMeta
import com.vidtools.framefall.game.platform.VideoPlayer
import com.vidtools.framefall.game.utils.runGDX
import com.vidtools.framefall.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit

// ═════════════════════════════════════════════════════════════════════════════
//  AndroidBridge — реалізація PlatformBridge. Усе Android-специфічне тут:
//  мережа (OkHttp), DownloadManager, буфер, роль HOME, LauncherApps, метадані й
//  мініатюри відео, нативне поле вводу. Відео — ExoVideoPlayer (OES-текстура в GDX),
//  Studio — StudioEngine (Media3 Transformer).
//
//  ПОТОКИ (контракт PlatformBridge):
//    • виклики приходять із GL-потоку;
//    • UI-речі (Toast, EditText, діалоги, MediaController) — через main;
//    • мережа/диск — scope на Dispatchers.IO;
//    • колбеки назад — runGDX { }.
// ═════════════════════════════════════════════════════════════════════════════
class AndroidBridge(private val activity: MainActivity) : PlatformBridge {

    private val ctx: Context = activity.applicationContext
    private val main  = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val http = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val downloadManager = ctx.getSystemService<DownloadManager>()!!
    private val launcherApps    = ctx.getSystemService<LauncherApps>()!!
    private val userManager     = ctx.getSystemService<UserManager>()!!

    private val studio = StudioEngine(ctx)

    // ------------------------------------------------------------------------
    // GDX events: до GDXGame.create() події складаємо в чергу
    // ------------------------------------------------------------------------
    @Volatile private var events: PlatformEvents? = null
    private val pending = ConcurrentLinkedQueue<PlatformEvents.() -> Unit>()

    /** Викликає GDXGame.create(): з цього моменту події йдуть одразу, черга — зливається. */
    fun attach(events: PlatformEvents) {
        this.events = events
        while (true) {
            val e = pending.poll() ?: break
            runGDX { events.e() }
        }
    }

    /** Подія Android → GDX. Завжди в GL-потоці. */
    fun emit(block: PlatformEvents.() -> Unit) {
        val e = events
        if (e == null) pending.add(block) else runGDX { e.block() }
    }

    // ------------------------------------------------------------------------
    // Система
    // ------------------------------------------------------------------------
    @Volatile override var statusBarPx: Int = 0
    @Volatile override var navBarPx: Int = 0

    override fun showToast(text: String) {
        main.post { Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show() }
    }

    override fun vibrate(ms: Long) {
        val v = ctx.getSystemService<Vibrator>() ?: return
        if (Build.VERSION.SDK_INT >= 26) v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        else @Suppress("DEPRECATION") v.vibrate(ms)
    }

    override fun isOnline(): Boolean {
        val cm = ctx.getSystemService<ConnectivityManager>() ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun openUrl(url: String) {
        main.post {
            try {
                activity.startInternal(Intent(Intent.ACTION_VIEW, url.toUri()))
            } catch (e: ActivityNotFoundException) {
                log("openUrl: no app for $url")
            }
        }
    }

    override fun moveToBack() {
        main.post { activity.moveTaskToBack(true) }
    }

    override fun storageInfo(): StorageInfo {
        val dir  = Environment.getExternalStorageDirectory()
        val stat = runCatching { StatFs(dir.path) }.getOrNull() ?: return StorageInfo(0, 0)
        return StorageInfo(stat.availableBytes, stat.totalBytes)
    }

    // ------------------------------------------------------------------------
    // Роль HOME (код з poc/concept/HomeLauncher-PoC)
    // ------------------------------------------------------------------------
    private var roleCallback: ((Boolean) -> Unit)? = null
    private var roleRequestedAt = 0L

    private val requestHomeRole =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val isHome  = isDefaultHome()
            val elapsed = SystemClock.elapsedRealtime() - roleRequestedAt
            log("Role HOME result: code=${result.resultCode}, isHome=$isHome, after ${elapsed}ms")
            // Діалог не показався взагалі (OEM / «Більше не питати») — відкриваємо налаштування
            if (!isHome && elapsed < DIALOG_SUPPRESSED_MS) openHomeSettings() else deliverRole(isHome)
        }

    private val openSettings =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            deliverRole(isDefaultHome())
        }

    private fun deliverRole(isHome: Boolean) {
        val cb = roleCallback ?: return
        roleCallback = null
        runGDX { cb(isHome) }
    }

    override fun isDefaultHome(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = ctx.getSystemService<RoleManager>()
            if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_HOME)) return rm.isRoleHeld(RoleManager.ROLE_HOME)
        }
        val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        return ctx.packageManager.resolveActivity(home, PackageManager.MATCH_DEFAULT_ONLY)
            ?.activityInfo?.packageName == ctx.packageName
    }

    override fun requestDefaultHome(onResult: (isHome: Boolean) -> Unit) {
        main.post {
            roleCallback = onResult
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val rm = ctx.getSystemService<RoleManager>()
                if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_HOME)) {
                    roleRequestedAt = SystemClock.elapsedRealtime()
                    activity.internalNavigation = true
                    requestHomeRole.launch(rm.createRequestRoleIntent(RoleManager.ROLE_HOME))
                    return@post
                }
            }
            openHomeSettings()
        }
    }

    /** Налаштування → «Головний екран» (Settings та debug-кнопка). Без колбеку: стан ролі перевіряє resume. */
    override fun openHomeAppSettings() {
        main.post {
            val intents = listOf(
                Intent(Settings.ACTION_HOME_SETTINGS),
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
                Intent(Settings.ACTION_SETTINGS),
            )
            for (i in intents) {
                try { activity.startInternal(i); return@post } catch (e: ActivityNotFoundException) { }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Конфіг з нашого сервера (ServerConfig)
    // ------------------------------------------------------------------------
    /** Офлайн — AppFlags.DEFAULT (ServerConfig.flags). */
    override fun appFlags(): AppFlags = ServerConfig.flags
    override fun hasConfig(): Boolean = ServerConfig.hasConfig()
    override fun loadConfig(timeoutMs: Long, onDone: () -> Unit) {
        main.post {
            var done = false
            fun finish(why: String) {
                if (done) return
                done = true
                log("config for splash: $why")
                runGDX { onDone() }
            }
            main.postDelayed({ finish("timeout ${timeoutMs}ms") }, timeoutMs)
            ServerConfig.refresh(force = false) { finish("done") }   // уже йде / щойно був — без другого запиту
        }
    }
    override fun configDebug(): String = ServerConfig.debugLine()
    override fun isAdMode(): Boolean = ServerConfig.isAdMode()

    /** Перезапуск процесу: новий таск через трамплін (він сам вирішить — home-таск чи звичайний). */
    override fun setAdMode(on: Boolean) {
        main.post {
            ServerConfig.setAdMode(on)
            val restart = Intent.makeRestartActivityTask(ComponentName(ctx, com.vidtools.framefall.LauncherTrampoline::class.java))
            ctx.startActivity(restart)
            Runtime.getRuntime().exit(0)
        }
    }

    override val appVersion: String get() = runCatching {
        ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "?"
    }.getOrDefault("?")

    private fun openHomeSettings() {
        try {
            activity.internalNavigation = true; openSettings.launch(Intent(Settings.ACTION_HOME_SETTINGS))
        } catch (e: ActivityNotFoundException) {
            activity.internalNavigation = true; openSettings.launch(Intent(Settings.ACTION_SETTINGS))
        }
    }

    // ------------------------------------------------------------------------
    // Буфер
    // ------------------------------------------------------------------------
    /** Лише за дією користувача (Paste). Android 12+ покаже системний тост — це очікувано. */
    override fun readClipboardText(): String? {
        val cm = ctx.getSystemService<ClipboardManager>() ?: return null
        return runCatching {
            cm.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(ctx)?.toString()
        }.getOrNull()
    }

    /** Встановлює MainActivity: ClipWatcher тримає ClipGate. */
    var clipWatcher: ClipWatcher? = null

    override fun dismissClipLink(url: String) {
        main.post { clipWatcher?.dismiss(url) }
    }

    // ------------------------------------------------------------------------
    // Мережа
    // ------------------------------------------------------------------------
    override fun probe(url: String, onResult: (ProbeResult) -> Unit) {
        scope.launch {
            val result = runCatching {
                val req = Request.Builder().url(url)
                    .header("Range", "bytes=0-63")
                    .header("User-Agent", USER_AGENT)
                    .build()
                http.newCall(req).execute().use { resp ->
                    val bytes  = resp.body.byteStream().use { s -> ByteArray(64).let { b -> val n = s.read(b); if (n > 0) b.copyOf(n) else ByteArray(0) } }
                    val type   = resp.header("Content-Type")
                    val cd     = resp.header("Content-Disposition")
                    val final  = resp.request.url.toString()
                    val total  = resp.header("Content-Range")?.substringAfterLast('/')?.toLongOrNull()
                        ?: resp.header("Content-Length")?.toLongOrNull()?.takeIf { resp.code == 200 } ?: -1L
                    val name   = resp.request.url.pathSegments.lastOrNull()
                    val decision = VideoSniffer.decide(type, cd, name, bytes)
                    log("probe $url → ${resp.code} $type ${decision.reason} final=$final size=$total")
                    ProbeResult(decision, final, total, resp.code)
                }
            }.getOrElse { e ->
                log("probe failed: ${e.message}")
                ProbeResult(VideoSniffer.Decision(false, null, null, "network: ${e.message}"), url, -1L, -1)
            }
            runGDX { onResult(result) }
        }
    }

    override fun fetchText(url: String, onResult: (Result<String>) -> Unit) {
        scope.launch {
            val result = runCatching {
                val req = Request.Builder().url(url).header("User-Agent", USER_AGENT).build()
                http.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) error("HTTP ${resp.code}")
                    // raw.githubusercontent віддає .rss/.json як text/plain — тип не перевіряємо
                    resp.body.string()
                }
            }
            runGDX { onResult(result) }
        }
    }

    override fun fetchBytes(url: String, onResult: (ByteArray?) -> Unit) {
        scope.launch {
            val bytes = runCatching {
                val dir  = File(ctx.cacheDir, "img").apply { mkdirs() }
                val file = File(dir, Integer.toHexString(url.hashCode()) + "_" + url.length)
                if (file.exists() && file.length() > 0) return@runCatching file.readBytes()
                val req = Request.Builder().url(url).header("User-Agent", USER_AGENT).build()
                http.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) error("HTTP ${resp.code}")
                    resp.body.bytes().also { file.writeBytes(it) }
                }
            }.onFailure { log("fetchBytes $url: ${it.message}") }.getOrNull()
            runGDX { onResult(bytes) }
        }
    }

    // ------------------------------------------------------------------------
    // Завантаження (DownloadManager)
    // ------------------------------------------------------------------------
    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id < 0) return
            scope.launch {
                val p = queryDownloads(longArrayOf(id)).firstOrNull()
                val local = localUriOf(id)
                val ok = p?.status == DownloadStatus.DONE && local != null
                log("download $id finished ok=$ok uri=$local")
                emit { onDownloadFinished(id, ok, local) }
            }
        }
    }

    init {
        // ACTION_DOWNLOAD_COMPLETE шле системний провайдер (інший uid) → на 33+ лише EXPORTED
        ContextCompat.registerReceiver(
            ctx, downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    override fun enqueueDownload(req: DownloadRequest): Long {
        val name = uniqueFileName(req.fileName)
        val r = DownloadManager.Request(req.url.toUri())
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "$FOLDER/$name")
            .setTitle(req.title)
            .setDescription("Framefall")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .addRequestHeader("User-Agent", USER_AGENT)
        req.mimeType?.let { r.setMimeType(it) }
        val id = downloadManager.enqueue(r)
        log("enqueue $id ${req.url} → Movies/$FOLDER/$name")
        return id
    }

    /** DownloadManager на однакове ім'я поводиться по-різному на прошивках — робимо «name (2).mp4» самі. */
    private fun uniqueFileName(fileName: String): String {
        val clean = fileName.replace(Regex("""[\\/:*?"<>|]"""), "_").ifBlank { "video.mp4" }
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), FOLDER)
        if (!File(dir, clean).exists()) return clean
        val base = clean.substringBeforeLast('.'); val ext = clean.substringAfterLast('.', "")
        var n = 2
        while (File(dir, "$base ($n).$ext").exists()) n++
        return "$base ($n).$ext"
    }

    override fun cancelDownload(id: Long) {
        downloadManager.remove(id)
    }

    override fun queryDownloads(ids: LongArray): List<DownloadProgress> {
        if (ids.isEmpty()) return emptyList()
        val out = ArrayList<DownloadProgress>(ids.size)
        runCatching {
            downloadManager.query(DownloadManager.Query().setFilterById(*ids))?.use { c ->
                val iId   = c.getColumnIndexOrThrow(DownloadManager.COLUMN_ID)
                val iDone = c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val iTot  = c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val iSt   = c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                val iRe   = c.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)
                val iUri  = c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                while (c.moveToNext()) {
                    val status = when (c.getInt(iSt)) {
                        DownloadManager.STATUS_PENDING    -> DownloadStatus.QUEUED
                        DownloadManager.STATUS_RUNNING    -> DownloadStatus.RUNNING
                        DownloadManager.STATUS_PAUSED     -> DownloadStatus.PAUSED
                        DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.DONE
                        else                              -> DownloadStatus.FAILED
                    }
                    out += DownloadProgress(c.getLong(iId), c.getLong(iDone), c.getLong(iTot), status, c.getInt(iRe), c.getString(iUri))
                }
            }
        }.onFailure { log("queryDownloads: ${it.message}") }
        return out
    }

    private fun localUriOf(id: Long): String? = runCatching {
        downloadManager.query(DownloadManager.Query().setFilterById(id))?.use { c ->
            if (c.moveToFirst()) c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)) else null
        }
    }.getOrNull()

    // ------------------------------------------------------------------------
    // Відео: плеєри (ExoPlayer → OES-текстура)
    // ------------------------------------------------------------------------
    private val players = java.util.Collections.synchronizedSet(LinkedHashSet<ExoVideoPlayer>())

    override fun createVideoPlayer(): VideoPlayer = ExoVideoPlayer(ctx) { players.remove(it) }.also { players.add(it) }

    /** GL-потік, GDX pause(): GL-контекст може зникнути — плеєри звільняють текстури й стають на паузу. */
    fun onGlPause() {
        players.toList().forEach { it.onGlPause() }
    }

    override fun builtInBackground(key: String): String =
        "asset:///video/bg_${if (HomeBg.isBuiltIn(key)) key else HomeBg.ORION}.mp4"

    // ------------------------------------------------------------------------
    // Файли: мініатюри, метадані, смуга кадрів
    // ------------------------------------------------------------------------
    override fun loadThumbBytes(video: Video, onResult: (ByteArray?) -> Unit) {
        scope.launch {
            val bytes = runCatching {
                video.thumbPath?.let { p -> File(p).takeIf { it.exists() }?.readBytes() }
                    ?: video.thumbAsset?.let { a -> ctx.assets.open("textures/thumbs/$a.jpg").use { it.readBytes() } }
                    ?: video.localUri?.takeIf { video.isVideo }?.let { uri ->
                        retriever(uri)?.let { mmr ->
                            try { mmr.getFrameAtTime(thumbTimeUs(mmr))?.let { bmp -> saveThumb(ctx, video.id, bmp)?.let { File(it).readBytes() } } }
                            finally { mmr.release() }
                        }
                    }
            }.getOrNull()
            runGDX { onResult(bytes) }
        }
    }

    override fun readVideoMeta(localUri: String, videoId: String, onResult: (VideoMeta?) -> Unit) {
        scope.launch {
            val meta = runCatching {
                val mmr = retriever(localUri) ?: return@runCatching null
                try {
                    val title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)?.trim()?.ifEmpty { null }
                    val dur   = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                    val mime  = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                    val hasV  = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO) == "yes"
                    var w = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
                    var h = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
                    val rot = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
                    if (rot == 90 || rot == 270) { val t = w; w = h; h = t }
                    val thumb = if (hasV) mmr.getFrameAtTime(thumbTimeUs(mmr))?.let { saveThumb(ctx, videoId, it) } else null
                    VideoMeta(title, dur, w, h, mime, fileSize(localUri), hasV, thumb)
                } finally { mmr.release() }
            }.onFailure { log("readVideoMeta $localUri: ${it.message}") }.getOrNull()
            runGDX { onResult(meta) }
        }
    }

    override fun stripFrames(video: Video, count: Int, onResult: (List<ByteArray>) -> Unit) {
        scope.launch {
            val frames = runCatching {
                val dir = File(ctx.cacheDir, "strips").apply { mkdirs() }
                val cached = (0 until count).map { File(dir, "${video.id}_${count}_$it.jpg") }
                if (cached.all { it.exists() }) return@runCatching cached.map { it.readBytes() }
                val mmr = retriever(video.localUri ?: return@runCatching emptyList()) ?: return@runCatching emptyList()
                try {
                    val dur = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: video.durationMs).coerceAtLeast(1L)
                    cached.mapIndexedNotNull { i, f ->
                        val us = dur * 1000L * (2 * i + 1) / (2 * count)
                        val bmp = (if (Build.VERSION.SDK_INT >= 27) mmr.getScaledFrameAtTime(us, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, 160, 160)
                                   else mmr.getFrameAtTime(us, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)) ?: return@mapIndexedNotNull null
                        val small = if (bmp.width > 160) Bitmap.createScaledBitmap(bmp, 160, (bmp.height * 160f / bmp.width).toInt().coerceAtLeast(1), true).also { if (it !== bmp) bmp.recycle() } else bmp
                        ByteArrayOutputStream().use { out -> small.compress(Bitmap.CompressFormat.JPEG, 80, out); small.recycle(); out.toByteArray() }
                            .also { f.writeBytes(it) }
                    }
                } finally { mmr.release() }
            }.onFailure { log("stripFrames: ${it.message}") }.getOrDefault(emptyList())
            runGDX { onResult(frames) }
        }
    }

    /** Кадр для мініатюри: 10 % тривалості, але не далі 5 с (у фільмів перші секунди — титри чорним). */
    private fun thumbTimeUs(mmr: MediaMetadataRetriever): Long {
        val dur = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        return (dur * 100L).coerceIn(0L, 5_000_000L).let { if (dur in 1..2000) 0L else it }
    }

    private fun retriever(uri: String): MediaMetadataRetriever? = runCatching {
        MediaMetadataRetriever().apply {
            val u = Uri.parse(uri)
            if (u.scheme == "file" || u.scheme == null) setDataSource(u.path ?: uri) else setDataSource(ctx, u)
        }
    }.onFailure { log("retriever $uri: ${it.message}") }.getOrNull()

    private fun fileSize(uri: String): Long {
        val u = Uri.parse(uri)
        return if (u.scheme == "file") File(u.path ?: return 0).length() else 0L
    }

    override fun hasAudio(video: Video): Boolean {
        val mmr = retriever(video.localUri ?: return false) ?: return false
        return try { mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO) == "yes" } catch (e: Exception) { true } finally { mmr.release() }
    }

    override fun fileExists(localUri: String?): Boolean {
        val u = Uri.parse(localUri ?: return false)
        return if (u.scheme == "file") File(u.path ?: return false).exists() else true
    }

    override fun shareVideo(video: Video) {
        val uri = video.localUri ?: return
        main.post {
            runCatching {
                val u = Uri.parse(uri)
                val content = if (u.scheme == "file") FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", File(u.path!!)) else u
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeOf(video)
                    putExtra(Intent.EXTRA_STREAM, content)
                    putExtra(Intent.EXTRA_TITLE, video.title)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                activity.startInternal(Intent.createChooser(intent, video.title))
            }.onFailure { log("shareVideo: ${it.message}") }
        }
    }

    /** Кадр і GIF не грає наш плеєр — відкриваємо системним переглядачем (Галерея). */
    override fun openExternally(video: Video) {
        val uri = video.localUri ?: return
        main.post {
            runCatching {
                val u = Uri.parse(uri)
                val content = if (u.scheme == "file") FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", File(u.path!!)) else u
                activity.startInternal(Intent(Intent.ACTION_VIEW).setDataAndType(content, mimeOf(video)).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
            }.onFailure { log("view: ${it.message}") }
        }
    }

    private fun mimeOf(v: Video) = when (v.format.uppercase()) {
        "M4A" -> "audio/mp4"; "MP3" -> "audio/mpeg"; "GIF" -> "image/gif"; "JPG" -> "image/jpeg"
        "WEBM" -> "video/webm"; "MKV" -> "video/x-matroska"; "MOV" -> "video/quicktime"; else -> "video/mp4"
    }

    override fun deleteVideo(video: Video, onResult: (Boolean) -> Unit) {
        scope.launch {
            val ok = video.localUri?.let { deleteUri(it) } ?: true
            if (ok) video.thumbPath?.let { File(it).delete() }
            runGDX { onResult(ok) }
        }
    }

    override val folderLabel: String get() = "Internal storage / Movies / $FOLDER"

    /**
     * Видалити файл НАСПРАВДІ: спершу File.delete (власник — ми, FUSE пускає), потім рядок
     * MediaStore за шляхом (інакше в Галереї лишається «привид»). Успіх = файла немає.
     */
    private fun deleteUri(uri: String): Boolean = runCatching {
        val u = Uri.parse(uri)
        if (u.scheme != "file") return@runCatching ctx.contentResolver.delete(u, null, null) > 0
        val f = File(u.path ?: return@runCatching false)
        val deleted = f.delete()
        val rows = runCatching {
            ctx.contentResolver.delete(MediaStore.Files.getContentUri("external"), "${MediaStore.MediaColumns.DATA}=?", arrayOf(f.absolutePath))
        }.getOrDefault(0)
        val gone = !f.exists()
        log("delete ${f.name}: file=$deleted mediaStore=$rows gone=$gone")
        gone
    }.onFailure { log("delete $uri: ${it.message}") }.getOrDefault(false)

    // ------------------------------------------------------------------------
    // Studio
    // ------------------------------------------------------------------------
    override fun runStudio(job: StudioJob, onProgress: (Float) -> Unit, onResult: (Result<List<StudioOutput>>) -> Unit) {
        studio.run(job, { p -> runGDX { onProgress(p) } }) { r -> runGDX { onResult(r) } }
    }

    override fun cancelStudio() = studio.cancel()

    // ------------------------------------------------------------------------
    // Лаунчер
    // ------------------------------------------------------------------------
    private val packageCallback = object : LauncherApps.Callback() {
        override fun onPackageAdded(packageName: String, user: android.os.UserHandle) { appsCache = null; emit { onAppsChanged() } }
        override fun onPackageRemoved(packageName: String, user: android.os.UserHandle) { appsCache = null; emit { onAppsChanged() } }
        override fun onPackageChanged(packageName: String, user: android.os.UserHandle) { appsCache = null; emit { onAppsChanged() } }
        override fun onPackagesAvailable(packageNames: Array<out String>, user: android.os.UserHandle, replacing: Boolean) { appsCache = null; emit { onAppsChanged() } }
        override fun onPackagesUnavailable(packageNames: Array<out String>, user: android.os.UserHandle, replacing: Boolean) { appsCache = null; emit { onAppsChanged() } }
    }

    init {
        launcherApps.registerCallback(packageCallback, main)
        scope.launch { queryApps() }      // прогрів кешу для дока й сітки
    }

    /**
     * Список апок кешуємо: LauncherApps з сотнею пакетів — це ~1–1.5 с у GL-потоці
     * (заміряно 07.10.2026: перший показ лаунчера був чорний). Прогрів — у фоні на старті,
     * скидання — у packageCallback.
     */
    @Volatile private var appsCache: List<LauncherApp>? = null
    private fun apps(): List<LauncherApp> = appsCache ?: queryApps()

    override fun listApps(): List<LauncherApp> = apps()

    private fun queryApps(): List<LauncherApp> = runCatching {
        userManager.userProfiles.flatMap { user ->
            val serial = userManager.getSerialNumberForUser(user)
            launcherApps.getActivityList(null, user).map { info ->
                LauncherApp(info.componentName.packageName, info.componentName.className, info.label.toString(), serial)
            }
        }.filter { it.packageName != ctx.packageName }
            .sortedBy { it.label.lowercase() }
    }.getOrDefault(emptyList()).also { appsCache = it }

    override fun appIconPng(app: LauncherApp, sizePx: Int, onResult: (ByteArray?) -> Unit) {
        scope.launch {
            val bytes = runCatching {
                val user = userManager.getUserForSerialNumber(app.userSerial) ?: return@runCatching null
                val info = launcherApps.getActivityList(app.packageName, user)
                    .firstOrNull { it.componentName.className == app.activityName } ?: return@runCatching null
                val d = info.getBadgedIcon(0)
                val bmp = createBitmap(sizePx, sizePx)
                d.setBounds(0, 0, sizePx, sizePx)
                d.draw(Canvas(bmp))
                ByteArrayOutputStream().use { out -> bmp.compress(Bitmap.CompressFormat.PNG, 100, out); bmp.recycle(); out.toByteArray() }
            }.getOrNull()
            runGDX { onResult(bytes) }
        }
    }

    override fun launchApp(app: LauncherApp) {
        main.post {
            runCatching {
                val user = userManager.getUserForSerialNumber(app.userSerial)
                launcherApps.startMainActivity(ComponentName(app.packageName, app.activityName), user, null, null)
            }.onFailure { log("launchApp ${app.packageName}: ${it.message}") }
        }
    }

    override fun dockApps(): List<LauncherApp?> = listOf(
        defaultAppFor(Intent(Intent.ACTION_DIAL)),
        defaultAppFor(Intent(Intent.ACTION_SENDTO, "smsto:".toUri())),
        defaultAppFor(Intent(Intent.ACTION_VIEW, "https://example.com".toUri())),
        defaultAppFor(Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)),
    )

    // ── Довге натискання: App info / Uninstall (AppFlags.isUninstall) ──
    /** Системні апки й апки робочого профілю не видаляємо — лише «App info». */
    override fun canUninstall(app: LauncherApp?): Boolean {
        if (app == null) return true
        val me = userManager.getSerialNumberForUser(android.os.Process.myUserHandle())
        if (app.userSerial != me) return false
        val ai = runCatching { ctx.packageManager.getApplicationInfo(app.packageName, 0) }.getOrNull() ?: return false
        return ai.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM == 0
    }

    override fun openAppInfo(app: LauncherApp?) {
        main.post {
            runCatching {
                activity.internalNavigation = true
                if (app == null) {
                    activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", ctx.packageName, null)))
                } else {
                    val user = userManager.getUserForSerialNumber(app.userSerial)
                    launcherApps.startAppDetailsActivity(ComponentName(app.packageName, app.activityName), user, null, null)
                }
            }.onFailure { log("openAppInfo: ${it.message}") }
        }
    }

    /** Системний діалог видалення (REQUEST_DELETE_PACKAGES у маніфесті, API 28+). */
    override fun uninstallApp(app: LauncherApp?) {
        val pkg = app?.packageName ?: ctx.packageName
        main.post {
            runCatching { activity.startInternal(Intent(Intent.ACTION_DELETE, Uri.fromParts("package", pkg, null))) }
                .onFailure { log("uninstall $pkg: ${it.message}") }
        }
    }

    /** Дефолтна апка для дії (Dialer/SMS/камера) — для дока лаунчера. */
    fun defaultAppFor(intent: Intent): LauncherApp? {
        val ri = ctx.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) ?: return null
        var pkg = ri.activityInfo?.packageName ?: return null
        if (pkg == "android") {
            // Дефолту немає (системний «вибір апки») — беремо першу апку, що вміє цю дію
            pkg = ctx.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .firstOrNull { it.activityInfo.packageName != "android" }?.activityInfo?.packageName ?: return null
        }
        val me = userManager.getSerialNumberForUser(android.os.Process.myUserHandle())
        return apps().firstOrNull { it.packageName == pkg && it.userSerial == me }
    }

    // ------------------------------------------------------------------------
    // Нативне поле вводу поверх GL
    // ------------------------------------------------------------------------
    @Volatile var isTextInputActive = false
        private set
    private var inputDone: ((String, Boolean) -> Unit)? = null
    private var inputWatcher: TextWatcher? = null

    override fun beginTextInput(req: TextInputRequest, onChange: (String) -> Unit, onDone: (String, Boolean) -> Unit) {
        main.post {
            val et = activity.binding.input
            inputWatcher?.let { et.removeTextChangedListener(it) }
            inputDone = onDone
            isTextInputActive = true

            et.layoutParams = (et.layoutParams as FrameLayout.LayoutParams).apply {
                width = req.wPx; height = req.hPx; leftMargin = req.xPx; topMargin = req.yPx
            }
            et.setTextSize(TypedValue.COMPLEX_UNIT_PX, req.textSizePx)
            et.hint = req.hint
            et.inputType = if (req.isUrl) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                           else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            et.imeOptions = if (req.isUrl) EditorInfo.IME_ACTION_GO else EditorInfo.IME_ACTION_SEARCH
            et.setText(req.text)
            et.setSelection(et.text.length)
            et.visibility = View.VISIBLE
            et.requestFocus()

            val w = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) { val t = s?.toString().orEmpty(); runGDX { onChange(t) } }
            }
            inputWatcher = w
            et.addTextChangedListener(w)
            et.setOnEditorActionListener { _, _, _ -> finishTextInput(submitted = true); true }
            et.setOnFocusChangeListener { _, has -> if (!has && isTextInputActive) finishTextInput(submitted = false) }

            activity.getSystemService<InputMethodManager>()?.showSoftInput(et, 0)
        }
    }

    override fun endTextInput() = finishTextInput(submitted = false)

    private fun finishTextInput(submitted: Boolean) {
        main.post {
            if (!isTextInputActive) return@post
            isTextInputActive = false
            val et = activity.binding.input
            val text = et.text.toString()
            activity.getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(et.windowToken, 0)
            inputWatcher?.let { et.removeTextChangedListener(it) }
            inputWatcher = null
            et.onFocusChangeListener = null
            et.clearFocus()
            et.visibility = View.GONE
            val cb = inputDone; inputDone = null
            runGDX { cb?.invoke(text, submitted) }
        }
    }

    // ------------------------------------------------------------------------
    // Бізнес-логіка UAPP: черга старту, реклама (adsmodule), події
    // ------------------------------------------------------------------------
    override fun runStartupFlow(onDone: () -> Unit) {
        main.post {
            var done = false
            val finish = { if (!done) { done = true; runGDX { onDone() } } }
            if (activity.isFinishing) { finish(); return@post }
            // Порядок як у LoaderScreen еталона: опт-ін → стартовий app_open → далі (у всіх гілках)
            log("startup flow: opt-in → app_open")
            Biz.runStartupFlow(
                activity = activity,
                requestPermission = { onResult -> activity.requestPushPermission(onResult) },
                openGateAndContinue = { activity.appOpenManager.showOnLoader(activity) { finish() } },
            )
        }
    }

    override fun appOpenOnReturn() {
        main.post { if (isActivityResumed()) activity.appOpenManager.onReturnToApp() }
    }

    override fun onFrontNavigation() {
        main.post { if (isActivityResumed() && !isTextInputActive) activity.adManager.onFrontNavigation() }
    }

    override fun onBackNavigation() {
        main.post { if (isActivityResumed() && !isTextInputActive) activity.adManager.onBackNavigation() }
    }

    // ── showInterstitial кожні 2 відео (лічильник переживає перезапуск) ──
    private val adsPrefs = ctx.getSharedPreferences("framefall_ads", Context.MODE_PRIVATE)
    private var downloadsSinceInterstitial: Int
        get() = adsPrefs.getInt("downloads", 0)
        set(v) = adsPrefs.edit().putInt("downloads", v).apply()

    override fun onVideoSaved() { downloadsSinceInterstitial += 1 }

    override fun isInterstitialDue(): Boolean = downloadsSinceInterstitial >= INTERSTITIAL_EVERY

    override fun showInterstitial() {
        main.post {
            // У фоні (відео докачалось, поки людина в іншій апці) — не показуємо: лишається в черзі
            if (!isActivityResumed() || isTextInputActive) return@post
            downloadsSinceInterstitial = 0
            activity.adManager.showInterstitial()
        }
    }

    override fun adsDebug(): String = activity.adManager.debugLine() + " · dl $downloadsSinceInterstitial/$INTERSTITIAL_EVERY"

    override fun screenView(bt: Bt, block: String) = Events.screenView(bt, block)

    override fun openHomeScreen() {
        runCatching { com.vidtools.framefall.services.analytics.AnalyticsManager.openHomeScreen() }
            .onFailure { log("TikTok openHomeScreen: ${it.message}") }
    }

    private fun isActivityResumed() =
        !activity.isFinishing && activity.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    fun dispose() {
        runCatching { ctx.unregisterReceiver(downloadReceiver) }
        runCatching { launcherApps.unregisterCallback(packageCallback) }
        studio.cancel()
        scope.cancel()
    }

    companion object {
        /** Мініатюра у filesDir/thumbs/<name>.jpg, ширина ≤ 480 (Bitmap не звільняє). */
        fun saveThumb(ctx: Context, name: String, bmp: Bitmap): String? = runCatching {
            val small = if (bmp.width > 480) Bitmap.createScaledBitmap(bmp, 480, (bmp.height * 480f / bmp.width).toInt().coerceAtLeast(1), true) else bmp
            val f = File(File(ctx.filesDir, "thumbs").apply { mkdirs() }, "$name.jpg")
            java.io.FileOutputStream(f).use { small.compress(Bitmap.CompressFormat.JPEG, 82, it) }
            if (small !== bmp) small.recycle()
            f.absolutePath
        }.getOrNull()

        const val FOLDER = "Framefall"
        const val USER_AGENT = "Framefall/1.0 (Android)"
        /** Швидше за будь-яку людську відповідь: діалог ролі навіть не показався. */
        const val DIALOG_SUPPRESSED_MS = 700L
        /** showInterstitial — після кожних N збережених відео. */
        const val INTERSTITIAL_EVERY = 2
    }
}
