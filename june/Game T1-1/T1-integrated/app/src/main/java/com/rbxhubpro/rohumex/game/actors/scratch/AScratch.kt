package com.rbxhubpro.rohumex.game.actors.scratch

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.rbxhubpro.rohumex.businesModule.economy.Econ
import com.rbxhubpro.rohumex.game.actors.label.ALabel
import com.rbxhubpro.rohumex.game.utils.GameColor
import com.rbxhubpro.rohumex.game.utils.actor.addAndFillActors
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedGroup
import com.rbxhubpro.rohumex.game.utils.advanced.AdvancedScreen
import com.rbxhubpro.rohumex.game.utils.font.FontParameter
import com.rbxhubpro.rohumex.game.utils.gdxGame

class AScratch(override val screen: AdvancedScreen) : AdvancedGroup() {

    // ------------------------------------------------------------------------
    // Font
    // ------------------------------------------------------------------------
    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(64)

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private var randomResult = Result.entries.random()
        set(value) {
            // payout, а не имя enum: подпись карточки и начисление обязаны быть
            // ОДНИМ числом (см. payout ниже). Имя enum ещё и печатается как
            // «_50» — подчёркивание в шрифте NUMBERS отсутствует.
            aResultLbl.setText(payout(value).toString())
            field = value
        }

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aResultImg   = Image(gdxGame.assetsAll.PANEL_SCRATCH_RESULT)
    private val aResultLbl   = ALabel(screen, payout(randomResult).toString(), GameColor.blue_335FFF, parameter, screen.fontGenerator_InterTight_Bold)
    private val aScratchCard = AScratchCard(screen, TextureRegionDrawable(gdxGame.assetsAll.PANEL_SCRATCH))

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onResult: (result: Result) -> Unit = {}

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    // правка 5: onScratched стреляет на КАЖДУЮ новую стёртую клетку, то есть
    // после 95% продолжал бы сыпать onResult на каждое движение пальца. Пока
    // результат только рисовался — это было безвредно; теперь он начисляет
    // монеты (ScratchScreen → Wallet), поэтому результат отдаём один раз на
    // карточку, гейт сбрасывается новой карточкой.
    private var resultFired = false

    override fun addActorsOnGroup() {
        addAndFillActors(aResultImg, aResultLbl, aScratchCard)
        aResultLbl.setAlignment(Align.center)
        aScratchCard.onScratched = { percent ->
            if (percent > 95 && !resultFired) {
                resultFired = true
                onResult(randomResult)
            }
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    fun regenerateScratch() {
        aScratchCard.reset()
        resultFired = false
        randomResult = Result.entries.random()
    }

    // ВОЗВРАЩЕНО (правка 5, было потеряно при рефакторинге): номиналы карточек
    // приезжают СПИСКОМ из конфига — economy.rewards_list.scratch, порядок =
    // порядок enum Result. Одна точка на ОБА применения — что нарисовано на
    // карточке и что упало в кошелёк: два разных источника числа = игрок видит
    // 500, получает 50. Читаем на каждый вызов — конфиг подъезжает асинхронно.
    fun payout(result: Result): Int =
        Econ.rewardList("scratch", DEFAULT_SUMS).getOrElse(result.ordinal) { result.sum }

    enum class Result(val sum: Int) {
        _50  (50),
        _100 (100),
        _150 (150),
        _200 (200),
        _250 (250),
        _300 (300),
        _350 (350),
        _400 (400),
        _450 (450),
        _500 (500),
        _1000(1000),
        _1500(1500),
    }

    companion object {
        // Дефолт = зашитые номиналы, в порядке Result. Он же фолбэк, если блока
        // rewards_list нет или длина в конфиге разъехалась.
        private val DEFAULT_SUMS =
            intArrayOf(50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 1000, 1500)
    }

}