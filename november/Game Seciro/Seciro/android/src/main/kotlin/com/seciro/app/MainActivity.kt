package com.seciro.app

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

// Точка входу Android: віддає екран libGDX-апці з :core
class MainActivity : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration().apply {
            useImmersiveMode = false   // статус-бар Android лишається — це утиліта, не гра
            numSamples = 4
        }
        initialize(SeciroApp(), config)
    }
}
