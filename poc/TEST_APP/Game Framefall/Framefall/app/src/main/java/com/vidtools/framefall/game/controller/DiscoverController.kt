package com.vidtools.framefall.game.controller

import com.badlogic.gdx.Gdx
import com.vidtools.framefall.core.model.Catalog
import com.vidtools.framefall.core.model.CatalogCodec
import com.vidtools.framefall.core.model.CatalogFile
import com.vidtools.framefall.core.model.CatalogFilm
import com.vidtools.framefall.core.model.VideoSource
import com.vidtools.framefall.game.model.AppModel
import com.vidtools.framefall.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  DiscoverController — вибрані відкриті фільми (assets/catalog.json): Blender
//  Foundation (CC BY 3.0), NASA, класика в суспільному надбанні. Файли — прямі
//  лінки Internet Archive / NASA, тож каталог працює без нашого хостингу.
//  Стан кнопки фільму: NONE → RUNNING (кільце) → DONE (Watch).
// ═════════════════════════════════════════════════════════════════════════════
class DiscoverController(
    private val model    : AppModel,
    private val downloads: DownloadController,
) {
    val catalog: Catalog by lazy {
        runCatching { CatalogCodec.decode(Gdx.files.internal("catalog.json").readString("UTF-8")) }
            .onFailure { log("catalog: ${it.message}") }
            .getOrDefault(Catalog())
    }

    val featured: CatalogFilm? get() = catalog.films.firstOrNull { it.id == catalog.featured } ?: catalog.films.firstOrNull()
    val openMovies: List<CatalogFilm> get() = catalog.films.filter { !it.pd && it.id != catalog.featured }
    val publicDomain: List<CatalogFilm> get() = catalog.films.filter { it.pd }

    fun film(id: String?) = catalog.films.firstOrNull { it.id == id }

    // ------------------------------------------------------------------------
    // Стан кнопки
    // ------------------------------------------------------------------------
    sealed interface ItemState {
        data object None : ItemState
        data class Running(val progress: Float) : ItemState
        data class Done(val videoId: String) : ItemState
    }

    fun stateOf(f: CatalogFilm): ItemState {
        val urls = f.files.map { it.url }.toSet()
        model.state.library.firstOrNull { it.sourceUrl in urls }?.let { return ItemState.Done(it.id) }
        model.state.downloads.firstOrNull { it.refId == f.id }?.let { return ItemState.Running(it.progress) }
        return ItemState.None
    }

    fun download(f: CatalogFilm, file: CatalogFile? = f.defaultFile) {
        file ?: return
        if (stateOf(f) != ItemState.None) return
        val ext = file.url.substringAfterLast('.').substringBefore('?').lowercase().takeIf { it.length in 2..4 } ?: "mp4"
        val name = "${f.title.replace(Regex("""[\\/:*?"<>|·]"""), " ").trim()} (${file.label}).$ext"
        downloads.enqueue(
            url = file.url, fileName = name, title = f.title, ext = ext, credit = "${f.by} · ${f.year}",
            source = VideoSource.CATALOG, license = f.license, thumbAsset = f.thumb, refId = f.id,
            durationMs = f.durationSec * 1000L, sizeHint = file.sizeBytes,
        )
    }
}
