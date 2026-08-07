package com.fimer.skintool.game.data.selectors

import com.fimer.skintool.game.data.ItemData
import com.fimer.skintool.game.utils.gdxGame

object PetsData {
    fun items(): List<ItemData> = listOf(

        ItemData(
            "KAKTUS",
            gdxGame.assetsAll.listItemPets[0],
            "Kaktus is a charming companion with a playful personality and a truly unforgettable look. " +
                    "Featuring a cute cactus-inspired design, oversized hoodie, and expressive animations, this pet brings both style and character to every adventure. " +
                    "Its unique appearance makes it instantly recognizable, while its cheerful attitude adds a fun touch to every match. " +
                    "Whether following you into battle or simply showing off in the lobby, Kaktus is a lovable companion that stands out with its distinctive design and memorable charm."
        ),

        ItemData(
            "FANG",
            gdxGame.assetsAll.listItemPets[1],
            "Fang is a fierce companion wrapped in living flame and restless energy. " +
                    "Featuring a wolf-inspired design, burning mane, and sharp predatory movements, this pet brings raw intensity to every adventure. " +
                    "Its blazing appearance makes it impossible to ignore, while its untamed presence adds a sense of danger to every match. " +
                    "Whether prowling beside you into battle or lighting up the lobby with its glow, Fang is a striking companion for players who like their style bold and untamed."
        ),

        ItemData(
            "HOOT",
            gdxGame.assetsAll.listItemPets[2],
            "Hoot is a wise little companion with sharp eyes and an unmistakably serious expression. " +
                    "Featuring an owl-inspired design, a tiny aviator cap, and calm, watchful animations, this pet brings quiet character to every adventure. " +
                    "Its thoughtful appearance makes it stand apart from louder companions, while its steady presence adds warmth to every match. " +
                    "Whether keeping watch beside you in battle or simply perched in the lobby, Hoot is a charming companion for players who appreciate personality over noise."
        ),

        ItemData(
            "FINN",
            gdxGame.assetsAll.listItemPets[3],
            "Finn is a laid-back companion who brings summer energy straight to the battlefield. " +
                    "Featuring a shark-inspired design, a bright surfboard, and confident, easygoing animations, this pet turns every adventure into a good time. " +
                    "Its playful appearance makes it instantly likeable, while its relaxed attitude adds a lighthearted touch to even the tensest match. " +
                    "Whether riding along into battle or showing off in the lobby, Finn is a fun companion for players who never take themselves too seriously."
        ),

        ItemData(
            "ZASIL",
            gdxGame.assetsAll.listItemPets[4],
            "Zasil is an adorable companion with soft colors and a gentle, curious personality. " +
                    "Featuring an axolotl-inspired design, a bright swim ring, and sweet, bouncy animations, this pet brings pure charm to every adventure. " +
                    "Its cute appearance makes it an instant favorite, while its friendly energy adds warmth to every match. " +
                    "Whether floating along beside you in battle or brightening up the lobby, Zasil is a delightful companion for players who like their style soft and cheerful."
        ),

        ItemData(
            "ARVON",
            gdxGame.assetsAll.listItemPets[5],
            "Arvon is a bold companion with vivid colors and a confident, restless attitude. " +
                    "Featuring a lizard-inspired design, a spiked crest, and quick, alert animations, this pet brings sharp character to every adventure. " +
                    "Its striking appearance makes it stand out at a glance, while its lively energy adds attitude to every match. " +
                    "Whether darting along beside you into battle or posing in the lobby, Arvon is a memorable companion for players who like a little wildness in their style."
        ),
    )
}