package com.skindustry.skinly.game.utils

import com.badlogic.gdx.graphics.Color

object GameColor {

    val background : Color = Color.valueOf("FFFFFF")

    val gray_818181 : Color = Color.valueOf("818181")
    val gray_F2F2F2 : Color = Color.valueOf("F2F2F2")
    val gray_B8B8B8 : Color = Color.valueOf("B8B8B8")

    // Зелена кнопка «FREE …» — єдиний зелений елемент на світло-помаранчевому UI
    val green_22 : Color = Color.valueOf("22C55E")
    val green_16 : Color = Color.valueOf("16A34A")

    // Монета — в тон помаранчевому градієнту апки (orange_def)
    val coin_FDE047 : Color = Color.valueOf("FDE047")
    val coin_F59E0B : Color = Color.valueOf("F59E0B")
    val coin_EA580C : Color = Color.valueOf("EA580C")

    val white_25: Color = Color.WHITE.cpy().apply { a = 0.25f }
    val white_22: Color = Color.WHITE.cpy().apply { a = 0.22f }
    val white_70: Color = Color.WHITE.cpy().apply { a = 0.70f }
    val black_80: Color = Color.BLACK.cpy().apply { a = 0.80f }
}