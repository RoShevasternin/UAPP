package com.redwave.downloader.android

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
import com.redwave.downloader.MainActivity
import com.redwave.downloader.android.ringtone.RingtoneMaker
import com.redwave.downloader.android.ringtone.WaveformPeaks
import com.redwave.downloader.core.link.AudioSniffer
import com.redwave.downloader.core.model.DownloadStatus
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.core.ringtone.CutSelection
import com.redwave.downloader.core.ringtone.SaveAs
import com.redwave.downloader.core.viz.SpectrumAnalyzer
import com.redwave.downloader.game.platform.DownloadProgress
import com.redwave.downloader.game.platform.DownloadRequest
import com.redwave.downloader.game.platform.FolderFile
import com.redwave.downloader.game.platform.LauncherApp
import com.redwave.downloader.game.platform.PlatformBridge
import com.redwave.downloader.game.platform.PlatformEvents
import com.redwave.downloader.game.platform.PlaybackSnapshot
import com.redwave.downloader.game.platform.ProbeResult
import com.redwave.downloader.game.platform.StorageInfo
import com.redwave.downloader.game.platform.TextInputRequest
import com.redwave.downloader.game.platform.TrackTags
import com.redwave.downloader.game.utils.runGDX
import com.redwave.downloader.playback.PlaybackClient
import com.redwave.downloader.playback.Spectrum
import com.redwave.downloader.util.log
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
//  мережа (OkHttp), DownloadManager, буфер, роль HOME, LauncherApps, теги,
//  нативне поле вводу. Відтворення — PlaybackClient (Media3 MediaController).
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

    val playback = PlaybackClient(ctx).apply { onChanged = { emit { onPlaybackChanged() } } }

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
                    val decision = AudioSniffer.decide(type, cd, name, bytes)
                    log("probe $url → ${resp.code} $type ${decision.reason} final=$final size=$total")
                    ProbeResult(decision, final, total, resp.code)
                }
            }.getOrElse { e ->
                log("probe failed: ${e.message}")
                ProbeResult(AudioSniffer.Decision(false, null, null, "network: ${e.message}"), url, -1L, -1)
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

    /** Зберегти байти обкладинки (з каталогу) у filesDir/covers/<id>.jpg — для Track.coverPath. */
    override fun saveCover(trackId: String, bytes: ByteArray): String =
        File(File(ctx.filesDir, "covers").apply { mkdirs() }, "$trackId.jpg").apply { writeBytes(bytes) }.absolutePath

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
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "$FOLDER/$name")
            .setTitle(req.title)
            .setDescription("Redwave")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .addRequestHeader("User-Agent", USER_AGENT)
        req.mimeType?.let { r.setMimeType(it) }
        val id = downloadManager.enqueue(r)
        log("enqueue $id ${req.url} → Music/$FOLDER/$name")
        return id
    }

    /** DownloadManager на однакове ім'я поводиться по-різному на прошивках — робимо «name (2).mp3» самі. */
    private fun uniqueFileName(fileName: String): String {
        val clean = fileName.replace(Regex("""[\\/:*?"<>|]"""), "_").ifBlank { "track.mp3" }
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), FOLDER)
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
    // Відтворення → PlaybackClient
    // ------------------------------------------------------------------------
    override fun play(queue: List<Track>, startIndex: Int, positionMs: Long) = playback.play(queue, startIndex, positionMs)
    override fun pause()  = playback.pause()
    override fun resume() = playback.resume()
    override fun seekTo(positionMs: Long) = playback.seekTo(positionMs)
    override fun next()     = playback.next()
    override fun previous() = playback.previous()
    override fun setShuffle(on: Boolean)   = playback.setShuffle(on)
    override fun setRepeatOne(on: Boolean) = playback.setRepeatOne(on)
    override fun setSleepTimer(minutes: Int) = playback.setSleepTimer(minutes)
    override fun playbackState(): PlaybackSnapshot = playback.snapshot
    override fun setEqualizer(enabled: Boolean, bandsDb: FloatArray) = AudioFx.set(enabled, bandsDb)
    override val spectrum: SpectrumAnalyzer get() = Spectrum.analyzer

    // ------------------------------------------------------------------------
    // Файли, обкладинки, теги
    // ------------------------------------------------------------------------
    override fun loadCoverBytes(track: Track, onResult: (ByteArray?) -> Unit) {
        scope.launch {
            val bytes = runCatching {
                track.coverPath?.let { p -> File(p).takeIf { it.exists() }?.readBytes() }
                    ?: track.localUri?.let { embeddedPicture(it) }
            }.getOrNull()
            runGDX { onResult(bytes) }
        }
    }

    override fun readTags(localUri: String, trackId: String, onResult: (TrackTags?) -> Unit) {
        scope.launch {
            val tags = runCatching {
                val mmr = MediaMetadataRetriever()
                try {
                    setSource(mmr, localUri)
                    val title  = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)?.trim()?.ifEmpty { null }
                    val artist = (mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                        ?: mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST))?.trim()?.ifEmpty { null }
                    val dur    = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                    val mime   = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                    val cover  = mmr.embeddedPicture?.let { pic ->
                        File(ctx.filesDir, "covers").apply { mkdirs() }.let { dir ->
                            File(dir, "$trackId.jpg").apply { writeBytes(pic) }.absolutePath
                        }
                    }
                    TrackTags(title, artist, dur, mime, fileSize(localUri), cover)
                } finally {
                    mmr.release()
                }
            }.onFailure { log("readTags $localUri: ${it.message}") }.getOrNull()
            runGDX { onResult(tags) }
        }
    }

    private fun setSource(mmr: MediaMetadataRetriever, uri: String) {
        val u = Uri.parse(uri)
        if (u.scheme == "file" || u.scheme == null) mmr.setDataSource(u.path ?: uri) else mmr.setDataSource(ctx, u)
    }

    private fun embeddedPicture(uri: String): ByteArray? {
        val mmr = MediaMetadataRetriever()
        return try { setSource(mmr, uri); mmr.embeddedPicture } catch (e: Exception) { null } finally { mmr.release() }
    }

    private fun fileSize(uri: String): Long {
        val u = Uri.parse(uri)
        return if (u.scheme == "file") File(u.path ?: return 0).length() else 0L
    }

    override fun waveformPeaks(track: Track, bins: Int, onResult: (FloatArray) -> Unit) {
        scope.launch {
            val peaks = WaveformPeaks.load(ctx, track, bins)
            runGDX { onResult(peaks) }
        }
    }

    override fun canWriteSettings(): Boolean =
        Build.VERSION.SDK_INT < 23 || Settings.System.canWrite(ctx)

    override fun openWriteSettings() {
        main.post {
            try {
                activity.startInternal(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, "package:${ctx.packageName}".toUri()))
            } catch (e: ActivityNotFoundException) {
                activity.startInternal(Intent(Settings.ACTION_SETTINGS))
            }
        }
    }

    override fun saveRingtone(
        track: Track, selection: CutSelection, fadeIn: Boolean, fadeOut: Boolean, saveAs: SaveAs,
        onResult: (Result<String>) -> Unit,
    ) {
        main.post {
            RingtoneMaker.save(ctx, track, selection, fadeIn, fadeOut, saveAs) { r -> runGDX { onResult(r) } }
        }
    }

    override fun shareTrack(track: Track) {
        val uri = track.localUri ?: return
        main.post {
            runCatching {
                val u = Uri.parse(uri)
                val content = if (u.scheme == "file") FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", File(u.path!!)) else u
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "audio/*"
                    putExtra(Intent.EXTRA_STREAM, content)
                    putExtra(Intent.EXTRA_TITLE, track.title)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                activity.startInternal(Intent.createChooser(intent, track.title))
            }.onFailure { log("shareTrack: ${it.message}") }
        }
    }

    override fun deleteTrack(track: Track, onResult: (Boolean) -> Unit) {
        scope.launch {
            val ok = track.localUri?.let { deleteUri(it) } ?: true
            if (ok) track.coverPath?.let { File(it).delete() }
            runGDX { onResult(ok) }
        }
    }

    // ------------------------------------------------------------------------
    // Тека Music/Redwave: що реально лежить на диску
    // ------------------------------------------------------------------------
    private val musicDir get() = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), FOLDER)

    override val musicFolderLabel: String get() = "Internal storage / Music / $FOLDER"

    /**
     * Без дозволу scoped storage показує лише НАШІ файли (поточної установки). З дозволом —
     * MediaStore за RELATIVE_PATH, тобто й «сироти» з минулих установок.
     */
    override fun musicFolderFiles(): List<FolderFile> {
        val own = runCatching {
            musicDir.listFiles()?.filter { it.isFile }?.map { FolderFile(it.name, it.absolutePath, it.length()) }.orEmpty()
        }.getOrDefault(emptyList())
        if (!hasAudioAccess()) return own
        val all = runCatching {
            val out = ArrayList<FolderFile>()
            val col = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val proj = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.SIZE)
            val where = "${MediaStore.Audio.Media.DATA} LIKE ?"
            ctx.contentResolver.query(col, proj, where, arrayOf("${musicDir.absolutePath}/%"), null)?.use { c ->
                while (c.moveToNext()) {
                    val uri = android.content.ContentUris.withAppendedId(col, c.getLong(0))
                    out += FolderFile(c.getString(1) ?: "?", c.getString(2) ?: "", c.getLong(3), uri.toString())
                }
            }
            out
        }.getOrDefault(emptyList())
        // свої, яких MediaStore ще не проіндексував, теж показуємо
        return all + own.filter { o -> all.none { it.path == o.path } }
    }

    override fun hasAudioAccess(): Boolean {
        val perm = if (Build.VERSION.SDK_INT >= 33) android.Manifest.permission.READ_MEDIA_AUDIO else android.Manifest.permission.READ_EXTERNAL_STORAGE
        return ContextCompat.checkSelfPermission(ctx, perm) == PackageManager.PERMISSION_GRANTED
    }

    private var audioPermCallback: ((Boolean) -> Unit)? = null
    private val audioPermLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val cb = audioPermCallback; audioPermCallback = null
            runGDX { cb?.invoke(granted) }
        }

    override fun requestAudioAccess(onResult: (Boolean) -> Unit) {
        main.post {
            audioPermCallback = onResult
            activity.internalNavigation = true
            audioPermLauncher.launch(if (Build.VERSION.SDK_INT >= 33) android.Manifest.permission.READ_MEDIA_AUDIO else android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private var deleteCallback: ((Int) -> Unit)? = null
    private var deleteDoneOwn = 0
    private var deletePending: List<FolderFile> = emptyList()
    private val deleteRequestLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { r ->
            scope.launch {
                // Після «Дозволити» система вже стерла файли — рахуємо, яких справді немає
                val gone = deletePending.count { !File(it.path).exists() }
                log("createDeleteRequest result=${r.resultCode}, gone=$gone/${deletePending.size}")
                val n = deleteDoneOwn + gone
                val cb = deleteCallback; deleteCallback = null
                runGDX { cb?.invoke(n) }
            }
        }

    override fun deleteFiles(files: List<FolderFile>, onResult: (Int) -> Unit) {
        scope.launch {
            // 1) свої — напряму
            val foreign = ArrayList<FolderFile>()
            var own = 0
            files.forEach { f -> if (deleteUri(Uri.fromFile(File(f.path)).toString())) own++ else foreign += f }
            // 2) чужі — системне підтвердження (API 30+)
            val uris = foreign.mapNotNull { it.contentUri?.let(Uri::parse) }
            if (uris.isEmpty() || Build.VERSION.SDK_INT < 30) { runGDX { onResult(own) }; return@launch }
            main.post {
                deleteCallback = onResult; deleteDoneOwn = own; deletePending = foreign
                activity.internalNavigation = true
                val pi = MediaStore.createDeleteRequest(ctx.contentResolver, uris)
                deleteRequestLauncher.launch(androidx.activity.result.IntentSenderRequest.Builder(pi.intentSender).build())
            }
        }
    }

    /**
     * Видалити файл НАСПРАВДІ: спершу File.delete (власник — ми, FUSE пускає), потім рядок
     * MediaStore за шляхом (інакше в системних плеєрах лишається «привид»). Успіх = файла немає.
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
    // Lifecycle
    // ------------------------------------------------------------------------
    fun onActivityResumed() {
        playback.connect()
    }

    fun dispose() {
        runCatching { ctx.unregisterReceiver(downloadReceiver) }
        runCatching { launcherApps.unregisterCallback(packageCallback) }
        playback.release()
        scope.cancel()
    }

    companion object {
        const val FOLDER = "Redwave"
        const val USER_AGENT = "Redwave/1.0 (Android)"
        /** Швидше за будь-яку людську відповідь: діалог ролі навіть не показався. */
        const val DIALOG_SUPPRESSED_MS = 700L
    }
}
