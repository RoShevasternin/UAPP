package com.skindustry.skinly.game.actors.popup

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.skindustry.skinly.game.actors.button.ATextButtonAnimTexture
import com.skindustry.skinly.game.actors.button.base.AButtonAnimTexture
import com.skindustry.skinly.game.actors.coin.ACoinIcon
import com.skindustry.skinly.game.actors.layout.constraintLayout.AConstraintLayout
import com.skindustry.skinly.game.utils.GameColor
import com.skindustry.skinly.game.utils.actor.disable
import com.skindustry.skinly.game.utils.actor.setOnClickListener
import com.skindustry.skinly.game.utils.advanced.AdvancedScreen
import com.skindustry.skinly.game.utils.font.FontFactory
import com.skindustry.skinly.game.utils.font.FontParameter
import com.skindustry.skinly.game.utils.gdxGame

// ═══════════════════════════════════════════════════════════════════════════
// Попап розблокування: «Watch Ad» (як було) або «Unlock for N» за монети.
//
// Картинка POPUP_UNLOCK (344×334) має намальовані «Watch Ad» і «Cancel».
// Попап подовжено вниз на EXTRA: картинка стоїть угорі, кнопка монет лягає
// рівно на намальований Cancel і закриває його, а Cancel переїжджає на білу
// смугу знизу. Під картинкою — біла закруглена підкладка, щоб закруглені нижні
// кути PNG не лишали прозорих «вирізів» на стику.
// ═══════════════════════════════════════════════════════════════════════════

class APopupUnlock(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBottomBgImg = Image(screen.drawerUtil.getRoundedRegion(WIDTH.toInt(), (EXTRA + 2 * RADIUS).toInt(), RADIUS.toInt(), Color.WHITE))
    private val aPopupImg    = Image(gdxGame.assetsAll.POPUP_UNLOCK)
    private val aWatchBtn    = Actor()
    private val aCoinsBtn    = ATextButtonAnimTexture(
        screen    = screen,
        text      = "",
        color     = Color.BLACK,
        parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(16),
        generator = screen.fontGenerator_SemiBold,
        style     = AButtonAnimTexture.Style(
            default = TextureRegionDrawable(screen.drawerUtil.getRoundedRegion(312, 52, 26, GameColor.gray_F2F2F2)),
        ),
    )
    private val aCoinIcon    = ACoinIcon(screen, 24)
    private val aCancelLbl   = Label("Cancel", FontFactory.create(
        screen, FontParameter().setCharacters(FontParameter.CharType.LATIN).setSize(18), screen.fontGenerator_Medium, GameColor.gray_B8B8B8))

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onWatch  = {}
    var onCoins  = {}
    var onCancel = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addActor(aBottomBgImg)
        aBottomBgImg.setBounds(0f, 0f, WIDTH, EXTRA + 2 * RADIUS)

        addActor(aPopupImg)
        aPopupImg.setBounds(0f, EXTRA, WIDTH, IMG_HEIGHT)

        addWatchBtn()
        addCoinsBtn()
        addCancelBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------

    private fun addWatchBtn() {
        addActor(aWatchBtn)
        aWatchBtn.setBounds(16f, EXTRA + 64f, 312f, 56f)

        aWatchBtn.setOnClickListener(null) {
            gdxGame.soundUtil.apply { play(UNLOCK) }
            onWatch()
        }
    }

    // Лягає на намальований у PNG Cancel (EXTRA+7 .. EXTRA+49) і повністю його закриває
    private fun addCoinsBtn() {
        addActor(aCoinsBtn)
        aCoinsBtn.setBounds(16f, EXTRA + 2f, 312f, 52f)

        aCoinsBtn.addActor(aCoinIcon)
        aCoinIcon.setBounds(16f, 14f, 24f, 24f)
        aCoinIcon.disable()

        aCoinsBtn.setOnClickListener { onCoins() }
    }

    private fun addCancelBtn() {
        addActor(aCancelLbl)
        aCancelLbl.setBounds(56f, 10f, 232f, 42f)
        aCancelLbl.setAlignment(Align.center)

        aCancelLbl.setOnClickListener { onCancel() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Ціна розблокування за монети — з Econ, тож підпис і списання однакові. */
    fun setPrice(price: Int) {
        aCoinsBtn.label.setText(if (price > 0) "Unlock for $price coins" else "Unlock for free")
    }

    companion object {
        const val WIDTH      = 344f
        const val IMG_HEIGHT = 334f
        const val EXTRA      = 64f
        const val HEIGHT     = IMG_HEIGHT + EXTRA

        private const val RADIUS = 24f
    }

}
