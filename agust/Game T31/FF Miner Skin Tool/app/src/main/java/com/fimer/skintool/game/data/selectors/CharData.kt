package com.fimer.skintool.game.data.selectors

import com.fimer.skintool.game.data.ItemData
import com.fimer.skintool.game.utils.gdxGame

object CharData {
    fun items(): List<ItemData> = listOf(

        ItemData(
            "A-PATROA",
            gdxGame.assetsAll.listItemChar[0],
            "A-Patroa is a fearless trendsetter known for her bold fashion, unmatched confidence, and unmistakable attitude. " +
                    "Her striking hairstyle, stylish accessories, and luxurious outfit create a look that instantly captures attention wherever she goes. " +
                    "Blending elegance with street style, she represents confidence, individuality, and power in every detail. " +
                    "Whether you're making an entrance before the action begins or standing victorious after an intense match, A-Patroa delivers a memorable presence that leaves a lasting impression on everyone around her."
        ),

        ItemData(
            "RAY",
            gdxGame.assetsAll.listItemChar[1],
            "Ray is a seasoned fighter known for lightning-fast reflexes and an uncanny ability to spot trouble before it arrives. " +
                    "His golden scarf, worn leather gear, and relaxed stance create the look of someone who has been through every kind of fight and walked away. " +
                    "Calm under pressure and precise when it matters, he represents experience, control, and quiet confidence. " +
                    "Whether you're leading a push into contested ground or holding the line until your squad regroups, Ray brings the steady presence of a veteran who has seen it all before."
        ),

        ItemData(
            "KELLY",
            gdxGame.assetsAll.listItemChar[2],
            "Kelly is a fast-moving specialist built around speed, momentum, and split-second decisions. " +
                    "Her bright yellow jacket, sharp posture, and ready stance create the look of someone who is always half a step ahead of everyone else. " +
                    "Quick to move and quicker to react, she represents agility, initiative, and relentless forward pressure. " +
                    "Whether you're rushing an early drop or chasing down a fleeing opponent, Kelly is the perfect match for players who never stop moving."
        ),

        ItemData(
            "KESSIE",
            gdxGame.assetsAll.listItemChar[3],
            "Kessie is a vibrant fighter who turns every appearance into a burst of pure energy. " +
                    "Her pink-and-white hair, playful accessories, and bold styling create a look that stands out long before the first shot is fired. " +
                    "Loud, fearless and impossible to overlook, she represents personality, attitude, and the joy of playing your own way. " +
                    "Whether you're celebrating in the lobby or diving straight into the busiest zone on the map, Kessie brings colour and confidence to every match."
        ),

        ItemData(
            "MORSE",
            gdxGame.assetsAll.listItemChar[4],
            "Morse is a silent operator who works best when nobody knows he is there. " +
                    "His sealed mask, dark tactical gear, and controlled movements create the look of someone who plans three steps ahead and never rushes a decision. " +
                    "Patient, precise and unreadable, he represents stealth, discipline, and the value of perfect timing. " +
                    "Whether you're setting up an ambush or slipping behind an unsuspecting squad, Morse is built for players who prefer to strike once and strike well."
        ),

        ItemData(
            "NERO",
            gdxGame.assetsAll.listItemChar[5],
            "Nero is a cold-tempered fighter who brings stillness to the middle of a firefight. " +
                    "His white hair, pale coat, and composed stance create the look of someone who never raises his voice and never needs to. " +
                    "Calm, deliberate and difficult to rattle, he represents patience, control, and the strength of holding your ground. " +
                    "Whether you're defending a position against a full squad or waiting out the final circle, Nero is the perfect match for players who win by staying calm."
        ),
    )
}