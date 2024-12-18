package com.example.customviewtask

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.customviewtask.custom_view.speedometer.SpeedometerView
import com.example.customviewtask.databinding.ScreenFirstBinding


class FirstScreen : Fragment(R.layout.screen_first) {
    private val binding: ScreenFirstBinding by viewBinding(ScreenFirstBinding::bind)
    /*
        @SuppressLint("ClickableViewAccessibility")
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val speedometer = binding.speedometerView

            // Increment speed on button press
            binding.incrementSpeedBtn.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Increase speed by 10, ensure it stays within 0 - 360 range
                        currentSpeed = (currentSpeed + 10f).coerceIn(0f, 360f)
                        animateSpeedNeedle(speedometer, speedometer.speed, currentSpeed)
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        true
                    }

                    else -> false
                }
            }

            // Decrement speed on button press
            binding.decrementSpeedBtn.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Decrease speed by 10, ensure it stays within 0 - 360 range
                        currentSpeed = (currentSpeed - 10f).coerceIn(0f, 360f)
                        animateSpeedNeedle(speedometer, speedometer.speed, currentSpeed)
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        true
                    }

                    else -> false
                }
            }
        }


        private fun animateSpeedNeedle(view: SpeedometerView, startSpeed: Float, endSpeed: Float) {
            val targetSpeed = endSpeed.coerceIn(0f, 360f)
            ValueAnimator.ofFloat(startSpeed, targetSpeed).apply {
                duration = 500
                addUpdateListener {
                    view.speed = it.animatedValue as Float
                }
                start()
            }

        }*/

    private var isIncreasePressed = false
    private var isDecreasePressed = false
    private var currentSpeed = 0f
    private val speedChangeHandler =
        Handler(Looper.getMainLooper()) // Handler for continuous speed change

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val speedometer = binding.speedometerView
        val increaseSpeed = binding.incrementSpeedBtn
        val decreaseSpeed = binding.decrementSpeedBtn

        // Handle touch events for increasing speed
        increaseSpeed.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (speedometer.speed < 300) {
                        isIncreasePressed = true
                        increaseSpeedAnimation(speedometer)
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isIncreasePressed = false
                }
            }
            true
        }

        // Handle touch events for decreasing speed
        decreaseSpeed.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    if (speedometer.speed > 0) {
                        isDecreasePressed = true
                        decreaseSpeedAnimation(speedometer)
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isDecreasePressed = false
                }
            }
            true
        }
    }

    // Function to animate speed increase
    private fun increaseSpeedAnimation(speedometer: SpeedometerView) {
        if (isIncreasePressed) {
            val targetSpeed = (currentSpeed + 10f).coerceIn(0f, 300f)
            val animator = ValueAnimator.ofFloat(currentSpeed, targetSpeed)
            animator.duration = 100 // Duration of the animation
            animator.addUpdateListener { animation ->
                currentSpeed = animation.animatedValue as Float
                speedometer.speed = currentSpeed
            }
            // Continue the animation if the button is still pressed
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    increaseSpeedAnimation(speedometer)
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}

            })
            animator.start()
        }
    }

    // Function to animate speed decrease
    private fun decreaseSpeedAnimation(speedometer: SpeedometerView) {
        if (isDecreasePressed) {
            val targetSpeed = (currentSpeed - 10f).coerceIn(0f, 300f)
            val animator = ValueAnimator.ofFloat(currentSpeed, targetSpeed)
            animator.duration = 100 // Duration of the animation
            animator.addUpdateListener { animation ->
                currentSpeed = animation.animatedValue as Float
                speedometer.speed = currentSpeed
            }
            animator.start()

            // Continue the animation if the button is still pressed
            speedChangeHandler.postDelayed(
                { decreaseSpeedAnimation(speedometer) },
                50 // 100 ms interval for continuous decrease
            )
        }
    }
}