package com.redwave.downloader.playback

import com.redwave.downloader.core.viz.SpectrumAnalyzer

/**
 * Один аналізатор на процес: PlaybackService годує його PCM через TeeAudioProcessor,
 * GDX читає смуги раз на кадр (bridge.spectrum). Без audiofx.Visualizer — тож без RECORD_AUDIO.
 */
object Spectrum {
    val analyzer = SpectrumAnalyzer()
}
