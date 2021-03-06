package com.android.settings.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.*

import androidx.core.content.ContextCompat

import com.android.settings.R

import com.android.settingslib.Utils


class ProgressCard(context: Context?, attrs: AttributeSet?): LinearLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.progress_card, this, true)
    }
    private var ANIMATION_DURATION = 2500


    fun setTitle(attr: String?) {
        val title = findViewById<TextView>(R.id.title)
        if (attr != null)
            title.text = attr
    }

    fun setSummary(attr: String?) {
        val summary = findViewById<TextView>(R.id.summary)
        if (attr != null)
            summary.text = attr
    }

    fun setProgress(attr: Int, animate: Boolean) {
        val progressBar: ProgressBar = findViewById(R.id.progress)
        progressBar.max = 1000
        if (animate) {
            val anim = ProgressAnimator(progressBar, 0f, attr.toFloat() * 10)
            anim.duration = ANIMATION_DURATION.toLong()
            progressBar.startAnimation(anim)
        }
    }

    fun setBackgroundTint(color: Int) {
        val progressBar = findViewById<ProgressBar>(R.id.progress)
        val layout = findViewById<RelativeLayout>(R.id.relativeLayout)
        progressBar.progressDrawable.setTint(color)
        layout.background.setTint(color)
    }

    fun setTitleTint(color: Int) {
        val title = findViewById<TextView>(R.id.title)
        title.setTextColor(color)
    }
}

/* https://stackoverflow.com/questions/26074784/how-to-make-a-view-with-rounded-corners, adapted 
   I put it here cause I am 100% confident it's the only view of ours that will ever need it.
*/
class RoundedCornerLayout: FrameLayout {
    private var maskBitmap: Bitmap? = null
    private var paint: Paint? = null
    private var maskPaint: Paint? = null
    private var cornerRadius = 0f

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val metrics = context.resources.displayMetrics
        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getCornerRadius(context), metrics)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        maskPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        maskPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        setWillNotDraw(false)
    }

    override fun draw(canvas: Canvas) {
        val offscreenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val offscreenCanvas = Canvas(offscreenBitmap)
        super.draw(offscreenCanvas)
        if (maskBitmap == null) {
            maskBitmap = createMask(width, height)
        }
        offscreenCanvas.drawBitmap(maskBitmap!!, 0f, 0f, maskPaint)
        canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint)
    }

    private fun createMask(width: Int, height: Int): Bitmap {
        val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(mask)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), getCornerRadius(getContext()), getCornerRadius(getContext()), paint)
        return mask
    }

    private fun getCornerRadius(context: Context): Float {
        val cornerRadius: Float = context.resources.getDimension(
            Utils.getThemeAttr(context, android.R.attr.dialogCornerRadius))
        return cornerRadius
    }
}

class ProgressAnimator(private val progressBar: ProgressBar, private val from: Float, private val to: Float): Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        val value = from + (to - from) * interpolatedTime
        progressBar.setProgress(value.toInt())
    }
}
