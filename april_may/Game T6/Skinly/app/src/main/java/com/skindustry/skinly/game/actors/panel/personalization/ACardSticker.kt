package com.skindustry.skinly.game.actors.panel.personalization

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBox
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxGroup
import com.skindustry.skinly.game.actors.checkbox.base.ACheckBoxStyles
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.actor.animHideAndDisable
import com.skindustry.skinly.game.utils.actor.animShowAndEnable
import com.skindustry.skinly.game.utils.actor.disable
import com.skindustry.skinly.game.utils.actor.setOnTouchListener
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.gdxGame

class ACardSticker(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------------
    enum class State { OPEN, LOCKED }

    private var currentState   = State.LOCKED
    private var currentSticker: Texture? = null

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBox        = ACheckBox(screen, ACheckBoxStyles.TEXTURE_ITEM)
    private val aStickerImg = Image()
    private val aLockImg    = Image(gdxGame.assetsAll.lock_white)

    // ------------------------------------------------------------------------
    // Callbacks
    // ------------------------------------------------------------------------
    var onOpen  : ((Texture) -> Unit)? = null
    var onLocked: ((Texture) -> Unit)? = null

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBox) { fillParent() }
        aBox.disable()

        aStickerImg.setSize(60f, 60f)
        add(aStickerImg) { center() }

        aLockImg.setSize(24f, 24f)
        add(aLockImg) { endToEnd(margin = 1f); topToTop(margin = 1f) }

        setOnTouchListener {
            if (currentSticker == null) return@setOnTouchListener

            when (currentState) {
                State.OPEN   -> {
                    aBox.check()
                    onOpen?.invoke(currentSticker!!)
                }
                State.LOCKED -> onLocked?.invoke(currentSticker!!)
            }
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun setSticker(texture: Texture) {
        currentSticker = texture
        aStickerImg.drawable = TextureRegionDrawable(texture)
    }

    fun setCheckBoxGroup(cbg: ACheckBoxGroup) {
        aBox.checkBoxGroup = cbg
    }

    fun setState(state: State) {
        currentState = state
        when (state) {
            State.OPEN   -> aLockImg.animHideAndDisable()
            State.LOCKED -> aLockImg.animShowAndEnable()
        }
    }
}