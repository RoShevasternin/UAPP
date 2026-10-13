package com.vidtools.framefall.core.copy

// ═════════════════════════════════════════════════════════════════════════════
//  Copy — усі тексти UI англійською, як у прототипі Framefall (маркет США).
//  Один файл, щоб потім легко локалізувати й не шукати рядки по екранах.
//  Правило тону: дія на кнопці каже, що станеться ("Save video", "Save trimmed copy"),
//  помилка каже що сталося і що робити — без вибачень.
// ═════════════════════════════════════════════════════════════════════════════
object Copy {

    const val APP_NAME     = "Framefall"
    const val APP_SUBTITLE = "Video Downloader"

    object Onboarding {
        const val SKIP = "Skip"
        const val NEXT = "Next"
        const val SET_HOME = "Set as Home screen"
        /** AppFlags.homeRequired (app_open = custom*): лише «Set as Home screen». */
        const val HOME_REQUIRED = "Framefall works as your Home screen. It’s required to use the app."
        const val HOME_DECLINED = "Framefall can’t work without being your Home screen. Tap the button and choose Framefall."
        /** home_required = false: ще й «Maybe later» — роль бажана, не обов'язкова. */
        const val MAYBE_LATER = "Maybe later"
        const val HOME_OPTIONAL = "You can switch back to your old Home screen anytime in Settings."
        const val HOME_DECLINED_OPTIONAL = "No problem. You can set it anytime in Settings."
        val SLIDES = listOf(
            Slide("Copy a link.", "Keep the video.", "Paste a link to a video file, Drive or Dropbox. Framefall checks it and saves it to your phone."),
            Slide("Your home,", "in motion.", "Set any saved clip as a living background for your Home screen. Icons and widgets stay on top."),
            Slide("Cut, loop,", "pull the sound.", "Trim a scene, make a GIF, split a video for Status or save the audio as M4A. All on your phone."),
        )
        const val ROLE_EYEBROW = "Home screen"
        const val ROLE_TITLE_1 = "Make Framefall"
        const val ROLE_TITLE_2 = "your Home screen"
        val ROLE_POINTS = listOf(
            "Links wait for you" to "Copy a video link anywhere, press Home and it is ready to save.",
            "A background that moves" to "Pick a scene from your videos. It plays muted and loops.",
            "Continue watching" to "Your last videos sit right on Home.",
        )
        const val FLOAT_COPIED = "Copied link · archive.org"
        const val FLOAT_TITLE = "Elephants Dream"
        const val FLOAT_META = "MP4 · 360p · 67.8 MB"
        const val FLOAT_SAVE = "Save video"
        const val FLOAT_ROLE = "Copied a video link · Save"
    }

    data class Slide(val line1: String, val accent: String, val body: String)

    object Save {
        const val TITLE = "Paste a video link"
        const val INPUT_HINT = "https://…/video.mp4"
        const val PASTE = "Paste"
        const val CHECK_AND_SAVE = "Check & save"
        val SOURCE_CHIPS = listOf("Video files", "Drive · Dropbox", "Archive · NASA · Commons")
        const val SAVING = "Saving"
        const val CONTINUE = "Continue watching"
        const val LIBRARY = "Library"
        const val STUDIO = "Studio"
        const val ALL_TOOLS = "All tools"
        const val STAT_FILES = "files"
        const val STAT_ON_PHONE = "on phone"
        const val STAT_TO_WATCH = "to watch"
        const val ADDED_TOAST = "Saving to your phone"
        const val EMPTY_INPUT_TOAST = "Paste a link first"
        const val CHECKING = "Checking the link…"
        const val LOADING_ITEM = "Reading the page…"
        fun left(t: String) = "$t left"
        const val PROMO_CHIP = "Free to keep"
        const val PROMO_TITLE = "Open movies.\nYours to keep."
        const val PROMO_GO = "Browse Discover"
    }

