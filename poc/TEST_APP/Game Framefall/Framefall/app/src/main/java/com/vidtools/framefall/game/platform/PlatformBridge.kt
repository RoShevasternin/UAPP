package com.vidtools.framefall.game.platform

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Matrix4
import com.vidtools.framefall.businesModule.backend.Bt
import com.vidtools.framefall.core.config.AppFlags
import com.vidtools.framefall.core.link.ResolvedLink
import com.vidtools.framefall.core.link.VideoSniffer
import com.vidtools.framefall.core.model.DownloadStatus
import com.vidtools.framefall.core.model.MediaKind
import com.vidtools.framefall.core.model.Video

// ═════════════════════════════════════════════════════════════════════════════
//  PlatformBridge — ЄДИНИЙ контракт між LibGDX-частиною і Android.
//
//  GDX-код (екрани, актори, контролери) НЕ імпортує android.*; лише цей інтерфейс.
//  Реалізація — AndroidBridge у пакеті android (MainActivity створює і віддає в GDXGame).
//
//  ПОТОКИ:
//    • методи викликаються з GL-потоку; усе важке (мережа, диск, MediaCodec)
//      реалізація сама переносить у корутини Dispatchers.IO;
//    • колбеки onResult реалізація ЗАВЖДИ повертає в GL-потік (runGDX) —
//      у GDX-коді їх можна одразу класти на сцену;
//    • «синхронні» геттери (queryDownloads, isDefaultHome, VideoPlayer.positionMs) —
//      дешеві, їх можна кликати раз на кадр.
// ═════════════════════════════════════════════════════════════════════════════
interface PlatformBridge {

    // ── Система ──────────────────────────────────────────────────────────────
    /** Висоти системних барів у px (WindowInsets) — для safe area, як у T35. */
    val statusBarPx: Int
    val navBarPx: Int
    fun showToast(text: String)
    fun vibrate(ms: Long = 12)
    fun isOnline(): Boolean
    fun openUrl(url: String)
    /** Лаунчер не можна закривати: «вихід» = moveTaskToBack(true), а не Gdx.app.exit(). */
    fun moveToBack()
    /** Вільне місце на сховищі — для плашки в Library. */
    fun storageInfo(): StorageInfo

    // ── Текстове поле: нативний EditText поверх GL ───────────────────────────
    /**
     * scene2d TextField не вміє MSDF, тож редагування — справжній EditText, який
     * Activity кладе ПОВЕРХ поля (координати в px від лівого ВЕРХНЬОГО кута екрана).
     * onDone(text, submitted): submitted = true лише для IME-дії («→» / Go).
     */
    fun beginTextInput(req: TextInputRequest, onChange: (String) -> Unit, onDone: (text: String, submitted: Boolean) -> Unit)
    fun endTextInput()

    // ── Роль HOME ────────────────────────────────────────────────────────────
    fun isDefaultHome(): Boolean
    /** RoleManager.createRequestRoleIntent(ROLE_HOME) на Q+, ACTION_HOME_SETTINGS на старших. */
    fun requestDefaultHome(onResult: (isHome: Boolean) -> Unit)
    /** Системний екран вибору головного застосунку (Settings → вимкнути). */
    fun openHomeAppSettings()
    val appVersion: String

    // ── Конфіг з нашого сервера (businesModule, ServerConfig) ───────────────
    /** Режим з картки: app_open = custom* → «наша реклама»; офлайн / na / admob — DEFAULT. Не щокадру. */
    fun appFlags(): AppFlags
    /** Чи є конфіг (хоч з кешу минулого запуску). Ні — Splash чекає сервер. */
    fun hasConfig(): Boolean
    /** /appconfig; onDone у GL-потоці рівно один раз — по відповіді або по таймауту. */
    fun loadConfig(timeoutMs: Long, onDone: () -> Unit)
    /** Settings → Debug: звідки конфіг, atk, тип юзера, режим. */
    fun configDebug(): String
    /** Debug AD_MODE: «наша реклама» примусово (вкладки на «Додому», роль обов'язкова, без Uninstall). */
    fun isAdMode(): Boolean
    /** Увімкнути / вимкнути AD_MODE і перезапустити апку (лише debug-збірка). */
    fun setAdMode(on: Boolean)

    // ── Бізнес-логіка UAPP: черга старту, реклама, події ─────────────────────
    /** Черга старту UAPP (Biz.runStartupFlow): опт-ін → стартовий app_open → onDone у GL-потоці рівно один раз. */
    fun runStartupFlow(onDone: () -> Unit)
    /** app_open при поверненні в застосунок (AppOpenManager.onReturnToApp). */
    fun appOpenOnReturn()
    /** Інтерстішал на переходах (AdManager): частоту front/back рахує картка сервера. */
    fun onFrontNavigation()
    fun onBackNavigation()
    /** Відео збережено — лічильник для showInterstitial (кожні 2). */
    fun onVideoSaved()
    fun isInterstitialDue(): Boolean
    /** showInterstitial (pl=interstitial / AdMob), скидає лічильник. */
    fun showInterstitial()
    /** Settings → Debug: провайдер інтерстішала, лічильники. */
    fun adsDebug(): String
    /** TikTok UNLOCK_ACHIEVEMENT (AnalyticsManager.openHomeScreen) — вхід у застосунок, як MainScreen еталона. */
    fun openHomeScreen()
    /** Подія screen_view (Events): bt — спільний словник парку, block — snake_case, не перейменовувати. */
    fun screenView(bt: Bt, block: String)

