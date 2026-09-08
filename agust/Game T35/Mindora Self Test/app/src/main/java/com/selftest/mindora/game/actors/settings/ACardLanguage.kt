package com.selftest.mindora.game.actors.settings

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.checkbox.base.ACheckBoxBase
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.content.LanguageCatalog
import com.selftest.mindora.game.utils.actor.addAndFillActors
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ACardLanguage — рядок однієї мови. 344×72.
//
//   ┌────────────────────────────────────┐
//   │ 🇺🇸  English                    (◉) │
//   └────────────────────────────────────┘
//
//  РЯДОК САМ Є ЧЕКБОКСОМ, а не містить його. Спадкування від ACheckBoxBase
//  дає одразу все, що інакше довелося б писати руками:
//    • ACheckBoxGroup — радіо-поведінка, попередній сам знімається;
//    • слухач, сумісний зі ScrollPane (не краде драг у списку);
//    • звук CHECK_BOX на реальному кліку, який поважає Sound Effect.
//
//  РАДІО-КРУЖОК НЕ МАЛЮЄТЬСЯ ОКРЕМО: він уже намальований у фонах lang_def і
//  lang_check, тож перемикання стану — це підміна одного фону на інший, а не
//  два незалежні актори, які треба тримати в синхроні.
// ═════════════════════════════════════════════════════════════════════════════
class ACardLanguage(override val screen: AdvancedScreen) : ACheckBoxBase(screen) {

    companion object {
        const val W = 344f

        /** 344×72 — з регіону lang_def (1032×216, атлас спакований 3×). */
        const val H = 72f

        private const val FLAG_X = 16f
        private const val FLAG_W = 26f

        /** Прапори 96×114 — тримаємо пропорцію, інакше вони сплющуються. */
        private const val FLAG_H = FLAG_W * 114f / 96f

        private const val NAME_X = 54f
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleName = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 15f, Color.WHITE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgDefImg   = Image(gdxGame.assetsAll.lang_def)
    private val aBgCheckImg = Image(gdxGame.assetsAll.lang_check)

    private val aFlagImg = Image()
    private val aNameLbl = AMsdfLabel("", styleName)

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addAndFillActors(listOf(aBgDefImg, aBgCheckImg))
        aBgCheckImg.isVisible = false

        aFlagImg.setSize(FLAG_W, FLAG_H)
        aFlagImg.setPosition(FLAG_X, (H - FLAG_H) / 2f)
        addActor(aFlagImg)

        aNameLbl.setSize(W - NAME_X - 60f, 22f)
        aNameLbl.setPosition(NAME_X, (H - 22f) / 2f)
        addActor(aNameLbl)
        aNameLbl.setAlignment(Align.left)
        aNameLbl.setEllipsis(true)

        // Слухач і collect — у базі, тому super ОБОВ'ЯЗКОВО і саме в кінці:
        // до нього актори ще не додані, а слухач має лежати поверх усіх.
        super.addActorsOnGroup()
    }

    // ------------------------------------------------------------------------
    // Check
    // ------------------------------------------------------------------------
    override fun onChecked() {
        aBgDefImg.isVisible   = false
        aBgCheckImg.isVisible = true
    }

    override fun onUnchecked() {
        aBgDefImg.isVisible   = true
        aBgCheckImg.isVisible = false
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /** @param index позиція в LanguageCatalog.ALL — вона ж номер прапора. */
    fun bind(index: Int, entry: LanguageCatalog.Entry) {
        val flags = gdxGame.assetsAll.listLangFlag
        aFlagImg.drawable = TextureRegionDrawable(flags[index.coerceIn(0, flags.lastIndex)])
        aNameLbl.setText(entry.name)
    }
}
