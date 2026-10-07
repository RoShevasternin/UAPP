package com.redwave.downloader.game.platform

import com.redwave.downloader.core.link.AudioSniffer
import com.redwave.downloader.core.link.ResolvedLink
import com.redwave.downloader.core.model.DownloadStatus
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.core.ringtone.CutSelection
import com.redwave.downloader.core.ringtone.SaveAs
import com.redwave.downloader.core.viz.SpectrumAnalyzer

// ═════════════════════════════════════════════════════════════════════════════
//  PlatformBridge — ЄДИНИЙ контракт між LibGDX-частиною і Android.
//
//  GDX-код (екрани, актори, контролери) НЕ імпортує android.*; лише цей інтерфейс.
//  Реалізація — AndroidBridge у пакеті android (MainActivity створює і віддає в GDXGame).
//  Так екрани можна запускати й без Android (desktop-бекенд, тести) зі стабом.
//
//  ПОТОКИ:
//    • методи викликаються з GL-потоку; усе важке (мережа, диск, MediaCodec)
//      реалізація сама переносить у корутини Dispatchers.IO;
//    • колбеки onResult реалізація ЗАВЖДИ повертає в GL-потік (Gdx.app.postRunnable /
//      runGDX) — у GDX-коді їх можна одразу класти на сцену;
//    • «синхронні» геттери (playbackState, queryDownloads, isDefaultHome) — дешеві,
//      їх можна кликати раз на кадр/секунду.
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

    // ── Роль HOME ────────────────────────────────────────────────────────────
    fun isDefaultHome(): Boolean
    /** RoleManager.createRequestRoleIntent(ROLE_HOME) на Q+, ACTION_HOME_SETTINGS на старших. */
    fun requestDefaultHome(onResult: (isHome: Boolean) -> Unit)

    // ── Буфер ────────────────────────────────────────────────────────────────
    /**
     * Лише за ДІЄЮ користувача (кнопка Paste). Автоматичне читання при «Додому»
     * робить Activity в onWindowFocusChanged через ClipGate і шле PlatformEvents.onClipboardLink.
     */
    fun readClipboardText(): String?

    // ── Мережа: перевірка лінка, тексти (RSS, catalog.json) ──────────────────
    /** GET з `Range: bytes=0-63` (+ редиректи) → AudioSniffer.decide(). */
    fun probe(url: String, onResult: (ProbeResult) -> Unit)
    fun fetchText(url: String, onResult: (Result<String>) -> Unit)

    // ── Завантаження (DownloadManager) ───────────────────────────────────────
    fun enqueueDownload(req: DownloadRequest): Long
    fun cancelDownload(id: Long)
    fun queryDownloads(ids: LongArray): List<DownloadProgress>

    // ── Відтворення (Media3 у PlaybackService) ───────────────────────────────
    fun play(queue: List<Track>, startIndex: Int, positionMs: Long = 0L)
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun next()
    fun previous()
    fun setShuffle(on: Boolean)
    fun setRepeatOne(on: Boolean)
    fun setSleepTimer(minutes: Int)
    fun playbackState(): PlaybackSnapshot
    /** android.media.audiofx.Equalizer на audioSessionId плеєра; bandsDb — 5 смуг UI (EqCurve). */
    fun setEqualizer(enabled: Boolean, bandsDb: FloatArray)
    /** Спектр для візуалізатора: Media3 TeeAudioProcessor → SpectrumAnalyzer. Без RECORD_AUDIO. */
    val spectrum: SpectrumAnalyzer

    // ── Файли, обкладинки, рингтон ───────────────────────────────────────────
    /** PNG/JPEG байти обкладинки (filesDir/covers або вбудована в теги) → Pixmap на GL-потоці. */
    fun loadCoverBytes(track: Track, onResult: (ByteArray?) -> Unit)
    /** Піки амплітуди для хвилі рингтону (MediaExtractor + MediaCodec), кешуються на диску. */
    fun waveformPeaks(track: Track, bins: Int, onResult: (FloatArray) -> Unit)
    fun canWriteSettings(): Boolean
    /** Settings.ACTION_MANAGE_WRITE_SETTINGS з package: URI. ◌ На MIUI може відкрити загальний список. */
    fun openWriteSettings()
    /** Media3 Transformer: кліп + fade → .m4a у MediaStore (Ringtones/Alarms/Notifications) → RingtoneManager. */
    fun saveRingtone(
        track: Track, selection: CutSelection, fadeIn: Boolean, fadeOut: Boolean, saveAs: SaveAs,
        onResult: (Result<String>) -> Unit,
    )
    fun shareTrack(track: Track)
    fun deleteTrack(track: Track, onResult: (Boolean) -> Unit)

    // ── Лаунчер ──────────────────────────────────────────────────────────────
    /** LauncherApps.getActivityList для всіх профілів (робочий профіль теж). */
    fun listApps(): List<LauncherApp>
    fun appIconPng(app: LauncherApp, sizePx: Int, onResult: (ByteArray?) -> Unit)
    fun launchApp(app: LauncherApp)
}

/** Android → GDX. Реалізує GDXGame; Android кличе вже через runGDX (GL-потік). */
interface PlatformEvents {
    /** onNewIntent з CATEGORY_HOME: показати LauncherScreen. */
    fun onHomePressed()
    /** Новий завантажуваний лінк у буфері (ClipGate вже відфільтрував). */
    fun onClipboardLink(link: ResolvedLink)
    /** ACTION_SEND text/plain: «Поділитися → Redwave». */
    fun onSharedText(text: String)
    fun onDownloadFinished(id: Long, success: Boolean, localUri: String?)
    fun onAppsChanged()
    fun onPlaybackChanged()
}

data class ProbeResult(
    val decision: AudioSniffer.Decision,
    /** URL після редиректів (Drive/Dropbox/1drv.ms) — саме його віддаємо DownloadManager. */
    val finalUrl: String,
    /** Content-Length або з Content-Range; -1 якщо невідомо. */
    val sizeBytes: Long,
    val httpCode: Int,
)

data class DownloadRequest(
    val url: String,
    /** Ім'я в Music/Redwave/ — без шляху, з розширенням. */
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
)

data class PlaybackSnapshot(
    val trackId: String?,
    val isPlaying: Boolean,
    val positionMs: Long,
    val durationMs: Long,
    val shuffle: Boolean,
    val repeatOne: Boolean,
    val sleepMinutesLeft: Int = 0,
)

data class LauncherApp(
    val packageName: String,
    val activityName: String,
    val label: String,
    /** UserManager.getSerialNumberForUser — розрізняє особистий і робочий профіль. */
    val userSerial: Long,
)
