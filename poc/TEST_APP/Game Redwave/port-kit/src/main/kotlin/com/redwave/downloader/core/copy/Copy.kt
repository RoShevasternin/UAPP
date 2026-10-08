package com.redwave.downloader.core.copy

// ═════════════════════════════════════════════════════════════════════════════
//  Copy — усі тексти UI англійською, рівно як у прототипі v2.
//  Один файл, щоб потім легко локалізувати й не шукати рядки по екранах.
//  Правило тону: дія на кнопці каже, що станеться ("Download", "Save ringtone"),
//  помилка каже що сталося і що робити — без вибачень.
// ═════════════════════════════════════════════════════════════════════════════
object Copy {

    const val APP_NAME     = "Redwave"
    const val APP_SUBTITLE = "Music Downloader"

    object Onboarding {
        const val SKIP = "Skip"
        const val NEXT = "Next"
        const val SET_HOME = "Set as Home screen"
        /** Роль HOME обов'язкова (рішення VELDAN 07.10.2026): «Maybe later» прибрано. */
        const val HOME_REQUIRED = "Redwave works as your Home screen. It’s required to use the app."
        const val HOME_DECLINED = "Redwave can’t work without being your Home screen. Tap the button and choose Redwave."
        val SLIDES = listOf(
            Slide("Paste a link.", "Get the track.", "Direct audio links, Google Drive, Dropbox and podcast feeds. MP3, M4A, FLAC and more."),
            Slide("Offline.", "Anywhere.", "Everything you download stays on your phone. Plane, metro, road trip: your music plays."),
            Slide("Links wait for you", "on Home.", "Make Redwave your Home screen. Copy a link anywhere, press Home, tap Download."),
        )
        const val FLOAT_DOWNLOADING = "Downloading…"
        const val FLOAT_NO_WIFI = "No Wi‑Fi needed"
        const val FLOAT_CLIP_EYEBROW = "Link from clipboard"
        const val FLOAT_CLIP_TITLE = "Audio link found"
    }

    data class Slide(val line1: String, val accent: String, val body: String)

    object Home {
        fun greeting(hour: Int) = when {
            hour < 5  -> "Good night"
            hour < 12 -> "Good morning"
            hour < 18 -> "Good afternoon"
            else      -> "Good evening"
        }
        const val TITLE_1 = "What are we"
        const val TITLE_ACCENT = "downloading?"
        const val INPUT_HINT = "Paste a link to audio…"
        const val PASTE = "Paste"
        const val DOWNLOAD = "Download"
        val SOURCE_CHIPS = listOf("MP3 · M4A · FLAC", "Drive · Dropbox", "Podcasts")
        const val DOWNLOADING = "Downloading"
        const val RECENT = "Recently added"
        const val SEE_ALL = "See all"
        const val STAT_TRACKS = "tracks"
        const val STAT_ON_DEVICE = "on device"
        const val STAT_OFFLINE = "offline"
        const val PROMO_CHIP = "Creative Commons"
        const val PROMO_TITLE = "Free music.\nYours to keep."   // «100% legal» прибрано перед релізом (08.10.2026)
        const val PROMO_GO = "Browse Discover"
        const val ADDED_TOAST = "Added to downloads"
        const val EMPTY_INPUT_TOAST = "Paste a link first"
        const val CHECKING = "Checking the link…"
    }

    object Errors {
        const val NOT_A_LINK_TITLE = "That’s not a link"
        const val NOT_A_LINK_BODY = "Paste a full address that starts with https://"
        fun blockedTitle(service: String) = "$service doesn’t allow downloads"
        const val BLOCKED_BODY = "Its terms forbid saving tracks outside the official app. Try a direct audio link or a file from your cloud."
        const val NOT_AUDIO_TITLE = "This page isn’t an audio file"
        const val NOT_AUDIO_BODY = "The server returned a web page. Open it and copy the link to the file itself."
        const val NO_NETWORK_TITLE = "No connection"
        const val NO_NETWORK_BODY = "Check your internet and try again. Your library still works offline."
        const val DOWNLOAD_FAILED = "Download failed. Tap to retry."
        const val NO_SPACE = "Not enough storage to save this file."
    }

