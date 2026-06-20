package com.treprosure.starbxup.adsmodule

// Тип юзера — визначається один раз при першому запуску через Install Referrer
//
// ORGANIC          — зайшов сам, без рекламної мітки
// PAID             — платний без конкретної мережі (fallback)
// PAID_GOOGLE      — прийшов через Google Ads   (gclid)
// PAID_TIKTOK      — прийшов через TikTok Ads    (ttclid)
// PAID_FACEBOOK    — прийшов через Facebook Ads  (fbclid)

enum class UserType {
    ORGANIC,
    PAID,
    PAID_GOOGLE,
    PAID_TIKTOK,
    PAID_FACEBOOK,
}