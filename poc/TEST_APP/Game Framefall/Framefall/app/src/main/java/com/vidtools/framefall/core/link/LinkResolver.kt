package com.vidtools.framefall.core.link

import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.security.MessageDigest

// ═════════════════════════════════════════════════════════════════════════════
//  LinkResolver — що за лінк вставив користувач (або що лежить у буфері).
//
//  Порт resolveLink() з прототипу Framefall. Чиста функція: ні мережі, ні Android.
//  Мережеву перевірку (заголовки + перші байти) робить уже завантажувач — VideoSniffer.
//
//  ПОРЯДОК ПЕРЕВІРОК ВАЖЛИВИЙ:
//    1. заборонені сервіси (YouTube, Instagram, TikTok…) — завжди першими, навіть якщо
//       в URL випадково є ".mp4";
//    2. відкриті архіви (Internet Archive, NASA, Wikimedia Commons) — свої API / прямі файли;
//    3. хмари (Drive, Dropbox, OneDrive) — у них свої правила прямого лінка;
//    4. стріми (.m3u8 / .mpd) — v1 чесно каже, що не зберігає;
//    5. розширення відео/аудіо;
//    6. інакше — WebPage: вирішить перевірка заголовків.
// ═════════════════════════════════════════════════════════════════════════════

sealed interface ResolvedLink {

    /** Текст без лінка або зламаний URL. */
    data object Invalid : ResolvedLink

    /** Сервіс, умови якого забороняють завантаження. Картку на Home НЕ показуємо. */
    data class Blocked(val service: String, val url: String, val host: String) : ResolvedLink

    /** Прямий файл: .mp4 / .webm / .mov / .mkv / .m4v / .3gp (або аудіо .mp3 / .m4a). */
    data class DirectVideo(val url: String, val host: String, val fileName: String, val ext: String) : ResolvedLink

    /** [downloadUrl] — перша спроба; великі файли Drive віддають HTML-підтвердження, див. GoogleDrive. */
    data class GoogleDrive(val url: String, val fileId: String, val downloadUrl: String) : ResolvedLink

    data class Dropbox(val url: String, val downloadUrl: String, val fileName: String) : ResolvedLink

    /** 1drv.ms — короткий лінк, справжній URL з'являється лише після редиректу. */
    data class OneDrive(val url: String) : ResolvedLink

    /** archive.org/details/<id> — список файлів дає archive.org/metadata/<id>. */
    data class ArchiveItem(val url: String, val identifier: String) : ResolvedLink

    /** images.nasa.gov/details/<nasa_id> — файли дає images-api.nasa.gov/asset/<nasa_id>. */
    data class NasaItem(val url: String, val nasaId: String) : ResolvedLink

    /** commons.wikimedia.org/wiki/File:… — прямий файл на upload.wikimedia.org (шлях з md5 імені). */
    data class CommonsFile(val url: String, val fileName: String, val directUrl: String, val ext: String) : ResolvedLink

    /** HLS / DASH-стрім. У v1 не зберігаємо (сегменти, часто DRM) — пояснюємо. */
    data class Stream(val url: String, val host: String, val kind: String) : ResolvedLink

    /** Невідомо що. Завантажувач спершу питає сервер (заголовки + перші байти). */
    data class WebPage(val url: String, val host: String) : ResolvedLink
}

val ResolvedLink.isDownloadable: Boolean
    get() = when (this) {
        is ResolvedLink.DirectVideo, is ResolvedLink.GoogleDrive, is ResolvedLink.Dropbox,
        is ResolvedLink.OneDrive, is ResolvedLink.ArchiveItem, is ResolvedLink.NasaItem,
        is ResolvedLink.CommonsFile -> true
        else -> false
    }

