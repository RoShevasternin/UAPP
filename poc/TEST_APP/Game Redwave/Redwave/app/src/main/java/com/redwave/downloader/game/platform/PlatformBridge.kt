package com.redwave.downloader.game.platform

import com.redwave.downloader.core.config.RemoteFlags
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
    /** Лаунчер не можна закривати: «вихід» = moveTaskToBack(true), а не Gdx.app.exit(). */
    fun moveToBack()
    /** Вільне місце на сховищі й скільки займає Music/Redwave — для плашки в Library. */
    fun storageInfo(): StorageInfo

    // ── Текстове поле: нативний EditText поверх GL ───────────────────────────
    /**
     * scene2d TextField не вміє MSDF, тож редагування — справжній EditText, який
     * Activity кладе ПОВЕРХ поля (координати в px від лівого ВЕРХНЬОГО кута екрана).
     * Системне «Вставити» з меню поля — без тосту Android 12+.
     * onChange — на кожну зміну; onDone(text, submitted) — кінець редагування: submitted = true
     * лише для IME-дії («→» / Go / Search); Back і втрата фокусу — false (нічого не відправляти).
     * Обидва — у GL-потоці.
     */
    fun beginTextInput(req: TextInputRequest, onChange: (String) -> Unit, onDone: (text: String, submitted: Boolean) -> Unit)
    fun endTextInput()

    // ── Роль HOME ────────────────────────────────────────────────────────────
    fun isDefaultHome(): Boolean
    /** RoleManager.createRequestRoleIntent(ROLE_HOME) на Q+, ACTION_HOME_SETTINGS на старших. */
    fun requestDefaultHome(onResult: (isHome: Boolean) -> Unit)
    /** Системний екран вибору головного застосунку (Settings: перемикач «Use as Home screen» → вимкнути). */
    fun openHomeAppSettings()
    val appVersion: String

    // ── Remote Config (Firebase) ─────────────────────────────────────────────
    /** Що діє зараз: офлайн — RemoteFlags.DEFAULT, онлайн — кеш Firebase (або DEFAULT до першої відповіді). Не щокадру. */
    fun remoteFlags(): RemoteFlags
    /** Чи були прапорці з сервера хоч раз (кеш минулого запуску теж рахується). */
    fun hasRemoteFlags(): Boolean
    /** fetchAndActivate; onDone у GL-потоці рівно один раз — по відповіді або по таймауту. */
    fun refreshRemoteFlags(timeoutMs: Long, onDone: (RemoteFlags) -> Unit)
    /** Settings → Debug: звідки прапорці (remote / default / debug) і що в них. */
    fun remoteFlagsDebug(): String
    /** Debug AD_MODE: реклама + роль обов'язкова + без Uninstall поверх Remote Config. */
    fun isAdMode(): Boolean
    /** Увімкнути / вимкнути AD_MODE і перезапустити апку (лише debug-збірка). */
    fun setAdMode(on: Boolean)

    // ── Буфер ────────────────────────────────────────────────────────────────
    /**
     * Лише за ДІЄЮ користувача (кнопка Paste). Автоматичне читання при «Додому»
     * робить Activity в onWindowFocusChanged через ClipGate і шле PlatformEvents.onClipboardLink.
     */
    fun readClipboardText(): String?
    /**
     * Картку буфера закрили або лінк уже завантажили — ClipGate більше його не показує.
     * Стан ClipGate (timestamp + закриті лінки) живе на Android-боці в SharedPreferences:
     * його читає ClipWatcher у main-потоці в момент фокусу, без походу в GL-потік.
     */
    fun dismissClipLink(url: String)

    // ── Мережа: перевірка лінка, тексти (RSS, catalog.json) ──────────────────
    /** GET з `Range: bytes=0-63` (+ редиректи) → AudioSniffer.decide(). */
    fun probe(url: String, onResult: (ProbeResult) -> Unit)
    fun fetchText(url: String, onResult: (Result<String>) -> Unit)
    /** Картинка за URL (обкладинки каталогу, лого фіду) з дисковим кешем у cacheDir/img. */
    fun fetchBytes(url: String, onResult: (ByteArray?) -> Unit)
    /** Записати обкладинку (з каталогу/фіду) у filesDir/covers/<trackId>.jpg → шлях для Track.coverPath. */
    fun saveCover(trackId: String, bytes: ByteArray): String

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
    /** Preview рингтону: гучність = fade за позицією, пауза рівно на endMs. Скидає clearPreviewFade / play. */
    fun previewFade(startMs: Long, endMs: Long, fadeInMs: Long, fadeOutMs: Long)
    fun clearPreviewFade()
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
    /** Чи стоїть зараз звук, встановлений Redwave (для цього типу). */
    fun isOurSoundActive(saveAs: SaveAs): Boolean
    /**
     * Повернути системний звук. Відомий оригінал (запам'ятали перед заміною) — одразу,
     * колбек RESTORED. Невідомий — системний список звуків: PICKED (вибрали) / CANCELLED.
     */
    fun restoreSystemSound(saveAs: SaveAs, onResult: (RestoreResult) -> Unit)
    /** Теги щойно завантаженого файлу (MediaMetadataRetriever); обкладинку кладе в filesDir/covers/<trackId>.jpg. */
    fun readTags(localUri: String, trackId: String, onResult: (TrackTags?) -> Unit)
    fun shareTrack(track: Track)
    /** Видаляє файл з диска (і рядок MediaStore). false — файл лишився. */
    fun deleteTrack(track: Track, onResult: (Boolean) -> Unit)
    /** Куди качаємо — для людей: «Internal storage / Music / Redwave». */
    val musicFolderLabel: String
    /** Що реально лежить у Music/Redwave (зокрема «сироти» поза бібліотекою). */
    fun musicFolderFiles(): List<FolderFile>
    /**
     * Видалити файли; колбек — скільки зникло. Свої — одразу; чужі (з попередньої установки) —
     * через системне підтвердження MediaStore.createDeleteRequest (API 30+).
     */
    fun deleteFiles(files: List<FolderFile>, onResult: (Int) -> Unit)
    /** Чи бачимо ВСІ файли теки (READ_MEDIA_AUDIO / READ_EXTERNAL_STORAGE). */
    fun hasAudioAccess(): Boolean
    fun requestAudioAccess(onResult: (Boolean) -> Unit)

    // ── Лаунчер ──────────────────────────────────────────────────────────────
    /** LauncherApps.getActivityList для всіх профілів (робочий профіль теж). */
    fun listApps(): List<LauncherApp>
    fun appIconPng(app: LauncherApp, sizePx: Int, onResult: (ByteArray?) -> Unit)
    fun launchApp(app: LauncherApp)
    /** Док лаунчера: дефолтні апки ролей [Телефон, Повідомлення, Браузер, Камера]; null — немає дефолту. */
    fun dockApps(): List<LauncherApp?>
    /** Довге натискання в лаунчері (RemoteFlags.isUninstall). app = null — сама Redwave. */
    fun canUninstall(app: LauncherApp?): Boolean
    fun openAppInfo(app: LauncherApp?)
    fun uninstallApp(app: LauncherApp?)
}

