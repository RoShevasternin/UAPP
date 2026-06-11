package com.skindustry.skinly.game.actors.panel.homeSelect

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.actor.animHideAndDisable
import com.skindustry.skinly.game.utils.actor.animShowAndEnable
import com.skindustry.skinly.game.utils.actor.setOnTouchListener
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class ACardCharacter(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    enum class State { OPEN, LOCKED }

    private var currentState = State.LOCKED

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg        = Image(gdxGame.assetsAll.MINI_CARD)
    private val aCharacterImg = Image()
    private val aLockImg      = Image(gdxGame.assetsAll.lock)

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onOpen  : (() -> Unit)? = null
    var onLocked: (() -> Unit)? = null

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg)        { fillParent() }
        add(aCharacterImg) { fillParent() }

        aLockImg.setSize(24f, 24f)
        add(aLockImg) { endToEnd(margin = 12f); topToTop(margin = 12f) }

        setOnTouchListener {
            when (currentState) {
                State.OPEN   -> onOpen?.invoke()
                State.LOCKED -> onLocked?.invoke()
            }
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setCharacter(texture: Texture) {
        aCharacterImg.drawable = TextureRegionDrawable(texture)
    }

    fun setState(state: State) {
        currentState = state
        when (state) {
            State.OPEN   -> aLockImg.animHideAndDisable()
            State.LOCKED -> aLockImg.animShowAndEnable()
        }
    }
}