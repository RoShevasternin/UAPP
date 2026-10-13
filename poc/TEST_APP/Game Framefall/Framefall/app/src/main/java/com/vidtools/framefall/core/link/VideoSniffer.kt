package com.vidtools.framefall.core.link

import java.net.URLDecoder

// ═════════════════════════════════════════════════════════════════════════════
//  VideoSniffer — чи справді за лінком відео (або аудіо). Вирішує ДО запуску
//  DownloadManager. Порядок: Content-Type → (якщо нічого не каже) перші байти файлу.
//
//  ЧОМУ НЕ ЛИШЕ Content-Type:
//    частина хостингів (S3, CDN, Drive після редиректу) віддає бінарники як
//    application/octet-stream. Якщо вірити лише заголовку, справжній .mp4 відхилимо.
//    Тому octet-stream = «не знаю» → дивимось магію.
//
//  Перші байти беремо GET-запитом з `Range: bytes=0-63` — частина серверів не
//  вміє HEAD, а Range дає і заголовки, і магію за один запит.
// ═════════════════════════════════════════════════════════════════════════════
object VideoSniffer {

    enum class Verdict { MEDIA, NOT_MEDIA, UNKNOWN }

    data class Decision(
        /** Можна качати: відео або аудіо, яке зіграє ExoPlayer. */
        val ok: Boolean,
        /** mp4 / mov / webm / mkv / 3gp / m4a / mp3, якщо вдалося визначити. */
        val ext: String?,
        val fileName: String?,
        val reason: String,
        /** Чому не можна — для тексту помилки. */
        val problem: Problem? = null,
    ) {
        val isAudio: Boolean get() = LinkResolver.isAudioExt(ext)
    }

    enum class Problem { WEB_PAGE, STREAM, UNSUPPORTED_CODEC, NOT_MEDIA }

    // ------------------------------------------------------------------------
    // Content-Type
    // ------------------------------------------------------------------------
    fun byContentType(contentType: String?): Verdict {
        val ct = contentType?.substringBefore(';')?.trim()?.lowercase() ?: return Verdict.UNKNOWN
        return when {
            ct == "video/ogg" || ct == "application/ogg"                       -> Verdict.UNKNOWN  // вирішить магія (Theora — ні)
            ct.startsWith("video/") || ct.startsWith("audio/")                -> Verdict.MEDIA
            ct == "application/vnd.apple.mpegurl" || ct == "application/x-mpegurl" ||
                ct == "application/dash+xml"                                   -> Verdict.NOT_MEDIA
            ct == "application/octet-stream" || ct == "binary/octet-stream" ||
                ct == "application/x-download" || ct == "application/force-download" -> Verdict.UNKNOWN
            ct.startsWith("text/") || ct.startsWith("image/") ||
                ct == "application/json" || ct == "application/xhtml+xml" ||
                ct == "application/pdf"                                        -> Verdict.NOT_MEDIA
            else                                                               -> Verdict.UNKNOWN
        }
    }

    fun extFromContentType(contentType: String?): String? =
        when (contentType?.substringBefore(';')?.trim()?.lowercase()) {
            "video/mp4"                              -> "mp4"
            "video/quicktime"                        -> "mov"
            "video/webm"                             -> "webm"
            "video/x-matroska"                       -> "mkv"
            "video/3gpp"                             -> "3gp"
            "video/x-m4v"                            -> "m4v"
            "audio/mpeg", "audio/mp3"                -> "mp3"
            "audio/mp4", "audio/x-m4a", "audio/m4a"  -> "m4a"
            "audio/aac", "audio/aacp"                -> "aac"
            else                                     -> null
        }

    // ------------------------------------------------------------------------
    // Магія перших байтів
    // ------------------------------------------------------------------------

