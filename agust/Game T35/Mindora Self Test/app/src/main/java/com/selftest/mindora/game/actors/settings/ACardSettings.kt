package com.selftest.mindora.game.actors.settings

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ACardSettings — рядок налаштувань. 344×64.
//
//   ┌────────────────────────────────────────────┐
//   │  Language                      ( English ) │
//   └────────────────────────────────────────────┘
//
//  ОДИН КЛАС НА ВСІ ТРИ РЯДКИ: у макеті вони відрізняються лише тим, що
//  стоїть справа — чіп мови, тумблер або шеврон. Різні класи означали б три
//  копії однакового фону, підпису й зони натискання.
//
//  ⚠️ ПРАВИЙ ЕЛЕМЕНТ НЕ КЛІКАБЕЛЬНИЙ, і це навмисно. Тумблер, у який вбудували
//  б власний слухач, ловив би тап ПЕРШИМ, а тап по решті рядка — ні; вийшло б
//  два різні результати на одну картку. Тому клікає вся картка, а справа
//  лежить чиста картинка стану.
// ═════════════════════════════════════════════════════════════════════════════
class ACardSettings(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        const val W = 344f
        const val H = 64f

        private const val PAD_TITLE = 20f
        private const val PAD_RIGHT = 16f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleTitle = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 16f, Color.WHITE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(gdxGame.assetsAll.settings_card)
    private val aTitleLbl = AMsdfLabel("", styleTitle)

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    var onClick: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        aTitleLbl.setSize(W - PAD_TITLE * 2, 22f)
        add(aTitleLbl) { startToStart(margin = PAD_TITLE); centerY() }
        aTitleLbl.setAlignment(Align.left)
        aTitleLbl.setEllipsis(true)

        touchable = Touchable.enabled
        setOnClickListener { onClick() }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setTitle(title: String) {
        aTitleLbl.setText(title)
    }

    /** Правий елемент: чіп мови, тумблер або шеврон. Див. шапку — без слухача. */
    fun setRight(actor: Actor, w: Float, h: Float) {
        actor.setSize(w, h)
        actor.touchable = Touchable.disabled
        add(actor) { endToEnd(margin = PAD_RIGHT); centerY() }
    }
}
