package com.driftglass.home.core.i18n

import com.driftglass.home.core.model.DaySlot
import com.driftglass.home.core.model.Shuffle
import com.driftglass.home.core.model.Style

// ═════════════════════════════════════════════════════════════════════════════
//  Тексти UI — ті самі, що в прототипі v2 (T у prototype/index.html).
//  Апка лише англійською (рішення VELDAN 09.10.2026: GDXGame.applyLanguage → EN);
//  uk / ru лишились тут на випадок, якщо мови повернуть.
//  Назви шпалер каталогу — бренд, не перекладаються (Catalog).
// ═════════════════════════════════════════════════════════════════════════════
enum class Lang(val code: String, val short: String, val native: String) {
    EN("en", "ENG", "English"),
    UK("uk", "УКР", "Українська"),
    RU("ru", "РУС", "Русский");

    companion object {
        fun of(code: String?): Lang = entries.firstOrNull { it.code == code } ?: EN
        /** Мова системи → наша: uk / ru, решта — англійська. */
        fun fromSystem(code: String?): Lang = when (code?.lowercase()) { "uk" -> UK; "ru" -> RU; else -> EN }
    }
}

object L {
    @Volatile var lang: Lang = Lang.EN

    /** Рядок поточною мовою: (en, uk, ru). */
    fun t(en: String, uk: String, ru: String): String = when (lang) { Lang.EN -> en; Lang.UK -> uk; Lang.RU -> ru }

    // ── Онбординг ────────────────────────────────────────────────────────────
    val next get() = t("Next", "Далі", "Дальше")
    val cont get() = t("Continue", "Продовжити", "Продолжить")
    val onb1t get() = t("Wallpapers that breathe", "Шпалери, що дихають", "Обои, которые дышат")
    val onb1d get() = t("Every Driftglass wallpaper is drawn live on your screen, so it never looks the same twice.",
        "Кожні шпалери Driftglass малюються наживо, тож ніколи не повторюються.",
        "Каждые обои Driftglass рисуются вживую, поэтому никогда не повторяются.")
    val onb2t get() = t("Make one that's yours", "Створи власні", "Создай свои")
    val onb2d get() = t("Pick a style, a palette and a mood. Studio builds a new wallpaper in seconds.",
        "Обери стиль, палітру й настрій — Studio зробить нові шпалери за секунди.",
        "Выбери стиль, палитру и настроение — Studio сделает новые обои за секунды.")
    val onb3t get() = t("Shifts with your day", "Змінюються з твоїм днем", "Меняются вместе с днём")
    val onb3d get() = t("Dawn, day, dusk and night palettes that change on their own.",
        "Палітри світанку, дня, заходу й ночі змінюються самі.",
        "Палитры рассвета, дня, заката и ночи меняются сами.")

    // ── Пояснення ролі ───────────────────────────────────────────────────────
    val optional get() = t("OPTIONAL", "ЗА БАЖАННЯМ", "ПО ЖЕЛАНИЮ")
    val required get() = t("REQUIRED", "ОБОВ'ЯЗКОВО", "ОБЯЗАТЕЛЬНО")
    val needHome get() = t("Driftglass needs to be your home screen to continue.",
        "Щоб продовжити, Driftglass має бути головним екраном.",
        "Чтобы продолжить, Driftglass должен быть главным экраном.")
    val exTitle get() = t("Let Driftglass light up your home screen", "Нехай Driftglass оживить головний екран", "Пусть Driftglass оживит главный экран")
    val ex1 get() = t("Living wallpapers move only on Driftglass Home. Elsewhere you get a still frame.",
        "Живі шпалери рухаються лише на Driftglass Home. Деінде — нерухомий кадр.",
        "Живые обои двигаются только на Driftglass Home. В остальных местах — неподвижный кадр.")
    val ex2 get() = t("Your everyday apps go to the dock, and every app is one swipe up.",
        "Щоденні апки — у доку, усі інші — одним свайпом угору.",
        "Ежедневные приложения — в доке, все остальные — одним свайпом вверх.")
    val ex3 get() = t("Switch back any time: Settings → Use as Home screen.",
        "Повернутися можна будь-коли: Налаштування → Головний екран.",
        "Вернуться можно в любой момент: Настройки → Главный экран.")
    val setHome get() = t("Set as home screen", "Зробити головним екраном", "Сделать главным экраном")
    val later get() = t("Maybe later", "Можливо, пізніше", "Может, позже")

    // ── Home ─────────────────────────────────────────────────────────────────
    val dayCycle get() = t("Day cycle", "Цикл дня", "Цикл дня")
    val autoShuffle get() = t("Auto-shuffle", "Автозміна", "Автосмена")
    val allApps get() = t("All apps", "Усі застосунки", "Все приложения")
    val searchApps get() = t("Search apps", "Пошук застосунків", "Поиск приложений")
    val noMatch get() = t("No apps match", "Нічого не знайдено", "Ничего не найдено")
    fun shuffleOn(m: Shuffle) = when (m) {
        Shuffle.UNLOCK -> t("every unlock", "кожне розблокування", "каждая разблокировка")
        Shuffle.HOURLY -> t("hourly", "щогодини", "каждый час")
        Shuffle.DAILY  -> t("daily", "щодня", "каждый день")
        Shuffle.OFF    -> ""
    }