/** Мітка-чип для картки: MP4 / DRIVE / DROPBOX / ONEDRIVE / ARCHIVE / NASA / COMMONS / HLS. */
val ResolvedLink.chipLabel: String
    get() = when (this) {
        is ResolvedLink.DirectVideo -> ext.uppercase()
        is ResolvedLink.GoogleDrive -> "DRIVE"
        is ResolvedLink.Dropbox     -> "DROPBOX"
        is ResolvedLink.OneDrive    -> "ONEDRIVE"
        is ResolvedLink.ArchiveItem -> "ARCHIVE"
        is ResolvedLink.NasaItem    -> "NASA"
        is ResolvedLink.CommonsFile -> "COMMONS"
        is ResolvedLink.Stream      -> kind
        else                        -> ""
    }

/** Хост для рядка «Copied link · archive.org». */
val ResolvedLink.hostLabel: String
    get() = when (this) {
        is ResolvedLink.DirectVideo -> host
        is ResolvedLink.GoogleDrive -> "drive.google.com"
        is ResolvedLink.Dropbox     -> "dropbox.com"
        is ResolvedLink.OneDrive    -> "onedrive.live.com"
        is ResolvedLink.ArchiveItem -> "archive.org"
        is ResolvedLink.NasaItem    -> "images.nasa.gov"
        is ResolvedLink.CommonsFile -> "commons.wikimedia.org"
        is ResolvedLink.Stream      -> host
        is ResolvedLink.WebPage     -> host
        is ResolvedLink.Blocked     -> host
        ResolvedLink.Invalid        -> ""
    }

/** Вихідний URL, як його вставили (для дедуплікації «вже показували / закрили»). */
val ResolvedLink.sourceUrl: String?
    get() = when (this) {
        ResolvedLink.Invalid           -> null
        is ResolvedLink.Blocked        -> url
        is ResolvedLink.DirectVideo    -> url
        is ResolvedLink.GoogleDrive    -> url
        is ResolvedLink.Dropbox        -> url
        is ResolvedLink.OneDrive       -> url
        is ResolvedLink.ArchiveItem    -> url
        is ResolvedLink.NasaItem       -> url
        is ResolvedLink.CommonsFile    -> url
        is ResolvedLink.Stream         -> url
        is ResolvedLink.WebPage        -> url
    }

object LinkResolver {

    /** Те, що вміє зіграти ExoPlayer на будь-якому Android 7+ (Theora .ogv — ні). */
    val VIDEO_EXT = setOf("mp4", "m4v", "mov", "webm", "mkv", "3gp")
    val AUDIO_EXT = setOf("mp3", "m4a", "aac")
    val ALL_EXT = VIDEO_EXT + AUDIO_EXT

    private val BLOCKED = listOf(
        Regex("""(^|\.)youtube\.com$|(^|\.)youtu\.be$|(^|\.)youtube-nocookie\.com$""") to "YouTube",
        Regex("""(^|\.)instagram\.com$|^instagr\.am$""")            to "Instagram",
        Regex("""(^|\.)facebook\.com$|^fb\.watch$|^fb\.com$""")     to "Facebook",
        Regex("""(^|\.)tiktok\.com$""")                             to "TikTok",
        Regex("""^(x|twitter)\.com$|^t\.co$""")                     to "X",
        Regex("""(^|\.)vimeo\.com$""")                              to "Vimeo",
        Regex("""(^|\.)twitch\.tv$""")                              to "Twitch",
        Regex("""(^|\.)dailymotion\.com$|^dai\.ly$""")              to "Dailymotion",
        Regex("""(^|\.)netflix\.com$""")                            to "Netflix",
        Regex("""(^|\.)disneyplus\.com$""")                         to "Disney+",
        Regex("""(^|\.)hulu\.com$""")                               to "Hulu",
        Regex("""(^|\.)primevideo\.com$""")                         to "Prime Video",
        Regex("""^(play\.)?max\.com$|(^|\.)hbomax\.com$""")         to "Max",
        Regex("""(^|\.)snapchat\.com$""")                           to "Snapchat",
        Regex("""(^|\.)pinterest\.[a-z.]+$|^pin\.it$""")            to "Pinterest",
        Regex("""(^|\.)reddit\.com$|^v\.redd\.it$|^redd\.it$""")    to "Reddit",
        Regex("""(^|\.)bilibili\.(com|tv)$""")                      to "Bilibili",
        Regex("""(^|\.)vk\.(com|ru)$|^vkvideo\.ru$""")              to "VK",
        Regex("""(^|\.)ok\.ru$""")                                  to "OK",
        Regex("""(^|\.)rumble\.com$""")                             to "Rumble",
        Regex("""(^|\.)kick\.com$""")                               to "Kick",
        Regex("""(^|\.)spotify\.com$""")                            to "Spotify",
    )

