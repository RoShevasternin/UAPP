package com.redwave.downloader.game.controller

import com.redwave.downloader.core.feed.RssParser
import com.redwave.downloader.core.model.Catalog
import com.redwave.downloader.core.model.CatalogCodec
import com.redwave.downloader.core.model.CatalogTrack
import com.redwave.downloader.core.model.DownloadItem
import com.redwave.downloader.core.model.Episode
import com.redwave.downloader.core.model.PodcastFeed
import com.redwave.downloader.core.model.TrackKind
import com.redwave.downloader.core.model.TrackSource
import com.redwave.downloader.game.model.AppModel
import com.redwave.downloader.game.platform.PlatformBridge
import com.redwave.downloader.game.utils.Sources
import com.redwave.downloader.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  DiscoverController — каталог CC-музики (catalog.json) і подкасти (RSS).
//  Стан кнопки треку: NONE → RUNNING (кільце) → DONE (галочка, тап = грати).
// ═════════════════════════════════════════════════════════════════════════════
class DiscoverController(
    private val model    : AppModel,
    private val bridge   : PlatformBridge,
    private val downloads: DownloadController,
) {
    enum class Load { IDLE, LOADING, READY, ERROR }

    var catalog: Catalog? = null
        private set
    var catalogLoad = Load.IDLE
        private set

    val feeds = LinkedHashMap<String, PodcastFeed>()
    val feedLoad = HashMap<String, Load>()

    /** Зростає на кожну зміну (каталог / фіди) — як AppModel.version. */
    var version = 0
        private set

    fun loadCatalog(force: Boolean = false) {
        if (!force && (catalogLoad == Load.LOADING || catalogLoad == Load.READY)) return
        catalogLoad = Load.LOADING; version++
        bridge.fetchText(Sources.CATALOG_URL) { r ->
            r.onSuccess { text ->
                catalog = runCatching { CatalogCodec.decode(text) }.getOrNull()
                catalogLoad = if (catalog != null) Load.READY else Load.ERROR
            }.onFailure { catalogLoad = Load.ERROR; log("catalog: ${it.message}") }
            version++
        }
        Sources.FEEDS.forEach { loadFeed(it) }
    }

    fun loadFeed(url: String, force: Boolean = false) {
        if (!force && (feedLoad[url] == Load.LOADING || feedLoad[url] == Load.READY)) return
        feedLoad[url] = Load.LOADING; version++
        bridge.fetchText(url) { r ->
            r.onSuccess { text ->
                val feed = runCatching { RssParser.parse(url, text.byteInputStream()) }.onFailure { log("rss: ${it.message}") }.getOrNull()
                if (feed != null) { feeds[url] = feed; feedLoad[url] = Load.READY } else feedLoad[url] = Load.ERROR
            }.onFailure { feedLoad[url] = Load.ERROR }
            version++
        }
    }

    fun tracks(mood: String): List<CatalogTrack> {
        val all = catalog?.tracks.orEmpty()
        return if (mood == "All") all else all.filter { mood in it.mood }
    }

    // ------------------------------------------------------------------------
    // Стан кнопки
    // ------------------------------------------------------------------------
    sealed interface ItemState {
        data object None : ItemState
        data class Running(val progress: Float) : ItemState
        data class Done(val trackId: String) : ItemState
    }

    fun stateOf(refId: String, url: String): ItemState {
        model.state.library.firstOrNull { it.sourceUrl == url }?.let { return ItemState.Done(it.id) }
        model.state.downloads.firstOrNull { it.refId == refId }?.let { return ItemState.Running(it.progress) }
        return ItemState.None
    }

    fun download(t: CatalogTrack) {
        if (stateOf(t.id, t.url) != ItemState.None) return
        val ext = t.format.lowercase()
        val file = t.url.substringAfterLast('/').substringBefore('?').ifBlank { "${t.title}.$ext" }
        downloads.enqueue(
            url = t.url, fileName = file, title = t.title, ext = ext, artist = t.artist,
            source = TrackSource.CATALOG, license = t.license, coverUrl = t.cover, refId = t.id,
            durationMs = t.durationSec * 1000L, sizeHint = t.sizeBytes,
        )
    }

    fun download(feed: PodcastFeed, e: Episode) {
        if (stateOf(e.guid, e.audioUrl) != ItemState.None) return
        val ext = e.audioUrl.substringAfterLast('.').substringBefore('?').lowercase().takeIf { it.length in 2..4 } ?: "mp3"
        val file = e.audioUrl.substringAfterLast('/').substringBefore('?').ifBlank { "episode.$ext" }
        downloads.enqueue(
            url = e.audioUrl, fileName = file, title = e.title, ext = ext, artist = feed.title,
            kind = TrackKind.PODCAST, source = TrackSource.PODCAST, coverUrl = feed.imageUrl, refId = e.guid,
            durationMs = (e.durationSec ?: 0) * 1000L, sizeHint = e.lengthBytes,
        )
    }

    @Suppress("unused") private fun DownloadItem.isFor(refId: String) = this.refId == refId
}
