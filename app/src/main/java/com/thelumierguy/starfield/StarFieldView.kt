package com.thelumierguy.starfield

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random


class RetroView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attributeSet, defStyle) {

    private val starsArray by lazy {
        Array(800, init = {
            Star()
        })
    }


    init {
        setBackgroundColor(Color.BLACK)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.translate(measuredWidth / 2F, measuredHeight / 2F)
            starsArray.forEach { star ->
                star.updateZPosition {
                    invalidate()
                }
                star.drawStar(canvas)
            }
        }
    }

    inner class Star {

        var x = Random.nextInt(-measuredWidth, measuredWidth).toFloat()
        var y = Random.nextInt(-measuredHeight, measuredHeight).toFloat()
        var z = Random.nextInt(measuredWidth).toFloat()

        private val starPaint by lazy {
            Paint().apply {
                color = Color.WHITE
            }
        }

        fun updateZPosition(invalidate: () -> Unit) {
            z -= 10 //translation speed
            invalidate()
            if (z < 0) {
                resetPosition()
            }
        }

        private fun resetPosition() {
            x = Random.nextInt(-measuredWidth, measuredWidth).toFloat()
            y = Random.nextInt(-measuredHeight, measuredHeight).toFloat()
            z = measuredWidth.toFloat()
        }

        fun drawStar(canvas: Canvas) {
            //Map coordinates to location on canvas
            val sx = map(x / z,
                0F,
                1F,
                0F,
                measuredWidth.toFloat()
            )
            val sy = map(y / z,
                0F,
                1F,
                0F,
                measuredHeight.toFloat()
            )
            //Map radius according to distance from z to width
            val radius = map(z, 0F, measuredWidth.toFloat(), 8F, 0F)
            canvas.drawCircle(sx, sy, radius, starPaint)
        }


        private fun map(
            value: Float,
            startRangeMin: Float,
            startRangeMax: Float,
            endRangeMin: Float,
            endRangeMax: Float
        ): Float {
            return (value - startRangeMin) / (startRangeMax - startRangeMin) * (endRangeMax - endRangeMin) + endRangeMin;
        }
    }

}


