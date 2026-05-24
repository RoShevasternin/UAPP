package com.rsbuxs.rcounbux.adsmodule

// Тип юзера — визначається один раз при першому запуску
// через Install Referrer або Deep Link
enum class UserType {
    ORGANIC, // звичайний юзер — зайшов сам
    PAID     // прийшов через рекламну кампанію (gclid / fbclid)
}