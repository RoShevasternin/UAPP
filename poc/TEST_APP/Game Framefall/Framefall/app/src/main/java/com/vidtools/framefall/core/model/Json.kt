package com.vidtools.framefall.core.model

import kotlinx.serialization.json.Json

/** Один налаштований Json на всю апку (як AppJson у T35). */
val AppJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults    = true
    explicitNulls     = false
    coerceInputValues = true
}

object CatalogCodec {
    fun decode(text: String): Catalog = AppJson.decodeFromString(Catalog.serializer(), text)
}

object AppStateCodec {
    fun encode(state: AppState): String = AppJson.encodeToString(AppState.serializer(), state)
    /** Пошкоджений або старий JSON → чистий стан, а не краш на старті. */
    fun decodeOrDefault(text: String?): AppState =
        if (text.isNullOrBlank()) AppState()
        else runCatching { AppJson.decodeFromString(AppState.serializer(), text) }.getOrElse { AppState() }
}
