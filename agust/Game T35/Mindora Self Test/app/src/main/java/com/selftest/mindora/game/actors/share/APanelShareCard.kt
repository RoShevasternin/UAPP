package com.selftest.mindora.game.actors.share

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  APanelShareCard — квадратна картка 1:1, яку юзер відправляє друзям.
//
//   My PERSONAL PORTRAIT      ← kicker
//   The Layered Original      ← name
//   There is more to you…     ← tagline, жовтим курсивом
//   [ арт ]
//   Mindora · Self Test       ← ЛОГО ВЖЕ У ФОНІ share.png, тут його немає
//
//  ЦЕ НЕ ЕКРАННИЙ АКТОР. Він ніколи не потрапляє на stage екрана: живе в
//  окремій сцені ShareCardRenderer і малюється в FBO. Тому й розмір у нього
//  фіксований (SIZE×SIZE), а не тягнеться під вьюпорт.
//
//  «YOUR» → «MY». Ті самі рядки на екрані звучать як «Your Archetype» — це
//  застосунок говорить до юзера. У шері говорить сам юзер до друзів, тому
//  kicker перевертається на «My Archetype». Одна заміна тут замість другого
//  комплекту рядків у JSON.
//
//  РОЗКЛАДКА ВРУЧНУ, а не констрейнтами: картка не резюмується і не
//  переливається, всі числа відомі наперед, а арт міняє пропорції між
//  квадратним (портрет) і широким (результат теста).
// ═════════════════════════════════════════════════════════════════════════════
class APanelShareCard(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        /** Сторона картки у world-юнітах. Квадрат — 1:1 для соцмереж. */
        const val SIZE = 376f

        private const val SIDE    = 26f
        private const val CONTENT = SIZE - SIDE * 2

        // ── Вертикаль, від ВЕРХУ картки ──────────────────────────────────────
        private const val KICKER_TOP  = 28f
        private const val KICKER_H    = 18f
        private const val KICKER_SIZE = 14f

        private const val NAME_TOP  = 48f
        private const val NAME_H    = 38f
        private const val NAME_SIZE = 30f

        private const val TAG_TOP  = 86f
        private const val TAG_H    = 20f
        private const val TAG_SIZE = 15f

        /** Нижче ART_TOP + ART_H починається лого, вшите у фон. */
        private const val ART_TOP = 110f
        private const val ART_H   = 158f

        private val YOUR = Regex("^your\\b", RegexOption.IGNORE_CASE)
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleKicker  = MsdfStyle(msdf, msdf.fontMontserrat_Regular, KICKER_SIZE, GameColor.white_80)
    private val styleName    = MsdfStyle(msdf, msdf.fontMontserrat_Bold, NAME_SIZE, Color.WHITE)
    private val styleTagline = MsdfStyle(msdf, msdf.fontMontserrat_Italic, TAG_SIZE, GameColor.yellow_FFD98A)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg = Image(gdxGame.assetsAll.SHARE)

    private val aKickerLbl  = AMsdfLabel("", styleKicker)
    private val aNameLbl    = AMsdfLabel("", styleName)
    private val aTaglineLbl = AMsdfLabel("", styleTagline)

    private val aArtImg = Image()

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActor(aBgImg)

        addActor(aKickerLbl);  aKickerLbl.setAlignment(Align.center)
        addActor(aNameLbl);    aNameLbl.setAlignment(Align.center)
        addActor(aTaglineLbl); aTaglineLbl.setAlignment(Align.center)
        addActor(aArtImg)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * @param kicker   рядок над назвою — «Your Archetype» або header синтезу.
     *                 Перевертається на «My …» тут, кличучому знати не треба.
     * @param artRatio ширина/висота арту. 1f — квадратний портретний арт,
     *                 310/200 — картинка результату теста.
     */
    fun bind(
        kicker  : String,
        name    : String,
        tagline : String,
        art     : Drawable,
        artRatio: Float,
    ) {
        aKickerLbl.setText(kicker.replaceFirst(YOUR, "My"))
        aNameLbl.setText(name)
        aTaglineLbl.setText(tagline)
        aArtImg.drawable = art

        layoutCard(artRatio)
    }

    // ------------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------------
    private fun layoutCard(artRatio: Float) {
        place(aKickerLbl, KICKER_TOP, KICKER_H, KICKER_SIZE)
        place(aNameLbl, NAME_TOP, NAME_H, NAME_SIZE)
        place(aTaglineLbl, TAG_TOP, TAG_H, TAG_SIZE)

        // Висота арту фіксована — вона впирається в лого знизу. Ширина йде
        // від пропорції, але не ширша за контент.
        val artW = (ART_H * artRatio).coerceAtMost(CONTENT)
        val artH = artW / artRatio

        aArtImg.setSize(artW, artH)
        aArtImg.setPosition((SIZE - artW) / 2f, SIZE - ART_TOP - artH)
    }

    /**
     * Лейбл на всю ширину контенту, вирівняний по центру.
     *
     * Довга назва («The Layered Original») в один рядок не влазить, а
     * переносити її не можна — під текстом одразу арт, зайвий рядок наїхав би
     * на нього. Тому замість переносу зменшуємо кегль рівно настільки,
     * наскільки не вистачило місця.
     */
    private fun place(lbl: AMsdfLabel, top: Float, h: Float, size: Float) {
        lbl.worldSize = size

        val w = lbl.prefWidth
        if (w > CONTENT && w > 0f) lbl.worldSize = size * (CONTENT / w)

        lbl.setSize(CONTENT, h)
        lbl.setPosition(SIDE, SIZE - top - h)
    }
}
