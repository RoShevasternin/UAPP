package com.seciro.app

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

// Заготовка апки. Екрани з'являться тут лише після того, як їх проклацано в прототипі (prototype/)
class SeciroApp : ApplicationAdapter() {
    override fun render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }
}
