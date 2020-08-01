package com.thelumierguy.starfield.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlinx.coroutines.*
import kotlin.random.Random


class LogoView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attributeSet, defStyle) {

    private val starPaint by lazy {
        Paint().apply {
            color = Color.parseColor("#E4962B")
        }
    }

    private val paintJob: Job = Job()

    private val logoTwinkles by lazy {
        List(20) {
            LogoTwinkles(measuredHeight, measuredWidth)
        }
    }

    var enableTinkling = false
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isInEditMode || !enableTinkling) {
            return
        }
        canvas?.let {
            paintJob.cancelChildren()
            CoroutineScope(paintJob + Dispatchers.Main).launch {
                logoTwinkles.forEach {
                    it.draw(canvas, starPaint)
                }
                delay(800)
                logoTwinkles.forEach {
                    it.randomiseCoords()
                }
                invalidate()

            }

        }

    }

    class LogoTwinkles(val height: Int, val width: Int) {

        var xCor = 0F
        var yCor = 0F


        fun draw(canvas: Canvas, paint: Paint) {
            canvas.drawCircle(xCor, yCor, 3F, paint)

        }

        fun randomiseCoords() {
            xCor = Random.nextInt(0, width).toFloat()
            yCor = Random.nextInt(0, height).toFloat()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        enableTinkling = false
    }
}
