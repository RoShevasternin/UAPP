package com.fimer.skintool.game.data.selectors

import com.fimer.skintool.game.data.ItemData
import com.fimer.skintool.game.utils.gdxGame

object WeaponData {
    fun items(): List<ItemData> = listOf(

        ItemData(
            "RGS50",
            gdxGame.assetsAll.listItemWeapon[0],
            "The RGS50 is a heavy explosive launcher built for maximum battlefield impact. " +
                    "Firing powerful grenade rounds, it excels at dealing area damage, destroying enemy cover, and controlling key positions. " +
                    "Its explosive projectiles can hit multiple targets at once, making every shot a serious threat. " +
                    "Although it has a slower firing rate than standard weapons, careful timing and accurate aim allow experienced players to unleash devastating attacks. " +
                    "With its unmatched destructive power and tactical versatility, the RGS50 is an excellent choice for those who prefer explosive, high-risk, high-reward combat."
        ),

        ItemData(
            "MGL140",
            gdxGame.assetsAll.listItemWeapon[1],
            "The MGL140 is a revolving grenade launcher designed to flood an area with explosives in seconds. " +
                    "Where other launchers fire once and force a reload, this one keeps going, letting you chain several blasts across a wide zone. " +
                    "It shines when you need to break a defended position or push a squad out of a building fast. " +
                    "The trade-off is weight and slow handling, so it rewards players who plan their angle before they commit. " +
                    "With its rapid explosive output and heavy area control, the MGL140 is an excellent choice for aggressive players who like to dictate the fight."
        ),

        ItemData(
            "M79",
            gdxGame.assetsAll.listItemWeapon[2],
            "The M79 is a classic single-shot grenade launcher known for its simplicity and raw punch. " +
                    "One well-placed round can clear a rooftop, break a camping squad, or turn a losing fight around instantly. " +
                    "Its arcing projectile takes practice to master, but experienced players can drop shells over walls and hit targets they cannot even see. " +
                    "The long reload between shots means every trigger pull has to count. " +
                    "With its precise arc and legendary stopping power, the M79 is an excellent choice for players who value one perfect shot over constant fire."
        ),

        ItemData(
            "GATLING",
            gdxGame.assetsAll.listItemWeapon[3],
            "The Gatling is a rotary heavy weapon built to lay down an overwhelming wall of fire. " +
                    "Once the barrels spin up, it delivers a sustained stream of bullets that shreds cover, pins enemies down, and denies entire lanes of movement. " +
                    "Its enormous magazine keeps you firing long after other weapons run dry, making it ideal for holding a position under pressure. " +
                    "The spin-up delay and heavy weight demand good positioning, so it rewards players who set up before the fight arrives. " +
                    "With its relentless rate of fire and pure suppressive power, the Gatling is an excellent choice for players who prefer to dominate through volume."
        ),

        ItemData(
            "M60",
            gdxGame.assetsAll.listItemWeapon[4],
            "The M60 is a battle-proven light machine gun that balances firepower with mobility. " +
                    "Its large magazine and steady rate of fire let you fight through several engagements without stopping to reload. " +
                    "It performs best at mid range, where controlled bursts keep the recoil in check and every shot lands where you want it. " +
                    "Held down too long, the spread widens quickly, so trigger discipline separates good players from great ones. " +
                    "With its dependable output and versatile range, the M60 is an excellent choice for players who want firepower they can carry anywhere."
        ),

        ItemData(
            "M240",
            gdxGame.assetsAll.listItemWeapon[5],
            "The M240 is a heavy machine gun built for players who never want to stop shooting. " +
                    "It hits harder than lighter machine guns and holds enough ammunition to keep an entire squad locked behind cover. " +
                    "Set up on high ground or in a doorway, it turns a single position into a wall nobody wants to cross. " +
                    "Its weight slows you down and the recoil builds fast, so it favors patience over chasing kills. " +
                    "With its brutal damage and endless ammunition, the M240 is an excellent choice for players who prefer to hold ground and win the long fight."
        ),
    )
}