package com.selftest.mindora.game.actors.test.card

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.selftest.mindora.game.actors.label.AMsdfLabel
import com.selftest.mindora.game.actors.layout.constraintLayout.AConstraintLayout
import com.selftest.mindora.game.utils.Block
import com.selftest.mindora.game.utils.GameColor
import com.selftest.mindora.game.utils.actor.animHide
import com.selftest.mindora.game.utils.actor.animShow
import com.selftest.mindora.game.utils.actor.setOnClickListener
import com.selftest.mindora.game.utils.advanced.AdvancedScreen
import com.selftest.mindora.game.utils.font.msdf.MsdfStyle
import com.selftest.mindora.game.utils.gdxGame

// ═════════════════════════════════════════════════════════════════════════════
//  ACardTest — картка одного теста в списку. 344×88.
//
//   ┌──────────────────────────────────────────────────┐
//   │  [icon]   Archetype                  ( кнопка )  │
//   │           Reveal the role that                   │
//   │           shapes your personality                │
//   └──────────────────────────────────────────────────┘
//
//  ЧОТИРИ СТАНИ — це одна вісь «наскільки далеко зайшов юзер», а не чотири
//  незалежні прапорці. Порядок фіксований і саме в такому пріоритеті
//  обчислюється в resolveState():
//
//    DONE       тест пройдено          → ic_ena + галочка, «Take Again»
//    PURCHASED  куплено, не пройдено   → ic_ena,            «Open»
//    AFFORDABLE не куплено, вистачає   → ic_ena,            ціна на жовтому
//    LOCKED     не куплено, не вистачає→ ic_dis (сіра),     ціна на сірому
//
//  ЧОМУ ІКОНКА І ФОН ПЕРЕМИКАЮТЬСЯ РАЗОМ: у макеті «сірий» — це не
//  затемнення картки, а окремий комплект текстур (card_test_dis + ic_dis_N).
//  Тому підміна йде парою, інакше сіра картка з кольоровою іконкою.
//
//  ЩО КАРТКА НЕ ВИРІШУЄ САМА: чи вистачає люменів і чи пройдено тест —
//  це стан гравця. Картка приймає готовий State ззовні (див. bind) і лише
//  малює його. Так один і той самий актор годиться і для хаба, і для
//  екрана Tests, і для дебаг-превʼю всіх станів.
// ═════════════════════════════════════════════════════════════════════════════
class ACardTest(override val screen: AdvancedScreen) : AConstraintLayout(screen) {

    companion object {
        const val W = 344f
        const val H = 88f
    }

    enum class State { LOCKED, AFFORDABLE, PURCHASED, DONE }

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val msdf = gdxGame.msdfManager

    private val styleTitle = MsdfStyle(msdf, msdf.fontMontserrat_Medium, 14f, GameColor.pink_E4D5FF)
    private val styleDesc  = MsdfStyle(msdf, msdf.fontMontserrat_Regular, 11f, GameColor.white_80)

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg    = Image(TextureRegionDrawable(gdxGame.assetsAll.card_test_ena))
    private val aIconImg  = Image(TextureRegionDrawable(gdxGame.assetsAll.listIcEna[0]))
    private val aDoneImg  = Image(TextureRegionDrawable(gdxGame.assetsAll.test_card_done))
    private val aTitleLbl = AMsdfLabel("", styleTitle)
    private val aDescLbl  = AMsdfLabel("", styleDesc)

    val aBtn = ACardTestBtn(screen)

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * Тап по картці. Слухач висить на ТІЛІ картки, а не на кнопці: у стані
     * LOCKED кнопка disabled, і тап по ній провалився б у порожнечу — а сіру
     * картку тиснути треба, щоб пояснити юзеру, чому тест закритий.
     */
    var onClick: Block = {}