    private val URL_IN_TEXT  = Regex("""https?://\S+|www\.\S+""", RegexOption.IGNORE_CASE)
    private val TRAILING     = Regex("""[),.;!?]+$""")
    private val HOST_PREFIX  = Regex("""^(www|m)\.""")
    private val EXT          = Regex("""\.([a-z0-9]{2,4})$""", RegexOption.IGNORE_CASE)
    private val DRIVE_FILE   = Regex("""/file/d/([\w-]+)""")
    private val ARCHIVE_ITEM = Regex("""^/(details|download)/([^/?#]+)""")
    private val NASA_ITEM    = Regex("""^/details[-/]([^/?#]+)""")
    private val COMMONS_FILE = Regex("""^/wiki/File:(.+)$""")

    fun resolve(raw: String?): ResolvedLink {
        val text  = raw?.trim().orEmpty()
        val found = URL_IN_TEXT.find(text) ?: return ResolvedLink.Invalid

        var s = found.value.replace(TRAILING, "")
        if (s.startsWith("www.", ignoreCase = true)) s = "https://$s"

        val uri  = parse(s) ?: return ResolvedLink.Invalid
        val host = uri.host?.lowercase()?.replaceFirst(HOST_PREFIX, "") ?: return ResolvedLink.Invalid
        val path = uri.path ?: ""

        BLOCKED.firstOrNull { it.first.containsMatchIn(host) }?.let { return ResolvedLink.Blocked(it.second, s, host) }

        val last = path.substringAfterLast('/')
        val ext  = EXT.find(last)?.groupValues?.get(1)?.lowercase().orEmpty()

        // ── Відкриті архіви ──
        if (host == "archive.org") {
            val m = ARCHIVE_ITEM.find(path)
            if (m != null) {
                // archive.org/download/<id>/<file>.mp4 — це вже прямий файл
                if (m.groupValues[1] == "download" && ext in ALL_EXT) return ResolvedLink.DirectVideo(s, host, decode(last), ext)
                return ResolvedLink.ArchiveItem(s, m.groupValues[2])
            }
        }
        if (host == "images.nasa.gov") {
            NASA_ITEM.find(path)?.let { return ResolvedLink.NasaItem(s, decode(it.groupValues[1])) }
        }
        if (host == "commons.wikimedia.org") {
            COMMONS_FILE.find(path)?.let { m ->
                val name = decode(m.groupValues[1]).replace(' ', '_')
                val e = name.substringAfterLast('.', "").lowercase()
                if (e in ALL_EXT) return ResolvedLink.CommonsFile(s, name, Commons.directUrl(name), e)
            }
        }

        // ── Хмари ──
        if (host == "drive.google.com") {
            val id = DRIVE_FILE.find(path)?.groupValues?.get(1) ?: queryParam(uri, "id")
            if (!id.isNullOrEmpty()) return ResolvedLink.GoogleDrive(s, id, GoogleDrive.firstTryUrl(id))
        }
        if (host == "dropbox.com" || host.endsWith(".dropbox.com")) {
            return ResolvedLink.Dropbox(s, withQueryParam(uri, "dl", "1"), decode(last))
        }
        if (host == "1drv.ms" || host.endsWith("onedrive.live.com")) return ResolvedLink.OneDrive(s)

        // ── Стріми ──
        if (ext == "m3u8") return ResolvedLink.Stream(s, host, "HLS")
        if (ext == "mpd") return ResolvedLink.Stream(s, host, "DASH")

        if (ext in ALL_EXT) return ResolvedLink.DirectVideo(s, host, decode(last), ext)

        return ResolvedLink.WebPage(s, host)
    }

