package com.racing.funtols.game.actors.panel.plate

import com.badlogic.gdx.math.Interpolation
import com.racing.funtols.game.actors.layout.constraintLayout.AConstraintLayout
import com.racing.funtols.game.controller.PlateController
import com.racing.funtols.game.utils.actor.animMoveTo
import com.racing.funtols.game.utils.advanced.AdvancedScreen
import com.racing.funtols.game.utils.gdxGame

// ------------------------------------------------------------------------
// APanelPlate — дошка 2 колонки × 4 ряди з 8 половинок
// ------------------------------------------------------------------------
// Панель нічого не вирішує сама: правила в PlateController, а тут тільки
// геометрія слотів, драг-н-дроп і анімації.
//
// ВАЖЛИВО: половинки додаються через addActor (не через add{}) і
// позиціонуються вручну по слотах — констрейнти воювали б із драгом.
class APanelPlate(override val screen: AdvancedScreen): AConstraintLayout(screen) {

    // ------------------------------------------------------------------------
    // Callback
    // ------------------------------------------------------------------------
    var onWin: () -> Unit = {}

    // Ряд змінив стан "зібраний/не зібраний" — сюди можна повісити підсвітку
    // зібраної таблички (glow), якщо є асет
    var onRowStateChanged: (row: Int, isCorrect: Boolean) -> Unit = { _, _ -> }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private val controller = PlateController(plateCount = 4)

    // ------------------------------------------------------------------------
    // Field
    // ------------------------------------------------------------------------
    private val pieceW = 171f
    private val pieceH = 75f
    private val gap    = 2f

    private val timeSwap   = 0.15f // обмін половинок місцями
    private val timeReturn = 0.12f // повернення на місце при невдалому дропі

    // ------------------------------------------------------------------------
    // Actors
    // ------------------------------------------------------------------------
    // aPieceList[i] — половинка з картинкою listPlate[i], назавжди
    private val aPieceList = List(controller.pieceCount) { APlatePiece(screen) }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    override fun addActorsOnGroup() {
        addPieces()
        initController()
        newGame()
    }

    // ------------------------------------------------------------------------
    // Add Actors
    // ------------------------------------------------------------------------
    private fun addPieces() {
        aPieceList.forEachIndexed { index, piece ->
            piece.setSize(pieceW, pieceH)
            piece.setRegion(gdxGame.assetsAll.listPlate[index])

            piece.canDrag = { !controller.isLocked }
            piece.onDrop  = { onPieceDropped(index) }

            addActor(piece)
        }
    }

    // ------------------------------------------------------------------------
    // Controller
    // ------------------------------------------------------------------------
    private fun initController() {

        // Вміст двох слотів помінявся — розвозимо половинки по нових місцях
        controller.onSwap = { slotA, slotB ->
            val aPieceA = aPieceList[controller.pieceAt(slotA)]
            val aPieceB = aPieceList[controller.pieceAt(slotB)]

            var finished = 0
            val onEnd = {
                finished++
                if (finished == 2) {
                    refreshRowStates()
                    controller.onSwapFinished()
                }
            }

            aPieceA.animMoveTo(slotX(slotA), slotY(slotA), timeSwap, Interpolation.fade) { onEnd() }
            aPieceB.animMoveTo(slotX(slotB), slotY(slotB), timeSwap, Interpolation.fade) { onEnd() }
        }

        // Усі 4 таблички зібрано
        controller.onWin = {
            onWin()
        }
    }

    // ------------------------------------------------------------------------
    // Drag & Drop
    // ------------------------------------------------------------------------
    private fun onPieceDropped(pieceIndex: Int) {
        val piece    = aPieceList[pieceIndex]
        val fromSlot = controller.slotOf(pieceIndex)
        val toSlot   = slotAt(piece.centerX, piece.centerY)

        when {
            // Кинули за межі дошки, на свій же слот або під час анімації —
            // половинка просто повертається на місце
            toSlot == null || toSlot == fromSlot || controller.isLocked -> {
                piece.animMoveTo(slotX(fromSlot), slotY(fromSlot), timeReturn, Interpolation.fade)
            }

            // Кинули на іншу половинку — міняємо місцями
            else -> {
                gdxGame.soundUtil.apply { play(CLICK) }
                controller.swap(fromSlot, toSlot)
            }
        }
    }

    // ------------------------------------------------------------------------
    // Slot geometry
    // ------------------------------------------------------------------------
    // Слот i: колонка = i % 2, ряд = i / 2 (ряд 0 — верхній).
    // Y в scene2d росте вгору, тому верхній ряд — це height - pieceH.

    private fun slotX(slot: Int) = (slot % 2) * (pieceW + gap)

    private fun slotY(slot: Int) = height - pieceH - (slot / 2) * (pieceH + gap)

    /** У який слот потрапляє точка (cx, cy); null — якщо повз дошку */
    private fun slotAt(cx: Float, cy: Float): Int? {
        if (cx < 0f || cx > width || cy < 0f || cy > height) return null

        val col = if (cx < width / 2f) 0 else 1
        val row = ((height - cy) / (pieceH + gap)).toInt().coerceIn(0, controller.rowCount - 1)

        return row * 2 + col
    }

    // ------------------------------------------------------------------------
    // Row states
    // ------------------------------------------------------------------------
    private fun refreshRowStates() {
        for (row in 0 until controller.rowCount) {
            onRowStateChanged(row, controller.isRowCorrect(row))
        }
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /** Нова роздача: перемішати і миттєво розставити половинки по слотах */
    fun newGame() {
        controller.newGame()

        for (slot in 0 until controller.pieceCount) {
            val piece = aPieceList[controller.pieceAt(slot)]
            piece.clearActions()
            piece.setPosition(slotX(slot), slotY(slot))
        }

        refreshRowStates()
    }
}