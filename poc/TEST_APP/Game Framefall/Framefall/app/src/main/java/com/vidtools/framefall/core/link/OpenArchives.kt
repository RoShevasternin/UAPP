package com.vidtools.framefall.core.link

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URLEncoder

// ═════════════════════════════════════════════════════════════════════════════
//  OpenArchives — сторінка твору в Internet Archive / NASA → список файлів, які
//  можна зберегти (шторка якості). Чисті функції над текстом JSON: мережу робить
//  AndroidBridge.fetchText, тут лише розбір — тестується JVM-тестом.
//
//  • archive.org/metadata/<id>        → files[] (name, size, format, width, height, length)
//  • images-api.nasa.gov/asset/<id>   → collection.items[].href (~mobile / ~small / ~medium / ~large / ~orig.mp4)
// ═════════════════════════════════════════════════════════════════════════════
object OpenArchives {

    data class RemoteFile(
        /** «360p», «Mobile» — головне в рядку шторки. */
        val label: String,
        val url: String,
        val sizeBytes: Long,
        /** «MP4 · H.264», «WebM» */
        val format: String,
        /** «640×360» або порожньо. */
        val dim: String,
        val ext: String,
    )

    data class Item(val title: String, val credit: String, val files: List<RemoteFile>)

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun archiveMetadataUrl(identifier: String) = "https://archive.org/metadata/${URLEncoder.encode(identifier, "UTF-8")}"
    fun nasaAssetUrl(nasaId: String) = "https://images-api.nasa.gov/asset/${URLEncoder.encode(nasaId, "UTF-8")}"

    // ------------------------------------------------------------------------
    // Internet Archive
    // ------------------------------------------------------------------------
    fun parseArchive(identifier: String, text: String): Item? = runCatching {
        val root = json.parseToJsonElement(text).jsonObject
        val meta = root["metadata"] as? JsonObject
        val title = meta?.str("title") ?: identifier
        val credit = meta?.str("creator") ?: "archive.org"
        val files = (root["files"] as? JsonArray).orEmpty().mapNotNull { el ->
            val f = el.jsonObject
            val name = f.str("name") ?: return@mapNotNull null
            val ext = name.substringAfterLast('.', "").lowercase()
            if (ext !in LinkResolver.ALL_EXT) return@mapNotNull null
            val size = f.str("size")?.toLongOrNull() ?: 0L
            val w = f.str("width")?.toIntOrNull() ?: 0
            val h = f.str("height")?.toIntOrNull() ?: 0
            val fmt = f.str("format").orEmpty()
            RemoteFile(
                label = if (h > 0) "${h}p" else ext.uppercase(),
                url = "https://archive.org/download/${enc(identifier)}/${name.split('/').joinToString("/") { enc(it) }}",
                sizeBytes = size,
                format = prettyFormat(ext, fmt),
                dim = if (w > 0 && h > 0) "$w×$h" else "",
                ext = ext,
            )
        }
            // Відео — спершу, далі від меншого до більшого: мобільний інтернет
            .sortedWith(compareBy<RemoteFile> { LinkResolver.isAudioExt(it.ext) }.thenBy { it.sizeBytes })
            .take(8)
        Item(title, credit, files)
    }.getOrNull()

    private fun prettyFormat(ext: String, archiveFormat: String): String = when {
        archiveFormat.contains("512Kb", true) -> "MP4 · 512 kbps"
        archiveFormat.contains("h.264", true) -> "MP4 · H.264"
        ext == "webm" -> "WebM"
        ext == "mkv"  -> "MKV"
        ext == "mov"  -> "MOV"
        else -> ext.uppercase()
    }

    // ------------------------------------------------------------------------
    // NASA Image and Video Library
    // ------------------------------------------------------------------------
    private val NASA_RENDITIONS = listOf("mobile" to "Mobile", "small" to "Small", "preview" to "Preview", "medium" to "Medium", "large" to "Large", "orig" to "Original")

    fun parseNasa(nasaId: String, text: String): Item? = runCatching {
        val items = json.parseToJsonElement(text).jsonObject["collection"]!!.jsonObject["items"]!!.jsonArray
        val hrefs = items.mapNotNull { it.jsonObject.str("href") }.filter { it.endsWith(".mp4", true) }
        val files = NASA_RENDITIONS.mapNotNull { (key, label) ->
            val href = hrefs.firstOrNull { it.contains("~$key.mp4") } ?: return@mapNotNull null
            RemoteFile(label, href.replaceFirst("http://", "https://"), 0L, "MP4", "", "mp4")
        }
        Item(nasaId.replace('_', ' '), "NASA", files)
    }.getOrNull()

    private fun JsonObject.str(k: String): String? = runCatching {
        val v = this[k] ?: return null
        if (v is JsonArray) v.firstOrNull()?.jsonPrimitive?.contentOrNull else v.jsonPrimitive.contentOrNull
    }.getOrNull()?.trim()?.ifEmpty { null }

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8").replace("+", "%20")
}
