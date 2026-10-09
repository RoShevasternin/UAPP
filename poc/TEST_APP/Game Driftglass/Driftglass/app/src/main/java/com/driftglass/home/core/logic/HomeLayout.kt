package com.driftglass.home.core.logic

// ═════════════════════════════════════════════════════════════════════════════
//  HomeLayout — як розкласти апки на головному екрані «як люди звикли» (запит VELDAN 09.10.2026).
//
//  Розкладку системного лаунчера (MIUI тощо) скопіювати не можна: вона лежить у його приватній
//  базі, а READ_SETTINGS лаунчера — signature|privileged. Тому папки збираємо самі за тим, що
//  Android дає кожній апці: пакет, ApplicationInfo.category, системна чи ні. Виходить те, що
//  користувач бачив у MIUI за замовчуванням: Google, Інструменти, Ігри, Соцмережі, Медіа.
//
//  Порядок: папки (фіксований порядок FolderKind), далі решта апок за абеткою.
//  Папка з однієї апки не потрібна — така апка лягає окремо.
//  Чистий Kotlin: generic по T, тестується JVM-тестом.
// ═════════════════════════════════════════════════════════════════════════════
enum class FolderKind { GOOGLE, TOOLS, GAMES, SOCIAL, MEDIA }

sealed class HomeEntry<out T> {
    data class App<T>(val app: T) : HomeEntry<T>()
    data class Folder<T>(val kind: FolderKind, val apps: List<T>) : HomeEntry<T>()
}

object HomeLayout {

    // ApplicationInfo.CATEGORY_* (API 26+); -1 — не вказано
    const val CAT_GAME = 0
    const val CAT_AUDIO = 1
    const val CAT_VIDEO = 2
    const val CAT_IMAGE = 3
    const val CAT_SOCIAL = 4
    const val CAT_NEWS = 5

    /** Google-набір, як папка «Google» у MIUI / Pixel. */
    private val GOOGLE_EXTRA = setOf("com.android.chrome", "com.android.vending")

    /** Лишаються на виду, навіть якщо системні: ними користуються щодня. */
    private val KEEP_LOOSE = setOf(
        "com.android.settings", "com.miui.gallery", "com.android.gallery3d",
        "com.sec.android.gallery3d", "com.miui.securitycenter",
    )

    /**
     * Передвстановлені апки виробника. MIUI ставить Калькулятор, Нотатки, Погоду, Сканер як звичайні
     * (без FLAG_SYSTEM — їх можна видалити), тож «системна» не ловить їх — ловимо за пакетом (Redmi, 09.10.2026).
     */
    private val VENDOR_PREFIXES = listOf(
        "com.miui.", "com.xiaomi.", "com.mi.", "com.android.", "com.sec.android.", "com.samsung.",
        "com.huawei.", "com.hihonor.", "com.oppo.", "com.coloros.", "com.oplus.", "com.oneplus.", "com.vivo.",
    )

    fun folderOf(pkg: String, category: Int, system: Boolean): FolderKind? = when {
        pkg in KEEP_LOOSE                                        -> null
        pkg.startsWith("com.google.") || pkg in GOOGLE_EXTRA     -> FolderKind.GOOGLE
        category == CAT_GAME                                     -> FolderKind.GAMES
        system || VENDOR_PREFIXES.any { pkg.startsWith(it) }     -> FolderKind.TOOLS
        category == CAT_SOCIAL || category == CAT_NEWS           -> FolderKind.SOCIAL
        category == CAT_AUDIO || category == CAT_VIDEO || category == CAT_IMAGE -> FolderKind.MEDIA
        else                                                     -> null
    }

    fun <T> build(
        apps: List<T>,
        pkg: (T) -> String,
        label: (T) -> String,
        category: (T) -> Int,
        system: (T) -> Boolean,
        minFolder: Int = 2,
    ): List<HomeEntry<T>> {
        val byFolder = linkedMapOf<FolderKind, MutableList<T>>()
        val loose = mutableListOf<T>()
        for (a in apps) {
            val f = folderOf(pkg(a), category(a), system(a))
            if (f == null) loose += a else byFolder.getOrPut(f) { mutableListOf() } += a
        }
        val folders = mutableListOf<HomeEntry<T>>()
        for (k in FolderKind.entries) {
            val list = byFolder[k] ?: continue
            if (list.size >= minFolder) folders += HomeEntry.Folder(k, list.sortedBy { label(it).lowercase() })
            else loose += list
        }
        return folders + loose.sortedBy { label(it).lowercase() }.map { HomeEntry.App(it) }
    }

    /** Скільки рядів влазить: висота області / висота клітинки, щонайменше 1. */
    fun rowsFor(areaH: Float, cellH: Float): Int = if (cellH <= 0f) 1 else (areaH / cellH).toInt().coerceAtLeast(1)

    /** Поділ на сторінки по cols × rows. */
    fun <T> pages(items: List<T>, cols: Int, rows: Int): List<List<T>> =
        if (items.isEmpty()) listOf(emptyList()) else items.chunked((cols * rows).coerceAtLeast(1))
}
