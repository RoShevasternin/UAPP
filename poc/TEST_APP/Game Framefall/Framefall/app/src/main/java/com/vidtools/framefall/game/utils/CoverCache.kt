package com.vidtools.framefall.game.utils

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import com.vidtools.framefall.core.model.Video
import com.vidtools.framefall.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  CoverCache — текстури мініатюр на всю сесію (LRU на 64 штуки).
//  Без кешу кожен екран заново декодував би JPEG-и і мініатюри «блимали».
//  Текстури належать кешу: актори їх НЕ dispose-ять.
//  Ключі: v:<id>:<thumbPath> — відео з бібліотеки, a:<name> — вбудований кадр каталогу,
//  u:<url> — картинка з мережі, app:… — іконки лаунчера (окремо, без LRU).
// ═════════════════════════════════════════════════════════════════════════════
class CoverCache : Disposable {

    private val map = object : LinkedHashMap<String, Texture?>(64, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Texture?>): Boolean {
            if (size > MAX) { eldest.value?.dispose(); return true }
            return false
        }
    }
    /**
     * Іконки апок — ОКРЕМО і без LRU: їх на телефоні сотні, і LRU знищував текстури,
     * які ще малюються в сітці (чорні квадрати, заміряно 07.10.2026). Скидаємо цілком,
     * коли змінився список апок (invalidateApps).
     */
    private val apps = HashMap<String, Texture?>()
    private val waiting = HashMap<String, MutableList<(Texture?) -> Unit>>()

    fun forVideo(video: Video, onResult: (Texture?) -> Unit) =
        get("v:${video.id}:${video.thumbPath}", onResult) { done -> gdxGame.bridge.loadThumbBytes(video, done) }

    /** Вбудований кадр (assets/textures/thumbs/<name>.jpg) — синхронно, файли маленькі. */
    fun forAsset(name: String): Texture? {
        val key = "a:$name"
        if (map.containsKey(key)) return map[key]
        val tex = runCatching {
            Texture(com.badlogic.gdx.Gdx.files.internal("textures/thumbs/$name.jpg"), true).apply {
                setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear)
            }
        }.onFailure { log("thumb asset $name: ${it.message}") }.getOrNull()
        map[key] = tex
        return tex
    }

    fun forUrl(url: String, onResult: (Texture?) -> Unit) =
        get("u:$url", onResult) { done -> gdxGame.bridge.fetchBytes(url, done) }

    /** Іконка застосунку лаунчера (LauncherApps → PNG). Скидається, коли змінився список апок. */
    fun forApp(app: com.vidtools.framefall.game.platform.LauncherApp, sizePx: Int, onResult: (Texture?) -> Unit) {
        val key = "app:${app.packageName}/${app.activityName}/${app.userSerial}"
        if (apps.containsKey(key)) { onResult(apps[key]); return }
        waiting[key]?.let { it += onResult; return }
        waiting[key] = mutableListOf(onResult)
        gdxGame.bridge.appIconPng(app, sizePx) { bytes ->
            val tex = bytes?.let { decode(it) }
            apps[key] = tex
            waiting.remove(key)?.forEach { it(tex) }
        }
    }

    fun invalidateApps() {
        apps.values.forEach { it?.dispose() }
        apps.clear()
    }

    /** Ключ з'являється в кеші (навіть null — «арту немає») лише після першого завантаження. */
    private fun get(key: String, onResult: (Texture?) -> Unit, load: ((ByteArray?) -> Unit) -> Unit) {
        if (map.containsKey(key)) { onResult(map[key]); return }
        waiting[key]?.let { it += onResult; return }
        waiting[key] = mutableListOf(onResult)
        load { bytes ->
            val tex = bytes?.let { decode(it) }
            map[key] = tex
            waiting.remove(key)?.forEach { it(tex) }
        }
    }

    fun invalidateVideo(v: Video) { map.remove("v:${v.id}:${v.thumbPath}")?.dispose() }

    /** JPEG/PNG → текстура з мипмапами, не більше 512 px по стороні (пам'ять). */
    private fun decode(bytes: ByteArray): Texture? = runCatching {
        var pm = Pixmap(bytes, 0, bytes.size)
        val maxSide = maxOf(pm.width, pm.height)
        if (maxSide > 512) {
            val k = 512f / maxSide
            val small = Pixmap((pm.width * k).toInt(), (pm.height * k).toInt(), pm.format)
            small.filter = Pixmap.Filter.BiLinear
            small.drawPixmap(pm, 0, 0, pm.width, pm.height, 0, 0, small.width, small.height)
            pm.dispose(); pm = small
        }
        Texture(pm, true).apply {
            setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear)
            pm.dispose()
        }
    }.onFailure { log("cover decode: ${it.message}") }.getOrNull()

    override fun dispose() {
        map.values.forEach { it?.dispose() }
        map.clear()
        invalidateApps()
    }

    private companion object { const val MAX = 64 }
}
