package com.redwave.downloader.game.controller

import com.redwave.downloader.core.copy.Copy
import com.redwave.downloader.core.link.LinkResolver
import com.redwave.downloader.core.link.ResolvedLink
import com.redwave.downloader.core.link.sourceUrl
import com.redwave.downloader.core.model.DownloadItem
import com.redwave.downloader.core.model.AppEvent
import com.redwave.downloader.core.model.DownloadStatus
import com.redwave.downloader.core.model.EventKind
import com.redwave.downloader.core.model.withEvent
import com.redwave.downloader.core.model.Track
import com.redwave.downloader.core.model.TrackKind
import com.redwave.downloader.core.model.TrackSource
import com.redwave.downloader.game.model.AppModel
import com.redwave.downloader.game.platform.DownloadRequest
import com.redwave.downloader.game.platform.PlatformBridge
import com.redwave.downloader.util.log
import java.util.UUID

// ═════════════════════════════════════════════════════════════════════════════
//  DownloadController — від вставленого тексту до треку в бібліотеці.
//
//    submit(text) → LinkResolver:
//      Invalid / Blocked        → помилка, нічого не качаємо
//      PodcastFeed              → відкрити шторку фіду (Feed)
//      DirectAudio / Dropbox    → одразу DownloadManager
//      WebPage / Drive / OneDrive → «Checking the link…» → probe → AudioSniffer
//    DownloadManager → прогрес опитуванням (tick, раз на 0.5 с) →
//    onFinished → теги (readTags) → Track у бібліотеці → тост.
// ═════════════════════════════════════════════════════════════════════════════
class DownloadController(
    private val model : AppModel,
    private val bridge: PlatformBridge,
    private val toast : (String) -> Unit,
) {

    sealed interface Submit {
        data object Checking : Submit
        data class Error(val title: String, val body: String) : Submit
        data class Feed(val url: String) : Submit
        data class Started(val title: String) : Submit
    }

    /** Швидкість (байт/с) по id — для рядка «4.1 of 5.2 MB · 1.8 MB/s». */
    val speed = HashMap<Long, Float>()
    private val lastBytes = HashMap<Long, Pair<Long, Long>>()

    val active: List<DownloadItem> get() = model.state.downloads

    /** Трек додано в бібліотеку (для лічильника інтерстішала — GDXGame). */
    var onTrackAdded: (() -> Unit)? = null

    // ------------------------------------------------------------------------
    // Submit
    // ------------------------------------------------------------------------
    fun submit(text: String, onState: (Submit) -> Unit) {
        when (val link = LinkResolver.resolve(text)) {
            ResolvedLink.Invalid -> onState(Submit.Error(Copy.Errors.NOT_A_LINK_TITLE, Copy.Errors.NOT_A_LINK_BODY))
            is ResolvedLink.Blocked -> onState(Submit.Error(Copy.Errors.blockedTitle(link.service), Copy.Errors.BLOCKED_BODY))
            is ResolvedLink.PodcastFeed -> onState(Submit.Feed(link.url))
            else -> {
                // dev-host (adb reverse) працює й без мережі на девайсі
                val local = link.sourceUrl?.let { it.startsWith("http://127.0.0.1") || it.startsWith("http://localhost") } == true
                if (!bridge.isOnline() && !local) {
                    onState(Submit.Error(Copy.Errors.NO_NETWORK_TITLE, Copy.Errors.NO_NETWORK_BODY)); return
                }
                startLink(link, onState)
            }
        }
    }

    /** Лінк з картки буфера / шеру — та сама логіка, що й submit, але з готовим ResolvedLink. */
    fun startLink(link: ResolvedLink, onState: (Submit) -> Unit) {
        when (link) {
            is ResolvedLink.DirectAudio -> {
                val t = enqueue(link.url, link.fileName, LinkResolver.titleFromFileName(link.fileName), link.ext)
                onState(Submit.Started(t))
            }
            is ResolvedLink.Dropbox -> {
                val ext = link.fileName.substringAfterLast('.', "mp3")
                val t = enqueue(link.downloadUrl, link.fileName.ifBlank { "dropbox.$ext" }, LinkResolver.titleFromFileName(link.fileName), ext)
                onState(Submit.Started(t))
            }
            is ResolvedLink.GoogleDrive -> probeThenEnqueue(link.downloadUrl, link.url, onState) { firstFailed ->
                // Великі файли Drive → HTML-підтвердження → повтор на confirmedUrl
                if (firstFailed) probeThenEnqueue(com.redwave.downloader.core.link.GoogleDrive.confirmedUrl(link.fileId), link.url, onState, null)
            }
            is ResolvedLink.OneDrive -> probeThenEnqueue(withDownloadParam(link.url), link.url, onState, null)
            is ResolvedLink.WebPage  -> probeThenEnqueue(link.url, link.url, onState, null)
            is ResolvedLink.PodcastFeed -> onState(Submit.Feed(link.url))
            is ResolvedLink.Blocked -> onState(Submit.Error(Copy.Errors.blockedTitle(link.service), Copy.Errors.BLOCKED_BODY))
            ResolvedLink.Invalid -> onState(Submit.Error(Copy.Errors.NOT_A_LINK_TITLE, Copy.Errors.NOT_A_LINK_BODY))
        }
        link.sourceUrl?.let { bridge.dismissClipLink(it) }
    }

    private fun probeThenEnqueue(url: String, sourceUrl: String, onState: (Submit) -> Unit, onNotAudio: ((Boolean) -> Unit)?) {
        onState(Submit.Checking)
        bridge.probe(url) { r ->
            if (r.decision.isAudio) {
                val ext  = r.decision.ext ?: "mp3"
                val name = r.decision.fileName?.takeIf { it.contains('.') } ?: "${r.decision.fileName ?: "track"}.$ext"
                val t = enqueue(r.finalUrl, name, LinkResolver.titleFromFileName(name), ext, sizeHint = r.sizeBytes, sourceUrl = sourceUrl)
                onState(Submit.Started(t))
            } else if (onNotAudio != null) {
                onNotAudio(true)
            } else if (r.httpCode < 0) {
                onState(Submit.Error(Copy.Errors.NO_NETWORK_TITLE, Copy.Errors.NO_NETWORK_BODY))
            } else {
                onState(Submit.Error(Copy.Errors.NOT_AUDIO_TITLE, Copy.Errors.NOT_AUDIO_BODY))
            }
        }
    }

    private fun withDownloadParam(url: String) = if ('?' in url) "$url&download=1" else "$url?download=1"

    // ------------------------------------------------------------------------
    // Enqueue
    // ------------------------------------------------------------------------
    fun enqueue(
        url: String, fileName: String, title: String, ext: String,
        artist: String? = null, kind: TrackKind = TrackKind.SONG, source: TrackSource = TrackSource.LINK,
        license: String? = null, coverUrl: String? = null, refId: String? = null,
        durationMs: Long = 0L, sizeHint: Long = -1L, sourceUrl: String = url,
    ): String {
        val mime = when (ext.lowercase()) {
            "mp3" -> "audio/mpeg"; "m4a", "aac" -> "audio/mp4"; "flac" -> "audio/flac"
            "ogg", "opus" -> "audio/ogg"; "wav" -> "audio/wav"; else -> null
        }
        val id = bridge.enqueueDownload(DownloadRequest(url, fileName, title, mime))
        val item = DownloadItem(
            id = id, trackId = UUID.randomUUID().toString(), title = title, url = sourceUrl,
            bytesTotal = sizeHint, status = DownloadStatus.QUEUED,
            artist = artist, format = ext.uppercase(), kind = kind, source = source, license = license,
            coverUrl = coverUrl, refId = refId, durationMs = durationMs, startedAt = System.currentTimeMillis(),
        )
        model.update { it.copy(downloads = it.downloads + item) }
        toast(Copy.Home.ADDED_TOAST)
        return title
    }

    fun cancel(id: Long) {
        bridge.cancelDownload(id)
        model.update { s -> s.copy(downloads = s.downloads.filterNot { it.id == id }) }
    }

    // ------------------------------------------------------------------------
    // Progress (опитування, лише поки є активні)
    // ------------------------------------------------------------------------
    private var acc = 0f

    fun tick(delta: Float) {
        if (model.state.downloads.isEmpty()) return
        acc += delta
        if (acc < 0.5f) return
        acc = 0f

        val ids = model.state.downloads.filter { it.status != DownloadStatus.DONE && it.status != DownloadStatus.FAILED }.map { it.id }.toLongArray()
        if (ids.isEmpty()) return
        val progress = bridge.queryDownloads(ids).associateBy { it.id }
        val now = System.currentTimeMillis()

        var changed = false
        val updated = model.state.downloads.map { d ->
            val p = progress[d.id] ?: return@map if (d.id in ids) d.copy(status = DownloadStatus.FAILED, error = Copy.Errors.DOWNLOAD_FAILED).also { changed = true } else d
            lastBytes[d.id]?.let { (b0, t0) -> if (now > t0) speed[d.id] = (p.bytesDone - b0) * 1000f / (now - t0) }
            lastBytes[d.id] = p.bytesDone to now
            val total = if (p.bytesTotal > 0) p.bytesTotal else d.bytesTotal
            val st = if (p.status == DownloadStatus.DONE) DownloadStatus.RUNNING else p.status   // DONE вирішує onFinished
            if (p.bytesDone != d.bytesDone || total != d.bytesTotal || st != d.status) { changed = true; d.copy(bytesDone = p.bytesDone, bytesTotal = total, status = st) } else d
        }
        if (changed) model.update { it.copy(downloads = updated) }

        // Пропущений ACTION_DOWNLOAD_COMPLETE (процес спав) — добиваємо тут
        progress.values.filter { it.status == DownloadStatus.DONE && it.id !in finishing }.forEach { p ->
            onFinished(p.id, p.localUri != null, p.localUri)
        }
        progress.values.filter { it.status == DownloadStatus.FAILED }.forEach { p -> onFinished(p.id, false, null) }
    }

    // ------------------------------------------------------------------------
    // Finish
    // ------------------------------------------------------------------------
    private val finishing = HashSet<Long>()

    fun onFinished(id: Long, success: Boolean, localUri: String?) {
        val d = model.state.downloads.firstOrNull { it.id == id } ?: return
        if (!finishing.add(id)) return

        if (!success || localUri == null) {
            log("download failed: ${d.title}")
            finishing.remove(id)
            val now = System.currentTimeMillis()
            model.update { s ->
                s.copy(downloads = s.downloads.map { if (it.id == id) it.copy(status = DownloadStatus.FAILED, error = Copy.Errors.DOWNLOAD_FAILED) else it })
                    .withEvent(AppEvent("dl-$id-$now", EventKind.FAILED, d.title, url = d.url, at = now))
            }
            toast(Copy.Errors.DOWNLOAD_FAILED)
            return
        }

        bridge.readTags(localUri, d.trackId) { tags ->
            val finishTrack = { coverPath: String? ->
                val track = Track(
                    id         = d.trackId,
                    title      = d.titleFromTags(tags?.title),
                    artist     = tags?.artist ?: d.artist ?: "Unknown artist",
                    durationMs = tags?.durationMs?.takeIf { it > 0 } ?: d.durationMs,
                    sizeBytes  = tags?.sizeBytes?.takeIf { it > 0 } ?: d.bytesTotal.coerceAtLeast(0),
                    format     = d.format ?: "MP3",
                    kind       = d.kind,
                    source     = d.source,
                    sourceUrl  = d.url,
                    localUri   = localUri,
                    coverPath  = tags?.coverPath ?: coverPath,
                    license    = d.license,
                    addedAt    = System.currentTimeMillis(),
                )
                model.update { s ->
                    s.copy(library = s.library.filterNot { it.id == track.id } + track, downloads = s.downloads.filterNot { it.id == id })
                        .withEvent(AppEvent("dl-$id-${track.addedAt}", EventKind.DOWNLOADED, track.title, track.artist, trackId = track.id, at = track.addedAt))
                }
                finishing.remove(id)
                speed.remove(id); lastBytes.remove(id)
                toast(Copy.Toasts.downloaded(track.title))
                onTrackAdded?.invoke()
                log("track added: ${track.title} · ${track.artist} · ${track.format} · cover=${track.coverPath != null}")
            }
            // Немає вбудованої обкладинки, але є з каталогу/фіду — докачуємо
            if (tags?.coverPath == null && d.coverUrl != null) {
                bridge.fetchBytes(d.coverUrl) { bytes -> finishTrack(bytes?.let { bridge.saveCover(d.trackId, it) }) }
            } else finishTrack(null)
        }
    }

    /** Теги кращі за ім'я файлу; але назва з каталогу (artist != null) — краща за обидва. */
    private fun DownloadItem.titleFromTags(tagTitle: String?): String =
        if (source == TrackSource.CATALOG || source == TrackSource.PODCAST) title else tagTitle ?: title

    /** Після холодного старту: черга з минулої сесії. */
    fun resumeAfterStart() {
        val ids = model.state.downloads.map { it.id }.toLongArray()
        if (ids.isEmpty()) return
        val alive = bridge.queryDownloads(ids).map { it.id }.toSet()
        val lost = model.state.downloads.filter { it.id !in alive }
        if (lost.isNotEmpty()) model.update { s -> s.copy(downloads = s.downloads.filter { it.id in alive }) }
    }
}