    /** "elephants_dream-1080p.mp4" → "Elephants Dream 1080p". Порожньо → "Saved video". */
    fun titleFromFileName(file: String?): String {
        val t = (file ?: "").replace(Regex("""\.[a-z0-9]{2,4}$""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""[-_]+"""), " ").replace(Regex("""\s+"""), " ").trim()
        if (t.isEmpty()) return "Saved video"
        return Regex("""\b\w""").replace(t) { it.value.uppercase() }
    }

    fun isAudioExt(ext: String?) = ext?.lowercase() in AUDIO_EXT

    // ------------------------------------------------------------------------
    // URL helpers
    // ------------------------------------------------------------------------

    /** java.net.URI суворий до пробілів — їх кодуємо, решту лишаємо як є. */
    private fun parse(s: String): URI? {
        val uri = try { URI(s.replace(" ", "%20")) } catch (e: URISyntaxException) { return null }
        val scheme = uri.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") return null
        if (uri.host.isNullOrEmpty()) return null
        return uri
    }

    private fun decode(s: String): String = runCatching { URLDecoder.decode(s.replace("+", "%2B"), "UTF-8") }.getOrDefault(s)

    private fun queryParam(uri: URI, name: String): String? =
        uri.rawQuery?.split('&')?.map { it.split('=', limit = 2) }
            ?.firstOrNull { it[0] == name }?.getOrNull(1)

    /** Ставить або замінює параметр запиту. Решта URL лишається байт у байт. */
    internal fun withQueryParam(uri: URI, name: String, value: String): String {
        val params = uri.rawQuery?.split('&')?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf()
        val i = params.indexOfFirst { it.substringBefore('=') == name }
        if (i >= 0) params[i] = "$name=$value" else params.add("$name=$value")
        val base = buildString {
            append(uri.scheme).append("://").append(uri.rawAuthority)
            append(uri.rawPath ?: "")
        }
        val frag = uri.rawFragment?.let { "#$it" } ?: ""
        return "$base?${params.joinToString("&")}$frag"
    }
}

// ═════════════════════════════════════════════════════════════════════════════
//  Google Drive: два кроки.
//  1) drive.google.com/uc?export=download&id=… — для малих файлів одразу файл.
//  2) Для великих (≈ >100 МБ, «не можемо перевірити на віруси») приходить HTML-
//     сторінка підтвердження. Тоді повторюємо на drive.usercontent.google.com з
//     confirm=t. ◌ Формат сторінки Google змінює — перевіряти на девайсі.
// ═════════════════════════════════════════════════════════════════════════════
object GoogleDrive {
    fun firstTryUrl(id: String) = "https://drive.google.com/uc?export=download&id=$id"
    fun confirmedUrl(id: String) = "https://drive.usercontent.google.com/download?id=$id&export=download&confirm=t"
}

// ═════════════════════════════════════════════════════════════════════════════
//  Wikimedia Commons: файл лежить на upload.wikimedia.org/wikipedia/commons/a/ab/<ім'я>,
//  де «a/ab» — перші 1 і 2 hex-символи md5 імені (пробіли → «_»). Так Commons
//  розкладає всі файли, тож прямий лінк рахується без запиту до API.
// ═════════════════════════════════════════════════════════════════════════════
object Commons {
    fun directUrl(fileName: String): String {
        val name = fileName.replace(' ', '_')
        val md5 = MessageDigest.getInstance("MD5").digest(name.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
        val enc = java.net.URLEncoder.encode(name, "UTF-8").replace("+", "%20")
        return "https://upload.wikimedia.org/wikipedia/commons/${md5[0]}/${md5.substring(0, 2)}/$enc"
    }
}
