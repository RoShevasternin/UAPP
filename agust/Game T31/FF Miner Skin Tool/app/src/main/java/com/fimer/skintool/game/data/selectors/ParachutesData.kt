package com.fimer.skintool.game.data.selectors

import com.fimer.skintool.game.data.ItemData
import com.fimer.skintool.game.utils.gdxGame

object ParachutesData {
    fun items(): List<ItemData> = listOf(

        ItemData(
            "DEFAULT PARACHUTE",
            gdxGame.assetsAll.listItemParachutes[0],
            "The Default Parachute is a reliable piece of equipment designed to ensure a safe and controlled landing at the start of every match. " +
                    "Its durable canopy provides stable descent, giving you enough time to scout the surroundings, choose the best landing spot, and plan your next move. " +
                    "While simple in appearance, this classic parachute has become a familiar companion for countless players. " +
                    "Whether you're aiming for high-value loot or a quiet starting location, it delivers dependable performance every time you jump into action."
        ),

        ItemData(
            "GREEN",
            gdxGame.assetsAll.listItemParachutes[1],
            "The Green Parachute is a clean, understated design built for players who prefer to arrive unnoticed. " +
                    "Its deep olive canopy blends into forests and open fields, making you a harder target to spot during those vulnerable seconds of descent. " +
                    "Simple, sharp and free of unnecessary decoration, it keeps all the attention on where you land rather than how you got there. " +
                    "Whether you're dropping into a contested zone or gliding toward a quiet corner of the map, it's a dependable pick for a low-profile start."
        ),

        ItemData(
            "CAMOUFLAGE - RED",
            gdxGame.assetsAll.listItemParachutes[2],
            "The Red Camouflage Parachute brings a bold, fiery pattern to the opening moments of every match. " +
                    "Its rust-toned canopy stands out against the sky, announcing your arrival to everyone watching the drop. " +
                    "Built for players who don't mind being seen, it turns the descent itself into a statement of confidence. " +
                    "Whether you're leading your squad into a hot zone or landing first to claim the best loot, it makes sure your entrance never goes unnoticed."
        ),

        ItemData(
            "CAMOUFLAGE - OLIVE",
            gdxGame.assetsAll.listItemParachutes[3],
            "The Olive Camouflage Parachute is a classic military design made for players who value function over flash. " +
                    "Its mottled green pattern breaks up your silhouette against treetops and grassland, helping you slip into position unseen. " +
                    "The look is battle-worn and practical, the kind of gear that suggests you've done this many times before. " +
                    "Whether you're planning a quiet flank or scouting the map before your squad commits, it's a solid choice for a tactical start."
        ),

        ItemData(
            "CAMOUFLAGE - SAND",
            gdxGame.assetsAll.listItemParachutes[4],
            "The Sand Camouflage Parachute is built for dry terrain, with a pale desert pattern and a bright accent that catches the light. " +
                    "Its light canopy blends with open sand and rocky ground, making it a natural fit for the drier corners of the map. " +
                    "Clean and distinctive at the same time, it strikes a balance between staying subtle and looking sharp. " +
                    "Whether you're descending onto a desert outpost or crossing wide open ground, it keeps your arrival stylish and grounded."
        ),

        ItemData(
            "SKULL",
            gdxGame.assetsAll.listItemParachutes[5],
            "The Skull Parachute is a bold statement piece for players who want their arrival to feel like a warning. " +
                    "Its dark canopy carries a striking skull design that's impossible to mistake as you drop toward the battlefield. " +
                    "Built for confident players, it turns those first seconds of the match into a moment of intimidation before a single shot is fired. " +
                    "Whether you're diving into the busiest zone on the map or landing behind an unsuspecting squad, it makes sure everyone knows exactly who just showed up."
        ),
    )
}