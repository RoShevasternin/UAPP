package com.coinsclub.funrbx.game.actors.panel.quiz

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.coinsclub.funrbx.game.actors.layout.constraintLayout.AConstraintLayout
import com.coinsclub.funrbx.game.utils.TextureEmpty
import com.coinsclub.funrbx.game.utils.advanced.AdvancedScreen
import com.coinsclub.funrbx.game.utils.gdxGame

class APanelProgressQuiz(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    private val aBgImg       = Image(gdxGame.assetsAll.QUIZ_PANEL)
    private val listStateImg = List(5) { Image() }          // кружечки 1..5
    private val listDotImg   = List(4) { Image() }          // конектори між ними (їх 4, не 5)

    // ------------------------------------------------------------------------
    // Assets (стани)
    // ------------------------------------------------------------------------
    private val stateLock    = TextureEmpty //gdxGame.assetsAll.quiz_lock      // замок
    private val stateCorrect = gdxGame.assetsAll.QUIZ_TRUE      // зелена галочка
    private val stateWrong   = gdxGame.assetsAll.QUIZ_FALSE     // червоний хрест

    private val dotGray  = TextureEmpty // gdxGame.assetsAll.quiz_dot_green
    private val dotGreen = gdxGame.assetsAll.QUIZ_CIRCLE_TRUE
    private val dotRed   = gdxGame.assetsAll.QUIZ_CIRCLE_FALSE

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addBgImg()
        addListStateImg()
        addListDotImg()
        reset()
    }

    private fun addBgImg() {
        add(aBgImg) { fillParent() }
    }

    private fun addListStateImg() {
        var nx = 0f
        listStateImg.forEach { img ->
            addActor(img)
            img.setBounds(nx, 17f, 32f, 32f)
            nx += 46f + 32f
        }
    }

    private fun addListDotImg() {
        var nx = 51f
        listDotImg.forEach { img ->          // 4 крапки між 5 кружечками
            addActor(img)
            img.setBounds(nx, 29f, 8f, 8f)
            nx += 70f + 8f
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------
    fun reset() {
        listStateImg.forEach { it.drawable = TextureRegionDrawable(stateLock) }
        listDotImg.forEach   { it.drawable = TextureRegionDrawable(dotGray) }
    }

    // позначити результат питання index (0-based)
    fun setResult(index: Int, correct: Boolean) {
        if (index !in listStateImg.indices) return

        listStateImg[index].drawable =
            TextureRegionDrawable(if (correct) stateCorrect else stateWrong)

        // крапка ПІСЛЯ цього кружечка (конектор до наступного) фарбується в колір результату
        if (index in listDotImg.indices) {
            listDotImg[index].drawable =
                TextureRegionDrawable(if (correct) dotGreen else dotRed)
        }
    }
}