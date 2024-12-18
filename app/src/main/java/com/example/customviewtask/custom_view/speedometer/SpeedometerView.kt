package com.example.customviewtask.custom_view.speedometer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**   Created by Sanjar Karimov 10:20 AM 12/18/2024   */

class SpeedometerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attributeSet, defStyleAttr) {
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textSpeedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var speed: Float = 0f
        set(value) {
            value.coerceIn(0f, 300f)
            invalidate()
            field = value
        }

    init {
        // Paint for background
        backgroundPaint.color = Color.BLACK
        backgroundPaint.style = Paint.Style.FILL

        // Paint for arc
        arcPaint.style = Paint.Style.STROKE
        arcPaint.strokeWidth = 20f

        // Paint for needle
        needlePaint.color = Color.RED
        needlePaint.strokeWidth = 5f
        needlePaint.style = Paint.Style.FILL

        // Paint for text
        textPaint.color = Color.WHITE
        textPaint.textSize = 28f
        textPaint.textAlign = Paint.Align.CENTER

        // Paint for text speed km/h
        textSpeedPaint.color = Color.WHITE
        textSpeedPaint.textSize = 40f
        textSpeedPaint.textAlign = Paint.Align.CENTER

        // Paint for ticks
        tickPaint.color = Color.WHITE
        tickPaint.strokeWidth = 3f
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        /** 0.8f*/
        val radius = width * 0.4f
        /** 0.4f*/

        // Draw background
        canvas.drawCircle(centerX, centerY, radius + 40, backgroundPaint)

        // Draw the arc
        arcPaint.color = Color.GREEN
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            135f,
            90f,
            false,
            arcPaint
        )
        arcPaint.color = Color.YELLOW
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            225f,
            45f,
            false,
            arcPaint
        )
        arcPaint.color = Color.RED
        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            270f,
            135.5f,
            false,
            arcPaint
        )

        // Draw ticks and numbers
        for (i in 0..30) {
            val angle = Math.toRadians((135 + i * 9).toDouble())
            val tickStartX = (centerX + (radius - 30) * cos(angle)).toFloat()
            val tickStartY = (centerY + (radius - 30) * sin(angle)).toFloat()
            val tickEndX = (centerX + radius * cos(angle)).toFloat()
            val tickEndY = (centerY + radius * sin(angle)).toFloat()

            canvas.drawLine(tickStartX, tickStartY, tickEndX, tickEndY, tickPaint)

            if (i % 5 == 0) {
                val textX = (centerX + (radius - 83) * cos(angle)).toFloat()
                val textY = (centerY + (radius - 75) * sin(angle)).toFloat()
                canvas.drawText("${i * 10}", textX, textY, textPaint)
            }
        }

        // Draw needle
/*
         val needleAngle = 135 + (speed / 300) * 270
         val needleX =
             (centerX + (radius - 150) * cos(Math.toRadians(needleAngle.toDouble()))).toFloat()
         val needleY =
             (centerY + (radius - 150) * sin(Math.toRadians(needleAngle.toDouble()))).toFloat()

         canvas.drawLine(centerX, centerY, needleX, needleY, needlePaint)
*/
// Draw center circle
        val centerCircleRadius = 20f
        canvas.drawCircle(centerX, centerY, centerCircleRadius, needlePaint)

        // Draw needle with tapering effect
        val needleAngle = 135 + (speed / 300) * 270
        val needleLength = radius - 50

        val needlePath = Path()
        val angleRadians = Math.toRadians(needleAngle.toDouble())

        // Calculate points for the needle shape
        val needleTipX = centerX + needleLength * cos(angleRadians).toFloat()
        val needleTipY = centerY + needleLength * sin(angleRadians).toFloat()
        val needleBaseLeftX =
            centerX + centerCircleRadius * cos(angleRadians + Math.PI / 2).toFloat()
        val needleBaseLeftY =
            centerY + centerCircleRadius * sin(angleRadians + Math.PI / 2).toFloat()
        val needleBaseRightX =
            centerX + centerCircleRadius * cos(angleRadians - Math.PI / 2).toFloat()
        val needleBaseRightY =
            centerY + centerCircleRadius * sin(angleRadians - Math.PI / 2).toFloat()

        // Draw the path for the needle
        needlePath.moveTo(needleBaseLeftX, needleBaseLeftY)
        needlePath.lineTo(needleTipX, needleTipY)
        needlePath.lineTo(needleBaseRightX, needleBaseRightY)
        needlePath.close()

        // Draw needle with color
        canvas.drawPath(needlePath, needlePaint)

        // Draw current speed
        canvas.drawText("${speed.toInt()} km/h", centerX, centerY + 280, textSpeedPaint)
    }

}