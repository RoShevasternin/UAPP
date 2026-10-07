package com.redwave.downloader.core.model

import kotlinx.serialization.Serializable

// ═════════════════════════════════════════════════════════════════════════════
//  Модель даних Redwave. Усе @Serializable — зберігається JSON-ом у DataStore
//  (як PlayerModel у T35) і читається з catalog.json / RSS.
//
//  Джерело правди для бібліотеки — НАШ індекс (LibraryState), а не MediaStore:
//  після перевстановлення апка втрачає «право власності» на свої файли в
//  MediaStore і без READ_MEDIA_AUDIO їх не бачить. Індекс + localUri надійніші.
// ═════════════════════════════════════════════════════════════════════════════

@Serializable
enum class TrackKind { SONG, PODCAST }

@Serializable
enum class TrackSource { LINK, CATALOG, PODCAST, SHARE }

@Serializable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val durationMs: Long,
    val sizeBytes: Long,
    /** MP3 / M4A / FLAC / OGG / OPUS / WAV — для чипа у списку. */
    val format: String,
    val kind: TrackKind = TrackKind.SONG,
    val source: TrackSource = TrackSource.LINK,
    /** Звідки качали (для «Share» і повторного завантаження). */
    val sourceUrl: String? = null,
    /** content:// від DownloadManager / MediaStore. */
    val localUri: String? = null,
    /** Файл обкладинки в filesDir/covers/ (витягнута з тегів або завантажена). */
    val coverPath: String? = null,
    /** CC BY / CC0 … — лише для треків з каталогу; CC BY вимагає показувати автора. */
    val license: String? = null,
    val addedAt: Long = 0L,
)

@Serializable
enum class DownloadStatus { QUEUED, CHECKING, RUNNING, PAUSED, DONE, FAILED }

@Serializable
data class DownloadItem(
    /** id з DownloadManager.enqueue(); до enqueue — від'ємний тимчасовий. */
    val id: Long,
    val trackId: String,
    val title: String,
    val url: String,
    val bytesDone: Long = 0,
    val bytesTotal: Long = -1,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val error: String? = null,
    // ── Метадані для Track після завершення. Лежать у самому DownloadItem (AppState),
    //    бо ACTION_DOWNLOAD_COMPLETE може прийти, коли процес уже вбито: тоді завершуємо
    //    на наступному старті з того, що збережено. ──
    val artist: String? = null,
    val format: String? = null,
    val kind: TrackKind = TrackKind.SONG,
    val source: TrackSource = TrackSource.LINK,
    val license: String? = null,
    val coverUrl: String? = null,
    /** id треку каталогу / guid епізоду — щоб кнопка в Discover знала, що вже качається. */
    val refId: String? = null,
    val durationMs: Long = 0L,
    val startedAt: Long = 0L,
) {
    val progress: Float get() = if (bytesTotal > 0) (bytesDone.toFloat() / bytesTotal).coerceIn(0f, 1f) else 0f
}

// ------------------------------------------------------------------------
// Каталог Discover (catalog.json)
// ------------------------------------------------------------------------
@Serializable
data class Catalog(
    val version: Int = 1,
    val tracks: List<CatalogTrack> = emptyList(),
    val featured: List<FeaturedPlaylist> = emptyList(),
)

@Serializable
data class CatalogTrack(
    val id: String,
    val title: String,
    val artist: String,
    val license: String,
    val mood: List<String> = emptyList(),
    val durationSec: Int = 0,
    val sizeBytes: Long = 0,
    val format: String = "MP3",
    val url: String,
    val cover: String? = null,
)

@Serializable
data class FeaturedPlaylist(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val mood: String = "",
    val cover: String? = null,
)

// ------------------------------------------------------------------------
// Подкасти (RSS)
// ------------------------------------------------------------------------
@Serializable
data class PodcastFeed(
    val url: String,
    val title: String,
    val imageUrl: String? = null,
    val episodes: List<Episode> = emptyList(),
)

@Serializable
data class Episode(
    val guid: String,
    val title: String,
    val audioUrl: String,
    val lengthBytes: Long = -1,
    val mimeType: String? = null,
    val durationSec: Int? = null,
    val pubDate: String? = null,
)

// ------------------------------------------------------------------------
// Збережений стан апки (один JSON у DataStore)
// ------------------------------------------------------------------------
@Serializable
data class AppState(
    val onboarded: Boolean = false,
    val library: List<Track> = emptyList(),
    val downloads: List<DownloadItem> = emptyList(),
    val favorites: Set<String> = emptySet(),
    val eqPreset: String = "Flat",
    val eqBandsDb: List<Float> = listOf(0f, 0f, 0f, 0f, 0f),
    val eqEnabled: Boolean = true,
    val clipLastSeenTimestamp: Long = -1L,
    val clipDismissed: List<String> = emptyList(),
    val lastPlayedId: String? = null,
    val lastPositionMs: Long = 0L,
)
