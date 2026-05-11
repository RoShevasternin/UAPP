package com.rbuxrds.counter.game.actors.panel

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.rbuxrds.counter.game.utils.actor.addActors
import com.rbuxrds.counter.game.utils.actor.addAndFillActor
import com.rbuxrds.counter.game.utils.actor.setOnClickListener
import com.rbuxrds.counter.game.utils.advanced.AdvancedGroup
import com.rbuxrds.counter.game.utils.advanced.AdvancedScreen
import com.rbuxrds.counter.game.utils.gdxGame

class APanelMemesForFun(override val screen: AdvancedScreen) : AdvancedGroup() {

    data class Meme(
        val title: String,
        val text: String
    )

    private val memes = listOf(
        Meme(
            "OOF at Spawn",
            "That moment when you join the game... and immediately fall off the map"
        ),
        Meme(
            "Lag Spike Doom",
            "That moment when the game freezes... and you’re already eliminated"
        ),
        Meme(
            "Clutch Fail Mode",
            "That moment when it’s 1v1... and you miss the easiest shot"
        )
    )

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aPanelImg = Image(gdxGame.assetsAll.PANEL_MEMES_FOR_FUN)

    private val listCopyBtn  = List(3) { Actor() }
    private val listShareBtn = List(3) { Actor() }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aPanelImg)

        var ny = 236f

        listCopyBtn.zip(listShareBtn).forEachIndexed { index, (copy, share) ->
            val meme = memes[index]

            addActors(copy, share)
            copy.setBounds(259f, ny, 43f, 43f)
            share.setBounds(299f, ny, 43f, 43f)

            ny -= 63f + 43f

            copy.setOnClickListener {
                copyMeme(meme)
                gdxGame.activity.showCopiedMemeToast()
            }
            share.setOnClickListener {
                gdxGame.activity.shareMeme(meme)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------

    private fun copyMeme(meme: Meme) {
        val text = "${meme.title}\n\n${meme.text}"
        Gdx.app.clipboard.contents = text
    }

}