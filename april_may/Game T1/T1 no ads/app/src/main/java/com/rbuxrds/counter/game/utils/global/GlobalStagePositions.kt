package com.rbuxrds.counter.game.utils.global

import com.badlogic.gdx.math.Vector2

object GlobalStagePositions {

    enum class Position { COIN, XP, BUY_BTN, CUBE_0, CUBE_1 }

    private val positionMap = Array(Position.entries.size) { Vector2() }

    fun register(position: Position, x: Float, y: Float) {
        positionMap[position.ordinal].set(x, y)
    }

    fun get(position: Position): Vector2 = positionMap[position.ordinal]
}