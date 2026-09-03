package com.selftest.mindora.game.actors.test

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.checkbox.base.ACheckBox
import com.selftest.mindora.game.actors.checkbox.base.ACheckBoxGroup
import com.selftest.mindora.game.actors.checkbox.base.ACheckBoxStyles
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ─────────────────────────────────────────────────────────────────────────────
// Варіант відповіді (Figma: «Card / Radio option», 344×64).
//
//   [ (○)  Текст варіанта до двох рядків              ]
//
// RADIO, А НЕ CHECKBOX: варіанти взаємовиключні, тож усі айтеми питання
// мусять сидіти в одному ACheckBoxGroup (attachTo). Без групи ACheckBoxBase
// працює в режимі toggle — тапнув два варіанти, обидва лишились
// підсвіченими, і питання виглядає як multi-select.
// ─────────────────────────────────────────────────────────────────────────────
class AItemTestOption(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleText = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 14f, Color.WHITE)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBg      = ACheckBox(screen, ACheckBoxStyles.TEST_OPTION)
    private val aTextLbl = AMsdfLabel("", styleText)

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    var onPick: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBg()
        addTextLbl()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addBg() {
        add(aBg) { fillParent() }
        aBg.setOnCheckListener { if (it) onPick() }
    }

    private fun addTextLbl() {
        aTextLbl.setSize(270f, 34f)
        add(aTextLbl) { startToStart(margin = 54f); centerY() }
        aTextLbl.setAlignment(Align.left)
        aTextLbl.setWrap(true)

        // ⚠️ Лейбл лежить ПОВЕРХ чекбокса, а в scene2d будь-який Actor за
        // замовчуванням ловить hit. Без цього рядка тап по тексту варіанта
        // не робив би нічого: подія діставалась лейблу, а слухач висить на
        // aBg — сусіді, не предку, тож спливання його не дістає.
        aTextLbl.touchable = Touchable.disabled
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun setText(text: String) {
        aTextLbl.setText(text)
    }

    /** Підключити до групи взаємовиключення. Зветься один раз при створенні. */
    fun attachTo(group: ACheckBoxGroup) {
        aBg.checkBoxGroup = group
    }

    /**
     * Вибрати ТИХО — намалювати стан без onPick. Для відновлення попередньої
     * відповіді, коли юзер повернувся назад по питаннях.
     */
    fun selectSilently(group: ACheckBoxGroup) {
        group.select(aBg, invokeBlock = false)
    }
}