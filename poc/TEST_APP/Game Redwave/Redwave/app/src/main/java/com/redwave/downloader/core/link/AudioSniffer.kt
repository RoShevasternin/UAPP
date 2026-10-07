package com.redwave.downloader.core.link

import java.net.URLDecoder

// ═════════════════════════════════════════════════════════════════════════════
//  AudioSniffer — чи справді за лінком аудіо. Вирішує ДО запуску DownloadManager.
//
//  Порядок: Content-Type → (якщо нічого не каже) перші байти файлу.
//
//  ЧОМУ НЕ ЛИШЕ Content-Type:
//    частина хостингів (S3, CDN, файлообмінники, Drive після редиректу) віддає
//    бінарники як application/octet-stream. Якщо вірити лише заголовку, справжній
//    .mp3 відхилимо. Тому octet-stream = «не знаю» → дивимось магію.
//    (raw.githubusercontent.com, заміряно 06.10.2026: аудіо за розширенням як
//    audio/*, але .mp4/.adts — octet-stream, а .rss/.json — text/plain.)
//
//  Перші байти беремо GET-запитом з `Range: bytes=0-63` — частина серверів не
//  вміє HEAD, а Range дає і заголовки, і магію за один запит.
// ═════════════════════════════════════════════════════════════════════════════
object AudioSniffer {

    enum class Verdict { AUDIO, NOT_AUDIO, UNKNOWN }

    data class Decision(
        val isAudio: Boolean,
        /** mp3 / m4a / aac / ogg / opus / flac / wav, якщо вдалося визначити. */
        val ext: String?,
        val fileName: String?,
        val reason: String,
    )

    // ------------------------------------------------------------------------
    // Content-Type
    // ------------------------------------------------------------------------
    fun byContentType(contentType: String?): Verdict {
        val ct = contentType?.substringBefore(';')?.trim()?.lowercase() ?: return Verdict.UNKNOWN
        return when {
            ct.startsWith("audio/")                                   -> Verdict.AUDIO
            ct == "application/ogg" || ct == "application/x-flac"     -> Verdict.AUDIO
            // m4a нерідко приходить як video/mp4 — вирішить магія
            ct == "video/mp4"                                         -> Verdict.UNKNOWN
            ct == "application/octet-stream" || ct == "binary/octet-stream" ||
                ct == "application/x-download" || ct == "application/force-download" -> Verdict.UNKNOWN
            ct.startsWith("text/") || ct.startsWith("image/") ||
                ct == "application/json" || ct == "application/xhtml+xml" ||
                ct == "application/pdf"                               -> Verdict.NOT_AUDIO
            else                                                      -> Verdict.UNKNOWN
        }
    }

    fun extFromContentType(contentType: String?): String? =
        when (contentType?.substringBefore(';')?.trim()?.lowercase()) {
            "audio/mpeg", "audio/mp3"                -> "mp3"
            "audio/mp4", "audio/x-m4a", "audio/m4a"  -> "m4a"
            "audio/aac", "audio/aacp"                -> "aac"
            "audio/ogg", "application/ogg"           -> "ogg"
            "audio/opus"                             -> "opus"
            "audio/flac", "audio/x-flac", "application/x-flac" -> "flac"
            "audio/wav", "audio/x-wav", "audio/wave" -> "wav"
            else                                     -> null
        }

    // ------------------------------------------------------------------------
    // Магія перших байтів
    // ------------------------------------------------------------------------

    /** Формат за першими байтами або null. "html" — явно не аудіо. */
    fun byMagic(b: ByteArray): String? {
        fun at(i: Int) = if (i < b.size) b[i].toInt() and 0xFF else -1
        fun ascii(from: Int, s: String) = s.indices.all { at(from + it) == s[it].code }

        if (ascii(0, "ID3")) return "mp3"
        if (ascii(0, "fLaC")) return "flac"
        if (ascii(0, "OggS")) return if (containsAscii(b, "OpusHead")) "opus" else "ogg"
        if (ascii(0, "RIFF") && ascii(8, "WAVE")) return "wav"
        if (ascii(4, "ftyp")) return "m4a"
        // MPEG audio frame sync: 11 одиничних бітів; ADTS AAC — 12 бітів і layer = 0
        if (at(0) == 0xFF && (at(1) and 0xE0) == 0xE0) {
            return if ((at(1) and 0xF6) == 0xF0) "aac" else "mp3"
        }
        val head = String(b.copyOfRange(0, minOf(b.size, 64)), Charsets.ISO_8859_1).trimStart().lowercase()
        if (head.startsWith("<!doctype html") || head.startsWith("<html") || head.startsWith("<?xml") || head.startsWith("{")) return "html"
        return null
    }

    private fun containsAscii(b: ByteArray, s: String): Boolean =
        String(b, Charsets.ISO_8859_1).contains(s)

    // ------------------------------------------------------------------------
    // Content-Disposition
    // ------------------------------------------------------------------------

    /**
     * filename*=UTF-8''… має пріоритет над filename="…".
     * Шлях відкидаємо: raw.githubusercontent.com віддає
     * `filename=poc/TEST_APP/Game Redwave/test-media/neon-heart.mp3` (заміряно 07.10.2026).
     */
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
        val extByName  = fileName?.substringAfterLast('.', "")?.lowercase()?.takeIf { it in LinkResolver.AUDIO_EXT }
        val magic      = firstBytes?.let { byMagic(it) }

        if (magic == "html") return Decision(false, null, fileName, "server returned a web page")

        return when (byContentType(contentType)) {
            Verdict.AUDIO     -> Decision(true, extFromContentType(contentType) ?: magic ?: extByName, fileName, "content-type")
            Verdict.NOT_AUDIO -> Decision(false, null, fileName, "content-type ${contentType?.substringBefore(';')}")
            Verdict.UNKNOWN   -> when {
                magic != null     -> Decision(true, magic, fileName, "magic bytes")
                extByName != null -> Decision(true, extByName, fileName, "file extension")
                else              -> Decision(false, null, fileName, "unknown binary")
            }
        }
    }
}
