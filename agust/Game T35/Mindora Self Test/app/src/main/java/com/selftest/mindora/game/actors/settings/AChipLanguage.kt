package com.selftest.mindora.game.actors.settings

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.actors.ui.ARoundRect
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  AChipLanguage — фіолетова таблетка з поточною мовою в рядку Language.
//
//  МАЛЮЄТЬСЯ, А НЕ БЕРЕТЬСЯ З АТЛАСА: у мов різна довжина назви («English»,
//  «Українська», «Português»), тож ширина чіпа плаває. Текстура-таблетка або
//  розтягувалась би й псувала скруглення, або вимагала 9-patch на кожен
//  варіант. ARoundRect бере радіус від власної висоти й лишається круглим за
//  будь-якої ширини.
// ═════════════════════════════════════════════════════════════════════════════
class AChipLanguage(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        const val H = 32f

        /** Поля з боків від тексту — з них рахується ширина. */
        private const val PAD = 16f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleName = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 13f, Color.WHITE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgRect = ARoundRect(screen)
    private val aNameLbl = AMsdfLabel("", styleName)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        aBgRect.radius      = height / 2f
        aBgRect.color       = GameColor.purple_6A2BD9
        aBgRect.fillAlpha   = 1f
        aBgRect.strokeWidth = 0f
        add(aBgRect) { fillParent() }

        add(aNameLbl) { fillParent() }
        aNameLbl.setAlignment(Align.center)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /** @return ширина, яку чіп зайняв — рядку треба знати її для розкладки. */
    fun setLanguage(name: String): Float {
        aNameLbl.setText(name)

        val w = aNameLbl.prefWidth + PAD * 2
        setSize(w, H)
        return w
    }
}
