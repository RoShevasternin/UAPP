package com.rbxgolden.fungamems.game.actors.panel

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbxgolden.fungamems.adsmodule.AdSizeManager
import com.rbxgolden.fungamems.game.actors.AScrollPane
import com.rbxgolden.fungamems.game.actors.ATmpGroup
import com.rbxgolden.fungamems.game.actors.layout.AlignH
import com.rbxgolden.fungamems.game.actors.layout.constraintLayout.AConstraintLayout
import com.rbxgolden.fungamems.game.actors.layout.linear.AVerticalGroup
import com.rbxgolden.fungamems.game.screens.main.ConverterScreen
import com.rbxgolden.fungamems.game.screens.main.DailyRewardScreen
import com.rbxgolden.fungamems.game.utils.ConverterType
import com.rbxgolden.fungamems.game.utils.GLOBAL_SELECTED_CONVERTER_TYPE
import com.rbxgolden.fungamems.game.utils.actor.setOnClickListener
import com.rbxgolden.fungamems.game.utils.actor.setOnTouchListener
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedGroup
import com.rbxgolden.fungamems.game.utils.advanced.AdvancedScreen
import com.rbxgolden.fungamems.game.utils.gdxGame
import com.rbxgolden.fungamems.game.utils.runGDX
import com.rbxgolden.fungamems.util.log
import kotlinx.coroutines.launch

class APanelMemes(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aVerticalGroup = AVerticalGroup(screen, alignH = AlignH.CENTER, wrap = true)
    private val aContentGroup  = ATmpGroup(screen)
    private val aPanelImg      = Image(gdxGame.assetsAll.MEMS)
    private val listCopyBtn    = List(5) { Actor() }
    private val listShareBtn   = List(5) { Actor() }
    private val aScrollPane    = AScrollPane(aVerticalGroup)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addScrollPane()
        setUpContentGroup()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addScrollPane() {
        add(aScrollPane) { fillParent() }
    }

    // Content Group ------------------------------------------------------------------------
    private fun setUpContentGroup() {
        val contentW = 376f
        val contentH = 840f

        aVerticalGroup.setSize(width, 1f)
        aContentGroup.setSize(contentW, contentH)

        aVerticalGroup.addActor(aContentGroup)

        val space = aScrollPane.height - contentH
        if (space > 0) aVerticalGroup.paddingBottom += space

        coroutine?.launch {
            AdSizeManager.adBottomFlow.collect {
                runGDX {
                    if (screen.adBottomUI >= 0f) aVerticalGroup.paddingBottom += screen.adBottomUI
                    log("APanelMain adBottomUI = ${screen.adBottomUI} | banner = ${screen.safeBannerUI}")
                }
            }
        }

        aContentGroup.also {
            it.addContentImg()
            it.addListCopyBtn()
            it.addListShareBtn()
        }

    }

    private fun AdvancedGroup.addContentImg() {
        addAndFillActor(aPanelImg)
    }

    private fun AdvancedGroup.addListCopyBtn() {
        var nx = 275f
        var ny = 687f
        listCopyBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 35f, 35f)

            //nx += 8f + 168f
            ny -= 129f + 35f
//            if (index.inc() % 2 == 0) {
//                nx = 16f
//                ny -= 8f + 120f
//            }

            btn.setOnTouchListener {
                val meme = MEMES[index]

                gdxGame.activity.copyMeme(
                    meme.title,
                    meme.text
                )
            }

        }
    }

    private fun AdvancedGroup.addListShareBtn() {
        var nx = 315f
        var ny = 687f
        listShareBtn.forEachIndexed { index, btn ->
            addActor(btn)
            btn.setBounds(nx, ny, 35f, 35f)

            //nx += 8f + 168f
            ny -= 129f + 35f
//            if (index.inc() % 2 == 0) {
//                nx = 16f
//                ny -= 8f + 120f
//            }

            btn.setOnTouchListener {
                val meme = MEMES[index]

                gdxGame.activity.shareMeme(
                    meme.title,
                    meme.text
                )
            }

        }
    }

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------
    data class Meme(
        val title: String,
        val text : String,
    )

    private val MEMES = listOf(

        Meme(
            title = "Server Luck",
            text =
                "That moment when you finally find a good server...and it shuts down for an update 😭😭..."
        ),

        Meme(
            title = "Robux Pain",
            text =
                "Me: “I’ll save my Robux.” Also me 5 minutes later buying a useless gamepass 💀💀💀"
        ),

        Meme(
            title = "OOF at Spawn",
            text =
                "That moment when you join the game... and immediately fall off the map 😂😂😂"
        ),

        Meme(
            title = "Tycoon Experience",
            text =
                "POV: You leave your tycoon for 2 seconds... and someone already stole your cash 😂😂"
        ),

        Meme(
            title = "Obby Skill",
            text =
                "Everyone in the obby: flying through levels 😎 Me stuck on the first jump for 20 minutes 🤡"
        ),
    )

}