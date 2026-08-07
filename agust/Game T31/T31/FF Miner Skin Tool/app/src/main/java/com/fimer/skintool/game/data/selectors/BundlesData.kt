package com.fimer.skintool.game.data.selectors

import com.fimer.skintool.game.data.ItemData
import com.fimer.skintool.game.utils.gdxGame

object BundlesData {
    fun items(): List<ItemData> = listOf(

        ItemData(
            "RED CRIMINAL",
            gdxGame.assetsAll.listItemBundles[0],
            "Red Criminal is one of the most iconic and legendary outfits ever released. " +
                    "Its striking red design, signature hood, and unforgettable mask create a bold appearance that instantly stands out on the battlefield. " +
                    "Worn by players who enjoy making a statement, this outfit combines mystery, confidence, and style in every detail. " +
                    "Even years after its debut, Red Criminal remains one of the most sought-after cosmetic sets, earning its place as a true classic among collectors and dedicated players."
        ),

        ItemData(
            "CLAMITY SCOUT",
            gdxGame.assetsAll.listItemBundles[1],
            "Clamity Scout is a dark and mysterious outfit built around deep purple energy and shadowed detailing. " +
                    "Its flowing silhouette and glowing accents give the impression of someone who moves through the battlefield unseen until the moment it matters. " +
                    "Chosen by players who prefer atmosphere over noise, this set turns every appearance into something quietly unsettling. " +
                    "With its haunting color palette and unmistakable presence, Clamity Scout has become a favorite among players who like their style with a touch of menace."
        ),

        ItemData(
            "CROCODARER",
            gdxGame.assetsAll.listItemBundles[2],
            "Crocodarer is a sharp, armored outfit that blends street attitude with battlefield toughness. " +
                    "Its layered jacket, textured plating, and confident stance create the look of a fighter who has survived more than a few close calls. " +
                    "Built for players who want to look prepared rather than flashy, every element of the set feels practical and lived-in. " +
                    "With its rugged design and quiet confidence, Crocodarer stands out as a favorite among players who let their gameplay do the talking."
        ),

        ItemData(
            "COBRA RAGE",
            gdxGame.assetsAll.listItemBundles[3],
            "Cobra Rage is an aggressive red-and-black outfit designed for players who fight on the front line. " +
                    "Its bold jacket, sharp accents, and striking silhouette make it impossible to overlook when you push into a contested zone. " +
                    "Every detail is built around speed and intimidation, matching the pace of players who never wait for the fight to come to them. " +
                    "With its fierce styling and unmistakable energy, Cobra Rage has earned its place among the most recognizable sets in the game."
        ),

        ItemData(
            "JELLY READY",
            gdxGame.assetsAll.listItemBundles[4],
            "Jelly Ready is a vivid outfit that mixes bright green accents with a sleek, futuristic build. " +
                    "Its glowing details and unusual color scheme make it a set you notice immediately, whether in the lobby or across the map. " +
                    "Chosen by players who enjoy standing apart from the crowd, it turns bold color into part of the strategy. " +
                    "With its distinctive look and playful energy, Jelly Ready has become a favorite among players who refuse to blend in."
        ),

        ItemData(
            "FREESTYLE DIVA",
            gdxGame.assetsAll.listItemBundles[5],
            "Freestyle Diva is a colorful, expressive outfit built for players who treat every match as a performance. " +
                    "Its bright hair, layered accessories, and vibrant styling create an appearance full of personality and confidence. " +
                    "Made for those who like their victories loud and their entrances louder, this set brings pure energy to the battlefield. " +
                    "With its eye-catching design and unmistakable attitude, Freestyle Diva has become a favorite among players who play with style."
        ),
    )
}