    /**
     * Формат за першими байтами або null.
     * "html" — сторінка; "hls" — плейлист; "ogg" — Ogg (на Android без Theora).
     */
    fun byMagic(b: ByteArray): String? {
        fun at(i: Int) = if (i < b.size) b[i].toInt() and 0xFF else -1
        fun ascii(from: Int, s: String) = s.indices.all { at(from + it) == s[it].code }

        if (ascii(4, "ftyp")) {
            val brand = String(b.copyOfRange(8, minOf(b.size, 12)), Charsets.ISO_8859_1)
            return when {
                brand.startsWith("M4A") || brand.startsWith("M4B") -> "m4a"
                brand.startsWith("qt")                              -> "mov"
                brand.startsWith("3g")                              -> "3gp"
                else                                                -> "mp4"
            }
        }
        if (at(0) == 0x1A && at(1) == 0x45 && at(2) == 0xDF && at(3) == 0xA3) {
            return if (String(b, Charsets.ISO_8859_1).contains("webm")) "webm" else "mkv"
        }
        if (ascii(0, "RIFF") && ascii(8, "AVI ")) return "avi"
        if (ascii(0, "OggS")) return "ogg"
        if (ascii(0, "ID3")) return "mp3"
        // MPEG audio frame sync: 11 одиничних бітів; ADTS AAC — 12 бітів і layer = 0
        if (at(0) == 0xFF && (at(1) and 0xE0) == 0xE0) return if ((at(1) and 0xF6) == 0xF0) "aac" else "mp3"
        // MPEG-TS: sync-байт 0x47 кожні 188 байт — у 64 байтах видно лише перший
        val head = String(b.copyOfRange(0, minOf(b.size, 64)), Charsets.ISO_8859_1).trimStart().lowercase()
        if (head.startsWith("#extm3u")) return "hls"
        if (head.startsWith("<!doctype html") || head.startsWith("<html") || head.startsWith("<?xml") || head.startsWith("{")) return "html"
        return null
    }

    // ------------------------------------------------------------------------
    // Content-Disposition
    // ------------------------------------------------------------------------

    /** filename*=UTF-8''… має пріоритет над filename="…". Шлях відкидаємо. */
    fun fileNameFromDisposition(cd: String?): String? =
        rawFileNameFromDisposition(cd)?.substringAfterLast('/')?.substringAfterLast('\\')?.trim()?.ifEmpty { null }

    private fun rawFileNameFromDisposition(cd: String?): String? {
        if (cd.isNullOrBlank()) return null
        Regex("""filename\*\s*=\s*([^']*)'[^']*'([^;]+)""", RegexOption.IGNORE_CASE).find(cd)?.let {
            val charset = it.groupValues[1].ifBlank { "UTF-8" }
            return runCatching { URLDecoder.decode(it.groupValues[2].trim().replace("+", "%2B"), charset) }.getOrNull()
        }
        Regex("""filename\s*=\s*"([^"]+)"""", RegexOption.IGNORE_CASE).find(cd)?.let { return it.groupValues[1] }
        Regex("""filename\s*=\s*([^;]+)""", RegexOption.IGNORE_CASE).find(cd)?.let { return it.groupValues[1].trim() }
        return null
    }

    // ------------------------------------------------------------------------
    // Підсумок
    // ------------------------------------------------------------------------
    fun decide(
        contentType: String?,
        contentDisposition: String?,
        urlFileName: String?,
        firstBytes: ByteArray?,
    ): Decision {
        val nameFromCd = fileNameFromDisposition(contentDisposition)
        val fileName   = nameFromCd ?: urlFileName?.takeIf { it.isNotBlank() }
        val extByName  = fileName?.substringAfterLast('.', "")?.lowercase()?.takeIf { it in LinkResolver.ALL_EXT }
        val magic      = firstBytes?.let { byMagic(it) }

        when (magic) {
            "html" -> return Decision(false, null, fileName, "server returned a web page", Problem.WEB_PAGE)
            "hls"  -> return Decision(false, null, fileName, "HLS playlist", Problem.STREAM)
            "ogg", "avi" -> return Decision(false, magic, fileName, "$magic container", Problem.UNSUPPORTED_CODEC)
        }

        return when (byContentType(contentType)) {
            Verdict.MEDIA     -> Decision(true, magic ?: extFromContentType(contentType) ?: extByName ?: "mp4", fileName, "content-type")
            Verdict.NOT_MEDIA -> {
                val stream = contentType?.contains("mpegurl", true) == true || contentType?.contains("dash", true) == true
                Decision(false, null, fileName, "content-type ${contentType?.substringBefore(';')}", if (stream) Problem.STREAM else Problem.WEB_PAGE)
            }
            Verdict.UNKNOWN   -> when {
                magic != null     -> Decision(true, magic, fileName, "magic bytes")
                extByName != null -> Decision(true, extByName, fileName, "file extension")
                else              -> Decision(false, null, fileName, "unknown binary", Problem.NOT_MEDIA)
            }
        }
    }
}
