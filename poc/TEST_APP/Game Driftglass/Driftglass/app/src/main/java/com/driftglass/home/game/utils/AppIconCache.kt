package com.driftglass.home.game.utils

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import com.driftglass.home.game.platform.LauncherApp
import com.driftglass.home.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  AppIconCache — іконки застосунків лаунчера на всю сесію (з Redwave CoverCache).
//  Без LRU: їх на телефоні сотні, і LRU знищував текстури, які ще малюються в сітці
//  (чорні квадрати, заміряно в Redwave 07.10.2026). Скидаємо цілком, коли змінився список апок.
//  Текстури належать кешу: актори їх НЕ dispose-ять.
// ═════════════════════════════════════════════════════════════════════════════
class AppIconCache : Disposable {

    private val apps = HashMap<String, Texture?>()
    private val waiting = HashMap<String, MutableList<(Texture?) -> Unit>>()

    fun forApp(app: LauncherApp, sizePx: Int, onResult: (Texture?) -> Unit) {
        val key = "${app.packageName}/${app.activityName}/${app.userSerial}"
        if (apps.containsKey(key)) { onResult(apps[key]); return }
        waiting[key]?.let { it += onResult; return }
        waiting[key] = mutableListOf(onResult)
        gdxGame.bridge.appIconPng(app, sizePx) { bytes ->
            val tex = bytes?.let { decode(it) }
            apps[key] = tex
            waiting.remove(key)?.forEach { it(tex) }
        }
    }

    fun invalidate() {
        apps.values.forEach { it?.dispose() }
        apps.clear()
    }

    private fun decode(bytes: ByteArray): Texture? = runCatching {
        val pm = Pixmap(bytes, 0, bytes.size)
        Texture(pm, true).apply {
            setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear)
            pm.dispose()
        }
    }.onFailure { log("icon decode: ${it.message}") }.getOrNull()

    override fun dispose() = invalidate()
}