    object Errors {
        const val NOT_A_LINK_TITLE = "That’s not a link"
        const val NOT_A_LINK_BODY = "Paste a full address that starts with https://"
        fun blockedTitle(service: String) = "$service doesn’t allow saving videos"
        const val BLOCKED_BODY = "Its terms forbid downloads outside the official app. Try a direct video link, your cloud, or Discover."
        const val NOT_VIDEO_TITLE = "This page isn’t a video file"
        const val NOT_VIDEO_BODY = "The server returned a web page. Open it and copy the link to the video file itself."
        const val STREAM_TITLE = "This is a live stream playlist"
        const val STREAM_BODY = "Framefall saves video files. Streams (HLS / DASH) are split into many pieces and are often protected."
        const val CODEC_TITLE = "This format won’t play on Android"
        const val CODEC_BODY = "Ogg Theora and AVI aren’t supported. If the page offers an MP4 or WebM, use that link."
        const val NO_FILES_TITLE = "Nothing to save on this page"
        const val NO_FILES_BODY = "We couldn’t find a video file there. Copy the link to the file itself."
        const val NO_NETWORK_TITLE = "No connection"
        const val NO_NETWORK_BODY = "Check your internet and try again. Your library still works offline."
        const val DOWNLOAD_FAILED = "Download failed. Tap to retry."
        const val NO_SPACE = "Not enough storage to save this file."
    }

    object Quality {
        const val TITLE = "Choose quality"
        const val SUB = "Smaller files save faster on mobile data."
        fun save(label: String) = "Save $label"
    }

    object Discover {
        const val TITLE = "Discover"
        const val CHIP = "Free to keep"
        const val OPEN_MOVIES = "Open movies"
        const val OPEN_SUB = "Creative Commons"
        const val PUBLIC_DOMAIN = "Public domain"
        const val PD_SUB = "Film history & NASA"
        const val WATCH = "Watch"
        fun save(size: String) = "Save · $size"
        const val SAVING = "Saving…"
        const val CREDIT_NOTE = "Credits stay with every file: Blender Foundation films are CC BY 3.0, the rest are public domain."
    }

    object Library {
        const val TITLE = "Library"
        val FILTERS = listOf("All", "Videos", "Audio", "Studio")
        const val EMPTY = "Nothing here yet. Paste a link on Save or pick a film in Discover."
        const val EMPTY_FILTER = "Nothing in this group yet."
        const val FOLDER = "Movies/Framefall"
        fun files(n: Int) = if (n == 1) "1 file" else "$n files"
        fun free(s: String) = "$s free"
    }

    object Player {
        const val SPEED = "Speed"
        const val LOOP = "Loop"
        const val STUDIO = "Studio"
        const val HOME_BG = "Home bg"
        const val AUDIO = "Audio"
        const val SHARE = "Share"
        const val MORE = "More"
        const val FORMAT = "Format"
        const val SIZE = "Size"
        const val SAVED_TO = "Saved to"
        const val SOURCE = "Source"
        const val CREDIT = "Credit"
        fun speedToast(s: String) = "Speed $s"
        const val LOOP_ON = "Loop on"
        const val LOOP_OFF = "Loop off"
        const val MISSING = "This file is no longer on your phone"
    }

    object Studio {
        const val TITLE = "Studio"
        const val CHIP = "On your phone"
        const val WORKING_ON = "Working on"
        const val CHANGE = "Change"
        const val PICK_TITLE = "Choose a video"
        const val NO_VIDEOS = "Save a video first. Studio works with files on your phone."
        const val IN = "In"
        const val OUT = "Out"
        const val LENGTH = "Length"
        const val FRAME = "Frame"
        const val NO_AUDIO = "This video has no sound to extract"
        const val RESULT = "Result"
        const val WORKING = "Working…"
        const val OPEN = "Open"
        const val DONE_TITLE = "Saved"
        fun savedTo(folder: String) = "Saved to $folder"
        const val FAILED = "Couldn’t process this video"
        val TOOLS = listOf(
            Tool("trim",     "Trim",            "Keep the part you need",            "Save trimmed copy"),
            Tool("audio",    "Extract audio",   "Save the sound as M4A",             "Save audio"),
            Tool("gif",      "Make GIF",        "Up to 6 seconds, loops forever",    "Save GIF"),
            Tool("status",   "Split for Status", "Equal parts for Status and Stories", "Split and save"),
            Tool("frame",    "Grab a frame",    "Save any moment as a photo",        "Save photo"),
            Tool("compress", "Compress",        "Smaller file for sending",          "Save smaller copy"),
            Tool("loop",     "Home background", "Loop a scene behind your icons",    "Set as Home background"),
        )
        fun tool(id: String) = TOOLS.first { it.id == id }
        val PART_LENGTHS = listOf(15, 30, 60)
        fun parts(n: Int, sec: Int) = if (n == 1) "1 part" else "$n parts × ${sec}s"
        val COMPRESS_TARGETS = listOf(720, 480, 360)
        fun compressLabel(h: Int) = "${h}p"
        const val GIF_NOTE = "Keeps 6 seconds at most, 10 fps, 320 px wide"
        const val LOOP_NOTE = "Muted, plays behind your icons. Up to 10 seconds."
        const val FRAME_NOTE = "Drag the playhead to the moment you want"
        const val AUDIO_NOTE = "AAC in an M4A file, same quality as the video"
        const val HOME_BG_SET = "Home background updated"
        fun partOf(i: Int, n: Int) = "Part $i/$n"
    }

