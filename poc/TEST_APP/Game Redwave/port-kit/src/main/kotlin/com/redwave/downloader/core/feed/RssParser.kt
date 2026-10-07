package com.redwave.downloader.core.feed

import com.redwave.downloader.core.model.Episode
import com.redwave.downloader.core.model.PodcastFeed
import org.w3c.dom.Element
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

// ═════════════════════════════════════════════════════════════════════════════
//  RssParser — RSS 2.0 подкасту → PodcastFeed.
//
//  DOM (javax.xml) свідомо: він є і на JVM (тести), і на Android, а фіди
//  невеликі. DOCTYPE вимикаємо — захист від XXE; на Android частина фіч
//  фабрики не підтримується, тому кожна в runCatching.
//
//  Епізод без <enclosure> пропускаємо: качати нічого.
// ═════════════════════════════════════════════════════════════════════════════
object RssParser {

    private const val ITUNES = "http://www.itunes.com/dtds/podcast-1.0.dtd"

    fun parse(feedUrl: String, input: InputStream, limit: Int = 50): PodcastFeed {
        val f = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            runCatching { setFeature("http://apache.org/xml/features/disallow-doctype-decl", true) }
            runCatching { setFeature("http://xml.org/sax/features/external-general-entities", false) }
            runCatching { setFeature("http://xml.org/sax/features/external-parameter-entities", false) }
            runCatching { isExpandEntityReferences = false }
        }
        val doc = f.newDocumentBuilder().parse(input)
        val channel = doc.getElementsByTagName("channel").item(0) as? Element
            ?: throw IllegalArgumentException("Not an RSS feed: no <channel>")

        val title = channel.childText("title") ?: "Podcast"
        val image = channel.firstChild(ITUNES, "image")?.getAttribute("href")?.ifBlank { null }
            ?: (channel.firstChild(null, "image"))?.childText("url")

        val items = channel.getElementsByTagName("item")
        val episodes = (0 until items.length).mapNotNull { i ->
            val item = items.item(i) as Element
            val enc  = item.firstChild(null, "enclosure") ?: return@mapNotNull null
            val url  = enc.getAttribute("url").ifBlank { return@mapNotNull null }
            Episode(
                guid        = item.childText("guid") ?: url,
                title       = item.childText("title") ?: "Episode",
                audioUrl    = url,
                lengthBytes = enc.getAttribute("length").toLongOrNull() ?: -1,
                mimeType    = enc.getAttribute("type").ifBlank { null },
                durationSec = parseDuration(item.firstChild(ITUNES, "duration")?.textContent),
                pubDate     = item.childText("pubDate"),
            )
        }.take(limit)

        return PodcastFeed(feedUrl, title, image, episodes)
    }

    /** itunes:duration буває "2840", "47:20" або "1:02:03". */
    fun parseDuration(s: String?): Int? {
        val parts = s?.trim()?.split(':')?.map { it.toIntOrNull() ?: return null } ?: return null
        if (parts.isEmpty() || parts.size > 3) return null
        return parts.fold(0) { acc, p -> acc * 60 + p }
    }

    // ------------------------------------------------------------------------
    // DOM helpers: лише прямі діти, щоб <title> епізоду не плутати з <image><title>
    // ------------------------------------------------------------------------
    private fun Element.firstChild(ns: String?, local: String): Element? {
        var n = firstChild
        while (n != null) {
            if (n is Element) {
                val name = n.localName ?: n.nodeName
                val nsOk = if (ns == null) n.namespaceURI.isNullOrEmpty() else n.namespaceURI == ns
                if (name == local && nsOk) return n
            }
            n = n.nextSibling
        }
        return null
    }

    private fun Element.childText(local: String): String? =
        firstChild(null, local)?.textContent?.trim()?.ifBlank { null }
}
