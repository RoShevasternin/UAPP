package com.fimer.skintool.game.data

data class DTipsData(
    val name : String,
    val text : String,
)

object TipsData {
    fun items(): List<DTipsData> = listOf(

        // ── 1 ─────────────────────────────────────────────────────────────────
        DTipsData(
            "CHOOSE THE CORRECT LANDING POINT",
            "Landing well is half the battle — long before you even hit the ground, your choice of drop zone already decides how the round will go. " +
                    "It's not a random first step; it's the foundation your whole run is built on — it shapes what loot you'll find, how safe you'll be early on, and how much of an edge you'll carry into later fights.\n\n" +

                    "Picking a spot isn't just about where you land — it's a statement of strategy. " +
                    "Head straight into the busiest, loot-rich zones and take your chances against heavy early competition, or drop somewhere quieter, gear up at your own pace, and stay out of the chaos until you're ready. " +
                    "Every choice signals something — to yourself, and to everyone else on the map.\n\n" +

                    "There's no single \"right\" answer — the best players read the map, read their own playstyle, and choose accordingly. " +
                    "Sometimes the boldest move is landing hot; sometimes the smartest move is patience."
        ),

        // ── 2 ─────────────────────────────────────────────────────────────────
        DTipsData(
            "COMPLETE IN-GAME ACHIEVEMENTS",
            "Welcome to a whole new level of gameplay — this app is built to be your go-to companion, whatever kind of player you are. " +
                    "Casual or competitive, you'll find a full toolkit of skins, gear, and utilities designed to help you stand out in every match.\n\n" +

                    "Make your style unmistakable\n" +
                    "Browse a wide collection of gear and cosmetics to shape a look that's entirely yours — from striking parachutes to glowing outfits. " +
                    "Every piece is designed to leave an impression the second you touch down, and to make your weapons and vehicles feel like an extension of your own strategy.\n\n" +

                    "Land with a plan, not by chance\n" +
                    "The real advantage starts before the fight even begins. " +
                    "Use the app to scout and plan your drop zones with precision, gliding in ready to take control — whether you're chasing high-value loot or playing it safe for a longer game."
        ),

        // ── 3 ─────────────────────────────────────────────────────────────────
        DTipsData(
            "EXPLORE MILITARY SITE",
            "Log in every day and get rewards. Our daily check-in rewards you just for staying active — the longer your streak, the better what's waiting for you. " +
                    "Early days bring small rewards, and they only get better the further your streak goes.\n\n" +

                    "Miss a day, and the streak resets — so checking in daily is genuinely the fastest way to grow your collection and unlock things you won't find anywhere else. " +
                    "Everything refreshes on a daily basis, and bigger milestones kick in after a full week, a month, and beyond."
        ),

        // ── 4 ─────────────────────────────────────────────────────────────────
        DTipsData(
            "DAILY LOGIN REWARDS",
            "Daily login rewards are a simple idea: log in, and you're rewarded just for showing up. " +
                    "It's a common feature in mobile apps, designed to make regular visits worth your while — the more consistent you are, the more it pays off.\n\n" +

                    "Rewards start small and grow the longer your streak runs. " +
                    "Log in a single day, and you'll get something modest. " +
                    "Keep it going for a full week without missing a day, and what's waiting for you gets noticeably better.\n\n" +

                    "The logic is simple: the more consistent you are, the more it's worth coming back."
        ),

        // ── 5 ─────────────────────────────────────────────────────────────────
        DTipsData(
            "PARTICIPATE IN GAME EVENTS",
            "Participating in events is what keeps things fresh — mobile games often run short, time-limited challenges that break up the usual routine and give you something new to chase. " +
                    "They come in all shapes, but the goal is always the same: give players a reason to jump back in.\n\n" +

                    "These events don't stick around forever — most run for just a few days, a weekend, or a week at most. " +
                    "Once they're over, whatever came with them is gone too, which is exactly what makes joining in early worth it.\n\n" +

                    "What makes them worth showing up for is what you can't get any other way — content and rewards made specifically for that event, and gone the moment it wraps up."
        ),
    )

}