    // ── Discover / Preview ───────────────────────────────────────────────────
    val discover get() = t("Discover", "Огляд", "Обзор")
    val banner get() = t("Wallpapers come alive on Driftglass Home.", "Шпалери оживають на Driftglass Home.", "Обои оживают на Driftglass Home.")
    val turnOn get() = t("Turn on", "Увімкнути", "Включить")
    val today get() = t("TODAY'S GLASS", "СКЛО ДНЯ", "СТЕКЛО ДНЯ")
    val view get() = t("View", "Дивитись", "Смотреть")
    val all get() = t("All", "Усі", "Все")
    val live get() = t("live", "живі", "живые")
    val alsoLock get() = t("Also set a still frame on the lock screen", "Поставити кадр і на екран блокування", "Поставить кадр и на экран блокировки")
    val customize get() = t("Customize", "Налаштувати", "Настроить")
    val apply get() = t("Apply", "Застосувати", "Применить")
    val needTitle get() = t("Living wallpapers need Driftglass Home", "Живим шпалерам потрібен Driftglass Home", "Живым обоям нужен Driftglass Home")
    val needBody get() = t("Without it, Driftglass sets a still frame as your system wallpaper. You can turn on Home any time.",
        "Без нього Driftglass поставить нерухомий кадр як системні шпалери. Home можна увімкнути будь-коли.",
        "Без него Driftglass поставит неподвижный кадр как системные обои. Home можно включить в любой момент.")
    val stillOnly get() = t("Still only", "Лише кадр", "Только кадр")
    val turnOnHome get() = t("Turn on Home", "Увімкнути Home", "Включить Home")

    // ── Studio ───────────────────────────────────────────────────────────────
    val studio = "Studio"
    val motion get() = t("Motion", "Рух", "Движение")
    val scale get() = t("Scale", "Масштаб", "Масштаб")
    val grain get() = t("Grain", "Зерно", "Зерно")
    val save get() = t("Save", "Зберегти", "Сохранить")

    // ── My Glass ─────────────────────────────────────────────────────────────
    val myGlass get() = t("My Glass", "Моє скло", "Моё стекло")
    val created get() = t("Created", "Створені", "Созданные")
    val favorites get() = t("Favorites", "Обране", "Избранное")
    val emptyCreated get() = t("Wallpapers you make in Studio land here.", "Тут з'являться шпалери зі Studio.", "Здесь появятся обои из Studio.")
    val emptyFav get() = t("Tap the heart on any wallpaper to keep it here.", "Натисни ♡ на шпалерах, щоб зберегти їх тут.", "Нажми ♡ на обоях, чтобы сохранить их здесь.")
    val openStudio get() = t("Open Studio", "Відкрити Studio", "Открыть Studio")
    val browse get() = t("Browse", "Переглянути", "Смотреть")
    fun shuffleSeg(m: Shuffle) = when (m) {
        Shuffle.OFF    -> t("Off", "Вимк.", "Выкл.")
        Shuffle.UNLOCK -> t("Unlock", "Розблок.", "Разблок.")
        Shuffle.HOURLY -> t("Hourly", "Щогодини", "Ежечасно")
        Shuffle.DAILY  -> t("Daily", "Щодня", "Ежедневно")
    }
    val picks get() = t("Picks from your favorites", "Бере з обраного", "Берёт из избранного")
    val add2 get() = t(" (add 2+ first)", " (додай 2+)", " (добавь 2+)")
    val cycleSub get() = t("A different wallpaper for each part of the day", "Окремі шпалери для кожної частини дня", "Отдельные обои для каждой части дня")
    val slotHint get() = t("Tap a slot to pick the next wallpaper for it.", "Торкнись слота, щоб змінити шпалери.", "Коснись слота, чтобы сменить обои.")
    fun slot(s: DaySlot) = when (s) {
        DaySlot.DAWN  -> t("Dawn", "Світанок", "Рассвет")
        DaySlot.DAY   -> t("Day", "День", "День")
        DaySlot.DUSK  -> t("Dusk", "Захід", "Закат")
        DaySlot.NIGHT -> t("Night", "Ніч", "Ночь")
    }
    fun glassName(n: Int) = t("Glass #$n", "Скло #$n", "Стекло #$n")