/** Android → GDX. Реалізує GDXGame; Android кличе вже через runGDX (GL-потік). */
interface PlatformEvents {
    /** onNewIntent з CATEGORY_HOME: показати LauncherScreen. */
    fun onHomePressed()
    /** Іконку Redwave натиснули, коли апка вже жива (singleTask → onNewIntent з LAUNCHER). */
    fun onAppIconPressed()
    /** Новий завантажуваний лінк у буфері (ClipGate вже відфільтрував). */
    fun onClipboardLink(link: ResolvedLink)
    /** ACTION_SEND text/plain: «Поділитися → Redwave». */
    fun onSharedText(text: String)
    fun onDownloadFinished(id: Long, success: Boolean, localUri: String?)
    fun onAppsChanged()
    fun onPlaybackChanged()
    /** Прапорці Remote Config змінились (fetch / real-time / debug з adb). */
    fun onRemoteFlags(flags: RemoteFlags)
}

data class ProbeResult(
    val decision: AudioSniffer.Decision,
    /** URL після редиректів (Drive/Dropbox/1drv.ms) — саме його віддаємо DownloadManager. */
    val finalUrl: String,
    /** Content-Length або з Content-Range; -1 якщо невідомо. */
    val sizeBytes: Long,
    val httpCode: Int,
)

enum class RestoreResult { RESTORED, PICKED, CANCELLED, NO_PERMISSION }

data class FolderFile(
    val name: String,
    val path: String,
    val sizeBytes: Long,
    /** content://media/… — для чужих файлів (видалення через createDeleteRequest). */
    val contentUri: String? = null,
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

data class TrackTags(
    val title: String?,
    val artist: String?,
    val durationMs: Long,
    val mime: String?,
    val sizeBytes: Long,
    val coverPath: String?,
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
    /** COLUMN_LOCAL_URI — для DONE, якщо ACTION_DOWNLOAD_COMPLETE пропустили (процес спав). */
    val localUri: String? = null,
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
