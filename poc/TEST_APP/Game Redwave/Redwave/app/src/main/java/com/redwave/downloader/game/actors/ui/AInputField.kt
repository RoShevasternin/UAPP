package com.redwave.downloader.game.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.redwave.downloader.game.actors.label.AMsdfLabel
import com.redwave.downloader.game.platform.TextInputRequest
import com.redwave.downloader.game.utils.GameColor
import com.redwave.downloader.game.utils.actor.icon
import com.redwave.downloader.game.utils.actor.lbl
import com.redwave.downloader.game.utils.advanced.AdvancedGroup
import com.redwave.downloader.game.utils.advanced.AdvancedScreen
import com.redwave.downloader.game.utils.gdxGame
import com.redwave.downloader.game.utils.msdf
import com.redwave.downloader.game.utils.px

// ─────────────────────────────────────────────────────────────────────────────
// AInputField — поле (.pin / .srch). Текст малюється MSDF-лейблом, а
// редагування — справжнім EditText, який Activity кладе ТОЧНО поверх лейбла
// (bridge.beginTextInput). Так працюють клавіатура, вибір тексту й системне
// «Вставити» без тосту Android 12+. Поки редагуємо — MSDF-текст ховаємо.
// ─────────────────────────────────────────────────────────────────────────────
class AInputField(
    override val screen: AdvancedScreen,
    private val hint: String,
    private val iconRegion: TextureRegion,
    private val isUrl: Boolean,
    private val bg: Color,
    private val trailing: AdvancedGroup? = null,
) : AdvancedGroup() {

    var text: String = ""
        private set
    var onChange: (String) -> Unit = {}
    var onSubmit: (String) -> Unit = {}
    var isEditing = false
        private set

    private val label: AMsdfLabel = lbl(hint, msdf.regular(14f, GameColor.hint_7C6A70))
    private val ic = icon(iconRegion, px(18f), GameColor.muted_A8949B)

    private val textX get() = px(12f) + ic.width + px(9f)
    private val textW get() = width - textX - (trailing?.let { it.width + px(10f) } ?: px(12f))

    override fun addActorsOnGroup() {
        addAndFillActor(ARect(screen, px(15f), bg, stroke = GameColor.line_white_7))
        addActor(ic); ic.setPosition(px(12f), (height - ic.height) / 2f)
        addActor(label)
        trailing?.let { addActor(it); it.setPosition(width - px(5f) - it.width, (height - it.height) / 2f) }
        refreshLabel()

        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean =
                trailing == null || x < (trailing.x - px(4f))
            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                if (x in 0f..width && y in 0f..height) beginEdit()
            }
        })
    }

    fun setText(t: String) {
        text = t
        refreshLabel()
    }

    private fun refreshLabel() {
        val empty = text.isEmpty()
        label.setEllipsis(false)
        label.setText(if (empty) hint else text)
        label.setTextColor(if (empty) GameColor.hint_7C6A70 else GameColor.text_FBF1F2)
        label.pack()
        label.setEllipsis(true)
        label.width = minOf(label.width, textW)
        label.setPosition(textX, (height - label.height) / 2f)
        label.isVisible = !isEditing
    }

    fun beginEdit() {
        if (isEditing) return
        isEditing = true
        label.isVisible = false
        val stage = stage ?: return
        // Рамка тексту в px екрана (y від верху)
        val a = localToStageCoordinates(Vector2(textX, height)).let { stage.stageToScreenCoordinates(it) }
        val b = localToStageCoordinates(Vector2(textX + textW, 0f)).let { stage.stageToScreenCoordinates(it) }
        val pxPerWu = stage.viewport.screenWidth / stage.viewport.worldWidth
        gdxGame.bridge.beginTextInput(
            TextInputRequest(
                xPx = a.x.toInt(), yPx = a.y.toInt(), wPx = (b.x - a.x).toInt(), hPx = (b.y - a.y).toInt(),
                text = text, hint = hint, textSizePx = px(14f) * pxPerWu, isUrl = isUrl,
            ),
            onChange = { t -> text = t; onChange(t) },
            onDone = { t -> text = t; isEditing = false; refreshLabel(); onSubmit(t) },
        )
    }

    override fun dispose() {
        if (isEditing) gdxGame.bridge.endTextInput()
        super.dispose()
    }
}