    // ── Settings ─────────────────────────────────────────────────────────────
    val settings get() = t("Settings", "Налаштування", "Настройки")
    val secHome get() = t("HOME SCREEN", "ГОЛОВНИЙ ЕКРАН", "ГЛАВНЫЙ ЭКРАН")
    val useHome get() = t("Use as Home screen", "Використовувати як головний", "Использовать как главный")
    val roleOn get() = t("Driftglass is your home app", "Driftglass — ваш головний екран", "Driftglass — ваш главный экран")
    val roleOff get() = t("Your phone uses another launcher", "Зараз інший лаунчер", "Сейчас другой лаунчер")
    val doubleTap get() = t("Double-tap to shuffle", "Подвійний дотик — інші шпалери", "Двойное касание — другие обои")
    val parallax get() = t("Motion parallax", "Паралакс", "Параллакс")
    val parallaxSub get() = t("Wallpaper follows the tilt of your phone", "Шпалери реагують на нахил телефона", "Обои реагируют на наклон телефона")
    val labels get() = t("Icon labels", "Підписи іконок", "Подписи иконок")
    val grid get() = t("Home grid", "Сітка", "Сетка")
    fun cols(n: Int) = t("$n columns", "$n стовпців", "$n столбцов")
    val language get() = t("Language", "Мова", "Язык")
    val secPower get() = t("BATTERY", "БАТАРЕЯ", "БАТАРЕЯ")
    val saver get() = t("Battery saver", "Економія заряду", "Экономия заряда")
    val saverAuto get() = t("Automatic below 20%", "Автоматично нижче 20%", "Автоматически ниже 20%")
    val saverOn get() = t("Always on: 30 fps, slower motion", "Завжди: 30 fps, повільніший рух", "Всегда: 30 fps, медленнее движение")
    val vAuto get() = t("Auto", "Авто", "Авто")
    val vOn get() = t("On", "Увімк.", "Вкл.")
    val vOff get() = t("Off", "Вимк.", "Выкл.")
    val secLock get() = t("LOCK SCREEN", "ЕКРАН БЛОКУВАННЯ", "ЭКРАН БЛОКИРОВКИ")
    val lockStill get() = t("Still frame on lock screen", "Кадр на екрані блокування", "Кадр на экране блокировки")
    val lockSub get() = t("Updates when your wallpaper changes", "Оновлюється разом зі шпалерами", "Обновляется вместе с обоями")
    val secAbout get() = t("ABOUT", "ПРО АПКУ", "О ПРИЛОЖЕНИИ")
    val privacy get() = t("Privacy policy", "Політика конфіденційності", "Политика конфиденциальности")
    val version get() = t("Version", "Версія", "Версия")

    // ── Тости й меню ─────────────────────────────────────────────────────────
    val tApplied get() = t("Applied to Home", "Застосовано на головному", "Применено на главном")
    val tFavAdd get() = t("Added to favorites", "Додано в обране", "Добавлено в избранное")
    val tFavRem get() = t("Removed from favorites", "Прибрано з обраного", "Убрано из избранного")
    val tSaved get() = t("Saved to My Glass", "Збережено в «Моє скло»", "Сохранено в «Моё стекло»")
    val tStill get() = t("Still wallpaper set", "Нерухомі шпалери встановлено", "Неподвижные обои установлены")
    val tRole get() = t("Driftglass is your home screen", "Driftglass тепер головний екран", "Driftglass теперь главный экран")
    val tStillFail get() = t("Couldn't set the wallpaper", "Не вдалося поставити шпалери", "Не удалось поставить обои")
    val mWallpapers get() = t("Wallpapers", "Шпалери", "Обои")
    val mHomeSettings get() = t("Home settings", "Налаштування екрана", "Настройки экрана")
    val appInfo get() = t("App info", "Про застосунок", "О приложении")
    fun folder(k: com.driftglass.home.core.logic.FolderKind) = when (k) {
        com.driftglass.home.core.logic.FolderKind.GOOGLE -> "Google"
        com.driftglass.home.core.logic.FolderKind.TOOLS  -> t("Tools", "Інструменти", "Инструменты")
        com.driftglass.home.core.logic.FolderKind.GAMES  -> t("Games", "Ігри", "Игры")
        com.driftglass.home.core.logic.FolderKind.SOCIAL -> t("Social", "Спілкування", "Общение")
        com.driftglass.home.core.logic.FolderKind.MEDIA  -> t("Media", "Медіа", "Медиа")
    }
    val uninstall get() = t("Uninstall", "Видалити", "Удалить")

    fun style(s: Style) = when (s) {
        Style.AURORA -> t("Aurora", "Аврора", "Аврора")
        Style.LIQUID -> t("Liquid", "Рідина", "Жидкость")
        Style.WAVES  -> t("Waves", "Хвилі", "Волны")
        Style.GLASS  -> t("Glass", "Скло", "Стекло")
        Style.MESH   -> t("Gradient", "Градієнт", "Градиент")
    }

    fun palette(id: String) = when (id) {
        "sea"    -> t("Sea Glass", "Морське скло", "Морское стекло")
        "ember"  -> t("Ember", "Жар", "Жар")
        "orchid" -> t("Orchid", "Орхідея", "Орхидея")
        "night"  -> t("Night Drive", "Нічна траса", "Ночная трасса")
        "pearl"  -> t("Pearl", "Перлина", "Жемчуг")
        "citrus" -> t("Citrus", "Цитрус", "Цитрус")
        "sahara" -> t("Sahara", "Сахара", "Сахара")
        "lagoon" -> t("Lagoon", "Лагуна", "Лагуна")
        else     -> id
    }
}
