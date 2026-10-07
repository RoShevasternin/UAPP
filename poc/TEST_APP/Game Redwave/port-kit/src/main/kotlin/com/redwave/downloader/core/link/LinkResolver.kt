package com.redwave.downloader.core.link

import java.net.URI
import java.net.URISyntaxException

// ═════════════════════════════════════════════════════════════════════════════
//  LinkResolver — що за лінк вставив користувач (або що лежить у буфері).
//
//  Порт resolveLink() з прототипу (prototype/index.html), поведінка один в один.
//  Чиста функція: ні мережі, ні Android. Мережеву перевірку (HEAD, перші байти)
//  робить уже завантажувач — див. AudioSniffer.
//
//  ПОРЯДОК ПЕРЕВІРОК ВАЖЛИВИЙ:
//    1. заборонені сервіси (YouTube, Spotify…) — завжди першими, навіть якщо
//       в URL випадково є ".mp3";
//    2. хмари (Drive, Dropbox, OneDrive) — у них свої правила прямого лінка;
//    3. розширення аудіофайлу;
//    4. схоже на RSS;
//    5. інакше — WebPage: вирішить HEAD-запит.
// ═════════════════════════════════════════════════════════════════════════════

sealed interface ResolvedLink {

    /** Текст без лінка або зламаний URL. */
    data object Invalid : ResolvedLink

    /** Сервіс, умови якого забороняють завантаження. Картку на Home НЕ показуємо. */
    data class Blocked(val service: String, val url: String, val host: String) : ResolvedLink

    data class DirectAudio(val url: String, val host: String, val fileName: String, val ext: String) : ResolvedLink

    /** [downloadUrl] — перша спроба; великі файли Drive віддають HTML-підтвердження, див. GoogleDrive. */
    data class GoogleDrive(val url: String, val fileId: String, val downloadUrl: String) : ResolvedLink

    data class Dropbox(val url: String, val downloadUrl: String, val fileName: String) : ResolvedLink

    /** 1drv.ms — короткий лінк, справжній URL з'являється лише після редиректу. */
    data class OneDrive(val url: String) : ResolvedLink

    data class PodcastFeed(val url: String, val host: String) : ResolvedLink

    /** Невідомо що. Завантажувач спершу питає сервер (HEAD / перші байти). */
    data class WebPage(val url: String, val host: String) : ResolvedLink
}

val ResolvedLink.isDownloadable: Boolean
    get() = when (this) {
        is ResolvedLink.DirectAudio, is ResolvedLink.GoogleDrive, is ResolvedLink.Dropbox,
        is ResolvedLink.OneDrive, is ResolvedLink.PodcastFeed -> true
        else -> false
    }

/** Мітка-чип для картки: MP3 / DRIVE / DROPBOX / ONEDRIVE / RSS. */
val ResolvedLink.chipLabel: String
    get() = when (this) {
        is ResolvedLink.DirectAudio -> ext.uppercase()
        is ResolvedLink.GoogleDrive -> "DRIVE"
        is ResolvedLink.Dropbox     -> "DROPBOX"
        is ResolvedLink.OneDrive    -> "ONEDRIVE"
        is ResolvedLink.PodcastFeed -> "RSS"
        else                        -> ""
    }

/** Вихідний URL, як його вставили (для дедуплікації «вже показували / закрили»). */
val ResolvedLink.sourceUrl: String?
    get() = when (this) {
        ResolvedLink.Invalid           -> null
        is ResolvedLink.Blocked        -> url
        is ResolvedLink.DirectAudio    -> url
        is ResolvedLink.GoogleDrive    -> url
        is ResolvedLink.Dropbox        -> url
        is ResolvedLink.OneDrive       -> url
        is ResolvedLink.PodcastFeed    -> url
        is ResolvedLink.WebPage        -> url
    }

object LinkResolver {

    val AUDIO_EXT = setOf("mp3", "m4a", "aac", "ogg", "opus", "flac", "wav")

