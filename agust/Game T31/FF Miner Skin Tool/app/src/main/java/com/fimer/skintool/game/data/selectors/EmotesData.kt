package com.fimer.skintool.game.data.selectors

import com.fimer.skintool.game.data.ItemData
import com.fimer.skintool.game.utils.gdxGame

object EmotesData {
    fun items(): List<ItemData> = listOf(

        ItemData(
            "DEVIL'S MOVE",
            gdxGame.assetsAll.listItemEmotes[0],
            "The Devil's Move is one of the most recognizable and stylish emotes ever created. " +
                    "Featuring smooth dance moves, confident body language, and a unique animation, it's the perfect way to celebrate your victories or show off after an intense battle. " +
                    "Whether you're waiting in the lobby or celebrating on the battlefield, this iconic emote helps you stand out and express your personality. " +
                    "Its legendary style and unforgettable performance have made it a favorite among players around the world."
        ),

        ItemData(
            "FLOWER OF LOVE",
            gdxGame.assetsAll.listItemEmotes[1],
            "The Flower of Love is one of the sweetest and most charming emotes in the game. " +
                    "Featuring a graceful kneeling pose, an outstretched hand, and a delicate flower offered to another player, it's the perfect way to greet a teammate or thank someone after a match. " +
                    "Whether you're meeting a new squad or celebrating a hard-won victory together, this gentle emote turns any moment into something memorable. " +
                    "Its romantic style and warm personality have made it a favorite among players around the world."
        ),

        ItemData(
            "SHUFFLING",
            gdxGame.assetsAll.listItemEmotes[2],
            "Shuffling is one of the most energetic and instantly recognizable emotes ever created. " +
                    "Featuring quick footwork, rhythmic sliding steps, and a relaxed upper body, it's the perfect way to fill the wait before a match or light up the lobby with pure energy. " +
                    "Whether you're warming up with your squad or dancing on the spot after a clutch play, this classic emote never goes out of style. " +
                    "Its catchy rhythm and effortless cool have made it a favorite among players around the world."
        ),

        ItemData(
            "FURIOUS SLAM",
            gdxGame.assetsAll.listItemEmotes[3],
            "The Furious Slam is one of the boldest and most powerful emotes in the game. " +
                    "Featuring a wide stance, raised arms, and an explosive burst of raw energy, it's the perfect way to claim a hard-fought win or send a message to your rivals. " +
                    "Whether you're standing over a defeated opponent or celebrating the final circle with your team, this emote makes sure everyone notices. " +
                    "Its aggressive style and unmistakable presence have made it a favorite among players around the world."
        ),

        ItemData(
            "HELLO",
            gdxGame.assetsAll.listItemEmotes[4],
            "Hello is one of the simplest and most universally loved emotes ever created. " +
                    "Featuring a friendly raised hand, an open posture, and a warm, welcoming wave, it's the perfect way to greet a stranger or let your squad know you're ready to go. " +
                    "Whether you're meeting new teammates in the lobby or saying goodbye after a long session, this timeless emote speaks a language everyone understands. " +
                    "Its friendly style and everyday usefulness have made it a favorite among players around the world."
        ),

        ItemData(
            "HIGH FIVE",
            gdxGame.assetsAll.listItemEmotes[5],
            "The High Five is one of the most satisfying team emotes in the game. " +
                    "Featuring a raised open hand, a playful stance, and an animation built to be shared with another player, it's the perfect way to celebrate teamwork after a clutch revive or a well-played round. " +
                    "Whether you're congratulating a friend or breaking the ice with a random squad, this emote turns a good moment into a great one. " +
                    "Its cheerful style and shared celebration have made it a favorite among players around the world."
        ),
    )
}