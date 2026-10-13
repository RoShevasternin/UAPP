package com.vidtools.framefall.game.controller

import com.vidtools.framefall.core.copy.Copy
import com.vidtools.framefall.core.link.LinkResolver
import com.vidtools.framefall.core.link.OpenArchives
import com.vidtools.framefall.core.link.ResolvedLink
import com.vidtools.framefall.core.link.VideoSniffer
import com.vidtools.framefall.core.link.hostLabel
import com.vidtools.framefall.core.link.sourceUrl
import com.vidtools.framefall.core.model.AppEvent
import com.vidtools.framefall.core.model.DownloadItem
import com.vidtools.framefall.core.model.DownloadStatus
import com.vidtools.framefall.core.model.EventKind
import com.vidtools.framefall.core.model.MediaKind
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.core.model.VideoSource
import com.vidtools.framefall.core.model.withEvent
import com.vidtools.framefall.game.model.AppModel
import com.vidtools.framefall.game.platform.DownloadRequest
import com.vidtools.framefall.game.platform.PlatformBridge
import com.vidtools.framefall.util.log
import java.util.UUID

// ═════════════════════════════════════════════════════════════════════════════
//  DownloadController — від вставленого тексту до відео в бібліотеці.
//
//    submit(text) → LinkResolver:
//      Invalid / Blocked / Stream      → помилка, нічого не качаємо
//      DirectVideo / Dropbox / Commons → одразу DownloadManager
//      ArchiveItem / NasaItem          → список файлів з API → шторка якості (Choose)
//      WebPage / Drive / OneDrive      → «Checking the link…» → probe → VideoSniffer
//    DownloadManager → прогрес опитуванням (tick, раз на 0.5 с) →
//    onFinished → метадані + мініатюра (readVideoMeta) → Video в бібліотеці → тост.
// ═════════════════════════════════════════════════════════════════════════════
class DownloadController(
    private val model : AppModel,
    private val bridge: PlatformBridge,
    private val toast : (String) -> Unit,
) {

    sealed interface Submit {
        data object Checking : Submit
        data class Error(val title: String, val body: String) : Submit
        /** Сторінка твору (Archive / NASA): обрати файл у шторці якості. */
        data class Choose(val item: OpenArchives.Item, val sourceUrl: String, val host: String) : Submit
        data class Started(val title: String) : Submit
    }

    /** Швидкість (байт/с) по id — для рядка «24.1 of 61.9 MB · 1.8 MB/s». */
    val speed = HashMap<Long, Float>()
    private val lastBytes = HashMap<Long, Pair<Long, Long>>()

    /** Відео додано в бібліотеку (для лічильника інтерстішала — GDXGame). */
    var onVideoAdded: (() -> Unit)? = null

    // ------------------------------------------------------------------------
    // Submit
    // ------------------------------------------------------------------------
    fun submit(text: String, onState: (Submit) -> Unit) {
        val link = LinkResolver.resolve(text)
        if (link !is ResolvedLink.Invalid && link !is ResolvedLink.Blocked && link !is ResolvedLink.Stream) {
            // dev-host (adb reverse) працює й без мережі на девайсі
            val local = link.sourceUrl?.let { it.startsWith("http://127.0.0.1") || it.startsWith("http://localhost") } == true
            if (!bridge.isOnline() && !local) { onState(Submit.Error(Copy.Errors.NO_NETWORK_TITLE, Copy.Errors.NO_NETWORK_BODY)); return }
        }
        startLink(link, onState)
    }

    /** Лінк з картки буфера / шеру — та сама логіка, що й submit, але з готовим ResolvedLink. */
    fun startLink(link: ResolvedLink, onState: (Submit) -> Unit) {
        when (link) {
            ResolvedLink.Invalid -> onState(Submit.Error(Copy.Errors.NOT_A_LINK_TITLE, Copy.Errors.NOT_A_LINK_BODY))
            is ResolvedLink.Blocked -> onState(Submit.Error(Copy.Errors.blockedTitle(link.service), Copy.Errors.BLOCKED_BODY))
            is ResolvedLink.Stream -> onState(Submit.Error(Copy.Errors.STREAM_TITLE, Copy.Errors.STREAM_BODY))
            is ResolvedLink.DirectVideo -> {
                if (alreadySaved(link.url, onState)) return
                val t = enqueue(link.url, link.fileName, LinkResolver.titleFromFileName(link.fileName), link.ext, credit = link.host)
                onState(Submit.Started(t))
            }
            is ResolvedLink.Dropbox -> {
                if (alreadySaved(link.url, onState)) return
                val ext = link.fileName.substringAfterLast('.', "mp4").lowercase()
                val t = enqueue(link.downloadUrl, link.fileName.ifBlank { "dropbox.$ext" }, LinkResolver.titleFromFileName(link.fileName), ext, credit = "Dropbox", sourceUrl = link.url)
                onState(Submit.Started(t))
            }
            is ResolvedLink.CommonsFile -> {
                if (alreadySaved(link.url, onState)) return
                val t = enqueue(link.directUrl, link.fileName, LinkResolver.titleFromFileName(link.fileName), link.ext, credit = "Wikimedia Commons", sourceUrl = link.url)
                onState(Submit.Started(t))
            }
            is ResolvedLink.ArchiveItem -> loadItem(OpenArchives.archiveMetadataUrl(link.identifier), link, onState) { OpenArchives.parseArchive(link.identifier, it) }
            is ResolvedLink.NasaItem    -> loadItem(OpenArchives.nasaAssetUrl(link.nasaId), link, onState) { OpenArchives.parseNasa(link.nasaId, it) }
            is ResolvedLink.GoogleDrive -> probeThenEnqueue(link.downloadUrl, link.url, "Google Drive", onState) { firstFailed ->
                // Великі файли Drive → HTML-підтвердження → повтор на confirmedUrl
                if (firstFailed) probeThenEnqueue(com.vidtools.framefall.core.link.GoogleDrive.confirmedUrl(link.fileId), link.url, "Google Drive", onState, null)
            }
            is ResolvedLink.OneDrive -> probeThenEnqueue(withDownloadParam(link.url), link.url, "OneDrive", onState, null)
            is ResolvedLink.WebPage  -> probeThenEnqueue(link.url, link.url, link.host, onState, null)
        }
        if (link is ResolvedLink.Blocked || link is ResolvedLink.Invalid) return
        link.sourceUrl?.let { bridge.dismissClipLink(it) }
    }

    private fun alreadySaved(url: String, onState: (Submit) -> Unit): Boolean {
        if (model.state.library.none { it.sourceUrl == url } && model.state.downloads.none { it.url == url }) return false
        toast(Copy.Toasts.ALREADY_SAVED)
        onState(Submit.Started(""))
        return true
    }

    private fun loadItem(apiUrl: String, link: ResolvedLink, onState: (Submit) -> Unit, parse: (String) -> OpenArchives.Item?) {
        onState(Submit.Checking)
        bridge.fetchText(apiUrl) { r ->
            val item = r.getOrNull()?.let(parse)
            when {
                r.isFailure -> onState(Submit.Error(Copy.Errors.NO_NETWORK_TITLE, Copy.Errors.NO_NETWORK_BODY))
                item == null || item.files.isEmpty() -> onState(Submit.Error(Copy.Errors.NO_FILES_TITLE, Copy.Errors.NO_FILES_BODY))
                else -> onState(Submit.Choose(item, link.sourceUrl ?: apiUrl, link.hostLabel))
            }
        }
    }

    /** Файл зі шторки якості. */
    fun saveChosen(c: Submit.Choose, file: OpenArchives.RemoteFile): String =
        enqueue(file.url, file.url.substringAfterLast('/').substringBefore('?').let { java.net.URLDecoder.decode(it, "UTF-8") },
            c.item.title, file.ext, credit = c.item.credit, sizeHint = file.sizeBytes, sourceUrl = file.url)

    private fun probeThenEnqueue(url: String, sourceUrl: String, credit: String, onState: (Submit) -> Unit, onNotMedia: ((Boolean) -> Unit)?) {
        onState(Submit.Checking)
        bridge.probe(url) { r ->
            val d = r.decision
            if (d.ok) {
                val ext  = d.ext ?: "mp4"
                val name = d.fileName?.takeIf { it.contains('.') } ?: "${d.fileName ?: "video"}.$ext"
                val t = enqueue(r.finalUrl, name, LinkResolver.titleFromFileName(name), ext, credit = credit, sizeHint = r.sizeBytes, sourceUrl = sourceUrl)
                onState(Submit.Started(t))
            } else if (onNotMedia != null && d.problem == VideoSniffer.Problem.WEB_PAGE) {
                onNotMedia(true)
            } else if (r.httpCode < 0) {
                onState(Submit.Error(Copy.Errors.NO_NETWORK_TITLE, Copy.Errors.NO_NETWORK_BODY))
            } else when (d.problem) {
                VideoSniffer.Problem.STREAM            -> onState(Submit.Error(Copy.Errors.STREAM_TITLE, Copy.Errors.STREAM_BODY))
                VideoSniffer.Problem.UNSUPPORTED_CODEC -> onState(Submit.Error(Copy.Errors.CODEC_TITLE, Copy.Errors.CODEC_BODY))
                else                                   -> onState(Submit.Error(Copy.Errors.NOT_VIDEO_TITLE, Copy.Errors.NOT_VIDEO_BODY))
            }
        }
    }

    private fun withDownloadParam(url: String) = if ('?' in url) "$url&download=1" else "$url?download=1"

    // ------------------------------------------------------------------------
    // Enqueue
    // ------------------------------------------------------------------------
    fun enqueue(
        url: String, fileName: String, title: String, ext: String,
        credit: String? = null, source: VideoSource = VideoSource.LINK,
        license: String? = null, thumbAsset: String? = null, refId: String? = null,
        durationMs: Long = 0L, sizeHint: Long = -1L, sourceUrl: String = url,
    ): String {
        val e = ext.lowercase()
        val mime = when (e) {
            "mp4", "m4v" -> "video/mp4"; "webm" -> "video/webm"; "mov" -> "video/quicktime"; "mkv" -> "video/x-matroska"
            "3gp" -> "video/3gpp"; "mp3" -> "audio/mpeg"; "m4a", "aac" -> "audio/mp4"; else -> null
        }
        val name = if (fileName.substringAfterLast('.', "").lowercase() == e) fileName else "$fileName.$e"
        val id = bridge.enqueueDownload(DownloadRequest(url, name, title, mime))
        val item = DownloadItem(
            id = id, videoId = UUID.randomUUID().toString(), title = title, url = sourceUrl,
            bytesTotal = sizeHint, status = DownloadStatus.QUEUED,
            credit = credit, format = e.uppercase(), kind = if (LinkResolver.isAudioExt(e)) MediaKind.AUDIO else MediaKind.VIDEO,
            source = source, license = license, thumbAsset = thumbAsset, refId = refId,
            durationMs = durationMs, startedAt = System.currentTimeMillis(),
        )
        model.update { it.copy(downloads = it.downloads + item) }
        toast(Copy.Toasts.saving(title))
        return title
    }

    fun cancel(id: Long) {
        bridge.cancelDownload(id)
        model.update { s -> s.copy(downloads = s.downloads.filterNot { it.id == id }) }
    }

    /** Повтор невдалого: той самий URL — новий запис DownloadManager. */
    fun retry(d: DownloadItem) {
        model.update { s -> s.copy(downloads = s.downloads.filterNot { it.id == d.id }) }
        submit(d.url) {}
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
            // DownloadManager оновлює байти раз на ~1–2 с: між оновленнями швидкість не обнуляємо,
            // а рахуємо від останньої зміни
            val last = lastBytes[d.id]
            if (last == null) lastBytes[d.id] = p.bytesDone to now
            else if (p.bytesDone != last.first && now > last.second) {
                speed[d.id] = (p.bytesDone - last.first) * 1000f / (now - last.second)
                lastBytes[d.id] = p.bytesDone to now
            }
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

        bridge.readVideoMeta(localUri, d.videoId) { meta ->
            val kind = when {
                meta != null && !meta.hasVideo -> MediaKind.AUDIO
                else -> d.kind
            }
            val v = Video(
                id         = d.videoId,
                title      = if (d.source == VideoSource.CATALOG) d.title else meta?.title?.takeIf { it.length in 2..80 } ?: d.title,
                credit     = d.credit ?: "Saved link",
                durationMs = meta?.durationMs?.takeIf { it > 0 } ?: d.durationMs,
                sizeBytes  = meta?.sizeBytes?.takeIf { it > 0 } ?: d.bytesTotal.coerceAtLeast(0),
                format     = d.format ?: "MP4",
                width      = meta?.width ?: 0,
                height     = meta?.height ?: 0,
                kind       = kind,
                source     = d.source,
                sourceUrl  = d.url,
                localUri   = localUri,
                thumbPath  = meta?.thumbPath,
                thumbAsset = d.thumbAsset,
                license    = d.license,
                addedAt    = System.currentTimeMillis(),
            )
            model.update { s ->
                s.copy(library = s.library.filterNot { it.id == v.id } + v, downloads = s.downloads.filterNot { it.id == id })
                    .withEvent(AppEvent("dl-$id-${v.addedAt}", EventKind.DOWNLOADED, v.title, v.credit, videoId = v.id, at = v.addedAt))
            }
            finishing.remove(id)
            speed.remove(id); lastBytes.remove(id)
            toast(Copy.Toasts.saved(v.title))
            onVideoAdded?.invoke()
            log("video added: ${v.title} · ${v.format} ${v.width}×${v.height} · ${v.durationMs / 1000}s · thumb=${v.thumbPath != null}")
        }
    }

    /** Після холодного старту: черга з минулої сесії. */
    fun resumeAfterStart() {
        val ids = model.state.downloads.map { it.id }.toLongArray()
        if (ids.isEmpty()) return
        val alive = bridge.queryDownloads(ids).map { it.id }.toSet()
        val lost = model.state.downloads.filter { it.id !in alive }
        if (lost.isNotEmpty()) model.update { s -> s.copy(downloads = s.downloads.filter { it.id in alive }) }
    }
}
