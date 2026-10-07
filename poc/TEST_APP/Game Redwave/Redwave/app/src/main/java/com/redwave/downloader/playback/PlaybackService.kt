@file:OptIn(UnstableApi::class)

package com.redwave.downloader.playback

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.TeeAudioProcessor
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.redwave.downloader.android.AudioFx
import java.nio.ByteBuffer
import java.nio.ByteOrder

// ═════════════════════════════════════════════════════════════════════════════
//  PlaybackService — Media3 MediaSessionService з ExoPlayer.
//  Музика — НЕ Gdx.audio: LibGDX ставить звук на паузу разом з GL-поверхнею,
//  а нам треба грати у фоні з вимкненим екраном (PORTING.md §6.4).
//
//  Візуалізатор: TeeAudioProcessor копіює PCM, що йде в AudioTrack, у
//  SpectrumAnalyzer — без audiofx.Visualizer і без дозволу RECORD_AUDIO.
//  Еквалайзер: AudioFx на audioSessionId плеєра (пересоздається при зміні).
//  Сповіщення з керуванням Media3 робить сам.
// ═════════════════════════════════════════════════════════════════════════════
class PlaybackService : MediaSessionService() {

    private var session: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this, TeeRenderersFactory(this))
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true,
            )
            .setHandleAudioBecomingNoisy(true)   // вийняли навушники — пауза
            .build()

        player.addListener(object : Player.Listener {
            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                AudioFx.attachSession(audioSessionId)
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying) Spectrum.analyzer.reset()
            }
        })
        AudioFx.attachSession(player.audioSessionId)

        session = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    /** Змахнули апку з «Недавніх» і нічого не грає — зупиняємо сервіс. */
    override fun onTaskRemoved(rootIntent: android.content.Intent?) {
        val p = session?.player
        if (p == null || !p.playWhenReady || p.mediaItemCount == 0) stopSelf()
    }

    override fun onDestroy() {
        session?.run {
            player.release()
            release()
        }
        session = null
        AudioFx.release()
        super.onDestroy()
    }

    // ------------------------------------------------------------------------
    // Tee → SpectrumAnalyzer
    // ------------------------------------------------------------------------
    private class TeeRenderersFactory(context: Context) : DefaultRenderersFactory(context) {
        override fun buildAudioSink(
            context: Context,
            enableFloatOutput: Boolean,
            enableAudioTrackPlaybackParams: Boolean,
        ): AudioSink = DefaultAudioSink.Builder(context)
            .setEnableFloatOutput(false)
            .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
            .setAudioProcessors(arrayOf(TeeAudioProcessor(PcmTap())))
            .build()
    }

    private class PcmTap : TeeAudioProcessor.AudioBufferSink {
        private var channels = 2
        private var encoding = C.ENCODING_PCM_16BIT
        private var scratch  = ShortArray(4096)

        override fun flush(sampleRateHz: Int, channelCount: Int, encoding: Int) {
            channels = channelCount.coerceAtLeast(1)
            this.encoding = encoding
        }

        override fun handleBuffer(buffer: ByteBuffer) {
            if (encoding != C.ENCODING_PCM_16BIT) return
            val sb = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
            val n = sb.remaining()
            if (n <= 0) return
            if (scratch.size < n) scratch = ShortArray(n)
            sb.get(scratch, 0, n)
            Spectrum.analyzer.pushPcm16(scratch, channels, n)
        }
    }
}
