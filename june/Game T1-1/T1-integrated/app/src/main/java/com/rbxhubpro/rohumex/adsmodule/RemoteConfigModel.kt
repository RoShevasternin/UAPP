package com.rbxhubpro.rohumex.adsmodule

import com.google.gson.annotations.SerializedName

// ------------------------------------------------------------------------
// RemoteConfigModel
// ------------------------------------------------------------------------
data class RemoteConfigModel(
    val config: Config?,
    @SerializedName("ad_units") val adUnits: AdUnits?,
    @SerializedName("tiktok")   val tiktok : TikTokConfig? = null,
    // правки 5–6: экономика и локальные уведомления. Оба блока nullable —
    // конфиг без них (или старый кэш AdPref) парсится как раньше, поведение
    // приложения при отсутствии блока не меняется (дефолты зашиты в APK).
    @SerializedName("economy")       val economy      : Economy?          = null,
    @SerializedName("notifications") val notifications: NotificationsCfg? = null,
)

// ------------------------------------------------------------------------
// Economy (правка 5) — числа экономики из карточки приложения.
// rewards/prices/penalties — карты по block-именам (не фиксированные поля):
// имена экранов у всех приложений разные, карта не требует правки модели
// под каждое новое. Чтение с фолбэком на константы APK — game/Econ.kt.
// ------------------------------------------------------------------------
data class Economy(
    @SerializedName("start_balance")     val startBalance   : Int? = null,
    @SerializedName("push_optin_reward") val pushOptInReward: Int? = null,
    @SerializedName("cashout_threshold") val cashoutThreshold: Int? = null,
    val rewards  : Map<String, Int>? = null,
    val prices   : Map<String, Int>? = null,
    val penalties: Map<String, Int>? = null,
    val quests   : Map<String, Int>? = null,
    // ВОЗВРАЩЕНО (было потеряно при рефакторинге): rewards_list — наборы
    // однотипных значений одной механики (12 секторов колеса, 12 номиналов
    // скретч-карты). Скаляром по одному ключу это не выражается, а карточка
    // приложения на сервере уже отдаёт этот блок для spin_wheel и scratch —
    // без поля Gson молча его выбрасывает, и крутилка экономики не работает.
    // Читает Econ.rewardList (сверяет длину со списком по умолчанию из APK).
    @SerializedName("rewards_list") val rewardsList: Map<String, List<Int>>? = null,
)

// ------------------------------------------------------------------------
// Notifications (правка 6.2) — правила локальных уведомлений. Приложение их
// ИСПОЛНЯЕТ, а не решает: тексты (hook) — арм бандита, назначенный сервером
// этой установке; расписание/лимиты — политика из карточки. Исполнитель —
// push/LocalPush.kt.
// ------------------------------------------------------------------------
data class NotificationsCfg(
    // Момент показа опт-іна на пуші. Ось теста: меняется из карточки без релиза.
    //   on_return   — после возврата из стартового таба (деф.: диалог точно виден)
    //   first_launch— сразу на первом заходе; стартовый таб подождёт (окно подавления)
    //   after_reward— по первой выданной награде; зовётся из игрового момента
    @SerializedName("opt_in_trigger") val optInTrigger: String? = null,
    @SerializedName("max_per_day") val maxPerDay : Int? = null,
    @SerializedName("quiet_hours") val quietHours: List<Int>? = null, // [start, end] в локальном времени устройства
    val campaigns: List<NotifCampaign>? = null,
)

data class NotifCampaign(
    val id       : String? = null,
    val trigger  : String? = null,   // этап 1 поддерживает только "on_exit"
    val condition: String? = null,   // "true" | выражение по локальному состоянию (см. LocalPush)
    @SerializedName("delay_h")   val delayH  : Double? = null,
    val priority : Int? = null,      // меньше = важнее (при конфликте окна)
    @SerializedName("cancel_on") val cancelOn: String? = null, // "app_open" → отмена при возврате
    val hook     : NotifHook? = null,
)

data class NotifHook(
    val id   : String? = null,
    val title: String? = null,
    val body : String? = null,
    val route: String? = null,       // экран внутри приложения (вовлечение)
    @SerializedName("gate_pl") val gatePl: String? = null, // ИЛИ лендинг через гейтвей (деньги)
)

// ------------------------------------------------------------------------
// Config — який провайдер для кожного типу юзера
// ------------------------------------------------------------------------
data class Config(
    val organic: AdProviders?,
    val paid   : AdProviders?,
    val gclid  : AdProviders?,   // Google Ads юзери
    val ttclid : AdProviders?,   // TikTok Ads юзери
    val fbclid : AdProviders?,   // Facebook Ads юзери
)

data class AdProviders(
    val banner      : String = "na",
    val native      : String = "na",
    val interstitial: String = "na",
    @SerializedName("app_open") val appOpen: String = "na"
)

// ------------------------------------------------------------------------
// AdUnits — конкретні рекламні юніти
// ------------------------------------------------------------------------
data class AdUnits(
    val admob          : AdmobUnits?,
    val custom         : CustomUnits?,
    @SerializedName("custom_google")   val customGoogle  : CustomUnits?,
    @SerializedName("custom_tiktok")   val customTiktok  : CustomUnits?,
    @SerializedName("custom_facebook") val customFacebook: CustomUnits?,
)

data class AdmobUnits(
    val banner      : String = "",
    val native      : String = "",
    val interstitial: String = "",
    @SerializedName("app_open") val appOpen: String = ""
)

data class CustomUnits(
    val banner      : CustomBanner?,
    val native      : CustomNative?,
    val interstitial: CustomInterstitial?,
    @SerializedName("app_open") val appOpen: CustomAppOpen?
)

data class CustomBanner(
    val images: List<BannerImage> = emptyList()
)

data class BannerImage(
    val url: String,
    @SerializedName("target_url") val targetUrl: String
)

data class CustomNative(
    val assets: List<NativeAsset> = emptyList()
)

data class NativeAsset(
    val icon: String,
    val image: String,
    val headline: String,
    val description: String,
    val cta: String,
    @SerializedName("target_url") val targetUrl: String
)

data class CustomInterstitial(
    @SerializedName("target_url")       val targetUrl: String = "",
    @SerializedName("front_navigation") val frontNavigation: NavConfig = NavConfig(),
    @SerializedName("back_navigation")  val backNavigation: NavConfig = NavConfig()
)

data class NavConfig(
    val enabled: Boolean = false,
    val frequency: Int = 1,
    @SerializedName("reset_on_app_restart") val resetOnAppRestart: Boolean = false
)

data class CustomAppOpen(
    @SerializedName("target_url") val targetUrl: String = ""
)

// ------------------------------------------------------------------------
// TikTok
// ------------------------------------------------------------------------
data class TikTokConfig(
    @SerializedName("app_id") val appIdRaw: String? = null,
    @SerializedName("secret") val secret  : String? = null,
) {
    val appIds: List<String>
        get() = appIdRaw
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

    val isValid: Boolean
        get() = appIds.isNotEmpty() && !secret.isNullOrBlank()
}