    var state: State = State.LOCKED
        private set

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addCardClick()
        addBgImg()
        addIconImg()
        addTitleLbl()
        addDescLbl()
        addBtn()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addCardClick() {
        // Уся картка — одна зона натискання, у будь-якому стані.
        //
        // stopEvent = false ОБОВ'ЯЗКОВО: картки лежать у ScrollPane, а він
        // ловить драг на СОБІ, тобто отримує подію спливанням від дитини.
        // Дефолтний event.stop() обривав це спливання — кліки працювали, а
        // список не скролився. Без stop() ScrollPane бачить драг, починає
        // скрол і сам скасовує touch-focus картки, тож після протяжки
        // clicked не спрацює: потягнув список — картка не відкрилась.
        //
        // tapSquareSize лишаємо дефолтним: надто мале значення скасовувало б
        // тап від дрібного тремтіння пальця.
        touchable = Touchable.enabled
        setOnClickListener(stopEvent = false) { onClick() }
    }

    private fun addBgImg() {
        add(aBgImg) { fillParent() }
    }

    private fun addIconImg() {
        aIconImg.setSize(54f, 54f)
        add(aIconImg) { startToStart(margin = 16f); centerY() }

        aDoneImg.setSize(48f, 48f)
        add(aDoneImg) { center(aIconImg) }
        aDoneImg.color.a = 0f
    }

    private fun addTitleLbl() {
        aTitleLbl.setSize(155f, 22f)
        add(aTitleLbl) { startToStart(margin = 86f); topToTop(margin = 16f) }
        aTitleLbl.setAlignment(Align.left)
        aTitleLbl.setEllipsis(true)
    }

    private fun addDescLbl() {
        aDescLbl.setSize(155f, 32f)
        add(aDescLbl) { startToStart(margin = 86f); topToTop(margin = 42f) }
        aDescLbl.setAlignment(Align.topLeft)
        aDescLbl.setWrap(true)
    }

    private fun addBtn() {
        aBtn.setSize(ACardTestBtn.W, ACardTestBtn.H)
        add(aBtn) { endToEnd(margin = 16f); centerY() }

        // Кнопка НЕ ловить клік сама: у стані LOCKED вона disabled (сіра
        // текстура + Touchable.disabled), і тап туди провалився б у порожнечу.
        // Але сіру картку тиснути МОЖНА — юзер має отримати пояснення, чому
        // тест закритий. Тому єдиний слухач висить на тілі картки, а кнопка
        // лишається чисто візуальною.
        aBtn.touchable = Touchable.disabled
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    /**
     * @param iconIndex 0..4 — позиція теста в TestRepository.ALL; вона ж
     *        визначає пару ic_ena_N / ic_dis_N, тому окремої мапи немає.
     * @param animated false при першому біндінгу: стани мають стати на місце
     *        до появи картки, інакше видно, як галочка «прилітає».
     */
    fun bind(
        title    : String,
        desc     : String,
        iconIndex: Int,
        price    : Long,
        state    : State,
        animated : Boolean = true,
    ) {
        this.state = state

        aTitleLbl.setText(title)
        aDescLbl.setText(desc)

        val isDim = state == State.LOCKED

        aBgImg.drawable = TextureRegionDrawable(
            if (isDim) gdxGame.assetsAll.card_test_dis else gdxGame.assetsAll.card_test_ena
        )
        aIconImg.drawable = TextureRegionDrawable(
            if (isDim) gdxGame.assetsAll.listIcDis[iconIndex] else gdxGame.assetsAll.listIcEna[iconIndex]
        )

        // Заголовок у сірій картці теж гасне — інакше білий текст на сірому
        // виглядає активнішим за саму картку.
        aTitleLbl.setTextColor(if (isDim) GameColor.white_70 else Color.WHITE)

        val t = if (animated) 0.15f else 0f
        if (state == State.DONE) aDoneImg.animShow(t) else aDoneImg.animHide(t)

        aBtn.bind(state, price)
    }

    /**
     * Єдине місце, де стан ВИВОДИТЬСЯ з даних гравця. Порядок перевірок
     * важливий: пройдений тест лишається пройденим, навіть якщо люменів
     * зараз нуль, а куплений — купленим.
     */
    fun resolveState(isDone: Boolean, isPurchased: Boolean, balance: Long, price: Long): State = when {
        isDone           -> State.DONE
        isPurchased      -> State.PURCHASED
        balance >= price -> State.AFFORDABLE
        else             -> State.LOCKED
    }
}