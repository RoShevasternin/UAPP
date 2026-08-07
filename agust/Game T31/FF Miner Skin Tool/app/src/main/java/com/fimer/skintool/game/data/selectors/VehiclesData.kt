package com.fimer.skintool.game.data.selectors

import com.fimer.skintool.game.data.ItemData
import com.fimer.skintool.game.utils.gdxGame

object VehiclesData {
    fun items(): List<ItemData> = listOf(

        ItemData(
            "DEFAULT PICKUP TRUCK",
            gdxGame.assetsAll.listItemVehicles[0],
            "The Default Pickup Truck is a dependable vehicle built for fast travel and reliable transportation across the battlefield. " +
                    "With its sturdy frame and balanced handling, it performs well on both paved roads and rough terrain, allowing you to reach key locations quickly and safely. " +
                    "Its spacious design makes it ideal for carrying an entire squad, while its durability helps protect passengers during intense encounters. " +
                    "Whether you're rotating to the next zone or making a quick escape, the Default Pickup Truck is a trusted choice for every mission."
        ),

        ItemData(
            "JEEP",
            gdxGame.assetsAll.listItemVehicles[1],
            "The Jeep is a rugged military-style vehicle built to go where smoother rides cannot. " +
                    "Its high suspension and open frame let it climb steep hills, cross broken ground, and take shortcuts that force other squads onto the main roads. " +
                    "The open sides make it easy to jump in and out under pressure, though they leave passengers more exposed to fire. " +
                    "Whether you're crossing rough country or repositioning fast between fights, the Jeep is a solid choice for players who value mobility over cover."
        ),

        ItemData(
            "TUK TUK",
            gdxGame.assetsAll.listItemVehicles[2],
            "The Tuk Tuk is a light three-wheeled ride that trades power for agility and charm. " +
                    "Its compact frame slips through narrow streets and tight alleys where larger vehicles get stuck, making it surprisingly effective in built-up areas. " +
                    "It won't win any races across open ground, but for short hops between buildings it gets your squad moving in seconds. " +
                    "Whether you're weaving through a crowded town or making a cheeky getaway, the Tuk Tuk turns every ride into a memorable one."
        ),

        ItemData(
            "AMPHIBIAN",
            gdxGame.assetsAll.listItemVehicles[3],
            "The Amphibian is a versatile all-terrain vehicle designed to move from land to water without slowing down. " +
                    "Where other vehicles stop at the shoreline, this one keeps going, opening up river crossings and coastal routes that most squads never consider. " +
                    "Its compact size keeps it quick and responsive, though it carries fewer passengers than a full truck. " +
                    "Whether you're flanking across a lake or escaping a closing zone the long way around, the Amphibian gives you options nobody expects."
        ),

        ItemData(
            "MONSTER TRUCK",
            gdxGame.assetsAll.listItemVehicles[4],
            "The Monster Truck is a heavy off-road beast built to crush everything in its path. " +
                    "Its oversized wheels and raised chassis let it roll straight over obstacles, fences and rough ground that would stop any normal vehicle. " +
                    "The sheer size makes it loud and impossible to hide, so everyone in the area will know you're coming. " +
                    "Whether you're smashing through a blockade or making the boldest entrance on the map, the Monster Truck is pure unstoppable presence."
        ),

        ItemData(
            "DEFAULT MOTORCYCLE",
            gdxGame.assetsAll.listItemVehicles[5],
            "The Default Motorcycle is the fastest way to cover open ground when every second counts. " +
                    "Its light frame and sharp acceleration make it perfect for outrunning a closing zone or reaching a distant drop before anyone else. " +
                    "Speed comes at a cost: there's no armor, little cover, and room for only a passenger or two. " +
                    "Whether you're scouting ahead of your squad or racing to claim a position first, the Default Motorcycle rewards players who move fast and think faster."
        ),
    )
}