    private val BLOCKED = listOf(
        Regex("""(^|\.)youtube\.com$|(^|\.)youtu\.be$""")        to "YouTube",
        Regex("""(^|\.)spotify\.com$|^spotify\.link$""")          to "Spotify",
        Regex("""(^|\.)soundcloud\.com$""")                       to "SoundCloud",
        Regex("""^music\.apple\.com$""")                          to "Apple Music",
        Regex("""(^|\.)deezer\.com$|^deezer\.page\.link$""")      to "Deezer",
        Regex("""(^|\.)vk\.com$|(^|\.)vk\.ru$""")                 to "VK",
        Regex("""(^|\.)tiktok\.com$""")                           to "TikTok",
        Regex("""^music\.yandex\.""")                             to "Yandex Music",
    )

    private val URL_IN_TEXT  = Regex("""https?://\S+|www\.\S+""", RegexOption.IGNORE_CASE)
    private val TRAILING     = Regex("""[),.;!?]+$""")
    private val HOST_PREFIX  = Regex("""^(www|m)\.""")
    private val EXT          = Regex("""\.([a-z0-9]{2,4})$""", RegexOption.IGNORE_CASE)
    private val DRIVE_FILE   = Regex("""/file/d/([\w-]+)""")
    private val FEED_PATH    = Regex("""/(feed|rss)/?$""")
    private val FEED_HOST    = Regex("""^feeds?\.""")
    private val FEED_FILE    = Regex("""\.(rss|xml)$""")

    fun resolve(raw: String?): ResolvedLink {
        val text  = raw?.trim().orEmpty()
        val found = URL_IN_TEXT.find(text) ?: return ResolvedLink.Invalid

        var s = found.value.replace(TRAILING, "")
        if (s.startsWith("www.", ignoreCase = true)) s = "https://$s"

        val uri  = parse(s) ?: return ResolvedLink.Invalid
        val host = uri.host?.lowercase()?.replaceFirst(HOST_PREFIX, "") ?: return ResolvedLink.Invalid

        BLOCKED.firstOrNull { it.first.containsMatchIn(host) }?.let { return ResolvedLink.Blocked(it.second, s, host) }

        val last = (uri.path ?: "").substringAfterLast('/')
        val ext  = EXT.find(last)?.groupValues?.get(1)?.lowercase().orEmpty()

        if (host == "drive.google.com") {
            val id = DRIVE_FILE.find(uri.path ?: "")?.groupValues?.get(1) ?: queryParam(uri, "id")
            if (!id.isNullOrEmpty()) return ResolvedLink.GoogleDrive(s, id, GoogleDrive.firstTryUrl(id))
        }
        if (host == "dropbox.com" || host.endsWith(".dropbox.com")) {
            return ResolvedLink.Dropbox(s, withQueryParam(uri, "dl", "1"), last)
        }
        if (host == "1drv.ms" || host.endsWith("onedrive.live.com")) return ResolvedLink.OneDrive(s)

        if (ext in AUDIO_EXT) return ResolvedLink.DirectAudio(s, host, last, ext)

        if (FEED_FILE.containsMatchIn(last) || FEED_PATH.containsMatchIn(uri.path ?: "") || FEED_HOST.containsMatchIn(host)) {
            return ResolvedLink.PodcastFeed(s, host)
        }
        return ResolvedLink.WebPage(s, host)
    }

    /** "night_drive-remix.mp3" → "Night Drive Remix". Порожньо → "Shared audio". */
    fun titleFromFileName(file: String?): String {
        val t = (file ?: "").replace(Regex("""\.[a-z0-9]{2,4}$""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""[-_]+"""), " ").trim()
        if (t.isEmpty()) return "Shared audio"
        return Regex("""\b\w""").replace(t) { it.value.uppercase() }
    }

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
//     confirm=t. ◌ Перевірити на девайсі: формат сторінки Google змінює.
// ═════════════════════════════════════════════════════════════════════════════
object GoogleDrive {
    fun firstTryUrl(id: String) = "https://drive.google.com/uc?export=download&id=$id"
    fun confirmedUrl(id: String) = "https://drive.usercontent.google.com/download?id=$id&export=download&confirm=t"
}
