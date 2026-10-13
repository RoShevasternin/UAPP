package com.vidtools.framefall.android.video

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.Looper
import android.view.Surface
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.TextureData
import com.badlogic.gdx.math.Matrix4
import com.vidtools.framefall.game.platform.VideoFrame
import com.vidtools.framefall.game.platform.VideoPlayer
import com.vidtools.framefall.util.log

// ═════════════════════════════════════════════════════════════════════════════
//  ExoVideoPlayer — ExoPlayer, що декодує прямо в OES-текстуру GDX.
//
//  ExoPlayer → Surface(SurfaceTexture(texId)) → updateTexImage() у GL-потоці → GDX
//  малює цю текстуру шейдером samplerExternalOES (game/utils/video/OesShader).
//  Так і плеєр, і живий фон головного екрана — частина сцени GDX: поверх них
//  лягають наші кнопки, градієнти й анімації, без SurfaceView поверх GL.
//
//  ПОТОКИ:
//    • ExoPlayer живе на main (Looper.getMainLooper) — усі команди туди постимо;
//    • SurfaceTexture/текстура — у GL-потоці: створюються ліниво в frame(),
//      updateTexImage — там само (контекст, у якому створено текстуру);
//    • стан (позиція, тривалість…) — volatile-поля, main оновлює кожні 100 мс.
//
//  GL-КОНТЕКСТ: Activity у фоні → GDX pause() → onGlPause(): текстуру й Surface
//  звільняємо, плеєр ставимо на паузу. На наступному frame() усе створюється наново
//  (контекст міг бути перестворений — старі id текстур недійсні).
// ═════════════════════════════════════════════════════════════════════════════
class ExoVideoPlayer(
    private val ctx: Context,
    private val onReleased: (ExoVideoPlayer) -> Unit,
) : VideoPlayer {

    private val main = Handler(Looper.getMainLooper())
    /** Лише main. */
    private var exo: ExoPlayer? = null
    private var released = false

    // ── GL-бік ──
    private var tex: OesTexture? = null
    private var st: SurfaceTexture? = null
    private var surface: Surface? = null
    @Volatile private var frameAvailable = false
    private var gotFrame = false
    private val stMatrix = FloatArray(16)
    private val matrix = Matrix4()

    // ── Стан (пише main) ──
    @Volatile override var positionMs: Long = 0L; private set
    @Volatile override var durationMs: Long = 0L; private set
    @Volatile override var isPlaying: Boolean = false; private set
    @Volatile override var isEnded: Boolean = false; private set
    @Volatile override var error: String? = null; private set
    @Volatile override var videoWidth: Int = 0; private set
    @Volatile override var videoHeight: Int = 0; private set
    /** Поворот, який декодер НЕ застосував (unappliedRotationDegrees) — додаємо в матрицю самі. */
    @Volatile private var rotation = 0
    override val hasFrame: Boolean get() = gotFrame

    @Volatile private var rangeStart = 0L
    @Volatile private var rangeEnd = -1L

    init {
        main.post { if (!released) createExo() }
    }

    private fun createExo() {
        val p = ExoPlayer.Builder(ctx).setLooper(Looper.getMainLooper()).build()
        p.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width == 0 || videoSize.height == 0) return
                @Suppress("DEPRECATION") val rot = videoSize.unappliedRotationDegrees
                val w = (videoSize.width * videoSize.pixelWidthHeightRatio).toInt()
                val h = videoSize.height
                rotation = rot
                if (rot == 90 || rot == 270) { videoWidth = h; videoHeight = w } else { videoWidth = w; videoHeight = h }
            }
            override fun onPlayerError(e: PlaybackException) {
                error = e.errorCodeName
                log("video error: ${e.errorCodeName} ${e.message}")
            }
            override fun onPlaybackStateChanged(state: Int) {
                isEnded = state == Player.STATE_ENDED
                if (state == Player.STATE_READY) error = null
            }
        })
        exo = p
        surface?.let { p.setVideoSurface(it) }
        poll.run()
    }

    private val poll = object : Runnable {
        override fun run() {
            val p = exo ?: return
            positionMs = p.currentPosition
            durationMs = p.duration.takeIf { it != C.TIME_UNSET } ?: 0L
            isPlaying = p.isPlaying
            if (rangeEnd > 0 && positionMs >= rangeEnd) p.seekTo(rangeStart)
            main.postDelayed(this, 100L)
        }
    }

    // ------------------------------------------------------------------------
    // Команди (будь-який потік → main)
    // ------------------------------------------------------------------------
    private fun onMain(block: ExoPlayer.() -> Unit) { main.post { exo?.block() } }

    override fun open(uri: String, autoplay: Boolean, loop: Boolean, muted: Boolean, startMs: Long) {
        isEnded = false; error = null; positionMs = startMs
        this.loopValue = loop; this.mutedValue = muted
        main.post {
            if (exo == null && !released) createExo()
            exo?.apply {
                setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(), !muted)
                setHandleAudioBecomingNoisy(!muted)
                volume = if (muted) 0f else 1f
                repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                setMediaItem(MediaItem.fromUri(uri), startMs)
                playWhenReady = autoplay
                prepare()
            }
        }
    }

    override fun play() = onMain {
        if (playbackState == Player.STATE_ENDED) seekTo(rangeStart)
        if (playbackState == Player.STATE_IDLE) prepare()
        play()
    }
    override fun pause() = onMain { this.pause() }
    override fun seekTo(ms: Long) { positionMs = ms; isEnded = false; onMain { seekTo(ms.coerceAtLeast(0L)) } }

    private var speedValue = 1f
    override var speed: Float
        get() = speedValue
        set(v) { speedValue = v; onMain { playbackParameters = PlaybackParameters(v) } }

    private var loopValue = false
    override var loop: Boolean
        get() = loopValue
        set(v) { loopValue = v; onMain { repeatMode = if (v) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF } }

    private var mutedValue = false
    override var muted: Boolean
        get() = mutedValue
        set(v) { mutedValue = v; onMain { volume = if (v) 0f else 1f } }

    override fun setRange(startMs: Long, endMs: Long) {
        rangeStart = startMs.coerceAtLeast(0L); rangeEnd = endMs
    }

    // ------------------------------------------------------------------------
    // GL
    // ------------------------------------------------------------------------
    override fun frame(): VideoFrame? {
        if (released) return null
        if (tex == null) createSurface()
        val s = st ?: return null
        if (frameAvailable) {
            frameAvailable = false
            runCatching { s.updateTexImage(); s.getTransformMatrix(stMatrix) }
                .onSuccess { gotFrame = true }
                .onFailure { log("updateTexImage: ${it.message}") }
        }
        if (!gotFrame) return null
        matrix.set(stMatrix)
        if (rotation != 0) {
            // поворот координат текстури навколо центру: M = ST · T(.5) · R · T(-.5)
            matrix.translate(0.5f, 0.5f, 0f).rotate(0f, 0f, 1f, -rotation.toFloat()).translate(-0.5f, -0.5f, 0f)
        }
        return VideoFrame(tex!!, matrix)
    }

    private fun createSurface() {
        val id = Gdx.gl.glGenTexture()
        Gdx.gl.glBindTexture(GL_TEXTURE_EXTERNAL_OES, id)
        Gdx.gl.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR)
        Gdx.gl.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR)
        Gdx.gl.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE)
        Gdx.gl.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE)
        Gdx.gl.glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0)
        val t = OesTexture(id)
        val s = SurfaceTexture(id)
        s.setOnFrameAvailableListener({ frameAvailable = true }, main)
        val sf = Surface(s)
        tex = t; st = s; surface = sf
        gotFrame = false
        main.post { exo?.setVideoSurface(sf) }
    }

    /** GL-потік, GDX pause(): контекст може зникнути — звільняємо все GL і ставимо на паузу. */
    fun onGlPause() {
        val s = st; val sf = surface
        tex?.let { runCatching { it.dispose() } }
        tex = null; st = null; surface = null; gotFrame = false; frameAvailable = false
        main.post {
            exo?.pause()
            exo?.clearVideoSurface(sf)
            sf?.release(); s?.release()
        }
    }

    override fun release() {
        if (released) return
        released = true
        val s = st; val sf = surface
        tex?.let { runCatching { it.dispose() } }
        tex = null; st = null; surface = null; gotFrame = false
        main.post {
            main.removeCallbacks(poll)
            exo?.release(); exo = null
            sf?.release(); s?.release()
        }
        onReleased(this)
    }

    companion object {
        const val GL_TEXTURE_EXTERNAL_OES = 0x8D65
    }
}

/**
 * Texture GDX поверх готового OES-хендла: SpriteBatch бінд-ить її як GL_TEXTURE_EXTERNAL_OES.
 * TextureData — Custom і порожня: пікселі пише декодер через SurfaceTexture, не ми.
 */
class OesTexture(handle: Int) : Texture(ExoVideoPlayer.GL_TEXTURE_EXTERNAL_OES, handle, Empty) {

    private object Empty : TextureData {
        override fun getType() = TextureData.TextureDataType.Custom
        override fun isPrepared() = true
        override fun prepare() {}
        override fun consumePixmap(): Pixmap = error("OES")
        override fun disposePixmap() = false
        override fun consumeCustomData(target: Int) {}
        override fun getWidth() = 1
        override fun getHeight() = 1
        override fun getFormat() = Pixmap.Format.RGBA8888
        override fun useMipMaps() = false
        override fun isManaged() = false
    }

}
