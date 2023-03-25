package com.melitopolcherry.hackathon.custom_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import com.melitopolcherry.hackathon.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Values change happens in [0, 1]
 */
class RangeSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var thumbImage: Bitmap

    private var thumbHalfWidth: Float = 0f

    private var thumbHalfHeight: Float = 0f

    private var lineHalfHeight: Float = 0f

    private var pressedThumb: Thumb? = null

    private var textSize: Float = 0f

    private var activeColor: Int = DEFAULT_ACTIVE_COLOR

    private var inactiveColor: Int = DEFAULT_INACTIVE_COLOR

    var textColor: Int = DEFAULT_TEXT_COLOR

    private var xOffset: Int = 0

    private var mWidth: Int = 0

    private var mHeight: Int = 0

    private var selectedMinValue: Double = 0.0

    private var selectedMaxValue: Double = 1.0

    var selectedMinMaxValue: Pair<Double, Double>
        get() = selectedMinValue to selectedMaxValue
        set(value) {
            if (value.first > value.second) {
                if (selectedMinValue == value.first) { // dragging max value
                    selectedMaxValue = selectedMinValue
                } else if (selectedMaxValue == value.second) { // dragging min value
                    selectedMinValue = selectedMaxValue
                }
            } else {
                selectedMinValue = value.first
                selectedMaxValue = value.second
            }
            if (selectedMinValue < 0) {
                selectedMinValue = 0.0
            }
            if (selectedMinValue > 1) {
                selectedMinValue = 1.0
            }
            if (selectedMaxValue < 0) {
                selectedMaxValue = 0.0
            }
            if (selectedMaxValue > 1) {
                selectedMaxValue = 1.0
            }
            invalidate()
        }

    var rangeSeekBarChangeListener: ((Double, Double) -> Unit)? = null

    var thumbTextGenerator: ((Double) -> String)? = null
        set(value) {
            field = value
            invalidate()
        }

    init {
        var lDrawable: Drawable? = null
        var lTextSizeInPx = 0f

        if (attrs != null) {
            val arr = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar)
            lDrawable = arr.getDrawable(R.styleable.RangeSeekBar_thumb)
            lTextSizeInPx = arr.getDimension(R.styleable.RangeSeekBar_textSize, 0f)
            activeColor = arr.getColor(R.styleable.RangeSeekBar_activeColor, DEFAULT_ACTIVE_COLOR)
            inactiveColor =
                arr.getColor(R.styleable.RangeSeekBar_inactiveColor, DEFAULT_INACTIVE_COLOR)
            textColor = arr.getColor(R.styleable.RangeSeekBar_textColor, DEFAULT_TEXT_COLOR)
            arr.recycle()
        }

        thumbImage =
            (lDrawable ?: ContextCompat.getDrawable(context, DEFAULT_DRAWABLE_RES))?.toBitmap()!!
        thumbHalfWidth = 0.5f * thumbImage.width
        thumbHalfHeight = 0.5f * thumbImage.height
        lineHalfHeight = 0.05f * thumbImage.height.toFloat()
        xOffset = thumbHalfWidth.toInt()

        textSize = if (lTextSizeInPx == 0f) dpToPx(context) else lTextSizeInPx
    }

    private fun dpToPx(context: Context): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_IN_SP,
            context.resources.displayMetrics
        )

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                pressedThumb = evalPressedThumb(event.x)

                // Only handle thumb presses.
                if (pressedThumb == null) {
                    return super.onTouchEvent(event)
                }

                trackTouchEvent(event)
                attemptClaimDrag()
            }
            MotionEvent.ACTION_MOVE -> if (pressedThumb != null) {
                trackTouchEvent(event)
                attemptClaimDrag()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                rangeSeekBarChangeListener?.invoke(selectedMinValue, selectedMaxValue)
                pressedThumb = null
                invalidate()
            }
        }
        return true
    }

    private fun trackTouchEvent(event: MotionEvent) {
        val x = event.x
        if (Thumb.MIN == pressedThumb) {
            selectedMinMaxValue = screenToValue(x) to selectedMaxValue
        } else if (Thumb.MAX == pressedThumb) {
            selectedMinMaxValue = selectedMinValue to screenToValue(x)
        }
    }

    private fun attemptClaimDrag() {
        parent?.requestDisallowInterceptTouchEvent(true)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.textSize = textSize
        paint.style = Paint.Style.FILL

        // draw seek bar background line
        paint.color = inactiveColor
        canvas.drawRect(
            xOffset.toFloat(), thumbHalfHeight - lineHalfHeight,
            (mWidth - xOffset).toFloat(), thumbHalfHeight + lineHalfHeight, paint
        )

        // draw seek bar active range line
        paint.color = activeColor
        canvas.drawRect(
            valueToScreen(selectedMinValue).toFloat(), thumbHalfHeight - lineHalfHeight,
            valueToScreen(selectedMaxValue).toFloat(), thumbHalfHeight + lineHalfHeight, paint
        )

        drawThumb(valueToScreen(selectedMinValue).toFloat(), canvas)
        drawThumb(valueToScreen(selectedMaxValue).toFloat(), canvas)

        // draw the text
        paint.textSize = textSize

        var minText = selectedMinValue.toString()
        var maxText = selectedMaxValue.toString()
        thumbTextGenerator?.let {
            minText = it(selectedMinValue)
            maxText = it(selectedMaxValue)
        }
        val minTextWidth = paint.measureText(minText)
        val maxTextWidth = paint.measureText(maxText)
        var minTextX = valueToScreen(selectedMinValue) - minTextWidth * 0.5f
        if (minTextX < 0) {
            minTextX = 0.0
        }
        var maxTextX = valueToScreen(selectedMaxValue) - maxTextWidth * 0.5f
        if (maxTextX + maxTextWidth > mWidth) {
            maxTextX = (mWidth - maxTextWidth).toDouble()
        }
        var minTextYOffset = 0f
        var maxTextYOffset = 0f
        if (minTextX + minTextWidth >= maxTextX) {
            minTextYOffset = -textSize / 1.5f
            maxTextYOffset = textSize / 1.5f
        }

        paint.color = textColor
        canvas.drawText(
            minText, minTextX.toFloat(),
            2 * textSize + thumbHalfHeight + thumbHalfHeight + minTextYOffset, paint
        )

        canvas.drawText(
            maxText, maxTextX.toFloat(),
            2 * textSize + thumbHalfHeight + thumbHalfHeight + maxTextYOffset, paint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w
        mHeight = h
    }

    private fun drawThumb(screenCoordX: Float, canvas: Canvas) {
        canvas.drawBitmap(thumbImage, screenCoordX - thumbHalfWidth, 0f, paint)
    }

    private fun evalPressedThumb(touchX: Float): Thumb? {
        var result: Thumb? = null
        val minThumbPressed = isInThumbRange(touchX, selectedMinValue)
        val maxThumbPressed = isInThumbRange(touchX, selectedMaxValue)
        if (minThumbPressed && maxThumbPressed) {
            result = if (touchX / mWidth > 0.5f) Thumb.MIN else Thumb.MAX
        } else if (minThumbPressed) {
            result = Thumb.MIN
        } else if (maxThumbPressed) {
            result = Thumb.MAX
        }
        return result
    }

    private fun isInThumbRange(touchX: Float, normalizedThumbValue: Double): Boolean {
        return abs(touchX - valueToScreen(normalizedThumbValue)) <= 2 * thumbHalfWidth
    }

    private fun valueToScreen(value: Double): Double {
        return xOffset + value * (mWidth - 2 * xOffset)
    }

    private fun screenToValue(screenCoordX: Float): Double {
        if (mWidth == 0) {
            return 0.0
        }
        val result = (screenCoordX - xOffset) / (mWidth - 2 * xOffset)
        return min(1f, max(0f, result)).toDouble()
    }

    private enum class Thumb {
        MIN, MAX
    }

    private fun Drawable.toBitmap(
        @Px
        width: Int = intrinsicWidth,
        @Px
        height: Int = intrinsicHeight, config: Bitmap.Config? = null
    ): Bitmap {
        if (this is BitmapDrawable) {
            if (config == null || bitmap.config == config) {
                // Fast-path to return original. Bitmap.createScaledBitmap will do this check, but it
                // involves allocation and two jumps into native code so we perform the check ourselves.
                if (width == intrinsicWidth && height == intrinsicHeight) {
                    return bitmap
                }
                return Bitmap.createScaledBitmap(bitmap, width, height, true)
            }
        }

        val oldLeft = bounds.left
        val oldTop = bounds.top
        val oldRight = bounds.right
        val oldBottom = bounds.bottom

        val bitmap = Bitmap.createBitmap(width, height, config ?: Bitmap.Config.ARGB_8888)
        setBounds(0, 0, width, height)
        draw(Canvas(bitmap))

        setBounds(oldLeft, oldTop, oldRight, oldBottom)
        return bitmap
    }

    companion object {
        val DEFAULT_INACTIVE_COLOR = Color.parseColor("#bce0fd")
        val DEFAULT_ACTIVE_COLOR = Color.parseColor("#00aec5")
        val DEFAULT_TEXT_COLOR = Color.parseColor("#333333")
        const val DEFAULT_DRAWABLE_RES = R.drawable.range_seek_thumb
        private const val DEFAULT_TEXT_SIZE_IN_SP = 15f
    }
}