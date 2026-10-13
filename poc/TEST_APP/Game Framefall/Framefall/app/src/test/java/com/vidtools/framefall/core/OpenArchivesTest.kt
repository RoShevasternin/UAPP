package com.vidtools.framefall.core

import com.vidtools.framefall.core.link.OpenArchives
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Сторінка твору → файли для шторки якості (формат відповідей заміряно 13.10.2026). */
class OpenArchivesTest {

    @Test fun archiveMetadata() {
        val json = """
            {"metadata":{"title":"Elephants Dream","creator":["Orange Open Movie Project"]},
             "files":[
               {"name":"ed_hd.mp4","size":"67800000","format":"h.264","width":"640","height":"360"},
               {"name":"ed_hd_512kb.mp4","size":"47311688","format":"512Kb MPEG4","width":"426","height":"240"},
               {"name":"ed_hd.ogv","size":"45700000","format":"Ogg Video"},
               {"name":"ed.jpg","size":"1000","format":"JPEG"}
             ]}
        """.trimIndent()
        val item = assertNotNull(OpenArchives.parseArchive("ElephantsDream", json))
        assertEquals("Elephants Dream", item.title)
        assertEquals("Orange Open Movie Project", item.credit)
        // .ogv (Theora) і картинки — ні; менший файл — першим
        assertEquals(listOf("240p", "360p"), item.files.map { it.label })
        assertEquals("https://archive.org/download/ElephantsDream/ed_hd_512kb.mp4", item.files.first().url)
        assertEquals("MP4 · 512 kbps", item.files.first().format)
    }

    @Test fun archivePathsAreEncoded() {
        val json = """{"files":[{"name":"Caminandes_ Llamigos-1080p.webm","size":"38041423","format":"WebM","width":"1920","height":"1080"}]}"""
        val f = assertNotNull(OpenArchives.parseArchive("CaminandesLlamigos", json)).files.single()
        assertEquals("https://archive.org/download/CaminandesLlamigos/Caminandes_%20Llamigos-1080p.webm", f.url)
    }

    @Test fun nasaRenditions() {
        val base = "http://images-assets.nasa.gov/video/x/x"
        val json = """{"collection":{"items":[{"href":"$base~orig.mp4"},{"href":"$base~mobile.mp4"},{"href":"$base~medium.mp4"},{"href":"$base~thumb.jpg"}]}}"""
        val item = assertNotNull(OpenArchives.parseNasa("x", json))
        assertEquals(listOf("Mobile", "Medium", "Original"), item.files.map { it.label })
        assertTrue(item.files.all { it.url.startsWith("https://") })
    }
}
