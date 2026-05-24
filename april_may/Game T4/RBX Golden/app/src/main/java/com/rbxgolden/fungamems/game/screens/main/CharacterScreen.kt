package com.rbxgolden.fungamems.game.screens.main

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.panel.APanelTop
import com.rbxgolden.fungamems.game.utils.Block
import com.rbxgolden.fungamems.game.utils.GLOBAL_SELECTED_CHARACTER_INDEX
import com.rbxgolden.fungamems.game.utils.GameColor
import com.rbxgolden.fungamems.game.utils.TIME_ANIM_SCREEN
import com.rbxgolden.fungamems.game.utils.actor.animDelay
import com.rbxgolden.fungamems.game.utils.actor.animHide
import com.rbxgolden.fungamems.game.utils.actor.animShow
import com.rbxgolden.fungamems.game.utils.actor.setSize
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.font.FontFactory
import com.rbxgolden.fungamems.game.utils.font.FontParameter
import com.rbxgolden.fungamems.game.utils.gdxGame

class CharacterScreen: AdvancedScreen() {

    private val maxIndex              = gdxGame.assetsAll.listCharacter.lastIndex
    private val currentCharacterIndex = GLOBAL_SELECTED_CHARACTER_INDEX.coerceIn(0, maxIndex)

    private val charName = listOf(
        "ROBLOX Girl",
        "ROBLOX Boy",
        "Woman",

        "Man",
        "ROBLOX Girl",
        "Skyler",

        "Dennis",
        "Lindsey",
        "Kenneth",

        "Cindy",
        "Knights of Redcliff",
        "Drop Dead Tedd",

        "Junkbot",
        "City Life Woman",
        "Oliver",

        "Summer",
        "Casey",
        "Claire",

        "Oakley",
        "Lin",
        "John",

        "Serena",
        "Addison",
        "Dawson",
    )[currentCharacterIndex]

    private val charDesc = listOf(
        "ROBLOX Girl is a cheerful and classic character with a soft pastel style and friendly personality. Perfect for casual adventures and beginner roleplay experiences. Her simple look makes her easy to customize with cute accessories and stylish outfits.",

        "ROBLOX Boy is a classic starter-style character known for his simple and friendly appearance. Great for casual gameplay, roleplay servers, and fun social experiences. Fits perfectly with sporty outfits and beginner adventures.",

        "Woman is an elegant and calm character with a stylish modern appearance. Her design works great for city roleplay games and fashion experiences. Pairs well with trendy accessories and stylish clothing combinations.",

        "Man is a simple and versatile character with a clean casual design. Great for roleplay servers, city adventures, and everyday gameplay experiences. Works well with realistic outfits and classic styles.",

        "ROBLOX Girl is a bright and playful character with colorful styling and a positive personality. Popular for hangout games and social roleplaying experiences. Looks great with fun accessories and expressive animations.",

        "Skyler is a trendy and energetic character with a youthful appearance and modern fashion style. Perfect for social games and casual adventures. Often used in roleplay experiences and friendly multiplayer worlds.",

        "Dennis is a confident and adventurous character with a cool classic gamer style. Known for his iconic look and energetic vibe in action-packed experiences. Great for adventure games and fun multiplayer challenges.",

        "Lindsey is a fashionable and confident character with a modern casual appearance. She fits perfectly into city roleplays and social hangout experiences. Stylish clothing and relaxed vibes make her stand out in any server.",

        "Kenneth is a calm and reliable character with a sporty urban look. His design is perfect for roleplay games, exploration adventures, and team-based experiences. Works great with modern accessories and cool animations.",

        "Cindy is a fun and cheerful character with bright colors and playful fashion. Perfect for friendly roleplay worlds and casual multiplayer games. Her vibrant style pairs well with cute accessories and expressive emotes.",

        "Knights of Redcliff is a legendary warrior character with powerful armor and heroic energy. Designed for fantasy battles, medieval adventures, and epic combat experiences. Perfect for players who enjoy strong and fearless heroes.",

        "Drop Dead Tedd is a spooky and mysterious character with a dark stylish appearance. Great for horror games, Halloween adventures, and creepy roleplay experiences. His unique look makes him stand out in darker themed worlds.",

        "Junkbot is a futuristic robotic character with mechanical details and sci-fi vibes. Perfect for technology-themed games and futuristic adventures. Works great in action experiences with robots and advanced worlds.",

        "City Life Woman is a modern and realistic character made for social city roleplays and everyday adventures. Her casual style and natural appearance fit perfectly into life simulation experiences.",

        "Oliver is a cool and athletic character with a modern urban appearance. Great for hangout servers, roleplaying games, and stylish multiplayer experiences. Pairs well with sporty and casual accessories.",

        "Summer is a calm and friendly character with a clean casual style. Perfect for relaxing adventures, social experiences, and roleplay games. Her simple appearance makes customization easy and fun.",

        "Casey is a fun-loving and energetic character with a stylish modern look and cool sunglasses. Great for beach vibes, city adventures, and casual roleplay experiences. Fits perfectly with trendy accessories and playful animations.",

        "Claire is a bright and fashionable character with colorful clothing and a confident personality. Popular in social roleplay games and fun multiplayer adventures. Her vibrant style makes her stand out in any crowd.",

        "Oakley is a confident gamer-style character with casual clothing and a cool attitude. Perfect for adventure servers, hangout games, and energetic multiplayer experiences. Looks great with sporty accessories and modern styles.",

        "Lin is a smart and creative character with a calm personality and stylish appearance. Great for social games, school roleplays, and modern city adventures. Pairs well with glasses and trendy outfits.",

        "John is a cool and adventurous character with a bold urban style. Perfect for action roleplays and multiplayer exploration games. His confident design fits well with modern accessories and streetwear fashion.",

        "Serena is a stylish explorer character with warm clothing and adventurous vibes. Great for fantasy roleplays and social adventure experiences. Her unique style works perfectly in creative multiplayer worlds.",

        "Addison is a stylish and trendy character known for her vibrant and confident personality. Her design features fashionable clothing and a cheerful expression, making her perfect for social settings. Great for casual and trendy looks, fitting well in modern city adventures. Pairs well with stylish accessories, sunglasses, and sneakers. Often seen in roleplaying games and hangout experiences.",

        "Dawson is a realistic and modern character with a calm confident appearance. Perfect for life simulation games and social roleplaying experiences. Her casual design fits well with stylish city adventures and modern worlds.",

        )[currentCharacterIndex]

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter36 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setBorder(2f, GameColor.orange_FE)
        .setSize(36)
    private val parameter14 = FontParameter()
        .setCharacters(FontParameter.CharType.ALL)
        .setSize(14)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------

