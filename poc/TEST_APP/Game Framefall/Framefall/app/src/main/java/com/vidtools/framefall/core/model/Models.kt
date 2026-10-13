package com.vidtools.framefall.core.model

import kotlinx.serialization.Serializable

// ═════════════════════════════════════════════════════════════════════════════
//  Модель даних Framefall. Усе @Serializable — зберігається JSON-ом у DataStore
//  (як PlayerModel у T35) і читається з catalog.json (assets).
//
//  Джерело правди для бібліотеки — НАШ індекс (AppState.library), а не MediaStore:
//  після перевстановлення апка втрачає «право власності» на свої файли в
//  MediaStore і без READ_MEDIA_VIDEO їх не бачить. Індекс + localUri надійніші.
// ═════════════════════════════════════════════════════════════════════════════

/** AUDIO — звук, витягнутий у Studio (M4A); решта бібліотеки — відео. */
@Serializable
enum class MediaKind { VIDEO, AUDIO, IMAGE }

@Serializable
enum class VideoSource { LINK, CATALOG, STUDIO, SHARE }

@Serializable
data class Video(
    val id: String,
    val title: String,
    /** Автор / хост: «Blender Foundation», «archive.org», «drive.google.com». */
    val credit: String,
    val durationMs: Long,
    val sizeBytes: Long,
    /** MP4 / WEBM / MOV / MKV / M4A / JPG — для чипа. */
    val format: String,
    val width: Int = 0,
    val height: Int = 0,
    val kind: MediaKind = MediaKind.VIDEO,
    val source: VideoSource = VideoSource.LINK,
    /** Звідки качали (Share, повторне завантаження, «вже збережено» в Discover). */
    val sourceUrl: String? = null,
    /** file:// у Movies/Framefall (DownloadManager або Studio). */
    val localUri: String? = null,
    /** Кадр-мініатюра у filesDir/thumbs/ (MediaMetadataRetriever). */
    val thumbPath: String? = null,
    /** Вбудована мініатюра каталогу (assets/textures/thumbs/<name>.jpg), поки свого кадру немає. */
    val thumbAsset: String? = null,
    /** CC BY 3.0 / Public domain — лише для фільмів каталогу; CC BY вимагає показувати автора. */
    val license: String? = null,
    val addedAt: Long = 0L,
    /** Де зупинились — для «Continue watching». */
    val watchedMs: Long = 0L,
    val lastWatchedAt: Long = 0L,
    /** Studio: з якого відео зроблено і яким інструментом («Trim», «Part 2/4»…). */
    val parentId: String? = null,
    val tool: String? = null,
) {
    val isVideo: Boolean get() = kind == MediaKind.VIDEO
    val aspect: Float get() = if (width > 0 && height > 0) width.toFloat() / height else 16f / 9f
    /** Додивились до кінця (останні 3 %) — у «Continue watching» не показуємо. */
    val progress: Float get() = if (durationMs > 0) (watchedMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    val inProgress: Boolean get() = isVideo && watchedMs > 3_000 && progress < 0.97f
}

@Serializable
enum class DownloadStatus { QUEUED, CHECKING, RUNNING, PAUSED, DONE, FAILED }

@Serializable
data class DownloadItem(
    /** id з DownloadManager.enqueue(). */
    val id: Long,
    val videoId: String,
    val title: String,
    val url: String,
    val bytesDone: Long = 0,
    val bytesTotal: Long = -1,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val error: String? = null,
    // ── Метадані для Video після завершення. Лежать у самому DownloadItem (AppState),
    //    бо ACTION_DOWNLOAD_COMPLETE може прийти, коли процес уже вбито: тоді завершуємо
    //    на наступному старті з того, що збережено. ──
    val credit: String? = null,
    val format: String? = null,
    val kind: MediaKind = MediaKind.VIDEO,
    val source: VideoSource = VideoSource.LINK,
    val license: String? = null,
    val thumbAsset: String? = null,
    /** id фільму каталогу — щоб кнопка в Discover знала, що вже качається. */
    val refId: String? = null,
    val durationMs: Long = 0L,
    val startedAt: Long = 0L,
) {
    val progress: Float get() = if (bytesTotal > 0) (bytesDone.toFloat() / bytesTotal).coerceIn(0f, 1f) else 0f
}

// ------------------------------------------------------------------------
// Каталог Discover (assets/catalog.json): вибрані відкриті фільми
// ------------------------------------------------------------------------
@Serializable
data class Catalog(
    val version: Int = 1,
    val featured: String = "",
    val films: List<CatalogFilm> = emptyList(),
)

@Serializable
data class CatalogFilm(
    val id: String,
    val title: String,
    val by: String,
    val year: Int,
    val license: String,
    /** true — суспільне надбання (зелений чип), false — Creative Commons (синій). */
    val pd: Boolean = false,
    val durationSec: Int = 0,
    val about: String = "",
    /** assets/textures/thumbs/<thumb>.jpg */
    val thumb: String,
    /** Сторінка твору (атрибуція CC BY). */
    val page: String = "",
    val files: List<CatalogFile> = emptyList(),
) {
    /** Що качаємо кнопкою «Save»: найменший MP4 — швидко і грає скрізь. */
    val defaultFile: CatalogFile? get() = files.filter { it.format.startsWith("MP4") }.minByOrNull { it.sizeBytes } ?: files.firstOrNull()
}

@Serializable
data class CatalogFile(
    val label: String,
    val dim: String = "",
    val sizeBytes: Long = 0,
    val format: String = "MP4",
    val url: String,
)

// ------------------------------------------------------------------------
// Центр подій (дзвіночок на Save): лише всередині апки
// ------------------------------------------------------------------------
@Serializable
enum class EventKind { DOWNLOADED, FAILED, CLIP_LINK, STUDIO_DONE }

@Serializable
data class AppEvent(
    val id: String,
    val kind: EventKind,
    val title: String,
    val detail: String = "",
    /** FAILED / CLIP_LINK — що повторити або зберегти. */
    val url: String? = null,
    /** DOWNLOADED / STUDIO_DONE — відео в бібліотеці. */
    val videoId: String? = null,
    val at: Long,
)

/** Найновіші зверху, не більше [max]; той самий лінк з буфера не дублюється — піднімається вгору. */
fun AppState.withEvent(e: AppEvent, max: Int = 30): AppState {
    val rest = if (e.kind == EventKind.CLIP_LINK && e.url != null) events.filterNot { it.kind == EventKind.CLIP_LINK && it.url == e.url } else events
    return copy(events = (listOf(e) + rest).take(max))
}

/** Скільки подій ще не бачили (крапка на дзвіночку). */
val AppState.unseenEvents: Int get() = events.count { it.at > eventsSeenAt }

// ------------------------------------------------------------------------
// Збережений стан апки (один JSON у DataStore)
// ------------------------------------------------------------------------
@Serializable
data class AppState(
    val onboarded: Boolean = false,
    val library: List<Video> = emptyList(),
    val downloads: List<DownloadItem> = emptyList(),
    val clipLastSeenTimestamp: Long = -1L,
    val clipDismissed: List<String> = emptyList(),
    val events: List<AppEvent> = emptyList(),
    val eventsSeenAt: Long = 0L,
    /** Фон головного екрана: ключ вбудованої петлі (HomeBg.ORION…) або шлях до своєї петлі зі Studio. */
    val homeBg: String = HomeBg.ORION,
    /** Settings: картка лінка з буфера і «Continue watching» на головному екрані. */
    val showClipCard: Boolean = true,
    val showContinue: Boolean = true,
    /** Відео, з яким працює Studio (вибране востаннє). */
    val studioVideoId: String? = null,
)

/** Вбудовані фони головного екрана (assets/video/bg_<key>.mp4). */
object HomeBg {
    const val ORION = "orion"
    const val SINTEL = "sintel"
    val BUILT_IN = listOf(ORION, SINTEL)
    fun isBuiltIn(v: String) = v in BUILT_IN
}
