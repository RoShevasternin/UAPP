package com.rbuxrds.counter.game.actors.allCharacters

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.rbuxrds.counter.game.actors.label.ALabel
import com.rbuxrds.counter.game.utils.GameColor
import com.rbuxrds.counter.game.utils.actor.addActors
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.actor.setOnTouchListener
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.font.FontParameter
import com.rbuxrds.counter.game.utils.gdxGame

class ACharacters(override val screen: AdvancedScreen) : AdvancedGroup() {

    override fun getPrefHeight() = height
    override fun getPrefWidth() = width

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------

    private val listDataCharacter = listOf(
        DataCharacter(
            "Addison",
            gdxGame.assetsAll.listCharacters[0],
            "Addison is a stylish and trendy character known for her vibrant and confident personality. Her design features fashionable clothing and a cheerful expression, making her perfect for social settings. Great for casual and trendy looks, fitting well in modern city adventures. Pairs well with stylish accessories, sunglasses, and sneakers. Often seen in roleplaying games and hangout experiences."
        ),
        DataCharacter(
            "Blazer Burner",
            gdxGame.assetsAll.listCharacters[1],
            "Blazer Burner is an edgy and high-energy character with a fiery aesthetic. He is often associated with speed, punk culture, and intense action. His design features metallic details and a bold, aggressive look. Perfect for racing games or combat scenarios where speed is key. Pairs well with futuristic gear and flame-themed accessories. A favorite for players who love a rebellious and powerful vibe."
        ),
        DataCharacter(
            "Calico",
            gdxGame.assetsAll.listCharacters[2],
            "Calico is a nimble and agile character inspired by feline traits and patchwork aesthetics. She is known for her quick reflexes and colorful, artistic appearance. Her design is playful yet mysterious, making her ideal for platformers or stealth-based adventures. Great for players who enjoy unique, animal-themed designs. Pairs well with cat-ear headbands and soft, colorful clothing. Often found in whimsical and magical worlds."
        ),
        DataCharacter(
            "Capybara",
            gdxGame.assetsAll.listCharacters[3],
            "Capybara is the ultimate chill and friendly character, embodying a relaxed and social spirit. Known for being the most peaceful resident of the digital world, this character is perfect for simulators and gathering spots. His design is simple, cute, and universally liked. Great for players who want to express a laid-back attitude. Pairs well with nature-themed hats or sunglasses. A true icon of calmness and social harmony."
        ),
        DataCharacter(
            "Cindy",
            gdxGame.assetsAll.listCharacters[4],
            "Cindy is a classic and versatile character with a friendly, approachable look. She is designed to be highly customizable, fitting into almost any game genre from town life to fantasy. Her expression is warm and welcoming, making her a great choice for beginner-friendly experiences. Pairs well with a wide variety of hair styles and outfits. Often used as a reliable and recognizable avatar for everyday adventures."
        ),
        DataCharacter(
            "City life woman",
            gdxGame.assetsAll.listCharacters[5],
            "City life woman represents the modern urban professional with a chic and sophisticated style. She is perfect for high-fashion roleplays and metropolitan simulators. Her design focuses on sleek clothing and a poised demeanor, reflecting a busy but elegant lifestyle. Great for city-themed maps and corporate office roleplays. Pairs well with business attire, handbags, and high-heeled shoes. A staple for urban-themed games."
        ),
        DataCharacter(
            "Claire",
            gdxGame.assetsAll.listCharacters[6],
            "Claire is a gentle and artistic character who loves everything related to nature and creativity. She has a soft, aesthetic look that appeals to players who enjoy peaceful and imaginative gameplay. Her design often includes floral patterns and pastel colors. Perfect for gardening simulators or art-focused experiences. Pairs well with flower crowns and cozy sweaters. Known for her kind heart and creative spark in social games."
        ),
        DataCharacter(
            "Dennis",
            gdxGame.assetsAll.listCharacters[7],
            "Dennis is a cool and energetic skater boy who brings a sense of fun and action to any lobby. With his sporty outfit and confident grin, he’s ready for any physical challenge. Great for sports games, obstacle courses (obbbies), and skating parks. His design is simple yet iconic, making him very recognizable. Pairs well with skateboards, backpacks, and caps. A go-to choice for players who want a classic, active avatar."
        ),
        DataCharacter(
            "Denny",
            gdxGame.assetsAll.listCharacters[8],
            "Denny is the more relaxed and easy-going counterpart to the classic active characters. He values comfort and friendship above all else, often seen hanging out at local spots. His design is casual and relatable, perfect for school or hangout roleplays. Great for casual gameplay where socializing is the main goal. Pairs well with hoodies, beanies, and basic sneakers. He represents a grounded and friendly personality."
        ),
        DataCharacter(
            "John",
            gdxGame.assetsAll.listCharacters[9],
            "John is a legendary and heroic character, often seen as a symbol of bravery and classic adventure. With a sturdy build and a determined look, he is the ideal protagonist for quest-based RPGs and battle games. His design is timeless and inspires trust among teammates. Great for leadership roles and frontline combat scenarios. Pairs well with swords, shields, and tactical gear. A true veteran of many digital battles."
        ),
        DataCharacter(
            "Junkbot",
            gdxGame.assetsAll.listCharacters[10],
            "Junkbot is a quirky and mechanical character built from scraps and recycled parts. He has a unique, clunky charm and a surprisingly expressive personality for a robot. Perfect for sci-fi adventures or games set in post-apocalyptic worlds. His design is eccentric and full of detail, appealing to tech lovers. Pairs well with robotic tools, antennas, and glowing energy cores. Known for his resilience and mechanical ingenuity."
        ),
        DataCharacter(
            "Knights of Redcliff",
            gdxGame.assetsAll.listCharacters[11],
            "The Knights of Redcliff are noble and disciplined warriors sworn to protect the realm from darkness. They are characterized by their heavy silver armor and red accents, symbolizing courage and honor. Perfect for large-scale medieval battles and fantasy RPGs. Great for players who want to uphold justice and lead an army. Pairs well with majestic horses and enchanted weaponry. They are the ultimate guardians of peace."
        ),
        DataCharacter(
            "Lindsey",
            gdxGame.assetsAll.listCharacters[12],
            "Lindsey is a bright and sporty character with a passion for competitive games and teamwork. She is always ready for a match, whether it's soccer, racing, or a dance-off. Her design is colorful and dynamic, reflecting her active lifestyle. Great for gym simulators and team-based sports games. Pairs well with athletic headbands, jerseys, and sport watches. She brings a positive, competitive energy to every team."
        ),
        DataCharacter(
            "Linlin",
            gdxGame.assetsAll.listCharacters[13],
            "Linlin is an elegant and graceful character inspired by traditional beauty and modern flair. She moves with a sense of poise that makes her stand out in any crowd. Her design features intricate patterns and a refined aesthetic, perfect for fantasy or ballroom roleplays. Great for players who appreciate detail and sophistication. Pairs well with traditional fans, silk robes, and elegant jewelry. A symbol of grace and cultural charm."
        ),
        DataCharacter(
            "Monster Hugs",
            gdxGame.assetsAll.listCharacters[14],
            "Monster Hugs is a large, fluffy, and misunderstood creature who just wants to make friends. Despite his intimidating size, his soft fur and big eyes reveal a gentle soul. Perfect for quirky adventure games or as a lovable companion in social hubs. Great for players who love unconventional and cute designs. Pairs well with oversized accessories and colorful scarves. He proves that monsters can be the kindest friends."
        ),
        DataCharacter(
            "Oliver",
            gdxGame.assetsAll.listCharacters[15],
            "Oliver is a smart and organized character with a preppy and academic style. He is often seen as the strategist of the group, always prepared with a plan. His design features neat clothing and a studious look, making him perfect for school-themed games or puzzle adventures. Great for players who enjoy a clever and polished aesthetic. Pairs well with glasses, books, and vests. A reliable friend who values intelligence and order."
        ),
        DataCharacter(
            "Peyton",
            gdxGame.assetsAll.listCharacters[16],
            "Peyton is a modern and tech-savvy character who is always up to date with the latest gadgets and trends. He has a cool, effortless style that fits perfectly in futuristic or high-tech settings. His design often includes headphones and neon details. Perfect for hacker-themed games or neon city explorations. Pairs well with drones, tablets, and light-up sneakers. He is the bridge between the present and the digital future."
        ),
        DataCharacter(
            "Roblax",
            gdxGame.assetsAll.listCharacters[17],
            "Roblax is a cyber-enhanced character with a sleek, futuristic design and high-tech capabilities. He represents the pinnacle of digital evolution, featuring glowing circuits and advanced armor plating. Ideal for space exploration games and futuristic shooters. Great for players who love an edgy, robotic look. Pairs well with laser blasters, jetpacks, and holographic visors. A powerful and mysterious figure in the digital universe."
        ),
        DataCharacter(
            "Squad Ghouls",
            gdxGame.assetsAll.listCharacters[18],
            "The Squad Ghouls are a spooky and fun group of characters who love everything gothic and mysterious. They bring a dark yet playful aesthetic to the game, perfect for Halloween events or haunted house simulators. Their design features dark clothing, pale skin, and ghostly accessories. Great for players who enjoy a bit of mystery and macabre humor. Pairs well with bats, pumpkins, and gothic capes. Spooky but always cool."
        ),
        DataCharacter(
            "Summer",
            gdxGame.assetsAll.listCharacters[19],
            "Summer is a cheerful and sunny character who embodies the spirit of beach days and warm holidays. She is always ready for a vacation, bringing a tropical vibe wherever she goes. Her design features bright colors, swimwear, and sun-kissed details. Perfect for island simulators, water parks, and summer-themed festivals. Pairs well with surfboards, ice cream cones, and straw hats. She is a constant ray of sunshine."
        )
    )

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onSelectCharacter: (DataCharacter) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        var nx = 16f
        var ny = 1096f

        listDataCharacter.forEachIndexed { index, data ->
            val img = Image(data.region)
            val lbl = ALabel(screen, data.name, GameColor.gray_5C6070, parameter, screen.fontGenerator_InterTight_Medium)

            addActors(img, lbl)
            img.setBounds(nx, ny, 109f, 121f)
            lbl.setBounds(nx, ny - (8f + 22f), 109f, 22f)
            nx += 9f + 109f
            if (index.inc() % 3 == 0) {
                ny -= 54f + 121f
                nx = 16f
            }

            lbl.setAlignment(Align.center)
            img.setOnTouchListener { onSelectCharacter(data) }
        }
    }

}