    private val aPanelTop by lazy { APanelTop(this) }

    private val aCharIconImg  by lazy { Image(gdxGame.assetsAll.listCharacter[currentCharacterIndex]) }
    private val aCharNameLbl  by lazy { Label(charName, FontFactory.create(this, parameter36, fontGenerator_Bold, GameColor.yellow_FF)) }
    private val aCharDescLbl  by lazy { Label(charDesc, FontFactory.create(this, parameter14, fontGenerator_Medium, Color.WHITE)) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun show() {
        //val coords = stageUI.root.localToScreenCoordinates(Vector2(0f, safeBannerUI))
        //gdxGame.activity.showNativeAt(coords.y)

        stageUI.root.color.a = 0f
        super.show()
        animShowScreen()
    }

//    override fun hide() {
//        super.hide()
//        gdxGame.activity.hideNative()
//    }

    override fun AConstraintLayout.addActorsOnRootConstraintLayout() {
        addPanelTop()
        addCharIconImg()
        addCharNameLbl()
        addCharDescLbl()
    }

    // ------------------------------------------------------------------------
    // Screen Animations
    // ------------------------------------------------------------------------
    override fun animHideScreen(blockEnd: Block) {
        stageUI.root.animHide(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    override fun animShowScreen(blockEnd: Block) {
        stageUI.root.animShow(TIME_ANIM_SCREEN)
        stageUI.root.animDelay(TIME_ANIM_SCREEN) { blockEnd() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun AConstraintLayout.addPanelTop() {
        aPanelTop.setSize(WIDTH, 56f)
        add(aPanelTop) { centerX(); topToTop() }

        aPanelTop.setTitle(charName)
        aPanelTop.onBack = { animHideScreen { gdxGame.navigationManager.back() } }
    }

    private fun AConstraintLayout.addCharIconImg() {
        aCharIconImg.setSize(168f, 168f)
        add(aCharIconImg) { centerX(); topToBottom(aPanelTop, 8f) }
    }

    private fun AConstraintLayout.addCharNameLbl() {
        aCharNameLbl.setSize(168f, 44f)
        add(aCharNameLbl) { centerX(); topToBottom(aCharIconImg, 10f) }

        aCharNameLbl.setAlignment(Align.center)
    }

    private fun AConstraintLayout.addCharDescLbl() {
        aCharDescLbl.setSize(344f, 100f)
        add(aCharDescLbl) { centerX(); topToBottom(aCharNameLbl, 16f) }

        aCharDescLbl.setAlignment(Align.center, Align.top)
        aCharDescLbl.wrap = true
    }

}