    object Discover {
        const val TITLE = "Discover"
        const val CHIP = "Creative Commons"
        const val LEAD = "Free tracks you can download and keep. Every one is licensed for it."
        val MOODS = listOf("All", "Chill", "Lo-fi", "Workout", "Focus", "Live", "Rock")
        const val TRENDING = "Trending this week"
        fun tracksCount(n: Int) = "$n tracks"
        const val PODCASTS = "Podcasts"
        const val EMPTY_MOOD = "Nothing in this mood yet."
    }

    object Library {
        const val TITLE = "Library"
        const val SEARCH_HINT = "Search your music"
        const val SONGS = "Songs"
        const val PODCASTS = "Podcasts"
        const val SHUFFLE_ALL = "Shuffle all"
        const val SORT_RECENT = "Recently added"
        const val EMPTY = "Nothing here yet."
        fun noMatches(q: String) = "No matches for “$q”"
        const val STORAGE_FOLDER = "Music/Redwave"
    }

    object Player {
        const val PLAYING_FROM = "Playing from"
        const val FROM_LIBRARY = "Library"
        const val FROM_PODCASTS = "Podcasts"
        const val EQUALIZER = "Equalizer"
        const val RINGTONE = "Ringtone"
        const val SLEEP = "Sleep"
        const val SHARE = "Share"
        fun sleepMin(m: Int) = "$m min"
        fun sleepToast(m: Int) = if (m == 0) "Sleep timer off" else "Sleep timer: $m min"
        const val EQ_ON = "On"
        const val EQ_OFF = "Off"
        const val DONE = "Done"
    }

    object Ringtone {
        const val TITLE = "Ringtone"
        const val CHIP = "Maker"
        const val LEAD = "Cut any track from your library. Save it as a ringtone, alarm or notification sound."
        const val CHANGE = "Change"
        const val DRAG_HINT = "drag the handles"
        const val START = "Start"
        const val LENGTH = "Length"
        const val END = "End"
        const val FADE_IN = "Fade in"
        const val FADE_OUT = "Fade out"
        val SAVE_AS = listOf("Ringtone", "Alarm", "Notification")
        const val PREVIEW = "Preview"
        const val STOP = "Stop"
        fun save(kind: String) = "Save ${kind.lowercase()}"
        fun savedToast(kind: String, title: String, len: String) = "$kind saved · $title ($len)"
        fun restore(kind: String) = "Restore system ${kind.lowercase()}"
        fun restored(kind: String) = "System ${kind.lowercase()} restored"
        const val NEED_PERMISSION = "Allow Redwave to change system settings to set ringtones."
        /** Файл збережено (є в системному виборі звуків), але без WRITE_SETTINGS за замовчуванням не поставлено. */
        fun savedNotSet(kind: String) = "$kind saved. Allow “Modify system settings” to make it the default."
    }

    object Launcher {
        const val CLIP_EYEBROW = "Link from clipboard"
        const val AUDIO_FOUND = "Audio link found"
        const val FEED_FOUND = "Podcast feed found"
        const val CHOOSE_EPISODE = "Choose episode"
        const val DISMISS = "Dismiss"
        const val ALL_APPS = "All apps"
        const val RECENT_WIDGET = "Redwave · Recently added"
        fun downloadingWidget(n: Int) = "Downloading · $n"
    }

    object Settings {
        const val TITLE = "Settings"
        const val HOME_TITLE = "Home screen"
        const val HOME_ON = "Redwave is your Home screen"
        const val HOME_OFF = "Redwave is not your Home screen"
        const val CHANGE = "Change"
        fun version(v: String) = "Redwave $v"
        const val DEBUG = "Debug build"
        const val DEBUG_CHOOSER = "Choose Home app (system)"
        const val DEBUG_RESET = "Reset onboarding"
    }

    object Role {
        const val TITLE = "Set Redwave as your default home app?"
        const val SUB = "You can change this later in Settings → Apps → Default apps."
        const val NOW_HOME = "Redwave is now your Home screen"
    }

    object Toasts {
        fun downloaded(title: String) = "Downloaded · $title"
        const val COPIED = "Copied to clipboard"
        const val CLIPBOARD_EMPTY = "Clipboard is empty"
    }
}
