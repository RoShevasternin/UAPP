package com.redwave.downloader.core.model

import kotlinx.serialization.json.Json

/** Один налаштований Json на всю апку (як AppJson у T35). */
val RedwaveJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults    = true
    explicitNulls     = false
    coerceInputValues = true
}

object CatalogCodec {
    fun decode(text: String): Catalog = RedwaveJson.decodeFromString(Catalog.serializer(), text)
}

object AppStateCodec {
    fun encode(state: AppState): String = RedwaveJson.encodeToString(AppState.serializer(), state)
    /** Пошкоджений або старий JSON → чистий стан, а не краш на старті. */
    fun decodeOrDefault(text: String?): AppState =
        if (text.isNullOrBlank()) AppState()
        else runCatching { RedwaveJson.decodeFromString(AppState.serializer(), text) }.getOrElse { AppState() }
}
