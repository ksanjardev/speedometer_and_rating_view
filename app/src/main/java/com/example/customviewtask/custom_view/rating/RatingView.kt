package com.example.customviewtask.custom_view.rating

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.customviewtask.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**   Created by Sanjar Karimov 1:15 PM 12/18/2024   */


class RatingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val filledStarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.YELLOW
    }

    private val emptyStarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.GRAY
    }

    private var starSize = 100f
    private var starSpacing = 20f
    private var numStars = 5
    private var rating = 3f

    private val starPath = Path()

    private var onRatingChanged: ((Float) -> Unit)? = null

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.RatingView)
            numStars = typedArray.getInt(R.styleable.RatingView_numStars, numStars)
            rating = typedArray.getFloat(R.styleable.RatingView_rating, rating)
            starSize = typedArray.getDimension(R.styleable.RatingView_starSize, starSize)
            starSpacing = typedArray.getDimension(R.styleable.RatingView_starSpacing, starSpacing)
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawStars(canvas)
    }

    private fun drawStars(canvas: Canvas) {
        val totalWidth = (numStars * starSize) + ((numStars - 1) * starSpacing)
        val startX = (width - totalWidth) / 2f
        val startY = (height - starSize) / 2f

        for (i in 0 until numStars) {
            val left = startX + i * (starSize + starSpacing)
            starPath.reset()
            createStarPath(starPath, left + starSize / 2, startY + starSize / 2, starSize / 2)

            if (i < rating.toInt()) {
                canvas.drawPath(starPath, filledStarPaint)
            } else if (i < rating) {
                // Partial star
                val saveCount = canvas.save()
                val clipWidth = starSize * (rating - i)
                canvas.clipRect(left, startY, left + clipWidth, startY + starSize)
                canvas.drawPath(starPath, filledStarPaint)
                canvas.restoreToCount(saveCount)
                canvas.drawPath(starPath, emptyStarPaint)
            } else {
                canvas.drawPath(starPath, emptyStarPaint)
            }
        }
    }

    private fun createStarPath(path: Path, cx: Float, cy: Float, radius: Float) {
        val angle = Math.toRadians(36.0) // 36 degrees for a 5-pointed star
        val innerRadius = radius / 1.6767f

        path.reset()
        for (i in 0..9) {
            val r = if (i % 2 == 0) radius else innerRadius
            val x = (cx + r * cos(i * angle - Math.PI / 2)).toFloat() // Adjust for top orientation
            val y = (cy + r * sin(i * angle - Math.PI / 2)).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = (numStars * starSize + (numStars - 1) * starSpacing).toInt()
        val totalHeight = starSize.toInt()
        setMeasuredDimension(
            resolveSize(totalWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                val totalWidth = (numStars * starSize) + ((numStars - 1) * starSpacing)
                val startX = (width - totalWidth) / 2f
                val x = event.x
                val newRating = (x - startX) / (starSize + starSpacing) + 1
                rating = min(numStars.toFloat(), maxOf(0f, newRating))
//                onRatingChanged?.invoke(rating)
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}