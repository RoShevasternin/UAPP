package com.rbuxrds.counter.game.manager.util

import com.badlogic.gdx.Gdx

class VibroUtil {

    var isVibro = true

    fun vibro(millis: Int) {
        if (isVibro) Gdx.input.vibrate(millis)
    }
}