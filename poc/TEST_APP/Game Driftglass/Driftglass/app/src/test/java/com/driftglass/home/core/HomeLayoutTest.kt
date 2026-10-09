package com.driftglass.home.core

import com.driftglass.home.core.logic.FolderKind
import com.driftglass.home.core.logic.HomeEntry
import com.driftglass.home.core.logic.HomeLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeLayoutTest {

    private data class A(val pkg: String, val label: String, val cat: Int = -1, val sys: Boolean = false)

    private fun build(vararg a: A) = HomeLayout.build(a.toList(), { it.pkg }, { it.label }, { it.cat }, { it.sys })

    @Test fun google_games_tools_social_media_folders() {
        val r = build(
            A("com.google.android.gm", "Gmail", sys = true), A("com.google.android.youtube", "YouTube", HomeLayout.CAT_VIDEO, true),
            A("com.android.chrome", "Chrome", sys = true),
            A("com.supercell.clash", "Clash", HomeLayout.CAT_GAME), A("com.king.candy", "Candy", HomeLayout.CAT_GAME),
            A("com.miui.calculator", "Calculator", sys = true), A("com.android.deskclock", "Clock", sys = true),
            A("org.telegram.messenger", "Telegram", HomeLayout.CAT_SOCIAL), A("com.instagram.android", "Instagram", HomeLayout.CAT_SOCIAL),
            A("com.spotify.music", "Spotify", HomeLayout.CAT_AUDIO), A("com.netflix.mediaclient", "Netflix", HomeLayout.CAT_VIDEO),
            A("com.anydesk", "AnyDesk"),
        )
        val kinds = r.filterIsInstance<HomeEntry.Folder<A>>().map { it.kind }
        assertEquals(listOf(FolderKind.GOOGLE, FolderKind.TOOLS, FolderKind.GAMES, FolderKind.SOCIAL, FolderKind.MEDIA), kinds)
        val google = r.first() as HomeEntry.Folder<A>
        assertEquals(listOf("Chrome", "Gmail", "YouTube"), google.apps.map { it.label })
        assertEquals(HomeEntry.App(A("com.anydesk", "AnyDesk")), r.last())
    }

    @Test fun single_app_folder_falls_back_to_loose_sorted() {
        val r = build(A("com.zeta", "Zeta"), A("com.king.candy", "Candy", HomeLayout.CAT_GAME), A("com.alpha", "alpha"))
        assertTrue(r.all { it is HomeEntry.App })
        assertEquals(listOf("alpha", "Candy", "Zeta"), r.map { (it as HomeEntry.App<A>).app.label })
    }

    @Test fun settings_and_gallery_stay_loose() {
        assertNull(HomeLayout.folderOf("com.android.settings", -1, true))
        assertNull(HomeLayout.folderOf("com.miui.gallery", HomeLayout.CAT_IMAGE, true))
        assertEquals(FolderKind.TOOLS, HomeLayout.folderOf("com.miui.notes", -1, true))
    }

    @Test fun rows_and_pages() {
        assertEquals(4, HomeLayout.rowsFor(400f, 95f))
        assertEquals(1, HomeLayout.rowsFor(10f, 95f))
        val p = HomeLayout.pages((1..37).toList(), 4, 4)
        assertEquals(3, p.size); assertEquals(16, p[0].size); assertEquals(5, p[2].size)
        assertEquals(1, HomeLayout.pages(emptyList<Int>(), 4, 4).size)
    }
}
