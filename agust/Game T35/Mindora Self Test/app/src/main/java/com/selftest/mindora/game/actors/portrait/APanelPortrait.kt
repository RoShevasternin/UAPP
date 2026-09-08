package com.selftest.mindora.game.actors.portrait

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.button.AMainButton
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.content.TestRepository
import com.selftest.mindora.game.controller.PortraitController
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  APanelPortrait — верхній блок екрана «Your Portrait».
//
//   Unlock at least N out of M tests to unlock your portrait
//   ⬡──⬡──⬡──⬡──⬡        ← 5 іконок, з'єднані лініями
//   [ арт портрета, із замком поки закрито ]
//   ( Unlock My Portrait )
//
//  ЛІНІЇ МАЛЮЮТЬСЯ КОДОМ, а не текстурою: сегмент між двома іконками має
//  колір «пройденої» пари тільки якщо ОБИДВІ пройдені. Текстурою це були б
//  2^4 варіантів, кодом — один if.
//
//  ПОРІГ — З КОНФІГУ. Панель ніде не порівнює лічильники сама: усе рішення
//  «чи можна відкрити» вже спаковане в state.canSynthesize, а він рахується
//  від portrait_synthesis_threshold. Постав менеджер 2 — працюватиме 2, і
//  жодного рядка тут міняти не треба.
// ═════════════════════════════════════════════════════════════════════════════
class APanelPortrait(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        const val W = 344f
        const val H = 334f          // 344×334 Hug з макета

        // ── Розміри зняті з макета, не «на око» ──────────────────────────────
        private const val SIDE      = 18f    // бічний відступ арту і кнопки
        private const val ART_TOP   = 45f
        private const val ART_H     = 207f

        private const val ICON      = 25f
        /** Центр ряду іконок від верху панелі. */
        private const val ICON_MID  = 75f
        /** Крайні іконки відступають більше за арт — вони всередині нього. */
        private const val ICON_SIDE = 35.5f

        private const val LOCK_W    = 46f
        private const val LOCK_H    = 58f
        /** Центр замка від верху панелі. */
        private const val LOCK_MID  = 166f

        private const val BTN_H     = 48f
        private const val BTN_BOT   = 21f

        private const val HINT_TOP  = 22f

        /** Колір лінії між двома ще НЕ пройденими вимірами. */
        private val LINE_LOCKED = Color.valueOf("26006A")

        /** Між двома пройденими — світла. */
        private val LINE_OPEN   = Color.valueOf("D7C1FF")

        private const val LINE_H = 1.5f

        /** Портрет ще не зібраний. */
        private const val TEXT_UNLOCK = "Unlock My Portrait"

        /** Зібраний: та сама кнопка веде переглянути свої сторони ще раз. */
        private const val TEXT_VIEW   = "View My Portrait"
    }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleHint = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 12f, GameColor.white_80)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg   = Image(gdxGame.assetsAll.panel_result)
    private val aHintLbl = AMsdfLabel("", styleHint)

    private val aArtImg  = Image(gdxGame.assetsAll.PANEL_YOUR_PRE_PORTRAIT)
    private val aLockImg = Image(gdxGame.assetsAll.lock)

    private val aIcons = List(TestRepository.ALL.size) { Image() }

    private val aUnlockBtn = AMainButton(screen, TEXT_UNLOCK)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    // Які виміри пройдені — потрібно в draw для кольору ліній. Ліній на один
    // менше, ніж іконок, тож зберігаємо стан іконок, а не сегментів.
    private var doneFlags = BooleanArray(TestRepository.ALL.size)

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    var onUnlock: Block = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        add(aBgImg) { fillParent() }

        aHintLbl.setSize(W - SIDE * 2, 20f)
        add(aHintLbl) { centerX(); topToTop(margin = HINT_TOP) }
        aHintLbl.setAlignment(Align.center)
        aHintLbl.setWrap(true)

        addArt()
        addIcons()

        aUnlockBtn.setSize(W - SIDE * 2, BTN_H)
        add(aUnlockBtn) { centerX(); bottomToBottom(margin = BTN_BOT) }
        aUnlockBtn.setOnClickListener { onUnlock() }
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addArt() {
        aArtImg.setSize(W - SIDE * 2, ART_H)
        add(aArtImg) { centerX(); topToTop(margin = ART_TOP) }

        // Замок не по центру арту, а трохи вище — за макетом він припадає
        // на обличчя, а не на геометричну середину картинки.
        aLockImg.setSize(LOCK_W, LOCK_H)
        add(aLockImg) { centerX(); topToTop(margin = LOCK_MID - LOCK_H / 2f) }
    }

    /** Іконки поверх арту — так само, як у макеті. */
    private fun addIcons() {
        aIcons.forEachIndexed { i, img ->
            img.setSize(ICON, ICON)
            add(img) { startToStart(margin = iconX(i)); topToTop(margin = ICON_MID - ICON / 2f) }
        }
    }

    // ------------------------------------------------------------------------
    // Draw
    // ------------------------------------------------------------------------
    /**
     * Лінії між іконками. Малюємо ПІСЛЯ super.draw, але вони візуально під
     * іконками, бо проходять по їхніх центрах і коротші за проміжок — заходити
     * під іконку не треба.
     *
     * Сегмент світлий лише коли пройдені ОБИДВА його кінці: лінія тут означає
     * «зв'язок між двома відкритими вимірами», а не «прогрес до наступного».
     */
    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch ?: return
        super.draw(batch, parentAlpha)

        // ⚠️ ЛІНІЇ ПІСЛЯ super.draw, і це принципово.
        //
        // Ряд іконок лежить ВСЕРЕДИНІ арту (арт 45..252 від верху, іконки на
        // 75). Намальовані до super.draw лінії просто перекривались артом і
        // не було видно нічого.
        //
        // Поверх іконок вони при цьому не лізуть: сегмент іде від правого
        // краю однієї до лівого краю наступної, під шестикутник не заходить.
        drawLines()
    }

    private fun drawLines() {
        val drawer = screen.drawerUtil.drawer

        // Координати — В СИСТЕМІ БАТЬКА, а не власній. ShapeDrawer малює тим
        // самим батчем, але сам матрицю не чіпає, а super.draw уже завершився
        // і відкотив трансформ групи. Тому до локальних відступів додаємо
        // власні x/y.
        val lineY = y + height - ICON_MID

        for (i in 0 until aIcons.size - 1) {
            val from = x + iconX(i) + ICON
            val to   = x + iconX(i + 1)

            drawer.filledRectangle(
                from,
                lineY - LINE_H / 2f,
                to - from,
                LINE_H,
                if (doneFlags[i] && doneFlags[i + 1]) LINE_OPEN else LINE_LOCKED,
            )
        }
    }

    // ------------------------------------------------------------------------
    // Logic
    // ------------------------------------------------------------------------
    /** Ліва межа i-ї іконки. Рівномірно між бічними відступами. */
    private fun iconX(i: Int): Float {
        val n   = aIcons.size
        val gap = (W - ICON_SIDE * 2 - n * ICON) / (n - 1).coerceAtLeast(1)
        return ICON_SIDE + i * (ICON + gap)
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun render(state: PortraitController.State, animated: Boolean = true) {
        val t = if (animated) 0.2f else 0f

        state.cards.forEachIndexed { i, card ->
            if (i >= aIcons.size) return@forEachIndexed
            doneFlags[i] = card.done
            val region = if (card.done) gdxGame.assetsAll.listIcEna[i] else gdxGame.assetsAll.listIcDis[i]
            aIcons[i].drawable = TextureRegionDrawable(region)
        }

        val isOpen = state.synthesis != null

        // Замок — рівно індикатор «портрет ще не зібраний».
        if (isOpen) aLockImg.animHide(t) else aLockImg.animShow(t)

        // ── КНОПКА НЕ ЗНИКАЄ НІКОЛИ ──────────────────────────────────────────
        //
        // Вона постійний вхід у портрет: доки тестів не вистачає — сіра й
        // неактивна, далі назавжди доступна і веде переглянути свої сторони
        // ще раз. Раніше після синтезу вона ховалась, і зайти в зібраний
        // портрет вдруге не було чим.
        //
        // Саме тому з панелі прибрані назва й таглайн: вони жили в цьому
        // місці, під кнопкою. Тепер їх показує сам екран портрета, куди
        // кнопка й веде.
        aUnlockBtn.label.setText(if (isOpen) TEXT_VIEW else TEXT_UNLOCK)

        // Активна у двох випадках: поріг узято (можна зібрати) або портрет
        // уже зібраний (можна переглянути). Сам поріг — у конфізі, панель
        // його не знає і знати не повинна.
        //
        // ⚠️ ПОРЯДОК ВАЖЛИВИЙ: enable/disable ПЕРЕД animShow.
        //
        // Було навпаки — і затемнення не працювало взагалі. animShow це
        // Actions.fadeIn, тобто дія, яка всі 0.2 с тягне alpha ДО 1.
        // Виставлений одразу після неї color.a = 0.5f вона просто
        // переїжджала, і неактивна кнопка світилась як активна.
        //
        // Альфою стан більше не керуємо: штатний disable() з
        // AButtonAnimTexture підмінює текстуру на сіру (btn_press) і сам
        // знімає touchable — рівно як ACardTest робить із картками.
        if (isOpen || state.canSynthesize) aUnlockBtn.enable() else aUnlockBtn.disable()
        aUnlockBtn.animShow(t)

        aHintLbl.setText(
            if (isOpen) "Your portrait is complete"
            // Текст підказки з конфігу: «at least N out of M».
            else "Unlock at least ${state.threshold} out of ${state.totalCount} tests to unlock your portrait"
        )
    }
}