    // ── Буфер ────────────────────────────────────────────────────────────────
    /** Лише за ДІЄЮ користувача (кнопка Paste). Автоматичне читання робить ClipWatcher. */
    fun readClipboardText(): String?
    /** Картку буфера закрили або лінк уже збережено — ClipGate більше його не показує. */
    fun dismissClipLink(url: String)

    // ── Мережа ───────────────────────────────────────────────────────────────
    /** GET з `Range: bytes=0-63` (+ редиректи) → VideoSniffer.decide(). */
    fun probe(url: String, onResult: (ProbeResult) -> Unit)
    fun fetchText(url: String, onResult: (Result<String>) -> Unit)
    /** Картинка за URL з дисковим кешем у cacheDir/img. */
    fun fetchBytes(url: String, onResult: (ByteArray?) -> Unit)

    // ── Завантаження (DownloadManager → Movies/Framefall) ────────────────────
    fun enqueueDownload(req: DownloadRequest): Long
    fun cancelDownload(id: Long)
    fun queryDownloads(ids: LongArray): List<DownloadProgress>

    // ── Відео ────────────────────────────────────────────────────────────────
    /**
     * Новий плеєр (ExoPlayer → SurfaceTexture → OES-текстура в GDX). Створювати й
     * звільняти з GL-потоку; кожен екран тримає свій і звільняє в hide()/dispose().
     */
    fun createVideoPlayer(): VideoPlayer
    /** URI вбудованої петлі фону головного екрана (assets/video/bg_<key>.mp4). */
    fun builtInBackground(key: String): String

    // ── Файли ────────────────────────────────────────────────────────────────
    /** JPEG мініатюри: thumbPath, інакше кадр з файлу (і кешується в filesDir/thumbs). */
    fun loadThumbBytes(video: Video, onResult: (ByteArray?) -> Unit)
    /** Метадані щойно збереженого файлу + мініатюра у filesDir/thumbs/<videoId>.jpg. */
    fun readVideoMeta(localUri: String, videoId: String, onResult: (VideoMeta?) -> Unit)
    /** Смуга кадрів для Studio: [count] JPEG по ~160 px, рівномірно по тривалості (кеш у cacheDir). */
    fun stripFrames(video: Video, count: Int, onResult: (List<ByteArray>) -> Unit)
    fun fileExists(localUri: String?): Boolean
    /** Чи є у файлі звукова доріжка (Studio → Extract audio). Синхронно: MMR, мілісекунди. */
    fun hasAudio(video: Video): Boolean
    fun shareVideo(video: Video)
    /** Кадр / GIF (наш плеєр їх не грає) — системний переглядач. */
    fun openExternally(video: Video)
    /** Видаляє файл з диска (і рядок MediaStore). false — файл лишився. */
    fun deleteVideo(video: Video, onResult: (Boolean) -> Unit)
    /** Куди зберігаємо — для людей: «Internal storage / Movies / Framefall». */
    val folderLabel: String

    // ── Studio (Media3 Transformer + MediaMetadataRetriever) ─────────────────
    /** onProgress 0..1 і onResult — у GL-потоці. Одна робота за раз. */
    fun runStudio(job: StudioJob, onProgress: (Float) -> Unit, onResult: (Result<List<StudioOutput>>) -> Unit)
    fun cancelStudio()

    // ── Лаунчер ──────────────────────────────────────────────────────────────
    /** LauncherApps.getActivityList для всіх профілів (робочий профіль теж). */
    fun listApps(): List<LauncherApp>
    fun appIconPng(app: LauncherApp, sizePx: Int, onResult: (ByteArray?) -> Unit)
    fun launchApp(app: LauncherApp)
    /** Док лаунчера: дефолтні апки ролей [Телефон, Повідомлення, Браузер, Камера]; null — немає дефолту. */
    fun dockApps(): List<LauncherApp?>
    /** Довге натискання в лаунчері (AppFlags.isUninstall). app = null — сама Framefall. */
    fun canUninstall(app: LauncherApp?): Boolean
    fun openAppInfo(app: LauncherApp?)
    fun uninstallApp(app: LauncherApp?)
}