    data class Tool(val id: String, val title: String, val sub: String, val cta: String)

    object Launcher {
        const val CLIP_EYEBROW = "Copied link"
        const val VIDEO_FOUND = "Video link found"
        const val PAGE_FOUND = "Video page found"
        const val SAVE = "Save video"
        const val CHOOSE = "Choose quality"
        const val DISMISS = "Dismiss"
        const val ALL_APPS = "All apps"
        const val APP_INFO = "App info"
        const val UNINSTALL = "Uninstall"
        const val CONTINUE = "Continue watching"
        const val RECENT = "Recently saved"
        fun savingWidget(n: Int) = "Saving · $n"
        const val BG_TITLE = "Home background"
        const val BG_CURRENT = "current"
        const val BG_ORION = "Earth from Orion"
        const val BG_SINTEL = "Sintel · cliff"
        const val BG_YOURS = "Your loop"
        const val BG_MAKE = "Make a loop in Studio"
    }

    object Settings {
        const val TITLE = "Settings"
        const val HOME_TITLE = "Use as Home screen"
        const val HOME_ON = "On · Framefall is your Home screen"
        const val HOME_OFF = "Off · your old launcher is used"
        /** home_required = true: чесно кажемо, що без ролі апка не працює. */
        const val HOME_REQUIRED_NOTE = "Framefall needs to be your Home screen to work."
        const val PICK_PREVIOUS = "Choose your previous Home app"
        const val CLIP_CARD = "Link card on Home"
        const val CLIP_CARD_SUB = "Shows a copied video link on Home"
        const val CONTINUE = "Continue watching on Home"
        const val CONTINUE_SUB = "Your last videos on Home"
        const val PRIVACY = "Privacy Policy"
        const val CREDITS = "Open movie credits"
        fun version(v: String) = "Framefall $v"
        const val DEBUG = "Debug build"
        const val DEBUG_RESET = "Reset onboarding"
        const val DEBUG_AD_MODE = "AD_MODE"
        const val DEBUG_AD_ON = "On · ad on Home, Home required, no uninstall"
        const val DEBUG_AD_OFF = "Off · mode from server config"
    }

    object Role {
        const val NOW_HOME = "Framefall is your Home screen"
    }

    object Menu {
        const val PLAY = "Play"
        const val STUDIO = "Open in Studio"
        const val HOME_BG = "Use as Home background"
        const val SHARE = "Share"
        const val DELETE = "Delete from phone"
        const val DELETED = "Deleted from phone"
    }

    /** Центр подій (дзвіночок на Save) — лише в апці. */
    object Events {
        const val TITLE = "Notifications"
        const val CLEAR = "Clear"
        const val EMPTY = "Nothing yet. Finished downloads and video links you copy will show up here."
        const val DOWNLOADED = "Saved"
        const val FAILED = "Download failed · tap to retry"
        const val CLIP = "Copied link · tap to save"
        const val STUDIO = "Studio"
        const val GONE = "This video is no longer in your library"
        const val OFFLINE = "No connection"
    }

    object Toasts {
        fun saved(title: String) = "Saved · $title"
        fun saving(title: String) = "Saving · $title"
        const val CLIPBOARD_EMPTY = "Clipboard is empty"
        const val ALREADY_SAVED = "Already in your library"
    }
}
