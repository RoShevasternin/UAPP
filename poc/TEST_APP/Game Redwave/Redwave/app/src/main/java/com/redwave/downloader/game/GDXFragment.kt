package com.redwave.downloader.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.redwave.downloader.MainActivity

class GDXFragment : AndroidFragmentApplication() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val conf = AndroidApplicationConfiguration().apply {
            a                = 8
            numSamples       = 0
            useAccelerometer = false
            useCompass       = false
            useImmersiveMode = false
        }
        return initializeForView(GDXGame((requireActivity() as MainActivity).bridge), conf)
    }
}
