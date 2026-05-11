package com.robuxe.robuxtracker.freerobux.Scratchcard

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.robuxe.robuxtracker.freerobux.R
import java.nio.ByteBuffer
import java.util.concurrent.Executors

class RC_ScratchView @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    private val styleAttr: Int = 0
) : View(context, attrs, styleAttr) {

    companion object {
        const val STROKE_WIDTH = 12f
        private const val TOUCH_TOLERANCE = 4f

        fun getTransparentPixelPercent(bitmap: Bitmap?): Float {
            if (bitmap == null) return 0f
            val buffer = ByteBuffer.allocate(bitmap.height * bitmap.rowBytes)
            bitmap.copyPixelsToBuffer(buffer)
            val array = buffer.array()
            var count = 0
            for (b in array) {
                if (b == 0.toByte()) count++
            }
            return count.toFloat() / array.size
        }
    }

    private var scratchBitmapOrig: Bitmap? = null
    private var mX = 0f
    private var mY = 0f
    private var mScratchBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mErasePath = Path()
    private var mTouchPath = Path()
    private var mBitmapPaint = Paint(Paint.DITHER_FLAG)
    private var mErasePaint = Paint()
    private var mGradientBgPaint = Paint()
    private var mDrawable: BitmapDrawable? = null
    private var mRevealListener: IRevealListener? = null
    private var mRevealPercent = 0f
    private var mThreadCount = 0
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        init()
    }

    private fun init() {
        mErasePaint.isAntiAlias = true
        mErasePaint.isDither = true
        mErasePaint.color = -0x1
        mErasePaint.style = Paint.Style.STROKE
        mErasePaint.strokeJoin = Paint.Join.BEVEL
        mErasePaint.strokeCap = Paint.Cap.ROUND
        mErasePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        setStrokeWidth(6)

        val arr = context.obtainStyledAttributes(attrs, R.styleable.ScratchView, styleAttr, 0)
        val overlayImage = arr.getResourceId(R.styleable.ScratchView_overlay_image, R.drawable.img_scratch_card)
        val overlayWidth = arr.getDimension(R.styleable.ScratchView_overlay_width, 1000f)
        val overlayHeight = arr.getDimension(R.styleable.ScratchView_overlay_height, 1000f)
        var tileMode = arr.getString(R.styleable.ScratchView_tile_mode) ?: "CLAMP"
        arr.recycle()

        var bitmap = BitmapFactory.decodeResource(resources, overlayImage)
        if (bitmap == null) {
            bitmap = drawableToBitmap(ContextCompat.getDrawable(context, overlayImage)!!)
        }
        scratchBitmapOrig = Bitmap.createScaledBitmap(bitmap, overlayWidth.toInt(), overlayHeight.toInt(), false)
        mDrawable = BitmapDrawable(resources, scratchBitmapOrig)

        when (tileMode) {
            "REPEAT" -> mDrawable?.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            "MIRROR" -> mDrawable?.setTileModeXY(Shader.TileMode.MIRROR, Shader.TileMode.MIRROR)
            else -> mDrawable?.setTileModeXY(Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
    }

    fun setStrokeWidth(multiplier: Int) {
        mErasePaint.strokeWidth = multiplier * STROKE_WIDTH
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mScratchBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mScratchBitmap!!)

        val rect = Rect(0, 0, w, h)
        mDrawable?.bounds = rect

        val startGradientColor = ContextCompat.getColor(context, R.color.transparent)
        val endGradientColor = ContextCompat.getColor(context, R.color.transparent)

        mGradientBgPaint.shader = LinearGradient(0f, 0f, 0f, h.toFloat(), startGradientColor, endGradientColor, Shader.TileMode.MIRROR)
        mCanvas?.drawRect(rect, mGradientBgPaint)
        mDrawable?.draw(mCanvas!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mScratchBitmap?.let { canvas.drawBitmap(it, 0f, 0f, mBitmapPaint) }
        canvas.drawPath(mErasePath, mErasePaint)
    }

    private fun touchStart(x: Float, y: Float) {
        mErasePath.reset()
        mErasePath.moveTo(x, y)
        mX = x
        mY = y
    }

    fun clear() {
        val bounds = getViewBounds()
        val paint = Paint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mCanvas?.drawRect(bounds[0].toFloat(), bounds[1].toFloat(), bounds[2].toFloat(), bounds[3].toFloat(), paint)
        checkRevealed()
        invalidate()
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mErasePath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
            drawPath()
        }
        mTouchPath.reset()
        mTouchPath.addCircle(mX, mY, 30f, Path.Direction.CW)
    }

    private fun drawPath() {
        mErasePath.lineTo(mX, mY)
        mCanvas?.drawPath(mErasePath, mErasePaint)
        mTouchPath.reset()
        mErasePath.reset()
        mErasePath.moveTo(mX, mY)
        checkRevealed()
    }

    fun reveal() = clear()

    fun mask() {
        clear()
        mRevealPercent = 0f
        scratchBitmapOrig?.let { mCanvas?.drawBitmap(it, 0f, 0f, mBitmapPaint) }
        invalidate()
    }

    private fun touchUp() = drawPath()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }
        return true
    }

    fun getColor() = mErasePaint.color

    fun getErasePaint() = mErasePaint

    fun setEraserMode() {
        mErasePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun setRevealListener(listener: IRevealListener) {
        mRevealListener = listener
    }

    fun isRevealed() = mRevealPercent >= 0.33f

    private fun checkRevealed() {
        if (!isRevealed() && mRevealListener != null) {
            val bounds = getViewBounds()
            if (mThreadCount > 1) return
            mThreadCount++

            executor.execute {
                try {
                    val croppedBitmap = Bitmap.createBitmap(mScratchBitmap!!, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1])
                    val percent = getTransparentPixelPercent(croppedBitmap)
                    mainHandler.post {
                        if (!isRevealed()) {
                            val oldPercent = mRevealPercent
                            mRevealPercent = percent
                            if (oldPercent != percent) {
                                mRevealListener?.onRevealPercentChangedListener(this, percent)
                            }
                            if (isRevealed()) {
                                mRevealListener?.onRevealed(this)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ScratchView", "Error checking revealed", e)
                } finally {
                    mThreadCount--
                }
            }
        }
    }

    fun getViewBounds(): IntArray {
        return intArrayOf(0, 0, width, height)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) return drawable.bitmap
        }
        val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    interface IRevealListener {
        fun onRevealed(rrScratchView: RC_ScratchView)
        fun onRevealPercentChangedListener(rrScratchView: RC_ScratchView, percent: Float)
    }
}
