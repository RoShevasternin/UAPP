package com.selftest.mindora.game.actors.result

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ─────────────────────────────────────────────────────────────────────────────
//  Блок «At your best:» / «You grow when:».
//
//  ІКОНКА І ЗАГОЛОВОК ЗАПЕЧЕНІ В 9-PATCH (panel_best / panel_grow, 310×42
//  у нерозтягнутому стані). Актор малює тільки ТІЛО — рядок з JSON, який
//  у різних результатах від трьох слів до півтора рядків.
//
//  Тому висота плаваюча: 9-patch тягнеться вниз, а не масштабується, тож
//  зірочка і напис лишаються того самого розміру при будь-якому тексті.
//
//  Два блоки в кожному результаті — і в одиночній картці, і в кожній з
//  п'яти карток Big Five. Різниця тільки в текстурі, тому вона параметр.
// ─────────────────────────────────────────────────────────────────────────────
class APanelResultBlock(
    override val screen: AdvancedScreen,
    private val patch  : NinePatch,
) : AConstraintLayout(screen) {

    companion object {
        /** Мінімальна висота 9-patch: зона із запеченою іконкою і заголовком. */
        const val TITLE_ZONE = 42f

        private const val PAD_H      = 16f
        private const val PAD_BOTTOM = 12f
        private const val GAP_TITLE  = 2f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleBody = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, Color.WHITE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg   = Image(patch)
    private val aBodyLbl = AMsdfLabel("", styleBody)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        addActor(aBodyLbl)
        aBodyLbl.setAlignment(Align.topLeft)
        aBodyLbl.setWrap(true)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * @return власна висота після підстановки тексту — батько по ній знає,
     *         куди класти наступний блок.
     */
    fun setBody(text: String): Float {
        val contentW = width - PAD_H * 2

        aBodyLbl.setText(text)
        // prefHeight рахується ТІЛЬКИ при заданій ширині: wrap рахує переноси,
        // а не сам текст. Тому спершу ширина, потім вимір, потім висота.
        aBodyLbl.setSize(contentW, 1f)
        aBodyLbl.setSize(contentW, aBodyLbl.prefHeight)

        val h = TITLE_ZONE + GAP_TITLE + aBodyLbl.height + PAD_BOTTOM
        setSize(width, h)

        aBodyLbl.setPosition(PAD_H, PAD_BOTTOM)
        return h
    }
}