/** Android → GDX. Реалізує GDXGame; Android кличе вже через runGDX (GL-потік). */
interface PlatformEvents {
    /** onNewIntent з CATEGORY_HOME: показати LauncherScreen. */
    fun onHomePressed()
    /** Іконку Framefall натиснули, коли апка вже жива (singleTask → onNewIntent з LAUNCHER). */
    fun onAppIconPressed()
    /** Новий лінк на відео в буфері (ClipGate вже відфільтрував). */
    fun onClipboardLink(link: ResolvedLink)
    /** ACTION_SEND text/plain: «Поділитися → Framefall». */
    fun onSharedText(text: String)
    fun onDownloadFinished(id: Long, success: Boolean, localUri: String?)
    fun onAppsChanged()
    /** Режим змінився (новий конфіг сервера / інтернет). */
    fun onFlagsChanged(flags: AppFlags)
    /**
     * Людина повернулась у Framefall з іншої апки / «Недавніх» / вимкненого екрана (не через «Додому»,
     * не з нашої реклами, вкладки чи системного екрана): awayMs — скільки її не було.
     * fromIcon = true — тапнули іконку Framefall (відкриття апки, а не повернення).
     */
    fun onAppForeground(awayMs: Long, fromIcon: Boolean)
}

// ═════════════════════════════════════════════════════════════════════════════
//  VideoPlayer — один ExoPlayer, що малює кадри в OES-текстуру для GDX.
//  Команди можна кликати з GL-потоку (реалізація сама переносить на main),
//  frame() — ЛИШЕ з GL-потоку, раз на кадр (updateTexImage).
// ═════════════════════════════════════════════════════════════════════════════
interface VideoPlayer {
    fun open(uri: String, autoplay: Boolean = true, loop: Boolean = false, muted: Boolean = false, startMs: Long = 0L)
    fun play()
    fun pause()
    fun seekTo(ms: Long)
    var speed: Float
    var loop: Boolean
    var muted: Boolean
    /** Studio: грати лише [startMs, endMs] по колу. endMs < 0 — без обмеження. */
    fun setRange(startMs: Long, endMs: Long)

    val positionMs: Long
    val durationMs: Long
    val isPlaying: Boolean
    /** Є перший кадр (до нього показуємо постер). */
    val hasFrame: Boolean
    val isEnded: Boolean
    val error: String?
    /** Розмір кадру з урахуванням повороту і пропорції пікселя. 0 — ще невідомо. */
    val videoWidth: Int
    val videoHeight: Int

    /** GL-потік: забрати свіжий кадр. null — кадру ще немає (або GL-контекст перестворюється). */
    fun frame(): VideoFrame?
    fun release()
}

/** OES-текстура + матриця SurfaceTexture (поворот/віддзеркалення/кроп декодера). */
class VideoFrame(val texture: Texture, val matrix: Matrix4)

data class ProbeResult(
    val decision: VideoSniffer.Decision,
    /** URL після редиректів (Drive/Dropbox/1drv.ms) — саме його віддаємо DownloadManager. */
    val finalUrl: String,
    /** Content-Length або з Content-Range; -1 якщо невідомо. */
    val sizeBytes: Long,
    val httpCode: Int,
)

data class StorageInfo(val freeBytes: Long, val totalBytes: Long)

data class TextInputRequest(
    val xPx: Int, val yPx: Int, val wPx: Int, val hPx: Int,
    val text: String,
    val hint: String,
    val textSizePx: Float,
    /** true — клавіатура для URL (без автокорекції). */
    val isUrl: Boolean = false,
)

data class VideoMeta(
    val title: String?,
    val durationMs: Long,
    val width: Int,
    val height: Int,
    val mime: String?,
    val sizeBytes: Long,
    val hasVideo: Boolean,
    val thumbPath: String?,
)

data class DownloadRequest(
    val url: String,
    /** Ім'я в Movies/Framefall/ — без шляху, з розширенням. */
    val fileName: String,
    val title: String,
    val mimeType: String? = null,
)

data class DownloadProgress(
    val id: Long,
    val bytesDone: Long,
    val bytesTotal: Long,
    val status: DownloadStatus,
    /** DownloadManager.COLUMN_REASON для FAILED/PAUSED. */
    val reason: Int = 0,
    /** COLUMN_LOCAL_URI — для DONE, якщо ACTION_DOWNLOAD_COMPLETE пропустили (процес спав). */
    val localUri: String? = null,
)

/**
 * Робота Studio. tool — id з Copy.Studio.TOOLS (trim / audio / gif / status / frame / compress / loop).
 * option: status — довжина частини в секундах; compress — висота кадру (720/480/360).
 */
data class StudioJob(
    val tool: String,
    val video: Video,
    val startMs: Long,
    val endMs: Long,
    val option: Int = 0,
)

data class StudioOutput(
    val localUri: String,
    val title: String,
    val kind: MediaKind,
    val format: String,
    val durationMs: Long,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val thumbPath: String?,
)

data class LauncherApp(
    val packageName: String,
    val activityName: String,
    val label: String,
    /** UserManager.getSerialNumberForUser — розрізняє особистий і робочий профіль. */
    val userSerial: Long,
)
